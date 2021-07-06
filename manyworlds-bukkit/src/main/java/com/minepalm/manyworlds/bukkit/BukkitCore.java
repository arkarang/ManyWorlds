package com.minepalm.manyworlds.bukkit;

import com.grinderwolf.swm.plugin.SWMPlugin;
import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.api.netty.Controller;
import com.minepalm.manyworlds.bukkit.mysql.MySQLWorldDatabase;
import com.minepalm.manyworlds.core.AbstractManyWorlds;
import com.minepalm.manyworlds.core.ManyWorldInfo;
import com.minepalm.manyworlds.core.netty.PacketFactory;
import com.minepalm.manyworlds.core.netty.PacketResolver;
import com.minepalm.manyworlds.core.netty.WorldCreatePacket;
import com.minepalm.manyworlds.core.netty.WorldLoadPacket;
import lombok.Getter;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BukkitCore extends AbstractManyWorlds implements WorldManager{

    private static final ExecutorService PACKET_EXECUTOR, POOL;
    private static WorldType user, sample;

    @Getter
    static BukkitCore inst;

    static{
        POOL = Executors.newScheduledThreadPool(4);
        PACKET_EXECUTOR = Executors.newSingleThreadExecutor();

    }

    private final HashMap<WorldType, WorldLoader> loaders = new HashMap<>();
    private final HashMap<WorldType, WorldDatabase> databases = new HashMap<>();
    private final ManyWorlds plugin;

    @Getter
    WorldStorage worldStorage;

    BukkitCore(ManyWorlds plugin, GlobalDatabase globalDatabase, Controller controller) {
        super(plugin.getConf().getServerName(), globalDatabase, controller);
        if (inst == null)
            inst = this;
        else
            throw new IllegalStateException("BukkitCore already exists");

        this.plugin = plugin;
        Conf conf = plugin.getConf();
        SWMPlugin swm = plugin.getSwm();

        worldStorage = new ManyWorldStorage(swm.getNms(), 100);

        databases.put(WorldType.SAMPLE, new MySQLWorldDatabase(WorldType.SAMPLE, conf.getSampleTableName(), conf.getDatabaseProperties(), POOL));
        databases.put(WorldType.USER, new MySQLWorldDatabase(WorldType.USER, conf.getUserTableName(), conf.getDatabaseProperties(), POOL));

        loaders.put(WorldType.USER, new ManyWorldLoader(getWorldDatabase(WorldType.USER), worldStorage));
        loaders.put(WorldType.SAMPLE, new ManyWorldLoader(getWorldDatabase(WorldType.SAMPLE), worldStorage));

        PacketListener listener = new PacketListener(this, PACKET_EXECUTOR, new PacketResolver(PACKET_EXECUTOR, this.getGlobalDatabase()));

        listener.register(WorldCreatePacket.class, packet -> {
            this.createNewWorld(new ManyWorldInfo(WorldType.USER, packet.getSampleName(), packet.getWorldName()));
        });

        listener.register(WorldLoadPacket.class, (packet) -> {
            if(packet.isLoad())
                this.loadWorld(new ManyWorldInfo(WorldType.USER, packet.getWorldName()));
            else
                this.save(new ManyWorldInfo(WorldType.USER, packet.getWorldName()));
        });
        
        globalDatabase.register();
        globalDatabase.resetWorlds(plugin);
        this.getController().send(PacketFactory.newPacket(plugin, this.getGlobalDatabase().getProxy()).createServerUpdated(true));
    }

    @Override
    public void shutdown(){
        this.getGlobalDatabase().unregister();
        worldStorage.shutdown();
        this.getController().send(PacketFactory.newPacket(plugin, this.getGlobalDatabase().getProxy()).createServerUpdated(false));
    }

    @Override
    public WorldDatabase getWorldDatabase(WorldType type) {
        return databases.get(type);
    }

    @Override
    public WorldLoader getWorldLoader(WorldType type) {
        return loaders.get(type);
    }

    @Override
    public Future<Void> createNewWorld(WorldInfo info) {
        return POOL.submit(()->{
            try {
                WorldInfo swapped = new ManyWorldInfo(WorldType.SAMPLE, info.getSampleName(), info.getSampleName());
                PreparedWorld toUse = getWorldDatabase(WorldType.SAMPLE).prepareWorld(swapped).get();
                CraftManyWorld mw = (CraftManyWorld) getWorldLoader(WorldType.SAMPLE).deserialize(toUse);
                mw.setLoader(getWorldLoader(WorldType.USER));
                worldStorage.registerWorld(mw);
            }catch (IOException | InterruptedException | ExecutionException e){
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public Future<Void> loadWorld(WorldInfo info){
        return POOL.submit(()->{
            try {
                PreparedWorld preparedWorld = getWorldDatabase(WorldType.USER).prepareWorld(info).get();
                worldStorage.registerWorld(getWorldLoader(WorldType.USER).deserialize(preparedWorld));
                this.getController().send(PacketFactory.newPacket(plugin, this.getGlobalDatabase().getProxy()).createWorldLoad(info.getWorldName(), true));
            }catch (IOException | InterruptedException | ExecutionException e){
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public Future<Void> save(WorldInfo info) {
        return save(info.getWorldName());
    }

    @Override
    public Future<Void> save(String worldFullName) {
        return POOL.submit(()->{
            getWorldStorage().unregisterWorld(worldFullName);
            this.getController().send(PacketFactory.newPacket(plugin, this.getGlobalDatabase().getProxy()).createWorldLoad(worldFullName, false));
            return null;
        });
    }

}

package com.minepalm.manyworlds.bukkit;

import co.aikar.commands.PaperCommandManager;
import com.grinderwolf.swm.nms.SlimeNMS;
import com.grinderwolf.swm.plugin.SWMPlugin;
import com.minepalm.hellobungee.bukkit.HelloBukkit;
import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.ManyWorldsCore;
import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.bukkit.mysql.MySQLWorldDatabase;
import com.minepalm.manyworlds.core.ManyWorlds;
import com.minepalm.manyworlds.core.database.global.MySQLGlobalDatabase;
import com.minepalm.manyworlds.core.netty.PacketFactory;
import com.minepalm.manyworlds.core.netty.PacketResolver;
import com.minepalm.manyworlds.core.netty.WorldCreatePacket;
import com.minepalm.manyworlds.core.netty.WorldLoadPacket;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ManyWorldsBukkit extends JavaPlugin implements WorldManager, ServerView {

    private static final ExecutorService PACKET_EXECUTOR, POOL;

    static{
        POOL = Executors.newScheduledThreadPool(4);
        PACKET_EXECUTOR = Executors.newSingleThreadExecutor();
    }

    @Getter
    static ManyWorldsBukkit inst;

    @Getter
    private static ManyWorldsCore core;

    private Conf conf;

    @Getter
    private String serverName;

    private HashMap<WorldType, WorldLoader> loaders = new HashMap<>();
    private HashMap<WorldType, WorldDatabase> databases = new HashMap<>();

    @Getter
    static SWMPlugin swm;

    @Getter
    WorldStorage worldStorage;

    @Override
    public void onEnable() {
        inst = this;
        conf = new Conf(this);
        serverName = conf.getServerName();
        swm = ((SWMPlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager"));

        GlobalDatabase globalDatabase = new MySQLGlobalDatabase(conf.proxyName(), this, conf.getServerTable(), conf.getWorldsTable(), conf.getDatabaseProperties());
        core = new ManyWorlds(this.getServerName(), globalDatabase, new ProxyController(HelloBukkit.getConnections()));

        databases.put(WorldType.SAMPLE, new MySQLWorldDatabase(WorldType.SAMPLE, conf.getSampleTableName(), conf.getDatabaseProperties(), POOL));
        databases.put(WorldType.USER, new MySQLWorldDatabase(WorldType.USER, conf.getUserTableName(), conf.getDatabaseProperties(), POOL));

        loaders.put(WorldType.USER, new ManyWorldLoader(getWorldDatabase(WorldType.USER)));
        loaders.put(WorldType.SAMPLE, new ManyWorldLoader(getWorldDatabase(WorldType.SAMPLE)));

        worldStorage = new ManyWorldStorage(swm.getNms(), 100);

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new Commands());

        PacketListener listener = new PacketListener(this, PACKET_EXECUTOR, new PacketResolver(PACKET_EXECUTOR, core.getGlobalDatabase()));

        listener.register(WorldCreatePacket.class, packet -> {
            this.createNewWorld(core.newWorldInfo(WorldType.USER, packet.getSampleName(), packet.getWorldName()));
        });

        listener.register(WorldLoadPacket.class, (packet) -> {
            if(packet.isLoad())
                this.loadWorld(core.newWorldInfo(WorldType.USER, packet.getWorldName()));
            else
                this.save(core.newWorldInfo(WorldType.USER, packet.getWorldName()));
        });

        Bukkit.getPluginManager().registerEvents(listener, this);

        globalDatabase.register();
        globalDatabase.resetWorlds(this);
        core.getController().send(PacketFactory.newPacket(this, core.getGlobalDatabase().getProxy()).createServerUpdated(true));
    }

    @Override
    public void onDisable(){
        core.getGlobalDatabase().unregister();
        worldStorage.shutdown();
        core.getController().send(PacketFactory.newPacket(this, core.getGlobalDatabase().getProxy()).createServerUpdated(false));
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
                WorldInfo swapped = core.newWorldInfo(WorldType.SAMPLE, info.getSampleName(), info.getSampleName());
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
                core.getController().send(PacketFactory.newPacket(this, core.getGlobalDatabase().getProxy()).createWorldLoad(info.getWorldName(), true));
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
            core.getController().send(PacketFactory.newPacket(this, core.getGlobalDatabase().getProxy()).createWorldLoad(worldFullName, false));
            return null;
        });
    }

}

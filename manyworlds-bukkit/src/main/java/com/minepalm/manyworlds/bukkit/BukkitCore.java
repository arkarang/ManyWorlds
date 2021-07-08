package com.minepalm.manyworlds.bukkit;

import com.grinderwolf.swm.plugin.SWMPlugin;
import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.api.netty.Controller;
import com.minepalm.manyworlds.api.errors.LoaderNotFoundException;
import com.minepalm.manyworlds.bukkit.mysql.MySQLWorldDatabase;
import com.minepalm.manyworlds.core.AbstractManyWorlds;
import com.minepalm.manyworlds.core.WorldTokens;
import com.minepalm.manyworlds.core.ManyWorldInfo;
import com.minepalm.manyworlds.core.netty.PacketFactory;
import com.minepalm.manyworlds.core.netty.PacketResolver;
import com.minepalm.manyworlds.core.netty.WorldCreatePacket;
import com.minepalm.manyworlds.core.netty.WorldLoadPacket;
import lombok.AccessLevel;
import lombok.Getter;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BukkitCore extends AbstractManyWorlds implements WorldManager{

    private static final ExecutorService POOL, CORE;

    @Getter
    static BukkitCore inst;

    @Getter(AccessLevel.PACKAGE)
    PacketListener listener;

    static{
        POOL = Executors.newScheduledThreadPool(4);
        CORE = Executors.newSingleThreadExecutor();

    }

    private final HashMap<WorldType, WorldLoader> loaders = new HashMap<>();
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

        WorldDatabase sampleDB, userDB;
        userDB = this.newMySQL(WorldTokens.USER, conf.getUserTableName());
        sampleDB = this.newMySQL(WorldTokens.SAMPLE, conf.getSampleTableName());

        registerWorldLoader(WorldTokens.SAMPLE, new ManyWorldLoader(sampleDB, worldStorage));
        registerWorldLoader(WorldTokens.USER, new ManyWorldLoader(userDB, worldStorage));
        
        globalDatabase.register();
        globalDatabase.resetWorlds(plugin);
        this.getController().send(PacketFactory.newPacket(plugin, this.getGlobalDatabase().getProxy()).createServerUpdated(true));

        listener = new PacketListener(this, Executors.newSingleThreadExecutor(), new PacketResolver(Executors.newSingleThreadExecutor(), this.getGlobalDatabase()));

        listener.register(WorldCreatePacket.class, packet -> {
            this.createNewWorld(new ManyWorldInfo(WorldTokens.USER, packet.getSampleName(), packet.getWorldName()));
        });

        listener.register(WorldLoadPacket.class, (packet) -> {
            if(packet.isLoad())
                this.loadWorld(new ManyWorldInfo(WorldTokens.USER, packet.getWorldName()));
            else
                this.save(new ManyWorldInfo(WorldTokens.USER, packet.getWorldName()));
        });
    }

    @Override
    public void shutdown(){
        this.getGlobalDatabase().unregister();
        worldStorage.shutdown();
        this.getController().send(PacketFactory.newPacket(plugin, this.getGlobalDatabase().getProxy()).createServerUpdated(false));
    }

    @Override
    @Nullable
    public WorldDatabase getWorldDatabase(WorldType type) {
        if(loaders.containsKey(type))
            return loaders.get(type).getDatabase();
        else
            return null;
    }

    @Override
    public WorldLoader getWorldLoader(WorldType type) throws LoaderNotFoundException {
        if(loaders.containsKey(type))
            return loaders.get(type);
        else
            throw new LoaderNotFoundException("loader does not exists");
    }

    @Override
    public boolean registerWorldLoader(WorldType type, WorldLoader loader) {
        boolean exists = loaders.containsKey(type);
        if(!exists)
            loaders.put(type, loader);

        return !exists;
    }

    @Override
    public Future<Void> createNewWorld(WorldInfo info) {
        return CORE.submit(()->{
            try {
                WorldLoader loader = getWorldLoader(WorldTokens.SAMPLE);
                WorldInfo swapped = new ManyWorldInfo(WorldTokens.SAMPLE, info.getSampleName(), info.getSampleName());
                PreparedWorld toUse = loader.getDatabase().prepareWorld(swapped).get();
                toUse.setWorldInfo(info);
                CraftManyWorld mw = (CraftManyWorld) loader.deserialize(toUse);
                mw.setLoader(getWorldLoader(info.getWorldType()));
                worldStorage.registerWorld(mw);
            }catch (IOException | InterruptedException | ExecutionException | LoaderNotFoundException e){
                plugin.getLogger().severe("World "+info.getWorldName()+" could not be created by: "+ e.getMessage());
            }
            return null;
        });
    }

    @Override
    public Future<Void> loadWorld(WorldInfo info){
        return CORE.submit(()->{
            try {
                WorldLoader loader = getWorldLoader(info.getWorldType());
                PreparedWorld preparedWorld = loader.getDatabase().prepareWorld(info).get();
                worldStorage.registerWorld(loader.deserialize(preparedWorld));
                this.getController().send(PacketFactory.newPacket(plugin, this.getGlobalDatabase().getProxy()).createWorldLoad(info.getWorldName(), true));
            }catch (IOException | InterruptedException | ExecutionException | LoaderNotFoundException e){
                plugin.getLogger().severe("World "+info.getWorldName()+" could not be loaded by: "+ e.getMessage());
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
        return CORE.submit(()->{
            getWorldStorage().unregisterWorld(worldFullName);
            this.getController().send(PacketFactory.newPacket(plugin, this.getGlobalDatabase().getProxy()).createWorldLoad(worldFullName, false));
            return null;
        });
    }

    public MySQLWorldDatabase newMySQL(WorldType type, String table){
        return new MySQLWorldDatabase(type, table, plugin.getConf().getDatabaseProperties(), POOL);
    }
}

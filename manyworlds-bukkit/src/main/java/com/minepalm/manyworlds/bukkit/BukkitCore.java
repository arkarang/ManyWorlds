package com.minepalm.manyworlds.bukkit;

import com.grinderwolf.swm.plugin.SWMPlugin;
import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.api.netty.Controller;
import com.minepalm.manyworlds.bukkit.executors.WorldCreatePacketExecutor;
import com.minepalm.manyworlds.bukkit.executors.WorldLoadPacketExecutor;
import com.minepalm.manyworlds.bukkit.mysql.MySQLWorldDatabase;
import com.minepalm.manyworlds.core.AbstractManyWorlds;
import com.minepalm.manyworlds.core.ManyWorldInfo;
import com.minepalm.manyworlds.core.WorldTokens;
import com.minepalm.manyworlds.core.netty.ServerUpdatedPacket;
import com.minepalm.manyworlds.core.netty.WorldLoadPacket;
import lombok.Getter;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BukkitCore extends AbstractManyWorlds implements WorldManager{

    private static final ExecutorService POOL, SERVICE;

    @Getter
    static BukkitCore inst;

    static{
        POOL = Executors.newScheduledThreadPool(4);
        SERVICE = Executors.newSingleThreadExecutor();

    }

    private final HashMap<WorldType, WorldLoader> loaders = new HashMap<>();
    private final ManyWorlds plugin;

    @Getter
    final WorldStorage worldStorage;

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

        this.getController().register(new WorldCreatePacketExecutor(this));
        this.getController().register(new WorldLoadPacketExecutor(this));

        registerWorldLoader(WorldTokens.SAMPLE, new ManyWorldLoader(sampleDB, worldStorage));
        registerWorldLoader(WorldTokens.USER, new ManyWorldLoader(userDB, worldStorage));
        
        globalDatabase.register();
        globalDatabase.resetWorlds(plugin);
        this.getController().send(new ServerUpdatedPacket(plugin, this.getGlobalDatabase().getProxy(), true));

    }

    @Override
    public void shutdown(){
        this.getGlobalDatabase().unregister();
        worldStorage.shutdown();
        this.getController().send(new ServerUpdatedPacket(plugin, this.getGlobalDatabase().getProxy(), false));
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
    public WorldLoader getWorldLoader(WorldType type){
        if(loaders.containsKey(type))
            return loaders.get(type);
        else
            throw new IllegalArgumentException("loader (WorldType: "+type.getName()+") does not exists");
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
        return this.createNewWorld(info, ()->{});
    }

    @Override
    public Future<Void> createNewWorld(WorldInfo info, Runnable run) {
        return SERVICE.submit(()->{
            try {
                WorldLoader loader = getWorldLoader(WorldTokens.SAMPLE);
                WorldInfo swapped = new ManyWorldInfo(WorldTokens.SAMPLE, info.getSampleName(), info.getSampleName());
                PreparedWorld toUse = loader.getDatabase().prepareWorld(swapped).get();
                toUse.setWorldInfo(info);
                CraftManyWorld mw = (CraftManyWorld) loader.deserialize(toUse);
                mw.setLoader(getWorldLoader(info.getWorldType()));
                worldStorage.registerWorld(mw, run);
            }catch (IOException | InterruptedException | ExecutionException e){
                plugin.getLogger().severe("World "+info.getWorldName()+" could not be created by: "+ e.getMessage());
            }
            return null;
        });
    }

    @Override
    public Future<Void> loadWorld(WorldInfo info){
        return loadWorld(info, ()->{});
    }

    @Override
    public Future<Void> loadWorld(WorldInfo info, Runnable after){
        return SERVICE.submit(()->{
            try {
                WorldLoader loader = getWorldLoader(info.getWorldType());
                PreparedWorld preparedWorld = loader.getDatabase().prepareWorld(info).get();
                worldStorage.registerWorld(loader.deserialize(preparedWorld), after);
            }catch (Exception  e){
                plugin.getLogger().warning(e.getMessage());
                for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                    plugin.getLogger().warning(stackTraceElement.toString());
                }
                plugin.getLogger().severe("World "+info.getWorldName()+" could not be loaded by: "+e.getMessage());
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
        return SERVICE.submit(()->{
            Bukkit.getWorld(worldFullName).save();
            return null;
        });
    }

    @Override
    public Future<Void> unload(WorldInfo info) {
        return unload(info.getWorldName());
    }

    @Override
    public Future<Void> unload(String worldFullName) {
        return SERVICE.submit(()->{
            ManyWorld world = getWorldStorage().unregisterWorld(worldFullName);
            this.getController().send(new WorldLoadPacket(plugin, this.getGlobalDatabase().getProxy(), world.getWorldInfo(), false));
            return null;
        });
    }

    public MySQLWorldDatabase newMySQL(WorldType type, String table){
        return new MySQLWorldDatabase(type, table, plugin.getConf().getDatabaseProperties(), POOL);
    }
}

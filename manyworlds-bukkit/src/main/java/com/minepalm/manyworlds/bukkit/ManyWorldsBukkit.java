package com.minepalm.manyworlds.bukkit;

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
import com.minepalm.manyworlds.core.netty.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManyWorldsBukkit extends JavaPlugin implements WorldManager, ServerView {

    private static ExecutorService RESOLVE_SERVICE, EXECUTE_SERVICE;
    private static final ManyProperties DEFAULT_PROPS = new ManyProperties();

    static{
        EXECUTE_SERVICE = Executors.newSingleThreadExecutor();
        RESOLVE_SERVICE = Executors.newSingleThreadExecutor();
    }

    @Getter
    static ManyWorldsBukkit inst;

    @Getter
    private static ManyWorldsCore core;

    private Conf conf;

    @Getter
    private String serverName;

    private WorldDatabase userDatabase, sampleDatabase;

    WorldLoader userWorldLoader, sampleWorldLoader;

    @Getter
    WorldStorage worldStorage;

    @Getter
    private long launchedTime;

    @Override
    public void onEnable() {
        inst = this;
        conf = new Conf(this);
        serverName = conf.getServerName();
        SlimeNMS nms = ((SWMPlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager")).getNms();
        GlobalDatabase globalDatabase = new MySQLGlobalDatabase(conf.proxyName(), this, conf.getServerTable(), conf.getWorldsTable(), conf.getDatabaseProperties());
        core = new ManyWorlds(this.getServerName(), globalDatabase, new BungeeController(HelloBukkit.getConnections()));
        userDatabase = new MySQLWorldDatabase(WorldType.USER, conf.getUserTableName(), conf.getDatabaseProperties());
        sampleDatabase = new MySQLWorldDatabase(WorldType.SAMPLE, conf.getSampleTableName(), conf.getDatabaseProperties());
        userWorldLoader = new ManyWorldLoader(userDatabase);
        sampleWorldLoader = new ManyWorldLoader(sampleDatabase);
        worldStorage = new BukkitWorldStorage(nms, 100);
        launchedTime = System.currentTimeMillis();

        PacketListener listener = new PacketListener(this, EXECUTE_SERVICE, new PacketResolver(RESOLVE_SERVICE, core.getGlobalDatabase()));

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
        switch (type){
            case SAMPLE:
                return sampleDatabase;
            case USER:
            default:
                return userDatabase;
        }
    }

    @Override
    public WorldLoader getWorldLoader(WorldType type) {
        switch (type){
            case SAMPLE:
                return sampleWorldLoader;
            case USER:
            default:
                return userWorldLoader;
        }
    }

    @Override
    public void createNewWorld(WorldInfo info) {
        PreparedWorld preparedWorld = sampleDatabase.prepareWorld(info);
        try {
            worldStorage.registerWorld(sampleWorldLoader.deserialize(preparedWorld));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void loadWorld(WorldInfo info){
        PreparedWorld preparedWorld = userDatabase.prepareWorld(info);
        try {
            worldStorage.registerWorld(userWorldLoader.deserialize(preparedWorld));
            core.getController().send(PacketFactory.newPacket(this, core.getGlobalDatabase().getProxy()).createWorldLoad(info.getWorldName(), true));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void save(WorldInfo info) {
        save(info.getWorldName());
    }

    @Override
    public void save(String worldFullName) {
        getWorldStorage().unregisterWorld(worldFullName);
        core.getController().send(PacketFactory.newPacket(this, core.getGlobalDatabase().getProxy()).createWorldLoad(worldFullName, false));
    }
}

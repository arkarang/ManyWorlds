package com.minepalm.manyworlds.bungee;

import com.minepalm.hellobungee.bungee.HelloBungee;
import com.minepalm.manyworlds.api.*;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.core.AbstractManyWorlds;
import com.minepalm.manyworlds.core.database.global.MySQLGlobalDatabase;
import com.minepalm.manyworlds.core.netty.PacketFactory;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Getter
public class ManyWorldsBungee extends Plugin implements BungeeView {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    @Getter
    static ManyWorldsBungee inst;
    @Getter
    static ProxyCore core;
    @Getter
    static GlobalDatabase database;

    private String serverName;
    Conf conf;

    @Override
    public void onEnable() {
        inst = this;
        conf = new Conf(this, "config.yml", true);
        serverName = conf.getName();
        database = new MySQLGlobalDatabase(serverName, this, conf.getServerTable(), conf.getWorldsTable(), conf.getProperties());
        core = new ProxyCore(this, database, new BukkitWorldController(HelloBungee.getConnections()));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Commands());
    }

    @Override
    public void onDisable() {
        core.shutdown();
    }

    public int getTotalCount() {
        return database.getProxy().getTotalCount();
    }

    public static Future<Void> createAtLeast(WorldInfo info){
        return core.createAtLeast(info);
    }

    public static Future<Void> loadAtLeast(WorldInfo info) {
        return core.loadAtLeast(info);
    }

    public static Future<Void> createSpecific(BukkitView view, WorldInfo info) {
        return core.createSpecific(view, info);
    }

    public static Future<Void> loadSpecific(BukkitView view, WorldInfo info, boolean onOff) {
        return core.loadSpecific(view, info, onOff);
    }

}

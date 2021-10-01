package com.minepalm.manyworlds.bungee;

import com.minepalm.hellobungee.api.HelloClient;
import com.minepalm.hellobungee.api.HelloEveryone;
import com.minepalm.hellobungee.bungee.HelloBungee;
import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.BungeeView;
import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.core.database.global.MySQLGlobalDatabase;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ManyWorldsBungee extends Plugin implements BungeeView {

    @Getter
    public static ManyWorldsBungee inst;
    @Getter
    public static ProxyCore core;
    @Getter
    static GlobalDatabase database;

    @Getter
    private String serverName;
    @Getter
    Conf conf;

    @Override
    public void onEnable() {
        inst = this;
        conf = new Conf(this, "config.yml", true);
        serverName = conf.getName();
        ManyWorldsBungee.database = new MySQLGlobalDatabase(serverName, this, conf.getServerTable(), conf.getWorldsTable(), conf.getProperties(), Executors.newScheduledThreadPool(4), getLogger());
        HelloEveryone network = HelloBungee.getInst().getMain();
        ManyWorldsBungee.core = new ProxyCore(this, database, new BukkitWorldController(network));
        ProxyServer.getInstance().getLogger().info("ManyWorlds - bungee("+serverName+") load complete");
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Commands());
        Map<String, HelloClient> map = network.getConnections().getClients();
        for (String key : map.keySet()) {
            HelloClient client = map.get(key);
            if(!client.isConnected()){
                database.unregister(key);
                database.resetWorlds(()->key);
            }
        }

        ProxyServer.getInstance().getLogger().info("ManyWorlds - bungee enabled");
    }

    @Override
    public void onDisable() {
        core.shutdown();
    }

    public int getTotalCount() {
        return database.getProxy().getTotalCount();
    }

    public static Future<ServerView> createAtLeast(WorldInfo info){
        return core.createAtLeast(info);
    }

    public static Future<ServerView> loadAtLeast(WorldInfo info) {
        return core.loadAtLeast(info);
    }

    public static Future<Void> createSpecific(BukkitView view, WorldInfo info) {
        return core.createSpecific(view, info);
    }

    public static Future<Void> loadSpecific(BukkitView view, WorldInfo info, boolean onOff) {
        return core.loadSpecific(view, info, onOff);
    }

}

package com.minepalm.manyworlds.bungee;

import com.minepalm.hellobungee.bungee.HelloBungee;
import com.minepalm.manyworlds.api.*;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.bungee.events.ManyWorldLoadEvent;
import com.minepalm.manyworlds.bungee.events.ManyWorldUnloadEvent;
import com.minepalm.manyworlds.core.database.global.MySQLGlobalDatabase;
import com.minepalm.manyworlds.core.netty.PacketResolver;
import com.minepalm.manyworlds.core.netty.WorldLoadPacket;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

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
        ManyWorldsBungee.core = new ProxyCore(this, database, new BukkitWorldController(HelloBungee.getConnections()));
        ProxyServer.getInstance().getLogger().info("ManyWorlds - bungee("+serverName+") load complete");

        Listener packetListener = new Listener(new PacketResolver(Executors.newSingleThreadExecutor(), database));
        packetListener.register(WorldLoadPacket.class, packet->{
            if(packet.isLoad()){
                ProxyServer.getInstance().getPluginManager().callEvent(new ManyWorldLoadEvent(packet.getSampleName(), packet.getWorldName()));
            }else
                ProxyServer.getInstance().getPluginManager().callEvent(new ManyWorldUnloadEvent(packet.getSampleName(), packet.getWorldName()));
        });

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Commands());
        ProxyServer.getInstance().getPluginManager().registerListener(this, packetListener);
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

package com.minepalm.manyworlds.bukkit;

import co.aikar.commands.PaperCommandManager;
import com.grinderwolf.swm.plugin.SWMPlugin;
import com.minepalm.hellobungee.bukkit.HelloBukkit;
import com.minepalm.hellobungee.bukkit.Listener;
import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.api.errors.LoaderNotFoundException;
import com.minepalm.manyworlds.api.netty.WorldPacket;
import com.minepalm.manyworlds.core.ManyWorldInfo;
import com.minepalm.manyworlds.core.WorldToken;
import com.minepalm.manyworlds.core.WorldTokens;
import com.minepalm.manyworlds.core.database.global.MySQLGlobalDatabase;
import com.minepalm.manyworlds.core.netty.PacketFactory;
import com.minepalm.manyworlds.core.netty.PacketResolver;
import com.minepalm.manyworlds.core.netty.WorldCreatePacket;
import com.minepalm.manyworlds.core.netty.WorldLoadPacket;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ManyWorlds extends JavaPlugin implements BukkitView {

    @Getter
    private static BukkitCore core;

    @Getter
    static ManyWorlds inst;

    @Getter
    String serverName;

    @Getter
    private Conf conf;

    @Getter(AccessLevel.PACKAGE)
    private SWMPlugin swm;

    @Override
    public void onEnable() {
        inst = this;
        conf = new Conf(this);
        swm = ((SWMPlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager"));
        this.serverName = conf.getServerName();

        GlobalDatabase globalDatabase = new MySQLGlobalDatabase(conf.proxyName(), this, conf.getServerTable(), conf.getWorldsTable(), conf.getDatabaseProperties(), Executors.newScheduledThreadPool(4), getLogger());
        core = new BukkitCore(this, globalDatabase, new ProxyController(HelloBukkit.getConnections()));

        PacketListener listener = new PacketListener(core, new PacketResolver(Executors.newSingleThreadExecutor(), getGlobalDatabase()));

        listener.register(WorldCreatePacket.class, packet -> {
            core.createNewWorld(new ManyWorldInfo(WorldTokens.USER, packet.getSampleName(), packet.getWorldName()));
        });

        listener.register(WorldLoadPacket.class, (packet) -> {
            if(packet.isLoad()) {
                if(Bukkit.getWorld(packet.getWorldName()) == null)
                    core.loadWorld(new ManyWorldInfo(WorldToken.get(packet.getSampleName()), packet.getWorldName()));
            }else {
                if(Bukkit.getWorld(packet.getWorldName()) != null)
                    core.save(new ManyWorldInfo(WorldToken.get(packet.getSampleName()), packet.getWorldName()));
            }
        });

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new Commands());

        Bukkit.getPluginManager().registerEvents(new Listener(), this);
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void onDisable() {
        core.shutdown();
    }

    public static GlobalDatabase getGlobalDatabase(){
        return core.getGlobalDatabase();
    }

    public static WorldStorage getWorldStorage(){
        return core.getWorldStorage();
    }

    public static WorldDatabase getWorldDatabase(WorldType type) {
        return core.getWorldDatabase(type);
    }

    public static WorldLoader getWorldLoader(WorldType type) throws LoaderNotFoundException {
        return core.getWorldLoader(type);
    }

    public static Future<Void> createNewWorld(WorldInfo info) {
        return core.createNewWorld(info);
    }

    public static Future<Void> loadWorld(WorldInfo info){
        return core.loadWorld(info);
    }

    public static Future<Void> save(WorldInfo info) {
        return core.save(info);
    }

    public static Future<Void> save(String worldFullName) {
        return core.save(worldFullName);
    }

    public static void send(WorldPacket packet){
        core.getController().send(packet);
    }

    public static PacketFactory newPacket(ServerView destination){
        return PacketFactory.newPacket(inst, destination);
    }

    @Override
    public int getLoadedWorlds() {
        return getWorldStorage().getCounts();
    }
}

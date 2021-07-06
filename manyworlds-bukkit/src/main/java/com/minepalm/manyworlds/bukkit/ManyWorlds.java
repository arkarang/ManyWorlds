package com.minepalm.manyworlds.bukkit;

import co.aikar.commands.PaperCommandManager;
import com.grinderwolf.swm.plugin.SWMPlugin;
import com.minepalm.hellobungee.bukkit.HelloBukkit;
import com.minepalm.hellobungee.bukkit.Listener;
import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.core.database.global.MySQLGlobalDatabase;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Future;

public class ManyWorlds extends JavaPlugin implements ServerView{

    @Getter
    private static BukkitCore core;

    @Getter
    static ManyWorlds inst;

    @Getter(AccessLevel.PACKAGE)
    String serverName;

    @Getter(AccessLevel.PACKAGE)
    private Conf conf;

    @Getter(AccessLevel.PACKAGE)
    private SWMPlugin swm;

    @Override
    public void onEnable() {
        inst = this;
        conf = new Conf(this);
        swm = ((SWMPlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager"));

        GlobalDatabase globalDatabase = new MySQLGlobalDatabase(conf.proxyName(), this, conf.getServerTable(), conf.getWorldsTable(), conf.getDatabaseProperties());
        core = new BukkitCore(this, globalDatabase, new ProxyController(HelloBukkit.getConnections()));

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new Commands());

        Bukkit.getPluginManager().registerEvents(new Listener(), this);
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

    public static WorldLoader getWorldLoader(WorldType type) {
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
}

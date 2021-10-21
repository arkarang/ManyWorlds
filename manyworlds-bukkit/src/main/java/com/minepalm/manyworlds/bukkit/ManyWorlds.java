package com.minepalm.manyworlds.bukkit;

import co.aikar.commands.PaperCommandManager;
import com.grinderwolf.swm.plugin.SWMPlugin;
import com.minepalm.hellobungee.api.HelloClient;
import com.minepalm.hellobungee.api.HelloEveryone;
import com.minepalm.hellobungee.bukkit.HelloBukkit;
import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.api.netty.WorldPacket;
import com.minepalm.manyworlds.core.database.global.MySQLGlobalDatabase;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
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

        HelloEveryone network = HelloBukkit.getInst().getMain();
        this.serverName = network.getName();
        GlobalDatabase globalDatabase = new MySQLGlobalDatabase(conf.proxyName(), this, conf.getServerTable(), conf.getWorldsTable(), conf.getDatabaseProperties(), Executors.newScheduledThreadPool(4), getLogger());
        core = new BukkitCore(this, serverName, globalDatabase, new ProxyController(network));

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new Commands());

        //todo: HelloBungee 1.5로 올릴때, 열려있는 서버 목록 모듈 옮겨놓기.
        Map<String, HelloClient> map = network.getConnections().getClients();
        for (String key : map.keySet()) {
            HelloClient client = map.get(key);
            if(!client.isConnected()){
                globalDatabase.unregister(key);
            }
        }

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

    public static Future<Void> createNewWorld(WorldInfo info, Runnable runAfter) {
        return core.createNewWorld(info, runAfter);
    }

    public static Future<Void> loadWorld(WorldInfo info){
        return core.loadWorld(info);
    }

    public static Future<Void> loadWorld(WorldInfo info, Runnable after){
        return core.loadWorld(info, after);
    }

    public static Future<Void> save(WorldInfo info) {
        return core.save(info);
    }

    public static Future<Void> save(String worldFullName) {
        return core.save(worldFullName);
    }

    public static Future<Void> unloadWorld(WorldInfo info){
        return core.unload(info);
    }

    public static Future<Void> unloadWorld(String name){
        return core.unload(name);
    }

    public static void send(WorldPacket packet){
        core.getController().send(packet);
    }

    @Override
    public int getLoadedWorlds() {
        return getWorldStorage().getCounts();
    }
}

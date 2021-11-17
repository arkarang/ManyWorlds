package com.minepalm.manyworlds.bukkit;

import co.aikar.commands.PaperCommandManager;
import com.grinderwolf.swm.plugin.SWMPlugin;
import com.minepalm.arkarangutils.bukkit.BukkitExecutor;
import com.minepalm.hellobungee.api.HelloClient;
import com.minepalm.hellobungee.api.HelloEveryone;
import com.minepalm.hellobungee.bukkit.HelloBukkit;
import com.minepalm.manyworlds.api.*;
import com.minepalm.manyworlds.api.bukkit.WorldEntityStorage;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.bukkit.WorldDatabase;
import com.minepalm.manyworlds.bukkit.executors.WorldCreatePacketExecutor;
import com.minepalm.manyworlds.bukkit.executors.WorldLoadPacketExecutor;
import com.minepalm.manyworlds.bukkit.mysql.MySQLWorldDatabase;
import com.minepalm.manyworlds.bukkit.swm.SWMWorldFactory;
import com.minepalm.manyworlds.bukkit.swm.SWMWorldLoadService;
import com.minepalm.manyworlds.bukkit.swm.SWMWorldStorage;
import com.minepalm.manyworlds.core.WorldTokens;
import com.minepalm.manyworlds.core.database.MySQLDatabase;
import com.minepalm.manyworlds.core.database.global.MySQLWorldNetwork;
import com.minepalm.manyworlds.core.netty.WorldCallbackAdapter;
import com.minepalm.manyworlds.core.netty.WorldCreatePacketAdapter;
import com.minepalm.manyworlds.core.netty.WorldLoadPacketAdapter;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.Executors;

public class ManyWorldsBukkitBootstrap extends JavaPlugin {

    @Getter
    private static ManyWorlds core;

    @Getter
    static ManyWorldsBukkitBootstrap inst;

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
        BukkitExecutor executor = new BukkitExecutor(this, Bukkit.getScheduler());
        MySQLDatabase database, worldDatabase;
        database = new MySQLDatabase(conf.getDatabaseProperties(), Executors.newScheduledThreadPool(4));
        worldDatabase = new MySQLDatabase(conf.getDatabaseProperties(), Executors.newScheduledThreadPool(4));
        WorldDatabase sample, user;
        sample = new MySQLWorldDatabase(conf.getSampleTableName(), worldDatabase);
        user = new MySQLWorldDatabase(WorldTokens.USER, conf.getSampleTableName(), conf.getUserTableName(), worldDatabase);
        WorldNetwork worldNetwork = new MySQLWorldNetwork(conf.proxyName(), new ServerView(serverName), conf.getServerTable(), conf.getWorldsTable(), database, getLogger());
        WorldEntityStorage storage = new SWMWorldStorage(100, executor, swm.getNms());
        WorldController controller = new ManyWorldController(network, Executors.newCachedThreadPool(), worldNetwork);
        WorldRegistry registry = new ManyWorldRegistry(worldNetwork, storage, controller);
        WorldLoadService loadService = new SWMWorldLoadService(Executors.newScheduledThreadPool(4), swm.getNms(), getLogger());
        core = new ManyWorlds(serverName, worldNetwork, controller, registry, storage, loadService, executor, this.getLogger());
        core.getLoadService().registerWorldFactory(WorldTokens.TYPE, new SWMWorldFactory(registry, executor));
        core.getLoadService().registerWorldFactory(WorldTokens.USER, new SWMWorldFactory(registry, executor));
        core.getRegistry().registerDatabase(sample);
        core.getRegistry().registerDatabase(user);
        network.getGateway().registerAdapter(new WorldCreatePacketAdapter());
        network.getGateway().registerAdapter(new WorldLoadPacketAdapter());
        network.getHandler().registerExecutor(new WorldCreatePacketExecutor(core));
        network.getHandler().registerExecutor(new WorldLoadPacketExecutor(core));
        network.getCallbackService().registerTransformer(new WorldCallbackAdapter.Create(registry));
        network.getCallbackService().registerTransformer(new WorldCallbackAdapter.Update(registry));
        network.getCallbackService().registerTransformer(new WorldCallbackAdapter.Move(registry));
        network.getCallbackService().registerTransformer(new WorldCallbackAdapter.Copy(registry));

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new Commands(core, swm, executor));
        manager.registerCommand(new BungeeCommands(core));

        //todo: HelloBungee 1.5로 올릴때, 열려있는 서버 목록 모듈 옮겨놓기.
        Map<String, HelloClient> map = network.getConnections().getClients();
        for (String key : map.keySet()) {
            HelloClient client = map.get(key);
            if(!client.isConnected()){
                worldNetwork.unregisterServer(key);
            }
        }

    }

    @Override
    public void onDisable() {
        core.shutdown();
    }
}

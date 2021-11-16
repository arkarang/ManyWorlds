package com.minepalm.manyworlds.bukkit;

import co.aikar.commands.PaperCommandManager;
import com.grinderwolf.swm.plugin.SWMPlugin;
import com.minepalm.arkarangutils.bukkit.BukkitExecutor;
import com.minepalm.hellobungee.api.HelloClient;
import com.minepalm.hellobungee.api.HelloEveryone;
import com.minepalm.hellobungee.bukkit.HelloBukkit;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.WorldController;
import com.minepalm.manyworlds.api.WorldNetwork;
import com.minepalm.manyworlds.api.WorldRegistry;
import com.minepalm.manyworlds.api.bukkit.WorldDatabase;
import com.minepalm.manyworlds.bukkit.executors.WorldCreatePacketExecutor;
import com.minepalm.manyworlds.bukkit.executors.WorldLoadPacketExecutor;
import com.minepalm.manyworlds.bukkit.mysql.MySQLWorldDatabase;
import com.minepalm.manyworlds.bukkit.swm.SWMWorldFactory;
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
        WorldNetwork worldNetwork = new MySQLWorldNetwork(conf.proxyName(), new ServerView(serverName), conf.getServerTable(), conf.getWorldsTable(), database, getLogger());
        WorldDatabase sample, user;
        sample = new MySQLWorldDatabase(WorldTokens.TYPE, conf.getSampleTableName(), worldDatabase);
        user = new MySQLWorldDatabase(WorldTokens.USER, conf.getUserTableName(), worldDatabase);
        WorldController controller = new ManyWorldController(network, Executors.newCachedThreadPool(), worldNetwork);
        WorldRegistry registry = new ManyWorldRegistry(worldNetwork, controller);
        core = new ManyWorlds(serverName, sample, user, worldNetwork, controller, registry, executor, swm, this.getLogger());
        core.getLoadService().registerWorldFactory(WorldTokens.TYPE, new SWMWorldFactory(sampleDB, worldEntityStorage, executor));
        core.getLoadService().registerWorldFactory(WorldTokens.USER, new SWMWorldFactory(userDB, worldEntityStorage, executor));
        core.getRegistry().registerDatabase(sampleDB);
        core.getRegistry().registerDatabase(userDB);
        network.getGateway().registerAdapter(new WorldCreatePacketAdapter());
        network.getGateway().registerAdapter(new WorldLoadPacketAdapter());
        network.getHandler().registerExecutor(new WorldCreatePacketExecutor(core));
        network.getHandler().registerExecutor(new WorldLoadPacketExecutor(core));
        network.getCallbackService().registerTransformer(new WorldCallbackAdapter.Create(registry));
        network.getCallbackService().registerTransformer(new WorldCallbackAdapter.Update(registry));

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new Commands(core, swm, executor));

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

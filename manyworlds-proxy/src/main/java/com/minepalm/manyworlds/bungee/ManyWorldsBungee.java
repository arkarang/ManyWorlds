package com.minepalm.manyworlds.bungee;

import com.minepalm.hellobungee.bungee.HelloBungee;
import com.minepalm.manyworlds.api.*;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.core.ManyWorlds;
import com.minepalm.manyworlds.core.database.global.MySQLGlobalDatabase;
import com.minepalm.manyworlds.core.netty.PacketFactory;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Getter
public class ManyWorldsBungee extends Plugin implements WorldLoadBalancer, BungeeView {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    @Getter
    private String serverName;

    ManyWorldsBungee inst;
    Conf conf;
    ManyWorldsCore core;
    GlobalDatabase database;

    @Override
    public void onEnable() {
        inst = this;
        conf = new Conf(this, "config.yml", true);
        serverName = conf.getName();
        database = new MySQLGlobalDatabase(serverName, this, conf.getServerTable(), conf.getWorldsTable(), conf.getProperties());
        core = new ManyWorlds(conf.getName(), database, new BukkitWorldController(HelloBungee.getConnections()));

        database.register();
    }

    @Override
    public void onDisable() {
        database.unregister();
    }

    @Override
    public int getTotalCount() {
        return database.getProxy().getTotalCount();
    }

    @Override
    public Future<Void> createAtLeast(WorldInfo info){
        return EXECUTOR_SERVICE.submit(()->{
            BukkitView least = null;
            int i = 1000;

            for (BukkitView view : database.getServers()) {
                least = least == null ? view : least;
                least = i > view.getLoadedWorlds() ? view : least;
                core.getController().send(PacketFactory.newPacket(this, least).createWorldCreate(info.getSampleName(), info.getWorldName()));
            }

            return null;
        });
    }

    @Override
    public Future<Void> loadAtLeast(WorldInfo info) {
        return EXECUTOR_SERVICE.submit(()->{
            BukkitView least = null;
            int i = 1000;

            for (BukkitView view : database.getServers()) {
                least = least == null ? view : least;
                least = i > view.getLoadedWorlds() ? view : least;
                core.getController().send(PacketFactory.newPacket(this, least).createWorldLoad(info.getWorldName(), true));
            }

            return null;
        });
    }

    @Override
    public Future<Void> createSpecific(BukkitView view, WorldInfo info) {
        return EXECUTOR_SERVICE.submit(()->{
            core.getController().send(PacketFactory.newPacket(this, view).createWorldCreate(info.getSampleName(), info.getWorldName()));
            return null;
        });
    }


    @Override
    public Future<Void> loadSpecific(BukkitView view, WorldInfo info, boolean onOff) {
        return EXECUTOR_SERVICE.submit(()->{
            core.getController().send(PacketFactory.newPacket(this, view).createWorldLoad(info.getWorldName(), onOff));
            return null;
        });
    }

}

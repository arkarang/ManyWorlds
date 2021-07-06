package com.minepalm.manyworlds.bungee;

import com.minepalm.hellobungee.bungee.HelloBungee;
import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.ManyWorldsCore;
import com.minepalm.manyworlds.api.WorldLoadBalancer;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.netty.Controller;
import com.minepalm.manyworlds.core.AbstractManyWorlds;
import com.minepalm.manyworlds.core.database.global.MySQLGlobalDatabase;
import com.minepalm.manyworlds.core.netty.PacketFactory;
import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProxyCore extends AbstractManyWorlds implements WorldLoadBalancer {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    @Getter
    static ProxyCore inst = null;
  
    @Getter
    GlobalDatabase database;
    ManyWorldsBungee plugin;

    public ProxyCore(ManyWorldsBungee plugin, GlobalDatabase globalDatabase, Controller controller) {
        super(plugin.getConf().getName(), globalDatabase, controller);
        if(inst == null)
            inst = this;
        else
            throw new IllegalStateException("ProxyCore already exists");
        this.plugin = plugin;
        database.register();
    }

    @Override
    public void shutdown() {
        database.unregister();
    }

    @Override
    public Future<Void> createAtLeast(WorldInfo info){
        return EXECUTOR_SERVICE.submit(()->{
            BukkitView least = null;
            int i = 1000;

            for (BukkitView view : database.getServers()) {
                least = least == null ? view : least;
                least = i > view.getLoadedWorlds() ? view : least;
                this.getController().send(PacketFactory.newPacket(plugin, least).createWorldCreate(info.getSampleName(), info.getWorldName()));
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
                this.getController().send(PacketFactory.newPacket(plugin, least).createWorldLoad(info.getWorldName(), true));
            }

            return null;
        });
    }

    @Override
    public Future<Void> createSpecific(BukkitView view, WorldInfo info) {
        return EXECUTOR_SERVICE.submit(()->{
            this.getController().send(PacketFactory.newPacket(plugin, view).createWorldCreate(info.getSampleName(), info.getWorldName()));
            return null;
        });
    }


    @Override
    public Future<Void> loadSpecific(BukkitView view, WorldInfo info, boolean onOff) {
        return EXECUTOR_SERVICE.submit(()->{
            this.getController().send(PacketFactory.newPacket(plugin, view).createWorldLoad(info.getWorldName(), onOff));
            return null;
        });
    }

}

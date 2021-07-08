package com.minepalm.manyworlds.bungee;

import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.WorldLoadbalancer;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.netty.Controller;
import com.minepalm.manyworlds.core.AbstractManyWorlds;
import com.minepalm.manyworlds.core.netty.PacketFactory;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProxyCore extends AbstractManyWorlds implements WorldLoadbalancer {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    @Getter
    static ProxyCore inst = null;

    ManyWorldsBungee plugin;

    public ProxyCore(ManyWorldsBungee plugin, GlobalDatabase globalDatabase, Controller controller) {
        super(plugin.getConf().getName(), globalDatabase, controller);
        if(inst == null)
            inst = this;
        else
            throw new IllegalStateException("ProxyCore already exists");
        this.plugin = plugin;
        super.getGlobalDatabase().register();
    }

    @Override
    public void shutdown() {
        super.getGlobalDatabase().unregister();
    }

    @Override
    public Future<ServerView> createAtLeast(WorldInfo info){
        return EXECUTOR_SERVICE.submit(()->{
            BukkitView least = null;
            int i = 1000;

            for (BukkitView view : super.getGlobalDatabase().getServers()) {
                ProxyServer.getInstance().getLogger().info("view: "+view.getServerName()+": "+view.getLoadedWorlds());
                least = least == null ? view : least;
                if(i > view.getLoadedWorlds()){
                    least = view;
                    i = view.getLoadedWorlds();
                }
                this.getController().send(PacketFactory.newPacket(plugin, least).createWorldCreate(info.getSampleName(), info.getWorldName()));
                ProxyServer.getInstance().getLogger().info("least: "+view.getServerName()+": "+view.getLoadedWorlds());
            }

            return least;
        });
    }

    @Override
    public Future<ServerView> loadAtLeast(WorldInfo info) {
        return EXECUTOR_SERVICE.submit(()->{
            BukkitView least = null;
            int i = 1000;

            for (BukkitView view : super.getGlobalDatabase().getServers()) {
                least = least == null ? view : least;
                least = i > view.getLoadedWorlds() ? view : least;
                this.getController().send(PacketFactory.newPacket(plugin, least).createWorldLoad(info.getWorldName(), true));
            }

            return least;
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

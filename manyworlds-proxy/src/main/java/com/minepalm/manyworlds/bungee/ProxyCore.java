package com.minepalm.manyworlds.bungee;

import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.WorldLoadbalancer;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.netty.Controller;
import com.minepalm.manyworlds.core.AbstractManyWorlds;
import com.minepalm.manyworlds.core.netty.WorldCreatePacket;
import com.minepalm.manyworlds.core.netty.WorldLoadPacket;
import lombok.Getter;

import java.util.List;
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
        controller.register(new WorldLoadPacketExecutor());
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

            for (BukkitView view : super.getGlobalDatabase().getServers().get()) {
                least = least == null ? view : least;
                if(i > view.getLoadedWorlds()){
                    least = view;
                    i = view.getLoadedWorlds();
                }
            }
            this.getController().send(new WorldCreatePacket(plugin, least, info.getSampleName(), info.getWorldName()));

            return least;
        });
    }

    @Override
    public Future<ServerView> loadAtLeast(WorldInfo info) {
        return EXECUTOR_SERVICE.submit(()->{
            BukkitView least = null;
            int i = 1000;

            for (BukkitView view : super.getGlobalDatabase().getServers().get()) {
                least = least == null ? view : least;
                least = i > view.getLoadedWorlds() ? view : least;
            }
            this.getController().send(new WorldLoadPacket(plugin, least, info, true));

            return least;
        });
    }

    @Override
    public Future<ServerView> getAtLeast() {
        return EXECUTOR_SERVICE.submit(()-> {
            BukkitView view = null;
            List<BukkitView> servers = ManyWorldsBungee.getDatabase().getServers().get();
            for (BukkitView server : servers) {
                if (view == null)
                    view = server;
                else {
                    view = view.getLoadedWorlds() < server.getLoadedWorlds() ? view : server;
                }
            }
            return view;
        });
    }

    @Override
    public Future<Void> createSpecific(BukkitView view, WorldInfo info) {
        return EXECUTOR_SERVICE.submit(()->{
            this.getController().send(new WorldCreatePacket(plugin, view, info.getSampleName(), info.getWorldName()));
            return null;
        });
    }

    @Override
    public Future<Void> loadSpecific(BukkitView view, WorldInfo info, boolean onOff) {
        return EXECUTOR_SERVICE.submit(()->{
            this.getController().send(new WorldLoadPacket(plugin, view, info, onOff));
            return null;
        });
    }

}

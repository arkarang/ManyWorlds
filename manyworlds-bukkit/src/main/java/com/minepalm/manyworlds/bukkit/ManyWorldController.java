package com.minepalm.manyworlds.bukkit;

import com.minepalm.hellobungee.api.HelloEveryone;
import com.minepalm.manyworlds.api.ManyWorld;
import com.minepalm.manyworlds.api.WorldController;
import com.minepalm.manyworlds.api.WorldNetwork;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.core.AbstractController;
import com.minepalm.manyworlds.core.netty.WorldCreatePacket;
import com.minepalm.manyworlds.core.netty.WorldLoadPacket;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ManyWorldController extends AbstractController implements WorldController {

    private final ExecutorService service;
    private final WorldNetwork worldNetwork;
    
    public ManyWorldController(HelloEveryone network, ExecutorService service, WorldNetwork worlds) {
        super(network);
        this.service = service;
        this.worldNetwork = worlds;
    }

    @Override
    public CompletableFuture<ServerView> createAtLeast(WorldInform origin, WorldInform info){
        return worldNetwork.getServers().thenApplyAsync(servers->{
            BukkitView least = null;
            int i = 1000;

            for (BukkitView view : servers) {
                least = least == null ? view : least;
                if(i > view.getWorldCounts()){
                    least = view;
                    i = view.getWorldCounts();
                }
            }

            if(least != null) {
                send(least, new WorldCreatePacket(origin, info));
            }
            return least;
        }, service);
    }

    @Override
    public CompletableFuture<ServerView> loadAtLeast(WorldInform info) {
        return worldNetwork.getServers().thenApplyAsync(list->{
            BukkitView least = null;
            int i = 1000;

            for (BukkitView view : list) {
                least = least == null ? view : least;
                least = i > view.getWorldCounts() ? view : least;
            }

            if(least != null)
                send(least, new WorldLoadPacket(info, true));

            return least;
        }, service);
    }

    @Override
    public CompletableFuture<ServerView> getAtLeast() {
        return worldNetwork.getServers().thenApplyAsync(servers->{
            BukkitView view = null;
            for (BukkitView server : servers) {
                if (view == null)
                    view = server;
                else {
                    view = view.getWorldCounts() < server.getWorldCounts() ? view : server;
                }
            }
            return view;
        }, service);
    }

    @Override
    public CompletableFuture<ManyWorld> createSpecific(BukkitView view, WorldInform origin, WorldInform info) {
        return callback(view, new WorldCreatePacket(origin, info));

    }

    @Override
    public CompletableFuture<ManyWorld> updateSpecific(BukkitView view, WorldInform info, boolean onOff) {
        return callback(view, new WorldLoadPacket(info, onOff));
    }

    @Override
    public CompletableFuture<ManyWorld> load(BukkitView view, WorldInform info) {
        return this.updateSpecific(view, info, true);
    }

    @Override
    public CompletableFuture<ManyWorld> move(BukkitView view) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public CompletableFuture<ManyWorld> copy(ManyWorld manyWorld) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public CompletableFuture<Boolean> unload(WorldInform info) {
        return worldNetwork.getLoadedServer(info).thenCompose(optional -> {
            if(optional.isPresent()){
                return this.updateSpecific(optional.get(), info, false).thenCompose(ManyWorld::isLoaded);
            }else
                return CompletableFuture.completedFuture(false);
        });
    }
}

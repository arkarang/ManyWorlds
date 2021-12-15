package com.minepalm.manyworlds.bukkit;

import com.minepalm.arkarangutils.bukkit.BukkitExecutor;
import com.minepalm.manyworlds.api.WorldLoadService;
import com.minepalm.manyworlds.api.WorldRegistry;
import com.minepalm.manyworlds.api.bukkit.WorldEntity;
import com.minepalm.manyworlds.api.bukkit.WorldEntityStorage;
import com.minepalm.manyworlds.api.entity.PreparedWorld;
import com.minepalm.manyworlds.api.entity.WorldInform;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/*
todo:
 1. SlimeNMS 의존하지 않게끔 구현하기 -> NMSHandler 구현
 2. CustomWorldServer 세이브 자체 구현 -> ?

 */
@RequiredArgsConstructor
public abstract class AbstractWorldEntityStorage implements WorldEntityStorage {

    //todo: 메모리 누수 위험, WorldEntity -> WorldInfo로 교체 필요.
    private static volatile ConcurrentHashMap<String, WorldEntity> worlds = new ConcurrentHashMap<>();

    @Getter
    protected final int maximumCounts;
    protected final BukkitExecutor executor;

    @Override
    public WorldEntity getLoadedWorld(String name) {
        return worlds.get(name);
    }

    @Override
    public List<String> getLoadedWorldsAll() {
        return new ArrayList<>(worlds.keySet());
    }

    @Override
    public WorldEntity getLoadedWorld(WorldInform info) {
        return getLoadedWorld(info.getName());
    }

    @Override
    public CompletableFuture<Void> registerWorld(WorldEntity world) {
        worlds.put(world.getWorldInform().getName(), world);
        return finalizeRegistration(world).thenRun(()-> executor.sync(()->{
            World bukkitWorld = Bukkit.getWorld(world.getWorldInform().getName());
            if (bukkitWorld != null) {
                bukkitWorld.save();
            }
        }));
    }

    protected abstract CompletableFuture<Void> finalizeRegistration(WorldEntity entity);

    @Override
    public WorldEntity unregisterWorld(String str) {
        return worlds.remove(str);
    }

    public void shutdown(WorldLoadService loadService) {
        List<CompletableFuture<PreparedWorld>> list = new ArrayList<>();
        for (WorldEntity value : worlds.values()) {
            list.add(loadService.unload(value));
        }
        try {
            CompletableFuture.allOf(list.toArray(new CompletableFuture<?>[0])).get();
        }catch (InterruptedException ignored){

        }catch (ExecutionException e){
            e.getCause().printStackTrace();
        }
    }

    @Override
    public int getCounts() {
        return worlds.size();
    }
}

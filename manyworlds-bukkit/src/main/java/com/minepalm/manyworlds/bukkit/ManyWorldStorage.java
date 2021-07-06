package com.minepalm.manyworlds.bukkit;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.grinderwolf.swm.nms.SlimeNMS;
import com.minepalm.manyworlds.api.bukkit.ManyWorld;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.bukkit.WorldStorage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
todo:
 1. SlimeNMS 의존하지 않게끔 구현하기 -> NMSHandler 구현
 2. CustomWorldServer 세이브 자체 구현 -> ?

 */
@RequiredArgsConstructor
public class ManyWorldStorage implements WorldStorage {

    private static final ExecutorService SERVICE = Executors.newFixedThreadPool(4);
    private static volatile HashMap<String, ManyWorld> worlds = new HashMap<>();
    private static final Multimap<String, String> nameHints = HashMultimap.create();

    private final SlimeNMS nms;

    @Getter
    private final int maximumCounts;

    public void registerWorld(ManyWorld world, List<String> subNames) {
        subNames.forEach(name->nameHints.put(name, world.getWorldInfo().getWorldName()));
        registerWorld(world);
    }

    @Override
    public Collection<String> getWorldAlias(String name) {
        return nameHints.get(name);
    }

    @Override
    public ManyWorld getLoadedWorld(String name) {
        return worlds.get(name);
    }

    @Override
    public List<ManyWorld> getLoadedWorlds(String tag) {
        List<ManyWorld> founds = new ArrayList<>();
        getWorldAlias(tag).forEach(alias -> founds.add(worlds.get(tag)));
        return founds;
    }

    @Override
    public List<String> getLoadedWorldsAll() {
        return new ArrayList<>(worlds.keySet());
    }

    @Override
    public ManyWorld getLoadedWorld(WorldInfo info) {
        return getLoadedWorld(info.getWorldName());
    }

    @Override
    public void registerWorld(ManyWorld world) {
        if(world instanceof CraftManyWorld) {
            Bukkit.getScheduler().runTask(ManyWorlds.getInst(), ()->{
                worlds.put(world.getName(), world);
                SERVICE.submit(()->nms.generateWorld(world));
            });
        }
    }

    @Override
    public ManyWorld unregisterWorld(String str) {
        ManyWorld mw = worlds.get(str);
        if(mw != null) {
            Optional<World> world = Optional.ofNullable(Bukkit.getWorld(mw.getWorldInfo().getWorldName()));
            world.ifPresent(w -> Bukkit.unloadWorld(w, true));
        }
        return mw;
    }

    //todo: SlimeLoader 의존성 삭제 후 이 메서드 제거.
    ManyWorld remove(String str){
        ManyWorld mw = worlds.remove(str);
        if(mw != null) {
            Optional<World> world = Optional.ofNullable(Bukkit.getWorld(mw.getWorldInfo().getWorldName()));
            world.ifPresent(w -> Bukkit.unloadWorld(w, true));
            System.out.println("NULL 아님!");
        }else{
            System.out.println("NULL 임!");
        }
        return mw;
    }

    void unregisterWorld(ManyWorld world){
        this.unregisterWorld(world.getName());
    }

    @Override
    public List<ManyWorld> unregisterWorlds(String tag) {
        List<ManyWorld> worlds = getLoadedWorlds(tag);
        worlds.forEach(this::unregisterWorld);
        return worlds;
    }

    @Override
    public void shutdown() {
        worlds.keySet().forEach(this::unregisterWorld);
    }

    @Override
    public int getCounts() {
        return worlds.size();
    }
}

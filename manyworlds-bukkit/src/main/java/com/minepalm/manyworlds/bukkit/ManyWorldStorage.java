package com.minepalm.manyworlds.bukkit;

import com.grinderwolf.swm.nms.SlimeNMS;
import com.minepalm.manyworlds.api.bukkit.ManyWorld;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.bukkit.WorldStorage;
import com.minepalm.manyworlds.bukkit.events.ManyWorldLoadAfterEvent;
import com.minepalm.manyworlds.bukkit.events.ManyWorldLoadBeforeEvent;
import com.minepalm.manyworlds.bukkit.events.ManyWorldUnloadAfterEvent;
import com.minepalm.manyworlds.bukkit.events.ManyWorldUnloadBeforeEvent;
import com.minepalm.manyworlds.core.netty.WorldLoadPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/*
todo:
 1. SlimeNMS 의존하지 않게끔 구현하기 -> NMSHandler 구현
 2. CustomWorldServer 세이브 자체 구현 -> ?

 */
@RequiredArgsConstructor
public class ManyWorldStorage implements WorldStorage {

    //todo: 메모리 누수 위험, ManyWorld -> WorldInfo로 교체 필요.
    private static volatile HashMap<String, ManyWorld> worlds = new HashMap<>();

    private final SlimeNMS nms;

    @Getter
    private final int maximumCounts;

    @Override
    public ManyWorld getLoadedWorld(String name) {
        return worlds.get(name);
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
        this.registerWorld(world, ()->{});
    }

    @Override
    public void registerWorld(ManyWorld world, Runnable after) {
        if(world instanceof CraftManyWorld) {
            Bukkit.getScheduler().runTask(ManyWorlds.getInst(), ()->{
                try {
                    ManyWorldLoadBeforeEvent event = new ManyWorldLoadBeforeEvent(world.getWorldInfo(), world);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        worlds.put(world.getName(), world);
                        nms.generateWorld(world);
                        ManyWorlds.getGlobalDatabase().registerWorld(ManyWorlds.getInst(), world.getWorldInfo());
                        ManyWorlds.send(new WorldLoadPacket(ManyWorlds.getInst(), ManyWorlds.getGlobalDatabase().getProxy(), world.getWorldInfo(), true));
                        Bukkit.getPluginManager().callEvent(new ManyWorldLoadAfterEvent(world.getWorldInfo(), world));
                        World bukkitWorld = Bukkit.getWorld(world.getName());
                        if (bukkitWorld != null) {
                            bukkitWorld.save();
                        }
                        after.run();
                    }
                }catch (Exception e){
                    Bukkit.getLogger().warning("경고!@#!@# \n"+e.getMessage());
                }
            });
        }
    }

    @Override
    public ManyWorld unregisterWorld(String str) {
        ManyWorld mw = worlds.get(str);
        if(mw != null) {
            Optional<World> world = Optional.ofNullable(Bukkit.getWorld(mw.getWorldInfo().getWorldName()));
            if(world.isPresent()){
                ManyWorldUnloadBeforeEvent event = new ManyWorldUnloadBeforeEvent(mw.getWorldInfo(), mw);
                Bukkit.getScheduler().runTask(ManyWorlds.getInst(), ()->{
                    Bukkit.getPluginManager().callEvent(event);
                    if(!event.isCancelled()) {
                        Bukkit.unloadWorld(world.get(), true);
                        ManyWorlds.getGlobalDatabase().unregisterWorld(str);
                        ManyWorlds.send(new WorldLoadPacket(ManyWorlds.getInst(), ManyWorlds.getGlobalDatabase().getProxy(), mw.getWorldInfo(), false));
                        Bukkit.getPluginManager().callEvent(new ManyWorldUnloadAfterEvent(mw.getWorldInfo(), mw));
                    }
                });
            }
        }
        return mw;
    }

    void unregisterWorldFromDatabase(String worldName){
        ManyWorlds.getGlobalDatabase().unregisterWorld(worldName);
    }

    void unregisterWorld(ManyWorld world){
        this.unregisterWorld(world.getName());
    }

    @Override
    public void shutdown() {
        worlds.keySet().forEach(this::unregisterWorldFromDatabase);
    }

    @Override
    public int getCounts() {
        return worlds.size();
    }
}

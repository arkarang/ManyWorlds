package com.minepalm.manyworlds.api.bukkit;

import java.util.Collection;
import java.util.List;

public interface WorldStorage {

    void registerWorld(ManyWorld world);

    void registerWorld(ManyWorld world, List<String> alias);

    Collection<String> getWorldAlias(String name);

    ManyWorld getLoadedWorld(String name);

    List<ManyWorld> getLoadedWorlds(String tag);

    List<String> getLoadedWorldsAll();

    ManyWorld getLoadedWorld(WorldInfo info);

    ManyWorld unregisterWorld(String name);

    List<ManyWorld> unregisterWorlds(String tag);

    void shutdown();

    int getCounts();

    int getMaximumCounts();

}

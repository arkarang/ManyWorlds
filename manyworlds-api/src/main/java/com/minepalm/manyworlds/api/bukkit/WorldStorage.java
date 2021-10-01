package com.minepalm.manyworlds.api.bukkit;

import java.util.List;

public interface WorldStorage {

    void registerWorld(ManyWorld world);

    void registerWorld(ManyWorld world, Runnable run);

    ManyWorld getLoadedWorld(String name);

    List<String> getLoadedWorldsAll();

    ManyWorld getLoadedWorld(WorldInfo info);

    ManyWorld unregisterWorld(String name);

    void shutdown();

    int getCounts();

    int getMaximumCounts();

}

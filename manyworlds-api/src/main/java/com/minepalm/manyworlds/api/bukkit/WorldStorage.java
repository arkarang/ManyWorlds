package com.minepalm.manyworlds.api.bukkit;

import java.util.Collection;
import java.util.List;

public interface WorldStorage {

    void registerWorld(ManyWorld world);


    ManyWorld getLoadedWorld(String name);

    List<String> getLoadedWorldsAll();

    ManyWorld getLoadedWorld(WorldInfo info);

    ManyWorld unregisterWorld(String name);

    void shutdown();

    int getCounts();

    int getMaximumCounts();

}

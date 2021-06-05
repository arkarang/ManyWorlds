package com.minepalm.manyworlds.api.bukkit;

import java.util.UUID;

public interface WorldStorage {

    void registerWorld(ManyWorld world);

    ManyWorld unregisterWorld(UUID uuid);

    void shutdown();

    int getCounts();

    int getMaximumCounts();

}

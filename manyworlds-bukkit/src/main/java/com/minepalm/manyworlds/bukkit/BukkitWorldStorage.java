package com.minepalm.manyworlds.bukkit;

import com.minepalm.manyworlds.api.bukkit.ManyWorld;
import com.minepalm.manyworlds.api.bukkit.WorldStorage;

import java.util.UUID;

//todo: 구현
public class BukkitWorldStorage implements WorldStorage {

    @Override
    public void registerWorld(ManyWorld world) {

    }

    @Override
    public ManyWorld unregisterWorld(UUID uuid) {
        return null;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public int getCounts() {
        return 0;
    }

    @Override
    public int getMaximumCounts() {
        return 0;
    }
}

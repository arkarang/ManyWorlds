package com.minepalm.manyworlds.bukkit.swm;

import com.grinderwolf.swm.nms.SlimeNMS;
import com.minepalm.arkarangutils.bukkit.BukkitExecutor;
import com.minepalm.manyworlds.api.bukkit.WorldEntity;
import com.minepalm.manyworlds.bukkit.AbstractWorldEntityStorage;

import java.util.concurrent.CompletableFuture;

public class SWMWorldStorage extends AbstractWorldEntityStorage {

    final SlimeNMS nms;

    public SWMWorldStorage(int maximumCounts, BukkitExecutor executor, SlimeNMS nms) {
        super(maximumCounts, executor);
        this.nms = nms;
    }

    @Override
    protected CompletableFuture<Void> finalizeRegistration(WorldEntity world) {
        return executor.sync(()->{
            nms.generateWorld((SWMWorldEntity)world);
        });
    }
}

package com.minepalm.manyworlds.api;

import com.minepalm.manyworlds.api.bukkit.WorldInfo;

import java.util.concurrent.Future;

public interface WorldLoadBalancer {

    Future<Void> createAtLeast(WorldInfo info);

    Future<Void> loadAtLeast(WorldInfo info);

    Future<Void> createSpecific(BukkitView view, WorldInfo info);

    Future<Void> loadSpecific(BukkitView view, WorldInfo info, boolean onOff);


}

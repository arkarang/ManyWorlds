package com.minepalm.manyworlds.api;

import com.minepalm.manyworlds.api.bukkit.WorldInfo;

import java.util.concurrent.Future;

public interface WorldLoadbalancer {

    Future<ServerView> createAtLeast(WorldInfo info);

    Future<ServerView> loadAtLeast(WorldInfo info);

    Future<Void> createSpecific(BukkitView view, WorldInfo info);

    Future<Void> loadSpecific(BukkitView view, WorldInfo info, boolean onOff);


}

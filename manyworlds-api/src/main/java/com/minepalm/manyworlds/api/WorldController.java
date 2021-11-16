package com.minepalm.manyworlds.api;

import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.api.netty.Controller;

import java.util.concurrent.CompletableFuture;

public interface WorldController extends Controller {

    CompletableFuture<ServerView> createAtLeast(WorldInform info);

    CompletableFuture<ServerView> loadAtLeast(WorldInform info);

    CompletableFuture<ServerView> getAtLeast();

    CompletableFuture<ManyWorld> createSpecific(BukkitView view, WorldInform info);

    CompletableFuture<ManyWorld> updateSpecific(BukkitView view, WorldInform info, boolean onOff);

    CompletableFuture<ManyWorld> load(BukkitView view, WorldInform info);

    CompletableFuture<Boolean> unload(WorldInform info);
}

package com.mineplam.manyworlds.core.test.stubs;

import com.minepalm.manyworlds.api.WorldNetwork;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TestWorldNetwork implements WorldNetwork {

    @Override
    public ServerView getCurrentServer() {
        return new ServerView("test");
    }

    @Override
    public CompletableFuture<ServerView> getServer(String name) {
        return CompletableFuture.completedFuture(new ServerView(name));
    }

    @Override
    public CompletableFuture<BukkitView> getBukkitServer(String name) {
        return null;
    }

    @Override
    public CompletableFuture<List<BukkitView>> getServers() {
        return null;
    }

    @Override
    public CompletableFuture<List<String>> getLoadedWorlds(String serverName) {
        return null;
    }

    @Override
    public void registerServer() {

    }

    @Override
    public CompletableFuture<Void> registerServer(String name) {
        return null;
    }

    @Override
    public void unregister() {

    }

    @Override
    public CompletableFuture<Void> unregisterServer(String name) {
        return null;
    }

    @Override
    public CompletableFuture<Void> registerWorld(BukkitView snapshot, WorldInform info) {
        return null;
    }

    @Override
    public CompletableFuture<Void> unregisterWorld(String fullName) {
        return null;
    }

    @Override
    public CompletableFuture<Void> unregisterWorld(String serverName, String sampleName, UUID uuid) {
        return null;
    }

    @Override
    public CompletableFuture<Void> resetWorlds(ServerView view) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> isWorldLoaded(WorldInform info) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> isWorldLoaded(String fullName) {
        return null;
    }

    @Override
    public CompletableFuture<Optional<BukkitView>> getLoadedServer(WorldInform info) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> isWorldLoaded(String serverName, String sampleName, UUID uuid) {
        return null;
    }
}

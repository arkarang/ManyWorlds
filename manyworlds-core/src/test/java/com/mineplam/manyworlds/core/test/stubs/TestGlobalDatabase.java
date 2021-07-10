package com.mineplam.manyworlds.core.test.stubs;

import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.BungeeView;
import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestGlobalDatabase implements GlobalDatabase {

    ExecutorService service = Executors.newSingleThreadExecutor();

    @Override
    public BungeeView getProxy() {
        return null;
    }

    @Override
    public ServerView getCurrentServer() {
        return () -> "test";
    }

    @Override
    public Future<ServerView> getServer(String name) {
        return service.submit(()-> ()->name);
    }

    @Override
    public Future<BukkitView> getBukkitServer(String name) {
        return null;
    }

    @Override
    public Future<List<BukkitView>> getServers() {
        return null;
    }

    @Override
    public Future<List<String>> getLoadedWorlds(String serverName) {
        return null;
    }

    @Override
    public void register() {

    }

    @Override
    public void unregister() {

    }

    @Override
    public Future<Void> registerWorld(BukkitView snapshot, WorldInfo info) {
        return null;
    }

    @Override
    public Future<Void> unregisterWorld(String fullName) {
        return null;
    }

    @Override
    public Future<Void> unregisterWorld(String serverName, String sampleName, UUID uuid) {
        return null;
    }

    @Override
    public Future<Void> resetWorlds(ServerView view) {
        return null;
    }

    @Override
    public Future<Boolean> isWorldLoaded(WorldInfo info) {
        return null;
    }

    @Override
    public Future<Boolean> isWorldLoaded(String fullName) {
        return null;
    }

    @Override
    public Future<Optional<BukkitView>> getLoadedServer(WorldInfo info) {
        return null;
    }

    @Override
    public Future<Boolean> isWorldLoaded(String serverName, String sampleName, UUID uuid) {
        return null;
    }

}

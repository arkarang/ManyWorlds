package com.mineplam.manyworlds.core.test.stubs;

import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.BungeeView;
import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TestGlobalDatabase implements GlobalDatabase {
    @Override
    public BungeeView getProxy() {
        return null;
    }

    @Override
    public ServerView getCurrentServer() {
        return new ServerView() {
            @Override
            public String getServerName() {
                return "test";
            }
        };
    }

    @Override
    public ServerView getServer(String name) {
        return () -> name;
    }

    @Override
    public BukkitView getBukkitServer(String name) {
        return null;
    }

    @Override
    public List<BukkitView> getServers() {
        return null;
    }

    @Override
    public List<String> getLoadedWorlds(String serverName) {
        return null;
    }

    @Override
    public void register() {

    }

    @Override
    public void unregister() {

    }

    @Override
    public void registerWorld(BukkitView snapshot, WorldInfo info) {

    }

    @Override
    public void unregisterWorld(String fullName) {

    }

    @Override
    public void unregisterWorld(String serverName, String sampleName, UUID uuid) {

    }

    @Override
    public void resetWorlds(ServerView view) {

    }

    @Override
    public boolean isWorldLoaded(WorldInfo info) {
        return false;
    }

    @Override
    public boolean isWorldLoaded(String fullName) {
        return false;
    }

    @Override
    public Optional<BukkitView> getLoadedServer(WorldInfo info) {
        return Optional.empty();
    }

    @Override
    public boolean isWorldLoaded(String serverName, String sampleName, UUID uuid) {
        return false;
    }
}

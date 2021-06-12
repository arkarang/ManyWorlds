package com.minepalm.manyworlds.core.database.global;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.BungeeView;
import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.core.database.AbstractRedis;
import com.minepalm.manyworlds.core.server.BukkitServerView;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

//todo: 나중으로
public class RedisGlobalDatabase extends AbstractRedis implements GlobalDatabase {

    @Getter
    BungeeView proxy;

    @Getter
    ServerView currentServer;

    private final ObjectMapper mapper = new ObjectMapper();

    public RedisGlobalDatabase(ServerView snapshot, Properties props) {
        super(props);
        this.currentServer = snapshot;
    }


    @Override
    public ServerView getServer(String name) {
        return null;
    }

    @Override
    public BukkitView getBukkitServer(String name) {
        String json = this.getSyncCommands().get("server_"+name);
        try {
            return mapper.readValue(json, BukkitServerView.class);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<BukkitView> getServers() {
        List<BukkitView> views = new ArrayList<>();
        String servers = this.getSyncCommands().get("servers");

        try {
            List<String> list = mapper.readValue(servers, List.class);
            list.forEach(name->{
                views.add(getBukkitServer(name));
            });
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        return views;
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
    public boolean isWorldLoaded(WorldInfo info) {
        return false;
    }

    @Override
    public boolean isWorldLoaded(String fullName) {
        return false;
    }

    @Override
    public boolean isWorldLoaded(String serverName, String sampleName, UUID uuid) {
        return false;
    }
}

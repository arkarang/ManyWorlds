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

import java.util.*;

//todo: 나중으로
public abstract class RedisGlobalDatabase extends AbstractRedis implements GlobalDatabase {

    @Getter
    BungeeView proxy;

    @Getter
    ServerView currentServer;

    private final ObjectMapper mapper = new ObjectMapper();

    public RedisGlobalDatabase(ServerView snapshot, Properties props) {
        super(props);
        this.currentServer = snapshot;
    }

}

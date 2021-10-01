package com.minepalm.manyworlds.core.database.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minepalm.manyworlds.api.BungeeView;
import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.core.database.AbstractRedis;
import lombok.Getter;

import java.util.Properties;

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

package com.minepalm.manyworlds.core.database.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minepalm.manyworlds.api.entity.BungeeView;
import com.minepalm.manyworlds.api.WorldNetwork;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.core.database.AbstractRedis;

import java.util.Properties;

//todo: 나중으로
public abstract class RedisWorldNetwork extends AbstractRedis implements WorldNetwork {

    BungeeView proxy;

    ServerView currentServer;

    private final ObjectMapper mapper = new ObjectMapper();

    public RedisWorldNetwork(ServerView snapshot, Properties props) {
        super(props);
        this.currentServer = snapshot;
    }

}

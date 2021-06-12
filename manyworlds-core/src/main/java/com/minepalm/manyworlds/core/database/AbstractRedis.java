package com.minepalm.manyworlds.core.database;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Properties;

@Getter(AccessLevel.PROTECTED)
public abstract class AbstractRedis {

    RedisClient redisClient;
    StatefulRedisConnection<String, String> connection;
    RedisCommands<String, String> syncCommands;
    RedisAsyncCommands<String, String> asyncCommands;

    private final String redisURI;

    public AbstractRedis(Properties props){
        String address = props.getProperty("address", "localhost");
        String password = props.getProperty("password");
        String port = props.getProperty("port", "6379");
        String machineNumber = props.getProperty("user", "0");
        //String timeout = props.getProperty("timeout", "60");
        redisURI = "redis://"+password+"@"+address+":"+port+"/"+machineNumber;
        //"redis://password@localhost:6379/0";

    }

    void connect(){
        redisClient = RedisClient.create(redisURI);
        connection = redisClient.connect();
        syncCommands = connection.sync();
        asyncCommands = connection.async();
    }

    void goodbye(){
        connection.close();
        redisClient.shutdown();
    }
}

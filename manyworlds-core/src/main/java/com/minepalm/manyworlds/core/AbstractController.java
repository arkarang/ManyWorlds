package com.minepalm.manyworlds.core;

import com.minepalm.hellobungee.api.HelloEveryone;
import com.minepalm.manyworlds.api.ManyWorld;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.netty.Controller;
import com.minepalm.manyworlds.api.netty.WorldPacket;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractController implements Controller {

    protected final HelloEveryone network;

    public AbstractController(HelloEveryone network){
        this.network = network;
        /*
        this.network.getGateway().registerAdapter(new ServerUpdatedPacketAdapter());
        this.network.getGateway().registerAdapter(new WorldCreatePacketAdapter());
        this.network.getGateway().registerAdapter(new WorldLoadPacketAdapter());
         */
    }

    @Override
    public void send(ServerView view, WorldPacket packet) {
        this.network.sender(view.getServerName()).send(packet);
    }

    public CompletableFuture<ManyWorld> callback(ServerView view, WorldPacket packet){
        return this.network.sender(view.getServerName()).callback(packet, ManyWorld.class).async();
    }

}

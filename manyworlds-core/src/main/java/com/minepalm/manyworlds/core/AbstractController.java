package com.minepalm.manyworlds.core;

import com.minepalm.hellobungee.api.HelloEveryone;
import com.minepalm.hellobungee.api.HelloExecutor;
import com.minepalm.manyworlds.api.netty.Controller;
import com.minepalm.manyworlds.api.netty.WorldPacket;
import com.minepalm.manyworlds.core.netty.ServerUpdatedPacketAdapter;
import com.minepalm.manyworlds.core.netty.WorldCreatePacketAdapter;
import com.minepalm.manyworlds.core.netty.WorldLoadPacketAdapter;

public abstract class AbstractController implements Controller {

    protected final HelloEveryone network;

    public AbstractController(HelloEveryone network){
        this.network = network;
        this.network.getGateway().registerAdapter(new ServerUpdatedPacketAdapter());
        this.network.getGateway().registerAdapter(new WorldCreatePacketAdapter());
        this.network.getGateway().registerAdapter(new WorldLoadPacketAdapter());
    }

    @Override
    public void send(WorldPacket packet) {
        this.network.sender(packet.getTo().getServerName()).send(packet);
    }

    @Override
    public <T extends WorldPacket> void register(HelloExecutor<T> executor) {
        network.getHandler().registerExecutor(executor);
    }
}

package com.minepalm.manyworlds.api.netty;

import com.minepalm.hellobungee.api.HelloExecutor;

public interface Controller {

    void send(WorldPacket packet);

    <T extends WorldPacket> void register(HelloExecutor<T> executor);

}

package com.minepalm.manyworlds.api.netty;


public interface Controller {

    void send(WorldPacket packet);

    boolean canExecute(byte packetID);

}

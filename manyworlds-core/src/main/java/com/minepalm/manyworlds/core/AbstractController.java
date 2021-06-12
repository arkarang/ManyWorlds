package com.minepalm.manyworlds.core;

import com.minepalm.hellobungee.api.HelloConnections;
import com.minepalm.manyworlds.api.netty.Controller;
import com.minepalm.manyworlds.api.netty.WorldPacket;
import com.minepalm.manyworlds.core.netty.PacketTypes;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractController implements Controller {

    final HelloConnections connections;
    final protected List<PacketTypes> canExecutes = new ArrayList<>();

    public AbstractController(HelloConnections connections){
        this.connections = connections;
    }

    @Override
    public void send(WorldPacket packet) {
        if(canExecute(packet.getPacketID()))
            connections.getClient(packet.getTo().getServerName()).sendData(packet);
    }

    @Override
    public boolean canExecute(byte packetID) {
        return canExecutes.stream().anyMatch(pt->pt.index() == packetID);
    }
}

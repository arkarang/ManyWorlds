package com.minepalm.manyworlds.core;

import com.minepalm.hellobungee.api.HelloClient;
import com.minepalm.hellobungee.api.HelloConnections;
import com.minepalm.manyworlds.api.netty.Controller;
import com.minepalm.manyworlds.api.netty.WorldPacket;
import com.minepalm.manyworlds.core.netty.PacketTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractController implements Controller {

    final HelloConnections connections;
    final protected List<PacketTypes> canExecutes = new ArrayList<>();

    public AbstractController(HelloConnections connections){
        this.connections = connections;
    }

    @Override
    public void send(WorldPacket packet) {
        if(canExecute(packet.getPacketID())) {
            Optional<HelloClient> optional = Optional.ofNullable(connections.getClient(packet.getTo().getServerName()));
            if(optional.isPresent()){
                optional.get().sendData(packet);
            }else{
                throw new IllegalArgumentException("cannot find destination: "+packet.getTo());
            }
        }else
            throw new IllegalArgumentException("cant execute packet type: "+packet.getClass().getSimpleName());
    }

    @Override
    public boolean canExecute(byte packetID) {
        return canExecutes.stream().anyMatch(pt->pt.index() == packetID);
    }
}

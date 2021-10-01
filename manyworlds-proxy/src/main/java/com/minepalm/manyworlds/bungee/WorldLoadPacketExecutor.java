package com.minepalm.manyworlds.bungee;

import com.minepalm.hellobungee.api.HelloExecutor;
import com.minepalm.manyworlds.bungee.events.ManyWorldLoadEvent;
import com.minepalm.manyworlds.bungee.events.ManyWorldUnloadEvent;
import com.minepalm.manyworlds.core.netty.WorldLoadPacket;
import net.md_5.bungee.api.ProxyServer;

public class WorldLoadPacketExecutor implements HelloExecutor<WorldLoadPacket> {

    @Override
    public String getIdentifier() {
        return WorldLoadPacket.class.getSimpleName();
    }

    @Override
    public void executeReceived(WorldLoadPacket packet) {
        if(packet.isLoad()){
            ProxyServer.getInstance().getPluginManager().callEvent(new ManyWorldLoadEvent(packet.getSampleName(), packet.getWorldName()));
        }else
            ProxyServer.getInstance().getPluginManager().callEvent(new ManyWorldUnloadEvent(packet.getSampleName(), packet.getWorldName()));
    }
}

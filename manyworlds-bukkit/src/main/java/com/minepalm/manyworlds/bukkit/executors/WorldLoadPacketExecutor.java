package com.minepalm.manyworlds.bukkit.executors;

import com.minepalm.hellobungee.api.HelloExecutor;
import com.minepalm.manyworlds.api.bukkit.WorldManager;
import com.minepalm.manyworlds.core.ManyWorldInfo;
import com.minepalm.manyworlds.core.WorldToken;
import com.minepalm.manyworlds.core.netty.WorldLoadPacket;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

@RequiredArgsConstructor
public class WorldLoadPacketExecutor implements HelloExecutor<WorldLoadPacket> {

    final WorldManager manager;

    @Override
    public String getIdentifier() {
        return WorldLoadPacket.class.getSimpleName();
    }

    @Override
    public void executeReceived(WorldLoadPacket packet) {
        if(packet.isLoad()) {
            if(Bukkit.getWorld(packet.getWorldName()) == null)
                manager.loadWorld(new ManyWorldInfo(WorldToken.get(packet.getSampleName()), packet.getWorldName()));
        }else {
            if(Bukkit.getWorld(packet.getWorldName()) != null)
                manager.unload(new ManyWorldInfo(WorldToken.get(packet.getSampleName()), packet.getWorldName()));
        }
    }
}

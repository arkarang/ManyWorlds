package com.minepalm.manyworlds.bukkit.executors;

import com.minepalm.hellobungee.api.HelloExecutor;
import com.minepalm.manyworlds.api.WorldService;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.core.WorldToken;
import com.minepalm.manyworlds.core.netty.WorldLoadPacket;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

@RequiredArgsConstructor
public class WorldLoadPacketExecutor implements HelloExecutor<WorldLoadPacket> {

    final WorldService manager;

    @Override
    public String getIdentifier() {
        return WorldLoadPacket.class.getSimpleName();
    }

    @Override
    public void executeReceived(WorldLoadPacket packet) {
        if(packet.isLoad()) {
            if(Bukkit.getWorld(packet.getInform().getName()) == null)
                manager.loadWorld(packet.getInform());
        }else {
            if(Bukkit.getWorld(packet.getInform().getName()) != null) {
                manager.unload(packet.getInform());
            }
        }
    }
}

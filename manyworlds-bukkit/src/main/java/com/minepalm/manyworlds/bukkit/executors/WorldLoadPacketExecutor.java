package com.minepalm.manyworlds.bukkit.executors;

import com.minepalm.hellobungee.api.HelloExecutor;
import com.minepalm.manyworlds.api.WorldService;
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
            if(Bukkit.getWorld(packet.getWorldInform().getName()) == null)
                manager.loadWorld(packet.getWorldInform());
        }else {
            if(Bukkit.getWorld(packet.getWorldInform().getName()) != null) {
                manager.unload(packet.getWorldInform());
            }
        }
    }
}

package com.minepalm.manyworlds.bukkit.executors;

import com.minepalm.hellobungee.api.HelloExecutor;
import com.minepalm.manyworlds.api.WorldService;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.core.WorldTokens;
import com.minepalm.manyworlds.core.netty.WorldCreatePacket;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorldCreatePacketExecutor implements HelloExecutor<WorldCreatePacket> {


    final WorldService manager;

    @Override
    public String getIdentifier() {
        return WorldCreatePacket.class.getSimpleName();
    }

    @Override
    public void executeReceived(WorldCreatePacket packet) {
        manager.createNewWorld(packet.getInform());
    }
}

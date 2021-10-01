package com.minepalm.manyworlds.bukkit.executors;

import com.minepalm.hellobungee.api.HelloExecutor;
import com.minepalm.manyworlds.api.bukkit.WorldManager;
import com.minepalm.manyworlds.core.ManyWorldInfo;
import com.minepalm.manyworlds.core.WorldTokens;
import com.minepalm.manyworlds.core.netty.WorldCreatePacket;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WorldCreatePacketExecutor implements HelloExecutor<WorldCreatePacket> {


    final WorldManager manager;

    @Override
    public String getIdentifier() {
        return WorldCreatePacket.class.getSimpleName();
    }

    @Override
    public void executeReceived(WorldCreatePacket packet) {
        manager.createNewWorld(new ManyWorldInfo(WorldTokens.USER, packet.getSampleName(), packet.getWorldName()));
    }
}

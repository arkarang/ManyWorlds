package com.minepalm.manyworlds.bukkit.executors;

import com.minepalm.hellobungee.api.HelloExecutor;
import com.minepalm.manyworlds.api.WorldService;
import com.minepalm.manyworlds.core.netty.WorldCreatePacket;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

@RequiredArgsConstructor
public class WorldCreatePacketExecutor implements HelloExecutor<WorldCreatePacket> {

    final WorldService manager;

    @Override
    public String getIdentifier() {
        return WorldCreatePacket.class.getSimpleName();
    }

    @Override
    @SneakyThrows
    public void executeReceived(WorldCreatePacket packet) {
        manager.createNewWorld(packet.getOrigin(), packet.getWorldInform()).thenAccept(result->{
            if(result == null){
                Bukkit.getLogger().info("result is null");
            }else{
                Bukkit.getLogger().info("result class: "+result.getClass().getSimpleName());
            }
        }).get();
    }
}

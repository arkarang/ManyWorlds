package com.minepalm.manyworlds.bukkit;

import com.minepalm.hellobungee.bukkit.events.BukkitMessageReceivedEvent;
import com.minepalm.manyworlds.api.bukkit.WorldManager;
import com.minepalm.manyworlds.api.netty.WorldPacket;
import com.minepalm.manyworlds.core.netty.PacketExecutor;
import com.minepalm.manyworlds.core.netty.PacketResolver;
import com.minepalm.manyworlds.core.netty.exception.CorruptedPacketException;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RequiredArgsConstructor
public class PacketListener implements Listener {

    final WorldManager manager;
    final PacketResolver resolver;

    final HashMap<Class<? extends WorldPacket>, PacketExecutor<? extends WorldPacket>> map = new HashMap<>();

    @SuppressWarnings("unchecked")
    @EventHandler
    public <T extends WorldPacket> void onReceived(BukkitMessageReceivedEvent event){
        try {
            Future<? extends WorldPacket> future = resolver.resolve(event.getMessage().get());
            T packet = (T)future.get();
            Optional.ofNullable(map.get(packet.getClass())).ifPresent(pe->((PacketExecutor<T>)pe).execute(packet));
        } catch (InterruptedException | ExecutionException | CorruptedPacketException ignored) {
            //discard packet
        }
    }

    <T extends WorldPacket> void register(Class<T> clazz, PacketExecutor<T> executor){
        map.put(clazz, executor);
    }
}

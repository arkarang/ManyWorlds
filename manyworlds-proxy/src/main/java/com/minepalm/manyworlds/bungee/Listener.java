package com.minepalm.manyworlds.bungee;

import com.minepalm.hellobungee.api.HelloMessage;
import com.minepalm.hellobungee.bungee.events.BungeeMessageReceivedEvent;
import com.minepalm.manyworlds.api.netty.WorldPacket;
import com.minepalm.manyworlds.core.netty.PacketExecutor;
import com.minepalm.manyworlds.core.netty.PacketResolver;
import com.minepalm.manyworlds.core.netty.exception.CorruptedPacketException;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@RequiredArgsConstructor
public class Listener implements net.md_5.bungee.api.plugin.Listener {


    ExecutorService service = Executors.newSingleThreadExecutor();
    final PacketResolver resolver;

    final HashMap<Class<? extends WorldPacket>, PacketExecutor<? extends WorldPacket>> map = new HashMap<>();

    @SuppressWarnings("unchecked")
    @EventHandler
    public <T extends WorldPacket> void onReceived(BungeeMessageReceivedEvent event){
        service.submit(()->{
            try {
                event.getMessage().get();
                Future<? extends WorldPacket> future = resolver.resolve(event.getMessage().get());
                T packet = (T)future.get();
                Optional.ofNullable(map.get(packet.getClass())).ifPresent(pe->((PacketExecutor<T>)pe).execute(packet));
            } catch (InterruptedException | ExecutionException | CorruptedPacketException ignored) {
                //discard packet
            }
        });
    }

    <T extends WorldPacket> void register(Class<T> clazz, PacketExecutor<T> executor){
        map.put(clazz, executor);
    }
}

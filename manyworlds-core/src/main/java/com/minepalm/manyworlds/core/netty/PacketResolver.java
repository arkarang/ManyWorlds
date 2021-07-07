package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.netty.WorldPacket;
import com.minepalm.manyworlds.core.netty.exception.CorruptedPacketException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class PacketResolver {

    private final HashMap<Byte, ResolveStrategy<?>> strategies = new HashMap<>();

    final ExecutorService service;
    final GlobalDatabase database;

    public PacketResolver(ExecutorService service, GlobalDatabase database){
        this.service = service;
        this.database = database;

        register(PacketTypes.WORLD_CREATE, (factory, left) -> {
            String sampleName = readString(left);
            String worldName = readString(left);

            return factory.createWorldCreate(sampleName, worldName);
        });
        register(PacketTypes.WORLD_LOAD_UNLOAD, (factory, left) -> {
            String worldName = readString(left);
            boolean b = left.readBoolean();
            return factory.createWorldLoad(worldName, b);
        });
        register(PacketTypes.SERVER_STATUS, (factory, left) -> {
            boolean b = left.readBoolean();
            return factory.createServerUpdated(b);
        });
    }

     <T extends WorldPacket> void register(PacketTypes type, ResolveStrategy<T> strategy){
        strategies.putIfAbsent(type.index(), strategy);
    }

    public Future<? extends WorldPacket> resolve(byte[] bytes) throws CorruptedPacketException {
        return resolve(Unpooled.buffer().writeBytes(bytes));
    }

    public Future<? extends WorldPacket> resolve(ByteBuf buf) throws CorruptedPacketException {
        return service.submit(()->{
            String name;

            try {
                name = readString(buf);
            }catch (Exception e){
                e.printStackTrace();
                throw new CorruptedPacketException("Header is corrupted! ");
            }

            if (!name.equals(BasicPacket.NAME))
                throw new CorruptedPacketException("Header is corrupted! : " + name);

            String from = readString(buf);
            String to = readString( buf);

            if(!database.getCurrentServer().getServerName().equals(to))
                throw new CorruptedPacketException("Target server is not here. : "+to);

            byte packetID = buf.readByte();
            PacketFactory factory = PacketFactory.newPacket(database.getServer(from), database.getServer(to));

            return Optional.ofNullable(strategies.get(packetID)).orElseThrow(()-> new CorruptedPacketException("Cannot find packet resolve strategy ID : "+packetID)).get(factory, buf);
        });
    }

    static String readString(ByteBuf buf){
        int i = buf.readInt();
        return String.valueOf(buf.readCharSequence(i, StandardCharsets.UTF_8));
    }

}

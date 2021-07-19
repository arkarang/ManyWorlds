package com.minepalm.manyworlds.core.netty;

import com.minepalm.hellobungee.netty.messages.PacketMessage;
import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.netty.WorldPacket;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

public abstract class BasicPacket extends PacketMessage implements WorldPacket{
    
    static final String NAME = "ManyWorlds";
    
    @Getter
    final ServerView from, to;

    BasicPacket(ServerView from, ServerView to){
        this.from = from;
        this.to = to;
        ByteBuf contexts = super.get();
        contexts.writeInt(NAME.length()).writeCharSequence(NAME, StandardCharsets.UTF_8);
        contexts.writeInt(from.getServerName().length()).writeCharSequence(from.getServerName(), StandardCharsets.UTF_8);
        contexts.writeInt(to.getServerName().length()).writeCharSequence(to.getServerName(), StandardCharsets.UTF_8);
        contexts.writeByte(getPacketID());

    }
    
}

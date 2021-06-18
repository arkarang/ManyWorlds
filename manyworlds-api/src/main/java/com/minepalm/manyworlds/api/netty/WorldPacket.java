package com.minepalm.manyworlds.api.netty;

import com.minepalm.hellobungee.api.HelloMessage;
import com.minepalm.manyworlds.api.ServerView;
import io.netty.buffer.ByteBuf;

public interface WorldPacket extends HelloMessage {

    ServerView getFrom();

    ServerView getTo();

    byte getPacketID();
}

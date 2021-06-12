package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.netty.WorldPacket;
import io.netty.buffer.ByteBuf;

public interface ResolveStrategy<T extends WorldPacket> {

    T get(PacketFactory factory, ByteBuf left);
}

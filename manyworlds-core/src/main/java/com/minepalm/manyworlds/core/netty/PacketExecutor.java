package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.netty.WorldPacket;

public interface PacketExecutor<T extends WorldPacket> {

    void execute(T packet);

}

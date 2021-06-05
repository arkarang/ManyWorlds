package com.minepalm.manyworlds.api;

import com.minepalm.manyworlds.api.netty.WorldPacket;

public interface ManyWorldCore {

    GlobalDatabase getGlobalDatabase();

    WorldTransaction send(ServerSnapshot snapshot, WorldPacket packet);

    long getLaunchedTime();
}

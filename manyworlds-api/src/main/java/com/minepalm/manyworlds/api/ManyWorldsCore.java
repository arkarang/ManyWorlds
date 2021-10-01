package com.minepalm.manyworlds.api;

import com.minepalm.manyworlds.api.netty.Controller;

public interface ManyWorldsCore {

    Controller getController();

    GlobalDatabase getGlobalDatabase();

    long getLaunchedTime();

    void shutdown();
}

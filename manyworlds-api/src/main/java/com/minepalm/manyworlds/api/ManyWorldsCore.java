package com.minepalm.manyworlds.api;

import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.bukkit.WorldType;
import com.minepalm.manyworlds.api.netty.Controller;
import com.minepalm.manyworlds.api.netty.WorldPacket;

public interface ManyWorldsCore {


    Controller getController();

    GlobalDatabase getGlobalDatabase();

    WorldInfo newWorldInfo(WorldType type, String worldName);

    WorldInfo newWorldInfo(WorldType type, String sampleName, String worldName);

    long getLaunchedTime();
}

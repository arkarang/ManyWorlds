package com.minepalm.manyworlds.core;

import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.ManyWorldsCore;
import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.bukkit.WorldType;
import com.minepalm.manyworlds.api.netty.Controller;
import com.minepalm.manyworlds.api.netty.WorldPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ManyWorlds implements ManyWorldsCore{

    @Getter
    final String name;

    @Getter
    final GlobalDatabase globalDatabase;

    @Getter
    final Controller controller;

    @Getter
    final long launchedTime = System.currentTimeMillis();

    @Override
    public WorldInfo newWorldInfo(WorldType type, String worldName) {
        return new ManyWorldInfo(type, null, worldName, System.currentTimeMillis());
    }

    @Override
    public WorldInfo newWorldInfo(WorldType type, String sampleName, String worldName) {
        return new ManyWorldInfo(type, sampleName, worldName, System.currentTimeMillis());
    }

}

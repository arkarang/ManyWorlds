package com.minepalm.manyworlds.core;

import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.ManyWorldsCore;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.bukkit.WorldType;
import com.minepalm.manyworlds.api.netty.Controller;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ManyWorlds implements ManyWorldsCore{

    final String name;

    final GlobalDatabase globalDatabase;

    final Controller controller;

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

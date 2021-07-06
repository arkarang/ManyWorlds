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
public abstract class AbstractManyWorlds implements ManyWorldsCore{

    final String name;

    final GlobalDatabase globalDatabase;

    final Controller controller;

    final long launchedTime = System.currentTimeMillis();

}

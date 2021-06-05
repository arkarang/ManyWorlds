package com.minepalm.manyworlds.bukkit.prototype;

import com.grinderwolf.swm.api.world.SlimeWorld;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
import java.util.concurrent.Future;

@RequiredArgsConstructor
public class InstantWorld {
    final String worldType;
    final UUID owner;
    Future<SlimeWorld> world;
}

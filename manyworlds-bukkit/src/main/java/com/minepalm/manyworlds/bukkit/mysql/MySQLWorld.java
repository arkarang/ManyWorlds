package com.minepalm.manyworlds.bukkit.mysql;

import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.minepalm.manyworlds.api.bukkit.PreparedWorld;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.bukkit.BukkitWorldInfo;
import com.minepalm.manyworlds.core.JsonWorldMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MySQLWorld implements PreparedWorld {

    final WorldInfo worldInfo;
    final byte[] worldBytes;
    final JsonWorldMetadata metadata;
    final SlimePropertyMap properties;

}

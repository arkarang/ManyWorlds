package com.minepalm.manyworlds.api.bukkit;

import com.minepalm.manyworlds.api.entity.PreparedWorld;

import java.io.IOException;

public interface WorldFactory {

    WorldEntity deserialize(PreparedWorld preparedWorld) throws IOException;

    PreparedWorld serialize(WorldEntity world) throws IOException;

}

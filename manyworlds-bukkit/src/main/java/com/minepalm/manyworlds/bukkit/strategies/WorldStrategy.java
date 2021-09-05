package com.minepalm.manyworlds.bukkit.strategies;

import com.minepalm.manyworlds.api.util.WorldInputStream;
import com.minepalm.manyworlds.api.util.WorldOutputStream;

import java.io.IOException;

public interface WorldStrategy {

    WorldBuffer serialize(WorldOutputStream stream, WorldBuffer world) throws IOException;

    WorldBuffer deserialize(WorldInputStream stream, WorldBuffer world) throws IOException;
}

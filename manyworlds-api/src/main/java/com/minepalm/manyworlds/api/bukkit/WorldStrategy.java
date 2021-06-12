package com.minepalm.manyworlds.api.bukkit;

import com.minepalm.manyworlds.api.util.WorldBuffer;
import com.minepalm.manyworlds.api.util.WorldInputStream;
import com.minepalm.manyworlds.api.util.WorldOutputStream;

import java.io.IOException;

public interface WorldStrategy {

    WorldBuffer serialize(WorldOutputStream stream, WorldBuffer world) throws IOException;

    WorldBuffer deserialize(WorldInputStream stream, WorldBuffer world) throws IOException;
}

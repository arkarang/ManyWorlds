package com.minepalm.manyworlds.api.bukkit;

import com.minepalm.manyworlds.api.util.WorldInputStream;
import com.minepalm.manyworlds.api.util.WorldOutputStream;

public interface WorldPipeline {

    WorldPipeline addFirst(String name, WorldStrategy strategy);

    WorldPipeline addLast(String name, WorldStrategy strategy);

    WorldInputStream serialize(WorldInputStream stream);

    WorldOutputStream deserialize(WorldOutputStream stream);
}

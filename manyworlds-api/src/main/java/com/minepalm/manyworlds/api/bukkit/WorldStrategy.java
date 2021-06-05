package com.minepalm.manyworlds.api.bukkit;

import com.minepalm.manyworlds.api.util.WorldInputStream;
import com.minepalm.manyworlds.api.util.WorldOutputStream;

public interface WorldStrategy {

    WorldInputStream serialize(WorldInputStream stream);

    WorldOutputStream deserialize(WorldOutputStream stream);
}

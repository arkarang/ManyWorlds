package com.minepalm.manyworlds.api.bukkit;

import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.api.WorldProperties;

public interface WorldEntity {

    WorldInform getWorldInform();

    WorldProperties getProperties();

}

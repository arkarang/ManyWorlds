package com.minepalm.manyworlds.api.bukkit;

import com.minepalm.manyworlds.api.WorldProperties;
import com.minepalm.manyworlds.api.entity.WorldInform;

public interface WorldEntity {

    WorldInform getWorldInform();

    WorldProperties getWorldProperties();

}

package com.minepalm.manyworlds.api.bukkit;

import com.minepalm.manyworlds.api.WorldProperties;

public interface WorldMetadata {

    <T> T getData(String name, Class<T> clazz);

    WorldProperties getProperties();
}

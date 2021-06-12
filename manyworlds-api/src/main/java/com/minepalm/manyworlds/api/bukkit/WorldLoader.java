package com.minepalm.manyworlds.api.bukkit;

import com.grinderwolf.swm.api.loaders.SlimeLoader;

import java.io.IOException;

public interface WorldLoader extends SlimeLoader {

    ManyWorld deserialize(PreparedWorld preparedWorld) throws IOException;

    PreparedWorld serialize(ManyWorld world) throws IOException;

}

package com.minepalm.manyworlds.api.bukkit;

public interface WorldLoader {

    ManyWorld deserialize(PreparedWorld preparedWorld);

    PreparedWorld serialize(ManyWorld world);

    WorldPipeline getPipeline(LoadPhase phase);
}

package com.minepalm.manyworlds.api.bukkit;

//todo: 객체 상호작용 명료하게.
public interface WorldLoader {

    ManyWorld deserialize(PreparedWorld preparedWorld);

    PreparedWorld serialize(ManyWorld world);

    WorldPipeline getPipeline(LoadPhase phase);
}

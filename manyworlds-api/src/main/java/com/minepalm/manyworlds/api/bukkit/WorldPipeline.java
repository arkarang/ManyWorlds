package com.minepalm.manyworlds.api.bukkit;

public interface WorldPipeline {

    WorldPipeline addFirst(String name, WorldStrategy strategy);

    WorldPipeline addLast(String name, WorldStrategy strategy);

}

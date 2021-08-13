package com.minepalm.manyworlds.bukkit;

import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import org.bukkit.generator.ChunkGenerator;

import java.util.HashMap;

public class ChunkGenRegistry {

    private final HashMap<String, ChunkGenerator> generators = new HashMap<>();


    public ChunkGenerator get(WorldInfo info){
        return generators.get(info.getGenerator());
    }

    public ChunkGenerator get(String key){
        return generators.get(key);
    }

    public void add(String key, ChunkGenerator gen){
        if(!generators.containsKey(key)){
            generators.put(key, gen);
        }else
            throw new IllegalArgumentException("the key "+key+" already exists");
    }
}

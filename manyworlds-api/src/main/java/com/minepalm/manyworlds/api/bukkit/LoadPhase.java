package com.minepalm.manyworlds.api.bukkit;

import java.util.Arrays;

public enum LoadPhase {
    NONE(-1),
    HEADER(0),
    CHUNK(1),
    TILE_ENTITY(2),
    ENTITY(3),
    TAG(4),
    MAP(5),
    EXTRA_DATA(7),
    MANYWORLD_METADATA(8),
    END(6),
    FAILED(99);

    final int number;

    LoadPhase(int order){
        this.number = order;
    }

    public int number(){
        return number;
    }

    public static LoadPhase getPhase(int i){
        return Arrays.stream(values()).filter(ph->ph.number == i).findFirst().orElse(NONE);
    }
}

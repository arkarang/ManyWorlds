package com.minepalm.manyworlds.api.bukkit;

import java.util.Arrays;

public enum LoadPhase {
    NONE(-1),
    HEADER(0),
    CHUNK(1),
    ENTITY(2),
    LIGHT(3),
    MAP(4),
    TAG(5),
    EXTRA_DATA(6),
    MANYWORLD_METADATA(7),
    FAILED(99);

    final int order;

    LoadPhase(int order){
        this.order = order;
    }

    public static LoadPhase getPhase(int i){
        return Arrays.stream(values()).filter(ph->ph.order == i).findFirst().orElse(NONE);
    }
}

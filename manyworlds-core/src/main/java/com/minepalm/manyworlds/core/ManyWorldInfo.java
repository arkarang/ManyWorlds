package com.minepalm.manyworlds.core;

import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.bukkit.WorldType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(exclude = "lastUpdated")
@RequiredArgsConstructor
public class ManyWorldInfo implements WorldInfo {

    @Getter
    @NonNull
    final WorldType worldType;

    @Getter
    private final String sampleName;

    @Getter
    @NonNull
    private final String worldName;

    @Getter
    final long lastUpdated;

    public ManyWorldInfo(String typeName, long time){
        this(WorldTokens.SAMPLE, typeName, typeName, time);
    }

    public ManyWorldInfo(WorldType type, String worldName){
        this(type, "", worldName, System.currentTimeMillis());
    }

    public ManyWorldInfo(WorldType type, String sampleName, String worldName){
        this(type, sampleName, worldName, System.currentTimeMillis());
    }

    @Override
    public WorldInfo clone() {
        return new ManyWorldInfo(worldType, sampleName, worldName, lastUpdated);
    }


    @Override
    public boolean isLocked() {
        return false;
    }

}

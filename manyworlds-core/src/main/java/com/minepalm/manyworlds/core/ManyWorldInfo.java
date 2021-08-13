package com.minepalm.manyworlds.core;

import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.bukkit.WorldType;
import lombok.*;

@EqualsAndHashCode(exclude = {"lastUpdated", "generator"})
@AllArgsConstructor
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

    @Getter
    @Setter
    String generator = "null";

    public ManyWorldInfo(String typeName, long time){
        this(WorldTokens.SAMPLE, typeName, typeName, time);
    }

    public ManyWorldInfo(WorldType type, String worldName){
        this(type, type.getName(), worldName, System.currentTimeMillis());
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

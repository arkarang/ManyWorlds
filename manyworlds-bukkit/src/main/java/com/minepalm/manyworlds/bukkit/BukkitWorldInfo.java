package com.minepalm.manyworlds.bukkit;

import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.bukkit.WorldMetadata;
import com.minepalm.manyworlds.api.bukkit.WorldType;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public class BukkitWorldInfo implements WorldInfo {

    @Getter
    @NonNull
    final WorldType worldType;

    @NonNull
    private final String typeName;

    private final UUID userUID;

    @Getter
    @NonNull
    final JsonSlimeProperties propertyMap;

    @Setter
    WorldMetadata metadata;

    public BukkitWorldInfo(String typeName, JsonSlimeProperties props){
        this(WorldType.SAMPLE, typeName, null, props);
    }

    public BukkitWorldInfo(String typeName, UUID uuid, JsonSlimeProperties props){
        this(WorldType.USER, typeName, uuid, props);
    }

    @Override
    public String getWorldName() {
        switch (worldType){
            case USER:
                return typeName + "_" + userUID.toString();
            case SAMPLE:
            default:
                return typeName;
        }
    }

    @Override
    public WorldInfo clone() {
        //todo: propsMap 클론
        return new BukkitWorldInfo(worldType, typeName, userUID, propertyMap);
    }
}
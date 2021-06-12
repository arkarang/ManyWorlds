package com.minepalm.manyworlds.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.minepalm.manyworlds.api.bukkit.WorldMetadata;
import com.minepalm.manyworlds.api.bukkit.WorldProperties;

public class JsonWorldMetadata implements WorldMetadata {

    @JsonProperty
    WorldProperties properties;

    public static JsonWorldMetadata fromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, JsonWorldMetadata.class);
    }

    @Override
    public SlimePropertyMap getProperties() {
        return properties.asSlime();
    }
}

package com.minepalm.manyworlds.bukkit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

//todo: Test Module 작성
@Getter
@Setter
public class ManyProperties {

    @JsonProperty("SPAWN_X")
    int spawn_x = 0;
    @JsonProperty("SPAWN_Y")
    int spawn_y = 64;
    @JsonProperty("SPAWN_Z")
    int spawn_z = 0;
    @JsonProperty("ALLOW_ANIMALS")
    boolean allow_animals = false;
    @JsonProperty("ALLOW_MONSTERS")
    boolean allow_monsters = false;
    @JsonProperty("WORLD_TYPE")
    String world_type = "DEFAULT";
    @JsonProperty("ENVIRONMENT")
    String environment = "NORMAL";
    @JsonProperty("DIFFICULTY")
    String difficulty = "normal";

    public SlimePropertyMap toSlime(){
        SlimePropertyMap map = new SlimePropertyMap();
        map.setInt(SlimeProperties.SPAWN_X, spawn_x);
        map.setInt(SlimeProperties.SPAWN_Y, spawn_y);
        map.setInt(SlimeProperties.SPAWN_Z, spawn_z);
        map.setBoolean(SlimeProperties.ALLOW_MONSTERS, allow_monsters);
        map.setBoolean(SlimeProperties.ALLOW_ANIMALS, allow_animals);
        map.setString(SlimeProperties.WORLD_TYPE, world_type);
        map.setString(SlimeProperties.ENVIRONMENT, environment);
        map.setString(SlimeProperties.DIFFICULTY, difficulty);
        return map;
    }

    public static ManyProperties fromString(String json){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, ManyProperties.class);
        }catch (JsonProcessingException ignored){

        }

        return new ManyProperties();
    }
}
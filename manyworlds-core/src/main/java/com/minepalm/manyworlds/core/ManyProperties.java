package com.minepalm.manyworlds.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.minepalm.manyworlds.api.WorldProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManyProperties implements WorldProperties {

    @JsonProperty("SPAWN_X")
    int spawnX = 0;

    @JsonProperty("SPAWN_Y")
    int spawnY = 64;

    @JsonProperty("SPAWN_Z")
    int spawnZ = 0;

    @JsonProperty("ALLOW_ANIMALS")
    boolean allowAnimals = false;

    @JsonProperty("ALLOW_MONSTERS")
    boolean allowMonsters = false;

    @JsonProperty("WORLD_TYPE")
    String worldType = "DEFAULT";

    @JsonProperty("ENVIRONMENT")
    String environment = "NORMAL";

    @JsonProperty("DIFFICULTY")
    String difficulty = "normal";

    public ManyProperties(){

    }

    public ManyProperties(SlimePropertyMap map){
        spawnX = map.getValue(SlimeProperties.SPAWN_X);
        spawnY = map.getValue(SlimeProperties.SPAWN_Y);
        spawnZ = map.getValue(SlimeProperties.SPAWN_Z);
        allowMonsters = map.getValue(SlimeProperties.ALLOW_MONSTERS);
        allowAnimals = map.getValue(SlimeProperties.ALLOW_ANIMALS);
        worldType = map.getValue(SlimeProperties.WORLD_TYPE);
        environment = map.getValue(SlimeProperties.ENVIRONMENT);
        difficulty = map.getValue(SlimeProperties.DIFFICULTY);
    }

    public SlimePropertyMap asSlime() {
        SlimePropertyMap map = new SlimePropertyMap();
        map.setValue(SlimeProperties.SPAWN_X, spawnX);
        map.setValue(SlimeProperties.SPAWN_Y, spawnY);
        map.setValue(SlimeProperties.SPAWN_Z, spawnZ);
        map.setValue(SlimeProperties.ALLOW_MONSTERS, allowMonsters);
        map.setValue(SlimeProperties.ALLOW_ANIMALS, allowAnimals);
        map.setValue(SlimeProperties.WORLD_TYPE, worldType);
        map.setValue(SlimeProperties.ENVIRONMENT, environment);
        map.setValue(SlimeProperties.DIFFICULTY, difficulty);
        return map;
    }
    @Override
    public ManyProperties clone(){
        return new ManyProperties(this.asSlime());
    }

    public static ManyProperties fromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, ManyProperties.class);
    }
    @Override
    public String toString(){
        return toJson();
    }

    public String toJson(){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return "";
    }

}

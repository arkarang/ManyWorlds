package com.minepalm.manyworlds.bukkit;

import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;

public class JsonSlimeProperties extends SlimePropertyMap {

    JsonSlimeProperties(String json){
        super();
        //todo String -> Json (Mapping)
    }


    public static JsonSlimeProperties fromString(String json){
        return new JsonSlimeProperties(json);
    }
}

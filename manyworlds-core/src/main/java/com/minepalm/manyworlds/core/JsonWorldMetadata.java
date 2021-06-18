package com.minepalm.manyworlds.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minepalm.manyworlds.api.bukkit.WorldMetadata;
import lombok.Getter;
import lombok.Setter;

public class JsonWorldMetadata implements WorldMetadata {

    @Getter
    @Setter
    @JsonProperty
    ManyProperties properties = new ManyProperties();

    public static JsonWorldMetadata fromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, JsonWorldMetadata.class);
    }

    @Override
    public <T> T getData(String name, Class<T> clazz) {
        throw new UnsupportedOperationException("not implemented");
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

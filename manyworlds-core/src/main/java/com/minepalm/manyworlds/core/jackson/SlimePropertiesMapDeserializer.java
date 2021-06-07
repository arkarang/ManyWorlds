package com.minepalm.manyworlds.core.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;

import java.io.IOException;

public class SlimePropertiesMapDeserializer extends StdDeserializer<SlimePropertyMap> {
    protected SlimePropertiesMapDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public SlimePropertyMap deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JsonProcessingException {

        return null;
    }
}

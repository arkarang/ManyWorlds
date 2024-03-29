package com.minepalm.manyworlds.core.jackson;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;

import java.io.IOException;

public class SlimePropertiesMapSerializer extends StdSerializer<SlimePropertyMap> {
    protected SlimePropertiesMapSerializer(Class<SlimePropertyMap> t) {
        super(t);
    }

    @Override
    public void serialize(SlimePropertyMap map, JsonGenerator g, SerializerProvider p) throws IOException {
        g.writeStartObject();
        g.writeFieldName("SPAWN_X");
        g.setCurrentValue(map.getInt(SlimeProperties.SPAWN_X));
        g.writeFieldName("SPAWN_Y");
        g.setCurrentValue(map.getInt(SlimeProperties.SPAWN_Y));
        g.writeFieldName("SPAWN_Z");
        g.setCurrentValue(map.getInt(SlimeProperties.SPAWN_Z));
        g.writeFieldName("ALLOW_ANIMALS");
        g.setCurrentValue(map.getBoolean(SlimeProperties.ALLOW_ANIMALS));
        g.writeFieldName("ALLOW_MONSTERS");
        g.setCurrentValue(map.getBoolean(SlimeProperties.ALLOW_MONSTERS));
        g.writeFieldName("WORLD_TYPE");
        g.setCurrentValue(map.getString(SlimeProperties.WORLD_TYPE));
        g.writeFieldName("DIFFICULTY");
        g.setCurrentValue(map.getString(SlimeProperties.DIFFICULTY));
        g.writeFieldName("ENVIRONMENT");
        g.setCurrentValue(map.getString(SlimeProperties.ENVIRONMENT));
        g.writeEndObject();

    }
}

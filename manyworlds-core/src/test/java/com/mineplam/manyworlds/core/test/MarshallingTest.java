package com.mineplam.manyworlds.core.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minepalm.manyworlds.api.bukkit.WorldMetadata;
import com.minepalm.manyworlds.core.JsonWorldMetadata;
import com.minepalm.manyworlds.core.ManyProperties;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MarshallingTest {

    @Test
    public void test_01_propertyTest() throws JsonProcessingException {
        ManyProperties props = new ManyProperties();
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(props).toString();
        System.out.println(json);

        ManyProperties props2 = mapper.readValue(json, ManyProperties.class);
    }

    @Test
    public void test_02_metadataTest(){
        WorldMetadata metadata = new JsonWorldMetadata();
        System.out.println(metadata.toString());
    }
}

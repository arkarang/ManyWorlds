package com.minepalm.manyworlds.bukkit.test;

import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.utils.SlimeFormat;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.api.util.WorldBuffer;
import com.minepalm.manyworlds.bukkit.CraftManyWorld;
import com.minepalm.manyworlds.bukkit.ManyWorldLoader;
import com.minepalm.manyworlds.bukkit.PreWorldData;
import com.minepalm.manyworlds.bukkit.mysql.MySQLWorldDatabase;
import com.minepalm.manyworlds.bukkit.strategies.v1_12.v1_12WorldUtils;
import com.minepalm.manyworlds.core.JsonWorldMetadata;
import com.minepalm.manyworlds.core.ManyWorldInfo;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Properties;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WorldDatabaseTest {

    static WorldDatabase db;
    static WorldInfo info;

    @Test
    public void test_00_createTest(){
        Properties props = new Properties();
        props.setProperty("address", "localhost");
        props.setProperty("port", "3306");
        props.setProperty("database", "test");
        props.setProperty("username", "root");
        props.setProperty("password", "M!nso0*o");
        db = new MySQLWorldDatabase(WorldType.SAMPLE, "manyworlds_world_data", props);
    }

    @Test
    public void test_01_serializationTest() throws IOException {
        RandomAccessFile file = new RandomAccessFile(new File("src/test/resources/test.slime"), "rw");
        byte[] bytes = new byte[(int)file.length()];
        file.readFully(bytes);

        WorldLoader loader = new ManyWorldLoader(null);

        PreparedWorld pw = this.getPreparedWorld();

        for(int i = 0 ; i < 2 ; i++) {
            long now = System.currentTimeMillis();
            ManyWorld world = loader.deserialize(pw);
            Assert.assertNotNull(world.getWorldInfo());
            Assert.assertNotNull(world.getMetadata());
            System.out.println("DESERIALIZATION TIME : " + (System.currentTimeMillis() - now) + "ms");

            now = System.currentTimeMillis();
            pw = loader.serialize(world);
            System.out.println("SERIALIZATION TIME : " + (System.currentTimeMillis() - now) + "ms");
        }

        //Assert.assertEquals(pw.getWorldBytes(), bytes);
    }

    private PreparedWorld getPreparedWorld() throws IOException {
        RandomAccessFile file = new RandomAccessFile(new File("src/test/resources/test.slime"), "rw");
        byte[] bytes = new byte[(int)file.length()];
        file.readFully(bytes);
        return new PreWorldData(new ManyWorldInfo("test", 0L), bytes, new JsonWorldMetadata());
    }

    @Test
    public void test_02_saveTest() throws IOException {
        PreparedWorld pWorld = getPreparedWorld();
        db.saveWorld(pWorld);
        info = new ManyWorldInfo(WorldType.SAMPLE, "test", "test", 0L);

        Assert.assertNotNull(info);
        Assert.assertEquals(info.getWorldName(), pWorld.getWorldInfo().getWorldName());
    }

    @Test
    public void test_03_loadTest(){
        info = new ManyWorldInfo(WorldType.SAMPLE, "test", "test", 0L);
        PreparedWorld world = db.prepareWorld(info);
        Assert.assertNotNull(world);
    }

    @Test
    public void asdf() throws IOException, CorruptedWorldException, NewerFormatException {
        RandomAccessFile file = new RandomAccessFile(new File("src/test/resources/test.slime"), "rw");
        byte[] bytes = new byte[(int)file.length()];
        file.readFully(bytes);

        WorldLoader loader = new ManyWorldLoader(null);

        PreparedWorld pw = this.getPreparedWorld();

        long now = System.currentTimeMillis();
        ManyWorld world = loader.deserialize(pw);
        //Assert.assertNotNull(world.getWorldInfo());
        //Assert.assertNotNull(world.getMetadata());
        System.out.println("DESERIALIZATION TIME : " + (System.currentTimeMillis() - now) + "ms");

        now = System.currentTimeMillis();
        pw = loader.serialize(world);
        System.out.println("SERIALIZATION TIME : " + (System.currentTimeMillis() - now) + "ms");

        now = System.currentTimeMillis();
        WorldBuffer buffer = new WorldBuffer();

        buffer.setName(world.getWorldInfo().getWorldName());
        buffer.setVersion(SlimeFormat.SLIME_VERSION);
        buffer.setPropertyMap(world.getMetadata().getProperties().asSlime());

        v1_12WorldUtils.serialize((CraftManyWorld) world);
        System.out.println("DESERIALIZATION TIME : " + (System.currentTimeMillis() - now) + "ms");
        now = System.currentTimeMillis();
        v1_12WorldUtils.deserializeWorld(loader, "asdf", pw.getWorldBytes(), pw.getMetadata().getProperties().asSlime(), false);
        System.out.println("SERIALIZATION TIME : " + (System.currentTimeMillis() - now) + "ms");

    }

}

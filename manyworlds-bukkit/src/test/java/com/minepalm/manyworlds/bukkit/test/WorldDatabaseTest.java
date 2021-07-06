package com.minepalm.manyworlds.bukkit.test;

import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.utils.SlimeFormat;
import com.grinderwolf.swm.nms.CraftSlimeWorld;
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
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

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
        db = new MySQLWorldDatabase(WorldType.SAMPLE, "manyworlds_world_data", props, Executors.newSingleThreadExecutor());
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
    public void test_03_loadTest() throws ExecutionException, InterruptedException {
        info = new ManyWorldInfo(WorldType.SAMPLE, "test", "test", 0L);
        PreparedWorld world = db.prepareWorld(info).get();
        Assert.assertNotNull(world);
    }

    /*
     * Performance Test
     */
    /*
    @Test
    public void asdf() throws IOException, CorruptedWorldException, NewerFormatException {
        RandomAccessFile file = new RandomAccessFile(new File("src/test/resources/test.slime"), "rw");
        byte[] bytes = new byte[(int)file.length()];
        file.readFully(bytes);

        WorldLoader loader = new ManyWorldLoader(null);

        PreparedWorld pw = this.getPreparedWorld();

        long now = System.currentTimeMillis();

        CraftSlimeWorld world = v1_12WorldUtils.deserializeWorld(loader, "asdf", bytes, pw.getMetadata().getProperties().asSlime(), false);
        System.out.println("DESERIALIZATION TIME : " + (System.currentTimeMillis() - now) + "ms");

        now = System.currentTimeMillis();
        v1_12WorldUtils.serialize(world);
        System.out.println("SERIALIZATION TIME : " + (System.currentTimeMillis() - now) + "ms");

    }

    @Test
    public void asdf2() throws IOException, CorruptedWorldException, NewerFormatException{
        RandomAccessFile file = new RandomAccessFile(new File("src/test/resources/test.slime"), "rw");
        byte[] bytes = new byte[(int)file.length()];
        file.readFully(bytes);

        WorldLoader loader = new ManyWorldLoader(null);

        PreparedWorld pw = this.getPreparedWorld();

        long now = System.currentTimeMillis();

        System.out.println("---------------------------------------------------------------------");
        ManyWorld world = loader.deserialize(pw);
        //Assert.assertNotNull(world.getWorldInfo());
        //Assert.assertNotNull(world.getMetadata());
        System.out.println("DESERIALIZATION TIME : " + (System.currentTimeMillis() - now) + "ms");

        now = System.currentTimeMillis();
        pw = loader.serialize(world);
        System.out.println("SERIALIZATION TIME : " + (System.currentTimeMillis() - now) + "ms");
    }

     */


}

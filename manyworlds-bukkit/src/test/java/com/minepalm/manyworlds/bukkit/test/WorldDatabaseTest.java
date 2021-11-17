package com.minepalm.manyworlds.bukkit.test;

import com.minepalm.manyworlds.api.entity.PreparedWorld;
import com.minepalm.manyworlds.api.bukkit.WorldDatabase;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.bukkit.mysql.MySQLWorldDatabase;
import com.minepalm.manyworlds.core.*;
import com.minepalm.manyworlds.core.database.MySQLDatabase;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WorldDatabaseTest {

    static WorldDatabase db;
    static WorldInform info;

    @Test
    public void test_00_createTest(){
        Properties props = new Properties();
        props.setProperty("address", "localhost");
        props.setProperty("port", "3306");
        props.setProperty("database", "test");
        props.setProperty("username", "root");
        props.setProperty("password", "test");
        db = new MySQLWorldDatabase(WorldTokens.TYPE, "manyworlds_samples", new MySQLDatabase(props, Executors.newSingleThreadExecutor()));
    }

    /*
    @Test
    public void test_01_serializationTest() throws IOException {
        RandomAccessFile file = new RandomAccessFile(new File("src/test/resources/test2.slime"), "rw");
        byte[] bytes = new byte[(int)file.length()];
        file.readFully(bytes);

        WorldLoadService loader = new AbstractWorldLoadService(null, null);

        PreparedWorld pw = this.getPreparedWorld();

        for(int i = 0 ; i < 2 ; i++) {
            long now = System.currentTimeMillis();
            WorldEntity world = loader.deserialize(pw);
            Assert.assertNotNull(world.getWorldInform());
            Assert.assertNotNull(world.getMetadata());
            System.out.println("DESERIALIZATION TIME : " + (System.currentTimeMillis() - now) + "ms");

            now = System.currentTimeMillis();
            pw = loader.serialize(world);
            System.out.println("SERIALIZATION TIME : " + (System.currentTimeMillis() - now) + "ms");
        }

        //Assert.assertEquals(pw.getWorldBytes(), bytes);
    }

     */

    private PreparedWorld getPreparedWorld() throws IOException {
        RandomAccessFile file = new RandomAccessFile(new File("src/test/resources/test.slime"), "rw");
        byte[] bytes = new byte[(int)file.length()];
        file.readFully(bytes);
        return new PreparedWorld(new WorldInform(WorldTokens.TYPE, "test"), bytes, new ManyProperties());
    }

    @Test
    public void test_02_saveTest() throws IOException {
        PreparedWorld pWorld = getPreparedWorld();
        db.saveWorld(pWorld);
        info = new WorldInform(WorldToken.get("SAMPLE"), "test");

        Assert.assertNotNull(info);
        Assert.assertEquals(info.getName(), pWorld.getWorldInform().getName());
    }

    @Test
    public void test_03_loadTest() throws ExecutionException, InterruptedException {
        info = new WorldInform(WorldToken.get("SAMPLE"), "test");
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

        WorldLoadService loader = new AbstractWorldLoadService(null);

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

        WorldLoadService loader = new AbstractWorldLoadService(null);

        PreparedWorld pw = this.getPreparedWorld();

        long now = System.currentTimeMillis();

        System.out.println("---------------------------------------------------------------------");
        WorldEntity world = loader.deserialize(pw);
        //Assert.assertNotNull(world.getWorldInform());
        //Assert.assertNotNull(world.getMetadata());
        System.out.println("DESERIALIZATION TIME : " + (System.currentTimeMillis() - now) + "ms");

        now = System.currentTimeMillis();
        pw = loader.serialize(world);
        System.out.println("SERIALIZATION TIME : " + (System.currentTimeMillis() - now) + "ms");
    }

     */


}

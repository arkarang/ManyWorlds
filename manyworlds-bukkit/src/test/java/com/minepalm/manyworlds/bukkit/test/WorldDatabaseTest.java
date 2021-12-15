package com.minepalm.manyworlds.bukkit.test;

import com.minepalm.manyworlds.api.WorldLoadService;
import com.minepalm.manyworlds.api.WorldProperties;
import com.minepalm.manyworlds.api.bukkit.WorldDatabase;
import com.minepalm.manyworlds.api.bukkit.WorldEntity;
import com.minepalm.manyworlds.api.bukkit.WorldFactory;
import com.minepalm.manyworlds.api.entity.PreparedWorld;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.bukkit.AbstractWorldFactory;
import com.minepalm.manyworlds.bukkit.AbstractWorldLoadService;
import com.minepalm.manyworlds.bukkit.mysql.MySQLWorldDatabase;
import com.minepalm.manyworlds.bukkit.strategies.WorldBuffer;
import com.minepalm.manyworlds.core.ManyProperties;
import com.minepalm.manyworlds.core.WorldToken;
import com.minepalm.manyworlds.core.WorldTokens;
import com.minepalm.manyworlds.core.database.MySQLDatabase;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WorldDatabaseTest {

    static WorldDatabase db;
    static WorldInform info;

    @Before
    public void test_00_createTest(){
        Properties props = new Properties();
        props.setProperty("address", "localhost");
        props.setProperty("port", "3306");
        props.setProperty("database", "test");
        props.setProperty("username", "root");
        props.setProperty("password", "test");
        db = new MySQLWorldDatabase(WorldTokens.TYPE, "manyworlds_samples", new MySQLDatabase(props, Executors.newSingleThreadExecutor()), Logger.getLogger("global"));
    }


    @Test
    public void test_01_serializationTest() throws IOException {
        RandomAccessFile file = new RandomAccessFile(new File("src/test/resources/test2.slime"), "rw");
        byte[] bytes = new byte[(int)file.length()];
        file.readFully(bytes);

        WorldFactory factory = new AbstractWorldFactory() {
            @Override
            protected WorldEntity buildWorldEntity(WorldInform info, WorldProperties properties, WorldBuffer buffer) {
                return null;
            }

            @Override
            protected void setProperties(WorldBuffer buffer, WorldProperties properties) {
                buffer.setPropertyMap(((ManyProperties)properties).asSlime());
            }
        };

        PreparedWorld pw = this.getPreparedWorld();
        long now = System.currentTimeMillis();
        WorldEntity world = factory.deserialize(pw);
        System.out.println("DESERIALIZATION TIME : " + (System.currentTimeMillis() - now) + "ms");

        now = System.currentTimeMillis();
        //pw = factory.serialize(world);
        //System.out.println("SERIALIZATION TIME : " + (System.currentTimeMillis() - now) + "ms");


        //Assert.assertEquals(pw.getWorldBytes(), bytes);
    }


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
        info = new WorldInform(WorldTokens.TYPE, "test");

        Assert.assertNotNull(info);
        Assert.assertEquals(info.getName(), pWorld.getWorldInform().getName());
    }

    @Test
    public void test_03_loadTest() throws ExecutionException, InterruptedException, TimeoutException {
        info = new WorldInform(WorldTokens.TYPE, "test");
        PreparedWorld world = db.prepareWorld(info).get(5000, TimeUnit.MILLISECONDS);
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

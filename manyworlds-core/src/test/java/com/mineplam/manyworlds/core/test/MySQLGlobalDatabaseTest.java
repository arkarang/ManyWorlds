package com.mineplam.manyworlds.core.test;

import com.minepalm.manyworlds.api.entity.BungeeView;
import com.minepalm.manyworlds.core.database.global.MySQLWorldNetwork;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MySQLGlobalDatabaseTest {

    private static BungeeView view = new BungeeView("test", 0);
    private static MySQLWorldNetwork db;

    @BeforeClass
    public static void prepare(){
        Properties props = new Properties();
        props.setProperty("address", "localhost");
        props.setProperty("port", "3306");
        props.setProperty("database", "test");
        props.setProperty("username", "root");
        props.setProperty("password", "test");
        db = new MySQLWorldNetwork("proxy", view, "manyworlds_servers", "manyworlds_worlds", props, Executors.newSingleThreadExecutor(), Logger.getGlobal());
    }

   // @Test
    public void registerTest() throws ExecutionException, InterruptedException {
        db.registerServer();
        Assert.assertNotNull(db.getServer("test").get());
        db.unregister();
        Assert.assertNull(db.getServer("test").get());
    }

   // @Test
    public void worldLoadedTest() throws ExecutionException, InterruptedException {
        Assert.assertFalse(db.isWorldLoaded("asdf").get());
    }

    /*
    @Test
    public void test2() throws ExecutionException, InterruptedException {
        List<BukkitView> list = db.getServers().get();
        for (BukkitView view : list) {
            System.out.println("view: "+view.getServerName()+", "+view.getLoadedWorlds());
        }
    }
     */
}

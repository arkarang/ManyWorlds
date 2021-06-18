package com.mineplam.manyworlds.core.test;

import com.minepalm.manyworlds.core.database.global.MySQLGlobalDatabase;
import com.minepalm.manyworlds.core.server.BungeeServerView;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

public class MySQLGlobalDatabaseTest {

    private static BungeeServerView view = new BungeeServerView("test", 0);
    private static MySQLGlobalDatabase db;

    @BeforeClass
    public static void prepare(){
        Properties props = new Properties();
        props.setProperty("address", "localhost");
        props.setProperty("port", "3306");
        props.setProperty("database", "test");
        props.setProperty("username", "root");
        props.setProperty("password", "M!nso0*o");
        db = new MySQLGlobalDatabase("proxy", view, "manyworlds_servers", "manyworlds_worlds", props);
    }

    @Test
    public void registerTest(){
        db.register();
        Assert.assertNotNull(db.getServer("test"));
        db.unregister();
        Assert.assertNull(db.getServer("test"));
    }
}

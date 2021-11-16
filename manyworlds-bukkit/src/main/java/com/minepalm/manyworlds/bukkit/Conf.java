package com.minepalm.manyworlds.bukkit;

import com.minepalm.arkarangutils.bukkit.SimpleConfig;

import java.util.Properties;

public class Conf extends SimpleConfig {

    protected Conf(ManyWorldsBukkitBootstrap plugin) {
        super(plugin, "config.yml");
    }

    Properties getDatabaseProperties(){
        Properties props = new Properties();
        props.setProperty("address", config.getString("Database.address"));
        props.setProperty("port", config.getString("Database.port"));
        props.setProperty("database", config.getString("Database.database"));
        props.setProperty("username", config.getString("Database.username"));
        props.setProperty("password", config.getString("Database.password"));
        return props;
    }

    public String getServerTable(){
        return config.getString("Global.Servers");
    }

    public String getWorldsTable(){
        return config.getString("Global.Worlds");
    }

    public String getUserTableName(){
        return config.getString("WorldDatabase.USER");
    }

    public String getSampleTableName(){
        return config.getString("WorldDatabase.SAMPLE");
    }

    public String proxyName(){
        return config.getString("HelloBungee");
    }
}

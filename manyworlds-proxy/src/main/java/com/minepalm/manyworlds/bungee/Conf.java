package com.minepalm.manyworlds.bungee;

import com.minepalm.manyworlds.bungee.utils.BungeeConfig;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Properties;

public class Conf extends BungeeConfig {
    public Conf(Plugin plugin, String fileName, boolean loadResource) {
        super(plugin, fileName, loadResource);
    }


    String getName(){
        return config.getString("Name");
    }

    public String getServerTable(){
        return config.getString("Global.Servers");
    }

    public String getWorldsTable(){
        return config.getString("Global.Worlds");
    }

    Properties getProperties(){
        Properties props = new Properties();
        props.setProperty("address", config.getString("Database.address"));
        props.setProperty("port", config.getString("Database.port"));
        props.setProperty("database", config.getString("Database.database"));
        props.setProperty("username", config.getString("Database.username"));
        props.setProperty("password", config.getString("Database.password"));
        return props;
    }

}

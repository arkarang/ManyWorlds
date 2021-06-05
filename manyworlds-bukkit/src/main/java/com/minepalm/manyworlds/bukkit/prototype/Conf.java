package com.minepalm.manyworlds.bukkit.prototype;


import com.minepalm.arkarangutils.bukkit.SimpleConfig;

import java.util.Properties;

public class Conf extends SimpleConfig {
    protected Conf(String fileName) {
        super(ManyWorldPlugin.getInst(), fileName);
    }

    public Properties getTypeDBSetting(){
        Properties props = new Properties();
        props.setProperty("address", config.getString("TypeDatabase.address"));
        props.setProperty("port", config.getString("TypeDatabase.port"));
        props.setProperty("database", config.getString("TypeDatabase.database"));
        props.setProperty("username", config.getString("TypeDatabase.username"));
        props.setProperty("password", config.getString("TypeDatabase.password"));
        return props;
    }
    
    public Properties getUserDBSetting(){
        Properties props = new Properties();
        props.setProperty("address", config.getString("UserDatabase.address"));
        props.setProperty("port", config.getString("UserDatabase.port"));
        props.setProperty("database", config.getString("UserDatabase.database"));
        props.setProperty("username", config.getString("UserDatabase.username"));
        props.setProperty("password", config.getString("UserDatabase.password"));
        return props;
    }
}

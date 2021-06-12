package com.minepalm.manyworlds.bungee.utils;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BungeeConfig {

    protected Configuration config;

    public BungeeConfig(Plugin plugin, String fileName, boolean loadResource){
        File file = new File(plugin.getDataFolder(), fileName);
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            if(!file.exists()) {
                if(loadResource) {
                    Files.copy(plugin.getResourceAsStream(fileName), Paths.get(plugin.getDataFolder().getPath(), fileName));
                }else{
                    file.createNewFile();
                }
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

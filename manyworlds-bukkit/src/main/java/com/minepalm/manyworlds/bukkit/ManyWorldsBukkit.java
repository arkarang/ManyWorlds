package com.minepalm.manyworlds.bukkit;

import com.minepalm.manyworlds.api.bukkit.WorldDatabase;
import com.minepalm.manyworlds.api.bukkit.WorldManager;
import com.minepalm.manyworlds.api.bukkit.WorldStorage;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class ManyWorldsBukkit extends JavaPlugin implements WorldManager {

    @Getter
    static ManyWorldsBukkit inst;

    @Getter
    WorldDatabase worldDatabase;

    @Getter
    WorldStorage worldStorage;

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable(){

    }
}

package com.minepalm.manyworlds.bukkit.prototype;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldUnloadEvent;


public class Listener implements org.bukkit.event.Listener {

    @EventHandler
    public void onTest(WorldUnloadEvent event){
        Bukkit.getLogger().info("WORLD UNLOAD : "+event.getWorld().getName());

    }
}

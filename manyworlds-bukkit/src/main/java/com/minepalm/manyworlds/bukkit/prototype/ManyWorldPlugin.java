package com.minepalm.manyworlds.bukkit.prototype;

import co.aikar.commands.PaperCommandManager;
import com.grinderwolf.swm.plugin.SWMPlugin;
import com.grinderwolf.swm.plugin.loaders.mysql.MysqlLoader;
import com.minepalm.manyworlds.api.*;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.api.netty.WorldPacket;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class ManyWorldPlugin extends JavaPlugin{

    @Getter
    static SWMPlugin swm;

    @Getter
    static ManyWorldPlugin inst;
    @Getter
    static SWMUtils slimeUtils;
    @Getter
    static Conf conf;
    @Getter
    static MysqlLoader userLoader, typeLoader;

    @Override
    public void onEnable(){
        inst = this;
        conf = new Conf("config.yml");
        swm = (SWMPlugin)Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        slimeUtils = new SWMUtils(swm);
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new Commands());
        try {
            userLoader = slimeUtils.getLoader(conf.getUserDBSetting(), "user_worlds");
            typeLoader = slimeUtils.getLoader(conf.getTypeDBSetting(), "world_type");
        }catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
}

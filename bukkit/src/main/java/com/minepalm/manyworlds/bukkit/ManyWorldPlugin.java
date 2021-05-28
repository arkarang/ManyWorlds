package com.minepalm.manyworlds.bukkit;

import com.grinderwolf.swm.plugin.SWMPlugin;
import com.grinderwolf.swm.plugin.loaders.mysql.MysqlLoader;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class ManyWorldPlugin extends JavaPlugin {

    @Getter
    WorldStatistics asdf;

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
        try {
            userLoader = slimeUtils.getLoader(conf.getUserDBSetting());
            typeLoader = slimeUtils.getLoader(conf.getTypeDBSetting());
        }catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
}

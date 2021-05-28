package com.minepalm.manyworlds.bukkit;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.nms.CraftSlimeWorld;
import com.grinderwolf.swm.plugin.loaders.LoaderUtils;
import com.grinderwolf.swm.plugin.upgrade.WorldUpgrader;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@CommandAlias("월드")
public class Commands extends BaseCommand {

    @Subcommand("등록")
    public void register(Player player, String name, String worldName){
        World world = Bukkit.getWorld(worldName);
        if(world != null) {
            File folder = world.getWorldFolder();
            try {
                ManyWorldPlugin.slimeUtils.swm.importWorld(folder, name, ManyWorldPlugin.getTypeLoader());
                player.sendMessage("등록 성공!");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Subcommand("생성")
    public void createOwn(Player player, String name){
        create(player, name, player.getUniqueId());
    }

    private void create(Player sender, String name, UUID uuid){
        Bukkit.getScheduler().runTask(ManyWorldPlugin.getInst(), () -> {
            try {
                byte[] serializedWorld = ManyWorldPlugin.typeLoader.loadWorld(name, false);
                CraftSlimeWorld world;

                SlimePropertyMap propertyMap = null;

                world = LoaderUtils.deserializeWorld(ManyWorldPlugin.userLoader, name + "_" + uuid, serializedWorld, propertyMap, false);

                if (world.getVersion() > ManyWorldPlugin.swm.getNms().getWorldVersion()) {
                    WorldUpgrader.downgradeWorld(world);
                } else if (world.getVersion() < ManyWorldPlugin.swm.getNms().getWorldVersion()) {
                    WorldUpgrader.upgradeWorld(world);
                }

                Object nmsWorld = ManyWorldPlugin.swm.getNms().createNMSWorld(world);
                ManyWorldPlugin.swm.getNms().addWorldToServerList(nmsWorld);
                sender.sendMessage("성공!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Subcommand("유저생성")
    public void createOthers(Player player, String type, String username){
        Optional.ofNullable(Bukkit.getOfflinePlayer(username)).ifPresent(target-> create(player, type, target.getUniqueId()));
    }

}

package com.minepalm.manyworlds.bukkit.prototype;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
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

    @Subcommand("파일등록")
    public void register(Player player, String name, String worldName){
        World world = Bukkit.getWorld(worldName);
        if(world != null) {

            File folder = world.getWorldFolder();
            try {
                World bukkitWorld = Bukkit.getWorld(worldName);
                if (bukkitWorld != null) {
                    CraftSlimeWorld slimeWorld = (CraftSlimeWorld)ManyWorldPlugin.swm.getNms().getSlimeWorld(bukkitWorld);
                    if (slimeWorld != null) {
                        SlimeLoader slimeLoader = slimeWorld.getLoader();
                        slimeWorld.setLoader(ManyWorldPlugin.getTypeLoader());
                        byte[] serializedWorld = slimeWorld.serialize();
                        ManyWorldPlugin.getTypeLoader().saveWorld(name, serializedWorld, false);
                        player.sendMessage("등록 성공! "+serializedWorld.length+"bytes.");
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Subcommand("목록")
    public void list(Player player){
        player.sendMessage("월드목록");
        Bukkit.getWorlds().forEach(world->player.sendMessage(world.getName()));
    }

    @Subcommand("생성")
    public void createOwn(Player player, String name){
        create(player, name, player.getUniqueId());
    }

    @Subcommand("저장")
    public void saveOwn(Player player, String name){
        Bukkit.unloadWorld(name+"_"+player.getUniqueId(), true);
        player.sendMessage("저장!");
    }

    @Subcommand("로드")
    public void loadOwn(Player player, String name){
        try {
            String worldName = name + "_" + player.getUniqueId();
            byte[] serializedWorld = ManyWorldPlugin.userLoader.loadWorld(worldName, false);
            CraftSlimeWorld world;

            //todo PropertyMap 구현 - Json
            SlimePropertyMap propertyMap = new SlimePropertyMap();
                /*
                spawn: 0, 64, 0
                difficulty: peaceful
                allowMonsters: true
                allowAnimals: true
                pvp: true
                environment: NORMAL
                worldType: DEFAULT
                loadOnStartup: true
                readOnly: false
                *
                * */
            propertyMap.setInt(SlimeProperties.SPAWN_X, 0);
            propertyMap.setInt(SlimeProperties.SPAWN_Y, 64);
            propertyMap.setInt(SlimeProperties.SPAWN_Z, 0);
            propertyMap.setString(SlimeProperties.DIFFICULTY, "PEACEFUL");
            propertyMap.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
            propertyMap.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
            propertyMap.setBoolean(SlimeProperties.PVP, false);
            propertyMap.setString(SlimeProperties.ENVIRONMENT, "NORMAL");
            propertyMap.setString(SlimeProperties.WORLD_TYPE, "DEFAULT");

            world = LoaderUtils.deserializeWorld(ManyWorldPlugin.userLoader, worldName, serializedWorld, propertyMap, false);

            if (world.getVersion() > ManyWorldPlugin.swm.getNms().getWorldVersion()) {
                WorldUpgrader.downgradeWorld(world);
            } else if (world.getVersion() < ManyWorldPlugin.swm.getNms().getWorldVersion()) {
                WorldUpgrader.upgradeWorld(world);
            }

            Object nmsWorld = ManyWorldPlugin.swm.getNms().createNMSWorld(world);
            ManyWorldPlugin.swm.getNms().addWorldToServerList(nmsWorld);
            player.sendMessage("불러오기 성공!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void create(Player sender, String name, UUID uuid){
        Bukkit.getScheduler().runTask(ManyWorldPlugin.getInst(), () -> {
            try {
                // World In Use Exception. -> 실제로 loadWorld 를 통해 byte[] 값을 받아올 필요가 없음.
                byte[] serializedWorld = ManyWorldPlugin.typeLoader.loadWorld(name, false);
                CraftSlimeWorld world;

                //todo PropertyMap 구현 - Json
                SlimePropertyMap propertyMap = new SlimePropertyMap();
                /*
                spawn: 0, 64, 0
                difficulty: peaceful
                allowMonsters: true
                allowAnimals: true
                pvp: true
                environment: NORMAL
                worldType: DEFAULT
                loadOnStartup: true
                readOnly: false
                *
                * */
                propertyMap.setInt(SlimeProperties.SPAWN_X, 0);
                propertyMap.setInt(SlimeProperties.SPAWN_Y, 64);
                propertyMap.setInt(SlimeProperties.SPAWN_Z, 0);
                propertyMap.setString(SlimeProperties.DIFFICULTY, "PEACEFUL");
                propertyMap.setBoolean(SlimeProperties.ALLOW_ANIMALS, false);
                propertyMap.setBoolean(SlimeProperties.ALLOW_MONSTERS, false);
                propertyMap.setBoolean(SlimeProperties.PVP, false);
                propertyMap.setString(SlimeProperties.ENVIRONMENT, "NORMAL");
                propertyMap.setString(SlimeProperties.WORLD_TYPE, "DEFAULT");

                world = LoaderUtils.deserializeWorld(ManyWorldPlugin.userLoader, name + "_" + uuid, serializedWorld, propertyMap, false);

                Object nmsWorld = ManyWorldPlugin.swm.getNms().createNMSWorld(world);
                ManyWorldPlugin.swm.getNms().addWorldToServerList(nmsWorld);
                sender.sendMessage("생성 성공!");
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

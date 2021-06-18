package com.minepalm.manyworlds.bukkit;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.BungeeView;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.bukkit.WorldType;
import com.minepalm.manyworlds.core.JsonWorldMetadata;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;

/*
* ManyWorlds - SWM 기반 번지코드 월드 밸런싱 플러그인
* /월드 전체 - 전체에 로드 되어 있는 정보를 봅니다.
* /월드 번지 - 번지 현재 정보를 봅니다.
* /월드 버킷 - 버킷 현재 정보를 봅니다.
* /월드 등록 <SWM월드명> <샘플 이름> - 샘플 월드를 등록합니다.
* /월드 생성 <샘플 이름> - 샘플 월드를 생성합니다.
* /월드 저장 <샘플 이름> - 샘플 월드를 저장합니다.
* stop
* */
@CommandAlias("월드")
public class Commands extends BaseCommand {

    @Subcommand("전체")
    @Description("네트워크 전체에 로드되어 있는 정보를 봅니다.")
    public void allInfo(Player player){
        Bukkit.getScheduler().runTaskAsynchronously(ManyWorldsBukkit.getInst(), ()->{
            List<BukkitView> views = ManyWorldsBukkit.getCore().getGlobalDatabase().getServers();
            for (BukkitView view : views) {
                for (String loadedWorld : ManyWorldsBukkit.getCore().getGlobalDatabase().getLoadedWorlds(view.getServerName())) {
                    player.sendMessage("서버: "+view.getServerName()+" 월드: "+loadedWorld);
                }
            }
        });
    }

    @Subcommand("번지")
    @Description("번지코드에 할당 되어 있는 정보를 봅니다.")
    public void bungeeInfo(Player player){
        Bukkit.getScheduler().runTaskAsynchronously(ManyWorldsBukkit.getInst(), ()-> {
            BungeeView view = ManyWorldsBukkit.getCore().getGlobalDatabase().getProxy();
            player.sendMessage("번지코드: "+view.getServerName());
            player.sendMessage("번지코드 토탈 카운트: "+view.getTotalCount());
        });
    }

    @Subcommand("버킷")
    @Description("버킷에 할당되어 있는 정보를 봅니다.")
    public void bukkitInfo(Player player, @Default("!Self") String name){
        Bukkit.getScheduler().runTaskAsynchronously(ManyWorldsBukkit.getInst(), ()-> {
            BukkitView view;

            if(name.equals("!Self")) {
                view = (BukkitView)ManyWorldsBukkit.getCore().getGlobalDatabase().getCurrentServer();
            }else{
                view = ManyWorldsBukkit.getCore().getGlobalDatabase().getBukkitServer(name);
            }

            if(view == null){
                player.sendMessage("존재하지 않는 버킷입니다.");
            }else {
                player.sendMessage("버킷: " + view.getServerName());
                player.sendMessage("버킷 토탈 카운트: " + view.getLoadedWorlds());
            }
        });

    }

    @Subcommand("등록")
    @Description("Slime World Manager 로부터 샘플 월드를 등록합니다.")
    public void register(Player player, String SWMWorld, String name){
        SlimeLoader loader = ManyWorldsBukkit.getSwm().getLoader("mysql");
        try {
            byte[] bytes;

            try {
                bytes = loader.loadWorld(SWMWorld, false);
            }catch (WorldInUseException e){
                player.sendMessage("월드가 사용중입니다. 언로드 하시고 등록해주세요.");
                return;
            }

            WorldInfo info = ManyWorldsBukkit.getCore().newWorldInfo(WorldType.SAMPLE, name, name);
            ManyWorldsBukkit.getInst().getWorldDatabase(WorldType.SAMPLE).saveWorld(new PreWorldData(info, bytes, new JsonWorldMetadata()));
            player.sendMessage("월드 저장 완료!");
        }catch (UnknownWorldException | IOException e){
            e.printStackTrace();
        }
    }

    @Subcommand("생성")
    @Description("해당 샘플에 맞는 플레이어 개인 월드를 생성합니다.")
    public void createOwn(Player player, String name){
        WorldInfo info = ManyWorldsBukkit.getCore().newWorldInfo(WorldType.USER, name, name+"_"+player.getUniqueId().toString());
        ManyWorldsBukkit.getInst().createNewWorld(info);
        player.sendMessage("월드 생성 완료!");
    }

    @Subcommand("저장")
    @Description("해당 샘플에 맞는 플레이어 개인 월드를 저장합니다.")
    public void saveOwn(Player player, String name){
        ManyWorldsBukkit.getInst().getWorldStorage().unregisterWorld(name+"_"+player.getUniqueId());
        player.sendMessage("월드 저장 완료!");
    }
}

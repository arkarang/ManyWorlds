package com.minepalm.manyworlds.bukkit;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;

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
        //todo: /월드 전체 명령어 구현
        player.sendMessage("미구현!");
    }

    @Subcommand("번지")
    @Description("번지코드에 할당 되어 있는 정보를 봅니다.")
    public void bungeeInfo(Player player){
        player.sendMessage("미구현!");
    }

    @Subcommand("버킷")
    @Description("버킷에 할당되어 있는 정보를 봅니다.")
    public void bukkitInfo(Player player, @Default("!Self") String name){

    }

    @Subcommand("등록")
    @Description("Slime World Manager 로부터 샘플 월드를 등록합니다.")
    public void register(Player player, String SWMWorld, String name){

    }

    @Subcommand("생성")
    @Description("해당 샘플에 맞는 플레이어 개인 월드를 생성합니다.")
    public void createOwn(Player player, String name){

    }

    @Subcommand("해당 샘플에 맞는 플레이어 개인 월드를 저장합니다.")
    public void saveOwn(Player player, String name){
        ManyWorldsBukkit.getInst().getWorldStorage().unregisterWorld(name+"_"+player.getUniqueId());
    }
}

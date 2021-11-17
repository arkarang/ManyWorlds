package com.minepalm.manyworlds.bukkit;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.minepalm.manyworlds.api.ManyWorld;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.core.WorldTokens;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@CommandAlias("월드번지")
@CommandPermission("manyworlds.admin")
@RequiredArgsConstructor
public class BungeeCommands extends BaseCommand {

    final ManyWorlds service;

    @Subcommand("자동생성")
    @Syntax("/월드번지 자동생성 <타입명> <월드명> - 월드를 적당한 버킷에 할당하여 새로운 월드를 생성합니다.")
    public void createAutomatically(CommandSender sender, String type, String worldName){
        sender.sendMessage("자동생성을 시도합니다...");
        ManyWorld mw = service.get(WorldTokens.USER, worldName);
        mw.isExists().thenApply(exists->{
            if(exists){
                sender.sendMessage("이미 월드가 존재합니다.");
                return CompletableFuture.completedFuture(null);
            }else{
                return mw.create(new WorldInform(WorldTokens.TYPE, worldName));
            }
        }).thenAccept(result->{
            if(result != null){
                sender.sendMessage("월드 생성이 완료되었습니다.");
            }
        });

    }

    @Subcommand("수동생성")
    @Syntax("/월드번지 수동생성 <서버명> <타입명> <월드명> - 월드를 해당 버킷에 할당하여 새로운 월드를 생성합니다. ")
    public void createManually(CommandSender sender, String server, String type, String worldName){
        sender.sendMessage("수동생성을 시도합니다...");
        ManyWorld mw = service.get(WorldTokens.USER, worldName);
        CompletableFuture<BukkitView> viewFuture = service.getWorldNetwork().getBukkitServer(server);
        mw.isExists().thenApply(exists->{
            if(exists){
                sender.sendMessage("이미 월드가 존재합니다.");
                return CompletableFuture.completedFuture(null);
            }else{
                return viewFuture.thenCompose(view -> {
                    if(view == null){
                        sender.sendMessage("서버가 존재하지 않습니다.");
                        return CompletableFuture.completedFuture(null);
                    }else{
                        return mw.create(view, new WorldInform(WorldTokens.TYPE, type)).thenAccept(mw2->{
                            if(mw2 != null){
                                sender.sendMessage("월드를 생성 완료했습니다.");
                            }else
                                sender.sendMessage("월드 생성을 실패했습니다.");
                        });
                    }
                });
            }
        });
    }

    @Subcommand("자동할당")
    @Syntax("/월드번지 자동할당 <월드명> - 월드를 적당한 버킷에 할당하여 로드합니다.")
    public void loadAutomatically(CommandSender sender, String worldName){
        sender.sendMessage("자동할당을 시도합니다...");
        ManyWorld mw = service.get(WorldTokens.USER, worldName);
        CompletableFuture<Boolean> isLoadedFuture = mw.isLoaded();
        mw.isExists().thenCombine(isLoadedFuture, (exists, loaded) -> {
            if(!exists) {
                sender.sendMessage("월드가 존재하지 않습니다.");
                return CompletableFuture.completedFuture(null);
            }
            if(loaded) {
                sender.sendMessage("이미 월드가 로드되어 있습니다.");
                return CompletableFuture.completedFuture(null);
            }
            return mw.load().thenAccept(mw2->{
                if(mw2 != null){
                    sender.sendMessage("월드를 로드 완료했습니다.");
                }else
                    sender.sendMessage("월드 로드를 실패했습니다.");
            });
        });
    }

    @Subcommand("수동할당")
    @Syntax("/월드번지 수동할당 <서버명> <월드명> - 월드를 해당 버킷에 로드합니다. ")
    public void loadManually(CommandSender sender, String server, String worldName){
        sender.sendMessage("수동할당을 시도합니다...");
        ManyWorld mw = service.get(WorldTokens.USER, worldName);
        CompletableFuture<BukkitView> viewFuture = service.getWorldNetwork().getBukkitServer(server);
        CompletableFuture<Boolean> isLoadedFuture = mw.isLoaded();
        mw.isExists().thenCombine(isLoadedFuture, (exists, loaded) -> {
            if(!exists) {
                sender.sendMessage("월드가 존재하지 않습니다.");
                return CompletableFuture.completedFuture(null);
            }
            if(loaded) {
                sender.sendMessage("이미 월드가 로드되어 있습니다.");
                return CompletableFuture.completedFuture(null);
            }
            return viewFuture.thenCompose(view -> {
                if(view == null){
                    sender.sendMessage("서버가 존재하지 않습니다.");
                    return CompletableFuture.completedFuture(null);
                }else{
                    return mw.load(view).thenAccept(mw2->{
                        if(mw2 != null){
                            sender.sendMessage("월드를 로드 완료했습니다.");
                        }else
                            sender.sendMessage("월드 로드를 실패했습니다.");
                    });
                }
            });
        });
    }

    @Subcommand("언로드")
    @Syntax("/월드번지 언로드 <월드명> - 월드를 언로드합니다.")
    public void unload(CommandSender sender, String worldName){
        sender.sendMessage("언로드를 시도합니다...");
        ManyWorld mw = service.get(WorldTokens.USER, worldName);
        CompletableFuture<Boolean> isLoadedFuture = mw.isLoaded();
        mw.isExists().thenCombine(isLoadedFuture, (exists, loaded) -> {
            if(!exists) {
                sender.sendMessage("월드가 존재하지 않습니다.");
                return CompletableFuture.completedFuture(null);
            }
            if(loaded) {
                sender.sendMessage("이미 월드가 로드되어 있습니다.");
                return CompletableFuture.completedFuture(null);
            }
            return mw.unload().thenAccept(completed->{
                if(completed != null){
                    sender.sendMessage("월드를 로드 완료했습니다.");
                }else
                    sender.sendMessage("월드 로드를 실패했습니다.");
            });
        });
    }

    @Subcommand("도움말")
    public void help(Player sender){
        sender.sendMessage("/월드번지 자동생성 <타입명> <월드명> - 월드를 적당한 버킷에 할당하여 새로운 월드를 생성합니다.");
        sender.sendMessage("/월드번지 수동생성 <서버명> <타입명> <월드명> - 월드를 해당 버킷에 할당하여 새로운 월드를 생성합니다. ");
        sender.sendMessage("/월드번지 자동할당 <월드명> - 월드를 적당한 버킷에 할당하여 로드합니다.");
        sender.sendMessage("/월드번지 수동할당 <서버명> <월드명> - 월드를 해당 버킷에 로드합니다. ");
        sender.sendMessage("/월드번지 언로드 <월드명> - 월드를 언로드합니다.");
    }
}

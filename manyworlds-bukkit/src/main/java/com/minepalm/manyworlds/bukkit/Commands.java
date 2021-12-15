package com.minepalm.manyworlds.bukkit;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.plugin.SWMPlugin;
import com.minepalm.arkarangutils.bukkit.BukkitExecutor;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.PreparedWorld;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.core.ManyProperties;
import com.minepalm.manyworlds.core.WorldTokens;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
@RequiredArgsConstructor
@CommandAlias("월드버킷")
public class Commands extends BaseCommand {

    private final ManyWorlds core;
    private final SWMPlugin swm;
    private final BukkitExecutor executor;

    @Default
    @Subcommand("도움말")
    public void help(CommandSender sender){
        sender.sendMessage("/월드버킷 도움말 - 도움말을 확인합니다.");
        sender.sendMessage("/월드버킷 전체 - 네트워크 전체에 로드되어 있는 정보를 봅니다.");
        sender.sendMessage("/월드버킷 버킷 <버킷명> - 이 버킷에 할당 되어 있는 정보를 봅니다.");
        sender.sendMessage("/월드버킷 등록 <월드> - 해당 월드를 데이터베이스에 등록합니다.");
    }

    @Subcommand("전체")
    @Description("네트워크 전체에 로드되어 있는 정보를 봅니다.")
    public void allInfo(CommandSender sender){
        executor.async(()->{
            try {
                List<BukkitView> views = core.getWorldNetwork().getServers().get();
                for (BukkitView view : views) {
                    for (String loadedWorld : ManyWorlds.getInst().getWorldNetwork().getLoadedWorlds(view.getServerName()).get())
                        sender.sendMessage("서버: " + view.getServerName() + " 월드: " + loadedWorld);
                }
            }catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
            }
        });
    }

    @Subcommand("버킷")
    @Description("버킷에 할당되어 있는 정보를 봅니다.")
    public void bukkitInfo(CommandSender player, @Default("!Self") String name){
        executor.async(()-> {
            BukkitView view;
            try {
                if (name.equals("!Self")) {
                    view = core;
                } else {
                    view = ManyWorlds.getInst().getWorldNetwork().getBukkitServer(name).get();
                }

                if (view == null) {
                    player.sendMessage("존재하지 않는 버킷입니다.");
                } else {
                    player.sendMessage("버킷: " + view.getServerName());
                    player.sendMessage("버킷 토탈 카운트: " + view.getWorldCounts());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

    }

    @Subcommand("등록")
    @Description("Slime World Manager 로부터 샘플 월드를 등록합니다.")
    public void register(CommandSender player, String SWMWorld, String name){
        /*
        1. SWM에서 mysql 설정을 킨다. (컨피그에 있음
        2. SWM에서 월드를 하나 새로 판다.
        3. 월드에 적절한 건축물을 복붙한다. (여따 FAWE 갈겨도 됨)
        4. 그리고 월드를 언로드한다.
        5. /월드버킷 등록 <월드명> <앞으로쓸 월드이름> 을 친다. 예) /월드버킷 등록 test123 island
        -> 등록 성공!
         */
        SlimeLoader loader = swm.getLoader("mysql");
        try {
            byte[] bytes;

            try {
                bytes = loader.loadWorld(SWMWorld, false);
            }catch (WorldInUseException e){
                player.sendMessage("월드가 사용중입니다. 언로드 하시고 등록해주세요.");
                return;
            }

            WorldInform info = new WorldInform(WorldTokens.TYPE, name);
            core.getRegistry().getWorldDatabase(WorldTokens.TYPE).saveWorld(new PreparedWorld(info, bytes, new ManyProperties()));
            player.sendMessage("월드 저장 완료!");
        }catch (UnknownWorldException | IOException e){
            e.printStackTrace();
        }
    }

}

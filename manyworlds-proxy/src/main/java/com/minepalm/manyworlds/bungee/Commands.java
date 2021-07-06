package com.minepalm.manyworlds.bungee;

import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.bukkit.WorldType;
import com.minepalm.manyworlds.core.ManyWorldInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.Optional;

public class Commands extends Command {

    public Commands() {
        super("월드번지");
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if(strings.length >= 1){
            switch (strings[0]){
                case "자동생성":
                    if(strings.length == 2) {
                        ManyWorldsBungee.createAtLeast(new ManyWorldInfo(WorldType.USER, strings[1]));
                    }else{
                        sender.sendMessage(new TextComponent("/월드번지 자동생성 <타입명> <월드명> - 월드를 적당한 버킷에 할당하여 새로운 월드를 생성합니다."));
                    }
                    break;
                case "수동생성":
                    if(strings.length == 4) {
                        String serverName = strings[1];
                        String typeName = strings[2];
                        String worldName = strings[3];
                        BukkitView view = ManyWorldsBungee.getDatabase().getBukkitServer(serverName);
                        ManyWorldsBungee.createSpecific(view, new ManyWorldInfo(WorldType.USER, typeName, worldName));
                    }else{
                        sender.sendMessage(new TextComponent("/월드번지 수동생성 <서버명> <타입명> <월드명> - 월드를 해당 버킷에 할당하여 새로운 월드를 생성합니다. "));
                    }
                    break;
                case "자동할당":
                    if(strings.length == 2) {
                        ManyWorldsBungee.loadAtLeast(new ManyWorldInfo(WorldType.USER, strings[1]));
                    }else{
                        sender.sendMessage(new TextComponent("/월드번지 자동할당 <월드명> - 월드를 적당한 버킷에 할당하여 로드합니다."));
                    }
                    break;
                case "수동할당":
                    if(strings.length == 3) {
                        String serverName = strings[1];
                        String worldName = strings[2];
                        BukkitView view = ManyWorldsBungee.getDatabase().getBukkitServer(serverName);
                        ManyWorldsBungee.loadSpecific(view, new ManyWorldInfo(WorldType.USER, worldName), true);
                    }else{
                        sender.sendMessage(new TextComponent("/월드번지 수동할당 <서버명> <월드명> - 월드를 해당 버킷에 로드합니다. "));
                    }
                    break;
                case "언로드":
                    if(strings.length == 2) {
                        String worldName = strings[1];
                        WorldInfo info = new ManyWorldInfo(WorldType.USER, worldName);
                        Optional<BukkitView> view = ManyWorldsBungee.getDatabase().getLoadedServer(info);
                        view.ifPresent(v-> ManyWorldsBungee.loadSpecific(v, info, false));
                    }else{
                        sender.sendMessage(new TextComponent("/월드번지 언로드 <월드명> - 월드를 언로드합니다."));
                    }
                    break;
                default:
                    help(sender);
            }
        }else{
            help(sender);
        }
    }

    public void help(CommandSender sender){
        sender.sendMessage(new TextComponent("/월드번지 자동생성 <타입명> <월드명> - 월드를 적당한 버킷에 할당하여 새로운 월드를 생성합니다."));
        sender.sendMessage(new TextComponent("/월드번지 수동생성 <서버명> <타입명> <월드명> - 월드를 해당 버킷에 할당하여 새로운 월드를 생성합니다. "));
        sender.sendMessage(new TextComponent("/월드번지 자동할당 <월드명> - 월드를 적당한 버킷에 할당하여 로드합니다."));
        sender.sendMessage(new TextComponent("/월드번지 수동할당 <서버명> <월드명> - 월드를 해당 버킷에 로드합니다. "));
        sender.sendMessage(new TextComponent("/월드번지 언로드 <월드명> - 월드를 언로드합니다."));

    }
}

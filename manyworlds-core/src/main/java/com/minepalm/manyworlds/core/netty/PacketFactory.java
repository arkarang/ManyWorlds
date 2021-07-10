package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PacketFactory {

    final ServerView from;
    final ServerView to;

    public WorldLoadPacket createWorldLoad(String sampleName, String worldName, boolean loadOrUnload){
        return new WorldLoadPacket(from, to, sampleName, worldName, loadOrUnload);
    }

    public WorldLoadPacket createWorldLoad(WorldInfo info, boolean loadOrUnload){
        return new WorldLoadPacket(from, to, info, loadOrUnload);
    }

    public WorldCreatePacket createWorldCreate(String sampleName, String worldName){
        return new WorldCreatePacket(from, to, sampleName, worldName);
    }

    public ServerUpdatedPacket createServerUpdated(boolean onOff){
        return new ServerUpdatedPacket(from, to, onOff);
    }

    public static PacketFactory newPacket(ServerView from, ServerView to){
        return new PacketFactory(from, to);
    }

}

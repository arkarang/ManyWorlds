package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.ServerView;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PacketFactory {

    final ServerView from;
    final ServerView to;

    public WorldLoadPacket createWorldLoad(String worldName, boolean loadOrUnload){
        WorldLoadPacket packet = new WorldLoadPacket(from, to, worldName, loadOrUnload);
        return packet;
    }

    public WorldCreatePacket createWorldCreate(String sampleName, String worldName){
        if(to instanceof BukkitView) {
            WorldCreatePacket packet = new WorldCreatePacket(from, (BukkitView) to, sampleName, worldName);
            return packet;
        }else
            throw new IllegalStateException("Target server must be Bukkit");
    }

    public ServerUpdatedPacket createServerUpdated(boolean onOff){
        return new ServerUpdatedPacket(from, to, onOff);
    }

    public static PacketFactory newPacket(ServerView from, ServerView to){
        return new PacketFactory(from, to);
    }

}
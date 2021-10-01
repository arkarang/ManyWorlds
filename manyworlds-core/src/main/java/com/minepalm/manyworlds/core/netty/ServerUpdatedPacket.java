package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.ServerView;
import lombok.Getter;

@Getter
public class ServerUpdatedPacket extends BasicPacket{

    final boolean online;

    public ServerUpdatedPacket(ServerView from, ServerView to, boolean online) {
        super(from, to);
        this.online = online;
    }

}

package com.minepalm.manyworlds.core.netty;

import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.netty.WorldPacket;
import lombok.Getter;

public abstract class BasicPacket implements WorldPacket{

    @Getter
    final ServerView from, to;

    BasicPacket(ServerView from, ServerView to){
        this.from = from;
        this.to = to;
    }
    
}

package com.minepalm.manyworlds.api.netty;

import com.minepalm.hellobungee.api.HelloExecutor;
import com.minepalm.manyworlds.api.entity.ServerView;

public interface Controller {

    void send(ServerView view, WorldPacket packet);

}

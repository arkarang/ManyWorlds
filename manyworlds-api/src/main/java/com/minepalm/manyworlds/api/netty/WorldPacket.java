package com.minepalm.manyworlds.api.netty;

import com.minepalm.manyworlds.api.ServerView;

public interface WorldPacket {

    ServerView getFrom();

    ServerView getTo();

}

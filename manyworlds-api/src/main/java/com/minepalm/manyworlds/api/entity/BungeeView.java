package com.minepalm.manyworlds.api.entity;

import lombok.Getter;

@Getter
public class BungeeView extends ServerView {

    private final int totalCount;

    public BungeeView(String serverName, int totalCount) {
        super(serverName);
        this.totalCount = totalCount;
    }
}

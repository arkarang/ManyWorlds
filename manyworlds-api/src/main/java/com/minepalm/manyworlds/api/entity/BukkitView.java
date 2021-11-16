package com.minepalm.manyworlds.api.entity;

import lombok.Getter;

@Getter
public class BukkitView extends ServerView {

    private final int worldCounts;

    public BukkitView(String serverName, int worldCounts) {
        super(serverName);
        this.worldCounts = worldCounts;
    }
}

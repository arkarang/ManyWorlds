package com.minepalm.manyworlds.api;

public interface GlobalDatabase {

    BungeeSnapshot getBungeeSnapshot();

    BukkitSnapshot getBukkitSnapshot(String name);
}

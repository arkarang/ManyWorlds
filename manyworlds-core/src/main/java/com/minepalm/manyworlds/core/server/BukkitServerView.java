package com.minepalm.manyworlds.core.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minepalm.manyworlds.api.BukkitView;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BukkitServerView implements BukkitView {

    @JsonProperty("serverName")
    @Getter
    final String serverName;

    @JsonProperty("loadedWorlds")
    @Getter
    final int loadedWorlds;

}

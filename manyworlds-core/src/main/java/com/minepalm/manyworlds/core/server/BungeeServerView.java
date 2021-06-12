package com.minepalm.manyworlds.core.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minepalm.manyworlds.api.BungeeView;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BungeeServerView implements BungeeView {

    @JsonProperty("serverName")
    final String serverName;

    @JsonProperty("count")
    final int totalCount;

}

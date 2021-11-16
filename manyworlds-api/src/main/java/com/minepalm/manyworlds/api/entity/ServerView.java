package com.minepalm.manyworlds.api.entity;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ServerView {

    @Getter
    private final String serverName;

}

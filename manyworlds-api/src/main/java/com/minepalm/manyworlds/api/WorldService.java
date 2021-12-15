package com.minepalm.manyworlds.api;


import com.minepalm.manyworlds.api.bukkit.WorldCategory;
import com.minepalm.manyworlds.api.bukkit.WorldEntityStorage;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;

import java.util.concurrent.CompletableFuture;

//todo: 월드 이름 전체 네트워크에서 유니크하게 만들기
//todo: 월드 생성할때 월드 이름 유일한지 검증하기
public interface WorldService {

    WorldLoadService getLoadService();

    WorldEntityStorage getWorldEntityStorage();

    ManyWorld get(WorldInform inform);

    ManyWorld get(WorldCategory category, String worldName);

    CompletableFuture<ManyWorld> createNewWorld(WorldInform origin, WorldInform info);

    CompletableFuture<ManyWorld> loadWorld(WorldInform info);

    CompletableFuture<Boolean> save(WorldInform info);

    CompletableFuture<Boolean> unload(WorldInform info);

    CompletableFuture<ManyWorld> move(WorldInform inform, ServerView to);

    CompletableFuture<ManyWorld> copy(WorldInform origin, WorldInform to);

}

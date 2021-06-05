package com.minepalm.manyworlds.api.bukkit;

//로딩된 월드를 관리하는 객체
public interface WorldManager {

    WorldDatabase getWorldDatabase();

    WorldStorage getWorldStorage();
}

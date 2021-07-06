package com.minepalm.manyworlds.api.bukkit;

import java.util.concurrent.Future;

public interface WorldDatabase {

    Future<PreparedWorld> prepareWorld(WorldInfo info);

    Future<Void> saveWorld(PreparedWorld world);

    Future<WorldInfo> getWorldInfo(String fullName);

    Future<Void> deleteWorld(WorldInfo info);

    Future<Void> deleteWorld(String fullName);


}

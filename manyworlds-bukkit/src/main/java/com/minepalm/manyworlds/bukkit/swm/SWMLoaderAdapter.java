package com.minepalm.manyworlds.bukkit.swm;

import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.minepalm.manyworlds.api.WorldProperties;
import com.minepalm.manyworlds.api.bukkit.WorldDatabase;
import com.minepalm.manyworlds.api.entity.PreparedWorld;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.bukkit.ManyWorlds;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class SWMLoaderAdapter implements SlimeLoader {

    final WorldInform inform;
    final WorldDatabase database;
    final ManyWorlds service;
    final WorldProperties properties;

    @SneakyThrows
    @Override
    public byte[] loadWorld(String s, boolean b){
        return database.prepareWorld(inform).get().getWorldBytes();
    }

    @SneakyThrows
    @Override
    public boolean worldExists(String s){
        return database.exists(inform).get();
    }

    @Override
    public List<String> listWorlds() {
        return service.getWorldEntityStorage().getLoadedWorldsAll();
    }

    @Override
    public void saveWorld(String s, byte[] bytes, boolean lock) throws IOException {
        database.saveWorld(new PreparedWorld(inform, bytes, properties));
        service.save(inform);
    }

    @Override
    public void unlockWorld(String s) throws IOException {
        //Do nothing.
    }

    @SneakyThrows
    @Override
    public boolean isWorldLocked(String s) throws IOException {
        return service.getWorldNetwork().isWorldLoaded(s).get();
    }

    @Override
    public void deleteWorld(String s) throws IOException {
        database.deleteWorld(s);
    }
}

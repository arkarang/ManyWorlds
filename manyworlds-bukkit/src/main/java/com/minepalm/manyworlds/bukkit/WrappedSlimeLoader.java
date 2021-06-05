package com.minepalm.manyworlds.bukkit;

import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.minepalm.manyworlds.api.bukkit.WorldDatabase;

import java.io.IOException;
import java.util.List;

//todo: implements
public class WrappedSlimeLoader implements SlimeLoader {

    WrappedSlimeLoader(WorldDatabase worldDB){

    }

    @Override
    public byte[] loadWorld(String s, boolean b) throws UnknownWorldException, WorldInUseException, IOException {
        return new byte[0];
    }

    @Override
    public boolean worldExists(String s) throws IOException {
        return false;
    }

    @Override
    public List<String> listWorlds() throws IOException {
        return null;
    }

    @Override
    public void saveWorld(String s, byte[] bytes, boolean b) throws IOException {

    }

    @Override
    public void unlockWorld(String s) throws UnknownWorldException, IOException {

    }

    @Override
    public boolean isWorldLocked(String s) throws UnknownWorldException, IOException {
        return false;
    }

    @Override
    public void deleteWorld(String s) throws UnknownWorldException, IOException {

    }
}

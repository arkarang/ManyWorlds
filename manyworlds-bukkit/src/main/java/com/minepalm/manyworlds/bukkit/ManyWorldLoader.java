package com.minepalm.manyworlds.bukkit;

import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.utils.SlimeFormat;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.api.util.WorldBuffer;
import com.minepalm.manyworlds.api.util.WorldInputStream;
import com.minepalm.manyworlds.api.util.WorldOutputStream;
import com.minepalm.manyworlds.bukkit.strategies.v1_12.*;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ManyWorldLoader implements WorldLoader {

    final WorldStorage storage;
    final WorldDatabase database;
    final HashMap<LoadPhase, WorldStrategy> strategies = new HashMap<>();

    public ManyWorldLoader(WorldDatabase database, WorldStorage storage){
        this.storage = storage;
        this.database = database;
        strategies.put(LoadPhase.HEADER, new WorldHeaderStrategy());
        strategies.put(LoadPhase.CHUNK, new WorldChunkStrategy());
        strategies.put(LoadPhase.TILE_ENTITY, new WorldTileEntityStrategy());
        strategies.put(LoadPhase.ENTITY, new WorldEntityStrategy());
        strategies.put(LoadPhase.TAG, new WorldExtraDataStrategy());
        strategies.put(LoadPhase.MAP, new WorldMapStrategy());
    }

    @Override
    public ManyWorld deserialize(@NonNull PreparedWorld world) throws IOException {
        WorldBuffer buffer = new WorldBuffer();

        buffer.setName(world.getWorldInfo().getWorldName());
        buffer.setVersion(SlimeFormat.SLIME_VERSION);
        buffer.setPropertyMap(world.getMetadata().getProperties().asSlime());

        WorldInputStream stream = new WorldInputStream(world.getWorldBytes());

        for (int i = LoadPhase.HEADER.number(); i < LoadPhase.END.number(); i++) {
            strategies.get(LoadPhase.getPhase(i)).deserialize(stream, buffer);
        }

        CraftManyWorld result = new CraftManyWorld(this, world.getWorldInfo(), world.getMetadata(), buffer);

        stream.close();
        buffer.release();

        return result;
    }

    @Override
    public PreparedWorld serialize(@NonNull ManyWorld world) throws IOException{
        WorldBuffer buffer = new WorldBuffer();
        if(!(world instanceof CraftManyWorld)){
            throw new UnsupportedOperationException("world must be CraftManyWorld");
        }
        CraftManyWorld manyworld = (CraftManyWorld) world;

        buffer.setName(world.getName());
        buffer.setVersion(SlimeFormat.SLIME_VERSION);
        buffer.setWorldVersion(manyworld.getVersion()); // 여기
        buffer.setPropertyMap(world.getPropertyMap());
        buffer.setChunks(manyworld.getChunks());
        buffer.setWorldMaps(manyworld.getWorldMaps());
        buffer.setExtraData(manyworld.getExtraData());

        WorldOutputStream stream = new WorldOutputStream();

        for (int i = LoadPhase.HEADER.number(); i < LoadPhase.END.number(); i++) {
            strategies.get(LoadPhase.getPhase(i)).serialize(stream, buffer);
        }

        PreparedWorld preparedWorld = new PreWorldData(world.getWorldInfo(), stream.get(), world.getMetadata());

        stream.close();
        buffer.release();

        return preparedWorld;
    }

    @SneakyThrows
    @Override
    public byte[] loadWorld(String s, boolean b){
        WorldInfo info = database.getWorldInfo(s).get();
        if(info != null)
            return database.prepareWorld(info).get().getWorldBytes();
        else
            throw new UnknownWorldException(s);
    }

    @Override
    public boolean worldExists(String s){
        return database.getWorldInfo(s) != null;
    }

    @Override
    public List<String> listWorlds() {
        return storage.getLoadedWorldsAll();
    }

    @Override
    public void saveWorld(String s, byte[] bytes, boolean lock) throws IOException {
        ManyWorldStorage storage = (ManyWorldStorage) this.storage;
        ManyWorld world = storage.remove(s);
        PreparedWorld pw = serialize(world);
        database.saveWorld(pw);
    }

    @Override
    public void unlockWorld(String s) throws UnknownWorldException, IOException {
        //Do nothing.
    }

    @Override
    public boolean isWorldLocked(String s) throws UnknownWorldException, IOException {
        return ManyWorlds.getCore().getGlobalDatabase().isWorldLoaded(s);
    }

    @Override
    public void deleteWorld(String s) throws UnknownWorldException, IOException {
        database.deleteWorld(s);
    }
}

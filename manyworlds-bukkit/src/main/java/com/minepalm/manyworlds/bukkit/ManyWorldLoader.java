package com.minepalm.manyworlds.bukkit;

import com.grinderwolf.swm.api.utils.SlimeFormat;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.api.util.WorldInputStream;
import com.minepalm.manyworlds.api.util.WorldOutputStream;
import com.minepalm.manyworlds.bukkit.errors.WorldCorruptedException;
import com.minepalm.manyworlds.bukkit.strategies.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ManyWorldLoader implements WorldLoader {

    @Getter
    protected final WorldDatabase database;
    protected final WorldStorage storage;
    protected final HashMap<LoadPhase, WorldStrategy> strategies = new HashMap<>();

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
            buffer.setPhase(LoadPhase.getPhase(i));
            strategies.get(LoadPhase.getPhase(i)).deserialize(stream, buffer);
        }
        buffer.setPhase(LoadPhase.END);
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
            buffer.setPhase(LoadPhase.getPhase(i));
            strategies.get(LoadPhase.getPhase(i)).serialize(stream, buffer);
        }
        buffer.setPhase(LoadPhase.END);
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
            throw new WorldCorruptedException(s);
    }

    @Override
    public boolean worldExists(String s){
        return database.getWorldInfo(s) != null;
    }

    @Override
    public List<String> listWorlds() {
        return storage.getLoadedWorldsAll();
    }

    // 런타임에서 메인 쓰레드에서 호출될 일이 없어서, 따로 비동기 처리 하지 않아도 됩니다.
    // 지금 당장은 SWM 의존성과 엮여서 다소 난잡해진 감이 없잖아 있는데,
    // 나중에 SWM 의존성 제거 할때, 멀티쓰레딩 지원하는 형식대로 교체할 것입니다.
    @Override
    public void saveWorld(String s, byte[] bytes, boolean lock) throws IOException {
        ManyWorldStorage storage = (ManyWorldStorage) this.storage;
        ManyWorld world = storage.getLoadedWorld(s);
        Bukkit.getScheduler().runTaskAsynchronously(ManyWorlds.getInst(), ()->{
            try {
                PreparedWorld pw = serialize(world);
                database.saveWorld(pw);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void unlockWorld(String s) throws IOException {
        //Do nothing.
    }

    @SneakyThrows
    @Override
    public boolean isWorldLocked(String s) throws IOException {
        return ManyWorlds.getCore().getGlobalDatabase().isWorldLoaded(s).get();
    }

    @Override
    public void deleteWorld(String s) throws IOException {
        database.deleteWorld(s);
    }
}

package com.minepalm.manyworlds.bukkit;

import com.grinderwolf.swm.api.utils.SlimeFormat;
import com.minepalm.arkarangutils.bukkit.BukkitExecutor;
import com.minepalm.manyworlds.api.WorldProperties;
import com.minepalm.manyworlds.api.bukkit.*;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.api.entity.PreparedWorld;
import com.minepalm.manyworlds.api.util.WorldInputStream;
import com.minepalm.manyworlds.api.util.WorldOutputStream;
import com.minepalm.manyworlds.bukkit.strategies.*;
import com.minepalm.manyworlds.bukkit.swm.SWMWorldEntity;
import lombok.NonNull;

import java.io.IOException;
import java.util.HashMap;

public abstract class AbstractWorldFactory implements WorldFactory {

    protected final BukkitExecutor executor;

    protected final HashMap<LoadPhase, WorldStrategy> strategies = new HashMap<>();

    public AbstractWorldFactory(BukkitExecutor executor){
        this.executor = executor;
        strategies.put(LoadPhase.HEADER, new WorldHeaderStrategy());
        strategies.put(LoadPhase.CHUNK, new WorldChunkStrategy());
        strategies.put(LoadPhase.TILE_ENTITY, new WorldTileEntityStrategy());
        strategies.put(LoadPhase.ENTITY, new WorldEntityStrategy());
        strategies.put(LoadPhase.TAG, new WorldExtraDataStrategy());
        strategies.put(LoadPhase.MAP, new WorldMapStrategy());
    }

    @Override
    public WorldEntity deserialize(@NonNull PreparedWorld world) throws IOException {

        WorldBuffer buffer = new WorldBuffer();

        buffer.setName(world.getWorldInform().getName());
        buffer.setVersion(SlimeFormat.SLIME_VERSION);

        WorldInputStream stream = new WorldInputStream(world.getWorldBytes());

        for (int i = LoadPhase.HEADER.number(); i < LoadPhase.END.number(); i++) {
            buffer.setPhase(LoadPhase.getPhase(i));
            strategies.get(LoadPhase.getPhase(i)).deserialize(stream, buffer);
        }
        buffer.setPhase(LoadPhase.END);
        WorldEntity result = buildWorldEntity(world.getWorldInform(), world.getProperties(), buffer);

        stream.close();
        buffer.release();

        return result;
    }

    protected abstract WorldEntity buildWorldEntity(WorldInform info, WorldProperties properties, WorldBuffer buffer);

    @Override
    public PreparedWorld serialize(@NonNull WorldEntity world) throws IOException{
        WorldBuffer buffer = new WorldBuffer();
        if(!(world instanceof SWMWorldEntity)){
            throw new UnsupportedOperationException("world must be SWMWorldEntity");
        }
        SWMWorldEntity manyworld = (SWMWorldEntity) world;

        buffer.setName(world.getWorldInform().getName());
        buffer.setVersion(SlimeFormat.SLIME_VERSION);
        buffer.setWorldVersion(manyworld.getVersion()); // 여기
        setProperties(buffer, world.getWorldProperties());
        buffer.setChunks(manyworld.getChunks());
        buffer.setWorldMaps(manyworld.getWorldMaps());
        buffer.setExtraData(manyworld.getExtraData());

        WorldOutputStream stream = new WorldOutputStream();

        for (int i = LoadPhase.HEADER.number(); i < LoadPhase.END.number(); i++) {
            buffer.setPhase(LoadPhase.getPhase(i));
            strategies.get(LoadPhase.getPhase(i)).serialize(stream, buffer);
        }
        buffer.setPhase(LoadPhase.END);
        PreparedWorld preparedWorld = new PreparedWorld(world.getWorldInform(), stream.get(), world.getWorldProperties());

        stream.close();
        buffer.release();

        return preparedWorld;
    }
    protected abstract void setProperties(WorldBuffer buffer, WorldProperties properties);
}

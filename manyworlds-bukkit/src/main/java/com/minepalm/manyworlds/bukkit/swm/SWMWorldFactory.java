package com.minepalm.manyworlds.bukkit.swm;

import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.minepalm.arkarangutils.bukkit.BukkitExecutor;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.api.WorldProperties;
import com.minepalm.manyworlds.api.entity.PreparedWorld;
import com.minepalm.manyworlds.api.bukkit.WorldDatabase;
import com.minepalm.manyworlds.api.bukkit.WorldEntity;
import com.minepalm.manyworlds.api.bukkit.WorldEntityStorage;
import com.minepalm.manyworlds.bukkit.AbstractWorldFactory;
import com.minepalm.manyworlds.bukkit.ManyWorlds;
import com.minepalm.manyworlds.bukkit.errors.WorldCorruptedException;
import com.minepalm.manyworlds.bukkit.strategies.WorldBuffer;
import com.minepalm.manyworlds.core.ManyProperties;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.List;

public class SWMWorldFactory extends AbstractWorldFactory {

    public SWMWorldFactory(WorldEntityStorage storage, BukkitExecutor executor) {
        super(storage, executor);
    }

    @Override
    protected WorldEntity buildWorldEntity(WorldInform info, WorldProperties properties, WorldBuffer buffer) {
        buffer.setPropertyMap(((ManyProperties)properties).asSlime());
        return new SWMWorldEntity(this, info, buffer);
    }

    @Override
    protected void setProperties(WorldBuffer buffer, WorldProperties properties) {
        buffer.setPropertyMap(((ManyProperties)properties).asSlime());
    }

}

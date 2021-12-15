package com.minepalm.manyworlds.bukkit.swm;

import com.minepalm.arkarangutils.bukkit.BukkitExecutor;
import com.minepalm.manyworlds.api.WorldProperties;
import com.minepalm.manyworlds.api.WorldRegistry;
import com.minepalm.manyworlds.api.bukkit.WorldEntity;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.bukkit.AbstractWorldFactory;
import com.minepalm.manyworlds.bukkit.ManyWorlds;
import com.minepalm.manyworlds.bukkit.strategies.WorldBuffer;
import com.minepalm.manyworlds.core.ManyProperties;

public class SWMWorldFactory extends AbstractWorldFactory {

    WorldRegistry registry;

    public SWMWorldFactory(WorldRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected WorldEntity buildWorldEntity(WorldInform info, WorldProperties properties, WorldBuffer buffer) {
        return new SWMWorldEntity(
                new SWMLoaderAdapter(info, registry.getWorldDatabase(info.getWorldCategory()), ManyWorlds.getInst(), properties),
                info,
                buffer);
    }

    @Override
    protected void setProperties(WorldBuffer buffer, WorldProperties properties) {
        buffer.setPropertyMap(((ManyProperties)properties).asSlime());
    }

}

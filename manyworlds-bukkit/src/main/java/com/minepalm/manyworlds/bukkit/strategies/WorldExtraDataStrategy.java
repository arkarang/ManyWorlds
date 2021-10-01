package com.minepalm.manyworlds.bukkit.strategies;

import com.github.luben.zstd.Zstd;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.internal.com.flowpowered.nbt.CompoundMap;
import com.grinderwolf.swm.internal.com.flowpowered.nbt.CompoundTag;
import com.minepalm.manyworlds.api.util.WorldInputStream;
import com.minepalm.manyworlds.api.util.WorldOutputStream;

import java.io.IOException;
import java.util.Optional;

public class WorldExtraDataStrategy implements WorldStrategy {

    @Override
    public WorldBuffer serialize(WorldOutputStream stream, WorldBuffer world) throws IOException {

        byte[] extra = WorldUtils.serializeCompoundTag(world.getExtraData());
        byte[] compressedExtra = Zstd.compress(extra);

        stream.writeInt(compressedExtra.length);
        stream.writeInt(extra.length);
        stream.writeBytes(compressedExtra);

        return world;
    }

    @Override
    public WorldBuffer deserialize(WorldInputStream stream, WorldBuffer buffer) throws IOException {
        byte[] compressedExtraTag = new byte[0];
        byte[] extraTag = new byte[0];

        if (buffer.getVersion() >= 2) {
            int compressedExtraTagLength = stream.readInt();
            int extraTagLength = stream.readInt();
            compressedExtraTag = new byte[compressedExtraTagLength];
            extraTag = new byte[extraTagLength];

            stream.read(compressedExtraTag);
        }

        Zstd.decompress(extraTag, compressedExtraTag);

        // Extra Data
        CompoundTag extraCompound = WorldUtils.readCompoundTag(extraTag);

        if (extraCompound == null) {
            extraCompound = new CompoundTag("", new CompoundMap());
        }

        buffer.setExtraData(extraCompound);

        // World properties
        SlimePropertyMap worldPropertyMap = buffer.getPropertyMap();
        Optional<CompoundMap> propertiesMap = extraCompound
                .getAsCompoundTag("properties")
                .map(CompoundTag::getValue);

        if (propertiesMap.isPresent()) {
            worldPropertyMap = new SlimePropertyMap(propertiesMap.get());
            worldPropertyMap.merge(buffer.getPropertyMap()); // Override world properties
        } else if (buffer.getPropertyMap() == null) { // Make sure the property map is never null
            worldPropertyMap = new SlimePropertyMap();
        }

        buffer.setPropertyMap(worldPropertyMap);

        return buffer;
    }
}

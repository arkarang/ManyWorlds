package com.minepalm.manyworlds.bukkit.strategies.v1_12;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.github.luben.zstd.Zstd;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.minepalm.manyworlds.api.bukkit.WorldStrategy;
import com.minepalm.manyworlds.api.util.WorldBuffer;
import com.minepalm.manyworlds.api.util.WorldInputStream;
import com.minepalm.manyworlds.api.util.WorldOutputStream;

import java.io.IOException;
import java.util.Optional;

public class WorldExtraDataStrategy implements WorldStrategy {

    @Override
    public WorldBuffer serialize(WorldOutputStream stream, WorldBuffer world) throws IOException {

        byte[] extra = v1_12WorldUtils.serializeCompoundTag(world.getExtraData());
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
        CompoundTag extraCompound = v1_12WorldUtils.readCompoundTag(extraTag);

        if (extraCompound == null) {
            extraCompound = new CompoundTag("", new CompoundMap());
        }

        buffer.setExtraData(extraCompound);

        // World properties
        SlimePropertyMap worldPropertyMap = buffer.getPropertyMap();
        Optional<CompoundTag> propertiesTag = buffer.getExtraData().getAsCompoundTag("properties");

        if (propertiesTag.isPresent()) {
            worldPropertyMap = SlimePropertyMap.fromCompound(propertiesTag.get());
            worldPropertyMap.merge(buffer.getPropertyMap()); // Override world properties
        } else if (buffer.getPropertyMap() == null) { // Make sure the property map is never null
            worldPropertyMap = new SlimePropertyMap();
        }

        buffer.setPropertyMap(worldPropertyMap);

        return buffer;
    }
}

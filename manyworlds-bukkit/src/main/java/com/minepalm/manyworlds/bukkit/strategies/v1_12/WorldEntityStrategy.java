package com.minepalm.manyworlds.bukkit.strategies.v1_12;

import com.flowpowered.nbt.*;
import com.github.luben.zstd.Zstd;
import com.grinderwolf.swm.api.world.SlimeChunk;
import com.minepalm.manyworlds.api.bukkit.WorldStrategy;
import com.minepalm.manyworlds.api.util.WorldBuffer;
import com.minepalm.manyworlds.api.util.WorldInputStream;
import com.minepalm.manyworlds.api.util.WorldOutputStream;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WorldEntityStrategy implements WorldStrategy {

    @Override
    public WorldBuffer serialize(WorldOutputStream stream, WorldBuffer buffer) throws IOException {
        List<CompoundTag> entitiesList = buffer.getSortedChunks().stream().flatMap(chunk -> chunk.getEntities().stream()).collect(Collectors.toList());
        stream.writeBoolean(!entitiesList.isEmpty());

        if (!entitiesList.isEmpty()) {
            ListTag<CompoundTag> entitiesNbtList = new ListTag<>("entities", TagType.TAG_COMPOUND, entitiesList);
            CompoundTag entitiesCompound = new CompoundTag("", new CompoundMap(Collections.singletonList(entitiesNbtList)));
            byte[] entitiesData = v1_12WorldUtils.serializeCompoundTag(entitiesCompound);
            byte[] compressedEntitiesData = Zstd.compress(entitiesData);

            stream.writeInt(compressedEntitiesData.length);
            stream.writeInt(entitiesData.length);
            stream.writeBytes(compressedEntitiesData);
        }

        return buffer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public WorldBuffer deserialize(WorldInputStream stream, WorldBuffer buffer) throws IOException {
        byte[] compressedEntities = new byte[0];
        byte[] entities = new byte[0];

        if (buffer.getVersion() >= 3) {
            boolean hasEntities = stream.readBoolean();

            if (hasEntities) {
                int compressedEntitiesLength = stream.readInt();
                int entitiesLength = stream.readInt();
                compressedEntities = new byte[compressedEntitiesLength];
                entities = new byte[entitiesLength];

                stream.read(compressedEntities);
            }
        }
        Zstd.decompress(entities, compressedEntities);

        CompoundTag entitiesCompound = v1_12WorldUtils.readCompoundTag(entities);

        if (entitiesCompound != null) {
            ListTag<CompoundTag> entitiesList = (ListTag<CompoundTag>) entitiesCompound.getValue().get("entities");

            for (CompoundTag entityCompound : entitiesList.getValue()) {
                ListTag<DoubleTag> listTag = (ListTag<DoubleTag>) entityCompound.getAsListTag("Pos").get();

                int chunkX = v1_12WorldUtils.floor(listTag.getValue().get(0).getValue()) >> 4;
                int chunkZ = v1_12WorldUtils.floor(listTag.getValue().get(2).getValue()) >> 4;
                long chunkKey = ((long) chunkZ) * Integer.MAX_VALUE + ((long) chunkX);
                SlimeChunk chunk = buffer.getChunks().get(chunkKey);

                if (chunk != null) {
                    chunk.getEntities().add(entityCompound);
                }else {
                    throw new IllegalStateException("chunk is null");
                }
            }
        }

        return buffer;
    }
}

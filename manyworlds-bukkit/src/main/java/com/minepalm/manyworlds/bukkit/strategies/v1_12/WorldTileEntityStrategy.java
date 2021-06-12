package com.minepalm.manyworlds.bukkit.strategies.v1_12;

import com.flowpowered.nbt.*;
import com.github.luben.zstd.Zstd;
import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.world.SlimeChunk;
import com.minepalm.manyworlds.api.util.WorldBuffer;
import com.minepalm.manyworlds.api.bukkit.WorldStrategy;
import com.minepalm.manyworlds.api.util.WorldInputStream;
import com.minepalm.manyworlds.api.util.WorldOutputStream;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WorldTileEntityStrategy implements WorldStrategy {

    @Override
    public WorldBuffer serialize(WorldOutputStream stream, WorldBuffer buffer) throws IOException {

        List<CompoundTag> tileEntitiesList = buffer.getSortedChunks().stream().flatMap(chunk -> chunk.getTileEntities().stream()).collect(Collectors.toList());
        ListTag<CompoundTag> tileEntitiesNbtList = new ListTag<>("tiles", TagType.TAG_COMPOUND, tileEntitiesList);
        CompoundTag tileEntitiesCompound = new CompoundTag("", new CompoundMap(Collections.singletonList(tileEntitiesNbtList)));
        byte[] tileEntitiesData = v1_12WorldUtils.serializeCompoundTag(tileEntitiesCompound);
        byte[] compressedTileEntitiesData = Zstd.compress(tileEntitiesData);

        stream.writeInt(compressedTileEntitiesData.length);
        stream.writeInt(tileEntitiesData.length);
        stream.writeBytes(compressedTileEntitiesData);

        return buffer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public WorldBuffer deserialize(WorldInputStream stream, WorldBuffer buffer) throws IOException {
        int compressedTileEntitiesLength = stream.readInt();
        int tileEntitiesLength = stream.readInt();
        byte[] compressedTileEntities = new byte[compressedTileEntitiesLength];
        byte[] tileEntities = new byte[tileEntitiesLength];

        stream.read(compressedTileEntities);
        Zstd.decompress(tileEntities, compressedTileEntities);

        CompoundTag tileEntitiesCompound = v1_12WorldUtils.readCompoundTag(tileEntities);

        if (tileEntitiesCompound != null) {
            ListTag<CompoundTag> tileEntitiesList = (ListTag<CompoundTag>) tileEntitiesCompound.getValue().get("tiles");

            for (CompoundTag tileEntityCompound : tileEntitiesList.getValue()) {
                int chunkX = ((IntTag) tileEntityCompound.getValue().get("x")).getValue() >> 4;
                int chunkZ = ((IntTag) tileEntityCompound.getValue().get("z")).getValue() >> 4;
                long chunkKey = ((long) chunkZ) * Integer.MAX_VALUE + ((long) chunkX);
                SlimeChunk chunk = buffer.getChunks().get(chunkKey);

                if (chunk != null) {
                    chunk.getTileEntities().add(tileEntityCompound);
                }else{
                    //throw new CorruptedWorldException(worldName);
                }
            }
        }


        return buffer;
    }
}

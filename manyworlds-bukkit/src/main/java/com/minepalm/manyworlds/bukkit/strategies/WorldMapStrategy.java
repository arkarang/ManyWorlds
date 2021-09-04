package com.minepalm.manyworlds.bukkit.strategies;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.ListTag;
import com.flowpowered.nbt.TagType;
import com.github.luben.zstd.Zstd;
import com.minepalm.manyworlds.api.bukkit.WorldStrategy;
import com.minepalm.manyworlds.api.util.WorldBuffer;
import com.minepalm.manyworlds.api.util.WorldInputStream;
import com.minepalm.manyworlds.api.util.WorldOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//마지막
public class WorldMapStrategy implements WorldStrategy {

    @Override
    public WorldBuffer serialize(WorldOutputStream stream, WorldBuffer buffer) {
        try {
            CompoundMap map = new CompoundMap();
            map.put("maps", new ListTag<>("maps", TagType.TAG_COMPOUND, buffer.getWorldMaps()));

            CompoundTag mapsCompound = new CompoundTag("", map);

            byte[] mapArray = WorldUtils.serializeCompoundTag(mapsCompound);
            byte[] compressedMapArray = Zstd.compress(mapArray);

            stream.writeInt(compressedMapArray.length);
            stream.writeInt(mapArray.length);
            stream.writeBytes(compressedMapArray);
        }catch (IOException e){
            e.printStackTrace();
        }
        return buffer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public WorldBuffer deserialize(WorldInputStream stream, WorldBuffer buffer) throws IOException {
        byte[] compressedMapsTag = new byte[0];
        byte[] mapsTag = new byte[0];

        if (buffer.getVersion() >= 7) {
            int compressedMapsTagLength = stream.readInt();
            int mapsTagLength = stream.readInt();
            compressedMapsTag = new byte[compressedMapsTagLength];
            mapsTag = new byte[mapsTagLength];

            stream.read(compressedMapsTag);
        }

        Zstd.decompress(mapsTag, compressedMapsTag);

        // World Maps
        CompoundTag mapsCompound = WorldUtils.readCompoundTag(mapsTag);
        List<CompoundTag> mapList;

        if (mapsCompound != null) {
            mapList = (List<CompoundTag>) mapsCompound.getAsListTag("maps").map(ListTag::getValue).orElse(new ArrayList<>());
        } else {
            mapList = new ArrayList<>();
        }

        buffer.setWorldMaps(mapList);

        return buffer;
    }
}

package com.minepalm.manyworlds.bukkit.strategies;

import com.github.luben.zstd.Zstd;
import com.grinderwolf.swm.api.world.SlimeChunk;
import com.grinderwolf.swm.api.world.SlimeChunkSection;
import com.minepalm.manyworlds.api.bukkit.WorldStrategy;
import com.minepalm.manyworlds.api.util.WorldBuffer;
import com.minepalm.manyworlds.api.util.WorldInputStream;
import com.minepalm.manyworlds.api.util.WorldOutputStream;

import java.io.IOException;
import java.util.*;

public class WorldChunkStrategy implements WorldStrategy {
    @Override
    public WorldBuffer serialize(WorldOutputStream stream, WorldBuffer buffer) throws IOException {

        List<SlimeChunk> list;
        Map<Long, SlimeChunk> chunks = buffer.getChunks();

        synchronized (buffer.getChunks()) {
            list = new ArrayList<>(chunks.values());
        }

        buffer.setSortedChunks(list);
        list.sort(Comparator.comparingLong(chunk -> (long) chunk.getZ() * Integer.MAX_VALUE + (long) chunk.getX()));
        list.removeIf(chunk -> chunk == null || Arrays.stream(chunk.getSections()).allMatch(Objects::isNull)); // Remove empty chunks to save space

        // Lowest chunk coordinates
        int minX = list.stream().mapToInt(SlimeChunk::getX).min().orElse(0);
        int minZ = list.stream().mapToInt(SlimeChunk::getZ).min().orElse(0);
        int maxX = list.stream().mapToInt(SlimeChunk::getX).max().orElse(0);
        int maxZ = list.stream().mapToInt(SlimeChunk::getZ).max().orElse(0);

        stream.writeShort(minX);
        stream.writeShort(minZ);

        // Width and depth
        int width = maxX - minX + 1;
        int depth = maxZ - minZ + 1;

        stream.writeShort(width);
        stream.writeShort(depth);

        // Chunk Bitmask
        BitSet chunkBitset = new BitSet(width * depth);

        for (SlimeChunk chunk : list) {
            int bitsetIndex = (chunk.getZ() - minZ) * width + (chunk.getX() - minX);
            chunkBitset.set(bitsetIndex, true);
        }

        int chunkMaskSize = (int) Math.ceil((width * depth) / 8.0D);

        byte[] array = chunkBitset.toByteArray();
        stream.writeBytes(array);

        int chunkMaskPadding = chunkMaskSize - array.length;

        for (int i = 0; i < chunkMaskPadding; i++) {
            stream.writeInt(0);
        }

        byte[] chunkData = WorldUtils.serializeChunks(list, buffer.getWorldVersion());
        byte[] compressedChunkData = Zstd.compress(chunkData);

        stream.writeInt(compressedChunkData.length);
        stream.writeInt(chunkData.length);
        stream.writeBytes(compressedChunkData);

        return buffer;
    }

    @Override
    public WorldBuffer deserialize(WorldInputStream stream, WorldBuffer buffer) throws IOException{

        int minX = stream.readShort();
        int minZ = stream.readShort();
        int width = stream.readShort();
        int depth = stream.readShort();

        if (width <= 0 || depth <= 0) {
            throw new IllegalStateException("illegal size: width: "+width+" depth: "+depth);
        }

        int bitmaskSize = (int) Math.ceil((width * depth) / 8.0D);
        byte[] chunkBitmask = new byte[bitmaskSize];
        stream.read(chunkBitmask);
        BitSet chunkBitset = BitSet.valueOf(chunkBitmask);

        int compressedChunkDataLength = stream.readInt();
        int chunkDataLength = stream.readInt();
        byte[] compressedChunkData = new byte[compressedChunkDataLength];
        byte[] chunkData = new byte[chunkDataLength];

        stream.read(compressedChunkData);
        Zstd.decompress(chunkData, compressedChunkData);

        // Chunk deserialization
        Map<Long, SlimeChunk> chunks = WorldUtils.readChunks(buffer.getWorldVersion(), buffer.getVersion(), buffer.getName(), minX, minZ, width, depth, chunkBitset, chunkData);

        // v1_13 world format detection for old versions
        if (buffer.getVersion() == 0) {
            for (SlimeChunk chunk : chunks.values()) {
                for (SlimeChunkSection section : chunk.getSections()) {
                    if (section != null) {
                        buffer.setWorldVersion((byte) (section.getBlocks() == null ? 0x04 : 0x01));

                        break;
                    }
                }
            }
        }

        buffer.setChunks(chunks);

        return buffer;
    }


}

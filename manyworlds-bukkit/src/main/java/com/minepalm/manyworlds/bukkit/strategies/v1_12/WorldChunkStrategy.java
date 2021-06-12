package com.minepalm.manyworlds.bukkit.strategies.v1_12;

import com.github.luben.zstd.Zstd;
import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.world.SlimeChunk;
import com.grinderwolf.swm.api.world.SlimeChunkSection;
import com.minepalm.manyworlds.api.util.WorldBuffer;
import com.minepalm.manyworlds.api.bukkit.WorldStrategy;
import com.minepalm.manyworlds.api.util.WorldInputStream;
import com.minepalm.manyworlds.api.util.WorldOutputStream;

import java.io.IOException;
import java.util.BitSet;
import java.util.Map;

public class WorldChunkStrategy implements WorldStrategy {
    @Override
    public WorldBuffer serialize(WorldOutputStream stream, WorldBuffer buffer) throws IOException {

        // Lowest chunk coordinates
        int minX = buffer.getSortedChunks().stream().mapToInt(SlimeChunk::getX).min().orElse(0);
        int minZ = buffer.getSortedChunks().stream().mapToInt(SlimeChunk::getZ).min().orElse(0);
        int maxX = buffer.getSortedChunks().stream().mapToInt(SlimeChunk::getX).max().orElse(0);
        int maxZ = buffer.getSortedChunks().stream().mapToInt(SlimeChunk::getZ).max().orElse(0);

        stream.writeShort(minX);
        stream.writeShort(minZ);

        // Width and depth
        int width = maxX - minX + 1;
        int depth = maxZ - minZ + 1;

        stream.writeShort(width);
        stream.writeShort(depth);

        // Chunk Bitmask
        BitSet chunkBitset = new BitSet(width * depth);

        for (SlimeChunk chunk : buffer.getSortedChunks()) {
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

        byte[] chunkData = v1_12WorldUtils.serializeChunks(buffer.getSortedChunks(), buffer.getVersion());
        byte[] compressedChunkData = Zstd.compress(chunkData);

        stream.writeInt(compressedChunkData.length);
        stream.writeInt(chunkData.length);
        stream.writeBytes(compressedChunkData);

        return buffer;
    }

    @Override
    public WorldBuffer deserialize(WorldInputStream stream, WorldBuffer buffer) throws IOException {
        short minX = stream.readShort();
        short minZ = stream.readShort();
        int width = stream.readShort();
        int depth = stream.readShort();

        if (width <= 0 || depth <= 0) {
            //throw new CorruptedWorldException(buffer.getName());
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
        Map<Long, SlimeChunk> chunks = v1_12WorldUtils.readChunks(buffer.getWorldVersion(), buffer.getVersion(), buffer.getName(), minX, minZ, width, depth, chunkBitset, chunkData);

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

        return buffer;
    }


}

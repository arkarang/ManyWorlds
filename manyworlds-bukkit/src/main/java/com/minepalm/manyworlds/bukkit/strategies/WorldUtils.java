package com.minepalm.manyworlds.bukkit.strategies;

import com.grinderwolf.swm.api.utils.NibbleArray;
import com.grinderwolf.swm.api.world.SlimeChunk;
import com.grinderwolf.swm.api.world.SlimeChunkSection;
import com.grinderwolf.swm.internal.com.flowpowered.nbt.*;
import com.grinderwolf.swm.internal.com.flowpowered.nbt.stream.NBTInputStream;
import com.grinderwolf.swm.internal.com.flowpowered.nbt.stream.NBTOutputStream;
import com.grinderwolf.swm.nms.CraftSlimeChunk;
import com.grinderwolf.swm.nms.CraftSlimeChunkSection;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

//todo: 모듈 분리
public class WorldUtils {

    static void writeBitSetAsBytes(DataOutputStream outStream, BitSet set, int fixedSize) throws IOException {
        byte[] array = set.toByteArray();
        outStream.write(array);

        int chunkMaskPadding = fixedSize - array.length;

        for (int i = 0; i < chunkMaskPadding; i++) {
            outStream.write(0);
        }
    }

    public static byte[] serializeChunks(List<SlimeChunk> chunks, byte worldVersion) throws IOException {
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream(16384);
        DataOutputStream outStream = new DataOutputStream(outByteStream);

        for (SlimeChunk chunk : chunks) {
            // Height Maps
            if (worldVersion >= 0x04) {
                byte[] heightMaps = serializeCompoundTag(chunk.getHeightMaps());
                outStream.writeInt(heightMaps.length);
                outStream.write(heightMaps);
            } else {
                int[] heightMap = chunk.getHeightMaps().getIntArrayValue("heightMap").get();

                for (int i = 0; i < 256; i++) {
                    outStream.writeInt(heightMap[i]);
                }
            }

            // Biomes
            int[] biomes = chunk.getBiomes();
            if (worldVersion >= 0x04) {
                outStream.writeInt(biomes.length);
            }

            for (int biome : biomes) {
                outStream.writeInt(biome);
            }

            // Chunk sections
            SlimeChunkSection[] sections = chunk.getSections();
            BitSet sectionBitmask = new BitSet(16);

            for (int i = 0; i < sections.length; i++) {
                sectionBitmask.set(i, sections[i] != null);
            }

            writeBitSetAsBytes(outStream, sectionBitmask, 2);

            for (SlimeChunkSection section : sections) {
                if (section == null) {
                    continue;
                }

                // Block Light
                boolean hasBlockLight = section.getBlockLight() != null;
                outStream.writeBoolean(hasBlockLight);

                if (hasBlockLight) {
                    outStream.write(section.getBlockLight().getBacking());
                }

                // Block Data
                if (worldVersion >= 0x04) {
                    // Palette
                    List<CompoundTag> palette = section.getPalette().getValue();
                    outStream.writeInt(palette.size());

                    for (CompoundTag value : palette) {
                        byte[] serializedValue = serializeCompoundTag(value);

                        outStream.writeInt(serializedValue.length);
                        outStream.write(serializedValue);
                    }

                    // Block states
                    long[] blockStates = section.getBlockStates();

                    outStream.writeInt(blockStates.length);

                    for (long value : section.getBlockStates()) {
                        outStream.writeLong(value);
                    }
                } else {
                    outStream.write(section.getBlocks());
                    outStream.write(section.getData().getBacking());
                }

                // Sky Light
                boolean hasSkyLight = section.getSkyLight() != null;
                outStream.writeBoolean(hasSkyLight);

                if (hasSkyLight) {
                    outStream.write(section.getSkyLight().getBacking());
                }
            }
        }

        return outByteStream.toByteArray();
    }

    static byte[] serializeCompoundTag(CompoundTag tag) throws IOException {
        if (tag == null || tag.getValue().isEmpty()) {
            return new byte[0];
        }

        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        NBTOutputStream outStream = new NBTOutputStream(outByteStream, NBTInputStream.NO_COMPRESSION, ByteOrder.BIG_ENDIAN);
        outStream.writeTag(tag);

        return outByteStream.toByteArray();
    }

    static int floor(double num) {
        final int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }

    public static Map<Long, SlimeChunk> readChunks(byte worldVersion, int version, String worldName, int minX, int minZ, int width, int depth, BitSet chunkBitset, byte[] chunkData) throws IOException {
        //Bukkit.getLogger().warning("chunkdata: "+chunkData.length);
        ByteArrayInputStream bs = new ByteArrayInputStream(chunkData);
        DataInputStream dataStream = new DataInputStream(bs);
        Map<Long, SlimeChunk> chunkMap = new HashMap<>();

        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                int bitsetIndex = z * width + x;

                if (chunkBitset.get(bitsetIndex)) {
                    // Height Maps
                    CompoundTag heightMaps;

                    if (worldVersion >= 0x04) {
                        int heightMapsLength = dataStream.readInt();
                        byte[] heightMapsArray = new byte[heightMapsLength];
                        dataStream.read(heightMapsArray);
                        heightMaps = readCompoundTag(heightMapsArray);

                        // Height Maps might be null if empty
                        if (heightMaps == null) {
                            heightMaps = new CompoundTag("", new CompoundMap());
                        }
                    } else {
                        int[] heightMap = new int[256];

                        for (int i = 0; i < 256; i++) {
                            heightMap[i] = dataStream.readInt();
                        }

                        CompoundMap map = new CompoundMap();
                        map.put("heightMap", new IntArrayTag("heightMap", heightMap));

                        heightMaps = new CompoundTag("", map);
                    }

                    // Biome array
                    int[] biomes;

                    if (version == 8 && worldVersion < 0x04) {
                        // Patch the v8 bug: biome array size is wrong for old worlds
                        dataStream.readInt();
                    }

                    if (worldVersion >= 0x04) {
                        int biomesArrayLength = version >= 8 ? dataStream.readInt() : 256;
                        biomes = new int[biomesArrayLength];

                        for (int i = 0; i < biomes.length; i++) {
                            biomes[i] = dataStream.readInt();
                        }
                    } else {
                        byte[] byteBiomes = new byte[256];
                        dataStream.read(byteBiomes);
                        biomes = toIntArray(byteBiomes);
                    }

                    // Chunk Sections
                    SlimeChunkSection[] sections = readChunkSections(dataStream, worldVersion, version);

                    chunkMap.put(((long) minZ + z) * Integer.MAX_VALUE + ((long) minX + x), new CraftSlimeChunk(worldName,minX + x, minZ + z,
                            sections, heightMaps, biomes, new ArrayList<>(), new ArrayList<>()));
                }
            }
        }

        return chunkMap;
    }

    static int[] toIntArray(byte[] buf) {
        ByteBuffer buffer = ByteBuffer.wrap(buf).order(ByteOrder.BIG_ENDIAN);
        int[] ret = new int[buf.length / 4];

        buffer.asIntBuffer().get(ret);

        return ret;
    }

    private static SlimeChunkSection[] readChunkSections(DataInputStream dataStream, byte worldVersion, int version) throws IOException {
        SlimeChunkSection[] chunkSectionArray = new SlimeChunkSection[16];
        byte[] sectionBitmask = new byte[2];
        dataStream.read(sectionBitmask);
        BitSet sectionBitset = BitSet.valueOf(sectionBitmask);

        for (int i = 0; i < 16; i++) {
            if (sectionBitset.get(i)) {
                // Block Light Nibble Array
                NibbleArray blockLightArray;

                if (version < 5 || dataStream.readBoolean()) {
                    byte[] blockLightByteArray = new byte[2048];
                    dataStream.read(blockLightByteArray);
                    blockLightArray = new NibbleArray((blockLightByteArray));
                } else {
                    blockLightArray = null;
                }

                // Block data
                byte[] blockArray;
                NibbleArray dataArray;

                ListTag<CompoundTag> paletteTag;
                long[] blockStatesArray;

                // Post 1.13 block format
                if (worldVersion >= 0x04) {
                    // Palette
                    int paletteLength = dataStream.readInt();
                    List<CompoundTag> paletteList = new ArrayList<>(paletteLength);

                    for (int index = 0; index < paletteLength; index++) {
                        int tagLength = dataStream.readInt();
                        byte[] serializedTag = new byte[tagLength];
                        dataStream.read(serializedTag);

                        paletteList.add(readCompoundTag(serializedTag));
                    }

                    paletteTag = new ListTag<>("", TagType.TAG_COMPOUND, paletteList);

                    // Block states
                    int blockStatesArrayLength = dataStream.readInt();
                    blockStatesArray = new long[blockStatesArrayLength];

                    for (int index = 0; index < blockStatesArrayLength; index++) {
                        blockStatesArray[index] = dataStream.readLong();
                    }

                    blockArray = null;
                    dataArray = null;
                } else {
                    blockArray = new byte[4096];
                    dataStream.read(blockArray);

                    // Block Data Nibble Array
                    byte[] dataByteArray = new byte[2048];
                    dataStream.read(dataByteArray);
                    dataArray = new NibbleArray((dataByteArray));

                    paletteTag = null;
                    blockStatesArray = null;
                }

                // Sky Light Nibble Array
                NibbleArray skyLightArray;

                if (version < 5 || dataStream.readBoolean()) {
                    byte[] skyLightByteArray = new byte[2048];
                    dataStream.read(skyLightByteArray);
                    skyLightArray = new NibbleArray((skyLightByteArray));
                } else {
                    skyLightArray = null;
                }

                // HypixelBlocks 3
                if (version < 4) {
                    short hypixelBlocksLength = dataStream.readShort();
                    dataStream.skipBytes(hypixelBlocksLength);
                }

                chunkSectionArray[i] = new CraftSlimeChunkSection(blockArray, dataArray, paletteTag, blockStatesArray, blockLightArray, skyLightArray);
            }
        }

        return chunkSectionArray;
    }

    static CompoundTag readCompoundTag(byte[] serializedCompound) throws IOException {
        if (serializedCompound.length == 0) {
            return null;
        }

        NBTInputStream stream = new NBTInputStream(new ByteArrayInputStream(serializedCompound), NBTInputStream.NO_COMPRESSION, ByteOrder.BIG_ENDIAN);

        return (CompoundTag) stream.readTag();
    }

}

package com.minepalm.manyworlds.bukkit;

import com.flowpowered.nbt.*;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;
import com.github.luben.zstd.Zstd;
import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.utils.NibbleArray;
import com.grinderwolf.swm.api.utils.SlimeFormat;
import com.grinderwolf.swm.api.world.SlimeChunk;
import com.grinderwolf.swm.api.world.SlimeChunkSection;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.grinderwolf.swm.nms.CraftSlimeChunk;
import com.grinderwolf.swm.nms.CraftSlimeChunkSection;
import com.grinderwolf.swm.nms.CraftSlimeWorld;
import com.minepalm.manyworlds.api.bukkit.*;
import lombok.Getter;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ManyWorldFactory implements WorldFactory {

    AtomicBoolean workDone = new AtomicBoolean(false);
    final WorldDatabase database;

    @Getter
    private final WorldLoader worldLoader;
    private final PreparedWorld target;

    //factory variables
    private WrappedSlimeLoader slimeLoader;
    private Map<Long, SlimeChunk> chunks;
    private CompoundTag extraData;

    public ManyWorldFactory(Thread worker, WorldDatabase database, WorldLoader loader, PreparedWorld target){
        this.database = database;
        this.worldLoader = loader;
        this.target = target;
    }

    @Override
    public ManyWorld build() {
        return new CraftManyWorld(this);
    }

    @Override
    public SlimeLoader getSlimeLoader() {
        return new WrappedSlimeLoader(database);
    }

    @Override
    public String getName() {
        return target.getWorldInfo().getWorldName();
    }

    @Override
    public Map<Long, SlimeChunk> getChunks() {
        return null;
    }

    @Override
    public CompoundTag getExtraData() {
        return null;
    }

    @Override
    public List<CompoundTag> getWorldMaps() {
        return null;
    }

    @Override
    public byte getVersion() {
        return 0;
    }

    @Override
    public SlimePropertyMap getPropertyMap() {
        return null;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public boolean isLocked() {
        return false;
    }
}

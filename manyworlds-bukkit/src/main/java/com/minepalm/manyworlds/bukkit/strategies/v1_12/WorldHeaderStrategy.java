package com.minepalm.manyworlds.bukkit.strategies.v1_12;

import com.grinderwolf.swm.api.utils.SlimeFormat;
import com.minepalm.manyworlds.api.bukkit.WorldStrategy;
import com.minepalm.manyworlds.api.util.WorldBuffer;
import com.minepalm.manyworlds.api.util.WorldInputStream;
import com.minepalm.manyworlds.api.util.WorldOutputStream;

import java.io.IOException;
import java.util.Arrays;

public class WorldHeaderStrategy implements WorldStrategy {
    @Override
    public WorldBuffer serialize(WorldOutputStream stream, WorldBuffer buffer) throws IOException{

        // Store world properties
        buffer.getExtraData().getValue().put("properties", buffer.getPropertyMap().toCompound());

        // File Header and Slime version
        stream.writeBytes(SlimeFormat.SLIME_HEADER);
        stream.writeByte(SlimeFormat.SLIME_VERSION);

        // World version
        stream.writeByte(buffer.getWorldVersion());
        return buffer;
    }

    @Override
    public WorldBuffer deserialize(WorldInputStream stream, WorldBuffer buffer) throws IOException {
        byte[] fileHeader = new byte[SlimeFormat.SLIME_HEADER.length];
        stream.read(fileHeader);

        if (!Arrays.equals(SlimeFormat.SLIME_HEADER, fileHeader)) {
            throw new IllegalStateException("Header is corrupted");
        }

        // File version
        buffer.setVersion(stream.readByte());

        if (buffer.getVersion() > SlimeFormat.SLIME_VERSION) {
            throw new IllegalStateException("newer format found");
        }

        // World version
        byte worldVersion;

        if (buffer.getVersion() >= 6) {
            worldVersion = stream.readByte();
        } else if (buffer.getVersion() >= 4) { // In v4 there's just a boolean indicating whether the world is pre-1.13 or post-1.13
            worldVersion = (byte) (stream.readBoolean() ? 0x04 : 0x01);
        } else {
            worldVersion = 0; // We'll try to automatically detect it later
        }

        buffer.setWorldVersion(worldVersion);

        return buffer;
    }
}

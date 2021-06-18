package com.minepalm.manyworlds.api.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class WorldInputStream extends WorldStream{

    DataInputStream buffer;

    public WorldInputStream(byte[] bytes){
        buffer = new DataInputStream(new ByteArrayInputStream(bytes));
    }

    public byte[] read(byte[] toWrite) throws IOException {
        buffer.read(toWrite);
        return toWrite;
    }

    public int readInt() throws IOException {
        return buffer.readInt();
    }

    public byte readByte() throws IOException {
        return buffer.readByte();
    }

    public short readShort() throws IOException {
        return buffer.readShort();
    }

    public boolean readBoolean() throws IOException {
        return buffer.readBoolean();
    }
}

package com.minepalm.manyworlds.api.util;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WorldOutputStream extends WorldStream{

    ByteArrayOutputStream bytes;
    DataOutputStream buffer;

    public WorldOutputStream() {
        bytes = new ByteArrayOutputStream();
        buffer = new DataOutputStream(bytes);
    }


    public void writeBytes(byte[] bytes) throws IOException {
        buffer.write(bytes);
    }

    public void writeInt(int i) throws IOException {
        buffer.writeInt(i);
    }

    public void writeBoolean(boolean b) throws IOException {
        buffer.writeBoolean(b);
    }

    public void writeByte(byte b) throws IOException {
        buffer.writeByte(b);
    }

    public void writeShort(int s) throws IOException {
        buffer.writeShort(s);
    }

    public byte[] get(){
        return bytes.toByteArray();
    }
}

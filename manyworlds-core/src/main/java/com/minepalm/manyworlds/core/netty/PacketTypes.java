package com.minepalm.manyworlds.core.netty;

public enum PacketTypes {
    WORLD_CREATE(0),
    WORLD_LOAD_UNLOAD(1),
    SERVER_STATUS(2);

    final byte index;

    PacketTypes(int i){
        index = (byte)i;
    }

    public byte index(){
        return index;
    }
}

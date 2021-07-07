package com.minepalm.manyworlds.core;

import com.minepalm.manyworlds.api.bukkit.WorldType;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;

@EqualsAndHashCode
public class WorldToken implements WorldType {

    private static final HashMap<String, WorldToken> types = new HashMap<>();

    @Getter
    final int id;

    private WorldToken(){
        this.id = types.size();
    }

    public static WorldToken get(String str){
        if(!types.containsKey(str)) {
            synchronized (types) {
                if(!types.containsKey(str))
                    types.putIfAbsent(str, new WorldToken());
            }
        }
        return types.get(str);
    }

}

package com.minepalm.manyworlds.core;

import com.minepalm.manyworlds.api.bukkit.WorldType;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Locale;

@EqualsAndHashCode
public class WorldToken implements WorldType {

    private static final HashMap<String, WorldToken> types = new HashMap<>();

    @Getter
    final int id;
    @Getter
    final String name;

    private WorldToken(String str){
        this.id = types.size()+1;
        this.name = str;
    }

    public static WorldToken get(String str){
        if(!types.containsKey(str)) {
            synchronized (types) {
                if(!types.containsKey(str))
                    types.putIfAbsent(str.toUpperCase(Locale.ROOT), new WorldToken(str));
            }
        }
        return types.get(str.toUpperCase(Locale.ROOT));
    }

}

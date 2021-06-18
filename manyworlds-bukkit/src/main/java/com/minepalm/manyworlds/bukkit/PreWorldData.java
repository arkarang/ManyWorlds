package com.minepalm.manyworlds.bukkit;

import com.minepalm.manyworlds.api.bukkit.PreparedWorld;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.bukkit.WorldMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class PreWorldData implements PreparedWorld {

    @Setter
    WorldInfo worldInfo;

    final byte[] worldBytes;

    @Setter
    WorldMetadata metadata;

}

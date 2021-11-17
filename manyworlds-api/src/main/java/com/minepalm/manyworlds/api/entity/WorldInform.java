package com.minepalm.manyworlds.api.entity;

import com.minepalm.manyworlds.api.bukkit.WorldCategory;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@Data
@RequiredArgsConstructor
public class WorldInform {

    @NonNull
    final WorldCategory worldCategory;

    @NonNull
    private final String name;

}

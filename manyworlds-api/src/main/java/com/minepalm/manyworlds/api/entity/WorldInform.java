package com.minepalm.manyworlds.api.entity;

import com.minepalm.manyworlds.api.bukkit.WorldCategory;
import lombok.*;

@Getter
@Data
@ToString
@RequiredArgsConstructor
public class WorldInform {

    @NonNull
    final WorldCategory worldCategory;

    @NonNull
    private final String name;

}

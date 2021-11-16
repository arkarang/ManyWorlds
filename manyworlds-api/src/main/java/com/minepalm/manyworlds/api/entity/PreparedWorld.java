package com.minepalm.manyworlds.api.entity;

import com.minepalm.manyworlds.api.WorldProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Data
@AllArgsConstructor
public class PreparedWorld {

    @Setter
    WorldInform worldInform;

    final byte[] worldBytes;

    final WorldProperties properties;

}

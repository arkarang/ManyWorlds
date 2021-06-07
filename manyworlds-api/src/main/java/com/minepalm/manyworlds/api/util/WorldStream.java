package com.minepalm.manyworlds.api.util;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class WorldStream {

    final ByteBuf buffer;

}

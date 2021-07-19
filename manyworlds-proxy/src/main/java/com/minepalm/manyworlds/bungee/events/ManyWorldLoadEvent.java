package com.minepalm.manyworlds.bungee.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.plugin.Event;

@Getter
@RequiredArgsConstructor
public class ManyWorldLoadEvent extends Event {
    final String sampleName, worldName;
}

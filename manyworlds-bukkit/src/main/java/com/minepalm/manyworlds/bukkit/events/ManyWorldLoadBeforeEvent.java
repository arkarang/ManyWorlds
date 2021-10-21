package com.minepalm.manyworlds.bukkit.events;

import com.minepalm.manyworlds.api.bukkit.ManyWorld;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
@RequiredArgsConstructor
public class ManyWorldLoadBeforeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Getter
    @Setter
    public boolean cancelled = false;

    final WorldInfo worldInfo;
    final ManyWorld manyWorld;
}

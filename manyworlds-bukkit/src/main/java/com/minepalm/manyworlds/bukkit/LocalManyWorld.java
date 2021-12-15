package com.minepalm.manyworlds.bukkit;

import com.minepalm.manyworlds.api.ManyWorld;
import com.minepalm.manyworlds.api.WorldController;
import com.minepalm.manyworlds.api.WorldNetwork;
import com.minepalm.manyworlds.api.WorldRegistry;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.WorldInform;
import lombok.NonNull;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;

public class LocalManyWorld extends AbstractManyWorld {

    LocalManyWorld(@NonNull WorldInform info, WorldRegistry registry, WorldNetwork worldNetwork, WorldController controller) {
        super(info, registry, worldNetwork, controller);
    }

    @Override
    public CompletableFuture<Boolean> isLoaded() {
        return CompletableFuture.completedFuture(Bukkit.getWorld(info.getName()) != null);
    }

    @Override
    public CompletableFuture<ManyWorld> move(BukkitView view) {
        //todo: implements this
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public CompletableFuture<ManyWorld> copy(WorldInform info) {
        //todo: implements this
        throw new UnsupportedOperationException("not implemented");
    }

}

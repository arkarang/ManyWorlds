package com.minepalm.manyworlds.bukkit;

import com.minepalm.manyworlds.api.ManyWorld;
import com.minepalm.manyworlds.api.WorldController;
import com.minepalm.manyworlds.api.WorldNetwork;
import com.minepalm.manyworlds.api.bukkit.WorldCategory;
import com.minepalm.manyworlds.api.bukkit.WorldEntity;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class LocalManyWorld extends AbstractManyWorld {

    LocalManyWorld(@NonNull WorldInform info, WorldNetwork worldNetwork, WorldController controller) {
        super(info, worldNetwork, controller);
    }

    @Override
    public CompletableFuture<Boolean> isLoaded() {
        return CompletableFuture.completedFuture(Bukkit.getWorld(info.getName()) != null);
    }

    @Override
    public CompletableFuture<ServerView> move(BukkitView view) {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> unload() {
        return null;
    }

    @Override
    public CompletableFuture<ManyWorld> copy(WorldCategory type, String worldName) {
        return null;
    }
}

package com.minepalm.manyworlds.bukkit;

import com.minepalm.manyworlds.api.*;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.api.bukkit.WorldCategory;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.ServerView;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ProxiedManyWorld extends AbstractManyWorld {

    ProxiedManyWorld(@Nonnull WorldInform info, WorldRegistry registry, WorldNetwork worldNetwork, WorldController controller){
        super(info, registry, worldNetwork, controller);
    }

    @Override
    public CompletableFuture<ManyWorld> move(BukkitView view) {
        //todo: implements this
        throw new UnsupportedOperationException("not implemented");
        /*
        return worldNetwork.isWorldLoaded(this.info).thenCompose(isLoaded->{
            if(isLoaded){
                return controller.unload(this.info)
                        .thenCompose(completed->{
                            if(completed) {
                                return controller.load(view, this.info);
                            }else
                                return CompletableFuture.completedFuture(null);
                        })
                        .thenApply(mw->view);
            }else
                return controller.load(view, this.info).thenApply(mw->view);
        });

         */
    }

    @Override
    public CompletableFuture<ManyWorld> copy(WorldInform inform) {
        //todo: implements this
        throw new UnsupportedOperationException("not implemented");
    }
}

package com.minepalm.manyworlds.bungee;

import com.minepalm.hellobungee.api.HelloEveryone;
import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.WorldController;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.core.AbstractController;

import java.util.concurrent.CompletableFuture;

public class BukkitWorldController extends AbstractController {

    public BukkitWorldController(HelloEveryone network){
        super(network);
    }


}

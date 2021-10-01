package com.minepalm.manyworlds.bungee;

import com.minepalm.hellobungee.api.HelloEveryone;
import com.minepalm.manyworlds.api.netty.WorldController;
import com.minepalm.manyworlds.core.AbstractController;

public class BukkitWorldController extends AbstractController implements WorldController {

    public BukkitWorldController(HelloEveryone network){
        super(network);
    }

}

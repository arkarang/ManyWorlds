package com.minepalm.manyworlds.bukkit;

import com.minepalm.hellobungee.api.HelloEveryone;
import com.minepalm.manyworlds.api.netty.BungeeController;
import com.minepalm.manyworlds.core.AbstractController;

public class ProxyController extends AbstractController implements BungeeController {

    public ProxyController(HelloEveryone network){
        super(network);
    }

}

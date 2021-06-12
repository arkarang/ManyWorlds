package com.minepalm.manyworlds.bukkit;

import com.minepalm.hellobungee.api.HelloConnections;
import com.minepalm.manyworlds.core.AbstractController;
import com.minepalm.manyworlds.core.netty.PacketTypes;

public class BungeeController extends AbstractController implements com.minepalm.manyworlds.api.netty.BungeeController {

    public BungeeController(HelloConnections connections){
        super(connections);
        canExecutes.add(PacketTypes.SERVER_STATUS);
    }
}

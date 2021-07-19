package com.minepalm.manyworlds.bukkit;

import com.minepalm.hellobungee.api.HelloConnections;
import com.minepalm.manyworlds.api.netty.BungeeController;
import com.minepalm.manyworlds.core.AbstractController;
import com.minepalm.manyworlds.core.netty.PacketTypes;

public class ProxyController extends AbstractController implements BungeeController {

    public ProxyController(HelloConnections connections){
        super(connections);
        canExecutes.add(PacketTypes.SERVER_STATUS);
        canExecutes.add(PacketTypes.WORLD_LOAD_UNLOAD);
    }
}

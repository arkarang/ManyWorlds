package com.minepalm.manyworlds.bungee;

import com.minepalm.hellobungee.api.HelloConnections;
import com.minepalm.manyworlds.api.netty.WorldController;
import com.minepalm.manyworlds.core.AbstractController;
import com.minepalm.manyworlds.core.netty.PacketTypes;

public class BukkitWorldController extends AbstractController implements WorldController {

    public BukkitWorldController(HelloConnections con){
        super(con);
        canExecutes.add(PacketTypes.WORLD_CREATE);
        canExecutes.add(PacketTypes.WORLD_LOAD_UNLOAD);
    }

}

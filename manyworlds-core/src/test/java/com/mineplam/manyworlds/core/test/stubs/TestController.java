package com.mineplam.manyworlds.core.test.stubs;

import com.minepalm.hellobungee.api.HelloConnections;
import com.minepalm.manyworlds.core.AbstractController;
import com.minepalm.manyworlds.core.netty.PacketTypes;
import com.mineplam.manyworlds.core.test.NothingConnections;

public class TestController extends AbstractController {
    public TestController() {
        super(new NothingConnections());
        canExecutes.add(PacketTypes.WORLD_CREATE);
        canExecutes.add(PacketTypes.WORLD_LOAD_UNLOAD);
        canExecutes.add(PacketTypes.SERVER_STATUS);
    }
}

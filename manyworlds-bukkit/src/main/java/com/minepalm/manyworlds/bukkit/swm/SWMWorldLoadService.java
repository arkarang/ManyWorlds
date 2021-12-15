package com.minepalm.manyworlds.bukkit.swm;

import com.grinderwolf.swm.nms.SlimeNMS;
import com.minepalm.manyworlds.api.bukkit.WorldEntity;
import com.minepalm.manyworlds.api.bukkit.WorldFactory;
import com.minepalm.manyworlds.api.entity.PreparedWorld;
import com.minepalm.manyworlds.bukkit.AbstractWorldLoadService;

import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class SWMWorldLoadService extends AbstractWorldLoadService {

    private final SlimeNMS swm;

    public SWMWorldLoadService(ExecutorService service, SlimeNMS swm, Logger logger) {
        super(service, logger);
        this.swm = swm;
    }


    @Override
    protected WorldEntity finalizeWorldLoad(WorldFactory factory, WorldEntity entity) {
        return entity;
    }

    @Override
    protected PreparedWorld finalizeWorldUnload(WorldFactory factory, PreparedWorld preparedWorld) {
        return preparedWorld;
    }


}

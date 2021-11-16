package com.minepalm.manyworlds.core.netty;

import com.minepalm.hellobungee.api.CallbackTransformer;
import com.minepalm.manyworlds.api.ManyWorld;
import com.minepalm.manyworlds.api.WorldRegistry;
import lombok.RequiredArgsConstructor;

public class WorldCallbackAdapter{

    @RequiredArgsConstructor
    public static class Create implements CallbackTransformer<WorldCreatePacket, ManyWorld>{

        final WorldRegistry registry;

        @Override
        public String getIdentifier() {
            return WorldCreatePacket.class.getSimpleName();
        }

        @Override
        public ManyWorld transform(WorldCreatePacket worldCreatePacket) {
            return registry.getWorld(worldCreatePacket.getInform());
        }
    }

    @RequiredArgsConstructor
    public static class Update implements CallbackTransformer<WorldLoadPacket, ManyWorld>{

        final WorldRegistry registry;

        @Override
        public String getIdentifier() {
            return WorldLoadPacket.class.getSimpleName();
        }

        @Override
        public ManyWorld transform(WorldLoadPacket worldLoadPacket) {
            return registry.getWorld(worldLoadPacket.getInform());
        }
    }

}

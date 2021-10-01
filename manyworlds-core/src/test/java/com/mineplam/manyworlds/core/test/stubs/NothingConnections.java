package com.mineplam.manyworlds.core.test.stubs;

import com.minepalm.hellobungee.api.*;

import java.util.List;

public class NothingConnections implements HelloEveryone {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public HelloConnections getConnections() {
        return null;
    }

    @Override
    public HelloGateway getGateway() {
        return null;
    }

    @Override
    public CallbackService getCallbackService() {
        return null;
    }

    @Override
    public HelloHandler getHandler() {
        return null;
    }

    @Override
    public List<HelloSender> all() {
        return null;
    }

    @Override
    public HelloSender sender(String s) {
        return null;
    }
}

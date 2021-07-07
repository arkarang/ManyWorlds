package com.mineplam.manyworlds.core.test;

import com.minepalm.hellobungee.api.HelloClient;
import com.minepalm.hellobungee.api.HelloConnections;

import java.net.InetSocketAddress;

public class NothingConnections implements HelloConnections {
    @Override
    public HelloClient getClient(String s) {
        return null;
    }

    @Override
    public boolean isConnected(String s) {
        return false;
    }

    @Override
    public void establish(String s) {

    }

    @Override
    public void shutdown(String s) {

    }

    @Override
    public void registerServerInfo(String s, InetSocketAddress inetSocketAddress) {

    }

    @Override
    public InetSocketAddress getServerAddress(String s) {
        return null;
    }

    @Override
    public void establishAll() {

    }

    @Override
    public void goodbye() {

    }

    @Override
    public boolean isRegistered(InetSocketAddress inetSocketAddress) {
        return false;
    }

    @Override
    public HelloClient getClient(InetSocketAddress inetSocketAddress) {
        return null;
    }
}

package com.mineplam.manyworlds.core.test;

import com.minepalm.manyworlds.core.netty.*;
import com.minepalm.manyworlds.core.netty.exception.CorruptedPacketException;
import com.mineplam.manyworlds.core.test.stubs.TestController;
import com.mineplam.manyworlds.core.test.stubs.TestGlobalDatabase;
import io.netty.buffer.ByteBuf;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PacketTest {

    @Test
    public void test_01_controllerTest(){
        TestController controller = new TestController();
        Assert.assertTrue(controller.canExecute(PacketTypes.WORLD_CREATE.index()));
        Assert.assertTrue(controller.canExecute(PacketTypes.WORLD_LOAD_UNLOAD.index()));
        Assert.assertTrue(controller.canExecute(PacketTypes.SERVER_STATUS.index()));
    }

    @Test
    public void test_02_packetTest() throws CorruptedPacketException, ExecutionException, InterruptedException {
        PacketResolver resolver = new PacketResolver(Executors.newSingleThreadExecutor(), new TestGlobalDatabase());
        WorldLoadPacket packet = PacketFactory.newPacket(()->"test", ()->"test").createWorldLoad("test", "world", true);
        byte[] bytes = packet.serialize();

        Future<WorldLoadPacket> future = (Future<WorldLoadPacket>)resolver.resolve(bytes);
        packet = future.get();
        Assert.assertNotNull(packet);
        Assert.assertEquals(packet.getPacketID(), PacketTypes.WORLD_LOAD_UNLOAD.index());
        Assert.assertEquals(packet.getTo().getServerName(), "test");
        Assert.assertEquals(packet.getFrom().getServerName(), "test");
        Assert.assertEquals(packet.getWorldName(), "world");
        Assert.assertTrue(packet.isLoad());
    }
}

package com.mao.server;

import com.mao.TestUtils;
import com.mao.core.func.server.P698ServerService;
import com.mao.core.func.server.P698ServerServiceFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 698服务端服务类获取指定属性测试
 *
 * @author mao
 * @date 2023/8/28 15:15
 */
public class ServiceGetAttrTest {
    private static String host = "0.0.0.0";
    private static int port = 50000;
    private static P698ServerService p698ServerService;
    private final String meterAddress = "39 12 19 08 37 00";

    @BeforeAll
    static void init() throws InterruptedException {
        p698ServerService = P698ServerServiceFactory.createService(host, port);
        p698ServerService.setInvokeSupplier(() -> 0); //  用于测试，因为模拟的数据 invokeId 都是 0
        TestUtils.waitForConnection(p698ServerService);
    }

    @Test
    public void testPapR() throws InterruptedException {
        List<Double> papRList = p698ServerService.getPapR(meterAddress);
        TestUtils.assertDoubleEquals(132.88D, papRList.get(0));
        TestUtils.assertDoubleEquals(25.44D, papRList.get(1));
        TestUtils.assertDoubleEquals(22.96D, papRList.get(2));
        TestUtils.assertDoubleEquals(40.96D, papRList.get(3));
        TestUtils.assertDoubleEquals(43.52D, papRList.get(4));
        TestUtils.print("读取正向有功电能成功", papRList);
        assertNotNull(papRList);
        assertEquals(5, papRList.size());
    }

    @Test
    public void testRapR() throws InterruptedException {
        List<Double> rapRList = p698ServerService.getRapR(meterAddress);
        assertNotNull(rapRList);
        assertEquals(5, rapRList.size());
        TestUtils.assertDoubleEquals(132.88D, rapRList.get(0));
        TestUtils.assertDoubleEquals(25.44D, rapRList.get(1));
        TestUtils.assertDoubleEquals(22.96D, rapRList.get(2));
        TestUtils.assertDoubleEquals(40.96D, rapRList.get(3));
        TestUtils.assertDoubleEquals(43.52D, rapRList.get(4));
        TestUtils.print("读取反向有功电能成功", rapRList);
    }

    @Test
    public void testGetVoltage() throws InterruptedException {
        List<Double> papSList = p698ServerService.getVoltage(meterAddress);
        assertNotNull(papSList);
        assertEquals(3, papSList.size());
        TestUtils.assertDoubleEquals(229.27D, papSList.get(0));
        TestUtils.print("读取电压成功", papSList);
    }
}

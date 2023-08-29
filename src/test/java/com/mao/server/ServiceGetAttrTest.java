package com.mao.server;

import com.mao.core.func.server.P698ServerService;
import com.mao.core.func.server.P698ServerServiceFactory;
import com.mao.core.p698.AttrEnum;
import com.mao.core.p698.P698Resp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        Thread.sleep(10_000); // 等待客户机连接...
    }

    @Test
    public void testPapR() throws InterruptedException {
        List<Double> papRList = p698ServerService.getPapR(meterAddress);
        assertNotNull(papRList);
        assertEquals(5, papRList.size());
        System.out.println(papRList);
    }

    @Test
    public void testRapR() throws InterruptedException {
        List<Double> papSList = p698ServerService.getRapR(meterAddress);
        assertNotNull(papSList);
        assertEquals(5, papSList.size());
        System.out.println(papSList);
    }

    @Test
    public void testGetVoltage() throws InterruptedException {
        List<Integer> papSList = p698ServerService.getVoltage(meterAddress);
        assertNotNull(papSList);
        assertEquals(3, papSList.size());
        assertEquals(229, papSList.get(0));
        System.out.println(papSList);
    }
}

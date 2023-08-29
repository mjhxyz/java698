package com.mao.server;

import com.mao.TestUtils;
import com.mao.core.func.server.P698ServerService;
import com.mao.core.func.server.P698ServerServiceFactory;
import com.mao.core.p698.AttrEnum;
import com.mao.core.p698.P698Resp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 698服务端服务类测试GET
 *
 * @author mao
 * @date 2023/8/28 15:15
 */
public class ServiceGetTest {
    private static String host = "0.0.0.0";
    private static int port = 50000;
    private static P698ServerService p698ServerService;
    private String meterAddress = "39 12 19 08 37 00";

    @BeforeAll
    static void init() throws InterruptedException {
        p698ServerService = P698ServerServiceFactory.createService(host, port);
        p698ServerService.setInvokeSupplier(() -> 0); //  用于测试，因为模拟的数据 invokeId 都是 0
        TestUtils.waitForConnection(p698ServerService);
    }

    @Test
    public void testGetTwoAttr() throws InterruptedException {
        P698Resp p698Resp = p698ServerService.get(meterAddress, AttrEnum.P0020, AttrEnum.P0010);
        System.out.println(p698Resp);
    }

    @Test
    public void testGetOneAttr1() throws InterruptedException {
        P698Resp p698Resp = p698ServerService.get(
                meterAddress, AttrEnum.P0000
        );
        System.out.println(p698Resp);
    }

    @Test
    public void testGetOneAttr2() throws InterruptedException {
        P698Resp p698Resp = p698ServerService.get(
                meterAddress, AttrEnum.P0010
        );
        System.out.println(p698Resp);
    }

    @Test
    public void testGetOneAttr3() throws InterruptedException {
        P698Resp p698Resp = p698ServerService.get(
                meterAddress, AttrEnum.P0020
        );
        assertNotNull(p698Resp);
        System.out.println(p698Resp);
    }

    @Test
    public void testGetVoltage() throws InterruptedException {
        // 测试获取电压值
        P698Resp p698Resp = p698ServerService.get(
                meterAddress, AttrEnum.P2000
        );
        assertNotNull(p698Resp);
        System.out.println(p698Resp);
    }
}

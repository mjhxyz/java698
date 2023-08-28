package com.mao.client;

import com.mao.core.func.client.P698Service;
import com.mao.core.func.client.P698ServiceFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 698 服务测试类
 * @author mao
 * @date 2023/8/26 15:36
 */
public class P698ServiceTest {
    private static P698Service service;
    private static String meterAddress = "39 12 19 08 37 00";
    private static String host = "127.0.0.1";
    private static int port = 9876;
    @BeforeAll
    public static void init() {
        service = P698ServiceFactory.createService(host, port);
    }

    @Test
    public void testGetPapR() throws InterruptedException {
        List<Double> papR = service.getPapR(meterAddress);
        assertNotNull(papR);
        assertEquals(5, papR.size());
        System.out.println("获取的正向有功电能示值:" + papR);
    }

    @Test
    public void testGetRapR() throws InterruptedException {
        List<Double> rapR = service.getRapR(meterAddress);
        assertNotNull(rapR);
        assertEquals(5, rapR.size());
        System.out.println("获取的反向有功电能示值:" + rapR);
    }
}

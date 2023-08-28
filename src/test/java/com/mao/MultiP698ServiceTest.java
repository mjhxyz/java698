package com.mao;

import com.mao.core.func.P698Service;
import com.mao.core.func.P698ServiceFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 多个 698 服务同时使用测试类
 * @author mao
 * @date 2023/8/26 15:50
 */
public class MultiP698ServiceTest {

    private static String host = "127.0.0.1";
    private static int port = 9876;
    private static String meterAddress = "39 12 19 08 37 00";
    private static P698Service[] services;
    private static final int SERVICE_NUM = 6;

    private P698Service getRandomService() {
        return services[(int)(Math.random() * SERVICE_NUM)];
    }

    @BeforeAll
    static void init() {
        services = new P698Service[SERVICE_NUM];
        for(int i = 0; i < SERVICE_NUM; i++) {
            services[i] = P698ServiceFactory.createService(host, port);
        }
    }

    @Test
    public void test() throws InterruptedException {
        int tryCount = 10;
        for(int i = 0; i < tryCount; i++) {
            P698Service service = getRandomService();
            List<Double> papR = service.getPapR(meterAddress);
            assertNotNull(papR);
            assertEquals(5, papR.size());
            System.out.println("使用的服务:" + service);
        }
    }
}

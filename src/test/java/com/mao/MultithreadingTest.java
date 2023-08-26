package com.mao;

import com.mao.core.func.P698Service;
import com.mao.core.func.P698ServiceFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 多线程请求测试
 * @author mao
 * @date 2023/8/26 9:20
 */
public class MultithreadingTest {
    private static P698Service service;
    private static String meterAddress = "39 12 19 08 37 00";
    private static String host = "127.0.0.1";
    private static int port = 9876;

    @BeforeAll
    static void init() throws InterruptedException {
        try {
            service = P698ServiceFactory.createService(host, port);
        }catch (Exception e) {
            System.out.println("创建 P698Service 失败");
            fail(e);
        }
    }

    @Test
    void test() throws InterruptedException {
        Thread[] threads = new Thread[5];
        // 多个线程同时调用
        for (int i = 0; i < threads.length; i++) {
            Thread thread = new Thread(() -> {
                try {
                    List<Double> elec = service.getPapR(meterAddress);
                    assertNotNull(elec);
                    assertEquals(5, elec.size());
                } catch (Exception e) {
                    fail(e);
                }
            });
            thread.start();
            threads[i] = thread;
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }
}

package com.mao;

import com.mao.core.func.P698Service;
import com.mao.core.func.P698ServiceFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
        service = P698ServiceFactory.createService(host, port);
    }

    @Test
    void test() throws InterruptedException {
        // 20个线程同时调用
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    service.getElec(meterAddress).forEach(System.out::println);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        System.out.println("--------------------------------------------------");
        Thread.sleep(10_000);
    }
}

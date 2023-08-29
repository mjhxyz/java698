package com.mao;

import com.mao.core.func.server.P698ServerService;
import com.mao.core.func.server.P698ServerServiceFactory;
import org.opentest4j.AssertionFailedError;

import java.util.concurrent.CountDownLatch;

/**
 * 测试工具类
 * @author mao
 * @date 2023/8/29 14:07
 */
public class TestUtils {
    public static void print(String key, Object o) {
        System.out.println(key + " >> " + o);
    }

    public static void assertDoubleEquals(double a, double b) {
        if (Math.abs(a - b) > 0.00001) {
            throw new AssertionFailedError("assert failed: " + a + " != " + b);
        }
    }

    public static void waitForConnection(P698ServerService p698ServerService) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        p698ServerService.addConnectionListener((connection) -> {
            TestUtils.print("连接事件", connection);
            countDownLatch.countDown();
        });
        TestUtils.print("Test", "服务启动成功, 等待客户机连接...");
        countDownLatch.await();
    }
}


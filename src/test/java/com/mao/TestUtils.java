package com.mao;

import org.opentest4j.AssertionFailedError;

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
}


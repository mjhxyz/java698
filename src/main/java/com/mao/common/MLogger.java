package com.mao.common;

/**
 * 简单日志工具类, 调试用
 *
 * @author mao
 * @date 2023/8/25 16:27
 */
public class MLogger {
    private static boolean debug = true;
    public static void log(Object o) {
        if (debug) {
            System.out.println(o);
        }
    }

    public static void log(String format, Object... args) {
        System.out.println(String.format(format, args));
    }
}

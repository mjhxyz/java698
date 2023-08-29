package com.mao.common;

/**
 * 普通的工具类
 * @author mao
 * @date 2023/8/29 11:35
 */
public class Utils {
    private static final double EPSILON = 0.00001;
    /**
     * 判断两个 double 是否相等
     */
    public static boolean equalsDouble(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }
}

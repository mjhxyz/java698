package com.mao.common;

import java.util.List;

public class HexUtils {
    public static byte[] hexStringToBytes(String hexString) {
        return getBytes(hexString);
    }

    public static String bytesList2HexString(List<Byte> bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b)).append(" ");
        }
        // 去除最后一个空格
        if(hexString.length() > 0) {
            hexString.deleteCharAt(hexString.length() - 1);
        }
        return hexString.toString();
    }

    public static String bytes2HexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b)).append(" ");
        }
        // 去除最后一个空格
        if(hexString.length() > 0) {
            hexString.deleteCharAt(hexString.length() - 1);
        }
        return hexString.toString();
    }

    public static byte[] toB(String hexString) {
        return getBytes(hexString);
    }

    private static byte[] getBytes(String hexString) {
        String[] hexStrings = hexString.split(" ");
        byte[] bytes = new byte[hexStrings.length];
        for (int i = 0; i < hexStrings.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexStrings[i], 16);
        }
        return bytes;
    }

    // 合并两个byte数组
    public static byte[] mergeBytes(byte[] bytes1, byte[] bytes2) {
        byte[] bytes = new byte[bytes1.length + bytes2.length];
        System.arraycopy(bytes1, 0, bytes, 0, bytes1.length);
        System.arraycopy(bytes2, 0, bytes, bytes1.length, bytes2.length);
        return bytes;
    }

    // 小端转换
    public static int bytes2int(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value += (bytes[i] & 0xFF) << (8 * i);
        }
        return value;
    }

    // 整数小端转换为字节数组
    public static byte[] int2bytes(int value, int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) ((value >> (8 * i)) & 0xFF);
        }
        return bytes;
    }

    // 截取字节数组, 左闭右开
    public static byte[] subBytes(byte[] bytes, int start, int end) {
        byte[] subBytes = new byte[end - start];
        System.arraycopy(bytes, start, subBytes, 0, end - start);
        return subBytes;
    }

    public static byte[] subBytesList(List<Byte> bytes, int start, int end) {
        byte[] subBytes = new byte[end - start];
        for (int i = start; i < end; i++) {
            subBytes[i - start] = bytes.get(i);
        }
        return subBytes;
    }

    public static boolean bytesEquals(byte[] src, byte[] other) {
        if (src.length != other.length) {
            return false;
        }
        for (int i = 0; i < src.length; i++) {
            if (src[i] != other[i]) {
                return false;
            }
        }
        return true;
    }


    /**
     * 字节数组转 16 位正整数
     * @param bytes 字节数组
     * @return 无符号 long
     */
    public static int bytesTo16bitInt(byte[] bytes) {
        // 数据范围为 0…65535
        return (bytes[1] & 0xff) | ((bytes[0] & 0xff) << 8);
    }

    // 小端转换
    public static float bytes2float(byte[] bytes) {
        int l;
        l = bytes[0];
        l &= 0xff;
        l |= ((long) bytes[1] << 8);
        l &= 0xffff;
        l |= ((long) bytes[2] << 16);
        l &= 0xffffff;
        l |= ((long) bytes[3] << 24);
        return Float.intBitsToFloat(l);
    }
}

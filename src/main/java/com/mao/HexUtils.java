package com.mao;

public class HexUtils {
    public static byte[] hexStringToBytes(String hexString) {
        return getBytes(hexString);
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b)).append(" ");
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
}

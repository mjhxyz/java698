package com.mao;

public class HexUtils {
    public static byte[] hexStringToBytes(String hexString) {
        String[] hexStrings = hexString.split(" ");
        byte[] bytes = new byte[hexStrings.length];
        for (int i = 0; i < hexStrings.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexStrings[i], 16);
        }
        return bytes;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}

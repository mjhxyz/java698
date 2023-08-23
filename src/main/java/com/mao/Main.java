package com.mao;

public class Main {
    public static void main(String[] args) {
        String hexStr = "68 1C 00 43 05 39 12 19 08 37 00 0B 7A 52 05 02 05 02 00 10 02 00 00 20 02 00 00 8D 28 16";
        byte[] data = HexUtils.hexStringToBytes(hexStr);
        TCPClient client = new TCPClient("127.0.0.1", 9876);
        byte[] result = client.request(data);
        System.out.println(HexUtils.bytesToHexString(result));
    }
}
package com.mao;

import com.mao.conn.SampleTCPClient;
import com.mao.p698.AttrEnum;
import com.mao.p698.P698Utils;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
//        String hexStr = "68 1C 00 43 05 39 12 19 08 37 00 0B 7A 52 05 02 05 02 00 10 02 00 00 20 02 00 00 8D 28 16";
//        byte[] data = HexUtils.hexStringToBytes(hexStr);
//        SampleTCPClient client = new SampleTCPClient("127.0.0.1", 9876, 5000);
//        byte[] result = client.request(data);
//        System.out.println(HexUtils.bytesToHexString(result));

        P698Utils.P698MsgBuilder builder = P698Utils.getBuilder();
        builder.addAttr(AttrEnum.P0010).
                addAttr(AttrEnum.P0020);
        builder.setMeterAddress("39 12 19 08 37 00");
        byte[] data = builder.build();

        System.out.println(HexUtils.bytesToHexString(data));
    }
}
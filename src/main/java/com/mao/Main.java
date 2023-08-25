package com.mao;

import com.mao.conn.DataFuture;
import com.mao.conn.NettyClient;
import com.mao.p698.P698Rep;
import com.mao.p698.P698RepParser;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        // 获取两个属性
        String hexStr = "68 1C 00 43 05 39 12 19 08 37 00 0B 7A 52 05 02 05 02 00 10 02 00 00 20 02 00 00 8D 28 16";
        // 获取一个属性
//        hexStr = "68 17 00 43 05 39 12 19 08 37 00 0b 63 10 05 01 10 00 10 02 00 00 65 5f 16";
        // 获取三个属性
        hexStr = "68 20 00 43 05 39 12 19 08 37 00 0b 4a 23 05 02 1e 03 00 00 02 00 00 10 02 00 00 20 02 00 00 7f 08 16";
        // 读取错误的情况
        hexStr = "68 17 00 43 05 39 12 19 08 37 00 0b 63 10 05 01 30 20 00 02 00 00 35 79 16";

//        SampleTCPClient client = new SampleTCPClient("127.0.0.1", 9876, 5000);
//        byte[] result = client.request(data);
//        System.out.println(HexUtils.bytesToHexString(result));

//        P698Utils.P698MsgBuilder builder = P698Utils.getBuilder();
//        builder.addAttr(AttrEnum.P0010).
//                addAttr(AttrEnum.P0020);
//        builder.setMeterAddress("39 12 19 08 37 00");
//        byte[] data = builder.build();
//        System.out.println(HexUtils.bytesToHexString(data));

        byte[] data = HexUtils.hexStringToBytes(hexStr);
        NettyClient client = new NettyClient("127.0.0.1", 9876);
        DataFuture<byte[]> requestFuture = client.request(data);
        byte[] resp = requestFuture.get(5, TimeUnit.SECONDS);
        if(resp == null) {
            System.out.println("超时...");
        }
        System.out.println("主线程获取的结果:" + HexUtils.bytesToHexString(resp));

        // resp 转换为 LinkedList
        LinkedList<Byte> respList = new LinkedList<>();
        for (byte b : resp) {
            respList.add(b);
        }

        P698Rep rep = P698RepParser.parse(respList);
        System.out.println(rep);
    }
}
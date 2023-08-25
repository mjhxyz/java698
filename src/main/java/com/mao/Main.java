package com.mao;

import com.mao.common.HexUtils;
import com.mao.core.conn.DataFuture;
import com.mao.core.conn.NettyClient;
import com.mao.core.p698.AttrEnum;
import com.mao.core.p698.P698Resp;
import com.mao.core.p698.P698Utils;

import java.io.IOException;
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
//        hexStr = "68 17 00 43 05 39 12 19 08 37 00 0b 63 10 05 01 30 20 00 02 00 00 35 79 16";
//        byte[] data = HexUtils.hexStringToBytes(hexStr);

        // ==================== 构建请求报文 ====================
        P698Utils.P698MsgBuilder builder = P698Utils.getBuilder(); // builder 中有一个 invokeId
        builder.addAttr(AttrEnum.P0010).
                addAttr(AttrEnum.P0020);
        builder.setMeterAddress("39 12 19 08 37 00");
        P698Utils.P698Msg p698Msg = builder.build();
        System.out.println("构建的数据包:" + HexUtils.bytesToHexString(p698Msg.getRawData()));

        // ==================== 发送请求 ====================
        NettyClient client = new NettyClient("127.0.0.1", 9876);
        DataFuture<P698Resp> requestFuture = client.request(p698Msg);
        P698Resp resp = requestFuture.get(3, TimeUnit.SECONDS);
        if(resp == null) {
            System.out.println("超时...");
        }
        System.out.println("主线程获取的结果:" + resp);
    }
}
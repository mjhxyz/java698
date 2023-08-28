package com.mao.client;

import com.mao.common.HexUtils;
import com.mao.core.conn.client.DataFuture;
import com.mao.core.conn.client.NettyClient;
import com.mao.core.p698.AttrEnum;
import com.mao.core.p698.P698Resp;
import com.mao.core.p698.P698Utils;
import org.junit.jupiter.api.Test;

/**
 * 测试 TCP 发送 698 报文测试
 * @author mao
 * @date 2023/8/26 14:24
 */
public class RequestTest {
    private static final String host = "127.0.0.1";
    private static final int port = 9876;
    private static final String meterAddress = "39 12 19 08 37 00";

    @Test
    void test() throws InterruptedException {
        NettyClient client = new NettyClient(host, port);
        // ==================== 构建请求报文 ====================
        P698Utils.P698MsgBuilder builder = P698Utils.getBuilder(client.nextInvokeId());
        builder.addAttr(AttrEnum.P0010).
                addAttr(AttrEnum.P0020);
        builder.setMeterAddress(meterAddress);
        P698Utils.P698Msg p698Msg = builder.build();
        System.out.println("构建的数据包:" + HexUtils.bytes2HexString(p698Msg.getRawData()));

        // ==================== 发送请求 ====================
        DataFuture<P698Resp> requestFuture = client.request(p698Msg);
        P698Resp resp = requestFuture.get();
        if(resp == null) {
            System.out.println("超时...");
        }
        System.out.println("主线程获取的结果:" + resp);
    }
}

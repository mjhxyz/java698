package com.mao.core.func;

import com.mao.common.HexUtils;
import com.mao.core.conn.DataFuture;
import com.mao.core.conn.NettyClient;
import com.mao.core.p698.AttrEnum;
import com.mao.core.p698.P698Resp;
import com.mao.core.p698.P698Utils;

/**
 * 698 服务类
 * @author mao
 * @date 2023/8/25 16:09
 */
public class P698Service {
    private P698Utils.P698MsgBuilder builder;
    private NettyClient nettyClient;

    public P698Service(P698Utils.P698MsgBuilder builder, NettyClient nettyClient) {
        this.builder = builder;
        this.nettyClient = nettyClient;
    }

    /**
     * 读取电表指定的属性值
     * @param meterAddress 电表地址 eg: 39 12 19 08 37 00
     * @param attrs 电表属性列表
     * @return 电表响应对象
     */
    public P698Resp get(String meterAddress, AttrEnum... attrs) throws InterruptedException {
        for (AttrEnum attr : attrs) {
            builder.addAttr(attr);
        }
        builder.setMeterAddress(meterAddress);
        P698Utils.P698Msg p698Msg = builder.build();
        System.out.println("构建的数据包:" + HexUtils.bytesToHexString(p698Msg.getRawData()));
        DataFuture<P698Resp> request = this.nettyClient.request(p698Msg);
        return request.get();
    }
}

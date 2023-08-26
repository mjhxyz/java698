package com.mao.core.func;

import com.mao.common.HexUtils;
import com.mao.core.conn.DataFuture;
import com.mao.core.conn.NettyClient;
import com.mao.core.p698.AttrEnum;
import com.mao.core.p698.P698Attr;
import com.mao.core.p698.P698Resp;
import com.mao.core.p698.P698Utils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 698 服务类
 * @author mao
 * @date 2023/8/25 16:09
 */
public class P698Service {
    private NettyClient nettyClient;
    private static final long DEFAULT_TIMEOUT = 5000;
    private long timeout;

    public P698Service(NettyClient nettyClient, long timeout) {
        this.nettyClient = nettyClient;
        this.timeout = timeout;
    }

    public P698Service(NettyClient nettyClient) {
        this(nettyClient, DEFAULT_TIMEOUT);
    }

    /**
     * 读取电表指定的属性值
     * @param meterAddress 电表地址 eg: 39 12 19 08 37 00
     * @param attrs 电表属性列表
     * @return 电表响应对象
     */
    public P698Resp get(String meterAddress, AttrEnum... attrs) throws InterruptedException {
        P698Utils.P698MsgBuilder builder = P698Utils.getBuilder(nettyClient.nextInvokeId());
        for (AttrEnum attr : attrs) {
            builder.addAttr(attr);
        }
        builder.setMeterAddress(meterAddress);
        P698Utils.P698Msg p698Msg = builder.build();
        System.out.println("构建的数据包:" + HexUtils.bytes2HexString(p698Msg.getRawData()));
        DataFuture<P698Resp> request = this.nettyClient.request(p698Msg);
        return request.get(timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 读取正向有功电能
     *
     * @param meterAddress 电表地址 eg: 39 12 19 08 37 00
     * @return
     */
    public List<Double> getPapR(String meterAddress) throws InterruptedException {
        P698Resp resp = get(meterAddress, AttrEnum.P0010);
        if(resp == null) { // 超时
            throw new RuntimeException("超时...");
        }
        List<P698Attr> attrs = resp.getAttrs();
        for (P698Attr attr : attrs) {
            if (AttrEnum.getAttrEnum(attr.getOI()) == AttrEnum.P0010) {
                if(attr.isError()) {
                    throw new RuntimeException(attr.getError());
                }
                return (List<Double>)attr.getData();
            }
        }
        throw new RuntimeException("未知错误");
    }
}

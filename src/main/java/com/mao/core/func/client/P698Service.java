package com.mao.core.func.client;

import com.mao.common.HexUtils;
import com.mao.common.MLogger;
import com.mao.core.conn.client.DataFuture;
import com.mao.core.conn.client.NettyClient;
import com.mao.core.exception.P698TimeOutException;
import com.mao.core.p698.AttrEnum;
import com.mao.core.p698.P698Attr;
import com.mao.core.p698.P698Resp;
import com.mao.core.p698.P698Utils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 698 服务类
 *
 * @author mao
 * @date 2023/8/25 16:09
 */
public class P698Service {
    private int id; // 服务标志
    private NettyClient nettyClient;
    private static final long DEFAULT_TIMEOUT = 10_000;
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
     *
     * @param meterAddress 电表地址 eg: 39 12 19 08 37 00
     * @param attrs        电表属性列表
     * @return 电表响应对象
     */
    public P698Resp get(String meterAddress, AttrEnum... attrs) throws InterruptedException {
        P698Utils.P698MsgBuilder builder = P698Utils.getBuilder(nettyClient.nextInvokeId());
        for (AttrEnum attr : attrs) {
            builder.addAttr(attr);
        }
        builder.setMeterAddress(meterAddress);
        P698Utils.P698Msg p698Msg = builder.build();
        MLogger.log("构建的数据包:" + HexUtils.bytes2HexString(p698Msg.getRawData()));
        DataFuture<P698Resp> request = this.nettyClient.request(p698Msg);
        return request.get(timeout, TimeUnit.MILLISECONDS);
    }

    // 处理单个属性值
    public <T> T get(String meterAddress, Function<P698Attr, T> func, AttrEnum attrEnum) throws InterruptedException {
        P698Resp resp = get(meterAddress, attrEnum);
        if(resp == null) { // 超时
            throw new P698TimeOutException("读取属性超时 - " + attrEnum.getName());
        }
        for (P698Attr attr : resp.getAttrs()) {
            if (AttrEnum.getAttrEnum(attr.getOI()) == attrEnum) {
                if (attr.isError()) {
                    throw new RuntimeException(attr.getError());
                }
                return func.apply(attr);
            }
        }
        throw new RuntimeException("属性未找到");
    }

    /**
     * 读取正向有功电能
     *
     * @param meterAddress 电表地址 eg: 39 12 19 08 37 00
     */
    public List<Double> getPapR(String meterAddress) throws InterruptedException {
        return this.get(meterAddress, (attr) -> (List<Double>) attr.getData(), AttrEnum.P0010);
    }

    /**
     * 读取反向有功电能
     * @param meterAddress 电表地址 eg: 39 12 19 08 37 00
     */
    public List<Double> getRapR(String meterAddress) throws InterruptedException {
        return this.get(meterAddress, (attr) -> (List<Double>) attr.getData(), AttrEnum.P0020);
    }

    public int getId() {
        return id;
    }

    /**
     * 设置服务标志, 用于调试区分不同的服务
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "P698Service{" +
                "id=" + id +
                ", nettyClient=" + nettyClient +
                ", timeout=" + timeout +
                '}';
    }
}

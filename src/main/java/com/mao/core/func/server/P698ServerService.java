package com.mao.core.func.server;

import com.mao.common.HexUtils;
import com.mao.common.MLogger;
import com.mao.core.conn.client.DataFuture;
import com.mao.core.conn.server.P698Server;
import com.mao.core.exception.P698NoConnectionException;
import com.mao.core.exception.P698TimeOutException;
import com.mao.core.p698.AttrEnum;
import com.mao.core.p698.P698Attr;
import com.mao.core.p698.P698Resp;
import com.mao.core.p698.P698Utils;
import io.netty.channel.Channel;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 698服务端服务类
 *
 * @author mao
 * @date 2023/8/28 14:53
 */
public class P698ServerService {
    private final P698Server server;
    private long timeout = 5_000;

    private Supplier<Integer> invokeSupplier;

    /**
     * 添加连接监听器
     * @param listener 监听器
     */
    public P698ServerService addConnectionListener(Consumer<Channel> listener) {
        server.addConnectionListener(listener);
        return this;
    }

    public P698ServerService setInvokeSupplier(Supplier<Integer> invokeSupplier) {
        this.invokeSupplier = invokeSupplier;
        return this;
    }

    public P698ServerService(P698Server server) {
        this.server = server;
        this.invokeSupplier = server.invokeSupplier();
    }

    public P698ServerService(P698Server server, long timeout) {
        this.server = server;
        this.timeout = timeout;
    }

    /**
     * 读取电表指定的属性值
     *
     * @param meterAddress 电表地址 eg: 39 12 19 08 37 00
     * @param attrs        电表属性列表
     * @return 电表响应对象
     */
    public P698Resp get(String meterAddress, AttrEnum... attrs) throws InterruptedException {
        P698Utils.P698MsgBuilder builder = P698Utils.getBuilder(this.invokeSupplier);
        for (AttrEnum attr : attrs) {
            builder.addAttr(attr);
        }
        builder.setMeterAddress(meterAddress);
        P698Utils.P698Msg p698Msg = builder.build();
        MLogger.log("构建的数据包:" + HexUtils.bytes2HexString(p698Msg.getRawData()));
        List<DataFuture<P698Resp>> request = server.request(p698Msg);
        // TODO 可能有多个响应, 暂时先返回第一个
        if (request.size() > 0) {
            return request.get(0).get(timeout, TimeUnit.MILLISECONDS);
        }
        throw new P698NoConnectionException("没有客户端连接");
    }

    public <T> T get(String meterAddress, Function<P698Attr, T> func, AttrEnum attrEnum) throws InterruptedException {
        P698Resp resp = get(meterAddress, attrEnum);
        if (resp == null) { // 超时或者没有客户端连接
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
     * 读取电表反向有功电能
     *
     * @param meterAddress 电表地址 eg: 39 12 19 08 37 00
     * @return 反向有功电能
     * @throws InterruptedException
     */
    public List<Double> getRapR(String meterAddress) throws InterruptedException {
        return this.get(meterAddress,
                (attr) -> this.doScaleList(attr.getDataAsLongList(), -2),
                AttrEnum.P0020
        );
    }

    /**
     * 读取正向有功电能
     *
     * @param meterAddress 电表地址 eg: 39 12 19 08 37 00
     * @return 正向有功电能
     * @throws InterruptedException
     */
    public List<Double> getPapR(String meterAddress) throws InterruptedException {
        // 转换 = -2
        return this.get(meterAddress,
                (attr) -> this.doScaleList(attr.getDataAsLongList(), -2),
                AttrEnum.P0010
        );
    }

    /**
     * 获取电能表电压
     *
     * @param meterAddress 电表地址 eg: 39 12 19 08 37 00
     * @return 电压列表 length = 3
     */
    public List<Double> getVoltage(String meterAddress) throws InterruptedException {
        int scale = -1;
        return this.get(meterAddress, (e) -> this.doScaleList(e.getDataAsLongList(), scale), AttrEnum.P2000);
    }

    /**
     * 获取电能表电流
     * @param meterAddress 电表地址 eg: 39 12 19 08 37 00
     */
    public List<Double> getCurrentValue(String meterAddress) throws InterruptedException {
        int scale = -3;
        return this.get(meterAddress, (e) -> this.doScaleList(e.getDataAsLongList(), scale), AttrEnum.P2001);
    }

    /**
     * 电能表结果根据scale进行转换
     * @param list 电能表结果
     * @param scale 转换比例
     */
    private List<Double> doScaleList(List<Long> list, int scale){
        return list.stream().map((o) -> P698Utils.longParse2Double(o, scale)).collect(Collectors.toList());
    }
}

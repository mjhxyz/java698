package com.mao.core.conn.server;

import com.mao.common.HexUtils;
import com.mao.common.MLogger;
import com.mao.core.conn.client.DataFuture;
import com.mao.core.p698.P698RepParser;
import com.mao.core.p698.P698Resp;
import com.mao.core.p698.P698Utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 698 服务端处理器
 *
 * @author mao
 * @date 2023/8/28 11:16
 */
public class P698ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private Map<Integer, P698ServerHandler> map;
    private Map<String, DataFuture<P698Resp>> futureMap = new ConcurrentHashMap<>();
    private ChannelHandlerContext ctx;
    private Integer id;
    private AtomicInteger invokeId = new AtomicInteger(0);
    private LinkedList<Byte> buffer = new LinkedList<>();

    private Channel channel;

    public P698ServerHandler(
            Integer id,
            Map<Integer, P698ServerHandler> map) {
        this.map = map;
        this.id = id;
    }

    public Integer invokeSupplier() {
        return invokeId.getAndIncrement();
    }

    public DataFuture<P698Resp> request(P698Utils.P698Msg p698Msg) {
        int iid = p698Msg.getInvokeId();
        String futureKey = String.format("%s-%s", this.id, iid);
        DataFuture<P698Resp> future = new DataFuture<>();
        futureMap.put(futureKey, future);
        ctx.writeAndFlush(Unpooled.copiedBuffer(p698Msg.getRawData()));
        return future;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        map.put(id, this);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf data) throws Exception {
        byte[] bytes = new byte[data.readableBytes()];
        data.readBytes(bytes);
        // 解析获取 invokeId
        for (byte b : bytes) {
            buffer.add(b);
        }
        // List 转 byte[]
        byte[] cur = new byte[this.buffer.size()];
        for (int i = 0; i < this.buffer.size(); i++) {
            cur[i] = this.buffer.get(i);
        }
        MLogger.log("收到数据，开始解析..." + HexUtils.bytes2HexString(cur));
        P698Resp resp = P698RepParser.parse(buffer);
        if(resp == null) {
            MLogger.log("收到数据后，解析失败...");
            return; // 解析失败
        }
        String futureKey = String.format("%s-%s", this.id, resp.getInvokeId());
        MLogger.log("收到数据后，解析成功，futureKey:" + futureKey);
        if (futureMap.containsKey(futureKey)) {
            DataFuture<P698Resp> future = futureMap.get(futureKey);
            future.setResponse(resp);
            futureMap.remove(futureKey);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        map.remove(id);
    }
}

package com.mao.core.conn;

import com.mao.common.HexUtils;
import com.mao.common.MLogger;
import com.mao.core.p698.P698Resp;
import com.mao.core.p698.P698RepParser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 698 协议客户端处理器
 * @author mao
 * @date 2023/8/24 13:59
 */
public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final LinkedList<Byte> buffer = new LinkedList<>();

    private ConcurrentHashMap<Integer, DataFuture<P698Resp>> map;

    public ClientHandler(ConcurrentHashMap<Integer, DataFuture<P698Resp>> map) {
        this.map = map;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf data) throws Exception {
        byte[] bytes = new byte[data.readableBytes()];
        data.readBytes(bytes);
        for (byte b : bytes) {
            buffer.add(b);
        }
        MLogger.log("接收到新的数据:" + HexUtils.bytes2HexString(bytes));
        // MLogger.log("现在的 buffer:" + HexUtils.bytesList2HexString(buffer));
        // 这里就得做解析，拿到 invokeId 了
        P698Resp resp = P698RepParser.parse(buffer);
        if(resp == null) {
            MLogger.log("收到数据后，解析失败...");
            return;
        }
        int invokeId = resp.getInvokeId();
        DataFuture<P698Resp> future = map.get(invokeId);
        future.setResponse(resp);
    }
}

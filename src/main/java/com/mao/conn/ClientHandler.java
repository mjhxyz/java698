package com.mao.conn;

import com.mao.HexUtils;
import com.mao.p698.P698Rep;
import com.mao.p698.P698RepParser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mao
 * @date 2023/8/24 13:59
 */
public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private LinkedList<Byte> buffer = new LinkedList<>();

    private ConcurrentHashMap<Integer, DataFuture<P698Rep>> map;

    public ClientHandler(ConcurrentHashMap<Integer, DataFuture<P698Rep>> map) {
        this.map = map;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf data) throws Exception {
        byte[] bytes = new byte[data.readableBytes()];
        data.readBytes(bytes);
        System.out.println("接收到的数据:" + HexUtils.bytesToHexString(bytes));
        // 这里就得做解析，拿到 invokeId 了
        P698Rep resp = P698RepParser.parse(bytes);
        if(resp == null) {
            System.out.println("收到数据后，解析失败");
            return;
        }
        int invokeId = resp.getInvokeId();
        DataFuture<P698Rep> future = map.get(invokeId);
        System.out.println(future);
        future.setResponse(resp);
    }
}

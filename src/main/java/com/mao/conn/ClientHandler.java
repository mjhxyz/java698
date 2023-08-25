package com.mao.conn;

import com.mao.HexUtils;
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

    private ConcurrentHashMap<Integer, DataFuture<byte[]>> map;

    public ClientHandler(ConcurrentHashMap<Integer, DataFuture<byte[]>> map) {
        this.map = map;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf data) throws Exception {
        byte[] bytes = new byte[data.readableBytes()];
        data.readBytes(bytes);
        System.out.println("接收到的数据:" + HexUtils.bytesToHexString(bytes));

        int id = 1;
        System.out.println("触发 Future 回调");
        DataFuture<byte[]> future = map.get(id);
        future.setResponse(bytes);
    }
}

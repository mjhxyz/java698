package com.mao.conn;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mao
 * @date 2023/8/24 13:55
 */
public class NettyClient {
    private String host;
    private int port;
    private EventLoopGroup group;
    private Channel channel;
    private ChannelHandler handler;

    ConcurrentHashMap<Integer, DataFuture<byte[]>> map = new ConcurrentHashMap<>();

    public NettyClient(String host, int port) throws InterruptedException {
        this.host = host;
        this.port = port;
        this.handler = new ClientHandler(map);
        this.connect();
    }

    public DataFuture<byte[]> request(byte[] request) throws InterruptedException {
        if(channel == null || !channel.isActive()) {
            this.connect();
        }
        int id = 1;
        DataFuture<byte[]> future = new DataFuture<>();
        map.put(id, future);
        channel.writeAndFlush(Unpooled.copiedBuffer(request));
        return future;
    }

    public void connect() throws InterruptedException {
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group).
                channel(NioSocketChannel.class).
                handler(handler);
        this.channel = b.connect(host, port).sync().channel();
    }
}

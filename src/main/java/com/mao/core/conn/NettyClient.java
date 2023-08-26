package com.mao.core.conn;

import com.mao.core.p698.P698Resp;
import com.mao.core.p698.P698Utils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

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
    private int invokeId = 0; // 服务序号 0~63 还是放在 NettyClient 中吧
    ConcurrentHashMap<Integer, DataFuture<P698Resp>> map = new ConcurrentHashMap<>();

    public NettyClient(String host, int port) throws InterruptedException {
        this.host = host;
        this.port = port;
        this.handler = new ClientHandler(map);
        this.connect();
    }

    public DataFuture<P698Resp> request(P698Utils.P698Msg p698Msg) throws InterruptedException {
        if(channel == null || !channel.isActive()) {
            this.connect();
        }
        DataFuture<P698Resp> future = new DataFuture<>();
        map.put(p698Msg.getInvokeId(), future);
        channel.writeAndFlush(Unpooled.copiedBuffer(p698Msg.getRawData()));
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

    public Supplier<Integer> nextInvokeId() {
        return () -> {
            invokeId = (invokeId + 1) % 64;
            return invokeId;
        };
    }
}

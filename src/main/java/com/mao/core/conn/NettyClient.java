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
    private volatile int invokeId = 0; // 服务序号 0~63 还是放在 NettyClient 中吧
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
        // 发送数据，添加前导 FE FE FE FE
        byte[] bytes = p698Msg.getRawData();
        byte[] bytes2send = new byte[bytes.length + 4];
        System.arraycopy(bytes, 0, bytes2send, 4, bytes.length);
        bytes2send[0] = (byte) 0xFE;
        bytes2send[1] = (byte) 0xFE;
        bytes2send[2] = (byte) 0xFE;
        bytes2send[3] = (byte) 0xFE;
        channel.writeAndFlush(Unpooled.copiedBuffer(bytes2send));
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
            synchronized (this) {
                invokeId = (invokeId + 1) % 64;
                return invokeId;
            }
        };
    }

    @Override
    public String toString() {
        return "NettyClient{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", invokeId=" + invokeId +
                '}';
    }
}

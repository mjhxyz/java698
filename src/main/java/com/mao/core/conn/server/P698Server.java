package com.mao.core.conn.server;

import com.mao.core.conn.client.DataFuture;
import com.mao.core.p698.P698Resp;
import com.mao.core.p698.P698Utils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Handler;

/**
 * 698 TCP 服务
 *
 * @author mao
 * @date 2023/8/28 11:14
 */

public class P698Server {

    private final int port;
    private final String host;
    private Channel channel;
    private final Map<Integer, P698ServerHandler> map = new ConcurrentHashMap<>(); // 保存连接
    private final Map<String , DataFuture<byte[]>> futureMap = new ConcurrentHashMap<>(); // 保存请求 Future
    private final AtomicInteger channelId = new AtomicInteger(0);
    private final AtomicInteger invokeId = new AtomicInteger(0);
    private Executor executor = Executors.newSingleThreadExecutor();
    private List<Consumer<Channel>> connectionListeners = new ArrayList<>(); // 连接监听器

    public P698Server(String host, int port) {
        this.port = port;
        this.host = host;
    }

    public void addConnectionListener(Consumer<Channel> listener) {
        connectionListeners.add(listener);
    }

    public Supplier<Integer> invokeSupplier() {
        return invokeId::getAndIncrement;
    }

    public List<DataFuture<P698Resp>> request(P698Utils.P698Msg p698Msg) {
        List<DataFuture<P698Resp>> list = new ArrayList<>();
        for(Map.Entry<Integer, P698ServerHandler> entry : map.entrySet()) {
            P698ServerHandler handler = entry.getValue();
            DataFuture<P698Resp> future = handler.request(p698Msg);
            list.add(future);
        }
        return list;
    }

    // 创建一个线程启动服务
    public void start() {
        executor.execute(() -> {
            try {
                run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            P698Server that = this;
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(
                            new ChannelInitializer<Channel>() {
                                @Override
                                protected void initChannel(Channel ch) throws Exception {
                                    P698ServerHandler handler = new P698ServerHandler(
                                            that.channelId.getAndIncrement(),
                                            that.map);
                                    ch.pipeline().addLast(handler);
                                    try{
                                        connectionListeners.forEach(listener -> listener.accept(ch));
                                    } catch (Exception e) {
                                        // ignore listener exception
                                        // TODO log
                                    }
                                }
                            })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(host, port).sync();
            channel = f.channel();
            channel.closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

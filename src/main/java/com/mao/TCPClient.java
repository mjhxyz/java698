package com.mao;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TCPClient {
    private EventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;
    private BlockingQueue<byte[]> responseQueue = new LinkedBlockingQueue<>();

    private String host;
    private int port;

    static class ClientHandler extends ChannelInboundHandlerAdapter {
        BlockingQueue<byte[]> responseQueue;

        public ClientHandler(BlockingQueue<byte[]> responseQueue) {
            this.responseQueue = responseQueue;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ByteBuf result = (ByteBuf) msg;
            byte[] resultBytes = new byte[result.readableBytes()];
            result.readBytes(resultBytes);
            responseQueue.offer(resultBytes);
        }
    }

    public TCPClient(String host, int port){
        this.host = host;
        this.port = port;
        System.out.println("连接服务器：" + host + ":" + port);
        initConnection();
    }

    public byte[] request(byte[] request) {
        if(!channel.isActive()) {
            System.out.println("连接已断开，重新连接");
            initConnection();
        }
        System.out.println("发送数据：" + HexUtils.bytesToHexString(request));

        //请求报文
        ByteBuf byteBufMsg = Unpooled.buffer();
        byteBufMsg.writeBytes(request);
        channel.writeAndFlush(byteBufMsg);

        channel.writeAndFlush("hello");
        System.out.println("等待响应：");
        try {
            return responseQueue.take();
        } catch (InterruptedException e) {
            // TODO log
            return null;
        }
    }

    private void initConnection() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ClientHandler(responseQueue));
                    }
                });
        try {
            channel = bootstrap.connect(this.host, this.port).sync().channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.mao.core.func.client;

import com.mao.core.conn.client.NettyClient;
import com.mao.core.exception.P698ConnectException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 698 服务工厂
 * @author mao
 * @date 2023/8/25 16:17
 */
public class P698ServiceFactory {
    private static final AtomicInteger serviceId = new AtomicInteger(0);
    private static final long DEFAULT_TIMEOUT = 5_000;
    public static P698Service createService(String host, int port){
        return createService(host, port, DEFAULT_TIMEOUT);
    }

    public static P698Service createService(String host, int port, long timeout){
        try{
            NettyClient nettyClient = new NettyClient(host, port);
            P698Service p698Service = new P698Service(nettyClient);
            p698Service.setId(serviceId.incrementAndGet());
            return p698Service;
        }catch (Exception e){
            throw new P698ConnectException("初始化698服务失败", e);
        }
    }
}

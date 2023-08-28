package com.mao.core.func;

import com.mao.core.conn.NettyClient;
import com.mao.core.exception.P698ConnectException;
import com.mao.core.p698.P698Utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 698 服务工厂
 * @author mao
 * @date 2023/8/25 16:17
 */
public class P698ServiceFactory {
    private static volatile AtomicInteger serviceId = new AtomicInteger(0);
    public static P698Service createService(String host, int port){
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

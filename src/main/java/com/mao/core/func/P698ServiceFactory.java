package com.mao.core.func;

import com.mao.core.conn.NettyClient;
import com.mao.core.p698.P698Utils;

/**
 * 698 服务工厂
 * @author mao
 * @date 2023/8/25 16:17
 */
public class P698ServiceFactory {
    public static P698Service createService(String host, int port) throws InterruptedException {
        NettyClient nettyClient = new NettyClient(host, port);
        return new P698Service(nettyClient);
    }
}

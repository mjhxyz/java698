package com.mao.core.func.server;

import com.mao.core.conn.server.P698Server;

/**
 * 698服务端服务类工厂类
 * @author mao
 * @date 2023/8/28 15:03
 */
public class P698ServerServiceFactory {
    public static P698ServerService createService(String host, int port){
        return createService(host, port, 3000);
    }

    public static P698ServerService createService(String host, int port, int timeout){
        P698Server p698Server = new P698Server(host, port);
        P698ServerService p698ServerService = new P698ServerService(p698Server, timeout);
        p698Server.start();
        return p698ServerService;
    }
}

package com.mao.core.exception;

/**
 * 698 没有客户端连接
 * @author mao
 * @date 2023/8/29 8:41
 */
public class P698NoConnectionException extends P698BaseException{
    public P698NoConnectionException(String message) {
        super(message);
    }
}

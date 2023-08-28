package com.mao.core.exception;

/**
 * 698 连接异常
 * @author mao
 * @date 2023/8/26 15:15
 */
public class P698ConnectException extends P698BaseException{
    public P698ConnectException(String message) {
        super(message);
    }

    public P698ConnectException(String message, Exception e) {
        super(message, e);
    }
}

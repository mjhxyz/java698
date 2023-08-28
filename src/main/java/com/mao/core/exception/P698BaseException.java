package com.mao.core.exception;

/**
 * 698 异常基类
 * @author mao
 * @date 2023/8/26 14:46
 */
public class P698BaseException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public P698BaseException(String message) {
        super(message);
    }

    public P698BaseException(String message, Exception e) {
        super(message, e);
    }
}

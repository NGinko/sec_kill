package com.zhang.exception;


/**
 * 秒杀关闭异常
 */
public class SeckillCloseException extends SeckillException {


    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(Throwable cause) {
        super(cause);
    }
}

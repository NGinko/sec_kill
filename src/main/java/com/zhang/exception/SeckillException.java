package com.zhang.exception;

public class SeckillException extends RuntimeException {



    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(Throwable cause) {
        super(cause);
    }
}

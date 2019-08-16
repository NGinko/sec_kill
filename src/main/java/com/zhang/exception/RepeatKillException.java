package com.zhang.exception;


/**
 * 重复秒杀异常（运行期）
 */
public class RepeatKillException extends SeckillException {

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(Throwable cause) {
        super(cause);
    }
}

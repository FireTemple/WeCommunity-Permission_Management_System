package com.bohan.exception;

import com.bohan.exception.code.ResponseCodeInterface;

public class BusinessException extends RuntimeException{

    /**
     * 异常 code
     */

    private final int code;

    public final String defaultMessage;

    public BusinessException(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public BusinessException(ResponseCodeInterface code) {
        this(code.getCode(), code.getMsg());
    }

    public int getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}

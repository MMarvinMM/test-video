package com.chinatower.common.core.exception;

public class NodeNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    public NodeNotFoundException(String message)
    {
        this.message = message;
    }

    public NodeNotFoundException(String message, Integer code)
    {
        this.message = message;
        this.code = code;
    }

    public NodeNotFoundException(String message, Throwable e)
    {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage()
    {
        return message;
    }

    public Integer getCode()
    {
        return code;
    }
}

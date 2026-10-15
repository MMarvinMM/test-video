package com.chinatower.platform.project.numtwin.util;

/**
 * AES加解密专用异常，统一封装加解密错误。
 */
public class AesCryptoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AesCryptoException(String message) {
        super(message);
    }

    public AesCryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}

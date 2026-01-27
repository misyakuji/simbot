package com.miko.exception;

import lombok.Getter;

/**
 * API调用异常类
 * <p>
 * 封装API调用过程中可能出现的异常信息
 * </p>
 *
 * @author misyakuji
 * @since 2026-01-10
 */
@Getter
public class ApiException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误信息
     */
    private final String message;

    /**
     * 原始异常
     */
    private final Throwable cause;

    /**
     * 构造函数（仅包含错误信息）
     *
     * @param message 错误信息
     */
    public ApiException(String message) {
        super(message);
        this.code = null;
        this.message = message;
        this.cause = null;
    }

    /**
     * 构造函数（包含错误码和错误信息）
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public ApiException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.cause = null;
    }

    /**
     * 构造函数（包含错误信息和原始异常）
     *
     * @param message 错误信息
     * @param cause   原始异常
     */
    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.code = null;
        this.message = message;
        this.cause = cause;
    }

    /**
     * 构造函数（包含错误码、错误信息和原始异常）
     *
     * @param code    错误码
     * @param message 错误信息
     * @param cause   原始异常
     */
    public ApiException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "ApiException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", cause=" + cause +
                '}';
    }
}

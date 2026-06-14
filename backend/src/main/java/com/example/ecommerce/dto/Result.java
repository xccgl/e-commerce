package com.example.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 统一返回结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    // ==================== 成功响应 ====================

    /**
     * 成功（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null, LocalDateTime.now());
    }

    /**
     * 成功（有数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data, LocalDateTime.now());
    }

    /**
     * 成功（有数据和消息）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data, LocalDateTime.now());
    }

    // ==================== 失败响应 ====================

    /**
     * 失败
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null, LocalDateTime.now());
    }

    /**
     * 参数错误
     */
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message, null, LocalDateTime.now());
    }

    /**
     * 未授权
     */
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message, null, LocalDateTime.now());
    }

    /**
     * 禁止访问
     */
    public static <T> Result<T> forbidden(String message) {
        return new Result<>(403, message, null, LocalDateTime.now());
    }

    /**
     * 资源不存在
     */
    public static <T> Result<T> notFound(String message) {
        return new Result<>(404, message, null, LocalDateTime.now());
    }

    /**
     * 服务器内部错误
     */
    public static <T> Result<T> serverError(String message) {
        return new Result<>(500, message, null, LocalDateTime.now());
    }
}

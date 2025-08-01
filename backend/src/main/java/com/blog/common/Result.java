package com.blog.common;

import lombok.Data;

/**
 * 统一响应结果
 * 
 * @author blog
 */
@Data
public class Result<T> {
    
    private Integer code;
    private String message;
    private T data;
    
    private Result() {}
    
    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }
    
    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }
    
    /**
     * 成功响应（带消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    
    /**
     * 失败响应
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
    
    /**
     * 失败响应（带数据）
     */
    public static <T> Result<T> error(String message, T data) {
        return new Result<>(500, message, data);
    }
    
    /**
     * 未授权响应
     */
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message, null);
    }
    
    /**
     * 未找到响应
     */
    public static <T> Result<T> notFound(String message) {
        return new Result<>(404, message, null);
    }
}
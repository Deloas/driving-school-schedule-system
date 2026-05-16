package com.drivingschool.common.result;

import lombok.Data;

/**
 * 统一响应结果封装
 * <p>
 * 所有后端接口都使用此类包装返回数据，确保前端收到的 JSON 结构一致：
 * { "code": 200, "message": "操作成功", "data": {} }
 * </p>
 *
 * @param <T> data 字段的类型
 */
@Data
public class Result<T> {

    /** 业务状态码，200 表示成功 */
    private int code;

    /** 提示信息，中文 */
    private String message;

    /** 响应数据，可为 null */
    private T data;

    private Result() {}

    // ==================== 成功响应 ====================

    /**
     * 操作成功（无数据返回）
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "操作成功";
        return result;
    }

    /**
     * 操作成功（有数据返回）
     *
     * @param data 返回给前端的数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "操作成功";
        result.data = data;
        return result;
    }

    /**
     * 操作成功（自定义提示 + 数据）
     *
     * @param message 自定义成功提示
     * @param data    返回给前端的数据
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = message;
        result.data = data;
        return result;
    }

    // ==================== 失败响应 ====================

    /**
     * 操作失败（自定义状态码和提示）
     *
     * @param code    业务状态码
     * @param message 错误提示
     */
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        return result;
    }

    /**
     * 操作失败（默认 400）
     *
     * @param message 错误提示
     */
    public static <T> Result<T> error(String message) {
        return error(400, message);
    }
}

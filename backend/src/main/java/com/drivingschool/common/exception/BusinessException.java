package com.drivingschool.common.exception;

import lombok.Getter;

/**
 * 业务异常基类
 * <p>
 * 所有业务校验失败时抛出此类异常，由 GlobalExceptionHandler 统一捕获并返回中文错误提示。
 * 使用举例：排班已满、重复预约、车辆维修中等场景。
 * </p>
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 业务状态码，方便前端根据 code 做差异化处理 */
    private final int code;

    /**
     * @param code    业务状态码
     * @param message 中文错误提示
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 默认使用 400 状态码
     *
     * @param message 中文错误提示
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }
}

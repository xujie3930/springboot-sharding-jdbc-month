package com.demo.module.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {

    private int code;

    private String message;

    private T data;

    /**
     * 默认成功
     */
    public Result() {
        code = 200;
        message = "操作成功";
    }

    /**
     * @param i 代码
     * @param s 消息
     * @param o 响应对象
     */
    public Result(int i, String s, Object o) {
        code = i;
        message = s;
        data = (T) o;
    }

    /**
     * 成功的基础方法
     *
     * @return 成功的BaseResult对象
     */
    public static <T> Result<T> succeed() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 成功自定义消息
     *
     * @param message 自定义消息提示
     * @return 成功的BaseResult对象
     */
    public static <T> Result<T> succeed(String message) {
        return new Result<>(200, message, null);
    }

    /**
     * 成功自定义数据和消息
     *
     * @param data    自定义数据
     * @param message 自定义消息提示
     * @return 成功的BaseResult对象
     */
    public static <T> Result<T> succeed(T data, String message) {
        return new Result<>(200, message, data);
    }

    public static <T> Result<T> succeedWith(Integer code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    public static <T> Result<T> failed() {
        return failedWith(500, "操作失败", null);
    }

    public static <T> Result<T> failed(String message) {
        return failedWith(500, message, null);
    }

    public static <T> Result<T> failed(T model, String message) {
        return failedWith(500, message, model);
    }

    public static <T> Result<T> failedWith(int code, String message, T data) {
        return new Result<>(code, message, data);
    }

}

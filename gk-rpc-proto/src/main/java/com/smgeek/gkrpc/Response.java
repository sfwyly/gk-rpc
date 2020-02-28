package com.smgeek.gkrpc;

import lombok.Data;

/**
 * @ClassName Response
 * @Description 表示RPC的返回
 * @Author 逝风无言
 * @Data 2020/2/25 16:18
 * @Version 1.0
 **/
@Data
public class Response {

    /**
     * 服务返回编码，0-成功 ，非0失败
     */
    private int code;//成功与否
    /**
     * 具体的错误信息
     */
    private String message  = "ok";
    /**
     * 返回的数据
     */
    private Object data;
}

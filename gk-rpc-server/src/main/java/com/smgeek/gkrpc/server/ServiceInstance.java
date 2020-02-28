package com.smgeek.gkrpc.server;

import lombok.AllArgsConstructor;
import lombok.Data;


import java.lang.reflect.Method;

/**
 * @ClassName ServiceInstance
 * @Description 表示一个具体服务
 * @Author 逝风无言
 * @Data 2020/2/26 11:29
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
public class ServiceInstance {
    private Object target;
    private Method method;

}

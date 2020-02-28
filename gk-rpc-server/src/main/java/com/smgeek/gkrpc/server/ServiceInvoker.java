package com.smgeek.gkrpc.server;

import com.smgeek.gkrpc.Request;
import com.smgeek.gkrpc.utils.ReflectionUtils;

/**
 * @ClassName ServiceInvoker
 * @Description TODO
 * @Author 逝风无言
 * @Data 2020/2/26 13:36
 * @Version 1.0
 **/
public class ServiceInvoker {
    public Object invoke(ServiceInstance service, Request request){
        return ReflectionUtils.invoke(service.getTarget(),service.getMethod(),request.getParameters());
    }
}

package com.smgeek.gkrpc;

import lombok.Data;

/**
 * @ClassName Request
 * @Description TODO
 * @Author 逝风无言
 * @Data 2020/2/25 16:17
 * @Version 1.0
 **/
@Data
public class Request {
    private ServiceDescriptor serviceDescriptor;
    private Object [] parameters;
}

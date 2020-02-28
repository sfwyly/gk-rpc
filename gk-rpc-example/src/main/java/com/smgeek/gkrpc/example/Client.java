package com.smgeek.gkrpc.example;

import com.smgeek.gkrpc.client.RpcClient;
import com.smgeek.gkrpc.client.RpcClientConfig;

/**
 * @ClassName Client
 * @Description TODO
 * @Author 逝风无言
 * @Data 2020/2/26 18:11
 * @Version 1.0
 **/
public class Client {
    public static void main(String[] args) {
        RpcClient client = new RpcClient(new RpcClientConfig());
        CalcService service = client.getProxy(CalcService.class);

        int r1 = service.add(1,2);
        int r2 = service.minus(10,8);

        System.out.println(r1);
        System.out.println(r2);
    }
}

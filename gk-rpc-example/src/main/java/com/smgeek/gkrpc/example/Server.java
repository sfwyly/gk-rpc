package com.smgeek.gkrpc.example;

import com.smgeek.gkrpc.server.RPCServerConfig;
import com.smgeek.gkrpc.server.RpcServer;

/**
 * @ClassName Server
 * @Description TODO
 * @Author 逝风无言
 * @Data 2020/2/26 18:11
 * @Version 1.0
 **/
public class Server {
    public static void main(String[] args) {
        RpcServer server = new RpcServer(new RPCServerConfig());
        server.register(CalcService.class,new CalcServiceImpl());
        server.start();
    }
}

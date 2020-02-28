package com.smgeek.gkrpc.transport;

/**
 * @ClassName TransportServer
 * @Description 1.  启动监听端口 2，接收请求 3，关闭监听
 * @Author 逝风无言
 * @Data 2020/2/26 10:09
 * @Version 1.0
 **/
public interface TransportServer {
    void init(int port , RequestHandler handler);

    void start();

    void stop();
}

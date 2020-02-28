package com.smgeek.gkrpc.transport;

import com.smgeek.gkrpc.Peer;

import java.io.InputStream;

/**
 * @ClassName TransportClient
 * @Description 1.创建连接 2.发送数据，并且等待响应3，关闭连接
 * @Author 逝风无言
 * @Data 2020/2/26 10:06
 * @Version 1.0
 **/
public interface TransportClient {
    void connect(Peer peer);

    InputStream write(InputStream data);

    void close();
}

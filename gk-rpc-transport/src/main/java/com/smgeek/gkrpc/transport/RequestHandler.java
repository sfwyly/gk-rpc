package com.smgeek.gkrpc.transport;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @ClassName RequestHandler
 * @Description 处理网络氢气的handler
 * @Author 逝风无言
 * @Data 2020/2/26 10:10
 * @Version 1.0
 **/
public interface RequestHandler {
    void onRequest(InputStream recive, OutputStream toResp);
}

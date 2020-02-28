package com.smgeek.gkrpc;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName Peer
 * @Description 表示网络传输的一个端点
 * @Author 逝风无言
 * @Data 2020/2/25 16:13
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
public class Peer {

    private String host;
    private int port;
}

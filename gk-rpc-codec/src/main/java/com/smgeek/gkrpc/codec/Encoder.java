package com.smgeek.gkrpc.codec;

/**
 * @ClassName Encoder
 * @Description 序列化
 * @Author 逝风无言
 * @Data 2020/2/26 9:39
 * @Version 1.0
 **/
public interface Encoder {
    byte [] encode(Object obj);

}

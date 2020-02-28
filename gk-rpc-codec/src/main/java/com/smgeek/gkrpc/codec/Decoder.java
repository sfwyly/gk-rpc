package com.smgeek.gkrpc.codec;

/**
 * @ClassName Decoder
 * @Description 反序列化
 * @Author 逝风无言
 * @Data 2020/2/26 9:40
 * @Version 1.0
 **/
public interface Decoder {
    <T> T decode(byte [] bytes, Class<T> clazz);
}

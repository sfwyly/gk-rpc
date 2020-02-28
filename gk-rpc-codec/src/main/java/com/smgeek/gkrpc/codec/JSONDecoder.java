package com.smgeek.gkrpc.codec;

import com.alibaba.fastjson.JSON;

/**
 * @ClassName JSONDecoder
 * @Description TODO
 * @Author 逝风无言
 * @Data 2020/2/26 9:45
 * @Version 1.0
 **/
public class JSONDecoder implements Decoder {
    @Override
    public <T> T decode(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes,clazz);
    }
}

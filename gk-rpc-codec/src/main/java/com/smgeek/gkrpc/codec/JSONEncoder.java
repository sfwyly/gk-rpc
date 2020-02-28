package com.smgeek.gkrpc.codec;

import com.alibaba.fastjson.JSON;

/**
 * @ClassName JSONEncoder
 * @Description 基于json的序列化实现
 * @Author 逝风无言
 * @Data 2020/2/26 9:43
 * @Version 1.0
 **/
public class JSONEncoder implements Encoder{
    @Override
    public byte[] encode(Object obj) {
        return JSON.toJSONBytes(obj);
    }
}

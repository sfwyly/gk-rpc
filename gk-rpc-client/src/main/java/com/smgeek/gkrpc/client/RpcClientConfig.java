package com.smgeek.gkrpc.client;

import com.smgeek.gkrpc.Peer;
import com.smgeek.gkrpc.codec.Decoder;
import com.smgeek.gkrpc.codec.Encoder;
import com.smgeek.gkrpc.codec.JSONDecoder;
import com.smgeek.gkrpc.codec.JSONEncoder;
import com.smgeek.gkrpc.transport.HTTPTransportClient;
import com.smgeek.gkrpc.transport.HttpTransportServer;
import com.smgeek.gkrpc.transport.TransportClient;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName RpcClientConfig
 * @Description TODO
 * @Author 逝风无言
 * @Data 2020/2/26 14:58
 * @Version 1.0
 **/
@Data
public class RpcClientConfig {
    private Class<? extends TransportClient> transportClass=
            HTTPTransportClient.class;
    private Class<? extends Encoder> encoderClass = JSONEncoder.class;
    private Class<? extends Decoder> decoderClass = JSONDecoder.class;
    private Class<? extends TransportSelector> selectorClass =
            RandomTransportSelector.class;
    private int connectCount = 1;
    private List<Peer>servers = Arrays.asList(new Peer("127.0.0.1",3000));
}

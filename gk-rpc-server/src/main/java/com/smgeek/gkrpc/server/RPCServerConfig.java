package com.smgeek.gkrpc.server;

import com.smgeek.gkrpc.codec.Decoder;
import com.smgeek.gkrpc.codec.Encoder;
import com.smgeek.gkrpc.codec.JSONDecoder;
import com.smgeek.gkrpc.codec.JSONEncoder;
import com.smgeek.gkrpc.transport.HttpTransportServer;
import com.smgeek.gkrpc.transport.TransportServer;
import lombok.Data;

/**
 * @ClassName RPCServerConfig
 * @Description server配置
 * @Author 逝风无言
 * @Data 2020/2/26 11:23
 * @Version 1.0
 **/
@Data
public class RPCServerConfig {
    private Class<? extends TransportServer> transportClass= HttpTransportServer.class;
    private Class<? extends Encoder> encoderClass = JSONEncoder.class;
    private Class<? extends Decoder> decoderClass = JSONDecoder.class;

    private int port = 3000;

}

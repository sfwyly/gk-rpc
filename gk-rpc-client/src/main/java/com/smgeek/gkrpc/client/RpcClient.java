package com.smgeek.gkrpc.client;

import com.smgeek.gkrpc.codec.Decoder;
import com.smgeek.gkrpc.codec.Encoder;
import com.smgeek.gkrpc.utils.ReflectionUtils;

import java.lang.reflect.Proxy;

/**
 * @ClassName RpcClient
 * @Description TODO
 * @Author 逝风无言
 * @Data 2020/2/26 17:25
 * @Version 1.0
 **/
public class RpcClient {
    private RpcClientConfig config;
    private Encoder encoder;
    private Decoder decoder;
    private TransportSelector selector;

    public RpcClient(RpcClientConfig config) {
        this.config = config;
        this.encoder = ReflectionUtils.newInstance(this.config.getEncoderClass());
        this.decoder = ReflectionUtils.newInstance(this.config.getDecoderClass());
        this.selector = ReflectionUtils.newInstance(this.config.getSelectorClass());

        this.selector.init(this.config.getServers(),
                this.config.getConnectCount(),
                this.config.getTransportClass());

    }

    public RpcClient() {
        this(new RpcClientConfig());
    }

    public <T> T getProxy(Class<T> clazz){
        return (T)Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{clazz},
                new RemoteInvoker(clazz,encoder,decoder,selector)
        );
    }
}

package com.smgeek.gkrpc.client;

import com.smgeek.gkrpc.Request;
import com.smgeek.gkrpc.Response;
import com.smgeek.gkrpc.ServiceDescriptor;
import com.smgeek.gkrpc.codec.Decoder;
import com.smgeek.gkrpc.codec.Encoder;
import com.smgeek.gkrpc.transport.TransportClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @ClassName RemoteInvoker
 * @Description 调用远程服务的代理类
 * @Author 逝风无言
 * @Data 2020/2/26 17:37
 * @Version 1.0
 **/
@Slf4j
public class RemoteInvoker implements InvocationHandler{

    private Class clazz;
    private Encoder encoder;
    private Decoder decoder;
    private TransportSelector selector;

    public RemoteInvoker(Class clazz , Encoder encoder, Decoder decoder, TransportSelector selector) {
        this.clazz = clazz;
        this.decoder = decoder;
        this.encoder = encoder;
        this.selector = selector;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Request request = new Request();
        request.setServiceDescriptor(ServiceDescriptor.from(clazz,method));
        request.setParameters(args);

        Response response = invokeRemote(request);
        if(response==null || response.getCode()!=0){
            throw new IllegalStateException("fail to invoke remote: "+response);
        }

        return response.getData();
    }

    private Response invokeRemote(Request request){
        TransportClient client = null;
        Response response = null;
        try{
            client = selector.select();

            byte[] outBytes = encoder.encode(request);
            InputStream revice = client.write(new ByteArrayInputStream(outBytes));

            byte[] inBytes = new byte[revice.available()];
            IOUtils.readFully(revice,inBytes,0,revice.available());

           // byte[] inBytes = IOUtils.readFully(revice , revice.available());

             response = decoder.decode(inBytes,Response.class);

        }catch (IOException e) {
            log.warn(e.getMessage(),e);
            response = new Response();
            response.setCode(1);
            response.setMessage("RpcClient got error:"+e.getClass()+" : "+e.getMessage());
        }finally {
            if(client!=null){
                selector.release(client);
            }
        }
        return response;
    }
}

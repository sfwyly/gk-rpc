package com.smgeek.gkrpc.transport;

import com.smgeek.gkrpc.Peer;
import org.apache.commons.io.IOUtils;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @ClassName HTTPTransportClient
 * @Description TODO
 * @Author 逝风无言
 * @Data 2020/2/26 10:13
 * @Version 1.0
 **/
public class HTTPTransportClient implements  TransportClient{

    private String url;

    @Override
    public void connect(Peer peer) {
        this.url="http://"+peer.getHost()+":"+peer.getPort();
    }

    @Override
    public InputStream write(InputStream data) {

        try {
            HttpURLConnection httpConn = (HttpURLConnection) new URL(url).openConnection();
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            httpConn.setUseCaches(false);
            httpConn.setRequestMethod("POST");

            httpConn.connect();
            IOUtils.copy(data,httpConn.getOutputStream());

            int resultCode = httpConn.getResponseCode();
            if(resultCode==HttpURLConnection.HTTP_OK){
                return httpConn.getInputStream();
            }else{
                return httpConn.getErrorStream();
            }
        } catch (IOException e) {
            throw  new IllegalStateException(e);
        }

    }

    @Override
    public void close() {

    }
}

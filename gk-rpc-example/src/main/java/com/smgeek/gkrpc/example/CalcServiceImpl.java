package com.smgeek.gkrpc.example;

/**
 * @ClassName CalcServiceImpl
 * @Description TODO
 * @Author 逝风无言
 * @Data 2020/2/26 18:12
 * @Version 1.0
 **/
public class CalcServiceImpl implements CalcService {
    @Override
    public int add(int a, int b) {
        return a+b;
    }

    @Override
    public int minus(int a, int b) {
        return a-b;
    }
}

package com.smgeek.gkrpc.utils;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ReflectionUtilsTest {
    @Test
    public void newInstance() throws Exception {
        TestClass t =  ReflectionUtils.newInstance(TestClass.class);
        assertNotNull(t);
    }

    @Test
    public void getPublicMethods() throws Exception {
        Method[] methods = ReflectionUtils.getPublicMethods(TestClass.class);
        assertEquals(1,methods.length);

        String mname = methods[0].getName();
        assertEquals("b",mname);
    }

    @Test
    public void invoke() throws Exception {
        Method[] methods = ReflectionUtils.getPublicMethods(TestClass.class);
        Method method = methods[0];

        TestClass t = new TestClass();
        Object r = ReflectionUtils.invoke(t,method);

        assertEquals("b",r);
    }

}
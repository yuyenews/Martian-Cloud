package com.mars.cloud.request.feign.proxy;

import com.mars.cloud.annotation.MarsContentType;
import com.mars.cloud.annotation.MarsFeign;
import com.mars.cloud.annotation.enums.ContentType;
import com.mars.cloud.request.rest.request.MarsRestTemplate;
import com.mars.server.server.request.HttpMarsRequest;
import com.mars.server.server.request.HttpMarsResponse;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 用于实现RPC的代理类
 */
public class FeignProxy implements MethodInterceptor {

    private Enhancer enhancer;

    private Class<?> cls;

    /**
     * 获取代理对象
     * @param clazz  bean的class
     * @return 对象
     */
    public Object getProxy(Class<?> clazz) {
        cls = clazz;

        enhancer = new Enhancer();
        // 设置需要创建子类的类
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        // 通过字节码技术动态创建子类实例
        return enhancer.create();
    }


    /**
     * 绑定代理
     */
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

        MarsFeign marsFeign = cls.getAnnotation(MarsFeign.class);
        MarsContentType marsContentType = method.getAnnotation(MarsContentType.class);

        check(marsFeign,method);

        ContentType contentType = ContentType.FORM;
        if(marsContentType != null){
            contentType = marsContentType.ContentType();
        }

        return MarsRestTemplate.request(marsFeign.serverName(),method.getName(),args, method.getReturnType(), contentType);
    }

    /**
     * 校验配置是否符合规则
     * @param marsFeign 注解
     * @param method 方法
     * @throws Exception 异常
     */
    private void check(MarsFeign marsFeign, Method method) throws Exception {
        if(marsFeign.serverName() == null){
            throw new Exception("接口上MarsFeign注解的serverName为空:["+cls.getName()+"."+method.getName()+"]");
        }
        Class[] paramTypes = method.getParameterTypes();
        for(Class cls : paramTypes){
            if(cls.equals(HttpMarsRequest.class) || cls.equals(HttpMarsResponse.class)){
                throw new Exception("MarsCloud接口，不可以传递内置对象:["+cls.getName()+"."+method.getName()+"]");
            }
        }
    }
}

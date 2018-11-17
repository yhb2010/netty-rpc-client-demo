package com.demo.netty.rpc.client;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.demo.netty.rpc.api.HelloService;
import com.demo.netty.rpc.domain.Person;

public class HelloClient2 {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = context.getBean(RpcProxy.class);

        HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.hello(new Person("Yong", "Huang"));
        System.out.println(result);

        System.exit(0);
    }

}
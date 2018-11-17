package com.demo.netty;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.cdel.util.helper.SpringContextUtil;
import com.demo.netty.rpc.client.RpcProxy;
import com.demo.netty.rpc.registry.ServiceDiscovery;
import com.demo.netty.rpc.registry.zookeeper.ZooKeeperServiceDiscovery;

@SpringBootApplication
public class App {

	public static void main(String[] args) throws InterruptedException {
		SpringContextUtil.setApplicationContext(SpringApplication.run(App.class, args));
	}

	@Value("${rpc.address}")
	private String zkAddress;

	@Bean
	public ServiceDiscovery serviceDiscovery(){
		return new ZooKeeperServiceDiscovery(zkAddress);
	}

	@Bean
	public RpcProxy rpcProxy(){
		return new RpcProxy(serviceDiscovery());
	}

	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}

package com.demo.netty.rpc.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.cdeledu.domain.ServiceResult;
import com.demo.netty.rpc.api.HelloService;

@RestController
public class HelloClient {

	@Autowired
	private RpcProxy rpcProxy;
	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/test1")
    public ServiceResult<String> test1() {
        HelloService helloService = rpcProxy.create(HelloService.class);
        long l2 = System.currentTimeMillis();
        String result = helloService.hello("World");
        long l3 = System.currentTimeMillis();
        System.out.println("l3-l2: " + (l3 - l2));

        //HelloService helloService2 = rpcProxy.create(HelloService.class, "sample.hello2");
        //long l4 = System.currentTimeMillis();
        //String result2 = helloService2.hello("世界");
        //long l5 = System.currentTimeMillis();
        //System.out.println("l5-l4: " + (l5 - l4));

        return ServiceResult.getSuccessResult(result);
    }

	@GetMapping("/test2")
	public ServiceResult<String> test2() {
		ResponseEntity<ServiceResult> response = restTemplate.postForEntity("http://127.0.0.10:30123/save/test", null, ServiceResult.class);
		return response.getBody();
	}

}
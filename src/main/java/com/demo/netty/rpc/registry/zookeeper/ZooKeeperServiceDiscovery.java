package com.demo.netty.rpc.registry.zookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.util.stream.Collectors.toList;
import com.demo.netty.rpc.registry.ServiceDiscovery;
import com.demo.netty.util.CollectionUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 基于 ZooKeeper 的服务发现接口实现
 *
 * @since 1.0.0
 */
public class ZooKeeperServiceDiscovery implements ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperServiceDiscovery.class);
    private final LoadingCache<String, List<String>> serverListCache;

    private String zkAddress;

    public ZooKeeperServiceDiscovery(String zkAddress) {
        this.zkAddress = zkAddress;
        this.serverListCache = Caffeine.newBuilder()
   			 .maximumSize(100)
   			 .expireAfterWrite(1, TimeUnit.MINUTES)
   			 .build(name -> getServerList(name));
    }

    public List<String> getServerList(String name) {
    	// 创建 ZooKeeper 客户端
        ZkClient zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");
        try {
            // 获取 service 节点
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + name;
            if (!zkClient.exists(servicePath)) {
                throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath));
            }
            List<String> addressList = zkClient.getChildren(servicePath);
            if (CollectionUtil.isEmpty(addressList)) {
                throw new RuntimeException(String.format("can not find any address node on path: %s", servicePath));
            }
            if (CollectionUtil.isEmpty(addressList)) {
                throw new RuntimeException(String.format("can not find any address node on path: %s", name));
            }
            return addressList.stream().map(address -> {
            	String addressPath = servicePath + "/" + address;
            	return (String)zkClient.readData(addressPath);
            }).collect(toList());
        } finally {
            zkClient.close();
        }
    }

    @Override
    public String discover(String name) {
        List<String> addressList = serverListCache.get(name);
        if (CollectionUtil.isEmpty(addressList)) {
            throw new RuntimeException(String.format("can not find any address node on path: %s", name));
        }
        // 获取 address 节点
        String address;
        int size = addressList.size();
        if (size == 1) {
            // 若只有一个地址，则获取该地址
            address = addressList.get(0);
            LOGGER.debug("get only address node: {}", address);
        } else {
            // 若存在多个地址，则随机获取一个地址
            address = addressList.get(ThreadLocalRandom.current().nextInt(size));
            LOGGER.debug("get random address node: {}", address);
        }
        // 获取 address 节点的值
        return address;
    }

}
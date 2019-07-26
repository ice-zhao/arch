package com.xunwei.collectdata.utils;

import com.xunwei.collectdata.TopicFactory;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.io.File;
import java.io.IOException;

public class RedissonClientFactory {
    private static RedissonClient redissonClient;

    public static RedissonClient getRedissonClient() {
        if(null == redissonClient) {
            try {
                Config config = Config.fromJSON(new File("src/main/resources/config-redisson.json"));
                SingleServerConfig singleServerConfig = config.useSingleServer();
                singleServerConfig.setAddress(TopicFactory.getRedisServer());
                singleServerConfig.setPassword(TopicFactory.getRedisPass());
                redissonClient = Redisson.create(config);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return redissonClient;
    }
}

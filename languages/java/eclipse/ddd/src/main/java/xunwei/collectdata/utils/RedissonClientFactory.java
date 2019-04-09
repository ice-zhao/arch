package xunwei.collectdata.utils;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.File;

public class RedissonClientFactory {
    private static RedissonClient redissonClient;

    public static RedissonClient getRedissonClient() {
        if(null == redissonClient) {
            try {
                Config config = Config.fromJSON(new File("src/main/resources/config-redisson.json"));
                redissonClient = Redisson.create(config);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return redissonClient;
    }
}

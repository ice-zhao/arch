package com.xunwei.collectdata.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisClientFactory {
    //address of your redis server  192.168.1.201
    private static final String redisHost = "localhost";
//    private static final String redisHost = "192.168.1.201";
//    private static final String redisHost = "192.168.0.168";
    private static final Integer redisPort = 6379;

    //the jedis connection pool..
    private static JedisPool pool = null;
//    private static Jedis jedis = null;

    public static Jedis getJedisInstance() {
        if(pool == null) {
//            JedisPoolConfig config = new JedisPoolConfig();
//            config.setMaxActive(10);
//            config.setMaxIdle(2);
//            config.setMaxWait(50000);
//            //在获取Jedis连接时，自动检验连接是否可用
//            config.setTestOnBorrow(true);
//            //在将连接放回池中前，自动检验连接是否有效
//            config.setTestOnReturn(true);
//            //自动测试池中的空闲连接是否都是可用连接
//            config.setTestWhileIdle(true);
            pool = new JedisPool(redisHost, redisPort);
        }

        Jedis jedis = null;
        while (true) {
            try {
                jedis = pool.getResource();
                if(jedis != null) {
                    jedis.auth("xunwei");
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return jedis;
    }

    public static JedisPool getJedisPoolInstance() {
        if(pool == null)
            pool = new JedisPool(redisHost, redisPort);

        return pool;
    }

    public static void returnJedisInstance(Jedis jedisInst) {
        if(pool != null)
            pool.returnResource(jedisInst);
    }
}

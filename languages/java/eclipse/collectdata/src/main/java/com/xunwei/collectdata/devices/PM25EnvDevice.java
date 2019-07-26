package com.xunwei.collectdata.devices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.App;
import com.xunwei.collectdata.utils.JacksonFactory;
import com.xunwei.collectdata.utils.RedissonClientFactory;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.TimestampType;
import org.redisson.api.RKeys;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PM25EnvDevice extends AbsCommonData {
    private float co2;
    private float tvoc;     //挥发性气体
    private float ch2o;     //甲醛
    private float pm25;
    private float temperature;
    private float humidity;
    private float pm10;

    private HashMap<String, String> pm25Dev = new HashMap<String, String>();

    @Override
    public Boolean readData() {
        RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();

        try {
            App.semaphore.acquire();
            RKeys keys = redissonClient.getKeys();
            Iterable<String> allKeys = keys.getKeysByPattern("*:*:21:100");

            for(String item : allKeys) {
                RList<String> rList = redissonClient.getList(item);
                pm25Dev.put(item, rList.get(rList.size()-1));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            App.semaphore.release();
        }


        return true;
    }

    public Boolean storeData() {
        boolean result = true;
        if(pm25Dev.size() <= 0)
            return false;

        Session sess = App.getSession();
        ObjectMapper mapper = JacksonFactory.getObjectMapper();

        for (Map.Entry<String, String> me : pm25Dev.entrySet()) {
            try {
                String value = me.getValue();
                PM25EnvDevice pm25EnvDevice = mapper.readValue(value, PM25EnvDevice.class);
                //to persist alert.
                Query query = sess.createQuery("select 1 from PM25EnvDevice where time = :time");
                query.setParameter("time", pm25EnvDevice.getTime(), TimestampType.INSTANCE);
//                System.out.println("Humidity and temperature :  " + temperatureHumidity.getTime());

                List list = query.getResultList();
                if (list.isEmpty()) {
                    //get cloud side's devId
                    query = sess.createQuery("select id from Device where hostNo=:host_no and devNo=:dev_no");
                    query.setParameter("host_no", pm25EnvDevice.getHostNo());
                    query.setParameter("dev_no", pm25EnvDevice.getDevNo());
                    List<Integer> list1 = query.getResultList();
//                    System.out.println(temperatureHumidity.getHostNo()+"         "+temperatureHumidity.getDevNo() + "     "+ list1.size());
                    if(list1.size() > 0) {
                        pm25EnvDevice.setDevId(list1.get(0));
                        App.bePersistedObject(pm25EnvDevice);
                    }
                }
            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                result = false;
            }
        }

        sess.close();
        return result;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getCo2() {
        return co2;
    }

    public void setCo2(float co2) {
        this.co2 = co2;
    }

    public float getTvoc() {
        return tvoc;
    }

    public void setTvoc(float tvoc) {
        this.tvoc = tvoc;
    }

    public float getCh2o() {
        return ch2o;
    }

    public void setCh2o(float ch2o) {
        this.ch2o = ch2o;
    }

    public float getPm25() {
        return pm25;
    }

    public void setPm25(float pm25) {
        this.pm25 = pm25;
    }

    public float getPm10() {
        return pm10;
    }

    public void setPm10(float pm10) {
        this.pm10 = pm10;
    }
}

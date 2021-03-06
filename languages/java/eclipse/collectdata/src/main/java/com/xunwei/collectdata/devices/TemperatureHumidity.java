package com.xunwei.collectdata.devices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.App;
import com.xunwei.collectdata.DataProcessThread;
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


public class TemperatureHumidity extends AbsCommonData {
    private float temperature;
    private float humidity;

    private HashMap<String, String> tempHumidity = new HashMap<String, String>();

    public Boolean readData() {
        RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();

        try {
            App.semaphore.acquire();
            RKeys keys = redissonClient.getKeys();
            Iterable<String> allKeys = keys.getKeysByPattern("*:*:1:100");

            for(String item : allKeys) {
                RList<String> rList = redissonClient.getList(item);
                tempHumidity.put(item, rList.get(rList.size()-1));
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
        if(tempHumidity.size() <= 0)
            return false;

        Session sess = App.getSession();
        ObjectMapper mapper = JacksonFactory.getObjectMapper();

        for (Map.Entry<String, String> me : tempHumidity.entrySet()) {
            try {
                String value = me.getValue();
                TemperatureHumidity temperatureHumidity = mapper.readValue(value, TemperatureHumidity.class);
                //to persist alert.
                Query query = sess.createQuery("select 1 from TemperatureHumidity where time = :time");
                query.setParameter("time", temperatureHumidity.getTime(), TimestampType.INSTANCE);
//                System.out.println("Humidity and temperature :  " + temperatureHumidity.getTime());

                List list = query.getResultList();
                if (list.isEmpty()) {
                    //get cloud side's devId
                    query = sess.createQuery("select id from Device where hostNo=:host_no and devNo=:dev_no");
                    query.setParameter("host_no", temperatureHumidity.getHostNo());
                    query.setParameter("dev_no", temperatureHumidity.getDevNo());
                    List<Integer> list1 = query.getResultList();
//                    System.out.println(temperatureHumidity.getHostNo()+"         "+temperatureHumidity.getDevNo() + "     "+ list1.size());
                    if(list1.size() > 0) {
                        temperatureHumidity.setDevId(list1.get(0));
                        App.bePersistedObject(temperatureHumidity);
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

    @Override
    public Boolean cleanupData() {
//        tempHumidity.clear();
        return true;
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
}

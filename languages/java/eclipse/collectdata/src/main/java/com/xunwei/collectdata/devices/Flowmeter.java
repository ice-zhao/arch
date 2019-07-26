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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Flowmeter extends AbsCommonData {
    private float temperature;
    private float pressure;
    private float instantFlowRate;
    private float totalFlowRate;

    private HashMap<String, String> flowMeter = new HashMap<String, String>();

    @Override
    public Boolean readData() {
        RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();

        try {
            App.semaphore.acquire();
            RKeys keys = redissonClient.getKeys();
            Iterable<String> allKeys = keys.getKeysByPattern("*:*:108:100");
            Iterable<String> allKeys1 = keys.getKeysByPattern("*:*:11[12]:100");
            List<Iterable<String>> list = new LinkedList<Iterable<String>>();

            if(allKeys != null)
                ((LinkedList<Iterable<String>>) list).addFirst(allKeys);

            if(allKeys1 != null)
                ((LinkedList<Iterable<String>>) list).addFirst(allKeys1);

            for(Iterable<String> key : list) {
                for (String item : key) {
                    RList<String> rList = redissonClient.getList(item);
                    flowMeter.put(item, rList.get(rList.size() - 1));
                }
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
        if(flowMeter.size() <= 0)
            return false;

        Session sess = App.getSession();
        ObjectMapper mapper = JacksonFactory.getObjectMapper();

        for (Map.Entry<String, String> me : flowMeter.entrySet()) {
            try {
                String value = me.getValue();
                Flowmeter flowmeter = mapper.readValue(value, Flowmeter.class);
                //to persist alert.
                Query query = sess.createQuery("select 1 from Flowmeter where time = :time and " +
                        "devNo=:dev_no");
                query.setParameter("time", flowmeter.getTime(), TimestampType.INSTANCE);
                query.setParameter("dev_no", flowmeter.getDevNo());
//                System.out.println("Flowmeter :  " + flowmeter.getTime());

                List list = query.getResultList();
                if (list.isEmpty()) {
                    //get cloud side's devId
                    query = sess.createSQLQuery("select ID from t_sys_device where HostNo=:host_no and DevNo=:dev_no");
                    query.setParameter("host_no", flowmeter.getHostNo());
                    query.setParameter("dev_no", flowmeter.getDevNo());
                    List<Integer> list1 = query.getResultList();
//                    System.out.println(temperatureHumidity.getHostNo()+"         "+temperatureHumidity.getDevNo() + "     "+ list1.size());
                    if(list1.size() > 0) {
                        flowmeter.setDevId(list1.get(0));
                        App.bePersistedObject(flowmeter);
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

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getInstantFlowRate() {
        return instantFlowRate;
    }

    public void setInstantFlowRate(float instantFlowRate) {
        this.instantFlowRate = instantFlowRate;
    }

    public float getTotalFlowRate() {
        return totalFlowRate;
    }

    public void setTotalFlowRate(float totalFlowRate) {
        this.totalFlowRate = totalFlowRate;
    }
}

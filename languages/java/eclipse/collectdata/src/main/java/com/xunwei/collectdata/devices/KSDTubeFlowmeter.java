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


public class KSDTubeFlowmeter extends AbsCommonData {
    private float temperature;
    private float pressure;
    private float tube1InstantFlowRate;
    private float tube1TotalFlowRate;
    private float tube2InstantFlowRate;
    private float tube2TotalFlowRate;

    private HashMap<String, String> flowMeter = new HashMap<String, String>();

    @Override
    public Boolean readData() {
        RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();

        try {
            App.semaphore.acquire();
            RKeys keys = redissonClient.getKeys();
            Iterable<String> allKeys = keys.getKeysByPattern("*:*:109:100");

            for(String item : allKeys) {
                RList<String> rList = redissonClient.getList(item);
                flowMeter.put(item, rList.get(rList.size()-1));
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
                KSDTubeFlowmeter flowmeter = mapper.readValue(value, KSDTubeFlowmeter.class);
                //to persist alert.
                Query query = sess.createQuery("select 1 from KSDTubeFlowmeter where time = :time and " +
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

    public float getTube1InstantFlowRate() {
        return tube1InstantFlowRate;
    }

    public void setTube1InstantFlowRate(float tube1InstantFlowRate) {
        this.tube1InstantFlowRate = tube1InstantFlowRate;
    }

    public float getTube1TotalFlowRate() {
        return tube1TotalFlowRate;
    }

    public void setTube1TotalFlowRate(float tube1TotalFlowRate) {
        this.tube1TotalFlowRate = tube1TotalFlowRate;
    }

    public float getTube2InstantFlowRate() {
        return tube2InstantFlowRate;
    }

    public void setTube2InstantFlowRate(float tube2InstantFlowRate) {
        this.tube2InstantFlowRate = tube2InstantFlowRate;
    }

    public float getTube2TotalFlowRate() {
        return tube2TotalFlowRate;
    }

    public void setTube2TotalFlowRate(float tube2TotalFlowRate) {
        this.tube2TotalFlowRate = tube2TotalFlowRate;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof KSDTubeFlowmeter)) return false;
        if(this == obj) return true;

        KSDTubeFlowmeter flowmeter = (KSDTubeFlowmeter)obj;
        if(flowmeter.getTemperature() == this.temperature && flowmeter.getPressure() == this.pressure &&
            flowmeter.getTube1InstantFlowRate() == this.tube1InstantFlowRate &&
            flowmeter.getTube1TotalFlowRate() == this.tube1TotalFlowRate &&
            flowmeter.getTube2InstantFlowRate() == this.tube2InstantFlowRate &&
            flowmeter.getTube2TotalFlowRate() == this.tube2TotalFlowRate)
            return true;

        return false;
    }
}

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

public class HRLC80Flowmeter extends AbsCommonData {
    private float instantFlowRate;
    private float totalFlowRate;

    private HashMap<String, String> flowMeter = new HashMap<String, String>();

    @Override
    public Boolean readData() {
        RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();

        try {
            App.semaphore.acquire();
            RKeys keys = redissonClient.getKeys();
            Iterable<String> allKeys = keys.getKeysByPattern("*:*:110:100");

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
                HRLC80Flowmeter flowmeter = mapper.readValue(value, HRLC80Flowmeter.class);
                //to persist alert.
                Query query = sess.createQuery("select 1 from HRLC80Flowmeter where time = :time and " +
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

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof HRLC80Flowmeter)) return false;
        if(this == obj) return true;

        HRLC80Flowmeter flowmeter = (HRLC80Flowmeter)obj;
        if(flowmeter.getInstantFlowRate() == this.instantFlowRate &&
            flowmeter.getTotalFlowRate() == this.totalFlowRate)
            return true;

        return false;
    }
}

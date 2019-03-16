package com.xunwei.collectdata.devices;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.xunwei.collectdata.utils.JacksonFactory;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.TimestampType;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.AbsDataProcess;
import com.xunwei.collectdata.App;
import com.xunwei.collectdata.utils.RedissonClientFactory;

public class GasData extends AbsDataProcess {
	private int id;
	private float Consumption;
	private float Pressure;
	private HashMap<String, String> gasData = new HashMap<String, String>();
	
    public Boolean readData() {
		RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();
		RKeys keys = redissonClient.getKeys();
		Iterable<String> allKeys = keys.getKeysByPattern("*:*:3:100");
		
		for(String item : allKeys) {
//			RBucket<String> rbucket = redissonClient.getBucket(item);
			RList<String> rList = redissonClient.getList(item);
			gasData.put(item, rList.get(0));
		}
        return true;
    }

    public Boolean processData() {
        return true;
    }

    public Boolean storeData() {
		boolean result = true;
		Session sess = App.getSession();
		ObjectMapper mapper = JacksonFactory.getObjectMapper();
		
        for (Entry<String, String> me : gasData.entrySet()) {
            try {
            	String value = me.getValue();
            	GasData gas = mapper.readValue(value, GasData.class);
                //to persist alert.
				Query query = sess.createQuery("select 1 from GasData where timestamp = :time");
				query.setParameter("time", gas.getTimestamp(), TimestampType.INSTANCE);
				
				List list = query.getResultList();
                if (list.isEmpty()) {
                    App.bePersistedObject(gas);
                }
            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                result = false;
            } finally {
                sess.close();
            }
        }
		
		return result;
    }

    public Boolean cleanupData() {
        return true;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getConsumption() {
		return Consumption;
	}

	public void setConsumption(float consumption) {
		Consumption = consumption;
	}

	public float getPressure() {
		return Pressure;
	}

	public void setPressure(float pressure) {
		Pressure = pressure;
	}

}

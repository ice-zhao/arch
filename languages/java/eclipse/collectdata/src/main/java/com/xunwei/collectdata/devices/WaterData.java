package com.xunwei.collectdata.devices;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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

public class WaterData extends AbsDataProcess {
	private int id;
	private float consumption;
	private String quality;
	private float pressure;
	private float speed;
	private HashMap<String, String> waterData = new HashMap<String, String>();
	
    public Boolean readData() {
		RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();
		RKeys keys = redissonClient.getKeys();
		Iterable<String> allKeys = keys.getKeysByPattern("*:*:2:100");
		
		for(String item : allKeys) {
//			RBucket<String> rbucket = redissonClient.getBucket(item);
			RList<String> rList = redissonClient.getList(item);
			waterData.put(item, rList.get(0));
		}
        return true;
    }

    public Boolean processData() {
        return true;
    }

    public Boolean storeData() {
		boolean result = true;
		Session sess = App.getSession();
		ObjectMapper mapper = new ObjectMapper();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mapper.setDateFormat(simpleDateFormat);

        for (Entry<String, String> me : waterData.entrySet()) {
            try {
            	String value = me.getValue();
            	WaterData water = mapper.readValue(value, WaterData.class);
                //to persist alert.
				Query query = sess.createQuery("select 1 from WaterData where StartTime = :time");
				query.setParameter("time", water.getTimestamp(), TimestampType.INSTANCE);
				
				List list = query.getResultList();
                if (list.isEmpty()) {
                    App.bePersistedObject(water);
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
		return consumption;
	}

	public void setConsumption(float consumption) {
		this.consumption = consumption;
	}

	public String getQuality() {
		return quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	public float getPressure() {
		return pressure;
	}

	public void setPressure(float pressure) {
		this.pressure = pressure;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
}

package com.xunwei.collectdata.alert;

import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Map.Entry;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.TimestampType;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.App;
import com.xunwei.collectdata.devices.Host;
import com.xunwei.collectdata.utils.JacksonFactory;
import com.xunwei.collectdata.utils.RedissonClientFactory;

public class SysAlert extends AbsAlert {
	private int id;
	private Date endTime;
	private int alertLevel;
	private HashMap<String, String> alertData = new HashMap<String, String>();

	public Boolean readData() {
		RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();
		RKeys keys = redissonClient.getKeys();
		Iterable<String> allKeys = keys.getKeysByPattern("*:*:*:110");
		
		for(String item : allKeys) {
			RBucket<String> rbucket = redissonClient.getBucket(item);
			alertData.put(item, rbucket.get());
		}
		
		return true;
	}

	public Boolean processData() {
		// TODO Auto-generated method stub
		return true;
	}

	public Boolean storeData() {
		boolean result = true;
		Session sess = App.getSession();
    	ObjectMapper mapper = new ObjectMapper();

        for (Entry<String, String> me : alertData.entrySet()) {
            try {
            	String value = me.getValue();
            	SysAlert sysAlert = mapper.readValue(value, SysAlert.class);
                //to persist alert.
				Query query = sess.createQuery("select 1 from SysAlert where timestamp = :time");
				query.setParameter("time", sysAlert.getTimestamp(), TimestampType.INSTANCE);
				List list = query.getResultList();

                if (list.isEmpty()) {
                    App.bePersistedObject(this);
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
		// TODO Auto-generated method stub
		return true;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getAlertLevel() {
		return alertLevel;
	}

	public void setAlertLevel(int alertLevel) {
		this.alertLevel = alertLevel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}

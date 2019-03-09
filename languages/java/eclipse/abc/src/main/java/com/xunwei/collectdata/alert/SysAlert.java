package com.xunwei.collectdata.alert;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Session;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.xunwei.collectdata.App;
import com.xunwei.collectdata.utils.JacksonFactory;
import com.xunwei.collectdata.utils.RedissonClientFactory;

public class SysAlert extends AbsAlert {
	private int id;
	private Date endTime;
	private int alertLevel;
	private HashMap<String, String> alertData;
	
	@Override
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

	@Override
	public Boolean processData() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Boolean storeData() {
		Boolean result = true;
		Session sess = App.getSession();
		Iterator<Entry<String,String>> it = alertData.entrySet().iterator();
		
		while(it.hasNext()) {
			Map.Entry<String, String> me = (Map.Entry<String, String>) it.next();
			try {
				JsonNode devid = JacksonFactory.findJsonNode(me.getValue(),"/number");
				this.setDeviceNumber(devid.asInt());
				
				JsonNode info = JacksonFactory.findJsonNode(me.getValue(),"/info");
				this.setInfo(info.asText());
				
				JsonNode start = JacksonFactory.findJsonNode(me.getValue(),"/timestamp");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = sdf.parse(start.asText());
				this.setTimestamp(date);
				this.setEndTime(date);
				
				//to persist alert.
				Object test = sess.get(SysAlert.class, this.getTimestamp());
				if(test == null) {
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

	@Override
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

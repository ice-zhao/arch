package com.xunwei.collectdata.devices;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.AbsDataProcess;
import com.xunwei.collectdata.App;
import com.xunwei.collectdata.utils.RedissonClientFactory;

public class AmmeterData extends AbsDataProcess {
	private Integer id;
	private float totalCurrent;
	private HashMap<String, String> ammeterData = new HashMap<String, String>();
	
	public Boolean readData() {
		RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();
		RKeys keys = redissonClient.getKeys();
		Iterable<String> allKeys = keys.getKeysByPattern("*:*:1:100");
		
		for(String item : allKeys) {
			RBucket<String> rbucket = redissonClient.getBucket(item);
			ammeterData.put(item, rbucket.get());
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
		
        for (Entry<String, String> me : ammeterData.entrySet()) {
            try {
            	String value = me.getValue();
            	AmmeterData ammeter = mapper.readValue(value, AmmeterData.class);
                //to persist alert.
				Query query = sess.createQuery("select 1 from AmmeterData where hostID = :host"
						+ " and devNumber = :number");
				query.setParameter("host", ammeter.getHostID());
				query.setParameter("number", ammeter.getDeviceNumber());
				
				List list = query.getResultList();
                if (list.isEmpty()) {
                    App.bePersistedObject(ammeter);
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public float getTotalCurrent() {
		return totalCurrent;
	}

	public void setTotalCurrent(float totalCurrent) {
		this.totalCurrent = totalCurrent;
	}

}

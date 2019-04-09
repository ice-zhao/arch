package com.xunwei.collectdata.devices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.AbsDataProcess;
import com.xunwei.collectdata.App;
import com.xunwei.collectdata.utils.JacksonFactory;
import com.xunwei.collectdata.utils.RedissonClientFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.redisson.api.RKeys;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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
				Query query = sess.createQuery("select Consumption, Pressure" +
						" from GasData where StartTime = :time");
//				query.setParameter("time", gas.getStartTime(), TimestampType.INSTANCE);
				
				List list = query.list();
				//only for testing
//				List<Object[]> list1 = query.list();
//				for(Object[] row : list1) {
//					System.out.println( row[0].toString()+ "    @@@@@@@@@@@@@@@@@");
//					System.out.println( row[1].toString()+ "    @@@@@@@@@@@@@@@@@");
//				}
                if (list.isEmpty()) {
                    App.bePersistedObject(gas);
                }
            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                result = false;
            } finally {
            	try {
					sess.close();
				} catch (Exception e) {
            		e.printStackTrace();
				}

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

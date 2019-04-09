package xunwei.collectdata.devices;

import com.fasterxml.jackson.databind.ObjectMapper;
//import xunwei.collectdata.App;
//import xunwei.collectdata.utils.JacksonFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.redisson.api.RKeys;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import xunwei.collectdata.AbsCommonData;
import xunwei.collectdata.App;
import xunwei.collectdata.utils.JacksonFactory;
import xunwei.collectdata.utils.RedissonClientFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

//import org.hibernate.Query;
//import org.hibernate.Session;

public class AmmeterData extends AbsCommonData {
	private float Ua;
	private float Ia;
	private float ActivePower;
	private float totalCurrent;
	private HashMap<String, String> ammeterData = new HashMap<String, String>();
	
	public Boolean readData() {
		RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();
		RKeys keys = redissonClient.getKeys();
		Iterable<String> allKeys = keys.getKeysByPattern("*:*:1:100");

		for(String item : allKeys) {
//			RBucket<String> rbucket = redissonClient.getBucket(item);
			RList<String> rList = redissonClient.getList(item);
			ammeterData.put(item, rList.get(0));
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
		ObjectMapper mapper = JacksonFactory.getObjectMapper();
		
        for (Entry<String, String> me : ammeterData.entrySet()) {
            try {
            	String value = me.getValue();
            	AmmeterData ammeter = mapper.readValue(value, AmmeterData.class);
                //to persist alert.
				Query query = sess.createQuery("select 1 from AmmeterData where StartTime = :time");
//				query.setParameter("time", ammeter.getStartTime(), TimestampType.INSTANCE);
				
				List list = query.list();
                if (list.isEmpty()) {
                    App.bePersistedObject(ammeter);
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
		// TODO Auto-generated method stub
		return true;
	}

	public float getTotalCurrent() {
		return totalCurrent;
	}

	public void setTotalCurrent(float totalCurrent) {
		this.totalCurrent = totalCurrent;
	}

	public float getUa() {
		return Ua;
	}

	public void setUa(float ua) {
		Ua = ua;
	}

	public float getIa() {
		return Ia;
	}

	public void setIa(float ia) {
		Ia = ia;
	}

	public float getActivePower() {
		return ActivePower;
	}

	public void setActivePower(float activePower) {
		ActivePower = activePower;
	}
}

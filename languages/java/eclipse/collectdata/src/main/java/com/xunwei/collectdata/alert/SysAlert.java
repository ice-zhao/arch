package com.xunwei.collectdata.alert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.App;
import com.xunwei.collectdata.devices.Device;
import com.xunwei.collectdata.utils.RedissonClientFactory;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.TimestampType;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class SysAlert extends AbsCommonData {
	private String alarmName;
	private String AlarmSite;
	private Date endTime;
	private int alertLevel;
	private HashMap<String, String> alertData = new HashMap<String, String>();

	private Integer logCounter = 0;

	public Boolean readData() {
		RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();

        try {
            App.semaphore.acquire();
            RKeys keys = redissonClient.getKeys();
            Iterable<String> allKeys = keys.getKeysByPattern("*:*:*:110");

            for(String item : allKeys) {
                RList<String> rList = redissonClient.getList(item);
                alertData.put(item, rList.get(rList.size()-1));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            App.semaphore.release();
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
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mapper.setDateFormat(simpleDateFormat);

        for (Entry<String, String> me : alertData.entrySet()) {
            try {
            	String value = me.getValue();
            	if(value == null) {
            		result = false;
					break;
				}
            	SysAlert sysAlert = mapper.readValue(value, SysAlert.class);
            	sysAlert.setEndTime(sysAlert.getStartTime());
            	//get devId
				Query query1 = sess.createQuery("from Device where hostNo = :host_no and devNo = :dev_no");
				query1.setParameter("host_no", sysAlert.getHostNo());
				query1.setParameter("dev_no", sysAlert.getDevNo());
				List<Device> list1 = query1.getResultList();

				if(list1.size() > 0) {
					Integer dev_id = list1.get(0).getId();
					Integer park_Id=list1.get(0).getParkId();
					if(park_Id == null) {
						if(logCounter > 300) {
							System.out.println("device id " + dev_id + " contains NULL park ID value.");
							logCounter = 0;
						}
						else
							logCounter++;

						continue;
					}
					sysAlert.setDevId(dev_id);
					sysAlert.setParkId(park_Id);
					//to persist alert.
					Query query = sess.createQuery("select 1 from SysAlert where StartTime = :time and devId = :dev_id");
					query.setParameter("time", sysAlert.getStartTime(), TimestampType.INSTANCE);
					query.setParameter("dev_id", dev_id);
					List list = query.getResultList();

					if (list.isEmpty()) {
						App.bePersistedObject(sysAlert);
					}
				}
            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                result = false;
            }
        }

        App.closeSession(sess);
		
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

	public String getAlarmName() {
		return alarmName;
	}

	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}

	public String getAlarmSite() {
		return AlarmSite;
	}

	public void setAlarmSite(String alarmSite) {
		AlarmSite = alarmSite;
	}
}

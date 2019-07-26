package com.xunwei.collectdata;

//import javax.persistence.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.utils.DataUtil;
import com.xunwei.collectdata.utils.JacksonFactory;
import com.xunwei.collectdata.utils.JedisClientFactory;
import com.xunwei.services.MqttAsyncCallback;
import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.xunwei.collectdata.utils.DevDataService.MapAdapter;

//@javax.persistence.Entity
public abstract class AbsCommonData implements ICommon{
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "ID")
	public Integer id;

	public String hostNo;

//	@Column(name = "DevNo")
	public String devNo;

	public int deviceType;
	public String name;		//device name
	public Date startTime;
	public Date endTime;
	public Date timestamp;
	public int parkId;
	public int buildingId;
	public int alarmLevel;
	public String alarmName;

	public Integer devId;
	public Integer fieldId;
	public Integer entityId;
	public String dataTable="t_data";

	public List<HostData> dataList = null;
	public List<Entity> listEntity = null;
	public HashMap<String, Object> jsonMap = new HashMap<String, Object>();
	public HashMap<String, Object> jsonRes = new HashMap<String, Object>();
	public String jsonData = null;
	public Entity entity = null;

	public Map<Integer,Integer> allSignals = null;
	public HashMap<String, Date> dateHashMap = new HashMap<String, Date>();

	public Integer getDevId() {
		return devId;
	}

	public void setDevId(Integer devId) {
		this.devId = devId;
	}

	public Integer getFieldId() {
		return fieldId;
	}

	public void setFieldId(Integer fieldId) {
		this.fieldId = fieldId;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public String getDataTable(Integer month) {
//		Calendar cale = null;
//		cale = Calendar.getInstance();
//		Integer month = cale.get(Calendar.MONTH) + 1;
		return dataTable+month;
	}

	public Boolean readData() {
		Jedis jedis = null;

		String redisKey = String.format("map_data_%d", devId);
		String timeKey = String.format("map_time_%d", devId);
		Map<byte[], byte[]> map = null;
		Map<byte[], byte[]> time = null;
		try {
			App.semaphore.acquire();
			jedis = JedisClientFactory.getJedisInstance();
			map = jedis.hgetAll(redisKey.getBytes());
			time = jedis.hgetAll(timeKey.getBytes());
		} catch (Exception e) {
			System.out.println("AbsCommonData caused by: " + e.getCause() + " Message: " + e.getMessage());
			return false;
		} finally {
			App.semaphore.release();
			JedisClientFactory.returnJedisInstance(jedis);
		}

		if(!map.isEmpty()) {
			allSignals = MapAdapter(map);
			timestamp = new Date();

			if(!time.isEmpty()) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);//设置日期格式
				for (Map.Entry<byte[], byte[]> entry : time.entrySet()) {
					Integer dateVal = DataUtil.ByteArr2Int(entry.getValue());
					Long lval = Long.parseLong(Integer.toString(dateVal)) *1000;
					String date = df.format(lval);
					try {
						timestamp = df.parse(date);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
		else {
			allSignals = null;
			return false;
		}
		return true;
	}
	
	public Boolean processData() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public Boolean storeData() {
		Boolean result = false;

		if(jsonRes != null) {
			ObjectMapper objectMapper = JacksonFactory.getObjectMapper();
			MqttAsyncCallback mqttAsyncCallback = TopicFactory.getInstanceOfTalkTopics();

			try {
				App.semaphore.acquire();
				jsonData = objectMapper.writeValueAsString(jsonRes);
				System.out.println("[" + getDevNo() + "] " + jsonData);
				if(mqttAsyncCallback.isConnect())
					mqttAsyncCallback.publish(App.topicDevReplyData, 2, jsonData.getBytes("UTF-8"));
				result = true;
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				result = false;
			} catch (Throwable throwable) {
				throwable.printStackTrace();
				result = false;
			} finally {
				App.semaphore.release();
			}
		}
		return result;
	}
	
	public Boolean cleanupData() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public String doSerialize() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public final Boolean produceData() {
		if(!readData())	return false;
		
		if(!processData()) return false;
		
		if(!storeData()) return false;
		
		if(!cleanupData()) return false;
		
		return true;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public int getParkId() {
		return parkId;
	}
	public void setParkId(int parkId) {
		this.parkId = parkId;
	}
	public int getBuildingId() {
		return buildingId;
	}
	public void setBuildingId(int buildingId) {
		this.buildingId = buildingId;
	}
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getHostNo() {
		return hostNo;
	}
	public void setHostNo(String hostNo) {
		this.hostNo = hostNo;
	}
	public String getDevNo() {
		return devNo;
	}
	public void setDevNo(String devNo) {
		this.devNo = devNo;
	}

	public int getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(int alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public String getAlarmName() {
		return alarmName;
	}

	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}

	public boolean isTimestampChanged() {
		if(dateHashMap.containsKey(getDevNo())) {
			if(dateHashMap.get(getDevNo()).equals(timestamp))
				return false;
			else
				dateHashMap.put(getDevNo(), timestamp);
		}
		else
			dateHashMap.put(getDevNo(), timestamp);

		return true;
	}
	
}

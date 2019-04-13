package com.xunwei.collectdata;

//import javax.persistence.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.utils.JacksonFactory;
import com.xunwei.services.MqttAsyncCallback;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

//@Entity
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
		Session session = App.getDataSession();
		Query query1;
		Integer entityId;
		Integer month;

		query1 = session.createQuery("from Entity where devId = :dev_id and " +
				"timestamp = (select max(timestamp) from Entity where devId = :dev_id)");
		query1.setParameter("dev_id", getDevId());
		listEntity = query1.list();
		System.out.println("dddddddddddddddddddd " + listEntity.size() + "      " + getDevId());
		if(listEntity == null) {
			System.out.println("AmmeterData: listEntity is null.");
			return false;
		}

		entity = listEntity.get(0);
		entityId = listEntity.get(0).getId();
		month = listEntity.get(0).getMonth();
		if(entityId == null || month == null || entity == null) {
			System.out.println("AmmeterData: entityId or month or entity is null.");
			return false;
		}

//		System.out.println("######################### "+ entityId);

		query1 = session.createSQLQuery("select Value,FieldId,EntityId from " + getDataTable(month) +
				" where EntityId = " + entityId).
				addEntity(HostData.class);
		dataList = query1.list();
		if(dataList == null) {
			System.out.println("AmmeterData: list1 is null.");
			return false;
		}

//		System.out.println("######################### "+ dataList.get(0).getValue());
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
			try {
				jsonData = objectMapper.writeValueAsString(jsonRes);

				MqttAsyncCallback mqttAsyncCallback = TopicFactory.getInstanceOfTalkTopics();
				mqttAsyncCallback.publish(App.topicDevReplyData, 2, jsonData.getBytes("UTF-8"));
				result = true;
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (Throwable throwable) {
				throwable.printStackTrace();
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
	
}

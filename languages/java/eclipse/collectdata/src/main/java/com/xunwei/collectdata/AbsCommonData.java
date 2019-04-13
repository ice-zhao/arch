package com.xunwei.collectdata;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
public abstract class AbsCommonData implements ICommon{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	public Integer id;

	public String hostNo;

	@Column(name = "DevNo")
	public String devNo;

	public int deviceType;
	public String name;		//device name

//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	public Date startTime;
	public Date endTime;
	public Date timestamp;
	public int parkId;
	public int buildingId;
	public int alarmLevel;
	public String alarmName;
	public Integer devId;
	
	
	
	public Boolean readData() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public Boolean processData() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public Boolean storeData() {
		// TODO Auto-generated method stub
		return true;
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
		return devNo.replace("\"", "");
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

	public Integer getDevId() {
		return devId;
	}

	public void setDevId(Integer devId) {
		this.devId = devId;
	}
}

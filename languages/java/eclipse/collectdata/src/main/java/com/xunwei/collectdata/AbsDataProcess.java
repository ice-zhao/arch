package com.xunwei.collectdata;

import java.util.Date;

public abstract class AbsDataProcess implements IDataProcess {
	public int HostId;
	public int DevId;		//device number
	public int deviceType;
	public String name;		//device name
	public Date StartTime;
	public int ParkId;
	public int BuildingId;
	public String DevNo;


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

	public int getParkId() {
		return ParkId;
	}

	public void setParkId(int parkId) {
		ParkId = parkId;
	}

	public int getBuildingId() {
		return BuildingId;
	}

	public void setBuildingId(int buildingId) {
		BuildingId = buildingId;
	}
	
	public final Boolean produceData() {
		if(!readData())	return false;
		
		if(!processData()) return false;
		
		if(!storeData()) return false;
		
		if(!cleanupData()) return false;
		
		return true;
	}

	public int getHostId() {
		return HostId;
	}

	public void setHostId(int hostId) {
		HostId = hostId;
	}

	public int getDevId() {
		return DevId;
	}

	public void setDevId(int devId) {
		DevId = devId;
	}

	public Date getStartTime() {
		return StartTime;
	}

	public void setStartTime(Date startTime) {
		StartTime = startTime;
	}

	public String getDevNo() {
		return DevNo;
	}

	public void setDevNo(String devNo) {
		DevNo = devNo;
	}
}

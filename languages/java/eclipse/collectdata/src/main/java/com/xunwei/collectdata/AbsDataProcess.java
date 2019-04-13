package com.xunwei.collectdata;

import java.util.Date;

public abstract class AbsDataProcess implements IDataProcess {
	public int HostId;
	public int DevId;		//device number
	public int deviceType;
	public String name;		//device name
	public Date timestamp;
	public int ParkId;
	public int BuildingId;
	public String DevNo;
	public String hostNo;


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

	public String getHostNo() {
		return hostNo;
	}

	public void setHostNo(String hostNo) {
		this.hostNo = hostNo;
	}

	public final Boolean produceData() {
		if(!readData())	return false;
		
		if(!processData()) return false;
		
		if(!storeData()) return false;
		
		if(!cleanupData()) return false;
		
		return true;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
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

	public String getDevNo() {
		return DevNo.replace("\"", "");
	}

	public void setDevNo(String devNo) {
		DevNo = devNo;
	}
}

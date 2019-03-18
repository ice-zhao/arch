package com.xunwei.collectdata.alert;

import java.util.Date;

import com.xunwei.collectdata.IDataProcess;

public abstract class AbsAlert implements IDataProcess {
	public int HostId;
	public String DevNo;		//device number
	public int deviceType;
	public String name;		//device name
	public Date StartTime;
	public int ParkId;
	public String info;	//alert info

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

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public final Boolean produceAlertData() {
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

	public Date getStartTime() {
		return StartTime;
	}

	public void setStartTime(Date startTime) {
		StartTime = startTime;
	}

	public int getParkId() {
		return ParkId;
	}

	public void setParkId(int parkId) {
		ParkId = parkId;
	}

	public String getDevNo() {
		return DevNo;
	}

	public void setDevNo(String devNo) {
		DevNo = devNo;
	}
}

package com.xunwei.collectdata.devices;

import java.io.Serializable;

public class Device implements IDevice, Serializable {
	private Integer hostID;
	private Integer deviceNumber;
	private Integer deviceType;
	private String	name;


	public Integer getHostID() {
		return hostID;
	}

	public void setHostID(Integer hostID) {
		this.hostID = hostID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	public Integer getDeviceNumber() {
		return deviceNumber;
	}

	public void setDeviceNumber(Integer deviceNumber) {
		this.deviceNumber = deviceNumber;
	}
}

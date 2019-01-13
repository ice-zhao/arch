package com.xuwei.abc.devices;

public class Device implements IDevice {
	private Integer deviceId;
	private String deviceName;
	
	
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	
	public Integer getDeviceId() {
		return this.deviceId;
	}
	
	
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public String getDeviceName() {
		return this.deviceName;
	}
}

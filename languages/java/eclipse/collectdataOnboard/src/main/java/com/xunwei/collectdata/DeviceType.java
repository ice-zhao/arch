package com.xunwei.collectdata;

public enum DeviceType {
	Ammeter(33),
	WaterMeter(2),
	NaturalGasMeter(3),
	UPS(4),
	SysAlert(5);

	private final int val;
	
	DeviceType(int val) { this.val = val; }
	int getVal() { return this.val; }

	public static DeviceType  getInstance(Integer deviceType) {
		DeviceType [] deviceTypes = DeviceType.values();
		for(int i = 0; i < deviceTypes.length; i++) {
			if(deviceType.equals(deviceTypes[i].getVal()))
				return deviceTypes[i];
		}
		return null;
	}
}

package com.xunwei.collectdata;

public enum DeviceType {
	TemperatureHumidity_A(1),
	Ammeter(4),		//3-phase ammeter
	SinglePhaseAmmeter(5),	//single phase ammeter
	WaterMeter(9),
	NaturalGasMeter(3),
	UPS(9),
	SysAlert(10);

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

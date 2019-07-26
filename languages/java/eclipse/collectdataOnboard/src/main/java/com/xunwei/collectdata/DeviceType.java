package com.xunwei.collectdata;

public enum DeviceType {
	TemperatureHumidity_A(1),
	Pm25EnvDevice(21),		//PM2.5 device
	Ammeter(4),		//3-phase ammeter
	SinglePhaseAmmeter(5),	//single phase ammeter
	Flowmeter(108),	//flowmeter
	NHR5600Flowmeter(111),	//NHR6500 flowmeter
	HATFlowmeter(112),	//hengAnTe flowmeter
	KsdTubeFlowmeter(109),	//KSD tube flowmeter
	HRFlowmeter(110),	//hongRun LC80 flowmeter
	WaterMeter(9),
	NaturalGasMeter(3),
	UPS(12),
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

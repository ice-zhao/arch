package com.xunwei.collectdata;

public enum DeviceType {
	Ammeter(1),
	SinglePhaseAmmeter(6),
	TemperatureHumidity_A(7),
	WaterMeter(2),
	NaturalGasMeter(3),
	UPS(4),
	SysAlert(5);
	
	private final int val;
	
	DeviceType(int val) { this.val = val; }
	int getVal() { return this.val; }
}

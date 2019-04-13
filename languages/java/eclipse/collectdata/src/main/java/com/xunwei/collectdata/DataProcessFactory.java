package com.xunwei.collectdata;
import com.xunwei.collectdata.devices.*;

public class DataProcessFactory {
	private static AmmeterData ammeterData;
	private static SinglePhaseAmmeter singlePhaseAmmeter;
	private static TemperatureHumidity temperatureHumidity;
	private static GasData gasData;
	private static WaterData waterData;
	
	public static AbsCommonData getDataProcessInstance(DeviceType devType) {
		if(devType == DeviceType.Ammeter) {
			if(ammeterData == null)
				ammeterData = new AmmeterData();
			return ammeterData;
		}

		if(devType == DeviceType.SinglePhaseAmmeter) {
			if(singlePhaseAmmeter == null)
				singlePhaseAmmeter = new SinglePhaseAmmeter();
			return singlePhaseAmmeter;
		}

		if(devType == DeviceType.TemperatureHumidity_A) {
			if(temperatureHumidity == null)
				temperatureHumidity = new TemperatureHumidity();
			return temperatureHumidity;
		}
//		if(devType == DeviceType.NaturalGasMeter) {
//			if(gasData == null)
//				gasData = new GasData();
//			return gasData;
//		}
//
//		if(devType == DeviceType.WaterMeter) {
//			if(waterData == null)
//				waterData = new WaterData();
//			return waterData;
//		}

		return null;
	}
}

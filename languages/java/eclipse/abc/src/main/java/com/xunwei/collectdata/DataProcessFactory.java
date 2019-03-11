package com.xunwei.collectdata;
import com.xunwei.collectdata.devices.*;

public class DataProcessFactory {
	private static AmmeterData ammeterData;
	private static GasData gasData;
	private static WaterData waterData;
	
	public static AbsDataProcess getDataProcessInstance(DeviceType devType) {
		if(devType == DeviceType.Ammeter) {
			if(ammeterData == null)
				ammeterData = new AmmeterData();
			return ammeterData;
		}

		if(devType == DeviceType.NaturalGasMeter) {
			if(gasData == null)
				gasData = new GasData();
			return gasData;
		}

		if(devType == DeviceType.WaterMeter) {
			if(waterData == null)
				waterData = new WaterData();
			return waterData;
		}

		return null;
	}
}

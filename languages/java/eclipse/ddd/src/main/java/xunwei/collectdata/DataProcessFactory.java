package xunwei.collectdata;
//import xunwei.collectdata.devices.*;

import xunwei.collectdata.devices.AmmeterData;

public class DataProcessFactory {
	private static AmmeterData ammeterData;
//	private static GasData gasData;
//	private static WaterData waterData;
	
	
	public static AbsCommonData getDataProcessInstance(DeviceType devType) {
		if(devType == DeviceType.Ammeter) {
			if(ammeterData == null)
				ammeterData = new AmmeterData();
			return ammeterData;
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

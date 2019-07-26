package com.xunwei.collectdata;
import com.xunwei.collectdata.devices.*;

public class DataProcessFactory {
	private static AmmeterData ammeterData;
	private static SinglePhaseAmmeter singlePhaseAmmeter;
	private static TemperatureHumidity temperatureHumidity;
	private static Flowmeter flowmeter;
	private static KSDTubeFlowmeter ksdTubeFlowmeter;
	private static HRLC80Flowmeter hrlc80Flowmeter;
	private static NHR6500Flowmeter nhr6500Flowmeter;
	private static HATFlowmeter hatFlowmeter;
	private static PM25EnvDevice pm25EnvDevice;
//	private static GasData gasData;
//	private static WaterData waterData;
	
	
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

		if(devType == DeviceType.Flowmeter ) {
			if(flowmeter == null)
				flowmeter = new Flowmeter();
			return flowmeter;
		}

		if(devType == DeviceType.KsdTubeFlowmeter) {		//KeSiDa Tube flowmeter
			if(ksdTubeFlowmeter == null)
				ksdTubeFlowmeter = new KSDTubeFlowmeter();
			return ksdTubeFlowmeter;
		}

		if(devType == DeviceType.HRFlowmeter) {
			if(hrlc80Flowmeter == null)
				hrlc80Flowmeter = new HRLC80Flowmeter();
			return hrlc80Flowmeter;
		}

		if(devType == DeviceType.NHR5600Flowmeter) {
			if(nhr6500Flowmeter == null)
				nhr6500Flowmeter = new NHR6500Flowmeter();
			return nhr6500Flowmeter;
		}

		if(devType == DeviceType.HATFlowmeter) {
			if(hatFlowmeter == null)
				hatFlowmeter = new HATFlowmeter();
			return hatFlowmeter;
		}

		if(devType == DeviceType.Pm25EnvDevice) {
			if(pm25EnvDevice == null)
				pm25EnvDevice = new PM25EnvDevice();
			return pm25EnvDevice;
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

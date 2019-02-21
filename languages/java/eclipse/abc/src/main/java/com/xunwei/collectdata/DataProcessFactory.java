package com.xunwei.collectdata;
import com.xunwei.collectdata.devices.*;

public class DataProcessFactory {
	private static AmmeterData ammeterData;
	
	public static IDataProcess getDataProcessInstance(DeviceType devType) {
		if(devType == DeviceType.Ammeter) {
			if(ammeterData == null)
				ammeterData = new AmmeterData();
			return ammeterData;
		}
		
		return null;
	}
}

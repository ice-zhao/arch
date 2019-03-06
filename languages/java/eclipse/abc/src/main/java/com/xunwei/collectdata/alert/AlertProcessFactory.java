package com.xunwei.collectdata.alert;

import com.xunwei.collectdata.DeviceType;

public class AlertProcessFactory {
	private static AmmeterAlert ammeterAlert;
	private static UPSAlert	upsAlert;
	
	public static AbsAlert getAlertInstance(DeviceType devType) {
		if(devType == DeviceType.Ammeter) {
			if(ammeterAlert == null)
				ammeterAlert = new AmmeterAlert();
			return ammeterAlert;
		}
		
		if(devType == DeviceType.UPS) {
			if(upsAlert == null)
				upsAlert = new UPSAlert();
			return upsAlert;
		}
		
		return null;
	}
}

package com.xunwei.collectdata.alert;

import com.xunwei.collectdata.DeviceType;

public class AlertProcessFactory {
	private static AmmeterAlert ammeterAlert;
	private static UPSAlert	upsAlert;
	private static SysAlert	sysAlert;
	
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
		
		if(devType == DeviceType.SysAlert) {
			if(sysAlert == null)
				sysAlert = new SysAlert();
			return sysAlert;
		}
		

		return null;
	}
}

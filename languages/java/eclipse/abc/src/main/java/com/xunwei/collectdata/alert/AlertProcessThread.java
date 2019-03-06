package com.xunwei.collectdata.alert;

import com.xunwei.collectdata.DeviceType;

public class AlertProcessThread extends Thread {
	public void run() {
        DeviceType []deviceType = DeviceType.values();
        
        while(true) {
            for (DeviceType item : deviceType) {
            	AbsAlert alertProcess = AlertProcessFactory.getAlertInstance(item);
                if (alertProcess != null)
                	alertProcess.produceAlertData();
            }
        	try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}

}

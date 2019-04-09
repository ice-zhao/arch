package com.xunwei.collectdata.alert;

import com.xunwei.collectdata.DeviceType;

public class AlertProcessThread extends Thread {
	public void run() {
        DeviceType []deviceType = DeviceType.values();

        do {
            for (DeviceType item : deviceType) {
                AbsAlert alertProcess = AlertProcessFactory.getAlertInstance(item);
                if (alertProcess != null)
                    alertProcess.produceAlertData();
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (true);
	}

}

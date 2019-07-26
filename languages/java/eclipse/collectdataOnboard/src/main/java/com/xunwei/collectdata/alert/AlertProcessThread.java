package com.xunwei.collectdata.alert;

import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.App;
import com.xunwei.collectdata.DeviceType;
import com.xunwei.collectdata.TopicFactory;
import com.xunwei.services.MqttAsyncCallback;

public class AlertProcessThread extends Thread {
	public void run() {
        DeviceType []deviceType = DeviceType.values();
        MqttAsyncCallback mqttClient = TopicFactory.getInstanceOfTalkTopics();

        do {
            try {
                Thread.sleep(10000);
                if(!App.isHostRegistered() || !mqttClient.isConnect())
                    continue;
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            for (DeviceType item : deviceType) {
                AbsCommonData alertProcess = AlertProcessFactory.getAlertInstance(item);
                if (alertProcess != null)
                    alertProcess.produceData();
            }
        } while (true);
	}

}

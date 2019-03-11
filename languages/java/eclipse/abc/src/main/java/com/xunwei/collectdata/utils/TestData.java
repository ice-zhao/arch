package com.xunwei.collectdata.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.services.MqttAsyncCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class TestData {
    private String action;
    private String pubTopic;
    private String clientId 	= null;
    private int qos 			= 2;
    private String broker 		= "LocalHost";//"m2m.eclipse.org";
    private int port 			= 1883;
    private String protocol = "tcp://";
    private String url = protocol + broker + ":" + port;
    private boolean cleanSession = true;			// Non durable subscriptions
    private boolean quietMode 	= false;
    private String password = null;
    private String userName = null;

    public void  produceAlertData() throws Throwable{
        pubTopic = "/devices/alert";
        action = "publish";

        clientId = pubTopic + " " + action;
        MqttAsyncCallback deviceAlertPubClient =
                new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password);

        deviceAlertPubClient.setPubTopic(pubTopic);
        try {
            deviceAlertPubClient.connect();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        String alertData = "{\n"+
                "\"key\": \"1:3:3:110\",\n"+
                "\"value\": {\"number\":3,\n"+
                "\"deviceId\":2,\n"+
                "\"name\":\"ammeter\",\n"+
                "\"current\":3,\n"+
                "\"info\":\"气表错误\",\n"+
                "\"timestamp\":\"2019-01-11 13:14:15\"}\n"+
                "}";

        deviceAlertPubClient.publish(pubTopic,qos,alertData.getBytes());
    }
}

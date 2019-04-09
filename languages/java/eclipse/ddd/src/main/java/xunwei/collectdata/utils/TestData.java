package xunwei.collectdata.utils;

import xunwei.services.MqttAsyncCallback;

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
                "\"value\": {" +
				"\"deviceType\":3,\n"+
				"\"devId\":1,\n"+
                "\"alarmName\":\"电表错误\",\n"+
                "\"alarmSite\":\"园区1\",\n"+
                "\"alertLevel\":1,\n"+
				"\"hostId\":1,\n"+
				"\"parkId\":1,\n"+
				"\"devNo\":\"dev_1\",\n"+
                "\"startTime\":\"2019-01-11 13:14:15\",\n"+
                "\"endTime\":\"2019-01-12 16:14:15\"}\n"+
                "}";

        deviceAlertPubClient.publish(pubTopic,qos,alertData.getBytes());
    }
    
    public void produceDeviceData() throws Throwable {
		//only for test. publish topic "/devices/reply/data";
		pubTopic = "/devices/reply/data";
		action = "publish";
		
		clientId = pubTopic + " " + action;
		MqttAsyncCallback deviceDataReplyPubClient =
				new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password);
		deviceDataReplyPubClient.connect();
		//ammeter
		String ammeterData = "{\n"+
							  "\"key\": \"1:6:1:100\",\n"+
							  "\"value\": {\n" +
                              "\"ua\":300.3,\n"+
                              "\"ia\":1.9,\n"+
                              "\"activePower\":3.9,\n"+
//                              "\"parkId\" : 6,\n" +
//				              "\"buildingId\" : 5,\n" +
                              "\"devId\":1,\n"+
                              "\"hostId\":1,\n"+
                              "\"devNo\":\"dev_1\",\n"+
							  "\"startTime\":\"2019-03-11 13:14:15\"}\n"+
							"}";
		deviceDataReplyPubClient.publish(pubTopic,qos,ammeterData.getBytes());

		//Gas
		String gasData = "{\n"+
				  "\"key\": \"8:9:3:100\",\n"+
				  "\"value\": {\n" +
				  "\"consumption\":100.5,\n"+
				  "\"pressure\":123.6,\n"+
//				  "\"parkId\" : 6,\n" +
//				  "\"buildingId\" : 5,\n" +
                  "\"devId\":1,\n"+
                  "\"hostId\":1,\n"+
                  "\"devNo\":\"dev_5\",\n"+
				  "\"startTime\":\"2019-03-11 09:08:06\"}\n"+
				"}";
		deviceDataReplyPubClient.publish(pubTopic,qos,gasData.getBytes());

		//water
		String waterData = "{\n"+
				  "\"key\": \"5:6:2:100\",\n"+
				  "\"value\": {\n" +
				  "\"consumption\":3000.5,\n"+
				  "\"quality\":\"100\",\n"+
				  "\"pressure\":123.6,\n"+
				  "\"speed\":50.8,\n"+
				  "\"parkId\" : 9,\n" +
				  "\"buildingId\" : 10,\n" +
                  "\"devId\":1,\n"+
                  "\"hostId\":1,\n"+
                  "\"devNo\":\"dev_8\",\n"+
				  "\"startTime\":\"2019-03-11 09:08:06\"}\n"+
				"}";
		deviceDataReplyPubClient.publish(pubTopic,qos,waterData.getBytes());
    }
}

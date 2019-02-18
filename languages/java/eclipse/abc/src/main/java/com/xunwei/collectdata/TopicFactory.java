package com.xunwei.collectdata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.devices.Host;
import com.xunwei.services.MqttAsyncCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class TopicFactory {
	// Default settings:
	private boolean quietMode 	= false;
	private String action 		= "publish";
	private int qos 			= 2;
	private String broker 		= "LocalHost";//"m2m.eclipse.org";
	private int port 			= 1883;
	private String clientId 	= null;
	private String pubTopic 	= "Sample/Java/v3";
	private boolean cleanSession = true;			// Non durable subscriptions
	private String password = null;
	private String userName = null;
	private static TopicFactory topicFactory = null;
	private boolean isStartAll = false;
	private String protocol = "tcp://";
	private String url = protocol + broker + ":" + port;
//	private boolean ssl = false;

	private TopicFactory(String[] args) {
		// Parse the arguments -
		for (int i=0; i<args.length; i++) {
			// Check this is a valid argument
			if (args[i].length() == 2 && args[i].startsWith("-")) {
				char arg = args[i].charAt(1);
				// Handle arguments that take no-value
				switch(arg) {
					case 'h': case '?':	printHelp(); return;
					case 'q': quietMode = true;	continue;
				}

				// Now handle the arguments that take a value and
				// ensure one is specified
				if (i == args.length -1 || args[i+1].charAt(0) == '-') {
					System.out.println("Missing value for argument: "+args[i]);
					printHelp();
					return;
				}
				
				switch(arg) {
					case 'a': action = args[++i];                 break;
					case 't':
						String topic = args[++i];
						break;
					case 'm':
						String message = args[++i];
						break;
					case 's': qos = Integer.parseInt(args[++i]);  break;
					case 'b': broker = args[++i];                 break;
					case 'p': port = Integer.parseInt(args[++i]); break;
					case 'i': clientId = args[++i];				  break;
					case 'c': cleanSession = Boolean.valueOf(args[++i]);  break;
          case 'k': System.getProperties().put("javax.net.ssl.keyStore", args[++i]); break;
          case 'w': System.getProperties().put("javax.net.ssl.keyStorePassword", args[++i]); break;
          case 'r': System.getProperties().put("javax.net.ssl.trustStore", args[++i]); break;
//          case 'v': ssl = Boolean.valueOf(args[++i]).booleanValue();  break;
          case 'u': userName = args[++i];               break;
          case 'z': password = args[++i];               break;
					default:
						System.out.println("Unrecognised argument: "+args[i]);
						printHelp();
						return;
				}
			} else {
				System.out.println("Unrecognised argument: "+args[i]);
				printHelp();
				return;
			}
		}
		
	}

	static TopicFactory getInstance(String[] args) {
		if(topicFactory == null)
			topicFactory = new TopicFactory(args);
		return topicFactory;
	}

	void startAllTopics() {
		if(isStartAll) return;

		try {
//			talkOnRegisterHost();
			talkOnDeviceDataRead();
			isStartAll = true;
		} catch(MqttException me) {
			// Display full details of any exception that occurs
			System.out.println("reason "+me.getReasonCode());
			System.out.println("msg "+me.getMessage());
			System.out.println("loc "+me.getLocalizedMessage());
			System.out.println("cause "+me.getCause());
			System.out.println("excep "+me);
			me.printStackTrace();
			System.exit(-1);
		} catch (Throwable th) {
			System.out.println("Throwable caught "+th);
			th.printStackTrace();
			System.exit(-1);
		}
	}
	
	private void talkOnRegisterHost() throws Throwable {
		String subTopic = "/control/register/dcms";
		pubTopic = "/control/register/dcms/ack";
		action 	= "subscribe";

		clientId = subTopic + " " + action;
	    // Create an instance of the subscribe client wrapper
		MqttAsyncCallback regHostSubClient = 
				new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password) {
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				super.messageArrived(topic, message);

				//parse received JSON data.
				String receivedData = new String(message.getPayload());
				ObjectMapper mapper = new ObjectMapper();
				Host host = mapper.readValue(receivedData, Host.class);
				System.out.println("----------"+ host.getName());

				//To persist data
				boolean result = true;
				try {
					App.bePersistedObject(host);
				} catch (Throwable throwable) {
					throwable.printStackTrace();
					result = false;
				}

				String jsonData = "{\n" +
								"\"ErrNumber\":0,\n" +
								"\"Description\":\"process data successfully.\"\n" +
								"}";
				if(!result) jsonData = "{\n" +
								"\"ErrNumber\":-1,\n" +
								"\"Description\":\"process data failure.\"\n" +
								"}";
				try {
					super.publish(pubTopic,qos,jsonData.getBytes());
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		regHostSubClient.connect();
		regHostSubClient.subscribe(subTopic,qos);
		
		
		//only for test ack topic
		action 	= "subscribe";
		String subAckTopic = "/control/register/dcms/ack";
		clientId = subAckTopic + " " + action;
		MqttAsyncCallback regHostSubAckClient = 
				new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password);
		regHostSubAckClient.connect();
		regHostSubAckClient.subscribe(subAckTopic, qos);
		
		//Only for test register dcms topic.
		action 	= "publish";
		String pubHostTopic = "/control/register/dcms";
		clientId = pubHostTopic + " " + action;
	    // Create an instance of the publish client wrapper
		MqttAsyncCallback regHostPubClient = 
				new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password);
		regHostPubClient.connect();
		String dcmsJson = "{\n" +
				"\"hostID\" : 8,\n" +
				"\"areaID\" : 9,\n" +
				"\"buildingID\" : 10,\n" +
				"\"name\" : \"sample park\",\n" +
				"\"serial\" : \"user defined\"\n" +
				"}";
		regHostPubClient.publish(pubHostTopic,qos,dcmsJson.getBytes());
	}

	private void talkOnDeviceDataRead() throws Throwable {
		String pubTopic = "/control/device/data/read";
		action 	= "publish";

		// Create an instance of the publish client wrapper
		clientId = pubTopic + " " + action;
		MqttAsyncCallback deviceDataReadPubClient =
				new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password) {
					//get redisson blockingQueue
					RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();
					RBlockingQueue<String> queue = redissonClient.getBlockingQueue("webRequestQueue");

					public void run() {
						String jsonData;
						try {
							jsonData = queue.poll(0, TimeUnit.DAYS);		//infinitely wait
							this.publish(this.getPubTopic(),qos,jsonData.getBytes());
						} catch (Throwable throwable) {
							throwable.printStackTrace();
						}
					}
				};
		deviceDataReadPubClient.setPubTopic(pubTopic);
		deviceDataReadPubClient.connect();

		Thread t = new Thread(deviceDataReadPubClient);

		t.start();

		//subscribe topic /devices/reply/data to store data in redis
		String subTopic = "/devices/reply/data";
		action 	= "subscribe";

		//create subscribe client
		clientId = subTopic + " " + action;
		MqttAsyncCallback deviceDataReadSubClient =
				new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password) {
					public void messageArrived(String topic, MqttMessage message) throws Exception {
//						super.messageArrived(topic, message);
						System.out.println("----------------");
						//store JSON data in redis
						ObjectMapper objectMapper = new ObjectMapper();
						JsonNode rootNode = objectMapper.readTree(message.getPayload());
						JsonNode key = rootNode.path("key");
						JsonNode value = rootNode.path("value");

//						System.out.println(value.toString());
						RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();
						RBucket rBucket = redissonClient.getBucket(key.asText());
						rBucket.set(value.toString());
						System.out.println(rBucket.get());

						RKeys keys = redissonClient.getKeys();
						Iterable<String> allkeys = keys.getKeys();
						for(String item : allkeys)
							System.out.println(item);

					}
				};
		deviceDataReadSubClient.setSubTopic(subTopic);
		deviceDataReadSubClient.connect();
		deviceDataReadSubClient.subscribe(subTopic,qos);


		//only for test. publish topic "/devices/reply/data";
		pubTopic = "/devices/reply/data";
		action = "publish";
		clientId = subTopic + " " + action;
		MqttAsyncCallback deviceDataReplyPubClient =
				new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password);
		deviceDataReplyPubClient.connect();
		String replyData = "{\n"+
							  "\"key\": \"Map:1:3:2:100\",\n"+
							  "\"value\": {\"number\":3,\n"+
							  "\"deviceId\":2,\n"+
							  "\"name\":\"ammeter\",\n"+
							  "\"current\":3,\n"+
							  "\"timestamp\":\"2019-01-11 13:14:15\"}\n"+
							"}";
/*		String replyData = "{\n"+
							  "\"Key\": \"Map:1:3:2:100\",\n"+
							  "\"value\": 3\n"+
							"}";*/
		deviceDataReplyPubClient.publish(pubTopic,qos,replyData.getBytes());
	}
	
	/*public void testLocalTopic() {
		// With a valid set of arguments, the real work of
		// driving the client API can begin
		try {
		    clientId = "SampleJavaV3_"+action;
		    broker = "LocalHost";
		    url = protocol + broker + ":" + port;

			// Create an instance of the Sample client wrapper
			MqttAsyncCallback sampleClient =
					new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password);

			sampleClient.connect();
			// Perform the specified action
			if (action.equals("publish")) {
				topic = pubTopic;
				int i = 0;
				for(i=0; i<10; i++)
					sampleClient.publish(topic,qos,message.getBytes());
			} else if (action.equals("subscribe")) {
				topic = subTopic;
				sampleClient.subscribe(topic,qos);
			}
		} catch(MqttException me) {
			// Display full details of any exception that occurs
			System.out.println("reason "+me.getReasonCode());
			System.out.println("msg "+me.getMessage());
			System.out.println("loc "+me.getLocalizedMessage());
			System.out.println("cause "+me.getCause());
			System.out.println("excep "+me);
			me.printStackTrace();
		} catch (Throwable th) {
			System.out.println("Throwable caught "+th);
			th.printStackTrace();
		}
        System.out.println( "good" );
	}*/
	
	/*public void testRemoteTopic() {
		// With a valid set of arguments, the real work of
		// driving the client API can begin
		try {
		    clientId = "SampleJavaV3_"+action;
		    
			// Create an instance of the Sample client wrapper
			MqttAsyncCallback sampleClient =
					new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password);

			sampleClient.connect();
			// Perform the specified action
			if (action.equals("publish")) {
				topic = pubTopic;
				int i = 0;
				for(i=0; i<10; i++)
					sampleClient.publish(topic,qos,message.getBytes());
			} else if (action.equals("subscribe")) {
				topic = subTopic;
				sampleClient.subscribe(topic,qos);
			}
		} catch(MqttException me) {
			// Display full details of any exception that occurs
			System.out.println("reason "+me.getReasonCode());
			System.out.println("msg "+me.getMessage());
			System.out.println("loc "+me.getLocalizedMessage());
			System.out.println("cause "+me.getCause());
			System.out.println("excep "+me);
			me.printStackTrace();
		} catch (Throwable th) {
			System.out.println("Throwable caught "+th);
			th.printStackTrace();
		}
        System.out.println( "good" );
    }*/
	
    private void printHelp() {
        System.out.println(
            "Syntax:\n\n" +
                "    SampleAsyncCallBack [-h] [-a publish|subscribe] [-t <topic>] [-m <message text>]\n" +
                "            [-s 0|1|2] -b <hostname|IP address>] [-p <brokerport>] [-i <clientID>]\n\n" +
                "    -h  Print this help text and quit\n" +
                "    -q  Quiet mode (default is false)\n" +
                "    -a  Perform the relevant action (default is publish)\n" +
                "    -t  Publish/subscribe to <topic> instead of the default\n" +
                "            (publish: \"Sample/Java/v3\", subscribe: \"Sample/#\")\n" +
                "    -m  Use <message text> instead of the default\n" +
                "            (\"Message from MQTTv3 Java client\")\n" +
                "    -s  Use this QoS instead of the default (2)\n" +
                "    -b  Use this name/IP address instead of the default (m2m.eclipse.org)\n" +
                "    -p  Use this port instead of the default (1883)\n\n" +
                "    -i  Use this client ID instead of SampleJavaV3_<action>\n" +
                "    -c  Connect to the server with a clean session (default is false)\n" +
                "     \n\n Security Options \n" +
                "     -u Username \n" +
                "     -z Password \n" +
                "     \n\n SSL Options \n" +
                "    -v  SSL enabled; true - (default is false) " +
                "    -k  Use this JKS format key store to verify the client\n" +
                "    -w  Passpharse to verify certificates in the keys store\n" +
                "    -r  Use this JKS format keystore to verify the server\n" +
                " If javax.net.ssl properties have been set only the -v flag needs to be set\n" +
                "Delimit strings containing spaces with \"\"\n\n" +
                "Publishers transmit a single message then disconnect from the server.\n" +
                "Subscribers remain connected to the server and receive appropriate\n" +
                "messages until <enter> is pressed.\n\n"
            );
      }
}

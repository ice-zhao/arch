package com.xunwei.collectdata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.devices.Device;
import com.xunwei.collectdata.devices.Host;
import com.xunwei.collectdata.utils.JacksonFactory;
import com.xunwei.collectdata.utils.RedissonClientFactory;
import com.xunwei.services.MqttAsyncCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.TimestampType;
import org.redisson.api.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TopicFactory {
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
	private String url = null;
	private static String redisServer = "redis://192.168.0.168:6379";
	private static String redisPass = "xunwei";
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
          case 'R': redisServer = args[++i];            break;
          case 'P': redisPass = args[++i];            break;
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

		url = protocol + broker + ":" + port;
	}

	static TopicFactory getInstance(String[] args) {
		if(topicFactory == null)
			topicFactory = new TopicFactory(args);
		return topicFactory;
	}

	void clearRedisKeys() {
		RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();
		RKeys keys = redissonClient.getKeys();
		keys.deleteByPattern("*");

	}

	void startAllTopics() {
		if(isStartAll) return;

		//clear all Redis keys
		clearRedisKeys();

		try {
			talkOnRegisterHost();
			talkOnRegisterDevices();
			talkOnDeviceDataRead();
			talkOnDeviceAlert();
			talkOnDeviceStatus();
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
		String subTopic = "/control/register/host";
		pubTopic = "/control/register/host/ack";
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

				//To persist data
				boolean result = false;
				Session sess = App.getSession();
				
				try {
//					Object test = sess.get(Host.class, host.getHostID());
					Query query = sess.createQuery("select 1 from Host where hostNo = :hostid");
					query.setParameter("hostid", host.getHostNo());
					List list = query.getResultList();
					if(list.isEmpty()) {
						App.bePersistedObject(host);
						result = true;
					}
				} catch (Throwable throwable) {
					throwable.printStackTrace();
					result = false;
				} finally {
					sess.close();
				}

				String jsonData = "{\n" +
								"\"errNumber\":0,\n" +
								"\"description\":\"Host registers successfully.\"\n" +
								"}";
				if(!result) jsonData = "{\n" +
								"\"errNumber\":0,\n" +
								"\"description\":\"Host has been registered.\"\n" +
								"}";
				try {
					super.publish(this.getPubTopic(),qos,jsonData.getBytes());
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		regHostSubClient.connect();
		regHostSubClient.subscribe(subTopic,qos);
		regHostSubClient.setPubTopic(pubTopic);
		
		
		//only for test ack topic
//		action 	= "subscribe";
//		String subAckTopic = "/control/register/dcms/ack";
//		clientId = subAckTopic + " " + action;
//		MqttAsyncCallback regHostSubAckClient =
//				new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password);
//		regHostSubAckClient.connect();
//		regHostSubAckClient.subscribe(subAckTopic, qos);
		
		//Only for test register dcms topic.
/*		action 	= "publish";
		String pubHostTopic = "/control/register/host";
		clientId = pubHostTopic + " " + action;
	    // Create an instance of the publish client wrapper
		MqttAsyncCallback regHostPubClient = 
				new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password);
		regHostPubClient.connect();
		String dcmsJson = "{\n" +
				"\"hostNo\" : \"dev_888\",\n" +
//				"\"areaID\" : 9,\n" +
//				"\"buildingID\" : 10,\n" +
				"\"remoteServerAddr\" : \"10.0.1.123\",\n" +
				"\"floor\" : 20,\n" +
				"\"serial\" : \"user defined\"\n" +
				"}";
		regHostPubClient.publish(pubHostTopic,qos,dcmsJson.getBytes());*/
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
						while(true) {
							try {
								jsonData = queue.poll(10, TimeUnit.MINUTES);        //infinitely wait
								System.out.println("webRequestQueue received: " + jsonData);
								if(jsonData != null)
									this.publish(this.getPubTopic(), qos, jsonData.getBytes());
							} catch (Throwable throwable) {
								throwable.printStackTrace();
							}
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
						String data = new String(message.getPayload());
						JsonNode key = JacksonFactory.findJsonNode(data, "/key");
						JsonNode value = JacksonFactory.findJsonNode(data,"/value");

						storeListToRedis(key,value);

						//only for test
						RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();
//						RBucket<String> rBucket = redissonClient.getBucket(key.asText());
//						rBucket.set(value.toString());
//						System.out.println(rBucket.get());

						RKeys keys = redissonClient.getKeys();
						Iterable<String> allkeys = keys.getKeys();
						for(String item : allkeys)
							System.out.println(item);

					}
				};
		deviceDataReadSubClient.setSubTopic(subTopic);
		deviceDataReadSubClient.connect();
		deviceDataReadSubClient.subscribe(subTopic,qos);

		//only for test redisson blocking queue
//		RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();
//		RBlockingQueue<String> rBlockingQueue = redissonClient.getBlockingQueue("webRequestQueue");
//		String queueData = "{\n" +
//				"\"hostNo\" : \"dev_88\",\n" +
//				"\"devNo\" : [\"AMT_C\",\"AMT_B\",\"TH_1\"]\n" +
//				"}";
/*		String queueData = "{\n" +
				"\"hostNo\" : \"*\",\n" +
				"\"devNo\" : [\"TH-SENSOR1\"]\n" +
				"}";*/
//		String queueData = "{\n" +
//				"\"hostNo\" : \"*\",\n" +
//				"\"devNo\" : [\"*\"]\n" +
//				"}";
/*		String queueData = "{\n" +
				"\"hostNo\" : \"*\",\n" +
				"\"devNo\" : [\"*\"]\n" +
				"}";
		rBlockingQueue.add(queueData);*/
	}

	private void talkOnRegisterDevices() throws Throwable {
		//subscribe topic /control/register/host/device to register device in mysql
		String subTopic = "/control/register/host/device";
		pubTopic = "/control/register/host/device/ack";
		action = "subscribe";

		//create subscribe client
		clientId = subTopic + " " + action;
		MqttAsyncCallback registerDeviceSubClient =
				new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password) {
					DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					public void messageArrived(String topic, MqttMessage message) throws Exception {
						super.messageArrived(topic, message);
						//store JSON data in redis
						ObjectMapper mapper = JacksonFactory.getObjectMapper();
						mapper.setDateFormat(sdf);

						Device device = null;
						try {
							device = mapper.readValue(message.getPayload(), Device.class);
						}catch (Exception e) {
							e.printStackTrace();
							return;
						}

						//To persist data
						boolean result = true;
						Session session = App.getSession();

						try {
							Query query = session.createQuery("select 1 from Device where hostNo = :host_no and " +
									"devNo=:devno");
							query.setParameter("devno", device.getDevNo());
							query.setParameter("host_no", device.getHostNo());
							List list = query.getResultList();

							if(list.isEmpty()) {
								query = session.createQuery("from Host where hostNo = :host_no");
								query.setParameter("host_no", device.getHostNo());
								List<Host> list1 = query.getResultList();

								if(list1 != null && list1.size() > 0) {
									device.setHostId(list1.get(0).getId());
									App.bePersistedObject(device);
								} else
									result = false;
							}
						} catch (Throwable throwable) {
//							throwable.printStackTrace();
							System.out.println("\nCausedBy: " + throwable.getCause());
							result = false;
						}

						App.closeSession(session);

						String reply = "{\n" +
								"\"errorNumber\":0,\n" +
								"\"description\":\"Device registers successfully.\"\n" +
								"}";
						if(!result) reply = "{\n" +
								"\"errorNumber\":-1,\n" +
								"\"description\":\"Device registered failure.\"\n" +
								"}";

						try {
							super.publish(this.getPubTopic(),qos,reply.getBytes());
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
		registerDeviceSubClient.setSubTopic(subTopic);
		registerDeviceSubClient.setPubTopic(pubTopic);
		registerDeviceSubClient.connect();
		registerDeviceSubClient.subscribe(subTopic,qos);
	}

	private void talkOnDeviceAlert() throws Throwable {
		String subTopic = "/devices/alert";
		action = "subscribe";

		clientId = subTopic + " " + action;
		MqttAsyncCallback deviceAlertSubClient =
				new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password) {
					public void messageArrived(String topic, MqttMessage message) throws Exception {
//						super.messageArrived(topic, message);
						ObjectMapper objectMapper = new ObjectMapper();
						JsonNode rootNode = objectMapper.readTree(message.getPayload());
						JsonNode key = rootNode.at("/key");
						JsonNode value = rootNode.at("/value");
						storeListToRedis(key,value);
					}
				};
		deviceAlertSubClient.setSubTopic(subTopic);
		deviceAlertSubClient.connect();
		deviceAlertSubClient.subscribe(subTopic,qos);
	}

	private void talkOnDeviceStatus() throws Throwable {
		String subTopic = "/devices/status";
		action = "subscribe";

		clientId = subTopic + " " + action;
		MqttAsyncCallback deviceStatusSubClient =
				new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password) {
					public void messageArrived(String topic, MqttMessage message) throws Exception {
//						super.messageArrived(topic, message);
						String data = new String(message.getPayload());
						JsonNode key = JacksonFactory.findJsonNode(data,"/key");
						JsonNode value = JacksonFactory.findJsonNode(data,"/value");
						storeListToRedis(key,value);
					}
				};

		deviceStatusSubClient.setSubTopic(subTopic);
		deviceStatusSubClient.connect();
		deviceStatusSubClient.subscribe(subTopic,qos);
	}

	private void storeBucketToRedis(JsonNode key, JsonNode value) {
		RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();
		RBucket<String> rBucket = redissonClient.getBucket(key.asText());
		rBucket.set(value.toString());
	}

	private void storeListToRedis(JsonNode key, JsonNode value) {
		RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();
        try {
            App.semaphore.acquire();
            RList<String> rList = redissonClient.getList(key.asText());
            if(rList.size() > 0)
                rList.clear();
            rList.add(value.toString());
//            rList.expire(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            App.semaphore.release();
        }
	}

	public static String getRedisServer() {
		return redisServer;
	}

	public static String getRedisPass() {
		return redisPass;
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

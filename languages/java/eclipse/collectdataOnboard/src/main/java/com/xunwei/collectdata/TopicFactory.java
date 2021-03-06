package com.xunwei.collectdata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.xunwei.collectdata.devices.Device;
import com.xunwei.collectdata.devices.DeviceRegisterThread;
import com.xunwei.collectdata.devices.Host;
import com.xunwei.collectdata.utils.ErrorInfo;
import com.xunwei.collectdata.utils.JacksonFactory;
import com.xunwei.collectdata.utils.RedissonClientFactory;
import com.xunwei.services.MqttAsyncCallback;
import org.apache.xpath.operations.Bool;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.hibernate.Query;
import org.hibernate.Session;
import org.redisson.api.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TopicFactory {
	// Default settings:
	private boolean quietMode 	= false;
	private String action 		= "publish";
	private int qos 			= 2;
//	private String broker 		= "10.0.0.6";//"LocalHost";//"m2m.eclipse.org";
	private String broker 		= "LocalHost";
//	private String broker 		= "113.137.39.193";
//	private String broker 		= "192.168.3.1";
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
	private String clientName = "xunwei";
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
          case 'n': clientName = args[++i];             break;
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
	
	static TopicFactory getInstance() throws Throwable {
		if(topicFactory == null)
			throw new Exception("class has not instance!");
		return topicFactory;
	}

	void startAllTopics() {
		if(isStartAll) return;

		try {
			talkOnTopics();
//			talkOnRegisterHost();
//			talkOnRegisterDevices();
//			talkOnDeviceDataRead();
//			talkOnDeviceAlert();
//			talkOnDeviceStatus();
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
	
	private static MqttAsyncCallback talkTopics = null;
	
	private void talkOnTopics() throws Throwable {
		String subTopic = App.topicHostAck +","+ App.topicDevAck + "," +
                          App.topicReadData;
		action 	= "subscribe";
		
		clientId = clientName + "  " + subTopic + " " + action;
		if(null == talkTopics) {
			talkTopics = new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password) {
				public void messageArrived(String topic, MqttMessage message) throws Exception {
//                    super.messageArrived(topic, message);
					String payload;
					payload = new String(message.getPayload());

					//Host ack
					if(topic.equals(App.topicHostAck)) {
						JsonNode errorNum = JacksonFactory.findJsonNode(payload, ErrorInfo.errNum);
						if((errorNum != null) && (errorNum.asInt() == ErrorInfo.SUCCESS)) {
						    App.setHostRegistered(true);
                        }
						return;
					}
					
					//device ack
					if(topic.equals(App.topicDevAck)) {
						JsonNode errorNum = JacksonFactory.findJsonNode(payload, ErrorInfo.errNum);
						if(errorNum != null) {
							DeviceRegisterThread.sendAcknowledge(errorNum.asInt());
						}
						return;
					}
					
					//get devNo to read data or parse "*" to get all device data
					if(topic.equals(App.topicReadData)) {
						try {
							boolean isHostNo = false;
							//TODO: 1. check hostNo if it equals the current host Number.
							ObjectMapper objectMapper = JacksonFactory.getObjectMapper();
							JsonNode hostNum = JacksonFactory.findJsonNode(payload, "/hostNo");
							boolean isArray = hostNum.isArray();
							if(!isArray) {
								System.out.println("JSON hostNo node is not a String array.");
								return;
							}

							Host host = Host.getHostInstance();
							String star = "*";
							ArrayType arrayType = objectMapper.getTypeFactory().constructArrayType(String.class);
							String [] hostNo = objectMapper.readValue(hostNum.toString(), arrayType);

							for(int i=0; i<hostNo.length; i++) {
								if(hostNo[i].equals(star) || hostNo[i].equals(host.getHostNo())) {
									isHostNo = true;
									break;
								}
							}

							if(!isHostNo) {
//								System.out.println("JSON don't contains current host number." + hostNo);
								return;
							}

							//2. if *, query all devNo, and put them to thread blocking queue.
							//otherwise get every single devNo, put them to thread blocking queue.
							JsonNode array = JacksonFactory.findJsonNode(payload, "/devNo");
//							System.out.println(array.size());
							if(array.size() == 1 && array.get(0).asText().equals("*")) {	//put all exist devNo on host.
								App.semaphore.acquire();
								Session session = App.getSession();
								if(!session.isOpen()) {
									System.out.println("[topicReadData] sqlite session is closed.");
									return;
								}
								Query query = session.createQuery("from Device");
								List<Device> list = query.list();
								if(list != null && list.size() > 0) {
									for(Device device : list) {
										DataProcessThread.queueAdd(device.getDevNo());
//										System.out.println(device.getDevNo());
									}
								}
							} else {		//only put specific devNo
								int i;
								for (i = 0; i < array.size(); i++) {
									DataProcessThread.queueAdd(array.get(i).asText());
								}
							}
						} catch (Exception e) {
							System.out.println("[topicReadData]  :" + e.getCause());
							System.out.println("[topicReadData]  :" +e.getMessage());
							DataProcessThread.clearQueue();
							App.closeSession();
						} finally {
							App.semaphore.release();
						}
					}
				}

				public void run() {
					while (true) {
						try {
							this.connect();
//							System.out.println("0.----- connect");
							Thread.sleep(8000);
						} catch (Throwable e) {
							;
						}

						while (!this.isConnect()) {
							try {
//								this.disconnect();
//								System.out.println("1.----- connect");
								Thread.sleep(60000);
								this.connect();
								Thread.sleep(8000);
//								System.out.println("2.----- sleep");
//								this.disconnect();
							} catch (Throwable e) {
								;
							}
						}

						try {
							Thread.sleep(5000);
							if (isConnect())
								this.subscribe(this.getSubTopic(), qos);
						} catch (Throwable e) {
							e.printStackTrace();
						}

						while (true) {
							try {
								Thread.sleep(5000);
								//Host is not registered.
								if (!App.isHostRegistered() && isConnect()) {
									Host host = Host.getHostInstance();
									String hostData = host.doSerialize();
									if (hostData != null)
										this.publish(App.topicHostRegister, qos, hostData.getBytes("UTF-8"));
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}    //end of while(true)
					}
				}
			};
		
			talkTopics.setSubTopic(subTopic);
		
			Thread t = new Thread(talkTopics);
			t.start();
		}
	}
	
	public static MqttAsyncCallback getInstanceOfTalkTopics() {
		TopicFactory tf = null;
		try {
			tf = TopicFactory.getInstance();
		} catch (Throwable e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(-1);
		}
		
		if(null == talkTopics) {
			try {
				tf.talkOnTopics();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return talkTopics;
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
//				super.messageArrived(topic, message);

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
					List list = query.list();
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
								"\"ErrNumber\":0,\n" +
								"\"Description\":\"Host registers successfully.\"\n" +
								"}";
				if(!result) jsonData = "{\n" +
								"\"ErrNumber\":-1,\n" +
								"\"Description\":\"Host has been registered.\"\n" +
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
				"\"hostID\" : \"8\",\n" +
//				"\"areaID\" : 9,\n" +
//				"\"buildingID\" : 10,\n" +
				"\"remoteServerAddr\" : \"10.0.1.123\",\n" +
				"\"floor\" : 20,\n" +
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
						while(true) {
							try {
								jsonData = queue.poll(0, TimeUnit.DAYS);        //infinitely wait
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
	}

	private void talkOnRegisterDevices() throws Throwable {
		//subscribe topic /control/register/dcms/device to register device in mysql
		String subTopic = "/control/register/dcms/device";
		pubTopic = "/control/register/dcms/device/ack";
		action = "subscribe";

		//create subscribe client
		clientId = subTopic + " " + action;
		MqttAsyncCallback registerDeviceSubClient =
				new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password) {
					public void messageArrived(String topic, MqttMessage message) throws Exception {
						super.messageArrived(topic, message);
						//store JSON data in redis
						ObjectMapper mapper = new ObjectMapper();
						Device device = mapper.readValue(message.getPayload(), Device.class);

						//To persist data
						boolean result = true;
						try {
							App.bePersistedObject(device);
						} catch (Throwable throwable) {
							throwable.printStackTrace();
							result = false;
						}

						String reply = "{\n" +
								"\"ErrNumber\":0,\n" +
								"\"Description\":\"Device registers successfully.\"\n" +
								"}";
						if(!result) reply = "{\n" +
								"\"ErrNumber\":-1,\n" +
								"\"Description\":\"Device has been registered.\"\n" +
								"}";

						try {
							super.publish(pubTopic,qos,reply.getBytes());
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
		registerDeviceSubClient.setSubTopic(subTopic);
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
		RList<String> rList = redissonClient.getList(key.asText());
		rList.add(value.toString());
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

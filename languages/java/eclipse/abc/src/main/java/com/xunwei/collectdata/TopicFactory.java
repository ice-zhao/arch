package com.xunwei.collectdata;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.xunwei.services.MqttAsyncCallback;

public class TopicFactory {
	// Default settings:
	private boolean quietMode 	= false;
	private String action 		= "publish";
	private String topic 		= "";
	private String message 		= "Message from async callback Paho MQTTv3 Java client sample";
	private int qos 			= 2;
	private String broker 		= "m2m.eclipse.org";
	private int port 			= 1883;
	private String clientId 	= null;
	private String subTopic		= "Sample/#";
	private String pubTopic 	= "Sample/Java/v3";
	private boolean cleanSession = true;			// Non durable subscriptions
	private String password = null;
	private String userName = null;
//	private boolean ssl = false;
	
	private String protocol = "tcp://";
	private String url = protocol + broker + ":" + port;
    
	public TopicFactory(String[] args) {
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
					case 't': topic = args[++i];                  break;
					case 'm': message = args[++i];                break;
					case 's': qos = Integer.parseInt(args[++i]);  break;
					case 'b': broker = args[++i];                 break;
					case 'p': port = Integer.parseInt(args[++i]); break;
					case 'i': clientId = args[++i];				  break;
					case 'c': cleanSession = Boolean.valueOf(args[++i]).booleanValue();  break;
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
	
	public void startAllTopics() {
		try {
			talkOnRegisterHost();
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
	}
	
	private void talkOnRegisterHost() throws Throwable {
		subTopic = "/control/register/dcms";
		pubTopic = "/control/register/dcms/ack";
		action 	= "subscribe";
		broker = "LocalHost";
		url = protocol + broker + ":" + port;
		
		// Create an instance of the publish client wrapper
		action 	= "publish";
		clientId = pubTopic + " " + action;
	    // Create an instance of the publish client wrapper
		MqttAsyncCallback regHostPubAckClient = 
				new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password);
		regHostPubAckClient.connect();
//		topic = pubTopic;
//		regHostPubAckClient.publish(topic,qos,message.getBytes());
		
		action 	= "subscribe";
		clientId = subTopic + " " + action;
	    // Create an instance of the subscribe client wrapper
		MqttAsyncCallback regHostSubClient = 
				new MqttAsyncCallback(url,clientId,cleanSession, quietMode,userName,password) {
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				super.messageArrived(topic, message);
				//TODO:parse received JSON data.
				String jsonData = "process data successfully.";
				
				try {
//					System.out.println(pubAckTopic);
					
					regHostPubAckClient.publish(pubTopic,qos,jsonData.getBytes());
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		regHostSubClient.connect();
		topic = subTopic;
		regHostSubClient.subscribe(topic,qos);
		
		
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
		topic = pubHostTopic;
		regHostPubClient.publish(topic,qos,message.getBytes());
	}
	
	public void testLocalTopic() {
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
	}
	
	public void testRemoteTopic() {
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
    }
	
    static void printHelp() {
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

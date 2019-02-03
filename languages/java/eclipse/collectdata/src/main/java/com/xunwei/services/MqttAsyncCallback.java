package com.xunwei.services;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;


public class MqttAsyncCallback implements MqttCallback {
	private int state = BEGIN;
	private String topic 		= "";
	private int qos 			= 2;
	String broker 		= "m2m.eclipse.org";
	int port 			= 1883;
	String clientId 	= null;
	String subTopic		= "Sample/#";
	String pubTopic 	= "Sample/Java/v3";
	private boolean cleanSession = true;			// Non durable subscriptions
	boolean ssl = false;
	private MqttAsyncClient 	client;
	private String 				brokerUrl;
	private boolean 			quietMode = true;
	private MqttConnectOptions 	conOpt;
	private boolean 			clean;
	Throwable 			ex = null;
	private final Object 	waiter = new Object();
	boolean 			donext = false;
	private String password;
	private String userName;
	private static int totalMessages = 0;

	static final int BEGIN = 0;
	static final int CONNECTED = 1;
	static final int PUBLISHED = 2;
	static final int SUBSCRIBED = 3;
	static final int DISCONNECTED = 4;
	static final int FINISH = 5;
	static final int ERROR = 6;
	static final int DISCONNECT = 7;
	
	/**
	 * Constructs an instance of the MQTT client wrapper
	 * @param brokerUrl the url to connect to
	 * @param clientId the client id to connect with
	 * @param cleanSession clear state at end of connection or not (durable or non-durable subscriptions)
	 * @param quietMode whether debug should be printed to standard out
	 * @param userName the username to connect with
	 * @param password the password for the user
	 * @throws MqttException
	 */
    public MqttAsyncCallback(String brokerUrl, String clientId, boolean cleanSession,
    		boolean quietMode, String userName, String password) throws MqttException {
    	this.brokerUrl = brokerUrl;
    	this.setQuietMode(quietMode);
    	this.clean 	   = cleanSession;
    	this.password = password;
    	this.userName = userName;
    	//This sample stores in a temporary directory... where messages temporarily
    	// stored until the message has been delivered to the server.
    	//..a real application ought to store them somewhere
    	// where they are not likely to get deleted or tampered with
    	String tmpDir = System.getProperty("java.io.tmpdir");
    	MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);

    	try {
    		// Construct the object that contains connection parameters
    		// such as cleanSession and LWT
	    	conOpt = new MqttConnectOptions();
	    	conOpt.setCleanSession(clean);
	    	if(password != null ) {
          conOpt.setPassword(this.password.toCharArray());
        }
        if(userName != null) {
          conOpt.setUserName(this.userName);
        }

    		// Construct the MqttClient instance
			client = new MqttAsyncClient(this.brokerUrl,clientId, dataStore);

			// Set this wrapper as the callback handler
	    	client.setCallback(this);

		} catch (MqttException e) {
			e.printStackTrace();
			log("Unable to set up client: "+e.toString());
			System.exit(1);
		}
    }
    
    
    private MqttConnector con = null;
    private Publisher pub = null;
    private Subscriber sub = null;
    
    public void connect() throws Throwable {
    	// Use a state machine to decide which step to do next. State change occurs
    	// when a notification is received that an MQTT action has completed
		if(null == con)
			con = new MqttConnector();
		con.doConnect();
	
		waitForStateChange(10000);
    }
    
    /**
     * Publish / send a message to an MQTT server
     * @param topicName the name of the topic to publish to
     * @param qos the quality of service to delivery the message at (0,1,2)
     * @param payload the set of bytes to send to the MQTT server
     * @throws MqttException
     */
    public void publish(String topicName, int qos, byte[] payload) throws Throwable {
		if(null == pub)
			pub = new Publisher();
		pub.doPublish(topicName, qos, payload);
	
		waitForStateChange(10000);
    }

    /**
     * Wait for a maximum amount of time for a state change event to occur
     * @param maxTTW  maximum time to wait in milliseconds
     * @throws MqttException
     */
	private void waitForStateChange(int maxTTW ) throws MqttException {
		synchronized (waiter) {
    		if (!donext ) {
    			try {
					waiter.wait(maxTTW);
				} catch (InterruptedException e) {
					log("timed out");
					e.printStackTrace();
				}

				if (ex != null) {
					throw (MqttException)ex;
				}
    		}
    		donext = false;
    	}
	}

    /**
     * Subscribe to a topic on an MQTT server
     * Once subscribed this method waits for the messages to arrive from the server
     * that match the subscription. It continues listening for messages until the enter key is
     * pressed.
     * @param topicName to subscribe to (can be wild carded)
     * @param qos the maximum quality of service to receive messages at for this subscription
     * @throws MqttException
     */
    public void subscribe(String topicName, int qos) throws Throwable {
    	// Use a state machine to decide which step to do next. State change occurs
    	// when a notification is received that an MQTT action has completed
//    	while (state != FINISH) {
//    		switch (state) {
//    			case BEGIN:
//    				// Connect using a non-blocking connect
//    				if(null == con)
//    					con = new MqttConnector();
//    		    	con.doConnect();
//    				break;
//    			case CONNECTED:
    				// Subscribe using a non-blocking subscribe
    				if(null == sub)
    					sub = new Subscriber();
    				sub.doSubscribe(topicName, qos);
//    				break;
//    			case SUBSCRIBED:
//    		    	// Block until Enter is pressed allowing messages to arrive
//    		    	log("Press <Enter> to exit");
//    				try {
//    					System.in.read();
//    				} catch (IOException e) {
//    					//If we can't read we'll just exit
//    				}
//    				state = DISCONNECT;
//    				donext = true;
//    				break;
//    			case DISCONNECT:
//    				Disconnector disc = new Disconnector();
//    				disc.doDisconnect();
//    				break;
//    			case ERROR:
//    				throw ex;
//    			case DISCONNECTED:
//    				state = FINISH;
//    				donext = true;
//    				break;
//    		}

//    		if (state != FINISH && state != DISCONNECT) {
    			waitForStateChange(10000);
    		}
//    	}
//    }
    
	public void connectionLost(Throwable cause) {
		// Called when the connection to the server has been lost.
		// An application may choose to implement reconnection
		// logic at this point. This sample simply exits.
		log("Connection to " + brokerUrl + " lost!" + cause);
		System.exit(1);
		
	}
	
	public void deliveryComplete(IMqttDeliveryToken token) {
		// Called when a message has been delivered to the
		// server. The token passed in here is the same one
		// that was returned from the original call to publish.
		// This allows applications to perform asynchronous
		// delivery without blocking until delivery completes.
		//
		// This sample demonstrates asynchronous deliver, registering
		// a callback to be notified on each call to publish.
		//
		// The deliveryComplete method will also be called if
		// the callback is set on the client
		//
		// note that token.getTopics() returns an array so we convert to a string
		// before printing it on the console
		log("Delivery complete callback: Publish Completed "+Arrays.toString(token.getTopics()));
		
	}
	
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// Called when a message arrives from the server that matches any
		// subscription made by the client
		String time = new Timestamp(System.currentTimeMillis()).toString();
		System.out.println("No."+ ++totalMessages + "  Time:\t" +time +
                           "  Topic:\t" + topic +
                           "  Message:\t" + new String(message.getPayload()) +
                           "  QoS:\t" + message.getQos());
		
	}
	/**
	 * Connect in a non-blocking way and then sit back and wait to be
	 * notified that the action has completed.
	 */
    public class MqttConnector {
    	private boolean isConnect = false;
    	
		public MqttConnector() {
		}

		public void doConnect() {
			if(isConnect)
				return;
	    	// Connect to the server
			// Get a token and setup an asynchronous listener on the token which
			// will be notified once the connect completes
	    	log("Connecting to "+brokerUrl + " with client ID "+client.getClientId());

	    	IMqttActionListener conListener = new IMqttActionListener() {
				public void onSuccess(IMqttToken asyncActionToken) {
			    	log("Connected");
			    	state = CONNECTED;
			    	isConnect = true;
			    	carryOn();
				}

				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					ex = exception;
					state = ERROR;
					isConnect = false;
					log ("connect failed" +exception);
					carryOn();
				}

				public void carryOn() {
			    	synchronized (waiter) {
			    		donext=true;
			    		waiter.notifyAll();
			    	}
				}
			};

	    	try {
	    		// Connect using a non-blocking connect
	    		client.connect(conOpt,"Connect sample context", conListener);
			} catch (MqttException e) {
				// If though it is a non-blocking connect an exception can be
				// thrown if validation of parms fails or other checks such
				// as already connected fail.
				state = ERROR;
				donext = true;
				ex = e;
			}
		}
	}

	/**
	 * Publish in a non-blocking way and then sit back and wait to be
	 * notified that the action has completed.
	 */
	public class Publisher {
		private IMqttActionListener pubListener = null;
		public void doPublish(String topicName, int qos, byte[] payload) {
		 	// Send / publish a message to the server
			// Get a token and setup an asynchronous listener on the token which
			// will be notified once the message has been delivered
	   		MqttMessage message = new MqttMessage(payload);
	    	message.setQos(qos);


	    	String time = new Timestamp(System.currentTimeMillis()).toString();
	    	log("Publishing at: "+time+ " to topic \""+topicName+"\" qos "+qos);

	    	// Setup a listener object to be notified when the publish completes.
	    	//
	    	if(null == pubListener) {
		    	pubListener = new IMqttActionListener() {
					public void onSuccess(IMqttToken asyncActionToken) {
				    	log("Publish Completed");
	//			    	state = PUBLISHED;
				    	state = FINISH;
				    	carryOn();
					}
	
					public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
						ex = exception;
						state = ERROR;
						log ("Publish failed" +exception);
						carryOn();
					}
	
					public void carryOn() {
				    	synchronized (waiter) {
				    		donext=true;
				    		waiter.notifyAll();
				    	}
					}
				};
	    	}

	    	try {
		    	// Publish the message
	    		client.publish(topicName, message, "Pub sample context", pubListener);
	    	} catch (MqttException e) {
				state = ERROR;
				donext = true;
				ex = e;
			}
		}
	}

	/**
	 * Subscribe in a non-blocking way and then sit back and wait to be
	 * notified that the action has completed.
	 */
	public class Subscriber {
		private boolean isSubscribe = false;
		public void doSubscribe(String topicName, int qos) {
			if(isSubscribe)
				return;
		 	// Make a subscription
			// Get a token and setup an asynchronous listener on the token which
			// will be notified once the subscription is in place.
	    	log("Subscribing to topic \""+topicName+"\" qos "+qos);

	    	IMqttActionListener subListener = new IMqttActionListener() {
				public void onSuccess(IMqttToken asyncActionToken) {
			    	log("Subscribe Completed");
//			    	state = SUBSCRIBED;
			    	isSubscribe = true;
			    	carryOn();
				}

				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					ex = exception;
					state = ERROR;
					isSubscribe = false;
					log ("Subscribe failed" +exception);
					carryOn();
				}

				public void carryOn() {
			    	synchronized (waiter) {
			    		donext=true;
			    		waiter.notifyAll();
			    	}
				}
			};

	    	try {
	    		client.subscribe(topicName, qos, "Subscribe sample context", subListener);
	    	} catch (MqttException e) {
				state = ERROR;
				donext = true;
				ex = e;
			}
		}
	}

	/**
	 * Disconnect in a non-blocking way and then sit back and wait to be
	 * notified that the action has completed.
	 */
	public class Disconnector {
		public void doDisconnect() {
	    	// Disconnect the client
	    	log("Disconnecting");

	    	IMqttActionListener discListener = new IMqttActionListener() {
				public void onSuccess(IMqttToken asyncActionToken) {
			    	log("Disconnect Completed");
			    	state = DISCONNECTED;
			    	carryOn();
				}

				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					ex = exception;
					state = ERROR;
					log ("Disconnect failed" +exception);
					carryOn();
				}
				public void carryOn() {
			    	synchronized (waiter) {
			    		donext=true;
			    		waiter.notifyAll();
			    	}
				}
			};

	    	try {
	    		client.disconnect("Disconnect sample context", discListener);
	    	} catch (MqttException e) {
				state = ERROR;
				donext = true;
				ex = e;
			}
		}
	}
	
    /**
     * Utility method to handle logging. If 'quietMode' is set, this method does nothing
     * @param message the message to log
     */
    private void log(String message) {
    	if (!quietMode) {
    		System.out.println(message);
    	}
    }
    
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}

	public boolean isQuietMode() {
		return quietMode;
	}

	public void setQuietMode(boolean quietMode) {
		this.quietMode = quietMode;
	}

	public boolean isCleanSession() {
		return cleanSession;
	}

	public void setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
	}
}

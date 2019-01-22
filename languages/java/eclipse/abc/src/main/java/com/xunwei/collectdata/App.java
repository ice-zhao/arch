package com.xunwei.collectdata;
import org.hibernate.cfg.Configuration;

import com.xunwei.collectdata.devices.Device;
import com.xunwei.collectdata.devices.News;
import com.xunwei.services.MqttAsyncCallback;

import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;


public class App 
{
	private static final SessionFactory concreteSessionFactory;
	static {
		try {
			concreteSessionFactory = new Configuration()
					.configure("com/xunwei/collectdata/hibernate.cfg.xml")
					.buildSessionFactory();
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static Session getSession() throws HibernateException {
		return concreteSessionFactory.openSession();
	}
	
    public static void closeSession(Session session){
        if(session != null){
            session.close();
        }
    }
    
    public static void closeSessionFactory(){
        if(concreteSessionFactory != null){
        	concreteSessionFactory.close();
        }
    }
	
    public static void main( String[] args )
    {
		// Default settings:
		boolean quietMode 	= false;
		String action 		= "publish";
		String topic 		= "";
		String message 		= "Message from async callback Paho MQTTv3 Java client sample";
		int qos 			= 2;
		String broker 		= "m2m.eclipse.org";
		int port 			= 1883;
		String clientId 	= null;
		String subTopic		= "Sample/#";
		String pubTopic 	= "Sample/Java/v3";
		boolean cleanSession = true;			// Non durable subscriptions
		String password = null;
		String userName = null;
		boolean ssl = false;
		
		String protocol = "tcp://";
	    String url = protocol + broker + ":" + port;
	    
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
          case 'v': ssl = Boolean.valueOf(args[++i]).booleanValue();  break;
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


//hibernate sample code
//get hibernate session
//Session sess = getSession();
//Transaction tx = sess.beginTransaction();

/*    	//Load all records.
@SuppressWarnings("unchecked")
List<News> newsEntity = (List<News>) sess.createQuery("from News").list();
for(News item : newsEntity) {
	System.out.println(item.getTitle());
}*/

//load one record bases on identifier
//News oneNews = sess.load(News.class, new Integer(1));
//System.out.println(oneNews.getTitle());
//Device oneDev = sess.load(Device.class, new Integer(9));
//System.out.println(oneDev.getDeviceName());
//News ups = new News();
//ups.setTitle("this is a UPS");
//ups.setDevice(oneDev);
//sess.save(ups);

/*    	Device dev = new Device();
dev.setDeviceId(8);
dev.setDeviceName("Ameter");

News news = new News();
news.setTitle("try hibernate3");
news.setDevice(dev);

News news1 = new News();
news1.setTitle("new device");
news1.setDevice(dev);

sess.save(dev);
sess.save(news);
sess.save(news1);*/

//tx.commit();
//
//closeSession(sess);
//closeSessionFactory();
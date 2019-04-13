package com.xunwei.collectdata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.alert.AlertProcessThread;
import com.xunwei.collectdata.devices.DeviceRegisterThread;
import com.xunwei.collectdata.utils.JacksonFactory;
import com.xunwei.collectdata.utils.TestData;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

//import com.xunwei.collectdata.devices.Device;
//import com.xunwei.collectdata.devices.News;
//import com.xunwei.services.MqttAsyncCallback;

//import java.util.List;

//import org.eclipse.paho.client.mqttv3.MqttException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
import org.apache.log4j.*;

import java.util.HashMap;

public class App 
{
	private static final SessionFactory concreteSessionFactory;
	private static final SessionFactory dataSessionFactory;
	private static boolean isHostRegistered = false;
	
	public static final String topicHostRegister = "/control/register/host";
	public static final String topicHostAck = "/control/register/host/ack";
	public static final String topicDevRegister = "/control/register/host/device";
	public static final String topicDevAck = "/control/register/host/device/ack";
	public static final String topicReadData = "/control/device/data/read";
	public static final String topicDevReplyData = "/devices/reply/data";
	
	static {
		BasicConfigurator.configure();
//		Logger.getLogger("org.hibernate").setLevel(Level.ERROR);
//		Logger.getLogger("org.jboss").setLevel(Level.ERROR);
//		Logger.getLogger("com.mchange").setLevel(Level.ERROR);
//		Logger.getLogger("org.redisson").setLevel(Level.ERROR);

		try {
			concreteSessionFactory = new Configuration()
					.configure("hibernate.cfg.xml")
					.buildSessionFactory();
			dataSessionFactory = new Configuration()
					.configure("hibernate.data.xml")
					.buildSessionFactory();
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static Session getSession() throws HibernateException {
		return concreteSessionFactory.openSession();
	}

	public static Session getDataSession() throws HibernateException {
		return dataSessionFactory.openSession();
	}
	
    static void closeSession(Session session){
        if(session != null){
        	try {
				session.close();
			} catch (Exception e) {
        		e.printStackTrace();
			}

        }
    }
    
    public static void closeSessionFactory(){
        if(concreteSessionFactory != null){
        	concreteSessionFactory.close();
        }
    }

	public static void closeDataSessionFactory(){
		if(dataSessionFactory != null){
			dataSessionFactory.close();
		}
	}
	
    public static void main( String[] args ) {
		TopicFactory topicFactory = TopicFactory.getInstance(args);
    	topicFactory.startAllTopics();

    	//periodic register device to cloud
        DeviceRegisterThread deviceRegisterThread = new DeviceRegisterThread();
        deviceRegisterThread.start();

		//start data process thread
		Thread t = new DataProcessThread();
		t.start();

		//start alert process thread
		/*Thread alert = new AlertProcessThread();
		alert.start();*/

		//produce Testing data
/*		TestData testData = new TestData();
		try {
//			testData.produceAlertData();
			testData.produceDeviceData();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}*/
	}

    public static void bePersistedObject(Object object) throws Throwable {
		Session sess = getSession();
		Transaction tx = sess.beginTransaction();
		sess.save(object);
		try {
			tx.commit();
		} catch (Exception ex)
		{
			throw ex;
		} finally {
			closeSession(sess);
		}
	}

	public static boolean isHostRegistered() {
		return isHostRegistered;
	}

	public static void setHostRegistered(boolean isHostRegistered) {
		App.isHostRegistered = isHostRegistered;
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

//        Config config = Config.fromJSON(new File("src/main/resources/config-redisson.json"));
//        RedissonClient redissonClient = Redisson.create(config);
//        RMap<String,Integer> mymap = redissonClient.getMap("mymap");
//        mymap.put("a", 1);
//        mymap.put("b", 23);
//		RBucket bucket = redissonClient.getBucket("test");
//		bucket.set("how are you.");
//
//		//to get all keys
//		RKeys rKeys = redissonClient.getKeys();
//		Iterable<String> allKeys = rKeys.getKeys();
//		for(String key : allKeys)
//			System.out.println(key);
//
//        redissonClient.shutdown();


//	HashMap<String, Object> hashMap = new HashMap<String, Object>();
//	HashMap<String, Object> hashMapValue = new HashMap<String, Object>();
//		hashMap.put("key","1234");
//				hashMapValue.put("time","2019-01-12");
//				hashMap.put("value",hashMapValue);
//				ObjectMapper objectMapper = JacksonFactory.getObjectMapper();
//				String json1=null;
//				try {
//				json1 = objectMapper.writeValueAsString(hashMap);
//				} catch (JsonProcessingException e) {
//				e.printStackTrace();
//				}
//
//				System.out.println(json1);
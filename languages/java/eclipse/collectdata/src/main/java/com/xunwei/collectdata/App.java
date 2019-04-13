package com.xunwei.collectdata;
import com.xunwei.collectdata.alert.AlertProcessThread;
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

public class App 
{
	private static final SessionFactory concreteSessionFactory;
	static {
		BasicConfigurator.configure();
		Logger.getLogger("org.hibernate").setLevel(Level.ERROR);
		Logger.getLogger("org.jboss").setLevel(Level.ERROR);
		Logger.getLogger("com.mchange").setLevel(Level.ERROR);
		Logger.getLogger("org.redisson").setLevel(Level.ERROR);

		try {
			concreteSessionFactory = new Configuration()
					.configure("hibernate.cfg.xml")
					.buildSessionFactory();
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static Session getSession() throws HibernateException {
		return concreteSessionFactory.openSession();
	}
	
    static void closeSession(Session session){
        if(session != null){
            session.close();
        }
    }
    
    public static void closeSessionFactory(){
        if(concreteSessionFactory != null){
        	concreteSessionFactory.close();
        }
    }
	
    public static void main( String[] args ) {
    	TopicFactory topicFactory = TopicFactory.getInstance(args);
    	topicFactory.startAllTopics();

		//start data process thread
		Thread t = new DataProcessThread();
		t.start();

		//start alert process thread
		Thread alert = new AlertProcessThread();
		alert.start();

/*		//produce Testing data
		TestData testData = new TestData();
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
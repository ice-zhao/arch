package com.xunwei.collectdata;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.alert.AlertProcessThread;
import com.xunwei.collectdata.utils.TestData;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
//import org.hibernate.mapping.List;
import org.hibernate.query.Query;
import org.hibernate.type.TimestampType;

//import com.xunwei.collectdata.devices.Device;
//import com.xunwei.collectdata.devices.News;
//import com.xunwei.services.MqttAsyncCallback;

//import java.util.List;

//import org.eclipse.paho.client.mqttv3.MqttException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import com.xunwei.collectdata.alert.SysAlert;
import com.xunwei.collectdata.devices.Host;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
//import org.hibernate.Transaction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.*;
//import org.slf4j.*;
public class App 
{
	private static final SessionFactory concreteSessionFactory;
	static {
		BasicConfigurator.configure();
		Logger.getLogger("org.hibernate").setLevel(Level.ERROR);
		Logger.getLogger("org.jboss").setLevel(Level.ERROR);
		Logger.getLogger("com.mchange").setLevel(Level.ERROR);
		
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
	
    public static void main( String[] args ) throws Throwable {
    	TopicFactory topicFactory = TopicFactory.getInstance(args);
    	topicFactory.startAllTopics();

//    	while(true) {
//    		Thread.sleep(5000);
//    		System.out.println("main task sleeping.");
//    	}
		//start data process thread
//		Thread t = new DataProcessThread();
//		t.start();
//    	Logger log = Logger.getLogger(App.class);
//    	log.info("-----------------only for test----");
//    	Logger.getLogger("org.hibernate").setLevel(Level.DEBUG);

/*    	
    	Host host1 = Host.getHostInstance();
    	System.out.println(host1.doSerialize());
    	System.exit(0);
    	
    	
    	String dcmsJson = "{\n" +
				"\"hostID\" : \"8\",\n" +
//				"\"areaID\" : 9,\n" +
//				"\"buildingID\" : 10,\n" +
				"\"remoteServerAddr\" : \"10.0.1.123\",\n" +
//				"\"floor\" : 20,\n" +
				"\"serial\" : \"user defined\"\n" +
				"}";
    	ObjectMapper mapper = new ObjectMapper();
		Host host = mapper.readValue(dcmsJson, Host.class);
		
		System.out.println(host.getRemoteServerAddr());
		
		Map<String, Object> jmap = new HashMap<>();
		jmap.put("remoteServerAddr", "10.0.1.111");
		String json = mapper.writeValueAsString(jmap);
		System.out.println(json);
		
		Host newhost = mapper.readValue(json, Host.class);
		System.out.println(newhost.getRemoteServerAddr());*/
		
/*
    	SysAlert sa = new SysAlert();
    	sa.setDeviceNumber(1);
    	sa.setInfo("hahahaha");
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date d;
    	Session sess = App.getSession();
		try {
//			d = sdf.parse("2019-03-09 15:03:30");
			d = sdf.parse("2019-03-09 00:10:00");
			sa.setTimestamp(d);
			sa.setEndTime(d);
			Query<?> query = sess.createQuery("select 1 from SysAlert where timestamp = :time");
			query.setParameter("time", sa.getTimestamp(), TimestampType.INSTANCE);
			List<?> ls = (List<?>) query.getResultList();
			System.out.println(ls);
//			System.out.println(query.getResultList());
			if(ls.isEmpty())
				App.bePersistedObject(sa);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sess.close();
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
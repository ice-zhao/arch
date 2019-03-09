package com.xunwei.collectdata;
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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
//import org.hibernate.Transaction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


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
//    	TopicFactory topicFactory = TopicFactory.getInstance(args);
//    	topicFactory.startAllTopics();

		//start data process thread
//		Thread t = new DataProcessThread();
//		t.start();
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
		}
    	
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
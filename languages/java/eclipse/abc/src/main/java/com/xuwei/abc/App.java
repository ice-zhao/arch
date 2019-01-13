package com.xuwei.abc;
import org.hibernate.cfg.Configuration;

import com.xuwei.abc.devices.Device;
import com.xuwei.abc.devices.News;

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
					.configure("com/xuwei/abc/hibernate.cfg.xml")
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
    	//get hibernate session
    	Session sess = getSession();
    	Transaction tx = sess.beginTransaction();
    	
    	Device dev = new Device();
    	dev.setDeviceId(8);
    	dev.setDeviceName("Ameter");
    	
    	News news = new News();
    	news.setTitle("try hibernate3");
    	news.setDevice(dev);
    	
    	News news1 = new News();
    	news1.setTitle("new device");
    	news1.setDevice(dev);
    	
//    	sess.save(dev);
    	sess.save(news);
    	sess.save(news1);
    	tx.commit();
    	
    	closeSession(sess);
    	closeSessionFactory();
        System.out.println( "hi" );
    }
}

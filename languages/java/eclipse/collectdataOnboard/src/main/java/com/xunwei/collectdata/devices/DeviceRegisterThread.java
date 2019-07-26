package com.xunwei.collectdata.devices;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.xunwei.collectdata.App;
import com.xunwei.collectdata.ICommon;
import com.xunwei.collectdata.TopicFactory;
import com.xunwei.collectdata.utils.ErrorInfo;
import com.xunwei.services.MqttAsyncCallback;
import com.xunwei.services.daos.DeviceService;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DeviceRegisterThread extends Thread implements ICommon {
	//to store deviceNo and device instance.
	private HashMap<String, Device> devices = new HashMap<String, Device>();
	private static Semaphore acknowledge = new Semaphore(0, true);
	private static int errStatus = ErrorInfo.FAIL;
	private static int delayCleanData = 0;

	MqttAsyncCallback mqttClient = TopicFactory.getInstanceOfTalkTopics();

	public void run() {
		while(true) {
			try {
				Thread.sleep(30000);
				if(!App.isHostRegistered() || !mqttClient.isConnect())
					continue;

				cleanupData();
//				System.out.println("############ in device register thread.");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 1. readData
			//2. publish a device info
			if(App.isHostRegistered() && readData()) {
				storeData();
			}
		}
	}

	public Boolean readData() {
		//1. check Map if has device, if not exist, add it to map
		List<Device> list = null;
		DeviceService deviceService = DeviceService.getInstance();
		list = deviceService.getAllDevices();

		for(Device item : list) {
			if(!devices.containsKey(item.getDevNo()))
				devices.put(item.getDevNo(), item);
		}
		return true;
	}

	public Boolean processData() {
		// TODO Auto-generated method stub
		return true;
	}

	public Boolean storeData() {
		MqttAsyncCallback mqttTalkTopics = TopicFactory.getInstanceOfTalkTopics();
		int count = 0;
		for(Entry<String, Device> entry : devices.entrySet()) {
		// TODO 1. only send message for unregistered device.
			Device dev = entry.getValue();
			if(dev.isRegistered() || dev.isViolate())
				continue;
			
			count = 0;
			errStatus = ErrorInfo.FAIL;
			while(errStatus != ErrorInfo.SUCCESS) {
				try {
					mqttTalkTopics.publish(App.topicDevRegister, 2, dev.doSerialize().getBytes("UTF-8"));
					waitAcknowledge();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(errStatus == ErrorInfo.SUCCESS)
					dev.setRegistered(true);
				
				if(++count >= 3)
					break;
			}

			if(count >= 3)
				dev.setViolate(true);
		}
		
		return true;
	}

	public Boolean cleanupData() {
		//to check if device has been deleted.
//		if(delayCleanData > 6 && !devices.isEmpty())
//			devices.clear();
//
//		delayCleanData = ++delayCleanData % 6;
		return true;
	}

	public String doSerialize() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean waitAcknowledge() throws InterruptedException {
		acknowledge.tryAcquire(30, TimeUnit.SECONDS);
		return true;
	}
	
	public static void sendAcknowledge(int errstatus) {
		acknowledge.release();
		errStatus = errstatus;
	}
}

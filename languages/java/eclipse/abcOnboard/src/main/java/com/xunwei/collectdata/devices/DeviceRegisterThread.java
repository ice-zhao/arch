package com.xunwei.collectdata.devices;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import com.xunwei.collectdata.App;
import com.xunwei.collectdata.ICommon;
import com.xunwei.collectdata.TopicFactory;
import com.xunwei.collectdata.utils.ErrorInfo;
import com.xunwei.services.MqttAsyncCallback;

public class DeviceRegisterThread extends Thread implements ICommon {
	//to store deviceNo and device instance.
	private HashMap<String, Device> devices = new HashMap<>();
	private static Semaphore acknowledge = new Semaphore(1, false);
	private static int errStatus = ErrorInfo.FAIL;
	
	public void run() {
		while(true) {
			//TODO 1. readData
			//2. publish a device info
			

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public Boolean readData() {
		// TODO 1. check Map if has device, if not exist, add it to map
		return true;
	}

	@Override
	public Boolean processData() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Boolean storeData() {
		MqttAsyncCallback mqttTalkTopics = TopicFactory.getInstanceOfTalkTopics();
		int count = 0;
		for(Map.Entry<String, Device> entry : devices.entrySet()) {
		// TODO 1. only send message for unregistered device.
			Device dev = entry.getValue();
			if(dev.isRegistered())
				continue;
			
			count = 0;
			errStatus = ErrorInfo.FAIL;
			while(errStatus != ErrorInfo.SUCCESS) {
				try {
					mqttTalkTopics.publish(App.topicDevRegister, 2, dev.doSerialize().getBytes());
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
		}
		
		return true;
	}

	@Override
	public Boolean cleanupData() {
		// TODO to check if device has been deleted.
		return true;
	}

	@Override
	public String doSerialize() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean waitAcknowledge() throws InterruptedException {
		acknowledge.acquire();
		return true;
	}
	
	public static void sendAcknowledge(int errstatus) {
		acknowledge.release();
		acknowledge.notifyAll();
		errStatus = errstatus;
	}
}

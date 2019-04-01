package com.xunwei.collectdata.devices;

import java.util.HashMap;
import java.util.concurrent.Semaphore;

import com.xunwei.collectdata.ICommon;

public class DeviceRegisterThread extends Thread implements ICommon {
	//to store deviceNo and device instance.
	private HashMap<String, Device> devices = new HashMap<>();
	private static Semaphore acknowledge = new Semaphore(1, false);
	
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
		// TODO 1. only send message for unregistered device.
		
		try {
			waitAcknowledge();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public Boolean cleanupData() {
		// TODO Auto-generated method stub
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
	
	public static void sendAcknowledge() {
		acknowledge.release();
	}
}

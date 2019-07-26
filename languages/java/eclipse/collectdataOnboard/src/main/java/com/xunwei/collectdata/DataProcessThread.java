package com.xunwei.collectdata;

import com.xunwei.collectdata.devices.Device;
import com.xunwei.services.MqttAsyncCallback;
import com.xunwei.services.daos.DeviceService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class DataProcessThread extends Thread {
	//store devNo
	private static final ArrayBlockingQueue<String> arrayBlockQueue = new ArrayBlockingQueue<String>(1024);
	//devNo,deviceType
	private static final HashMap<String,Integer> devNoTypeMap = new HashMap<String, Integer>();

	//devNo,devid
	private static final HashMap<String,Integer> devNoIdMap = new HashMap<String, Integer>();
	MqttAsyncCallback mqttClient = TopicFactory.getInstanceOfTalkTopics();

    public void run() {
        String devNo;
        int devType = -1;
        int devid = -1;

		while(true) {
			if(!App.isHostRegistered() || !mqttClient.isConnect()) {
				try {
					Thread.sleep(5000);
					arrayBlockQueue.clear();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}

        	try {
				devNo = arrayBlockQueue.take();
//				System.out.println("take devNo from device block queue: " + devNo);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				devNo = "";
			}
        	
        	if(!devNo.equals("")) {
        		if(!devNoTypeMap.containsKey(devNo)) {
					DeviceService deviceService = DeviceService.getInstance();
					List<Device> list= deviceService.getDeviceByDevNo(devNo);

					if(list.size() <= 0) {
						devNoTypeMap.put(devNo, -1);
						devNoIdMap.put(devNo, -1);
						continue;
					}

					devType = list.get(0).getDeviceType();
					devid = list.get(0).getId();
        			devNoTypeMap.put(devNo, devType);
        			devNoIdMap.put(devNo, devid);
        		}

				devType = devNoTypeMap.get(devNo);
        		devid = devNoIdMap.get(devNo);
        		if(devid == -1 || devType == -1)
        			continue;

				DeviceType type = DeviceType.getInstance(devType);
				if(type != null) {
					AbsCommonData dataProcess = DataProcessFactory.getDataProcessInstance(type);
					if (dataProcess != null) {
						dataProcess.setDevNo(devNo);
						dataProcess.setDevId(devid);
						dataProcess.produceData();
					}
				}
        	}
        }
    }
    
    public static boolean queueAdd(String devNo) throws Exception {
    	return arrayBlockQueue.add(devNo);
    }
    
    public static Integer getDeviceType(String devNo) {
    	if(devNoTypeMap.containsKey(devNo))
    		return devNoTypeMap.get(devNo);
    	return -1;
    }

	public static Integer getDevId(String devNo) {
    	if(devNoIdMap.containsKey(devNo))
    		return devNoIdMap.get(devNo);
    	return -1;
	}

	public static void clearQueue() {
		arrayBlockQueue.clear();
	}
}

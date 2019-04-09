package xunwei.collectdata;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

public class DataProcessThread extends Thread {
	private static final ArrayBlockingQueue<String> arrayBlockQueue = new ArrayBlockingQueue<String>(1024);
	//devNo,deviceType
	private static final HashMap<String,Integer> devNoTypeMap = new HashMap<String, Integer>();
	
    public void run() {
        String devNo;
        int devType = -1;

		while(true) {
			if(!App.isHostRegistered())
				continue;

        	try {
				devNo = arrayBlockQueue.take();
//				System.out.println("@@@@@@@@@@@@@@ "+devNo);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				devNo = "";
			}
        	
        	if(!devNo.equals("")) {
        		if(!devNoTypeMap.containsKey(devNo)) {
        			//TODO: query devType, then add it to map.
        			//if query fail, continue.
        			devNoTypeMap.put(devNo, devType);
        		}

				devType = devNoTypeMap.get(devNo);
				DeviceType type = DeviceType.getInstance(devType);
//				System.out.println("@@@@@@@@@@@@@@ "+type);
				if(type != null) {
					AbsCommonData dataProcess = DataProcessFactory.getDataProcessInstance(type);
					if (dataProcess != null)
						dataProcess.produceData();
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
}

package com.xunwei.collectdata;

import com.xunwei.collectdata.devices.Device;
import org.hibernate.Query;
import org.hibernate.Session;

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
	
    public void run() {
        String devNo;
        int devType = -1;
        int devid = -1;

//        Session session = App.getDataSession();
//		Query query1;
//		query1 = session.createSQLQuery("select Value,FieldId,EntityId from t_data4 " +
//				" where EntityId = 413").addEntity(HostData.class);
//		List<HostData> list1 = query1.list();
//		System.out.println("######################### "+ list1.get(0).getValue());
//        Query query1 = session.createQuery("select id,devId,timestamp,schemaId from Entity where devId = :dev_id");
//		Query query1 = session.createSQLQuery("select id, max(timestamp), from Entity where devId = :dev_id");
//		Query query1 = session.createQuery("from Entity where devId = :dev_id and " +
//				"timestamp = (select max(timestamp) from Entity)");
//        query1.setParameter("dev_id", 2);
//        List<Entity> list1 = query1.list();
//		System.out.println("!!!!!!!!!!!!!!!!!!!!!!! " + list1.get(0).getId());

		while(true) {
//			System.out.println("!!!!!!!!!!!!!!!!!!!!!!! ");
			if(!App.isHostRegistered()) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}

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
					Session cfg_sess = App.getSession();
					Query query = cfg_sess.createQuery("from Device where devNo=:dev_No");
					query.setParameter("dev_No", devNo.replaceAll("\"", ""));
					List<Device> list = query.list();
					if(list.size() <= 0)
						continue;

					devType = list.get(0).getDeviceType();
					devid = list.get(0).getId();
//					System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%% " + devid);
        			//if query fail, continue.
        			devNoTypeMap.put(devNo, devType);
        			devNoIdMap.put(devNo, devid);
        		}

				devType = devNoTypeMap.get(devNo);
				DeviceType type = DeviceType.getInstance(devType);
				System.out.println("@@@@@@@@@@@@@@ "+type);
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
}

package com.xunwei.collectdata.alert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.App;
import com.xunwei.collectdata.FieldSignal;
import com.xunwei.collectdata.TopicFactory;
import com.xunwei.collectdata.devices.Device;
import com.xunwei.collectdata.devices.Host;
import com.xunwei.collectdata.utils.DataUtil;
import com.xunwei.collectdata.utils.JacksonFactory;
import com.xunwei.collectdata.utils.JedisClientFactory;
import com.xunwei.services.MqttAsyncCallback;
import com.xunwei.services.daos.DeviceService;
import com.xunwei.services.daos.FieldSignalService;
import redis.clients.jedis.Jedis;

import java.util.*;

public class SysAlert extends AbsCommonData {
	private String alarmName;
	private String AlarmSite;
	private Date endTime;
	private int alertLevel;
	private Long alertStartTime;
	private static HashMap<String, Object> alertData = new HashMap<String, Object>();
	private static HashMap<String, Object> alertValue = new HashMap<String, Object>();
	private static SysAlert sysAlert = null;
	private static HashMap<Integer, Device> devIdMapping = new HashMap<Integer, Device>();
//	private static Integer clean_counter = 0;
	private static HashMap<String, Long> savedSysAlert = new HashMap<String, Long>();

	@Override
	public Boolean readData() {
		boolean result = true;
		Jedis jedis = null;
		Set<String> allKeys = null;
		try {
			App.semaphore.acquire();
			jedis = JedisClientFactory.getJedisInstance();
			allKeys = jedis.keys("map_alarm_*");
		} catch (Exception e) {
			System.out.println("in SysAlert to get jedis resource failure.");
			System.out.println("SysAlert caused by: " + e.getCause() + " Message: " + e.getMessage());
			return false;
		} finally {
			App.semaphore.release();
			JedisClientFactory.returnJedisInstance(jedis);
		}

		Map<byte[], byte[]> map = Collections.emptyMap();
		SysAlert sysAlert = null;

		for(String item : allKeys) {
			if(item.equals("map_alarm_confirm"))
				continue;

			try {
				map = jedis.hgetAll(item.getBytes());
			} catch (Exception e) {
				JedisClientFactory.returnJedisInstance(jedis);
				return false;
			}

			for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
				try {
					sysAlert = _parseToAlarm(entry.getValue());
				} catch (Throwable throwable)
				{
					JedisClientFactory.returnJedisInstance(jedis);
					return false;
				}

				if(null != sysAlert) {
					if(savedSysAlert.containsKey(sysAlert.devNo)) {
						Long saved = savedSysAlert.get(sysAlert.devNo);
//						System.out.println("current: "+ sysAlert.getAlertStartTime() + "    saved: "+saved);
						if(saved.equals(sysAlert.getAlertStartTime()))
							continue;
					}

					MqttAsyncCallback mqttAsyncCallback = TopicFactory.getInstanceOfTalkTopics();
					try {
						App.semaphore.acquire();
						String  data = sysAlert.doSerialize();
						if(data != null && mqttAsyncCallback.isConnect()) {
							mqttAsyncCallback.publish(App.topicSendAlert,2, data.getBytes("UTF-8"));
							savedSysAlert.put(sysAlert.devNo, sysAlert.getAlertStartTime());
						}
					} catch (Throwable throwable) {
						throwable.printStackTrace();
						result = false;
					} finally {
						App.semaphore.release();
					}
				}
			}
		}

		JedisClientFactory.returnJedisInstance(jedis);
		
		return result;
	}

	public Boolean processData() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Boolean storeData() {
		boolean result = true;
		return result;
	}

	public Boolean cleanupData() {
/*		if(++clean_counter > 60) {
			savedSysAlert.clear();
			clean_counter = 0;
		}*/
		return true;
	}

	protected SysAlert _parseToAlarm(byte[] byteArray)
	{
		try
		{
			SysAlert alarmObj=SysAlert.getSysAlert();
			//field1: [0-3]
			//int primaryId = DataUtil.ByteArr2Int(byteArray);

			//field2: [4-7]
			Integer signalId = DataUtil.ByteArr2Int(DataUtil.subByte(byteArray, 4, 4));
//			alarmObj.fieldId=signalId;

			//field3: [8]
			byte alarmType = byteArray[8];

			//field4: [9-12]
			Float signalVal=DataUtil.ByteArr2Float(DataUtil.subByte(byteArray, 9, 4));

			//field5: [13]
//			alarmObj.alarmOfLevel = byteArray[13];
			alarmObj.alertLevel = byteArray[13];

			//field6: [14-21]/[14-17]
			//Long startTime = DataUtil.ByteArr2Long(DataUtil.subByte(byteArray, 14, 8));
			Integer startTime = DataUtil.ByteArr2Int(DataUtil.subByte(byteArray, 14, 4));
//			alarmObj.t_begin= startTime.longValue();
			alarmObj.setAlertStartTime(startTime.longValue()*1000);

			//field7: [22-29]/[18-21]
			//Long endTime = DataUtil.ByteArr2Long(DataUtil.subByte(byteArray, 22,8));
			//Long endTime = DataUtil.ByteArr2Long(DataUtil.subByte(byteArray, 18,4));

			//field8: [30-33]/[22-25]
			int devId = DataUtil.ByteArr2Int(DataUtil.subByte(byteArray, 22/*30*/, 4));

			if(!devIdMapping.containsKey(devId)) {
				DeviceService deviceService = DeviceService.getInstance();
				List<Device> list = deviceService.getDeviceByDevId(devId);

				if (null != list && list.size() > 0) {
					devIdMapping.put(devId, list.get(0));
				} else {
//					devIdMapping.put(devId, null);
					return null;
				}
			}

			Device device = devIdMapping.get(devId);
			if(device != null) {
				alarmObj.devNo = device.getDevNo();
				alarmObj.devId = devId;
				alarmObj.setDeviceType(device.getDeviceType());
			} else {
				alarmObj.devNo = "Unknown";
				alarmObj.devId = null;
				alarmObj.setDeviceType(0);
				System.out.println("Error (SysAlert): t_device table misses device ID: " + devId);
				return null;
			}

			Host host = Host.getHostInstance();
			alarmObj.setHostNo(host.getHostNo());

			//field9: [34]/[26]
			//byte alarmStatus = byteArray[26/*34*/];

			//field10: [35-38]/[27-30]
			alarmObj.id= DataUtil.ByteArr2Int(DataUtil.subByte(byteArray, 27/*35*/, 4));

			//field11: [39-42]/[31-34]
			//int droomId = DataUtil.ByteArr2Int(DataUtil.subByte(byteArray, 31/*39*/, 4));

			//field12: [43-][35-]
			String alarmSite = new String(DataUtil.subByte(byteArray, 35/*43*/, 256), "UTF-8");
			alarmObj.AlarmSite = alarmSite.trim();

			FieldSignalService signalService = FieldSignalService.getFieldSignalService();
			FieldSignal fieldObj = signalService.getSignalById(signalId);
			if(fieldObj != null)
			{
				alarmObj.alarmName = fieldObj.getMeaning();

				if (2 == alarmType)//over
					alarmObj.alarmName = String.format("%s偏高(%.02f)", fieldObj.getMeaning(), signalVal);
				else if (1 == alarmType)//low
					alarmObj.alarmName = String.format("%s偏低(%.02f)", fieldObj.getMeaning(), signalVal);
				else if(0 == alarmType)
				{
					if(null != fieldObj.cfgProp && null != fieldObj.cfgProp.optMap && !fieldObj.cfgProp.optMap.isEmpty())
					{
						String itemName=fieldObj.cfgProp.optMap.get(signalVal.intValue());
						if(null != itemName)
							alarmObj.alarmName = String.format("%s-%s", fieldObj.getMeaning(), itemName);
					}
				}
			}
			else {
				alarmObj.alarmName = "Unknown";
				System.out.println("Error: Unknown signal field ID "+signalId+" of device ID "+devId);
				return null;
			}

			return alarmObj;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println("SysAlert Error: caused by " + ex.getCause() + "  Message: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public String doSerialize() {
		alertData.clear();
		alertValue.clear();
		alertData.put("key", getHostNo()+":"+getDevNo()+":"+getDeviceType()+":110");

		alertValue.put("alarmName", getAlarmName());
		alertValue.put("alarmSite", getAlarmSite());
		alertValue.put("startTime", new Date(alertStartTime));
		alertValue.put("alarmLevel", getAlarmLevel());
		alertValue.put("hostNo", getHostNo());
		alertValue.put("devNo", getDevNo());

		alertData.put("value", alertValue);

		ObjectMapper objectMapper = JacksonFactory.getObjectMapper();
		String json = null;
		try {
			json = objectMapper.writeValueAsString(alertData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	public static SysAlert getSysAlert() {
		if(sysAlert == null)
			sysAlert = new SysAlert();
		return sysAlert;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getAlertLevel() {
		return alertLevel;
	}

	public void setAlertLevel(int alertLevel) {
		this.alertLevel = alertLevel;
	}

	public String getAlarmName() {
		return alarmName;
	}

	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}

	public String getAlarmSite() {
		return AlarmSite;
	}

	public void setAlarmSite(String alarmSite) {
		AlarmSite = alarmSite;
	}

	public Long getAlertStartTime() {
		return alertStartTime;
	}

	public void setAlertStartTime(Long alertStartTime) {
		this.alertStartTime = alertStartTime;
	}
}

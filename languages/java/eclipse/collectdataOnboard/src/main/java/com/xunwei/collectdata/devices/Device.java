package com.xunwei.collectdata.devices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.utils.JacksonFactory;

import java.util.HashMap;
import java.util.Map;

//import javax.persistence.Entity;
//import javax.persistence.Query;
//import javax.persistence.Table;

//@Entity
//@Table(name = "t_sys_device")
public class Device extends AbsCommonData {
	private boolean isRegistered = false;
	private Map<String, Object> jmap = new HashMap<String, Object>();
	private String jsonData = null;

	public boolean isRegistered() {
		return isRegistered;
	}

	public void setRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}

	public String doSerialize() {
		Host host = Host.getHostInstance();
		jmap.put("name", this.getName());
		jmap.put("hostNo", host.getHostNo());
		jmap.put("devNo", this.getDevNo());
		jmap.put("deviceType",this.getDeviceType());
		jmap.put("startTime", this.getStartTime());

		//only for test data field.
/*		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse("2019-04-05 22:17:18");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		jmap.put("startTime", date);*/

		ObjectMapper objectMapper = JacksonFactory.getObjectMapper();
		try {
			jsonData = objectMapper.writeValueAsString(jmap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return jsonData;
	}
}

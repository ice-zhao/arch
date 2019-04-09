package xunwei.collectdata.devices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import xunwei.collectdata.AbsCommonData;
import xunwei.collectdata.utils.JacksonFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
//        Session session = App.getSession();
//        Query query = session.createQuery("select name from Device where devNo='dev_88'");
//        List list = query.getResultList();
//        System.out.println("$$$$$$$$$$$$$$$$$  "+ list.get(0));
		jmap.put("name", "电表");
		jmap.put("devNo", "dev_88");
		jmap.put("deviceType",11);
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse("2019-04-05 22:17:18");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		jmap.put("startTime", date);

		ObjectMapper objectMapper = JacksonFactory.getObjectMapper();
		try {
			jsonData = objectMapper.writeValueAsString(jmap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return jsonData;
	}
}

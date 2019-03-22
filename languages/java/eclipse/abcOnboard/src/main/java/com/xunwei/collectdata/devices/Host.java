package com.xunwei.collectdata.devices;

import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.utils.JacksonFactory;

public class Host extends AbsCommonData {
    private String remoteServerAddr;
    private Integer floor;
    private HashMap<String, Object> jsonMap = new HashMap<>();
    private static Host host = null;
    
    private Host() {}
    
    public static Host getHostInstance() {
    	if(host == null)
    		host = new Host();
    	return host;
    }
    
	public Boolean readData() {
		Host newHost = Host.getHostInstance();
		newHost.setParkId(888);
		
		hostNo = "XW12345678";
		jsonMap.put("hostNo", hostNo);
		jsonMap.put("remoteServerAddr", "192.168.3.123");
		jsonMap.put("parkID", newHost.getParkId());
		return true;
	}
	
	public String doSerialize() {
		String json = null;
		
		if(!readData()) return json;
		
		ObjectMapper objMapper = JacksonFactory.getObjectMapper();
		try {
			json = objMapper.writeValueAsString(jsonMap);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			json = null;
		}
		return json;
	}



	public String getRemoteServerAddr() {
        return remoteServerAddr;
    }

    public void setRemoteServerAddr(String remoteServerAddr) {
    	this.remoteServerAddr = remoteServerAddr;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }
    
}

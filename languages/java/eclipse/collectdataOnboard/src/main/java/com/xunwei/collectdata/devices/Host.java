package com.xunwei.collectdata.devices;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.App;
import com.xunwei.collectdata.utils.JacksonFactory;

public class Host extends AbsCommonData {
    private String remoteServerAddr;
    private Integer floor;
    private HashMap<String, Object> jsonMap = new HashMap<String, Object>();
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
		String pathName = "/.host_info";

		try {
			FileReader fileReader = new FileReader(pathName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			hostNo = bufferedReader.readLine();
		} catch (Exception e) {
//			e.printStackTrace();
			hostNo = "XW12345678";
			App.setHostRegistered(false);
			return false;
		}

//		hostNo = "XW_M_LO8888888888";
		jsonMap.put("hostNo", hostNo);
		jsonMap.put("remoteServerAddr", "192.168.3.123");
//		jsonMap.put("parkId", newHost.getParkId());
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

package com.xunwei.collectdata.alert;

import java.util.HashMap;

import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;

import com.xunwei.collectdata.utils.RedissonClientFactory;

public class UPSAlert extends AbsAlert {
	 private float voltage;
	 private float current;

	 private HashMap<String, String> data;

	public Boolean readData() {
		RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();
		RKeys rKeys = redissonClient.getKeys();
		return true;
	}


	public Boolean processData() {
		// TODO Auto-generated method stub
		return true;
	}


	public Boolean storeData() {
		// TODO Auto-generated method stub
		return true;
	}


	public Boolean cleanupData() {
		// TODO Auto-generated method stub
		return true;
	}

	public float getVoltage() {
		return voltage;
	}

	public void setVoltage(float voltage) {
		this.voltage = voltage;
	}

	public float getCurrent() {
		return current;
	}

	public void setCurrent(float current) {
		this.current = current;
	}

}

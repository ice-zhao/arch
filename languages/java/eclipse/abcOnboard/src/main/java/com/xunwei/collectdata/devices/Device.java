package com.xunwei.collectdata.devices;

import java.io.Serializable;

import com.xunwei.collectdata.AbsCommonData;

public class Device extends AbsCommonData {
	private boolean isRegistered = false;

	public boolean isRegistered() {
		return isRegistered;
	}

	public void setRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}
}

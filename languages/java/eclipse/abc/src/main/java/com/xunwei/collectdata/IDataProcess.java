package com.xunwei.collectdata;

public interface IDataProcess {
	public Boolean readData();
	public Boolean processData();
	public Boolean storeData();
	public Boolean cleanupData();
}

package com.xunwei.collectdata;

public interface IDataProcess {
	Boolean readData();
	Boolean processData();
	Boolean storeData();
	Boolean cleanupData();
}

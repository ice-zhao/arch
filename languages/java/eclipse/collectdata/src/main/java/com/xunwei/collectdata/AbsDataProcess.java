package com.xunwei.collectdata;

public abstract class AbsDataProcess implements IDataProcess {
	public final Boolean produceData() {
		if(!readData())	return false;
		
		if(!processData()) return false;
		
		if(!storeData()) return false;
		
		if(!cleanupData()) return false;
		
		return true;
	}
}

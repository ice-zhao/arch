package xunwei.collectdata;

public interface ICommon {
	Boolean readData();
	Boolean processData();
	Boolean storeData();
	Boolean cleanupData();
	String doSerialize();
}

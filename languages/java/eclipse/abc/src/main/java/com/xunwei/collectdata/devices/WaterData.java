package com.xunwei.collectdata.devices;

import com.xunwei.collectdata.AbsDataProcess;

public class WaterData extends AbsDataProcess {
    public Boolean readData() {
        return true;
    }

    public Boolean processData() {
        return true;
    }

    public Boolean storeData() {
        return true;
    }

    public Boolean cleanupData() {
        return true;
    }
}

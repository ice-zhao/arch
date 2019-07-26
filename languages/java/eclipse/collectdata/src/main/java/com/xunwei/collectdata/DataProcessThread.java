package com.xunwei.collectdata;

public class DataProcessThread extends Thread {
    public void run() {
        DeviceType []deviceType = DeviceType.values();

        while(true) {
            for (DeviceType item : deviceType) {
                AbsCommonData dataProcess = DataProcessFactory.getDataProcessInstance(item);
                if (dataProcess != null)
                    dataProcess.produceData();
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

package com.xunwei.collectdata.devices;

import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.DataProcessThread;
import com.xunwei.collectdata.HostData;

import static com.xunwei.collectdata.HostField.Humidity;
import static com.xunwei.collectdata.HostField.Temperature;

public class TemperatureHumidity extends AbsCommonData {
    private float temperature;
    private float humidity;

    @Override
    public Boolean readData() {
        return super.readData();
    }

    @Override
    public Boolean processData() {
        boolean result = true;
        Host host = Host.getHostInstance();
        if (dataList != null) {
            Integer devType = DataProcessThread.getDeviceType(getDevNo());
            jsonRes.put("key", host.getHostNo() + ":" + getDevNo() + ":" + devType + ":100");

            for (HostData hostData : dataList) {
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ " + hostData.getValue());
                switch (hostData.getFieldId()) {
                    case Temperature:
                        jsonMap.put("temperature", hostData.getValue());
                        break;
                    case Humidity:
                        jsonMap.put("humidity", hostData.getValue());
                        break;
                    default:
                        System.out.println("temperature humidity device don't have this kind of field. value: " + hostData.getValue());
                        break;
                }
            }

            jsonMap.put("timestamp", entity.getTimestamp());
            jsonMap.put("devNo", getDevNo());
            jsonMap.put("hostNo", host.getHostNo());

            jsonRes.put("value", jsonMap);
        }

        return super.processData();
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }
}

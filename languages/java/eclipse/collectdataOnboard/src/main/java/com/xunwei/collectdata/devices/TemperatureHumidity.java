package com.xunwei.collectdata.devices;

import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.DataProcessThread;
import com.xunwei.collectdata.FieldSignal;
import com.xunwei.services.daos.FieldSignalService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        boolean result = false;

        if(!isTimestampChanged())
            return false;

        Host host = Host.getHostInstance();
        if (allSignals != null) {
            Integer devType = DataProcessThread.getDeviceType(getDevNo());
            jsonRes.put("key", host.getHostNo() + ":" + getDevNo() + ":" + devType + ":100");

            for (Map.Entry<Integer,Integer> entry : allSignals.entrySet()) {
                Integer signalId =entry.getKey();
                Integer signalVal = entry.getValue();
//                System.out.println(signalId + "    :   " + signalVal);

                FieldSignalService signalService = FieldSignalService.getFieldSignalService();
                FieldSignal fieldSignal = signalService.getSignalById(signalId);

                switch (signalId) {
                    case Temperature:
                        jsonMap.put("temperature", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        break;
                    case Humidity:
                        jsonMap.put("humidity", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        break;
                    default:
                        System.out.println("temperature humidity device don't have this kind of field. value: " + signalId);
                        break;
                }
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            jsonMap.put("time", df.format(timestamp));
            jsonMap.put("devNo", getDevNo());
            jsonMap.put("hostNo", host.getHostNo());

            jsonRes.put("value", jsonMap);
            result = true;
        }

        return result;
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

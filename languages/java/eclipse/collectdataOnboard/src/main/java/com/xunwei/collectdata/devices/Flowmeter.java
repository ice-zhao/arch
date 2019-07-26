package com.xunwei.collectdata.devices;

import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.DataProcessThread;
import com.xunwei.collectdata.FieldSignal;
import com.xunwei.services.daos.FieldSignalService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.xunwei.collectdata.HostField.*;

public class Flowmeter extends AbsCommonData {
    private float temperature;
    private float pressure;
    private float instantFlowRate;
    private float totalFlowRate;

    private static Flowmeter oldValue = new Flowmeter();

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
                    case WorkTemperature:
                        jsonMap.put("temperature", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        this.temperature = signalVal;
                        break;
                    case Pressure:
                        jsonMap.put("pressure", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        this.pressure = signalVal;
                        break;
                    case InstantFlowRate:
                        jsonMap.put("instantFlowRate", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        this.instantFlowRate = signalVal;
                        break;
                    case TotalFlowRate:
                        jsonMap.put("totalFlowRate", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        this.totalFlowRate = signalVal;
                        break;
                    default:
                        System.out.println("flowmeter device don't have this kind of field. value: " + signalId);
                        break;
                }
            }

            if(oldValue.equals(this)) {
                System.out.println("[Flowmeter Info]: analog values are equals.");
                return false;
            }
            else {
                oldValue.setTemperature(this.temperature);
                oldValue.setPressure(this.pressure);
                oldValue.setInstantFlowRate(this.instantFlowRate);
                oldValue.setTotalFlowRate(this.totalFlowRate);
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            jsonMap.put("time", df.format(timestamp));// new Date()为获取当前系统时间
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

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getInstantFlowRate() {
        return instantFlowRate;
    }

    public void setInstantFlowRate(float instantFlowRate) {
        this.instantFlowRate = instantFlowRate;
    }

    public float getTotalFlowRate() {
        return totalFlowRate;
    }

    public void setTotalFlowRate(float totalFlowRate) {
        this.totalFlowRate = totalFlowRate;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof Flowmeter)) return false;
        if(this == obj) return true;

        Flowmeter flowmeter = (Flowmeter)obj;
        if(flowmeter.getTemperature() == this.temperature && flowmeter.getPressure() == this.pressure &&
            flowmeter.getInstantFlowRate() == this.instantFlowRate &&
            flowmeter.getTotalFlowRate() == this.totalFlowRate)
            return true;

        return false;
    }
}

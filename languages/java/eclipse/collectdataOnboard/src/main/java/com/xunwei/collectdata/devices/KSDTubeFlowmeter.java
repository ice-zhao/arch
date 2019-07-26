package com.xunwei.collectdata.devices;

import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.DataProcessThread;
import com.xunwei.collectdata.FieldSignal;
import com.xunwei.services.daos.FieldSignalService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.xunwei.collectdata.HostField.*;

public class KSDTubeFlowmeter extends AbsCommonData {
    private float temperature;
    private float pressure;
    private float tube1InstantFlowRate;
    private float tube1TotalFlowRate;
    private float tube2InstantFlowRate;
    private float tube2TotalFlowRate;

    private static KSDTubeFlowmeter oldValue = new KSDTubeFlowmeter();

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
                    case TubeWorkTemperature:
                        jsonMap.put("temperature", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        this.temperature = signalVal;
                        break;
                    case TubeWorkPressure:
                        jsonMap.put("pressure", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        this.pressure = signalVal;
                        break;
                    case Tube1InstantFlowRate:
                        jsonMap.put("tube1InstantFlowRate", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        this.tube1InstantFlowRate = signalVal;
                        break;
                    case Tube1TotalFlowRate:
                        jsonMap.put("tube1TotalFlowRate", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        this.tube1TotalFlowRate = signalVal;
                        break;
                    case Tube2InstantFlowRate:
                        jsonMap.put("tube2InstantFlowRate", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        this.tube2InstantFlowRate = signalVal;
                        break;
                    case Tube2TotalFlowRate:
                        jsonMap.put("tube2TotalFlowRate", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        this.tube2TotalFlowRate = signalVal;
                        break;
                    default:
                        System.out.println("KSD Tube flowmeter device don't have this kind of field. value: " + signalId);
                        break;
                }
            }

            if(oldValue.equals(this))
                return false;
            else {
                oldValue.setTemperature(this.temperature);
                oldValue.setPressure(this.pressure);
                oldValue.setTube1InstantFlowRate(this.tube1InstantFlowRate);
                oldValue.setTube1TotalFlowRate(this.tube1TotalFlowRate);
                oldValue.setTube2InstantFlowRate(this.tube2InstantFlowRate);
                oldValue.setTube2TotalFlowRate(this.tube2TotalFlowRate);
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

    public float getTube1InstantFlowRate() {
        return tube1InstantFlowRate;
    }

    public void setTube1InstantFlowRate(float tube1InstantFlowRate) {
        this.tube1InstantFlowRate = tube1InstantFlowRate;
    }

    public float getTube1TotalFlowRate() {
        return tube1TotalFlowRate;
    }

    public void setTube1TotalFlowRate(float tube1TotalFlowRate) {
        this.tube1TotalFlowRate = tube1TotalFlowRate;
    }

    public float getTube2InstantFlowRate() {
        return tube2InstantFlowRate;
    }

    public void setTube2InstantFlowRate(float tube2InstantFlowRate) {
        this.tube2InstantFlowRate = tube2InstantFlowRate;
    }

    public float getTube2TotalFlowRate() {
        return tube2TotalFlowRate;
    }

    public void setTube2TotalFlowRate(float tube2TotalFlowRate) {
        this.tube2TotalFlowRate = tube2TotalFlowRate;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof KSDTubeFlowmeter)) return false;
        if(this == obj) return true;

        KSDTubeFlowmeter flowmeter = (KSDTubeFlowmeter)obj;
        if(flowmeter.getTemperature() == this.temperature && flowmeter.getPressure() == this.pressure &&
            flowmeter.getTube1InstantFlowRate() == this.tube1InstantFlowRate &&
            flowmeter.getTube1TotalFlowRate() == this.tube1TotalFlowRate &&
            flowmeter.getTube2InstantFlowRate() == this.tube2InstantFlowRate &&
            flowmeter.getTube2TotalFlowRate() == this.tube2TotalFlowRate)
            return true;

        return false;
    }
}

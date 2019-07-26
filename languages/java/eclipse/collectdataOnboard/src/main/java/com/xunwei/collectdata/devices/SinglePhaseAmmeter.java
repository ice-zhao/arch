package com.xunwei.collectdata.devices;

import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.DataProcessThread;
import com.xunwei.collectdata.FieldSignal;
import com.xunwei.services.daos.FieldSignalService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.xunwei.collectdata.HostField.*;

public class SinglePhaseAmmeter extends AbsCommonData {
    private float Ua;
    private float Ia;
    private float pa;
    private float qa;
    private float pfa;
    private float frq;
    private float epwr;
    private float erq;

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

            for (Map.Entry<Integer,Integer> hostData : allSignals.entrySet()) {
                Integer signalId =hostData.getKey();
                Integer signalVal = hostData.getValue();

//                System.out.println(getDevNo()+"  " + signalVal);

                FieldSignalService signalService = FieldSignalService.getFieldSignalService();
                FieldSignal fieldSignal = signalService.getSignalById(signalId);

                switch (signalId) {
                    case PhaseVoltage:
                        jsonMap.put("ua", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        break;
                    case PhaseCurrent:
                        jsonMap.put("ia", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        break;
                    case Power:
                        jsonMap.put("pa", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        break;
                    case QPower:
                        jsonMap.put("qa", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        break;
                    case PowerPF:
                        jsonMap.put("pfa", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        break;
                    case SystemFrq:
                        jsonMap.put("frq", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        break;
                    case EPWR:
                        jsonMap.put("epwr", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        break;
                    case ERQ:
                        jsonMap.put("erq", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
                        break;
                    default:
                        System.out.println("single phase ammeter don't have this kind of field. value: " + signalId);
                        break;
                }
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

    public Boolean storeData() {
        return super.storeData();
    }

    public float getUa() {
        return Ua;
    }

    public void setUa(float ua) {
        Ua = ua;
    }

    public float getIa() {
        return Ia;
    }

    public void setIa(float ia) {
        Ia = ia;
    }

    public float getPa() {
        return pa;
    }

    public void setPa(float pa) {
        this.pa = pa;
    }

    public float getQa() {
        return qa;
    }

    public void setQa(float qa) {
        this.qa = qa;
    }

    public float getPfa() {
        return pfa;
    }

    public void setPfa(float pfa) {
        this.pfa = pfa;
    }

    public float getFrq() {
        return frq;
    }

    public void setFrq(float frq) {
        this.frq = frq;
    }

    public float getEpwr() {
        return epwr;
    }

    public void setEpwr(float epwr) {
        this.epwr = epwr;
    }

    public float getErq() {
        return erq;
    }

    public void setErq(float erq) {
        this.erq = erq;
    }

}

package com.xunwei.collectdata.devices;

import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.DataProcessThread;
import com.xunwei.collectdata.HostData;

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
        boolean result = true;
        Host host = Host.getHostInstance();
        if (dataList != null) {
            Integer devType = DataProcessThread.getDeviceType(getDevNo());
            jsonRes.put("key", host.getHostNo() + ":" + getDevNo() + ":" + devType + ":100");

            for (HostData hostData : dataList) {
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ " + hostData.getValue());
                switch (hostData.getFieldId()) {
                    case PhaseVoltage:
                        jsonMap.put("ua", hostData.getValue());
                        break;
                    case PhaseCurrent:
                        jsonMap.put("ia", hostData.getValue());
                        break;
                    case Power:
                        jsonMap.put("pa", hostData.getValue());
                        break;
                    case QPower:
                        jsonMap.put("qa", hostData.getValue());
                        break;
                    case PowerPF:
                        jsonMap.put("pfa", hostData.getValue());
                        break;
                    case SystemFrq:
                        jsonMap.put("frq", hostData.getValue());
                        break;
                    case EPWR:
                        jsonMap.put("epwr", hostData.getValue());
                        break;
                    case ERQ:
                        jsonMap.put("erq", hostData.getValue());
                        break;
                    default:
                        System.out.println("single phase ammeter don't have this kind of field. value: " + hostData.getValue());
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

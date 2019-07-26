package com.xunwei.collectdata.devices;
import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.DataProcessThread;
import com.xunwei.collectdata.FieldSignal;
import com.xunwei.services.daos.FieldSignalService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.xunwei.collectdata.HostField.*;

/*3 phase ammeter class */
public class AmmeterData extends AbsCommonData {
	private float Ua;
	private float Ub;
	private float Uc;
	private float Uab;
	private float Ubc;
	private float Uca;
	private float volUnbalance;
	private float Ia;
	private float Ib;
	private float Ic;
	private float curunbalance;
	private float pa;
	private float pb;
	private float pc;
	private float pz;
	private float qa;
	private float qb;
	private float qc;
	private float qz;
	private float sa;
	private float sb;
	private float sc;
	private float sz;
	private float pfa;
	private float pfb;
	private float pfc;
	private float pfz;
	private float frq;
	private float epwr_in;
	private float epwr_out;
	private float eq_in;
	private float eq_out;
	private float reserved1;

	public Boolean readData() {
		return super.readData();
	}

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
//				System.out.println(getDevNo()+"  " + signalVal);

				FieldSignalService signalService = FieldSignalService.getFieldSignalService();
				FieldSignal fieldSignal = signalService.getSignalById(signalId);

				switch (signalId) {
					case UA:
						jsonMap.put("ua", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case UB:
						jsonMap.put("ub", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case UC:
						jsonMap.put("uc", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case UAB:
						jsonMap.put("uab", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case UBC:
						jsonMap.put("ubc", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case UCA:
						jsonMap.put("uca", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case VolUnbalance:
						jsonMap.put("volUnbalance", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case IA:
						jsonMap.put("ia", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case IB:
						jsonMap.put("ib", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case IC:
						jsonMap.put("ic", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case Curunbalance:
						jsonMap.put("curunbalance", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case PA:
						jsonMap.put("pa", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case PB:
						jsonMap.put("pb", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case PC:
						jsonMap.put("pc", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case PZ:
						jsonMap.put("pz", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case QA:
						jsonMap.put("qa", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case QB:
						jsonMap.put("qb", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case QC:
						jsonMap.put("qc", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case QZ:
						jsonMap.put("qz", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case SA:
						jsonMap.put("sa", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case SB:
						jsonMap.put("sb", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case SC:
						jsonMap.put("sc", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case SZ:
						jsonMap.put("sz", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case PFA:
						jsonMap.put("pfa", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case PFB:
						jsonMap.put("pfb", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case PFC:
						jsonMap.put("pfc", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case PFZ:
						jsonMap.put("pfz", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case FRQ:
						jsonMap.put("frq", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case Epwr_in:
						jsonMap.put("epwr_in", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case Epwr_out:
						jsonMap.put("epwr_out", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case Eq_in:
						jsonMap.put("eq_in", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					case Eq_out:
						jsonMap.put("eq_out", fieldSignal != null ? fieldSignal.GetFieldValue(signalVal) : signalVal);
						break;
					default:
						System.out.println("3-phases ammeter don't have this kind of field. value: " + signalId);
						break;
				}
			}

//			jsonMap.put("timestamp", entity.getTimestamp());
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			jsonMap.put("time", df.format(timestamp));// new Date()为获取当前系统时间
			jsonMap.put("devNo", getDevNo());
			jsonMap.put("hostNo", host.getHostNo());
			jsonMap.put("devId", getDevId());

			jsonRes.put("value", jsonMap);
			result = true;
		}
		return result;
	}

	public Boolean storeData() {
		return super.storeData();
	}

	public Boolean cleanupData() {
		// TODO Auto-generated method stub
		return true;
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

	public float getUb() {
		return Ub;
	}

	public void setUb(float ub) {
		Ub = ub;
	}

	public float getUc() {
		return Uc;
	}

	public void setUc(float uc) {
		Uc = uc;
	}

	public float getUab() {
		return Uab;
	}

	public void setUab(float uab) {
		Uab = uab;
	}

	public float getUbc() {
		return Ubc;
	}

	public void setUbc(float ubc) {
		Ubc = ubc;
	}

	public float getUca() {
		return Uca;
	}

	public void setUca(float uca) {
		Uca = uca;
	}

	public float getVolUnbalance() {
		return volUnbalance;
	}

	public void setVolUnbalance(float volUnbalance) {
		this.volUnbalance = volUnbalance;
	}

	public float getIb() {
		return Ib;
	}

	public void setIb(float ib) {
		Ib = ib;
	}

	public float getIc() {
		return Ic;
	}

	public void setIc(float ic) {
		Ic = ic;
	}

	public float getCurunbalance() {
		return curunbalance;
	}

	public void setCurunbalance(float curunbalance) {
		this.curunbalance = curunbalance;
	}

	public float getPa() {
		return pa;
	}

	public void setPa(float pa) {
		this.pa = pa;
	}

	public float getPb() {
		return pb;
	}

	public void setPb(float pb) {
		this.pb = pb;
	}

	public float getPc() {
		return pc;
	}

	public void setPc(float pc) {
		this.pc = pc;
	}

	public float getPz() {
		return pz;
	}

	public void setPz(float pz) {
		this.pz = pz;
	}

	public float getQa() {
		return qa;
	}

	public void setQa(float qa) {
		this.qa = qa;
	}

	public float getQb() {
		return qb;
	}

	public void setQb(float qb) {
		this.qb = qb;
	}

	public float getQc() {
		return qc;
	}

	public void setQc(float qc) {
		this.qc = qc;
	}

	public float getQz() {
		return qz;
	}

	public void setQz(float qz) {
		this.qz = qz;
	}

	public float getSa() {
		return sa;
	}

	public void setSa(float sa) {
		this.sa = sa;
	}

	public float getSb() {
		return sb;
	}

	public void setSb(float sb) {
		this.sb = sb;
	}

	public float getSc() {
		return sc;
	}

	public void setSc(float sc) {
		this.sc = sc;
	}

	public float getSz() {
		return sz;
	}

	public void setSz(float sz) {
		this.sz = sz;
	}

	public float getPfa() {
		return pfa;
	}

	public void setPfa(float pfa) {
		this.pfa = pfa;
	}

	public float getPfb() {
		return pfb;
	}

	public void setPfb(float pfb) {
		this.pfb = pfb;
	}

	public float getPfc() {
		return pfc;
	}

	public void setPfc(float pfc) {
		this.pfc = pfc;
	}

	public float getPfz() {
		return pfz;
	}

	public void setPfz(float pfz) {
		this.pfz = pfz;
	}

	public float getFrq() {
		return frq;
	}

	public void setFrq(float frq) {
		this.frq = frq;
	}

	public float getEpwr_in() {
		return epwr_in;
	}

	public void setEpwr_in(float epwr_in) {
		this.epwr_in = epwr_in;
	}

	public float getEpwr_out() {
		return epwr_out;
	}

	public void setEpwr_out(float epwr_out) {
		this.epwr_out = epwr_out;
	}

	public float getEq_in() {
		return eq_in;
	}

	public void setEq_in(float eq_in) {
		this.eq_in = eq_in;
	}

	public float getEq_out() {
		return eq_out;
	}

	public void setEq_out(float eq_out) {
		this.eq_out = eq_out;
	}

	public float getReserved1() {
		return reserved1;
	}

	public void setReserved1(float reserved1) {
		this.reserved1 = reserved1;
	}
}

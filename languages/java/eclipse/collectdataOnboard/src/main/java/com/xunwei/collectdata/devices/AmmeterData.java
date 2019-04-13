package com.xunwei.collectdata.devices;
import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.DataProcessThread;
import com.xunwei.collectdata.HostData;

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
		boolean result = true;
		Host host = Host.getHostInstance();
		if(dataList != null) {
			Integer devType = DataProcessThread.getDeviceType(getDevNo());
			jsonRes.put("key", host.getHostNo() + ":" + getDevNo() + ":" + devType + ":100");

			for (HostData hostData : dataList) {
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ " + hostData.getValue());
				switch (hostData.getFieldId()) {
					case UA:
						jsonMap.put("ua", hostData.getValue());
						break;
					case UB:
						jsonMap.put("ub", hostData.getValue());
						break;
					case UC:
						jsonMap.put("uc", hostData.getValue());
						break;
					case UAB:
						jsonMap.put("uab", hostData.getValue());
						break;
					case UBC:
						jsonMap.put("ubc", hostData.getValue());
						break;
					case UCA:
						jsonMap.put("uca", hostData.getValue());
						break;
					case VolUnbalance:
						jsonMap.put("volUnbalance", hostData.getValue());
						break;
					case IA:
						jsonMap.put("ia", hostData.getValue());
						break;
					case IB:
						jsonMap.put("ib", hostData.getValue());
						break;
					case IC:
						jsonMap.put("ic", hostData.getValue());
						break;
					case Curunbalance:
						jsonMap.put("curunbalance", hostData.getValue());
						break;
					case PA:
						jsonMap.put("pa", hostData.getValue());
						break;
					case PB:
						jsonMap.put("pb", hostData.getValue());
						break;
					case PC:
						jsonMap.put("pc", hostData.getValue());
						break;
					case PZ:
						jsonMap.put("pz", hostData.getValue());
						break;
					case QA:
						jsonMap.put("qa", hostData.getValue());
						break;
					case QB:
						jsonMap.put("qb", hostData.getValue());
						break;
					case QC:
						jsonMap.put("qc", hostData.getValue());
						break;
					case QZ:
						jsonMap.put("qz", hostData.getValue());
						break;
					case SA:
						jsonMap.put("sa", hostData.getValue());
						break;
					case SB:
						jsonMap.put("sb", hostData.getValue());
						break;
					case SC:
						jsonMap.put("sc", hostData.getValue());
						break;
					case SZ:
						jsonMap.put("sz", hostData.getValue());
						break;
					case PFA:
						jsonMap.put("pfa", hostData.getValue());
						break;
					case PFB:
						jsonMap.put("pfb", hostData.getValue());
						break;
					case PFC:
						jsonMap.put("pfc", hostData.getValue());
						break;
					case PFZ:
						jsonMap.put("pfz", hostData.getValue());
						break;
					case FRQ:
						jsonMap.put("frq", hostData.getValue());
						break;
					case Epwr_in:
						jsonMap.put("epwr_in", hostData.getValue());
						break;
					case Epwr_out:
						jsonMap.put("epwr_out", hostData.getValue());
						break;
					case Eq_in:
						jsonMap.put("eq_in", hostData.getValue());
						break;
					case Eq_out:
						jsonMap.put("eq_out", hostData.getValue());
						break;
					default:
						System.out.println("3-phases ammeter don't have this kind of field. value: " + hostData.getValue());
						break;
				}
			}

			jsonMap.put("timestamp", entity.getTimestamp());
			jsonMap.put("devNo", getDevNo());
			jsonMap.put("hostNo", host.getHostNo());
			jsonMap.put("devId", getDevId());

			jsonRes.put("value", jsonMap);
		}
		return true;
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

package com.xunwei.collectdata.devices;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.AbsCommonData;
import com.xunwei.collectdata.AbsDataProcess;
import com.xunwei.collectdata.App;
import com.xunwei.collectdata.utils.JacksonFactory;
import com.xunwei.collectdata.utils.RedissonClientFactory;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.TimestampType;
import org.redisson.api.RKeys;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
/*3-phases ammeter*/
public class AmmeterData extends AbsCommonData {
	private Integer id;
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

	private HashMap<String, String> ammeterData = new HashMap<String, String>();

	public Boolean readData() {
		RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();
		RKeys keys = redissonClient.getKeys();
		Iterable<String> allKeys = keys.getKeysByPattern("*:*:4:100");
		
		for(String item : allKeys) {
			RList<String> rList = redissonClient.getList(item);
			ammeterData.put(item, rList.get(rList.size()-1));
		}
		
		return true;
	}

	public Boolean processData() {
		// TODO Auto-generated method stub
		return true;
	}

	public Boolean storeData() {
		boolean result = true;
		Session sess = App.getSession();
		ObjectMapper mapper = JacksonFactory.getObjectMapper();
		
        for (Entry<String, String> me : ammeterData.entrySet()) {
            try {
            	String value = me.getValue();
            	AmmeterData ammeter = mapper.readValue(value, AmmeterData.class);
                //to persist alert.
				Query query = sess.createQuery("select 1 from AmmeterData where timestamp = :time");
				query.setParameter("time", ammeter.getTimestamp(), TimestampType.INSTANCE);
				
				List list = query.getResultList();
                if (list.isEmpty()) {
                	//get cloud side's devId
					query = sess.createSQLQuery("select ID from t_sys_device where HostNo=:host_no and DevNo=:dev_no");
					query.setParameter("host_no", ammeter.getHostNo());
					query.setParameter("dev_no", ammeter.getDevNo());
					List<Integer> list1 = query.getResultList();
					System.out.println(ammeter.getHostNo()+"         "+ammeter.getDevNo() + "     "+ list1.size());
					if(list1.size() > 0) {
						ammeter.setDevId(list1.get(0));
						App.bePersistedObject(ammeter);
					}
                }
            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                result = false;
            } finally {
                sess.close();
            }
        }
		
		return result;
	}

	public Boolean cleanupData() {
		// TODO Auto-generated method stub
		return true;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public float getUa() {
		return Ua;
	}

	public void setUa(float ua) {
		Ua = ua;
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

	public float getIa() {
		return Ia;
	}

	public void setIa(float ia) {
		Ia = ia;
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

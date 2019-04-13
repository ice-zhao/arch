package com.xunwei.collectdata.devices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xunwei.collectdata.AbsCommonData;
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
import java.util.Map;

/*for single phase ammeter */
public class SinglePhaseAmmeter extends AbsCommonData {
    private float Ua;
    private float Ia;
    private float pa;
    private float qa;
    private float pfa;
    private float frq;
    private float epwr;
    private float erq;

    private HashMap<String, String> singleAmmeter = new HashMap<String, String>();

    public Boolean readData() {
        RedissonClient redissonClient = RedissonClientFactory.getRedissonClient();
        RKeys keys = redissonClient.getKeys();
        Iterable<String> allKeys = keys.getKeysByPattern("*:*:5:100");

        for(String item : allKeys) {
            RList<String> rList = redissonClient.getList(item);
            singleAmmeter.put(item, rList.get(rList.size()-1));
        }

        return true;
    }

    public Boolean storeData() {
        boolean result = true;
        Session sess = App.getSession();
        ObjectMapper mapper = JacksonFactory.getObjectMapper();

        for (Map.Entry<String, String> me : singleAmmeter.entrySet()) {
            try {
                String value = me.getValue();
                SinglePhaseAmmeter ammeter = mapper.readValue(value, SinglePhaseAmmeter.class);
                //to persist alert.
                Query query = sess.createQuery("select 1 from SinglePhaseAmmeter where timestamp = :time");
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

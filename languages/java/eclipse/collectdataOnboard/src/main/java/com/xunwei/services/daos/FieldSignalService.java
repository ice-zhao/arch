package com.xunwei.services.daos;

import com.xunwei.collectdata.App;
import com.xunwei.collectdata.FieldSignal;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

public class FieldSignalService {
    private static FieldSignalService fieldSignalService = null;
    private static Semaphore semaphore = new Semaphore(1, false);
    private static HashMap<Integer, FieldSignal> cachedSignal = new HashMap<Integer, FieldSignal>();



    private FieldSignalService() {
    }

    public static FieldSignalService getFieldSignalService() {
        if(fieldSignalService == null)
            fieldSignalService = new FieldSignalService();
        return fieldSignalService;
    }

    public FieldSignal getSignalById(Integer id) {
        if(id < 0)
            return null;

        if(cachedSignal.containsKey(id))
            return cachedSignal.get(id);

        List<FieldSignal> list = null;
        boolean isQueried = false;
        int retry = 3;

        for(int i = 0; i < retry; i++) {
            try {
                semaphore.acquire();
                Session session = App.getSession();

                if (session.isOpen()) {
                    Transaction transaction = session.beginTransaction();
                    Query query = session.createQuery("from FieldSignal where id=:signal_id");
                    query.setParameter("signal_id", id);
                    list = query.list();
                    transaction.commit();
                }

                if (list != null && list.size() > 0) {
                    cachedSignal.put(id, list.get(0));
                    isQueried = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("[FieldSignalService]");
                isQueried = false;
            } finally {
                semaphore.release();
            }

            if (isQueried)
                return cachedSignal.get(id);
        }

        return null;
    }
}

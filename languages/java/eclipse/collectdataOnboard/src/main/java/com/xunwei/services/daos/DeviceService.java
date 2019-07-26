package com.xunwei.services.daos;

import com.xunwei.collectdata.App;
import com.xunwei.collectdata.devices.Device;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

public class DeviceService {
    private static DeviceService deviceService = null;

    public static DeviceService getInstance() {
        if(deviceService == null)
            deviceService = new DeviceService();
        return deviceService;
    }

    private DeviceService() {
    }

    public List<Device> getAllDevices() {
        int retry = 3;
        boolean isSuccess = false;
        List<Device> list = Collections.emptyList();

        for(int i = 0; i < retry; i++) {
            try {
                App.semaphore.acquire();
                Session session = App.getSession();
                Transaction transaction = session.beginTransaction();
                list = (List<Device>)session.createQuery("from Device").list();
                transaction.commit();
                isSuccess = true;
            }catch (Exception e) {
                System.out.println("[DeviceService]  caused by:" + e.getCause() + ". Message: " + e.getMessage());
                isSuccess = false;
            } finally {
                App.closeSession();
                App.semaphore.release();
            }

            if(isSuccess)
                break;
        }

        return list;
    }

    public List<Device> getDeviceByDevNo(String devNo) {
        List<Device> list=Collections.EMPTY_LIST;
        int retry = 3;
        boolean isSuccess = false;

        for(int i=0; i<retry; i++) {
            try {
                App.semaphore.acquire();
                Session cfg_sess = App.getSession();
                Transaction transaction = cfg_sess.beginTransaction();
                Query query = cfg_sess.createQuery("from Device where devNo=:dev_No");
                query.setParameter("dev_No", devNo.replaceAll("\"", ""));
                list = query.list();
                transaction.commit();
                isSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                isSuccess = false;
            } finally {
                App.closeSession();
                App.semaphore.release();
            }

            if(isSuccess)
                break;
        }

        return list;
    }

    public List<Device> getDeviceByDevId(Integer devId) {
        List<Device> list = Collections.EMPTY_LIST;
        int retry = 3;
        boolean isSuccess = false;

        for(int i=0; i<retry; i++) {
            try {
                App.semaphore.acquire();
                Session session = App.getSession();
                Transaction transaction = session.beginTransaction();
                Query query = session.createQuery("from Device where id=:dev_id");
                query.setParameter("dev_id", devId);
                list = query.list();
                transaction.commit();
                isSuccess = true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                isSuccess = false;
            } finally {
                App.closeSession();
                App.semaphore.release();
            }

            if(isSuccess)
                break;
        }

        return list;
    }
}

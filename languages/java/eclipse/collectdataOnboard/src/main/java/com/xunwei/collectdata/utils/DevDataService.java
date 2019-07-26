package com.xunwei.collectdata.utils;

//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import net.xunweitech.demh.Dao.IDataEntityDao;
//import net.xunweitech.demh.Dao.IFieldDao;
//import net.xunweitech.demh.Dao.IJedisClient;
//import net.xunweitech.demh.Entity.DataListPlugin.AnalogData_History;
//import net.xunweitech.demh.Entity.DataListPlugin.AnalogData_Monitor;
//import net.xunweitech.demh.Entity.DataListPlugin.DataTableInputParameters;
//import net.xunweitech.demh.Entity.DataListPlugin.ListData;
//import net.xunweitech.demh.Entity.DbEntities.dbCfg.T_FIELD;
//import net.xunweitech.demh.Entity.DbEntities.dbData.T_DATA;
//import net.xunweitech.demh.Entity.PlainData.MetaDataBase;
//import net.xunweitech.demh.Entity.PlainData.MetaData_Str_S1;
//import net.xunweitech.demh.Entity.Result;
//import net.xunweitech.demh.Entity.SimpleKV;
//import net.xunweitech.demh.LogicalProcLayer.Impl.SpeedUpRequestProc;
//import net.xunweitech.demh.Service.IDevDataService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;

import java.util.*;

//@Service
public class DevDataService /*implements IDevDataService*/
{
//    @Autowired
//    public IJedisClient jedisClient;
//
//    @Autowired
//    private IDataEntityDao dataDao;
//
//    @Autowired
//    private IFieldDao fieldDao;

    public static Map<Integer, Integer> MapAdapter(Map<byte[], byte[]> byteArrayMap)
    {
        Map<Integer,Integer> sortMap = Collections.EMPTY_MAP;
        if(!byteArrayMap.isEmpty())
        {
            sortMap = new TreeMap<Integer, Integer>(
                    new Comparator<Integer>()
                    {
                        public int compare(Integer o1, Integer o2)
                        {
                            return o1.compareTo(o2);
                        }
                    });
        }

        for (Map.Entry<byte[], byte[]> entry : byteArrayMap.entrySet())
        {
            Integer signalId = (int) DataUtil.ByteArr2Short(entry.getKey());
            Integer signalVal = DataUtil.ByteArr2Int(entry.getValue());
            sortMap.put(signalId,signalVal);
        }
        return sortMap;
    }

/*
    public Result GetRealTimeData(Integer devId)
    {
        Result result = new Result();
        try
        {
            SpeedUpRequestProc.GetInstance().PushRequest(devId,1);

            //step1: hgetall map_data_devId
            String redisKey = String.format("map_data_%d", devId);
            Map<Integer,Integer> allSignals = MapAdapter(jedisClient.hmap_getall(redisKey.getBytes()));
            if (allSignals.isEmpty())
                result.getNone("设备无数据");
            else
            {
                ListData analog = new ListData();
                ListData status = new ListData();
                ListData param = new ListData();

                JSONArray prop = new JSONArray();

                JSONObject jsonObj = new JSONObject();
                jsonObj.put("analog", analog);
                jsonObj.put("status", status);

                JSONObject paramObj = new JSONObject();
                paramObj.put("userdata", param.getUserData());
                paramObj.put("rawData", param.getRawData());
                paramObj.put("prop", paramObj);

                jsonObj.put("param", paramObj);

                for (Map.Entry<Integer,Integer> entry : allSignals.entrySet())
                {
                    Integer signalId =entry.getKey();
                    Integer signalVal = entry.getValue();

                    //step2: get signal name from cache by signal id
                    T_FIELD fieldObj = fieldDao.FindById(signalId);
                    if (null != fieldObj)
                    {
                        if (5 == fieldObj.category)//analog
                        {
                            analog.getUserData().add(fieldObj.GetFieldName());
                            analog.getRawData().add(fieldObj.GetFieldValue(signalVal));
                        } else if (2 == fieldObj.category || 1 == fieldObj.category)//status
                        {
                            status.getUserData().add(fieldObj.GetFieldName());
                            if (0 == fieldObj.disMode)
                            {
                                status.getRawData().add(fieldObj.GetFieldValue(signalVal));
                            } else if (1 == fieldObj.disMode)
                            {
                                String optName = fieldObj.cfgProp.optMap.get(signalVal);
                                status.getRawData().add(null == optName ? "Unknown" : optName);
                            } else
                            {
                                status.getRawData().add("Unknown");
                            }
                        } else if (3 == fieldObj.category)
                        {
                            param.getUserData().add(fieldObj.GetFieldName());
                            if (0 == fieldObj.disMode)
                            {
                                param.getRawData().add(fieldObj.GetFieldValue(signalVal));
                            } else if (1 == fieldObj.disMode)
                            {
                                String optName = fieldObj.cfgProp.optMap.get(signalVal);
                                param.getRawData().add(null == optName ? "Unknown" : optName);

                                prop.add(fieldObj.cfgProp);
                            } else
                            {
                                param.getRawData().add("Unknown");
                            }
                        }
                    }
                }
                result.getDataSuccess(jsonObj);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            result.getDataFailed(null,ex.getMessage());
        }
        return result;
    }


    protected Map<Long, Map<Integer, T_DATA>> flipData(List<T_DATA> dataList)
    {
        Map<Long, Map<Integer, T_DATA>> standardData = new LinkedHashMap<>();
        for (T_DATA dataObj : dataList)
        {
            if (standardData.containsKey(dataObj.Time))
            {
                Map<Integer, T_DATA> signalMap = standardData.get(dataObj.Time);
                if (!signalMap.containsKey(dataObj.fieldId))
                    signalMap.put(dataObj.fieldId, dataObj);
            } else
            {
                Map<Integer, T_DATA> signalMap = new HashMap<Integer, T_DATA>();
                signalMap.put(dataObj.fieldId, dataObj);
                standardData.put(dataObj.Time, signalMap);
            }
        }
        return standardData;
    }


    public Result GetDataOfRecent(String signals, Integer devId, Long timeSpan)
    {
        Result result = new Result();
        try
        {
            List<Short> signalList = JSON.parseArray(signals, Short.class);

            List<T_DATA> dataList = dataDao.FindNopagedData(signalList, devId, timeSpan,System.currentTimeMillis());

            Map<Long, Map<Integer, T_DATA>> standardData = this.flipData(dataList);

            int signalNumber = signalList.size();
            AnalogData_Monitor analogData = new AnalogData_Monitor((short) signalNumber, MetaDataBase.GetContainerClazz(signalNumber));

            String[] fieldNames = new String[signalNumber];
            T_FIELD[] fieldCache = new T_FIELD[signalNumber];

            for (Map.Entry<Long, Map<Integer, T_DATA>> entry : standardData.entrySet())
            {
                String[] dataArray = new String[signalNumber];

                Map<Integer, T_DATA> innerMap = entry.getValue();
                for (byte index = 0; index < signalNumber; ++index)
                {
                    T_FIELD fieldObj = null;
                    T_DATA dataObj = innerMap.get(signalList.get(index).intValue());
                    if (null != dataObj)
                    {
                        if (null == fieldCache[index])
                        {
                            fieldObj = fieldDao.FindById(dataObj.fieldId);
                            fieldCache[index] = fieldObj;
                        }

                        if (null == fieldNames[index])
                        {
                            fieldNames[index] = fieldObj.GetFieldName();
                        }

                        dataArray[index] = fieldCache[index].GetFieldValue(dataObj.value);
                    } else
                    {
                        dataArray[index] = "NA";
                    }
                }

                Long time = entry.getKey();
                analogData.PushData(dataArray, new Date(time * 1000));
            }
            analogData.SetTitle(fieldNames);
            result.getDataSuccess(analogData);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            result.getDataFailed(null,ex.getMessage());
        }
        return result;
    }


    public Result GetListTitle(Integer devId, String signals, Long t_begin, Long t_end)
    {
        Result result = new Result();

        try
        {
            List<Short> signalList = JSON.parseArray(signals, Short.class);
            SimpleKV<Integer, String> attr = dataDao.GetRcdNumber(signalList, devId, t_begin, t_end);
            JSONObject jsonObj = new JSONObject();
            JSONArray titles = new JSONArray();
            jsonObj.put("cookie", attr.getValue());
            jsonObj.put("records", attr.getKey());
            jsonObj.put("titles", titles);
            for (byte i = 0; i < signalList.size(); ++i)
            {
                T_FIELD fieldObj = fieldDao.FindById(signalList.get(i).intValue());
                SimpleKV<String, String> pair = new SimpleKV<String, String>("a_val" + (i + 1), fieldObj.GetFieldName());
                titles.add(pair);
            }
            titles.add(new SimpleKV<String,String>("time","时间"));
            result.getDataSuccess(jsonObj);
        }catch (Exception ex)
        {
            ex.printStackTrace();
            result.getDataFailed(null,ex.getMessage());
        }
        return result;
    }


    public Result GetDevHistoryData(String signals, Integer devId, Long t_begin, Long t_end, DataTableInputParameters parameters)
    {
        Result result = new Result();
        try
        {
            List<Short> signalList = JSON.parseArray(signals, Short.class);
            Map<String, String> rcdMap = FormatUtil.ParseAttributes(parameters.cookie, ";", "=");
            Map<Byte, Integer> dataDistribute = new HashMap<Byte, Integer>();
            for (Map.Entry<String, String> entry : rcdMap.entrySet())
                dataDistribute.put(Byte.parseByte(entry.getKey()), Integer.parseInt(entry.getValue()));

            int signalNumber = signalList.size();
            AnalogData_History analogData = new AnalogData_History();

            //for export
            if(null == parameters.iDisplayStart && null == parameters.iDisplayLength)
            {
                List<T_DATA> dataList = dataDao.FindNopagedData(signalList, devId, t_begin,t_end);

                Map<Integer,T_FIELD> fieldMap=new HashMap<Integer,T_FIELD>();
                for(byte i=0;i<signalList.size();++i)
                {
                    int fieldId=signalList.get(i).intValue();
                    T_FIELD fieldObj=fieldDao.FindById(fieldId);
                    fieldMap.put(fieldId,fieldObj);
                }

                for (T_DATA dataObj : dataList)
                    analogData.PushData(MetaDataBase.GetContainerClazz(signalNumber), fieldMap.get(dataObj.fieldId).GetAnanlogValue(dataObj.value), new Date(dataObj.Time * 1000));

            }
            else//only history
            {
                int pageNo = parameters.iDisplayStart / parameters.iDisplayLength;
                byte pageSize = parameters.iDisplayLength;
                if (1 == signalNumber)
                {
                    List<T_DATA> dataList = dataDao.FindPagedData(dataDistribute, signalList, devId, t_begin, t_end, pageNo, pageSize);
                    T_FIELD fieldObj = fieldDao.FindById(signalList.get(0).intValue());
                    for (T_DATA dataObj : dataList)
                        analogData.PushData(MetaData_Str_S1.class, fieldObj.GetAnanlogValue(dataObj.value), new Date(dataObj.Time * 1000));

                } else
                {
                    //
                    pageNo = pageNo * signalNumber;
                    pageSize = (byte) (pageSize * signalNumber);

                    List<T_DATA> dataList = dataDao.FindPagedData(dataDistribute, signalList, devId, t_begin, t_end, pageNo, pageSize);

                    Map<Long, Map<Integer, T_DATA>> standardData = this.flipData(dataList);

                    T_FIELD[] fieldCache = new T_FIELD[signalNumber];
                    for (Map.Entry<Long, Map<Integer, T_DATA>> entry : standardData.entrySet())
                    {
                        String[] dataArray = new String[signalNumber];

                        Map<Integer, T_DATA> innerMap = entry.getValue();
                        for (byte index = 0; index < signalNumber; ++index)
                        {
                            T_FIELD fieldObj = null;
                            T_DATA dataObj = innerMap.get(signalList.get(index).intValue());
                            if (null != dataObj)
                            {
                                if (null == fieldCache[index])
                                {
                                    fieldObj = fieldDao.FindById(dataObj.fieldId);
                                    fieldCache[index] = fieldObj;
                                }
                                dataArray[index] = fieldCache[index].GetFieldValue(dataObj.value);
                            } else
                            {
                                dataArray[index] = "NA";
                            }
                        }

                        Long time = entry.getKey();
                        analogData.PushData(MetaDataBase.GetContainerClazz(signalNumber), dataArray, new Date(time * 1000));

                    }
                }
            }

            analogData.setiTotalRecords(parameters.iTotalRecords);
            analogData.setiTotalDisplayRecords(parameters.iTotalRecords);
            analogData.setsEcho(parameters.sEcho);

            result.getDataSuccess(analogData);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            result.getDataFailed(null,ex.getMessage());
        }
        return result;
    }*/

}

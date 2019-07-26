package com.xunwei.collectdata;

import com.xunwei.collectdata.utils.FormatUtil;

import java.util.HashMap;
import java.util.Map;

public class FieldSignal extends AbsCommonData{
    private Integer proCode;
    private Integer bitIndex;
    private Integer mapIndex;
    private String meaning;
    private Integer category;
    private String valType;
    private String alarmCfg;
    private String saveStrategy;
    private Integer magTimes;
    private String unit;
    private Integer disMode;
    private String disOptions;
    private String remark;
    private Integer schemaId;

    public class ConfigureProp
    {

        /**
         * confId : 配置标识符
         * displayMode : 展现方式
         * maxVal : 最大值
         * minVal : 最小值
         */
        public ConfigureProp()
        {
            optMap = new HashMap<Integer, String>();
        }

        public Integer confId;
        public Integer displayMode;
        public Integer maxVal;
        public Integer minVal;
        public Map<Integer, String> optMap;
    }


    public ConfigureProp cfgProp;

    public String getDisOptions()
    {
        return this.disOptions;
    }

    public void setDisOptions(String disOptions)
    {
        this.disOptions = disOptions;

        if (null == disOptions || null == disMode)
            return;
        try
        {
            cfgProp = new ConfigureProp();
            cfgProp.confId = id;
            cfgProp.displayMode = disMode;

            if (0 == disMode)
            {
                Map<String, String> attrMap = FormatUtil.ParseAttributes(this.disOptions, ";", "=");
                cfgProp.maxVal = Integer.parseInt(attrMap.get("max"));
                cfgProp.minVal = Integer.parseInt(attrMap.get("min"));
            } else if (1 == disMode)
            {
                Map<String, String> attrMap = FormatUtil.ParseAttributes(this.disOptions, ";", "=");
                for (Map.Entry<String, String> entry : attrMap.entrySet())
                {
                    cfgProp.optMap.put(Integer.valueOf(entry.getKey(), 16), entry.getValue());
                }
            } else
            {
                System.out.println("Not supported configure display mode " + disMode);
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    public String GetFieldValue(Integer signalVal)
    {
        if (5 == this.category)//analog
        {
            return GetAnanlogValue(signalVal);
        } else if (2 == this.category)//status
        {
            if (0 == this.disMode)
            {
                return GetAnanlogValue(signalVal);
            } else if (1 == this.disMode)
            {
                String optName = this.cfgProp.optMap.get(signalVal);
                return (null == optName ? "Unknown" : optName);
            } else
            {
                return "Unknown";
            }
        } else if (3 == this.category)
        {
            if (0 == this.disMode)
            {
                return GetAnanlogValue(signalVal);
            } else if (1 == this.disMode)
            {
                String optName = this.cfgProp.optMap.get(signalVal);
                return (null == optName ? "Unknown" : optName);
            } else
            {
                return "Unknown";
            }
        }
        else
            return "Unknown";

    }

    public String GetAnanlogValue(Integer fieldVal)
    {
        return (null != this.magTimes) ? FormatUtil.Float2String("0.0", (float)fieldVal/this.magTimes) : fieldVal.toString();
    }

    public Integer getProCode() {
        return proCode;
    }

    public void setProCode(Integer proCode) {
        this.proCode = proCode;
    }

    public Integer getBitIndex() {
        return bitIndex;
    }

    public void setBitIndex(Integer bitIndex) {
        this.bitIndex = bitIndex;
    }

    public Integer getMapIndex() {
        return mapIndex;
    }

    public void setMapIndex(Integer mapIndex) {
        this.mapIndex = mapIndex;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public String getValType() {
        return valType;
    }

    public void setValType(String valType) {
        this.valType = valType;
    }

    public String getAlarmCfg() {
        return alarmCfg;
    }

    public void setAlarmCfg(String alarmCfg) {
        this.alarmCfg = alarmCfg;
    }

    public String getSaveStrategy() {
        return saveStrategy;
    }

    public void setSaveStrategy(String saveStrategy) {
        this.saveStrategy = saveStrategy;
    }

    public Integer getMagTimes() {
        return magTimes;
    }

    public void setMagTimes(Integer magTimes) {
        this.magTimes = magTimes;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getDisMode() {
        return disMode;
    }

    public void setDisMode(Integer disMode) {
        this.disMode = disMode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(Integer schemaId) {
        this.schemaId = schemaId;
    }
}

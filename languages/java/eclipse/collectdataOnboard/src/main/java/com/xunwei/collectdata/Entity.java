package com.xunwei.collectdata;

import com.xunwei.collectdata.AbsCommonData;

public class Entity extends AbsCommonData {
    private Integer schemaId;
    private Integer month;

    public Integer getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(Integer schemaId) {
        this.schemaId = schemaId;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }
}

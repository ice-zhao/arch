package com.xunwei.collectdata;

import java.io.Serializable;

public class HostData extends AbsCommonData implements Serializable {
    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}

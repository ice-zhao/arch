package com.xunwei.collectdata.devices;

public class Host implements IDevice {
    private Integer id;
    private String hostID;
    private Integer areaID;
    private Integer buildingID;
    private String name;
    private String serial;
    private String RemoteServerAddr;
    private Integer floor;

    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    public Integer getAreaID() {
        return areaID;
    }

    public void setAreaID(Integer areaID) {
        this.areaID = areaID;
    }

    public Integer getBuildingID() {
        return buildingID;
    }

    public void setBuildingID(Integer buildingID) {
        this.buildingID = buildingID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getRemoteServerAddr() {
        return RemoteServerAddr;
    }

    public void setRemoteServerAddr(String remoteServerAddr) {
        RemoteServerAddr = remoteServerAddr;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

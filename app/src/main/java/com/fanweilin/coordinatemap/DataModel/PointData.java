package com.fanweilin.coordinatemap.DataModel;

/**
 * Created by Administrator on 2016/7/17 0017.
 */
public class PointData {
    private String filename;
    private String address;
    private String wgslatitude;
    private String wgslongitude;
    private String baidulatitude;
    private String baidulongitude;
    private String altitude;
    private String pointname;
    private String describe;
    private Long guid;
    private int id;
    public PointData() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWgslatitude() {
        return wgslatitude;
    }

    public void setWgslatitude(String wgslatitude) {
        this.wgslatitude = wgslatitude;
    }

    public String getWgslongitude() {
        return wgslongitude;
    }

    public void setWgslongitude(String wgslongitude) {
        this.wgslongitude = wgslongitude;
    }

    public String getBaidulatitude() {
        return baidulatitude;
    }

    public void setBaidulatitude(String baidulatitude) {
        this.baidulatitude = baidulatitude;
    }

    public String getBaidulongitude() {
        return baidulongitude;
    }

    public void setBaidulongitude(String baidulongitude) {
        this.baidulongitude = baidulongitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getPointname() {
        return pointname;
    }

    public void setPointname(String pointname) {
        this.pointname = pointname;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getGuid() {
        return guid;
    }

    public void setGuid(Long guid) {
        this.guid = guid;
    }
}

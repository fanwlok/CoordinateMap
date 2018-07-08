package com.fanweilin.coordinatemap.DataModel;

/**
 * Created by Administrator on 2018/2/13.
 */

public class Poi {
    private String id;
    private String geotable_id;
    private String title;
    private float[]location;
    private float[]gcj_location;
    private String address;
    private String tags;
    private String create_time;
    private String modify_time;
    //所在的省份
    private String province;
    //所在的区
    private String district;
    private String describe;
    private int datatype;
    private String polygons;
    private int vip;
    private int markerid;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGeotable_id() {
        return geotable_id;
    }

    public void setGeotable_id(String geotable_id) {
        this.geotable_id = geotable_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float[] getLocation() {
        return location;
    }

    public void setLocation(float[] location) {
        this.location = location;
    }

    public float[] getGcj_location() {
        return gcj_location;
    }

    public void setGcj_location(float[] gcj_location) {
        this.gcj_location = gcj_location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getModify_time() {
        return modify_time;
    }

    public void setModify_time(String modify_time) {
        this.modify_time = modify_time;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getDatatype() {
        return datatype;
    }

    public void setDatatype(int datatype) {
        this.datatype = datatype;
    }

    public String getPolygons() {
        return polygons;
    }

    public void setPolygons(String polygons) {
        this.polygons = polygons;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getMarkerid() {
        return markerid;
    }

    public void setMarkerid(int markerid) {
        this.markerid = markerid;
    }
}

package com.fanweilin.coordinatemap.DataModel;

/**
 * Created by Administrator on 2018/2/12.
 */

public class BaiduDataClass {
    private String id;
    private String title;
    private String address;
    private String tags;
    private String describe;
    private double latitude;
    private double longitude;
    private int coord_type;
    private String geotable_id;
    private String usermap_id;
    private String ak;
    private int  markerid;
    /*0 poinr
      1 linw
      1 polygon
    * */
    private int datatype;
    private String polygons;
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getCoord_type() {
        return coord_type;
    }

    public void setCoord_type(int coord_type) {
        this.coord_type = coord_type;
    }

    public String getGeotable_id() {
        return geotable_id;
    }

    public void setGeotable_id(String geotable_id) {
        this.geotable_id = geotable_id;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getUsermap_id() {
        return usermap_id;
    }

    public void setUsermap_id(String usermap_id) {
        this.usermap_id = usermap_id;
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


    public int getMarkerid() {
        return markerid;
    }

    public void setMarkerid(int markerid) {
        this.markerid = markerid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

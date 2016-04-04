package com.fanweilin.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table SHOW_DATA.
 */
public class ShowData {

    private Long id;
    /** Not-null value. */
    private String title;
    private String latitude;
    private String longitude;
    private Integer cdstyle;
    private Long fileid;
    private Long pointid;
    private Integer datastyle;

    public ShowData() {
    }

    public ShowData(Long id) {
        this.id = id;
    }

    public ShowData(Long id, String title, String latitude, String longitude, Integer cdstyle, Long fileid, Long pointid, Integer datastyle) {
        this.id = id;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cdstyle = cdstyle;
        this.fileid = fileid;
        this.pointid = pointid;
        this.datastyle = datastyle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getTitle() {
        return title;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Integer getCdstyle() {
        return cdstyle;
    }

    public void setCdstyle(Integer cdstyle) {
        this.cdstyle = cdstyle;
    }

    public Long getFileid() {
        return fileid;
    }

    public void setFileid(Long fileid) {
        this.fileid = fileid;
    }

    public Long getPointid() {
        return pointid;
    }

    public void setPointid(Long pointid) {
        this.pointid = pointid;
    }

    public Integer getDatastyle() {
        return datastyle;
    }

    public void setDatastyle(Integer datastyle) {
        this.datastyle = datastyle;
    }

}

package com.fanweilin.greendao;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "SHOW_DATA".
 */
@Entity
public class ShowData {

    @Id(autoincrement = true)
    private Long id;
    private String title;
    private String latitude;
    private String longitude;
    private String wgslatitude;
    private String wgslongitude;
    private String baidulatitude;
    private String baidulongitude;
    private Integer cdstyle;
    private Long fileid;
    private Long pointid;
    private Integer datastyle;
    private Integer style;

    @Generated
    public ShowData() {
    }

    public ShowData(Long id) {
        this.id = id;
    }

    @Generated
    public ShowData(Long id, String title, String latitude, String longitude, String wgslatitude, String wgslongitude, String baidulatitude, String baidulongitude, Integer cdstyle, Long fileid, Long pointid, Integer datastyle, Integer style) {
        this.id = id;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.wgslatitude = wgslatitude;
        this.wgslongitude = wgslongitude;
        this.baidulatitude = baidulatitude;
        this.baidulongitude = baidulongitude;
        this.cdstyle = cdstyle;
        this.fileid = fileid;
        this.pointid = pointid;
        this.datastyle = datastyle;
        this.style = style;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

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

    public Integer getStyle() {
        return style;
    }

    public void setStyle(Integer style) {
        this.style = style;
    }

}

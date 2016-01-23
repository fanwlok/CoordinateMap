package com.fanweilin.coordinatemap.computing;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by Administrator on 2016/1/10 0010.
 */
public class DataItem {
    String name;
    LatLng latLng;

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getName() {
        return name;
    }
}

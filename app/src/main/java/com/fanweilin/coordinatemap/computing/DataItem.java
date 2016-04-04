package com.fanweilin.coordinatemap.computing;

import com.baidu.mapapi.model.LatLng;
import com.fanweilin.coordinatemap.Class.PointDataParcel;

/**
 * Created by Administrator on 2016/1/10 0010.
 */
public class DataItem {
    String name;
    LatLng latLng;

    public PointDataParcel getPointDataParcel() {
        return pointDataParcel;
    }

    public void setPointDataParcel(PointDataParcel pointDataParcel) {
        this.pointDataParcel = pointDataParcel;
    }

    PointDataParcel pointDataParcel;
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

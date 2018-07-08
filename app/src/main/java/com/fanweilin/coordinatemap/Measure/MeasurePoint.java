package com.fanweilin.coordinatemap.Measure;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/13 0013.
 */

public class MeasurePoint implements Serializable {
    private int Fisrst =0;
    private int middle=2;
    private  int end=3;
    private double lat;
    private double lon;
    private boolean isBIG;
    private int type;
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public boolean isBIG() {
        return isBIG;
    }

    public void setBIG(boolean BIG) {
        isBIG = BIG;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }



}

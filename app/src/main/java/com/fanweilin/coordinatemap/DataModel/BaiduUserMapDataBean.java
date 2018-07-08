package com.fanweilin.coordinatemap.DataModel;

import java.util.List;

/**
 * Created by Administrator on 2018/2/13.
 */

public class BaiduUserMapDataBean {
    private int status;
    private String message;
    private int size;
    private int total;
    private List<Poi> pois;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Poi> getPois() {
        return pois;
    }

    public void setPois(List<Poi> pois) {
        this.pois = pois;
    }

}

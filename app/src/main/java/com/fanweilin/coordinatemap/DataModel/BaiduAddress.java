package com.fanweilin.coordinatemap.DataModel;

/**
 * Created by Administrator on 2018/6/24.
 */

public class BaiduAddress {
    private int status;
    private addressComponent addressComponent;
    private String sematic_description;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public com.fanweilin.coordinatemap.DataModel.addressComponent getAddressComponent() {
        return addressComponent;
    }

    public void setAddressComponent(com.fanweilin.coordinatemap.DataModel.addressComponent addressComponent) {
        this.addressComponent = addressComponent;
    }

    public String getSematic_description() {
        return sematic_description;
    }

    public void setSematic_description(String sematic_description) {
        this.sematic_description = sematic_description;
    }
}

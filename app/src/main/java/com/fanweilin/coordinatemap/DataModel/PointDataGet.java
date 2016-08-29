package com.fanweilin.coordinatemap.DataModel;

import java.util.Date;

/**
 * Created by Administrator on 2016/7/28 0028.
 */
public class PointDataGet {
    private Long id;
    private Date anchor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getAnchor() {
        return anchor;
    }

    public void setAnchor(Date anchor) {
        this.anchor = anchor;
    }
}

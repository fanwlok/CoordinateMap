package com.fanweilin.coordinatemap.DataModel;

import java.util.Date;

/**
 * Created by Administrator on 2016/8/2 0002.
 */
public class FilesClass {
    private long id;
    private String filename;
    private  String date;

    public FilesClass() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

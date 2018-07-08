package com.fanweilin.coordinatemap.Class;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Administrator on 2016/3/13 0013.
 */
public class PointDataParcel implements Parcelable {
    private String activity;
    private String address;
    private String wgsLatitude;
    private String wgsLongitude;
    private String baiduLatitude;
    private String baiduLongitude;
    private String gcjLatitude;
    private String gcjLongitude;
    private String altitude;
    private String pointname;
    private String describe;
    private long pointdataid;

    public long getFileid() {
        return fileid;
    }

    public void setFileid(long fileid) {
        this.fileid = fileid;
    }

    private long fileid;

    public long getPointdataid() {
        return pointdataid;
    }

    public void setPointdataid(long pointdataid) {
        this.pointdataid = pointdataid;
    }


    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getPointname() {
        return pointname;
    }

    public void setPointname(String pointname) {
        this.pointname = pointname;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    protected PointDataParcel(Parcel in) {
        address = in.readString();
        wgsLatitude = in.readString();
        wgsLongitude = in.readString();
        baiduLatitude = in.readString();
        baiduLongitude = in.readString();
        gcjLatitude = in.readString();
        gcjLongitude = in.readString();
        altitude = in.readString();
        pointname = in.readString();
        describe = in.readString();
        activity = in.readString();
        pointdataid = in.readLong();
        fileid = in.readLong();
    }

    public PointDataParcel() {

    }

    public static final Creator<PointDataParcel> CREATOR = new Creator<PointDataParcel>() {
        @Override
        public PointDataParcel createFromParcel(Parcel in) {
            return new PointDataParcel(in);
        }


        @Override
        public PointDataParcel[] newArray(int size) {
            return new PointDataParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(wgsLatitude);
        dest.writeString(wgsLongitude);
        dest.writeString(baiduLatitude);
        dest.writeString(baiduLongitude);
        dest.writeString(gcjLatitude);
        dest.writeString(gcjLongitude);
        dest.writeString(altitude);
        dest.writeString(pointname);
        dest.writeString(describe);
        dest.writeString(activity);
        dest.writeLong(pointdataid);
        dest.writeLong(fileid);
    }


    public String getWgsLatitude() {
        return wgsLatitude;
    }

    public void setWgsLatitude(String wgsLatitude) {
        this.wgsLatitude = wgsLatitude;
    }

    public String getWgsLongitude() {
        return wgsLongitude;
    }

    public void setWgsLongitude(String wgsLongitude) {
        this.wgsLongitude = wgsLongitude;
    }

    public String getBaiduLatitude() {
        return baiduLatitude;
    }

    public void setBaiduLatitude(String baiduLatitude) {
        this.baiduLatitude = baiduLatitude;
    }

    public String getBaiduLongitude() {
        return baiduLongitude;
    }

    public void setBaiduLongitude(String baiduLongitude) {
        this.baiduLongitude = baiduLongitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getGcjLatitude() {
        return gcjLatitude;
    }

    public void setGcjLatitude(String gcjLatitude) {
        this.gcjLatitude = gcjLatitude;
    }

    public String getGcjLongitude() {
        return gcjLongitude;
    }

    public void setGcjLongitude(String gcjLongitude) {
        this.gcjLongitude = gcjLongitude;
    }
}

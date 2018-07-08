package com.fanweilin.coordinatemap.Class;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanweilin.coordinatemap.Measure.MeasureDistance;

/**
 * Created by Administrator on 2017/7/11.
 */

public class MeasureParcel implements Parcelable {
    private String Content;
    private MeasureDistance measureDistance=new MeasureDistance();
    private long id;
    private String name;
    private String describe;
    protected MeasureParcel(Parcel in) {
        Content = in.readString();
       measureDistance= (MeasureDistance) in.readValue(MeasureDistance.class.getClassLoader());
        id=in.readLong();
        name=in.readString();
        describe=in.readString();
    }
    public MeasureParcel(){}

    public static final Creator<MeasureParcel> CREATOR = new Creator<MeasureParcel>() {
        @Override
        public MeasureParcel createFromParcel(Parcel in) {
            return new MeasureParcel(in);
        }

        @Override
        public MeasureParcel[] newArray(int size) {
            return new MeasureParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Content);
        dest.writeValue(measureDistance);
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(describe);
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public MeasureDistance getMeasureDistance() {
        return measureDistance;
    }

    public void setMeasureDistance(MeasureDistance measureDistance) {
        this.measureDistance = measureDistance;
    }
}

package com.fanweilin.coordinatemap.EventBus;

import android.location.Location;

import com.baidu.location.BDLocation;

/**
 * Created by Administrator on 2016/4/5 0005.
 */
public class ServiceEvents {
    public static class LocationUpdate {
        public BDLocation location;
        public LocationUpdate(BDLocation loc) {
            this.location = loc;
        }
    }
    public static class WgsLocation{
        public  Location location;
        public  WgsLocation(Location location){
            this.location=location;
        }
    }
}

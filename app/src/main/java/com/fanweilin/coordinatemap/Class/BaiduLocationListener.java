package com.fanweilin.coordinatemap.Class;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.fanweilin.coordinatemap.Activity.LocationService;

/**
 * Created by Administrator on 2016/4/5 0005.
 */
public class BaiduLocationListener implements BDLocationListener{
    private LocationService locationService;
    public BaiduLocationListener(LocationService locationService){
       this.locationService=locationService;
    }
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
          locationService.onlocationchange(bdLocation);
    }


}

package com.fanweilin.coordinatemap.Class;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.fanweilin.coordinatemap.Activity.LocationService;

/**
 * Created by Administrator on 2016/4/5 0005.
 */
public class BaiduLocationListener implements BDLocationListener {
    private LocationService locationService;
    public BaiduLocationListener(LocationService locationService){
       this.locationService=locationService;
    }
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
          locationService.onlocationchange(bdLocation);
    }
}

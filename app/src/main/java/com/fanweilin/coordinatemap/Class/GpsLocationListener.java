package com.fanweilin.coordinatemap.Class;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.fanweilin.coordinatemap.Activity.LocationService;

/**
 * Created by Administrator on 2016/8/28 0028.
 */
public class GpsLocationListener implements LocationListener {
    private LocationService locationService;
    public GpsLocationListener(LocationService locationService){
        this.locationService=locationService;
    }
    @Override
    public void onLocationChanged(Location location) {
        locationService.locationchange(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

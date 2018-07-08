package com.fanweilin.coordinatemap.Compass.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.fanweilin.coordinatemap.Activity.data;
import com.fanweilin.coordinatemap.Compass.location.model.LocationData;
import com.fanweilin.coordinatemap.Compass.utils.DLog;
import com.fanweilin.coordinatemap.DataModel.BaiduAddress;
import com.fanweilin.coordinatemap.DataModel.BaiduDataApi;
import com.fanweilin.coordinatemap.DataModel.BaiduHttpControl;
import com.fanweilin.coordinatemap.DataModel.Constants;
import com.fanweilin.coordinatemap.DataModel.HttpControl;
import com.fanweilin.coordinatemap.computing.Location3TheConvert;

import org.osmdroid.util.GeoPoint;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * Created by Duy on 10/16/2017.
 */

public class LocationListener implements android.location.LocationListener {
    private static final String TAG = "LocationListener";
    @NonNull
    private Context mContext;
    @Nullable
    private LocationHelper.LocationDataChangeListener mLocationValueListener;

    public LocationListener(@NonNull Context context) {
        this.mContext = context;
    }

    public void setLocationValueListener(@Nullable LocationHelper.LocationDataChangeListener listener) {
        this.mLocationValueListener = listener;
    }

    @Override
    public void onLocationChanged(@Nullable final Location location) {
        DLog.d(TAG, "onLocationChanged() called with: location = [" + location + "]");
        if (mLocationValueListener != null && location != null) {

            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            final LocationData weatherData = new LocationData();
            weatherData.setLongitude((float) longitude);
            weatherData.setLatitude((float) latitude);
            weatherData.setAltitude(location.getAltitude());
            GeoCoder geoCoder=GeoCoder.newInstance();
            ReverseGeoCodeOption op = new ReverseGeoCodeOption();
             GeoPoint gcj =Location3TheConvert.ConverToGCJ2(latitude,longitude, Location3TheConvert.WGS84);
            op.location(new LatLng(gcj.getLatitude(),gcj.getLongitude()));
            geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                @Override
                public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                    Log.d("TAG",geoCodeResult.getAddress());

                }

                @Override
                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                    weatherData.setAddressLine(reverseGeoCodeResult.getAddress());
                    weatherData.setAdcode(reverseGeoCodeResult.getAdcode());
                    mLocationValueListener.onUpdateLocationData(weatherData);
                }
            });
            geoCoder.reverseGeoCode(op);

        }
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

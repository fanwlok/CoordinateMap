package com.fanweilin.coordinatemap.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.fanweilin.coordinatemap.Activity.data;
import com.threshold.rxbus2.RxBus;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Administrator on 2018/6/24.
 */

abstract public class BaiduMapLocationUtils {
    private  BaiduMapLocationUtils mLocationUtils;

    public BaiduMapLocationUtils(Context context) {
        init(context);
    }

    private LocationClient mLocationClient;
    private MyLocationListener myListener = new MyLocationListener();
    private void init(Context context) {
        //声明LocationClient类
        mLocationClient = new LocationClient(context);
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);

        LocationClientOption locationClientOption = initOption();
        mLocationClient.setLocOption(locationClientOption);
        mLocationClient.start();
        //启动定位
    }
    private LocationClientOption initOption() {
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(data.get());
        int time = Integer.parseInt(prefs.getString("gps_rate", "1"));
        option.setScanSpan(time);
        option.setCoorType("gcj02");
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        option.setEnableSimulateGps(false);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        return option;
    }
    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            onLocationlistener(bdLocation);
        }
    }
protected abstract void onLocationlistener(BDLocation bdLocation);
    public void onStop() {
        mLocationClient.stop();
    }
}

package com.fanweilin.coordinatemap.Activity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.fanweilin.coordinatemap.Class.BaiduLocationListener;
import com.fanweilin.coordinatemap.EventBus.ServiceEvents;

import de.greenrobot.event.EventBus;

public class LocationService extends Service {
    private LocationClient bdLocationClient;
    private BaiduLocationListener bdLocationListener;
    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        bdLocationClient=new LocationClient(getApplicationContext());
        initLocation();
        super.onCreate();
    }
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("gcj02");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        bdLocationListener=new BaiduLocationListener(this);
        bdLocationClient.setLocOption(option);
        bdLocationClient.start();
        bdLocationClient.registerLocationListener(bdLocationListener);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    private void RegisterEventBus() {
        EventBus.getDefault().registerSticky(this);
    }

    private void UnregisterEventBus(){
        try {
            EventBus.getDefault().unregister(this);
        } catch (Throwable t){
            //this may crash if registration did not go through. just be safe
        }
    }
   public void onlocationchange(BDLocation bdloc){
       EventBus.getDefault().post(new ServiceEvents.LocationUpdate(bdloc));
   }
    @Override
    public void onDestroy() {
        bdLocationClient.unRegisterLocationListener(bdLocationListener);
        bdLocationClient.stop();
        UnregisterEventBus();
        super.onDestroy();
    }
}

package com.fanweilin.coordinatemap.Compass.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.fanweilin.coordinatemap.Activity.WebActivity;
import com.fanweilin.coordinatemap.Activity.data;
import com.fanweilin.coordinatemap.Compass.location.LocationHelper;
import com.fanweilin.coordinatemap.Compass.location.WeatherManager;
import com.fanweilin.coordinatemap.Compass.location.database.CompassPref;
import com.fanweilin.coordinatemap.Compass.location.model.LocationData;
import com.fanweilin.coordinatemap.Compass.location.model.Sunshine;
import com.fanweilin.coordinatemap.Compass.sensor.SensorListener;
import com.fanweilin.coordinatemap.Compass.sensor.view.AccelerometerView;
import com.fanweilin.coordinatemap.Compass.sensor.view.CompassView2;
import com.fanweilin.coordinatemap.Compass.utils.Utility;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.location.BaiduMapLocationUtils;
import com.threshold.rxbus2.RxBus;

import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.fanweilin.coordinatemap.Compass.utils.Utility.getDirectionText;
import static com.fanweilin.coordinatemap.computing.ConvertLatlng.convertToSexagesimal;


/**
 * Created by Duy on 10/17/2017.
 */

public class CompassFragment extends BaseFragment implements SensorListener.OnValueChangedListener,
        LocationHelper.LocationDataChangeListener {
    public static final String TAG = "CompassFragment";
    private static final int REQUEST_ENABLE_GPS = 1002;
    private TextView mTxtAddress;
    private TextView mTxtSunrise, mTxtSunset;
    private TextView mTxtPitch, mTxtRoll;
    private TextView mTxtLonLat, mTxtAltitude;
    private TextView mTxtPressure, mTxtHumidity, mTxtTemp;
    private ImageView mImgWeather;
    private LocationHelper mLocationHelper;
    private CompassView2 mCompassView;
    private AccelerometerView mAccelerometerView;
    private SensorListener mSensorListener;
    private CompassPref mCompassPref;
    myLocation location;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         location=new myLocation(data.get());
    }

    private LinearLayout llweather;
    double lon ;
    double lat;
    CompositeDisposable mCompositeDisposable;
    public static CompassFragment newInstance() {

        Bundle args = new Bundle();

        CompassFragment fragment = new CompassFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindView();

        mLocationHelper = new LocationHelper(this);
        mLocationHelper.setLocationValueListener(this);
        mLocationHelper.onCreate();

        mSensorListener = new SensorListener(getContext());
        mSensorListener.setOnValueChangedListener(this);

        if (!Utility.isNetworkAvailable(getContext())) {
            Toast.makeText(getContext(), "No internet access", Toast.LENGTH_SHORT).show();
        } else {
            LocationManager manager = (LocationManager) getContext()
                    .getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            }
        }

        onUpdateLocationData(null);
    }

    private void bindView() {
        mTxtAddress = (TextView) findViewById(R.id.txt_address);
        mTxtAddress.setSelected(true);

        mTxtSunrise = (TextView) findViewById(R.id.txt_sunrise);
        mTxtSunset = (TextView) findViewById(R.id.txt_sunset);

        mTxtLonLat = (TextView) findViewById(R.id.txt_lon_lat);
        mTxtAltitude = (TextView) findViewById(R.id.txt_altitude);

        mCompassView = (CompassView2) findViewById(R.id.compass_view);
        mAccelerometerView = (AccelerometerView) findViewById(R.id.accelerometer_view);

        mTxtPressure = (TextView) findViewById(R.id.txt_pressure);
        mTxtHumidity = (TextView) findViewById(R.id.txt_humidity);
        mImgWeather = (ImageView) findViewById(R.id.img_weather);
        mTxtTemp = (TextView) findViewById(R.id.txt_temp);
        llweather= (LinearLayout) findViewById(R.id.container_weather);
        llweather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                if(isFirst){
                   String url="http://m.weather.com.cn/d/town/index?";
                    String latlng="lat="+String.valueOf(lat)+"&"+"lon="+String.valueOf(lon);
                    Log.d("url",url+latlng);
                   intent.putExtra("url",url+latlng);
                }else {
                    intent.putExtra("url","");
                }

                intent.setClass(getActivity(), WebActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mSensorListener != null) {
            mSensorListener.start();
        }
    }
   class  myLocation extends BaiduMapLocationUtils {

       public myLocation(Context context) {
           super(context);
       }

       @Override
       protected void onLocationlistener(BDLocation bdLocation) {
           if ( bdLocation.getAdCode()==null)
               return;
           if(!isFirst){
               setWeather(Integer.valueOf(bdLocation.getAdCode()));
           }
           isFirst=true;
           lat=bdLocation.getLatitude();
           lon=bdLocation.getLongitude();
       }
   }
    @Override
    public void onStop() {
        if (mSensorListener != null) {
            mSensorListener.stop();
        }
        super.onStop();

    }


    @Override
    public void onRotationChanged(float azimuth, float roll, float pitch) {
        String str = ((int) azimuth) + "° " + getDirectionText(azimuth);
        mCompassView.getSensorValue().setRotation(azimuth, roll, pitch);
        mAccelerometerView.getSensorValue().setRotation(azimuth, roll, pitch);

    }

    @Override
    public void onMagneticFieldChanged(float value) {
        mCompassView.getSensorValue().setMagneticField(value);
    }

    @Override
    public void onPressure(float value) {
        mTxtPressure.setText(String.format(Locale.US, "%.1f hPa", value));
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_compass;
    }

    //https://stackoverflow.com/questions/39336461/how-can-i-enable-or-disable-the-gps-programmatically-on-android-6-x
    private void buildAlertMessageNoGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, REQUEST_ENABLE_GPS);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_GPS:
                mLocationHelper.onCreate();
                break;
        }
    }

private Boolean isFirst=false;
    @Override
    public void onUpdateLocationData(@Nullable LocationData locationData) {
        if (locationData == null) {
          /*  locationData = mCompassPref.getLastedLocationData();*/
        }
        if (locationData != null) {
            //sunshine
            Sunshine sunshine = locationData.getSunshine();
            if (sunshine != null) {
                mTxtSunrise.setText(sunshine.getReadableSunriseTime());
                mTxtSunset.setText(sunshine.getReadableSunsetTime());
            }


            //address
            mTxtAddress.setText(locationData.getAddressLine());

            //location
             float longitude = locationData.getLongitude();
             float latitude = locationData.getLatitude();
            String lonStr =  convertToSexagesimal(longitude) + " " + getDirectionText(longitude);
            String latStr =  convertToSexagesimal(latitude) + " " + getDirectionText(latitude);
            mTxtLonLat.setText(String.format("%s\n%s", lonStr, latStr));

            //altitude
            double altitude = locationData.getAltitude();
            mTxtAltitude.setText(String.format(Locale.US, "%d m", (long) altitude));
        }
    }

    private void setWeather(int adcode) {
        //weather
        LocationData weatherLocation= WeatherManager.getWeatherData(adcode);
        mTxtHumidity.setText(String.format(Locale.US, "%s %%",weatherLocation.getHumidity()));
        mTxtTemp.setText(Utility.formatTemperature(getContext(),weatherLocation.getTemp()));
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        location.onStop();
    }
}

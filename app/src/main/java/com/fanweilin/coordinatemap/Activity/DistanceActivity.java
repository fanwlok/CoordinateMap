package com.fanweilin.coordinatemap.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.fanweilin.coordinatemap.EventBus.ServiceEvents;
import com.fanweilin.coordinatemap.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class DistanceActivity extends AppCompatActivity {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private CoordinateConverter converter;
    public double latitude;
    public double lontitude;
    private List<LatLng> pts;
    private List<Overlay> dotOptionses;
    private List<Overlay> polylineOptionses;
    private double totalDistance;
    private InfoWindow preInfo;
    private List<Double> listDistance;
    private View view;
    private TextView tvDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_distance);
        init();
    }

    public void init() {
        mMapView = (MapView) findViewById(R.id.map_distance_bdmap);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapClickListener(listener);
        converter = new CoordinateConverter();
        view = LayoutInflater.from(DistanceActivity.this).inflate(R.layout.infoview, null);
        pts = new ArrayList<LatLng>();
        dotOptionses = new ArrayList<Overlay>();
        polylineOptionses = new ArrayList<Overlay>();
        listDistance = new ArrayList<Double>();
        tvDistance = (TextView) view.findViewById(R.id.tv_info_distance);
        ImageButton btn = (ImageButton) view.findViewById(R.id.imgbtn_info_clean);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int last = dotOptionses.size() - 1;
                if (polylineOptionses.size() >= 1) {
                    Overlay dotOptions = dotOptionses.get(last);
                    dotOptions.remove();
                    dotOptionses.remove(last);
                    int linelast = polylineOptionses.size() - 1;
                    Overlay polylineOptions = polylineOptionses.get(linelast);

                    String distance;
                    if (polylineOptionses.size() == 1) ;
                    DecimalFormat df = new DecimalFormat("#.00");
                    totalDistance = listDistance.get(last - 1);
                    if (totalDistance >= 1000) {
                        distance = String.valueOf(df.format(listDistance.get(last - 1) / 1000)) + "km";
                    } else {
                        distance = String.valueOf(df.format(listDistance.get(last - 1))) + "m";
                    }
                    if (polylineOptionses.size() == 1) {
                        distance = "起点";
                    }
                    tvDistance.setText(distance);
                    polylineOptions.remove();
                    polylineOptionses.remove(linelast);
                    listDistance.remove(last);
                    InfoWindow mInfoWindow = new InfoWindow(view, pts.get(linelast), -15);
                    preInfo = mInfoWindow;
                    mBaiduMap.showInfoWindow(mInfoWindow);
                } else {
                    Overlay lastdotOptions = dotOptionses.get(0);
                    lastdotOptions.remove();
                    dotOptionses.clear();
                    polylineOptionses.clear();
                    listDistance.clear();
                    mBaiduMap.hideInfoWindow();
                }
                pts.remove(last);
            }
        });
        RegisterEventBus();
    }

    public void onEventMainThread(ServiceEvents.LocationUpdate bdLocation) {
        BDLocation location = bdLocation.location;
        LatLng gc02Lng = new LatLng(location.getLatitude(), location.getLongitude());
        converter.from(CoordinateConverter.CoordType.COMMON);
        converter.coord(gc02Lng);
        LatLng bdLng = converter.convert();
        if (latitude == 0.0) {
            latitude = bdLng.latitude;
            lontitude = bdLng.longitude;
            setMapCenter(latitude, lontitude);
        } else {
            latitude = bdLng.latitude;
            lontitude = bdLng.longitude;
        }
        showLocation(location, bdLng);
    }

    private void showLocation(BDLocation location, LatLng bdlat) {
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .direction(location.getDirection()).latitude(bdlat.latitude)
                .longitude(bdlat.longitude).build();
        mBaiduMap.setMyLocationData(locData);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
        mBaiduMap.setMyLocationConfigeration(config);
    }

    private void setMapCenter(double latitude, double lontitude) {
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newLatLng(new LatLng(latitude, lontitude));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18f));
        mBaiduMap.animateMapStatus(mMapStatusUpdate);
    }

    private void RegisterEventBus() {
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    protected void onDestroy() {
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        UnregisterEventBus();
        super.onDestroy();
    }

    BaiduMap.OnMapClickListener listener = new BaiduMap.OnMapClickListener() {
        /**
         * 地图单击事件回调函数
         * @param point 点击的地理坐标
         */
        public void onMapClick(LatLng point) {
            pts.add(point);
            DecimalFormat df = new DecimalFormat("#.00");
            if (pts.size() >= 2) {
                int n = pts.size();
                List<LatLng> linepoints = new ArrayList<LatLng>();
                linepoints.add(pts.get(n - 1));
                linepoints.add(pts.get(n - 2));
                double distance = DistanceUtil.getDistance(pts.get(n - 1), pts.get(n - 2));
                totalDistance += distance;
                listDistance.add(totalDistance);
                OverlayOptions lineOption = new PolylineOptions().color(0xFF0000FF).points(pts).width(5);
                polylineOptionses.add(mBaiduMap.addOverlay(lineOption));
            }
            String distance;
            if (totalDistance >= 1000) {
                distance = String.valueOf(df.format(totalDistance / 1000)) + "km";
            } else {
                distance = String.valueOf(df.format(totalDistance)) + "m";
            }
            if (pts.size() > 1) {
                final OverlayOptions dotOption = new DotOptions().center(point).color(0xFF0000FF).radius(10);
                dotOptionses.add(mBaiduMap.addOverlay(dotOption));
                tvDistance.setText(distance);
                InfoWindow mInfoWindow = new InfoWindow(view, point, -15);
                mBaiduMap.showInfoWindow(mInfoWindow);
            } else {
                listDistance.add(0.0);
                OverlayOptions dotOption = new DotOptions().center(point).color(0xFF0000FF).radius(10);
                dotOptionses.add(mBaiduMap.addOverlay(dotOption));
            }
        }


        /**
         * 地图内 Poi 单击事件回调函数
         * @param poi 点击的 poi 信息
         */
        public boolean onMapPoiClick(MapPoi poi) {
            return false;
        }
    };

    private void UnregisterEventBus() {
        try {
            EventBus.getDefault().unregister(this);
        } catch (Throwable t) {
            //this may crash if registration did not go through. just be safe
        }
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

}

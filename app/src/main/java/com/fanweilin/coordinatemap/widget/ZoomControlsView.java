package com.fanweilin.coordinatemap.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.fanweilin.coordinatemap.R;

/**
 * Created by Administrator on 2015/12/27 0027.
 */
public class ZoomControlsView extends RelativeLayout implements View.OnClickListener {
    private MapView mapView;
    private MapStatus mapStatus;
    private BaiduMap baiduMap;
    private ImageButton zoomIn;
    private ImageButton zoomOut;
    private float maxZoomLevel;
    private float minZoomLevel;

    public ZoomControlsView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();

    }


    public ZoomControlsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.zoom_controls, null);
        zoomIn = (ImageButton) view.findViewById(R.id.btn_zoom_in);
        zoomOut = (ImageButton) view.findViewById(R.id.btn_zoom_out);
        zoomIn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);
        addView(view);
    }

    public void setBaiduMap(MapView mapView) {
        if (mapView != null) {
            this.mapView = mapView;
            baiduMap = mapView.getMap();
            mapStatus = baiduMap.getMapStatus();
            maxZoomLevel = baiduMap.getMaxZoomLevel();
            minZoomLevel = baiduMap.getMinZoomLevel();
        } else {
            throw new NullPointerException("you should call setMapView(MapView mapView) at first");
        }
    }

    @Override
    public void onClick(View view) {
        mapStatus = baiduMap.getMapStatus();
        switch (view.getId()) {
            case R.id.btn_zoom_in:
                baiduMap.animateMapStatus(MapStatusUpdateFactory.zoomIn());
                break;
            case R.id.btn_zoom_out:
                baiduMap.animateMapStatus(MapStatusUpdateFactory.zoomOut());
                break;
        }
        mapStatus = mapView.getMap().getMapStatus();
        controlZoomShow();
    }

    private void controlZoomShow() {
        float zoom = baiduMap.getMapStatus().zoom;
        if (zoom >= maxZoomLevel) {
            zoomIn.setEnabled(false);
        } else {
            zoomIn.setEnabled(true);
        }
        if (zoom <= minZoomLevel) {
            zoomOut.setEnabled(false);
        } else {
            zoomOut.setEnabled(true);
        }
    }
}


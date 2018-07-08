package com.fanweilin.coordinatemap.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.fanweilin.coordinatemap.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.views.MapView;

/**
 * Created by Administrator on 2016/9/27 0027.
 */
public class OsZoomControlsView extends RelativeLayout implements View.OnClickListener {
    private MapView mapView;
    private IMapController mapController;
    private ImageButton zoomIn;
    private ImageButton zoomOut;
    private float maxZoomLevel;
    private float minZoomLevel;

    public OsZoomControlsView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();

    }


    public OsZoomControlsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.zoom_controls, null);
        zoomIn = view.findViewById(R.id.btn_zoom_in);
        zoomOut = view.findViewById(R.id.btn_zoom_out);
        zoomIn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);
        addView(view);
    }

    public void setMapView(MapView mapView) {
        if (mapView != null) {
            this.mapView = mapView;
            mapController = mapView.getController();
          /*  maxZoomLevel = mapView.getMaxZoomLevel();
            minZoomLevel = mapView.getMinZoomLevel();*/
        } else {
            throw new NullPointerException("you should call setMapView(MapView mapView) at first");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_zoom_in:
                mapController.zoomIn();
                break;
            case R.id.btn_zoom_out:
                mapController.zoomOut();
                break;
        }
        controlZoomShow();
    }

    private void controlZoomShow() {
    /*    float zoom = mapView.getZoomLevel();
        if (zoom >= maxZoomLevel) {
            zoomIn.setEnabled(false);
        } else {
            zoomIn.setEnabled(true);
        }
        if (zoom <= minZoomLevel) {
            zoomOut.setEnabled(false);
        } else {
            zoomOut.setEnabled(true);
        }*/
    }
}
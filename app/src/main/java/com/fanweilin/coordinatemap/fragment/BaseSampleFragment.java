package com.fanweilin.coordinatemap.fragment;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseSampleFragment extends Fragment {
    private static int MENU_LAST_ID =  Menu.FIRST; // Always set to last unused id
    public static final String TAG = "osmBaseFrag";
    public abstract String getSampleTitle();

    // ===========================================================
    // Fields
    // ===========================================================

   public MapView mapView;

    public MapView getmapView(){
        return mapView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapView = new MapView(inflater.getContext());
        if (Build.VERSION.SDK_INT >= 12) {
            mapView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
                /**
                 * mouse wheel zooming ftw
                 * http://stackoverflow.com/questions/11024809/how-can-my-view-respond-to-a-mousewheel
                 * @param v
                 * @param event
                 * @return
                 */
                @Override
                public boolean onGenericMotion(View v, MotionEvent event) {
                    if (0 != (event.getSource() & InputDevice.SOURCE_CLASS_POINTER)) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_SCROLL:
                                if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f)
                                    mapView.getController().zoomOut();
                                else {
                                    mapView.getController().zoomIn();
                                }
                                return true;
                        }
                    }
                    return false;
                }
            });
        }
        Log.d(TAG, "onCreateView");
        return mapView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
      /*  *//**//*addOverlays();*/

        if (mapView!=null) {
            final Context context = this.getActivity();
            final DisplayMetrics dm = context.getResources().getDisplayMetrics();

            CopyrightOverlay copyrightOverlay = new CopyrightOverlay(getActivity());

            //i hate this very much, but it seems as if certain versions of android and/or
            //device types handle screen offsets differently
            if (Build.VERSION.SDK_INT <= 10)
                copyrightOverlay.setOffset(0,(int)(55*dm.density));

            mapView.getOverlays().add(copyrightOverlay);
          /*  mapView.setBuiltInZoomControls(true);
            mapView.setMultiTouchControls(true);
            mapView.setTilesScaledToDpi(true);*/
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(TAG, "onDetach");
        if (mapView!=null)
            mapView.onDetach();
        mapView=null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");

    }


   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem add = menu.add("Run Tests");
        MENU_LAST_ID++;
        // Put overlay items first
        try {
            mapView.getOverlayManager().onCreateOptionsMenu(menu, MENU_LAST_ID, mapView);
        }catch (NullPointerException npe){
            //can happen during CI tests and very rapid fragment switching
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        try{
            mapView.getOverlayManager().onPrepareOptionsMenu(menu, MENU_LAST_ID, mapView);
        }catch (NullPointerException npe){
            //can happen during CI tests and very rapid fragment switching
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equals("Run Tests")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        runTestProcedures();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return true;
        }
        else if (mapView.getOverlayManager().onOptionsItemSelected(item, MENU_LAST_ID, mapView)) {
            return true;
        }

        return false;
    }

    *//**
     * An appropriate place to override and add overlays.
     *//*
    protected void addOverlays() {
        //
    }

    public boolean skipOnCiTests(){
        return false;
    }

    *//**
     * optional place to put automated test procedures, used during the connectCheck tests
     * this is called OFF of the UI thread. block this method call util the test is done
     *//*
    public void runTestProcedures() throws Exception{

    }*/
}
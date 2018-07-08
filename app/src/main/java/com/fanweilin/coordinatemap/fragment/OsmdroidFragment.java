package com.fanweilin.coordinatemap.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.share.LocationShareURLOption;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.fanweilin.coordinatemap.Activity.DataManagerActivity;
import com.fanweilin.coordinatemap.Activity.MainMapsActivity;
import com.fanweilin.coordinatemap.Activity.MapsActivity;
import com.fanweilin.coordinatemap.Activity.OlWayActivity;
import com.fanweilin.coordinatemap.Activity.WayponitActivity;
import com.fanweilin.coordinatemap.Activity.data;
import com.fanweilin.coordinatemap.Class.LatStyle;
import com.fanweilin.coordinatemap.Class.PointDataParcel;
import com.fanweilin.coordinatemap.Class.ShowPointStyle;
import com.fanweilin.coordinatemap.Class.SpfOlMap;
import com.fanweilin.coordinatemap.Class.StringToPoint;
import com.fanweilin.coordinatemap.MapSource.BingMapsTileSource;
import com.fanweilin.coordinatemap.MapSource.GaoDeMapsTileSource;
import com.fanweilin.coordinatemap.MapSource.GoogleMaps;
import com.fanweilin.coordinatemap.MapSource.GoogleMapsSatellite;
import com.fanweilin.coordinatemap.MapSource.GoogleMapsTileSource;
import com.fanweilin.coordinatemap.MapSource.GoolgeMapsTerrain;
import com.fanweilin.coordinatemap.MapSource.TianDiTuText;
import com.fanweilin.coordinatemap.MapSource.TianDituMapsTile;
import com.fanweilin.coordinatemap.Measure.Measure;
import com.fanweilin.coordinatemap.Measure.MeasureDistance;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.JZLocationConverter;
import com.fanweilin.coordinatemap.computing.Location3TheConvert;
import com.fanweilin.coordinatemap.widget.OsZoomControlsView;
import com.fanweilin.coordinatemap.widget.TextMarker;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.PointData;
import com.fanweilin.greendao.ShowData;
import com.fanweilin.greendao.ShowDataDao;
import com.fanweilin.greendao.SqlPolygon;
import com.fanweilin.greendao.SqlPolyline;
import com.fanweilin.greendao.Sqlpoint;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class OsmdroidFragment extends BaseSampleFragment implements View.OnClickListener, MapEventsReceiver,MapView.OnFirstLayoutListener,OnGetShareUrlResultListener {
    private DirectedLocationOverlay mLocationOverlay;
    private ImageButton imgLayerChange;
    private ImageButton imgClearall;
    private ImageButton imgMyloncation;
    private ImageButton imgDistance;
    private ImageButton imgAllMark;
    public OsZoomControlsView zoomControlsView;
    public String MARKBUNDLE = "markbundle";
    IMapController mapController;
    private View osmdroidview;
    private int mapstyle;
   // private RadiusMarkerClusterer poiMarkers;
    Drawable drawable;
    Drawable drawableRed;
    Drawable drawableGreen;
    Drawable drawableYL;
    Drawable drawableZs;
    Bitmap locnavi;
    Drawable circle;
    MainMapsActivity mainMapsActivity;
    OverlayManager overlayManager;
    //距离测量
    private ArrayList<GeoPoint> pts;
    private List<Marker> dotOptionses;
    private List<Polyline> polylineOptionses;
    private double totalDistance;
    private List<Double> listDistance;
    private View view;
    private boolean distance = false;
    DistanceInfoWindow myInfo;
    //
    public ArrayList<GeoPoint> marks;
    private static final int REQUEST = 1;
    private MapTileProviderBasic provider;
    TilesOverlay layer;
    private SharedPreferences spfmaps;
    private List<Long> ids=new ArrayList<Long>();
    //当前坐标系
    public int mapcoordinate;
    public OsmdroidFragment() {
        // Required empty public constructor
    }
    //社会化分享
    private ShareUrlSearch mShareUrlSearch = null;
    public String DATATYPE="datatype";
    SharedPreferences prefs;
    private ProgressDialog dialog;
    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        if (distance) {
            pts.add(p);
            DecimalFormat df = new DecimalFormat("0.00");
            if (pts.size() >= 2) {
                int n = pts.size();
                List<GeoPoint> linepoints = new ArrayList<GeoPoint>();
                linepoints.add(pts.get(n - 1));
                linepoints.add(pts.get(n - 2));
                LatLng latLng1 = new LatLng(pts.get(n - 1).getLatitude(), pts.get(n - 1).getLongitude());
                LatLng latLng2 = new LatLng(pts.get(n - 2).getLatitude(), pts.get(n - 2).getLongitude());
                double distance = DistanceUtil.getDistance(latLng1, latLng2);
                totalDistance += distance;
                listDistance.add(totalDistance);
//                OverlayOptions lineOption = new PolylineOptions().color(0xFF0000FF).points(pts).width(5);
                Polyline lineOption = new Polyline(mapView);
                lineOption.setColor(0xFF0000FF);
                lineOption.setPoints(pts);
                lineOption.setWidth(5);
                polylineOptionses.add(lineOption);
                mapView.getOverlays().add(lineOption);
            }
            String distance;
            if (totalDistance >= 1000) {
                distance = String.valueOf(df.format(totalDistance / 1000)) + "km";
            } else {
                distance = String.valueOf(df.format(totalDistance)) + "m";
            }
            if (pts.size() > 1) {
//                final Polygon dotOption = new DotOptions().center(point).color(0xFF0000FF).radius(10);
                Marker dotOption = new Marker(mapView, getActivity());
                dotOption.setIcon(circle);
                dotOption.setPosition(p);
                mapView.getOverlays().add(dotOption);
                dotOptionses.add(dotOption);
                myInfo.getTvDis().setText(distance);
                dotOption.setInfoWindow(myInfo);
                dotOption.showInfoWindow();
            } else {
                listDistance.add(0.0);
                Marker dotOption = new Marker(mapView, getActivity());
                dotOption.setIcon(circle);
                dotOption.setPosition(p);
                myInfo.getTvDis().setText("起点");
                dotOption.setInfoWindow(myInfo);
                dotOptionses.add(dotOption);
                dotOption.showInfoWindow();
                mapView.getOverlays().add(dotOption);
            }
        }
        mapView.invalidate();
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint geoPoint) {
        return false;
    }

    @Override
    public void onFirstLayout(View v, int left, int top, int right, int bottom) {

    }

    @Override
    public void onGetPoiDetailShareUrlResult(ShareUrlResult shareUrlResult) {

    }

    @Override
    public void onGetLocationShareUrlResult(ShareUrlResult shareUrlResult) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.putExtra(Intent.EXTRA_TEXT, "您的朋友通过经纬度定位与您分享一个位置: "
                + " -- " +shareUrlResult.getUrl());
        it.setType("text/plain");
        startActivity(Intent.createChooser(it, "将短串分享到"));
    }

    @Override
    public void onGetRouteShareUrlResult(ShareUrlResult shareUrlResult) {

    }


    public class MyMark extends Marker {
        private PointDataParcel pointDataParcel;

        public MyMark(MapView mapView, Context resourceProxy) {
            super(mapView, resourceProxy);
        }

        public PointDataParcel getPointDataParcel() {
            return pointDataParcel;
        }

        public void setPointDataParcel(PointDataParcel pointDataParcel) {
            this.pointDataParcel = pointDataParcel;
        }
    }

    class DistanceInfoWindow extends MarkerInfoWindow {
        private TextView tvDis;

        public DistanceInfoWindow() {
            super(R.layout.infoview, mapView);
            this.mView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            ImageButton btn = mView.findViewById(R.id.imgbtn_info_clean);
            tvDis = mView.findViewById(R.id.tv_info_distance);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int last = dotOptionses.size() - 1;
                    if (polylineOptionses.size() >= 1) {
                        Marker dotOptions = dotOptionses.get(last);
                        mapView.getOverlays().remove(dotOptions);
                        dotOptionses.remove(last);
                        int linelast = polylineOptionses.size() - 1;
                        Polyline polylineOptions = polylineOptionses.get(linelast);

                        String distance;
                        if (polylineOptionses.size() == 1) ;
                        DecimalFormat df = new DecimalFormat("0.00");
                        totalDistance = listDistance.get(last - 1);
                        if (totalDistance >= 1000) {
                            distance = String.valueOf(df.format(listDistance.get(last - 1) / 1000)) + "km";
                        } else {
                            distance = String.valueOf(df.format(listDistance.get(last - 1))) + "m";
                        }
                        if (polylineOptionses.size() == 1) {
                            distance = "起点";
                        }
                        tvDis.setText(distance);
                        mapView.getOverlays().remove(polylineOptions);
                        polylineOptionses.remove(linelast);
                        listDistance.remove(last);
                        dotOptionses.get(linelast).showInfoWindow();
                        mapView.invalidate();

                    } else {
                        Marker lastdotOptions = dotOptionses.get(0);
                        lastdotOptions.closeInfoWindow();
                        mapView.getOverlays().remove(lastdotOptions);
                        dotOptionses.clear();
                        polylineOptionses.clear();
                        listDistance.clear();
                        imgDistance.setImageResource(R.mipmap.ic_ruler);
                        distance = false;
                        mapView.invalidate();
                    }
                    pts.remove(last);
                }
            });
        }

        public TextView getTvDis() {
            return tvDis;
        }

        @Override
        public void onOpen(Object o) {

        }

        @Override
        public void onClose() {

        }

    }

    public void remove() {

    }

    @Override
    public String getSampleTitle() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmentf
        File dirs = new File(data.BASE_PATH, "title");
        //OpenStreetMapTileProviderConstants.setCachePath(dirs.getAbsolutePath());
        osmdroidview = inflater.inflate(R.layout.fragment_osmdroid, container, false);
        init();
        return osmdroidview;
    }

    public void init() {
        //poiMarkers = new RadiusMarkerClusterer(getActivity());
        dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mainMapsActivity = (MainMapsActivity) getActivity();
        mapView =  osmdroidview.findViewById(R.id.map);
        imgLayerChange =  osmdroidview.findViewById(R.id.imgbtn_layer);
        imgClearall =  osmdroidview.findViewById(R.id.imgbtn_clearall);
        imgMyloncation =  osmdroidview.findViewById(R.id.imgbtn_mylocation);
        imgDistance = osmdroidview.findViewById(R.id.imgbtn_distance);
        imgAllMark = osmdroidview.findViewById(R.id.imgbtn_allmark);
        zoomControlsView =  osmdroidview.findViewById(R.id.activity_main_zoomcontrols);
        //
        mapView.setUseDataConnection(true);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(false);
        mapView.setTilesScaledToDpi(true);
        final DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        spfmaps = getActivity().getSharedPreferences("spfmaps", Context.MODE_PRIVATE);
        mapController = mapView.getController();
        mapController.setZoom(16);
        mapstyle = spfmaps.getInt("mapstyle", 0);
        int mapmodel=spfmaps.getInt("mapmodel",0);
        mapcoordinate = spfmaps
                .getInt("coordiname", Location3TheConvert.Baidu);
        if(mapmodel==1){
            setNight(true);
        }
//play around with these values to get the location on screen in the right place for your applicatio

        mapcoordinate = spfmaps.getInt("coordiname", Location3TheConvert.Baidu);
        imgLayerChange.setOnClickListener(this);
        imgClearall.setOnClickListener(this);
        imgMyloncation.setOnClickListener(this);
        imgDistance.setOnClickListener(this);
        imgAllMark.setOnClickListener(this);
        drawable = getResources().getDrawable(R.mipmap.blu_blank_map_pin_48px_552642_easyicon);
        drawableRed = getResources().getDrawable(R.mipmap.red_blank_48px_553042_easyicon);
        drawableGreen = getResources().getDrawable(R.mipmap.gr_icon);
        drawableYL = getResources().getDrawable(R.mipmap.yl_icon);
        drawableZs = getResources().getDrawable(R.mipmap.zs_icon);
        locnavi = BitmapFactory.decodeResource(getResources(), R.mipmap.gps_navi);
        circle = getResources().getDrawable(R.drawable.circle);
        mLocationOverlay = new DirectedLocationOverlay(getActivity());
        mLocationOverlay.setDirectionArrow(locnavi);
        mapView.getOverlays().add(mLocationOverlay);
        zoomControlsView.setMapView(mapView);
        marks = new ArrayList<GeoPoint>();
        provider=new MapTileProviderBasic(getActivity(),new TianDiTuText());
        layer=new TilesOverlay(provider,getActivity());
        layer.setLoadingBackgroundColor(Color.TRANSPARENT);
        layer.setLoadingLineColor(Color.TRANSPARENT);
        //layer
        initmap();
        String latitude=spfmaps.getString("latitude","39.92");
        String longitude= spfmaps.getString("longitude","116.46");
        GeoPoint pointcenter=new GeoPoint(Double.parseDouble(latitude),Double.parseDouble(longitude));
        mapController.setCenter(pointcenter);
        //distance
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(), this);
        mapView.getOverlays().add(mapEventsOverlay);
        pts = new ArrayList<GeoPoint>();
        dotOptionses = new ArrayList<Marker>();
        polylineOptionses = new ArrayList<Polyline>();
        listDistance = new ArrayList<Double>();
         myInfo = new DistanceInfoWindow();
        if(mapcoordinate!=Location3TheConvert.Baidu){
         /*   showdata(mapcoordinate);*/
          /*  showAllMarks();*/
        }
        mShareUrlSearch = ShareUrlSearch.newInstance();
        mShareUrlSearch.setOnGetShareUrlResultListener(this);
    }
    public void setNight(Boolean show) {
        if(show)
        { mapView.getOverlayManager().getTilesOverlay().setColorFilter(TilesOverlay.INVERT_COLORS);
         mapView.getOverlayManager().getTilesOverlay().setLoadingBackgroundColor(android.R.color.black);
         mapView.getOverlayManager().getTilesOverlay().setLoadingLineColor(Color.argb(255, 0, 255, 0));}
        else {
            mapView.getOverlayManager().getTilesOverlay().setColorFilter(null);
            mapView.getOverlayManager().getTilesOverlay().setLoadingBackgroundColor(Color.rgb(216, 208, 208));
            mapView.getOverlayManager().getTilesOverlay().setLoadingLineColor( Color.rgb(200, 192, 192));
        }
        mapView.invalidate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgbtn_layer:
                Intent intent = new Intent();
                intent.setClass(getActivity(), MapsActivity.class);
                getActivity().startActivityForResult(intent, REQUEST);
                break;
            case R.id.imgbtn_clearall: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("确认清除标注吗?");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ids.clear();
                        mainMapsActivity.clearall();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();

            }
            break;
            case R.id.imgbtn_mylocation:

                if (mainMapsActivity.bdLng!=null){
                    JZLocationConverter.LatLng latLng = mainMapsActivity.getLatLng();
                    setMapcenter(new GeoPoint(latLng.getLatitude(), latLng.getLongitude()));
                }else {
                    Toast.makeText(getActivity(),"gps未开或没有网络",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.imgbtn_distance:
                if (distance) {
                    myInfo.close();
                    imgDistance.setImageResource(R.mipmap.ic_ruler);
                    for (Marker marker : dotOptionses) {
                        mapView.getOverlays().remove(marker);
                    }
                    for (Polyline polyline : polylineOptionses) {
                        mapView.getOverlays().remove(polyline);
                    }
                    pts.clear();
                    listDistance.clear();
                    dotOptionses.clear();
                    polylineOptionses.clear();
                    totalDistance = 0.0;
                    distance = false;
                } else {
                    imgDistance.setImageResource(R.mipmap.ic_close_black_36dp);
                    distance = true;
                    MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(), this);
                    mapView.getOverlays().add(mapEventsOverlay);
                }
                mapView.invalidate();
                break;
            case R.id.imgbtn_allmark:
                showAllMarks();
                break;

        }
    }

    public void showAllMarks() {
        if (marks.size() > 1) {
            try{
                BoundingBox bb = BoundingBox.fromGeoPoints(marks);
                mapView.zoomToBoundingBox(bb, true);
            }catch (IllegalArgumentException ee){
                Toast.makeText(getActivity(),"数据错误,纬度不能超过90,经度不能超过180,请删除错误数据",Toast.LENGTH_LONG).show();
            }

         ;
        }else if(marks.size()==1){
            GeoPoint point=marks.get(0);
            mapController.animateTo(point);
        }
    }

    public void clearall() {
        if (distance) {
            myInfo.close();
            imgDistance.setImageResource(R.mipmap.ic_ruler);
            pts.clear();
            listDistance.clear();
            dotOptionses.clear();
            polylineOptionses.clear();
            totalDistance = 0.0;
            distance = false;
        }
        mapView.getOverlays().clear();
        if(mLocationOverlay!=null){
            mLocationOverlay = new DirectedLocationOverlay(getActivity());
            mLocationOverlay.setDirectionArrow(locnavi);
        }
        mapView.getOverlays().add(mLocationOverlay);
        marks.clear();
        mapView.invalidate();
        if(mainMapsActivity.mapstyle==9){
            mapView.getOverlays().add(layer);
        }

    }

    public void showLocation(BDLocation txLocation, JZLocationConverter.LatLng latLng) {
        mLocationOverlay.setAccuracy((int) txLocation.getRadius());
        mLocationOverlay.setLocation(new GeoPoint(latLng.getLatitude(), latLng.getLongitude()));
        mLocationOverlay.setBearing(txLocation.getDirection());
        mapView.invalidate();

    }

    public void setlocaiton(Intent intent, int mapcoordinate) {
        String name;
        String test = DataManagerActivity.class.getName();
        name = intent.getStringExtra(MainMapsActivity.DATAMANAGERACTIVITY);
        long id=intent.getLongExtra("id",-1);
        int i=intent.getIntExtra(DATATYPE,1);
        if (name != null) {
            if (name.equals(test)) {
              switch (i){
                  case 1:
                      PointDataParcel pp = intent.getParcelableExtra(MainMapsActivity.GETPOINTDATAPARCE);
                      mapController.animateTo(convert(pp, mapcoordinate));
                      break;
                  case 2:
                      SqlPolyline polyline=data.findPolyLineByID(id);
                      List<Sqlpoint> sqlpoints=polyline.getPointpolyItems();
                      List<GeoPoint> geoPoints=new ArrayList<GeoPoint>();
                      for (int j=0;j<sqlpoints.size();j++){
                          GeoPoint point = null;
                          switch (mapcoordinate){
                              case Location3TheConvert.GCJ2:
                                  point=new GeoPoint(sqlpoints.get(j).getLatitude(),sqlpoints.get(j).getLongitude());
                                  break;
                              case Location3TheConvert.WGS84:
                                  JZLocationConverter.LatLng latLng = JZLocationConverter.gcj02ToWgs84(new JZLocationConverter.LatLng(sqlpoints.get(j).getLatitude(),sqlpoints.get(j).getLongitude()));
                                  point = new GeoPoint(latLng.getLatitude(), latLng.getLongitude());
                                  break;
                          }
                          geoPoints.add(point);
                      }
                      showLine(geoPoints,polyline);
                      break;
                  case 3:
                      SqlPolygon polygon=data.findPolyGonByID(id);
                      List<Sqlpoint> polygonpoints=polygon.getPointGonItems();
                      List<GeoPoint> points=new ArrayList<GeoPoint>();
                      for (int j=0;j<polygonpoints.size();j++){
                          GeoPoint point = null;
                          switch (mapcoordinate){
                              case Location3TheConvert.GCJ2:
                                  point=new GeoPoint(polygonpoints.get(j).getLatitude(),polygonpoints.get(j).getLongitude());
                                  break;
                              case Location3TheConvert.WGS84:
                                  JZLocationConverter.LatLng latLng = JZLocationConverter.gcj02ToWgs84(new JZLocationConverter.LatLng(polygonpoints.get(j).getLatitude(),polygonpoints.get(j).getLongitude()));
                                  point = new GeoPoint(latLng.getLatitude(), latLng.getLongitude());
                                  break;
                          }
                          points.add(point);
                      }
                      ids.add(id);
                      showPolygon(points);
                      break;
              }

            }
        }
        mapView.invalidate();
    }

    private GeoPoint convert(PointDataParcel pp, int mapcoordinate) {
        JZLocationConverter.LatLng wgs=null;
        if (pp!=null) {
            wgs = new JZLocationConverter.LatLng(Double.parseDouble(pp.getWgsLatitude()),
                    Double.parseDouble(pp.getWgsLongitude()));
        }

        GeoPoint point = null;
        switch (mapcoordinate) {
            case Location3TheConvert.GCJ2:
                JZLocationConverter.LatLng latLng = JZLocationConverter.wgs84ToGcj02(wgs);
                point = new GeoPoint(latLng.getLatitude(), latLng.getLongitude());
                break;
            case Location3TheConvert.WGS84:
                point = new GeoPoint(wgs.getLatitude(), wgs.getLongitude());
                break;
        }
        final MyMark myMark = new MyMark(mapView, getActivity());
        Boolean ishow=prefs.getBoolean("pre_isshow_name",false);
        PointData pointData=data.findPointDataDaoById(pp.getPointdataid());
        int REID= com.fanweilin.coordinatemap.Class.Marker.getResource(pointData);
        int MarkerID= com.fanweilin.coordinatemap.Class.Marker.getMarkerId(pointData);
        if(ishow){
            BitmapDescriptor bitmapDescriptor;
            TextMarker textMarker=new TextMarker(getContext());
            textMarker.setText(pp.getPointname());
            textMarker.setImge(REID);
            bitmapDescriptor= BitmapDescriptorFactory.fromView(textMarker);
            Drawable drawables=new BitmapDrawable(getActivity().getResources(),bitmapDescriptor.getBitmap());
            myMark.setIcon(drawables);
        }else {
            switch(MarkerID){
                case 1:
                    myMark.setIcon(drawable);
                    break;
                case 2:
                    myMark.setIcon(drawableRed);
                    break;
                case 3:
                    myMark.setIcon(drawableGreen);
                    break;
                case 4:
                    myMark.setIcon(drawableYL);
                    break;
                case 5:
                    myMark.setIcon(drawableZs);
                    break;
                default:
                    myMark.setIcon(drawable);
            }

        }

        myMark.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        myMark.setPosition(point);
        myMark.setPointDataParcel(pp);
        myMark.setTitle(pp.getPointname());
        myMark.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                MyMark myMark1 = (MyMark) marker;
                mapController.animateTo(myMark1.getPosition());
                showpopupwindows(myMark1);
                return false;
            }
        });
    /*poiMarkers.add(myMark);*/

        mapView.getOverlays().add(myMark);
        marks.add(point);
        return point;
    }

    public void addMark(GeoPoint point, PointDataParcel pp) {
        final MyMark myMark = new MyMark(mapView, getActivity());
        Boolean ishow=prefs.getBoolean("pre_isshow_name",false);
        if(ishow){
            BitmapDescriptor bitmapDescriptor;
            TextMarker textMarker=new TextMarker(getContext());
            textMarker.setText(pp.getPointname());
            bitmapDescriptor= BitmapDescriptorFactory.fromView(textMarker);
            Drawable drawables=new BitmapDrawable(getActivity().getResources(),bitmapDescriptor.getBitmap());
            myMark.setIcon(drawables);
        }else {
            myMark.setIcon(drawable);
        }
        myMark.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        myMark.setPosition(point);
        myMark.setPointDataParcel(pp);
        myMark.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                MyMark myMark1 = (MyMark) marker;
                mapController.animateTo(myMark1.getPosition());
                showpopupwindows(myMark1);
                return true;
            }
        });
        marks.add(point);
        mapView.getOverlays().add(myMark);
        mapController.animateTo(point);
    }

    public void showdata(final int mapcoordinate) {
        clearall();
        DaoSession mDaoSession = data.getmDaoSession();
        ShowDataDao showDataDao = mDaoSession.getShowDataDao();
        final List<ShowData> showDataList= showDataDao.loadAll();
                if (showDataList.size()>0) {
                    for(ShowData showData:showDataList) {
                        if(showData.getStyle()==null){
                            PointData pointData = data.findPointDataDaoById(showData.getPointid());
                            long filesID = showData.getFileid();
                            PointDataParcel pp = new PointDataParcel();
                            if (pointData != null) {
                                pp = mainMapsActivity.setPointdataParcel(filesID, pointData);
                                convert(pp, mapcoordinate);
                            }
                        }else {
                            if (ShowPointStyle.PONIT ==showData.getStyle()) {
                                PointData pointData = data.findPointDataDaoById(showData.getPointid());
                                long fileID=0;
                                if(showData.getFileid()!=null){
                                    fileID = showData.getFileid();
                                }
                                PointDataParcel pp = new PointDataParcel();
                                if (pointData != null) {
                                    pp = mainMapsActivity.setPointdataParcel(fileID, pointData);
                                    convert(pp, mapcoordinate);
                                }
                            } else if (ShowPointStyle.LINE == showData.getStyle()) {
                                SqlPolyline polyline = data.findPolyLineByID(showData.getPointid());
                                if (polyline != null) {
                                    List<GeoPoint> geoPointList=StringToPoint.getGeoPoints(polyline.getPoints());
                                    List<GeoPoint> geoPoints = new ArrayList<>();
                                    if (geoPointList==null)
                                        return;
                                    for (int j = 0; j <geoPointList.size(); j++) {
                                        GeoPoint point = null;
                                        switch (mapcoordinate) {
                                            case Location3TheConvert.GCJ2:
                                                point=geoPointList.get(j);
                                                break;
                                            case Location3TheConvert.WGS84:
                                                JZLocationConverter.LatLng latLng = JZLocationConverter.gcj02ToWgs84(new JZLocationConverter.LatLng(geoPointList.get(j).getLatitude(), geoPointList.get(j).getLongitude()));
                                                point = new GeoPoint(latLng.getLatitude(), latLng.getLongitude());
                                                break;
                                        }
                                        geoPoints.add(point);
                                    }
                                    showLine(geoPoints,polyline);
                                }
                            } else if (ShowPointStyle.POLGON == showData.getStyle()) {
                                SqlPolygon polygon = data.findPolyGonByID(showData.getPointid());
                                if (polygon != null) {
                                    List<Sqlpoint> sqlpoints = polygon.getPointGonItems();
                                    List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
                                    for (int j = 0; j < sqlpoints.size(); j++) {
                                        GeoPoint point = null;
                                        switch (mapcoordinate) {
                                            case Location3TheConvert.GCJ2:
                                                point = new GeoPoint(sqlpoints.get(j).getLatitude(), sqlpoints.get(j).getLongitude());
                                                break;
                                            case Location3TheConvert.WGS84:
                                                JZLocationConverter.LatLng latLng = JZLocationConverter.gcj02ToWgs84(new JZLocationConverter.LatLng(sqlpoints.get(j).getLatitude(), sqlpoints.get(j).getLongitude()));
                                                point = new GeoPoint(latLng.getLatitude(), latLng.getLongitude());
                                                break;
                                        }
                                        geoPoints.add(point);
                                    }
                                    ids.add(polygon.getId());
                                    showPolygon(geoPoints);
                                }

                            }
                        }

                    }
                }





    }

    public void showDistance(Intent intent, int coordinate) {
        double[] data = intent.getDoubleArrayExtra(MainMapsActivity.DISTANCELAT);
        LatLng bgwgs = new LatLng(data[0], data[1]);
        LatLng endwgs = new LatLng(data[2], data[3]);
        GeoPoint bg = null;
        GeoPoint end = null;
        switch (coordinate) {
            case Location3TheConvert.WGS84:
                bg = new GeoPoint(bgwgs.latitude, bgwgs.longitude);
                end = new GeoPoint(endwgs.latitude, endwgs.longitude);
                break;
            case Location3TheConvert.GCJ2:
                bg = Location3TheConvert.ConverToGCJ2(bgwgs.latitude, bgwgs.longitude, Location3TheConvert.WGS84);
                end = Location3TheConvert.ConverToGCJ2(endwgs.latitude, endwgs.longitude, Location3TheConvert.WGS84);
                break;
        }

        ArrayList<GeoPoint> linepoints = new ArrayList<GeoPoint>();
        linepoints.add(bg);
        linepoints.add(end);
        showLine(linepoints);
    }
    private void showLine(List<GeoPoint> points){
        Polyline polyline = new Polyline(mapView);
        polyline.setColor(0xFF0000FF);
        polyline.setPoints(points);
        polyline.setWidth(5);
        polyline.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                return false;
            }
        });
        mapView.getOverlays().add(polyline);
        try{
            BoundingBox boundingBox = BoundingBox.fromGeoPoints(points);
            mapView.zoomToBoundingBox(boundingBox, true);
        }catch (IllegalArgumentException ee){
            Toast.makeText(getActivity(),"数据错误,纬度不能超过90,经度不能超过180,请删除错误数据",Toast.LENGTH_LONG).show();
        }
    }
    private void showLine(List<GeoPoint> points, final SqlPolyline sqlPolyline){
        Polyline polyline = new Polyline(mapView);
        polyline.setColor(0xFF0000FF);
        polyline.setPoints(points);
        polyline.setWidth(5);
        polyline.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                MeasureDistance measure= Measure.setMeasuerParcel(sqlPolyline);
                popupPoly(measure);
                return false;
            }
        });
       try{
           BoundingBox boundingBox = BoundingBox.fromGeoPoints(points);
           mapView.getOverlays().add(polyline);
           mapView.zoomToBoundingBox(boundingBox, true);
       }catch (IllegalArgumentException ee){
           Toast.makeText(getActivity(),"数据错误,纬度不能超过90,经度不能超过180,请删除错误数据",Toast.LENGTH_LONG).show();
       }

    }
    private void popupPoly(MeasureDistance measureDistance){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        TextView name;
        TextView info;
        View view = inflater.inflate(R.layout.popup_poly, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        name= view.findViewById(R.id.tv_name);
        info= view.findViewById(R.id.tv_info);
        name.setText(measureDistance.getName());
        if (measureDistance.getType()==1){
            double distance=measureDistance.getDistance();
            DecimalFormat df = new DecimalFormat("0.000");
            String dis;
            if(distance<1000){
                dis=df.format(distance)+"米";
            }else {
                dis=df.format(distance/1000)+"千米";

            }
            String content="距离为"+dis;
            info.setText(content);
        }else {
            double farea=measureDistance.getArea();
            DecimalFormat df = new DecimalFormat("0.0000");
            DecimalFormat df1 = new DecimalFormat("0.0");
            String area=null;
            if(farea<1000000){
                area=df1.format(farea)+"平方米";
            }else {
                area=df.format(farea/1000000)+"平方千米";
            }
            String mu=df.format(farea/666.6666666);
            String content="面积为："+area+"("+mu+"亩)";
            info.setText(content);
        }
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
        popupWindow.showAsDropDown(mainMapsActivity.toolbar);
        popupWindow.setFocusable(true);
    }
   private void showPolygon(List<GeoPoint> points){
       Polygon polygon = new Polygon();    //see note below
       polygon.setStrokeColor(0xFF0000FF);
       polygon.setStrokeWidth(2);
       polygon.setFillColor(0x61aeea00);
       polygon.setPoints(points);
       polygon.setTitle("A sample polygon");
       mapView.getOverlays().add(polygon);
       try {
           BoundingBox boundingBox = BoundingBox.fromGeoPoints(points);
           mapView.zoomToBoundingBox(boundingBox, true);
       }catch (IllegalArgumentException ee){
           Toast.makeText(getActivity(),"数据错误,纬度不能超过90,经度不能超过180,请删除错误数据",Toast.LENGTH_LONG).show();
       }

   }
    public void showpopupwindows(final MyMark marker) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout ll;
        TextView address;
        TextView name;
        Button btn_detail;
        Button btn_navagation;
        Button btn_share;
        View view = inflater.inflate(R.layout.popupwindow, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        name =  view.findViewById(R.id.tv_name);
        address = view.findViewById(R.id.tv_lat);
        ll = view.findViewById(R.id.card_popup);
        btn_detail = view.findViewById(R.id.btn_main_detailed);
        btn_navagation = view.findViewById(R.id.btn_main_navigation);
        btn_share = view.findViewById(R.id.btn_main_share);
        final PointDataParcel pp = marker.getPointDataParcel();
        name.setText(pp.getPointname());
        if (TextUtils.isEmpty(pp.getAddress())) {
            PointData pointData = data.findPointDataDaoById(pp.getPointdataid());
            String text = "LAT:" + pointData.getWgslatitude() + " " + "LNG:" + pointData.getWgslongitude();
            address.setText(text);
        } else {
            address.setText(pp.getAddress());
        }
        ll.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK)
                    popupWindow.dismiss();
                return false;
            }
        });
        btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDescribe(pp);
            }
        });
        btn_navagation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapcoordinate = spfmaps
                        .getInt("coordiname", Location3TheConvert.Baidu);
                GeoPoint point=marker.getPosition();
                GeoPoint gcj=Location3TheConvert.ConverToGCJ2(point.getLatitude(),point.getLongitude(),mapcoordinate);
                LatLng baidu=new LatLng(gcj.getLatitude(),gcj.getLongitude());
                mainMapsActivity.startnavi(baidu);

            }
        });
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareText(pp);
            }
        });
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(getActivity().findViewById(R.id.main), Gravity.BOTTOM | Gravity.RELATIVE_LAYOUT_DIRECTION, 0, 0);
        popupWindow.setAnimationStyle(R.style.anim_popupwindow_center);
    }

    public void showDescribe(PointDataParcel pp) {
        pp.setActivity(WayponitActivity.DATAMANAGERACTIVITY);
        Intent intent = new Intent();
        if(data.findPointDataDaoById(pp.getPointdataid())!=null){
            PointDataParcel pointDataParcel=mainMapsActivity.setPointdataParcel(pp.getFileid(),data.findPointDataDaoById(pp.getPointdataid()));
            intent.putExtra(WayponitActivity.POINTDATA, pointDataParcel);
        }else {
            intent.putExtra(WayponitActivity.POINTDATA, pp);
        }
        if(data.findPointDataDaoById(pp.getPointdataid()).getFileId()!=null){
            intent.setClass(getActivity(), WayponitActivity.class);
        }else {
            intent.setClass(getActivity(), OlWayActivity.class);

        }

        startActivity(intent);
    }

    public void shareText(PointDataParcel pp) {

        LatLng baidu=null;
        if(pp.getGcjLatitude()==null){
            GeoPoint point=Location3TheConvert.ConverToGCJ2(Double.valueOf(pp.getWgsLatitude()),Double.valueOf(pp.getWgsLongitude()),Location3TheConvert.WGS84);
            baidu=new LatLng(point.getLatitude(),point.getLongitude());
        }else {
            baidu=new LatLng(Double.valueOf(pp.getGcjLatitude()),Double.valueOf(pp.getGcjLongitude()));
        }
        String name;
        String describe;
        if(pp.getPointname()==null){
            name="未命名";
        }else {
            name=pp.getPointname();
        }
        if(pp.getDescribe()==null){
            describe="无描述";
        }else {
            describe=pp.getDescribe();
        }
        mShareUrlSearch
                .requestLocationShareUrl(new LocationShareURLOption()
                        .location(baidu).snippet(describe)
                        .name(name));
    }


    public void setMapcenter(GeoPoint point) {
        mapController.setZoom(16);
        mapController.animateTo(point);
    }

    public void initmap() {
        mapstyle = spfmaps.getInt("mapstyle", Location3TheConvert.Baidu);
        switch (mapstyle) {
            case 2:
                mapView.setTileSource(new GoogleMaps());
                break;
            case 3:
                mapView.setTileSource(new GoolgeMapsTerrain());
                break;
            case 4:
                mapView.setTileSource(new GoogleMapsSatellite());
                break;
            case 5:
                mapView.setTileSource(new GoogleMapsTileSource());
                break;
            case 6:
                mapView.setTileSource(new BingMapsTileSource());
                break;
            case 7:
                mapView.setTileSource(new GaoDeMapsTileSource());
                break;
            case 8:
                mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
                break;
            case 9:
                mapView.setTileSource(new TianDituMapsTile());
                mapView.getOverlays().add(layer);
                break;
        }
        this.mapView.invalidate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    @Override
    public void onStop() {
        super.onStop();
        if(mapView!=null){
            IGeoPoint point=  mapView.getMapCenter();
            String latitude=String.valueOf(point.getLatitude());
            String longitude=String.valueOf(point.getLongitude());
            SharedPreferences.Editor editor=spfmaps.edit();
            editor.putString("latitude",latitude);
            editor.putString("longitude",longitude);
            editor.commit();
        }

    }
    @Override
    public void onDestroyView() {
        locnavi.recycle();
        layer.onDetach(mapView);
        layer=null;
        if(mapView!=null){
            mapView.onDetach();
            mapView=null;
        }
        super.onDestroyView();

    }
    private Drawable getViewBitmap(View addViewContent) {

        addViewContent.setDrawingCacheEnabled(true);
        addViewContent.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0,
                (addViewContent.getMeasuredWidth()),
                (addViewContent.getMeasuredHeight()));
        addViewContent.buildDrawingCache();

        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Drawable drawable=new BitmapDrawable(null,cacheBitmap);
        return drawable;
    }
}

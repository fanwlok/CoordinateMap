package com.fanweilin.coordinatemap.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.clusterutil.clustering.Cluster;
import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.share.LocationShareURLOption;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.fanweilin.coordinatemap.Activity.DataManagerActivity;
import com.fanweilin.coordinatemap.Activity.MainMapsActivity;
import com.fanweilin.coordinatemap.Activity.MapsActivity;
import com.fanweilin.coordinatemap.Activity.OlWayActivity;
import com.fanweilin.coordinatemap.Activity.WayponitActivity;
import com.fanweilin.coordinatemap.Activity.data;
import com.fanweilin.coordinatemap.Class.PointDataParcel;
import com.fanweilin.coordinatemap.Class.ShowPointStyle;
import com.fanweilin.coordinatemap.Class.StringToPoint;
import com.fanweilin.coordinatemap.Measure.Measure;
import com.fanweilin.coordinatemap.Measure.MeasureDistance;
import com.fanweilin.coordinatemap.Measure.MeasurePoint;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.DataItem;
import com.fanweilin.coordinatemap.computing.JZLocationConverter;
import com.fanweilin.coordinatemap.computing.Location3TheConvert;
import com.fanweilin.coordinatemap.widget.DragView;
import com.fanweilin.coordinatemap.widget.TextMarker;
import com.fanweilin.coordinatemap.widget.ZoomControlsView;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.PointData;
import com.fanweilin.greendao.ShowData;
import com.fanweilin.greendao.ShowDataDao;
import com.fanweilin.greendao.SqlPolygon;
import com.fanweilin.greendao.SqlPolyline;


import org.osmdroid.util.GeoPoint;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class BaiduMapFragment extends Fragment implements View.OnClickListener, BaiduMap.OnMapLongClickListener  ,OnGetShareUrlResultListener {
    public LocationClient mLocationClient = null;
    public String DATATYPE="datatype";
    public BaiduMap mBaiduMap;
    public MapView mMapView;
    private List<Long> ids=new ArrayList<>();
    public ZoomControlsView zoomControlsView;
    private BitmapDescriptor bitmap;
    private BitmapDescriptor bitmapRed;
    private BitmapDescriptor bitmapGreen;
    private BitmapDescriptor bitmapYl;
    private BitmapDescriptor bitmapZs;
    private ArrayList<BitmapDescriptor> bitmaplocation;
    private BitmapDescriptor bitmaplocation1;
    private BitmapDescriptor bitmaplocation2;
    public ClusterManager<MyItem> mClusterManager;
    private ImageButton imgLayerChange;
    private ImageButton imgClearall;
    private ImageButton imgMyloncation;
    private ImageButton imgAllMark;
    private static final String MARKBUNDLE = "markbundle";
    private Button btn_mapPoint;
    private Button btncancel;
    TextView tv_mappoint_adress;
    //当前baidu经纬度
    public double latitude;
    //地图点选
    TextView tvCenter;
    ImageView ivCenter;
    //距离测量
    private List<LatLng> pts;
    private List<Overlay> dotOptionses;

    private List<Overlay> polylineOptionses;
    private double totalDistance;
    private List<Double> listDistance;
    private View view;
    private TextView tvDistance;
    //0;1距离 2：面积
    LatLng bd;
    int diffx;
    int diffy;
    public DragView moveIcon;
    public int distance = 0;
    BitmapDescriptor Bigcircle;
    BitmapDescriptor Circle;
    BitmapDescriptor markselect;
    int top;
    //面积测量
    Projection projection;
    public MeasureDistance measureDistance;
    private Overlay polyGonOption;
    //

    private View myview;
    private static final int REQUEST = 1;
    //clusmark
    LatLngBounds mLatLngBounds;
    LatLngBounds.Builder builder;
    //社会化分享
    private ShareUrlSearch mShareUrlSearch = null;
    SharedPreferences prefs;
    private ProgressDialog dialog;
    public int mapcoordinate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myview = inflater.inflate(R.layout.fragment_baidu_map, container, false);
        initview();
        initData();
        return myview;
    }

    private void initview() {
        dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        mainMapsActivity = (MainMapsActivity) getActivity();
        builder = new LatLngBounds.Builder();
        ivCenter = myview.findViewById(R.id.iv_main_center);
        tvCenter = myview.findViewById(R.id.tv_main_center_lng);
        imgLayerChange = myview.findViewById(R.id.imgbtn_layer);
        imgClearall = myview.findViewById(R.id.imgbtn_clearall);
        imgMyloncation = myview.findViewById(R.id.imgbtn_mylocation);
        imgAllMark = myview.findViewById(R.id.imgbtn_allmark);
        btn_mapPoint = myview.findViewById(R.id.btn_main_pointsave);
        btncancel = myview.findViewById(R.id.btn_main_cancel);
        moveIcon= myview.findViewById(R.id.rl_marker);
        btncancel.setOnClickListener(this);
        tv_mappoint_adress = myview.findViewById(R.id.tv_main_mappointAdress);
        //声明LocationClient类
        mLocationClient = new LocationClient(getContext());
        //mapview初始化
        SharedPreferences spfmaps;
        spfmaps = getActivity().getSharedPreferences("spfmaps", Context.MODE_PRIVATE);
        mMapView = myview.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mMapView.showZoomControls(false);
        mMapView.setScaleControlPosition(new Point(0, 0));
        zoomControlsView = myview.findViewById(R.id.activity_main_zoomcontrols);
        zoomControlsView.setBaiduMap(mMapView);

        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(16f));
        bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.blu_blank_map_pin_48px_552642_easyicon);
        bitmapRed = BitmapDescriptorFactory
                .fromResource(R.mipmap.red_blank_48px_553042_easyicon);
        bitmapGreen = BitmapDescriptorFactory
                .fromResource(R.mipmap.gr_icon);
        bitmapYl = BitmapDescriptorFactory
                .fromResource(R.mipmap.yl_icon);
        bitmapZs = BitmapDescriptorFactory
                .fromResource(R.mipmap.zs_icon);
        mClusterManager = new ClusterManager<>(getContext(), mBaiduMap);
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(onClusterClickListener);
        mClusterManager.setOnClusterItemClickListener(onClusterItemClickListener);
        mBaiduMap.setOnMarkerClickListener(mClusterManager);
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        // mBaiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
        bitmaplocation1 = BitmapDescriptorFactory
                .fromResource(R.mipmap.red_blank_48px_553042_easyicon);
        bitmaplocation2 = BitmapDescriptorFactory
                .fromResource(R.mipmap.blu_blank_map_pin_48px_552642_easyicon);
        ImageView bigcircle=new ImageView(getActivity());
        bigcircle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bigcircle.setImageResource(R.drawable.bigcircle);
        Bigcircle =  BitmapDescriptorFactory
                .fromView(bigcircle);
        ImageView circle=new ImageView(getActivity());
        circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        circle.setImageResource(R.drawable.circle);
        Circle =  BitmapDescriptorFactory
                .fromView(circle);
        markselect =  BitmapDescriptorFactory
                .fromResource(R.mipmap.marker_selected);
        bitmaplocation = new ArrayList<>();
        bitmaplocation.add(bitmaplocation1);
        bitmaplocation.add(bitmaplocation2);
        mBaiduMap.setOnMapLongClickListener(this);
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(projection==null){
                    projection=mBaiduMap.getProjection();
                }
                PopupWindowmark(marker);
                return false;
            }
        });
        imgLayerChange.setOnClickListener(this);
        imgClearall.setOnClickListener(this);
        imgMyloncation.setOnClickListener(this);
        imgAllMark.setOnClickListener(this);
        btn_mapPoint.setOnClickListener(this);
        //distance init
        mBaiduMap.setOnMapClickListener(listener);
        mBaiduMap.setOnPolylineClickListener(new onPolyLineClick());
        measureDistance=new MeasureDistance();
        dotOptionses = new ArrayList<>();
        view = LayoutInflater.from(getContext()).inflate(R.layout.infoview, null);
        pts = new ArrayList<>();
        polylineOptionses = new ArrayList<>();
        listDistance = new ArrayList<>();
        tvDistance = view.findViewById(R.id.tv_info_distance);
        ImageButton btn = view.findViewById(R.id.imgbtn_info_clean);
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
                    tvDistance.setText(distance);
                    polylineOptions.remove();
                    polylineOptionses.remove(linelast);
                    listDistance.remove(last);
                    InfoWindow mInfoWindow = new InfoWindow(view, pts.get(linelast), -15);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                } else {
                    Overlay lastdotOptions = dotOptionses.get(0);
                    lastdotOptions.remove();
                    dotOptionses.clear();
                    polylineOptionses.clear();
                    listDistance.clear();
                    mBaiduMap.hideInfoWindow();
                    distance = 0;
                }
                pts.remove(last);
            }
        });
        //定位图层
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
      /*  mBaiduMap.setMyLocationConfigeration(config);*/
        mBaiduMap.setMyLocationConfiguration(config);
        //
        mapcoordinate = spfmaps.getInt("coordiname", Location3TheConvert.Baidu);
        if(mapcoordinate==Location3TheConvert.Baidu){
            showdata();
        }
    }

    private void initData(){
        mShareUrlSearch = ShareUrlSearch.newInstance();
        mShareUrlSearch.setOnGetShareUrlResultListener(this);   mShareUrlSearch = ShareUrlSearch.newInstance();
        mShareUrlSearch.setOnGetShareUrlResultListener(this);
           prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        moveIcon.setDragListener(new DragView.DragViewListener() {
            @Override
            public void onDoun(int RawX, int RawY) {
                MeasurePoint measurePoint=measureDistance.measurePoints.get(index);
                bd=new LatLng(measurePoint.getLat(),measurePoint.getLon());
                Point p=projection.toScreenLocation(bd);
                diffx=RawX-p.x;
                diffy=RawY-p.y;
            }

            @Override
            public void onDrag(int RawX, int RawY) {
                polyGonOption.remove();
                MeasurePoint measurePoint=measureDistance.measurePoints.get(index);
                bd= projection.fromScreenLocation(new Point(RawX-diffx,RawY-diffy));
                Double lat=bd.latitude;
                Double lon=bd.longitude;
                measurePoint.setLat(lat);
                measurePoint.setLon(lon);
                createArea(measureDistance.getBaiduPoints());
                     showArea();
            }

            @Override
            public void onUp(int RawX, int RawY) {
                primermarker.setPosition(bd);
            }
        });
    }
    private void showArea(){
        if(distance==1){
            double distance=measureDistance.getDistance();
            DecimalFormat df = new DecimalFormat("0.000");
            String dis;
            if(distance<1000){
                dis=df.format(distance)+"米";
            }else {
                dis=df.format(distance/1000)+"千米";

            }
            String content="距离为"+dis;
            mainMapsActivity.tvarea.setText(content);
        }else if(distance==2){
            double farea=measureDistance.getArea();
            DecimalFormat df = new DecimalFormat("0.0000");
            DecimalFormat df1 = new DecimalFormat("0.0");
            String area;
            if(farea<1000000){
                area=df1.format(farea)+"平方米";
            }else {
                area=df.format(farea/1000000)+"平方千米";
            }
            String mu=df.format(farea/666.6666666);
            String content="面积为："+area+"("+mu+"亩)";
            mainMapsActivity.tvarea.setText(content);
        }

    }
    private Boolean ismove=false;
    private int index;
    private Marker primermarker;
    private void ismove(Boolean move){
        if(move){
            mBaiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
            moveIcon.setVisibility(View.VISIBLE);
            ismove=move;
        }else {
            mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
            moveIcon.setVisibility(View.GONE);
            ismove=move;
             index=-1;
        }
    }
    private void PopupWindowmark(Marker marker) {
        Bundle bundle;
        bundle = marker.getExtraInfo();
        Rect frame=new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        top=frame.top+mainMapsActivity.toolbar.getHeight();
        if (bundle != null) {
            if(primermarker!=null){
                primermarker.setAlpha((float) 0.5);
            }
            ismove(true);
            index=bundle.getInt("index");
            primermarker=marker;
            marker.setAlpha(0);
            bd=marker.getPosition();
            final Point point=projection.toScreenLocation(bd);
            Observable<Integer> observable= Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                    emitter.onNext(top);
                }
            }).delay(5, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            observable.subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    moveIcon.setpositionByPx(point.x,point.y+integer);
                }

            });

        }

    }
    BaiduMap.OnMapStatusChangeListener onMapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {
            if(bd!=null){
                Point point=projection.toScreenLocation(bd);
                moveIcon.setpositionByPx(point.x,point.y+top);
            }

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {


        }
    };
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

    private class  onPolyLineClick implements BaiduMap.OnPolylineClickListener{

        @Override
        public boolean onPolylineClick(Polyline polyline) {
            if(distance<1){
                Bundle bundle=polyline.getExtraInfo();
                long id=bundle.getLong("id");
                SqlPolyline poly=data.findPolyLineByID(id);
                MeasureDistance measure= Measure.setMeasuerParcel(poly);
                popupPoly(measure);
            }
            return true;
        }
    }
    public void undoArea(){
        if(measureDistance.measurePoints.size()==0){
            if(polyGonOption!=null){
                polyGonOption.remove();
            }
            mainMapsActivity.showFabDo();
            distance=0;

        }
        measureDistance.back();
        if(distance==1){
            if(polyGonOption!=null){
                polyGonOption.remove();
            }
            List<LatLng> points=measureDistance.getBaiduPoints();
            createPoint(points);
            createArea(points);
            showArea();
        }else if(distance==2){
            List<LatLng> points=measureDistance.getBaiduPoints();
            createPoint(points);
            if(polyGonOption!=null){
                polyGonOption.remove();
            }
            if (points.size()==2){
                OverlayOptions lineOption = new PolylineOptions().color(0xFF0000FF).points(points).width(5);
                polyGonOption=mBaiduMap.addOverlay(lineOption);
                showArea();
            }
            if(points.size()>3){
                createArea(points);
                showArea();
            }
        }

    }
    BaiduMap.OnMapClickListener listener = new BaiduMap.OnMapClickListener() {
        /**
         * 地图单击事件回调函数
         *
         * @param point 点击的地理坐标
         */
        public void onMapClick(LatLng point) {
            if(ismove){
                ismove(false);
                if(primermarker!=null){
                    primermarker.setAlpha((float) 0.5);
                }
            }else {
             polylinegon(point.latitude,point.longitude);
            }

        }
            @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            if (ismove) {
                ismove(false);
                if(primermarker!=null){
                    primermarker.setAlpha((float) 0.5);
                }
            } else {
             polylinegon(mapPoi.getPosition().latitude,mapPoi.getPosition().longitude);
            }
                return true;
            }



        /**
         * 地图内 Poi 单击事件回调函数
         * @param poi 点击的 poi 信息
         */
    };
    public void polylinegon(double latitude,double longitude){
        if(distance>0){
            MeasurePoint measurePoint=new MeasurePoint();
            measurePoint.setLat(latitude);
            measurePoint.setLon(longitude);
            measureDistance.addPoint(measurePoint);
            List<LatLng> points=measureDistance.getBaiduPoints();
            createPoint(points);
            if(polyGonOption!=null){
                polyGonOption.remove();
            }
            if (points.size()==2){

                if(distance==1){
                    createArea(points);
                    showArea();
                }else {
                    OverlayOptions lineOption = new PolylineOptions().color(0xFF0000FF).points(points).width(5);
                    polyGonOption=mBaiduMap.addOverlay(lineOption);
                }

            }
            if(points.size()>=3){
                createArea(points);
                showArea();
            }

        }else {
            if(getAreaMear(latitude,longitude)!=null){
                popupPoly(getAreaMear(latitude,longitude));
            }
        }
    }

    private void createPoint (List<LatLng> points){


        for (int j=0;j<dotOptionses.size();j++){
            dotOptionses.get(j).remove();
        }
        dotOptionses.clear();
        for(int i=0;i<points.size();i++){
            Bundle bundle=new Bundle();
            bundle.putInt("index",i);
            if(distance==1){
                OverlayOptions dotOption = new MarkerOptions().icon(Bigcircle).position(points.get(i)).alpha((float) 0.7).anchor((float)0.5,(float)0.5).extraInfo(bundle);
                dotOptionses.add(mBaiduMap.addOverlay(dotOption));
            }else if(distance==2) {
                if(i%2==0&&i!=1){
                    OverlayOptions dotOption = new MarkerOptions().icon(Bigcircle).position(points.get(i)).alpha((float) 0.7).anchor((float)0.5,(float)0.5).extraInfo(bundle);
                    dotOptionses.add(mBaiduMap.addOverlay(dotOption));
                }else {
                    OverlayOptions dotOption = new MarkerOptions().icon(Circle).position(points.get(i)).alpha((float) 0.7).anchor((float)0.5,(float)0.5).extraInfo(bundle);
                    dotOptionses.add(mBaiduMap.addOverlay(dotOption));
                }
            }

        }
    }
    private void createArea(List<LatLng> points){
        if(distance==1){
            if(points.size()>1){
                OverlayOptions polineOption=new PolylineOptions().points(points).color(0xFF0000FF).width(5);
                polyGonOption=mBaiduMap.addOverlay(polineOption);
            }

        }else if(distance==2){
            OverlayOptions polygonOption = new PolygonOptions()
                    .points(points)
                    .stroke(new Stroke(2, 0xFF0000FF))
                    .fillColor(0x61aeea00);
            polyGonOption=mBaiduMap.addOverlay(polygonOption);
        }

    }
    public void showDistance(Intent intent) {
        LatLngBounds mLatLngBounds;
        double[] data = intent.getDoubleArrayExtra(MainMapsActivity.DISTANCELAT);
        LatLng bgwgs = new LatLng(data[0], data[1]);
        LatLng endwgs = new LatLng(data[2], data[3]);
       GeoPoint Gbgbd = Location3TheConvert.ConverToGCJ2(bgwgs.latitude, bgwgs.longitude, Location3TheConvert.WGS84);
       GeoPoint Gendbd = Location3TheConvert.ConverToGCJ2(endwgs.latitude, endwgs.longitude, Location3TheConvert.WGS84);
        List<LatLng> pts = new ArrayList<>();
        LatLng bgbd=new LatLng(Gbgbd.getLatitude(),Gbgbd.getLongitude());
        LatLng endbd=new LatLng(Gendbd.getLatitude(),Gendbd.getLongitude());
        pts.add(bgbd);
        pts.add(endbd);
        OverlayOptions polylineOptionsOption = new PolylineOptions()
                .points(pts).width(5).color(0xFF0000FF);
        mBaiduMap.addOverlay(polylineOptionsOption);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(bgbd);
        builder.include(endbd);
        mLatLngBounds = builder.build();
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(mLatLngBounds));
    }

    public void showpopupwindows(final MyItem item) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout ll;
        TextView address;
        TextView name;
        Button btn_detail;
        Button btn_navagation;
        Button btn_share;
        View view = inflater.inflate(R.layout.popupwindow, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        name = view.findViewById(R.id.tv_name);
        address = view.findViewById(R.id.tv_lat);
        ll = view.findViewById(R.id.card_popup);
        btn_detail = view.findViewById(R.id.btn_main_detailed);
        btn_navagation = view.findViewById(R.id.btn_main_navigation);
        btn_share = view.findViewById(R.id.btn_main_share);
        name.setText(item.getDataItem().getName());
        PointDataParcel pp = item.getDataItem().getPointDataParcel();
        PointData pointData = data.findPointDataDaoById(pp.getPointdataid());
        if (TextUtils.isEmpty(pointData.getAddress())) {
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
                showDescribe(item.dataItem.getPointDataParcel());
            }
        });
        btn_navagation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainMapsActivity.startnavi(item.getPosition());
            }
        });
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareText(item.dataItem.getPointDataParcel());
            }
        });
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
        popupWindow.showAtLocation(getActivity().findViewById(R.id.main), Gravity.BOTTOM | Gravity.RELATIVE_LAYOUT_DIRECTION, 0, 0);

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

       LatLng baidu;
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





    public void addMark(LatLng bdlat, PointDataParcel pp) {
        DataItem mDataItem = new DataItem();
        mDataItem.setName(pp.getPointname());
        mDataItem.setLatLng(bdlat);
        mDataItem.setPointDataParcel(pp);
        mClusterManager.addItem(new MyItem(bdlat, mDataItem));
        builder.include(bdlat);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
        setMapCenter(bdlat);

    }

    public ClusterManager.OnClusterItemClickListener<MyItem> onClusterItemClickListener = new ClusterManager.OnClusterItemClickListener<MyItem>() {
        @Override
        public boolean onClusterItemClick(MyItem item) {
            setMapCenter(item.getPosition());
            showpopupwindows(item);
            return true;
        }
    };
    public ClusterManager.OnClusterClickListener<MyItem> onClusterClickListener = new ClusterManager.OnClusterClickListener<MyItem>() {
        @Override
        public boolean onClusterClick(Cluster<MyItem> cluster) {
            LatLngBounds latLngBounds;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            List<MyItem> list;
            list = (List<MyItem>) cluster.getItems();
            for (int i = 0; i < list.size(); i++) {
                builder.include(list.get(i).getPosition());
            }
            latLngBounds = builder.build();
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(latLngBounds));
            return false;
        }
    };

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.imgbtn_layer:
                Intent intent = new Intent();
                intent.setClass(getActivity(), MapsActivity.class);
                getActivity().startActivityForResult(intent, REQUEST);
                break;
            case R.id.imgbtn_clearall: {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("确认清除标注吗?");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
                if(!mainMapsActivity.bdLocationClient.isStarted()){
                    mainMapsActivity.bdLocationClient.restart();
                }
                if(mainMapsActivity.gcjLng!=null){
                    setMapCenter(mainMapsActivity.gcjLng);
                }else {
                    Toast.makeText(getActivity(),"gps未开或没有网络",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.imgbtn_distance:
                if (1==distance) {
                    for (int i = 0; i < dotOptionses.size(); i++) {
                        Overlay dotovlay = dotOptionses.get(i);
                        dotovlay.remove();
                    }
                    for (int i = 0; i < polylineOptionses.size(); i++) {
                        Overlay polyOvlay = polylineOptionses.get(i);
                        polyOvlay.remove();
                    }
                    mBaiduMap.hideInfoWindow();
                    pts.clear();
                    listDistance.clear();
                    dotOptionses.clear();
                    polylineOptionses.clear();
                    totalDistance = 0.0;
                    distance = 0;
                } else {
                    distance = 1;
                }
                break;
            case R.id.imgbtn_allmark:
                showAllMarks();
        /*  case R.id.imgbtn_edit:
              distance=2;
              measureDistance.clear();
                break;*/
            case R.id.btn_main_pointsave:
//                TAG_SAVEPOINT = false;
//                tvCenter.setVisibility(View.GONE);
//                ivCenter.setVisibility(View.GONE);
//                DecimalFormat df = new DecimalFormat("0.0000000");
//                PointDataParcel pointData = new PointDataParcel();
//                pointData.setAddress(pointAdress);
//                pointData.setBaiduLatitude(String.valueOf(df.format(pointlat)));
//                pointData.setBaiduLongitude(String.valueOf(df.format(pointlng)));
//                JZLocationConverter.LatLng wgsLng = JZLocationConverter.bd09ToWgs84(new JZLocationConverter.LatLng(pointlat, pointlng));
//                pointData.setWgsLatitude(String.valueOf(df.format(wgsLng.getLatitude())));
//                pointData.setWgsLongitude(String.valueOf(df.format(wgsLng.getLongitude())));
//                pointData.setPointname(null);
//                pointData.setActivity(WayponitActivity.MAIACTIVTY);
//                Intent intent = new Intent();
//                intent.putExtra(WayponitActivity.POINTDATA, pointData);
//                intent.setClass(getActivity(), WayponitActivity.class);
//                startActivity(intent);
//                appxBannerContainer.setVisibility(View.VISIBLE);
//                mapPoint.setVisibility(View.GONE);
//                mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
                break;
            case R.id.btn_main_cancel:
//                TAG_SAVEPOINT = false;
//                tvCenter.setVisibility(View.GONE);
//                ivCenter.setVisibility(View.GONE);
//                appxBannerContainer.setVisibility(View.VISIBLE);
//                mapPoint.setVisibility(View.GONE);
//                mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
                break;
        }
    }
    public void areaOpen(){
        distance=2;
        measureDistance.setType(2);
        measureDistance.clear();
    }
   public void clearArea(){
       final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
       builder.setMessage("尚未保存，确定结束吗?");
       builder.setTitle("提示");
       builder.setPositiveButton("放弃", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
           clearmeare();
           }
       });
       builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {

           }
       });
       builder.create().show();

   }
   public void clearmeare(){
       measureDistance.clear();
       List<LatLng> points=measureDistance.getBaiduPoints();
       createPoint(points);
       if(polyGonOption!=null){
           polyGonOption.remove();
       }
       distance=0;
       moveIcon.setVisibility(View.GONE);
       mainMapsActivity.showFabDo();
   }
    public void showAllMarks() {
        mLatLngBounds = builder.build();
        if (mLatLngBounds.getCenter().latitude>0.001&&mLatLngBounds.getCenter().longitude>0.001){
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(mLatLngBounds));
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomOut());
        }

    }

    public void setMapCenter(LatLng latLng) {
        if(latLng!=null){
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.animateMapStatus(mMapStatusUpdate);
        }

    }

    public MainMapsActivity mainMapsActivity;

    @Override
    public void onMapLongClick(LatLng latLng) {
      // setMapCenter(mainMapsActivity.bdLng);
    }

    public void distanceOpen() {
        distance=1;
        measureDistance.setType(1);
    }


    public class MyItem implements ClusterItem {
        private final LatLng mPosition;
        private DataItem dataItem;
        public MyItem(LatLng latLng, DataItem mDataItem) {
            mPosition = latLng;
            dataItem = mDataItem;

        }

        @Override
        public DataItem getDataItem() {
            return dataItem;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            Boolean ishow=prefs.getBoolean("pre_isshow_name",false);
            PointData pointData=data.findPointDataDaoById(dataItem.getPointDataParcel().getPointdataid());
            int REID= com.fanweilin.coordinatemap.Class.Marker.getResource(pointData);
            int MarkerID= com.fanweilin.coordinatemap.Class.Marker.getMarkerId(pointData);
            if(ishow){
                BitmapDescriptor bitmapDescriptor;
                TextMarker textMarker=new TextMarker(getContext());
                textMarker.setText(dataItem.getName());
                textMarker.setImge(REID);
                bitmapDescriptor=BitmapDescriptorFactory.fromView(textMarker);
                return bitmapDescriptor;
            }else {
                switch(MarkerID){
                    case 1:
                        return bitmap;
                    case 2:
                        return bitmapRed;
                    case 3:
                        return bitmapGreen;
                    case 4:
                        return bitmapYl;
                    case 5:
                        return bitmapZs;
                        default:
                            return bitmap;
                }

            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    //datamanager 单点显示
    public void setlocation(Intent intent) {
        String name;
        String test = DataManagerActivity.class.getName();
        name = intent.getStringExtra(MainMapsActivity.DATAMANAGERACTIVITY);
        int i=intent.getIntExtra(DATATYPE,1);
        long id=intent.getLongExtra("id",0);
        if (name != null) {
            if (name.equals(test)) {
                if (i==1){
                    JZLocationConverter.LatLng wgs;
                    PointDataParcel pp = intent.getParcelableExtra(MainMapsActivity.GETPOINTDATAPARCE);
                    LatLng baiduLng;
                    if (pp.getGcjLatitude()!=null) {
                      baiduLng=new LatLng(Double.parseDouble(pp.getGcjLatitude()),
                              Double.parseDouble(pp.getGcjLongitude()));
                    } else {
                        wgs = new JZLocationConverter.LatLng(Double.parseDouble(pp.getWgsLatitude()),
                                Double.parseDouble(pp.getWgsLongitude()));
                        JZLocationConverter.LatLng latLng = JZLocationConverter.wgs84ToGcj02(wgs);
                        baiduLng=new LatLng(latLng.getLatitude(),latLng.getLongitude());
                    }
                    DataItem mDataItem = new DataItem();
                    mDataItem.setName(pp.getPointname());
                    mDataItem.setLatLng(baiduLng);
                    mDataItem.setPointDataParcel(pp);
                    mClusterManager.addItem(new MyItem(baiduLng, mDataItem));
                    builder.include(baiduLng);
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                    setMapCenter(baiduLng);
                }
              else if(i==2){
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    SqlPolyline polyline=data.findPolyLineByID(id);
                    List<LatLng> bdlatLng=StringToPoint.getBdPoints(polyline.getPoints());
                    for (int j=0;j<bdlatLng.size();j++){
                        builder.include(bdlatLng.get(j));
                    }
                    Bundle bundle=new Bundle();
                    bundle.putLong("id",polyline.getId());
                    OverlayOptions polylineOptionsOption = new PolylineOptions()
                            .points( bdlatLng).width(5).color(0xFF0000FF).extraInfo(bundle);
                    mBaiduMap.addOverlay(polylineOptionsOption);
                    mLatLngBounds = builder.build();
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(mLatLngBounds));
                }else if(i==3){
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    SqlPolygon polygon=data.findPolyGonByID(id);
                    List<LatLng> bdlatLng=StringToPoint.getBdPoints(polygon.getPoints());
                    for (int j=0;j<bdlatLng.size();j++){
                        builder.include(bdlatLng.get(j));
                    }
                    ids.add(id);
                    OverlayOptions polyGonOptionsOption = new PolygonOptions()
                            .points( bdlatLng)   .stroke(new Stroke(2, 0xFF0000FF))
                            .fillColor(0x61aeea00);
                    mBaiduMap.addOverlay(polyGonOptionsOption);
                    mLatLngBounds = builder.build();
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(mLatLngBounds));
                }
            }
        }
    }

    public void showdata() {
        dialog.show();
        mBaiduMap.clear();
        mClusterManager.clearItems();
        builder = null;
        builder = new LatLngBounds.Builder();
        DaoSession mDaoSession = data.getmDaoSession();
        final ShowDataDao showDataDao = mDaoSession.getShowDataDao();
        final List<MyItem> items = new ArrayList<>();
        final List<OverlayOptions> overlayOptions=new ArrayList<>();
        final List<ShowData> showDataList= showDataDao.queryBuilder().list();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                if (showDataList.size()>0) {
                    for(ShowData showData:showDataList) {
                        if(showData.getStyle()==null){
                            DataItem mDataItem = new DataItem();
                            String name =showData.getTitle();
                            String  bdlat=showData.getBaidulatitude();
                            LatLng point;
                            if(bdlat!=null){
                                double lat = Double.parseDouble(showData.getBaidulatitude());
                                double lon = Double.parseDouble(showData.getBaidulongitude());
                                point = new LatLng(lat, lon);
                            }else {
                                double lat = Double.parseDouble(showData.getWgslatitude());
                                double lon = Double.parseDouble(showData.getWgslongitude());
                                GeoPoint P= Location3TheConvert.ConverToGCJ2(lat,lon,Location3TheConvert.WGS84);
                                point=new LatLng(P.getLatitude(),P.getLongitude());
                            }

                            PointData pointData = data.findPointDataDaoById(showData.getPointid());
                            long fileID=0;
                            if(showData.getFileid()!=null){
                                fileID = showData.getFileid();
                            }
                            PointDataParcel pp;
                            if (pointData != null) {
                                pp = mainMapsActivity.setPointdataParcel(fileID, pointData);
                                mDataItem.setName(name);
                                mDataItem.setLatLng(point);
                                mDataItem.setPointDataParcel(pp);
                                items.add(new MyItem(point, mDataItem));
                                builder.include(point);
                            }
                        }else {
                            if (ShowPointStyle.PONIT ==showData.getStyle()) {
                                DataItem mDataItem = new DataItem();
                                String name =showData.getTitle();
                                String  bdlat=showData.getBaidulatitude();
                                LatLng point ;
                                if(bdlat!=null){
                                    double lat = Double.parseDouble(showData.getBaidulatitude());
                                    double lon = Double.parseDouble(showData.getBaidulongitude());
                                    point = new LatLng(lat, lon);
                                }else {
                                    double lat = Double.parseDouble(showData.getWgslatitude());
                                    double lon = Double.parseDouble(showData.getWgslongitude());
                                    GeoPoint P= Location3TheConvert.ConverToGCJ2(lat,lon,Location3TheConvert.WGS84);
                                    point=new LatLng(P.getLatitude(),P.getLongitude());
                                }
                                PointData pointData = data.findPointDataDaoById(showData.getPointid());
                                PointDataParcel pp ;
                                if (pointData != null) {
                                    long fileID;
                                    if(pointData.getFileId()!=null){
                                        fileID=pointData.getFileId();
                                    }else {
                                        fileID=pointData.getOlfileId();
                                    }
                                    pp = mainMapsActivity.setPointdataParcel(fileID, pointData);
                                    mDataItem.setName(name);
                                    mDataItem.setLatLng(point);
                                    mDataItem.setPointDataParcel(pp);
                                    items.add(new MyItem(point, mDataItem));
                                    builder.include(point);
                                }
                            } else if (ShowPointStyle.LINE == showData.getStyle()) {
                                SqlPolyline polyline = data.findPolyLineByID(showData.getPointid());
                                if (polyline != null) {
                                    String points=polyline.getPoints();
                                    List<LatLng> bdlatLng= StringToPoint.getBdPoints(points);
                                    for(LatLng latLng:bdlatLng){
                                        builder.include(latLng);
                                    }
                                    if(bdlatLng.size()>=2){
                                        Bundle bundle=new Bundle();
                                        bundle.putLong("id",polyline.getId());
                                        OverlayOptions polylineOptionsOption = new PolylineOptions()
                                                .points( bdlatLng).width(5).color(0xFF0000FF).extraInfo(bundle);
                                        overlayOptions.add(polylineOptionsOption);

                                    }


                                }
                            }else if (ShowPointStyle.POLGON == showData.getStyle()) {
                                SqlPolygon polygon= data.findPolyGonByID(showData.getPointid());
                                if (polygon != null) {
                                    List<LatLng> bdlatLng=StringToPoint.getBdPoints(polygon.getPoints());
                                    for (int j=0;j<bdlatLng.size();j++){
                                        builder.include(bdlatLng.get(j));
                                    }
                                    OverlayOptions polyGonOptionsOption = new PolygonOptions()
                                            .points( bdlatLng).stroke(new Stroke(2, 0xFF0000FF))
                                            .fillColor(0x61aeea00);
                                    ids.add(polygon.getId());
                     /*   mBaiduMap.addOverlay(polyGonOptionsOption );*/
                                    overlayOptions.add(polyGonOptionsOption);
                                }

                            }
                        }

                    }

                }
                emitter.onNext("su");
            }

        }).subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if(s.equals("su")){
                            mBaiduMap.addOverlays(overlayOptions);
                            mLatLngBounds = builder.build();
                            mClusterManager.addItems(items);
                            if(showDataList.size()>0){
                                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(mLatLngBounds));
                            }
                        }
                        dialog.dismiss();
                    }

                });



        }
    private MeasureDistance getAreaMear(Double latitude,Double lontitude){
        LatLng latLng=new LatLng(latitude,lontitude);
                  for (int i=0;i<ids.size();i++){
                      SqlPolygon polygon= data.findPolyGonByID(ids.get(i));
                      if(polygon!=null){
                          List<LatLng> latLngs=Measure.setMeasuerParcel(polygon).getBaiduPoints();
                          if(SpatialRelationUtil.isPolygonContainsPoint(latLngs,latLng)){
                              return Measure.setMeasuerParcel(polygon);
                          }
                      }
                  }
                  return null;
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
        String area;
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

    public void clearall() {
        mBaiduMap.clear();
        mClusterManager.clearItems();
        ids.clear();
        builder = null;
        builder = new LatLngBounds.Builder();
    }

    public void showLocation(BDLocation location, LatLng bdlat) {
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .direction(location.getDirection()).latitude(bdlat.latitude)
                .longitude(bdlat.longitude).build();
        mBaiduMap.setMyLocationData(locData);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        mBaiduMap.setMyLocationEnabled(false);
        bitmaplocation1.recycle();
        bitmaplocation2.recycle();
        bitmap.recycle();
        bitmapYl.recycle();
        bitmapZs.recycle();
        bitmapGreen.recycle();
        bitmapRed.recycle();
        Circle.recycle();
        Bigcircle.recycle();
        mMapView.onDestroy();//销毁地图
        mMapView = null;
        super.onDestroy();
    }
}

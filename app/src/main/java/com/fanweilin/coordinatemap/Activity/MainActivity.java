package com.fanweilin.coordinatemap.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.clusterutil.clustering.Cluster;
import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.DotOptions;
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
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.AppActivity;
import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.fanweilin.coordinatemap.Class.LatStyle;
import com.fanweilin.coordinatemap.Class.PointDataParcel;
import com.fanweilin.coordinatemap.EventBus.ServiceEvents;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.ConvertLatlng;
import com.fanweilin.coordinatemap.computing.DataItem;
import com.fanweilin.coordinatemap.computing.JZLocationConverter;
import com.fanweilin.coordinatemap.widget.ZoomControlsView;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.PointData;
import com.fanweilin.greendao.ShowData;
import com.fanweilin.greendao.ShowDataDao;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BaiduMap.OnMapLoadedCallback {
    public Toolbar toolbar;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle mDrawerToggle;
    public LocationClient mLocationClient = null;
    public BaiduMap mBaiduMap;
    public MapView mMapView;
    public double latitude;
    public double lontitude;
    public static String GETPOINTDATAPARCE = "getpointdataparcel";
    public static String DATAMANAGERACTIVITY = "datamanageractivity";
    public ZoomControlsView zoomControlsView;
    private static CoordinateConverter converter;
    private BitmapDescriptor bitmap;
    private ArrayList<BitmapDescriptor> bitmaplocation;
    private BitmapDescriptor bitmaplocation1;
    private BitmapDescriptor bitmaplocation2;
    private ClusterManager<MyItem> mClusterManager;
    private ImageButton imgLayerChange;
    private ImageButton imgClearall;
    private ImageButton imgMyloncation;
    private ImageButton imgDistance;
    private boolean MAP_STYLE = true;
    private int coordstyle;
    private int datastyle;
    private NavigationView mNavigationView;
    private FloatingActionButton fabLocation;
    private View popupWindow;
    private BDLocation bdLocationForpw;
    private String address = "";
    private String altitude = "";
    private RelativeLayout appxBannerContainer;
    private RelativeLayout mapPoint;
    private ProgressDialog dialog;
    private static final String MARKBUNDLE = "markbundle";
    AdView adView;
    private Marker previous;
    private boolean TAG_SAVEPOINT = false;
    private Button btn_mapPoint;
    private String pointAdress;
    Double pointlat;
    Double pointlng;
    TextView tv_mappoint_adress;
    //distance
    private List<LatLng> pts;
    private List<Overlay> dotOptionses;
    private List<Overlay> polylineOptionses;
    private double totalDistance;
    private InfoWindow preInfo;
    private List<Double> listDistance;
    private View view;
    private TextView tvDistance;
    private boolean distance = false;
    PopupWindow popupWindowDismiss;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        setlocation(getIntent());
        showdata(getIntent());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        init();
        toolbar.setLogo(R.mipmap.earth_globe_48px);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);
        startService();
        showdata();
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {

                            case R.id.nav_computer:
                                computer();
                                break;
                            case R.id.nav_down:
                                download();
                                break;
                            case R.id.nav_about:
                                about();
                                break;
                            case R.id.nav_quit:
                                alipay();
                                break;
                            case R.id.nav_update:
                                dialog.show();
                                BDAutoUpdateSDK.uiUpdateAction(MainActivity.this, new MyUICheckUpdateCallback());
                                break;
                        }
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
    public void alipay(){
        Intent intent=new Intent();
        intent.setClass(MainActivity.this,AlipayActivity.class);
        startActivity(intent);
    }
    private class MyUICheckUpdateCallback implements UICheckUpdateCallback {

        @Override
        public void onCheckComplete() {
            dialog.dismiss();
        }

    }


    private void download() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, Bdoffilin_activity.class);
        startActivity(intent);
    }

    private void computer() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, ConvertActivity.class);
        startActivity(intent);
    }

    private void about() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("本软件主要功能:\n一:通过经纬度值查询在地图上位置,可以添加数据到文件,也可导入数据到文件进行批量查询.\n" +
                "二:查看当前位置经纬度,并记录下来." + "\n有bug和好的建议可发送邮件至897481601@qq.com");
        builder.setTitle("关于");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
//        BDAutoUpdateSDK.uiUpdateAction(this, new MyUICheckUpdateCallback());
    }
//    private class MyUICheckUpdateCallback implements UICheckUpdateCallback {
//
//        @Override
//        public void onCheckComplete() {
//                       Log.d("DSAF","UI");
//        }
//
//    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.imgbtn_layer:
                if (MAP_STYLE) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    MAP_STYLE = false;
                } else {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    MAP_STYLE = true;
                }
                break;
            case R.id.imgbtn_clearall: {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("确认清除标注吗?");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DaoSession mDaoSession = data.getmDaoSession();
                        ShowDataDao showDataDao = mDaoSession.getShowDataDao();
                        mBaiduMap.clear();
                        mClusterManager.clearItems();
                        showDataDao.deleteAll();
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
                setMapCenter(latitude, lontitude);
                break;
            case R.id.fab_location:
                popupWindow = showpopupwindow();
                break;
            case R.id.imgbtn_distance:
                if (distance) {
                    imgDistance.setImageResource(R.mipmap.ic_ruler);
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
                    distance = false;
                } else {
                    imgDistance.setImageResource(R.mipmap.ic_close_black_36dp);
                    distance = true;
                }
                break;
            case R.id.btn_main_pointsave:
                TAG_SAVEPOINT = false;
                previous.remove();
                mBaiduMap.hideInfoWindow();
                DecimalFormat df = new DecimalFormat("#.0000000");
                PointDataParcel pointData = new PointDataParcel();
                pointData.setAddress(pointAdress);
                pointData.setBaiduLatitude(String.valueOf(df.format(pointlat)));
                pointData.setBaiduLongitude(String.valueOf(df.format(pointlng)));
                JZLocationConverter.LatLng wgsLng=JZLocationConverter.bd09ToWgs84(new JZLocationConverter.LatLng(pointlat,pointlng));
                pointData.setWgsLatitude(String.valueOf(df.format(wgsLng.getLatitude())));
                pointData.setWgsLongitude(String.valueOf(df.format(wgsLng.getLongitude())));
                pointData.setPointname(null);
                pointData.setActivity(WayponitActivity.MAIACTIVTY);
                Intent intent = new Intent();
                intent.putExtra(WayponitActivity.POINTDATA, pointData);
                intent.setClass(MainActivity.this, WayponitActivity.class);
                startActivity(intent);
                appxBannerContainer.setVisibility(View.VISIBLE);
                mapPoint.setVisibility(View.GONE);
                mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
                break;
        }
    }

    @Override
    public void onMapLoaded() {

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
            return bitmap;
        }
    }

    private void showdata(Intent intent) {
        String data = intent.getStringExtra("data");
        if (data != null && data.equals("more")) {
            showdata();
        }
    }

    private void showdata() {
        mBaiduMap.clear();
        mClusterManager.clearItems();
        LatLngBounds mLatLngBounds;
        Cursor cursor;
        SQLiteDatabase db;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        DaoSession mDaoSession = data.getmDaoSession();
        ShowDataDao showDataDao = mDaoSession.getShowDataDao();
        db = data.getDb();
        cursor = db.query(showDataDao.getTablename(), showDataDao.getAllColumns(), null, null, null, null, null);
        List<MyItem> items = new ArrayList<>();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                DataItem mDataItem = new DataItem();
                int CoordStyle = cursor.getInt(cursor.getColumnIndex(ShowDataDao.Properties.Cdstyle.columnName));
                int DataStyle = cursor.getInt(cursor.getColumnIndex(ShowDataDao.Properties.Datastyle.columnName));
                String name = cursor.getString(cursor.getColumnIndex(ShowDataDao.Properties.Title.columnName));
                double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(ShowDataDao.Properties.Latitude.columnName)));
                double lon = Double.parseDouble(cursor.getString(cursor.getColumnIndex(ShowDataDao.Properties.Longitude.columnName)));
                PointData pointData = data.findPointDataDaoById(cursor.getLong(cursor.getColumnIndex(ShowDataDao.Properties.Pointid.columnName)));
                Files files = data.findOrderById(cursor.getLong(cursor.getColumnIndex(ShowDataDao.Properties.Fileid.columnName)));
                LatLng point = new LatLng(lat, lon);
                LatLng bdpoint = ComanLngConvertBdLngt(point, CoordStyle, DataStyle);
                PointDataParcel pp = new PointDataParcel();
                if (pointData != null) {
                    pp = setPointdataParcel(files, pointData);
                    mDataItem.setName(name);
                    mDataItem.setLatLng(point);
                    mDataItem.setPointDataParcel(pp);
                    items.add(new MyItem(bdpoint, mDataItem));
                    builder.include(bdpoint);
                    Log.d("name",name);
                }
            }
            mLatLngBounds = builder.build();
            mClusterManager.addItems(items);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(mLatLngBounds));
        }
        cursor.close();
    }

    private boolean setlocation(Intent intent) {
        String name;
        String test = DataManagerActivity.class.getName();
        name = intent.getStringExtra(DATAMANAGERACTIVITY);
        if (name != null) {
            if (name.equals(test)) {
                PointDataParcel pp = intent.getParcelableExtra(GETPOINTDATAPARCE);
                LatLng baiduLng = new LatLng(Double.parseDouble(pp.getBaiduLatitude()), Double.parseDouble(pp.getBaiduLongitude()));
                Bundle bundle = new Bundle();
                bundle.putParcelable(MARKBUNDLE, pp);
                OverlayOptions options2 = new MarkerOptions().position(baiduLng).icons(bitmaplocation).extraInfo(bundle).period(25);
                mBaiduMap.addOverlay(options2);
                setMapCenter(baiduLng.latitude, baiduLng.longitude);
                return true;

            }
        }
        return false;
    }

    private void init() {
        converter = new CoordinateConverter();
        fabLocation = (FloatingActionButton) findViewById(R.id.fab_location);
        imgLayerChange = (ImageButton) findViewById(R.id.imgbtn_layer);
        imgClearall = (ImageButton) findViewById(R.id.imgbtn_clearall);
        imgMyloncation = (ImageButton) findViewById(R.id.imgbtn_mylocation);
        imgDistance = (ImageButton) findViewById(R.id.imgbtn_distance);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mapPoint = (RelativeLayout) findViewById(R.id.rl_main_mappoint);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        btn_mapPoint = (Button) findViewById(R.id.btn_main_pointsave);
        appxBannerContainer = (RelativeLayout) findViewById(R.id.appx_banner_container);
        tv_mappoint_adress= (TextView) findViewById(R.id.tv_main_mappointAdress);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);
        mMapView.setScaleControlPosition(new Point(0, 0));
        zoomControlsView = (ZoomControlsView) findViewById(R.id.activity_main_zoomcontrols);
        mBaiduMap = mMapView.getMap();
        zoomControlsView.setBaiduMap(mMapView);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(16f));
        bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.blu_blank_map_pin_48px_552642_easyicon);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        setupDrawerContent(mNavigationView);
        mClusterManager = new ClusterManager<>(this, mBaiduMap);
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
        bitmaplocation = new ArrayList<BitmapDescriptor>();
        bitmaplocation.add(bitmaplocation1);
        bitmaplocation.add(bitmaplocation2);

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                PopupWindowmark(marker);
                return false;
            }
        });
        startCount();
       // createAD();
       // isshowBananer();
        RegisterEventBus();
        imgLayerChange.setOnClickListener(this);
        imgClearall.setOnClickListener(this);
        imgMyloncation.setOnClickListener(this);
        fabLocation.setOnClickListener(this);
        imgDistance.setOnClickListener(this);
        btn_mapPoint.setOnClickListener(this);
        //distance init
        mBaiduMap.setOnMapClickListener(listener);
        view = LayoutInflater.from(this).inflate(R.layout.infoview, null);
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
                    imgDistance.setImageResource(R.mipmap.ic_ruler);
                    distance = false;
                }
                pts.remove(last);
            }
        });
    }

    BaiduMap.OnMapClickListener listener = new BaiduMap.OnMapClickListener() {
        /**
         * 地图单击事件回调函数
         * @param point 点击的地理坐标
         */
        public void onMapClick(LatLng point) {
            if (distance) {
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
        }

        @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            if (distance) {
                pts.add( mapPoi.getPosition());
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
                    final OverlayOptions dotOption = new DotOptions().center( mapPoi.getPosition()).color(0xFF0000FF).radius(10);
                    dotOptionses.add(mBaiduMap.addOverlay(dotOption));
                    tvDistance.setText(distance);
                    InfoWindow mInfoWindow = new InfoWindow(view,  mapPoi.getPosition(), -15);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                } else {
                    listDistance.add(0.0);
                    OverlayOptions dotOption = new DotOptions().center( mapPoi.getPosition()).color(0xFF0000FF).radius(10);
                    dotOptionses.add(mBaiduMap.addOverlay(dotOption));
                }
            }
            return true;
        }


        /**
         * 地图内 Poi 单击事件回调函数
         * @param poi 点击的 poi 信息
         */
    };

    public void isshowBananer() {
        if (data.time >= 2) {
            createAD();
        } else {
            data.settime();
        }
    }

    public ClusterManager.OnClusterItemClickListener<MyItem> onClusterItemClickListener = new ClusterManager.OnClusterItemClickListener<MyItem>() {
        @Override
        public boolean onClusterItemClick(MyItem item) {
            showpopupwindows(item);
            return false;
        }
    };
    public ClusterManager.OnClusterClickListener<MyItem> onClusterClickListener = new ClusterManager.OnClusterClickListener<MyItem>() {
        @Override
        public boolean onClusterClick(Cluster<MyItem> cluster) {
            LatLngBounds latLngBounds;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            List<MyItem> list = new ArrayList<>();
            list = (List<MyItem>) cluster.getItems();
            for (int i = 0; i < list.size(); i++) {
                builder.include(list.get(i).getPosition());
            }
            latLngBounds = builder.build();
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(latLngBounds));
            return false;
        }
    };


    private LatLng bdLng = null;

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

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_search:
                    ShowDialog();
                    break;
                case R.id.add_file:
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, FileActivity.class);
                    startActivity(intent);
                    break;
                case R.id.menu_main_compass:
                    Intent compassIntent = new Intent();
                    compassIntent.setClass(MainActivity.this, CompassActivity.class);
                    startActivity(compassIntent);
                    break;
            }
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        StatService.onPause(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onDestroy() {

        mBaiduMap.setMyLocationEnabled(false);
        bitmaplocation1.recycle();
        bitmaplocation2.recycle();
        BaiduMapNavigation.finish(this);
        dialog.dismiss();
        mMapView.onDestroy();//销毁地图
        mMapView = null;
        UnregisterEventBus();
        stopService(serviceIntent);
        super.onDestroy();
    }

    //wgs84 ，baidu 转换成百度格式
    public static LatLng ComanLngConvertBdLngt(LatLng lat, int coordstyle, int datastyle) {
        LatLng bdLng = null;
        switch (coordstyle) {
            case LatStyle.GPSSYTELE://84
                if (datastyle == LatStyle.DEGREE) {
                    converter.from(CoordinateConverter.CoordType.GPS);
                    converter.coord(lat);
                    bdLng = converter.convert();
                } else {
                    ConvertLatlng convertLatlng = new ConvertLatlng();
                    double latitude = convertLatlng.convertToDecimalByString(String.valueOf(lat.latitude));
                    double longtitude = convertLatlng.convertToDecimalByString(String.valueOf(lat.longitude));
                    LatLng latdimacal = new LatLng(latitude, longtitude);
                    converter.from(CoordinateConverter.CoordType.GPS);
                    converter.coord(latdimacal);
                    bdLng = converter.convert();
                }
                break;
            case LatStyle.BAIDUMAPSTYELE://bd
                if (datastyle == LatStyle.DEGREE) {
                    return lat;
                } else {
                    ConvertLatlng convertLatlng = new ConvertLatlng();
                    double latitude = convertLatlng.convertToDecimalByString(String.valueOf(lat.latitude));
                    double longtitude = convertLatlng.convertToDecimalByString(String.valueOf(lat.longitude));
                    bdLng = new LatLng(latitude, longtitude);
                }
                break;
            case LatStyle.OTHERS://
                if (datastyle == LatStyle.DEGREE) {
                    converter.from(CoordinateConverter.CoordType.COMMON);
                    converter.coord(lat);
                    bdLng = converter.convert();
                } else {
                    converter.from(CoordinateConverter.CoordType.COMMON);
                    ConvertLatlng convertLatlng = new ConvertLatlng();
                    double latitude = convertLatlng.convertToDecimalByString(String.valueOf(lat.latitude));
                    double longtitude = convertLatlng.convertToDecimalByString(String.valueOf(lat.longitude));
                    LatLng latdimacal = new LatLng(latitude, longtitude);
                    converter.coord(latdimacal);
                    bdLng = converter.convert();
                }
                break;
        }
        return bdLng;
    }


    private void addMark(LatLng bdlat, Bundle bundle) {
        OverlayOptions options2 = new MarkerOptions().position(bdlat).icons(bitmaplocation).extraInfo(bundle).period(25);
        mBaiduMap.addOverlay(options2);
        setMapCenter(bdlat.latitude, bdlat.longitude);
    }

    public void showpopupwindows(final MyItem item) {
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout ll;
        TextView lat;
        TextView name;
        Button btn_detail;
        Button btn_navagation;
        Button btn_share;
        View view = inflater.inflate(R.layout.popupwindow, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        name = (TextView) view.findViewById(R.id.tv_name);
        lat = (TextView) view.findViewById(R.id.tv_lat);
        ll = (LinearLayout) view.findViewById(R.id.card_popup);
        btn_detail = (Button) view.findViewById(R.id.btn_main_detailed);
        btn_navagation = (Button) view.findViewById(R.id.btn_main_navigation);
        btn_share = (Button) view.findViewById(R.id.btn_main_share);
        name.setText(item.getDataItem().getName());
        PointDataParcel pp = item.getDataItem().getPointDataParcel();
        PointData pointData = data.findPointDataDaoById(pp.getPointdataid());
        if (pointData.getAddress().equals(null)) {
            lat.setText("LAT:" + pointData.getLatitude()
                    + "LNG:" + pointData.getLongitude());
        } else {
            lat.setText(pointData.getAddress());
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
                startnavi(item.getPosition());
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
        popupWindow.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM | Gravity.RELATIVE_LAYOUT_DIRECTION, 0, 0);
        popupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
    }

    public void showDescribe(PointDataParcel pp) {
        pp.setActivity(WayponitActivity.DATAMANAGERACTIVITY);
        Intent intent = new Intent();
        intent.putExtra(WayponitActivity.POINTDATA, pp);
        intent.setClass(MainActivity.this, WayponitActivity.class);
        startActivity(intent);
    }

    public void shareText(PointDataParcel pp) {
        Intent shareIntent = new Intent();
        String content = "名称:" + pp.getPointname() + "\n" + "WGS经纬度" + pp.getWgsLatitude() + "," + pp.getWgsLongitude() + "\n" + "baidu经纬度:"
                + pp.getBaiduLatitude() + "," + pp.getBaiduLongitude() + "\n" + "地址:" + pp.getAddress();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        shareIntent.setType("text/plain");

        //设置分享列表的标题，并且每次都显示分享列表
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    private View showpopupwindow() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.locationpopupwindow, null);
        popupWindowDismiss = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        DisplayLocationInfo(view, bdLocationForpw, bdLng);
        popupWindowDismiss.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindowDismiss.setOutsideTouchable(true);
        popupWindowDismiss.showAsDropDown(toolbar);
        popupWindowDismiss.setAnimationStyle(R.style.anim_menu_bottombar);
        return view;

    }

    private int SELECT = 0;

    public void DisplayLocationInfo(View view, BDLocation locationInfo, final LatLng bdLng) {
        if (locationInfo == null) {
            Toast.makeText(MainActivity.this, "网络异常", Toast.LENGTH_SHORT);
        } else {
            TextView tvAddress = (TextView) view.findViewById(R.id.et_address);
            TextView tvNotice = (TextView) view.findViewById(R.id.et_notice_text);
            final TextView tvLat = (TextView) view.findViewById(R.id.et_lat_text);
            final TextView tvLong = (TextView) view.findViewById(R.id.et_lon_text);
            TextView tvAccuracy = (TextView) view.findViewById(R.id.et_accuracy_text);
            TextView tvSpeed = (TextView) view.findViewById(R.id.et_speed_text);
            TextView tvAltitude = (TextView) view.findViewById(R.id.et_altitude_text);
            TextView tvDerection = (TextView) view.findViewById(R.id.et_direction_text);
            final TextView tvwgs = (TextView) view.findViewById(R.id.tv_wgs);
            final TextView tvbaidu = (TextView) view.findViewById(R.id.tv_baidu);
            final TextView tvlatitude = (TextView) view.findViewById(R.id.tv_latitude);
            final TextView tvlongitude = (TextView) view.findViewById(R.id.tv_longtitude);
            if (locationInfo.hasAddr()) {
                address = locationInfo.getAddress().address;
            }

            String notice;
            if (locationInfo.getLocType() != BDLocation.TypeGpsLocation) {
                notice = "GPS未开启";
            } else {
                notice = "GPS已开启";
            }
            tvAddress.setText(address);
            tvNotice.setText(notice);
            DecimalFormat dfthree = new DecimalFormat("#.000");
            if (locationInfo.hasAltitude()) {
                altitude = String.valueOf(dfthree.format(locationInfo.getAltitude()));
            }
            if (locationInfo.hasAltitude()) {
                tvAltitude.setText(altitude + "米");//海拔
            } else {
                tvAltitude.setText("无");//海拔
            }
            if (locationInfo.hasSpeed()) {
                tvSpeed.setText(String.valueOf(dfthree.format(locationInfo.getSpeed())) + "km/h");
                tvDerection.setText(String.valueOf(locationInfo.getDirection()));
            } else {
                tvSpeed.setText("无");
                tvDerection.setText("无");
            }
            tvAccuracy.setText(String.valueOf(dfthree.format(locationInfo.getRadius())) + "米");
            final DecimalFormat df = new DecimalFormat("#.0000000");
            final JZLocationConverter.LatLng location = JZLocationConverter.gcj02ToWgs84(new JZLocationConverter.LatLng(locationInfo.getLatitude(), locationInfo.getLongitude()));

            final String wgsLatitude = String.valueOf((df.format(location.getLatitude())));
            final String wgsLongitude = String.valueOf(df.format(location.getLongitude()));
            final String baiduLatidude = String.valueOf((df.format(bdLng.latitude)));
            final String baiduLongitude = String.valueOf(df.format(bdLng.longitude));
            if (SELECT == 0) {
                tvlatitude.setText("WGS纬度");
                tvlongitude.setText("WGS经度");
                tvwgs.setTextColor(getResources().getColor(R.color.white));
                tvbaidu.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvLat.setText(wgsLatitude);
                tvLong.setText(wgsLongitude);
            } else {
                tvlatitude.setText("baidu纬度");
                tvlongitude.setText("baidu经度");
                tvbaidu.setTextColor(getResources().getColor(R.color.white));
                tvwgs.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvLat.setText(baiduLatidude);
                tvLong.setText(baiduLongitude);
            }

            Button btnSave = (Button) view.findViewById(R.id.btn_save);
            Button btnSaveByMove = (Button) view.findViewById(R.id.btn_save_move);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PointDataParcel pointData = new PointDataParcel();
                    pointData.setAddress(address);
                    pointData.setWgsLatitude(wgsLatitude);
                    pointData.setWgsLongitude(wgsLongitude);
                    pointData.setBaiduLatitude(baiduLatidude);
                    pointData.setBaiduLongitude(baiduLongitude);
                    pointData.setAltitude(altitude);
                    pointData.setPointname(null);
                    pointData.setActivity(WayponitActivity.MAIACTIVTY);
                    Intent intent = new Intent();
                    intent.putExtra(WayponitActivity.POINTDATA, pointData);
                    intent.setClass(MainActivity.this, WayponitActivity.class);
                    startActivity(intent);

                }
            });
            btnSaveByMove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (previous != null) {
                        previous.remove();
                    }
                    TAG_SAVEPOINT = true;
                    popupWindowDismiss.dismiss();
                    OverlayOptions presentOption = new MarkerOptions().icon(bitmap).position(bdLng);
                    previous = (Marker) mBaiduMap.addOverlay(presentOption);
                    TextView textView = new TextView(MainActivity.this);
                    textView.setTextColor(getResources().getColor(R.color.white));
                    textView.setText(String.valueOf(df.format(bdLng.latitude)) + "," + String.valueOf(df.format(bdLng.longitude)));
                    textView.setBackgroundResource(R.drawable.v4_bg_ballon);
                    InfoWindow infoWindow = new InfoWindow(textView, bdLng, -50);
                    mBaiduMap.showInfoWindow(infoWindow);
                    mBaiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
                    appxBannerContainer.setVisibility(View.GONE);
                    mapPoint.setVisibility(View.VISIBLE);
                }
            });

            tvwgs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SELECT = 0;
                    tvlatitude.setText("WGS纬度");
                    tvlongitude.setText("WGS经度");
                    tvwgs.setTextColor(getResources().getColor(R.color.white));
                    tvbaidu.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tvLat.setText(String.valueOf((df.format(location.getLatitude()))));
                    tvLong.setText(String.valueOf(df.format(location.getLongitude())));
                }
            });
            tvbaidu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SELECT = 1;
                    tvlatitude.setText("baidu纬度");
                    tvlongitude.setText("baidu经度");
                    tvbaidu.setTextColor(getResources().getColor(R.color.white));
                    tvwgs.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tvLat.setText(String.valueOf((df.format(bdLng.latitude))));
                    tvLong.setText(String.valueOf(df.format(bdLng.longitude)));
                }
            });
        }
    }

    public void showpopupwindows(final Marker marker, Bundle bundle) {
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout ll;
        TextView address;
        TextView name;
        Button btn_detail;
        Button btn_navagation;
        Button btn_share;
        View view = inflater.inflate(R.layout.popupwindow, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        name = (TextView) view.findViewById(R.id.tv_name);
        address = (TextView) view.findViewById(R.id.tv_lat);
        ll = (LinearLayout) view.findViewById(R.id.card_popup);
        btn_detail = (Button) view.findViewById(R.id.btn_main_detailed);
        btn_navagation = (Button) view.findViewById(R.id.btn_main_navigation);
        btn_share = (Button) view.findViewById(R.id.btn_main_share);
        final PointDataParcel pp = bundle.getParcelable(MARKBUNDLE);
        name.setText(pp.getPointname());
        if (pp.getAddress().isEmpty()) {
            PointData pointData = data.findPointDataDaoById(pp.getPointdataid());
            String text = "LAT:" + pointData.getLatitude() + " " + "LNG:" + pointData.getLongitude();
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
                startnavi(marker.getPosition());

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
        popupWindow.showAtLocation(findViewById(R.id.main), Gravity.BOTTOM | Gravity.RELATIVE_LAYOUT_DIRECTION, 0, 0);
        popupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
    }

    private void PopupWindowmark(Marker marker) {
        Bundle bundle = new Bundle();
        bundle = marker.getExtraInfo();
        if (bundle != null) {
            showpopupwindows(marker, bundle);
        }

    }

    private void ShowDialog() {
        Spinner spinner;
        datastyle = data.currentLatFormat;
        coordstyle = data.currentCoordinate;
        final EditText edtLatitude;
        final EditText edtLongtitude;
        RadioGroup radioGroup;
        final RadioButton radiobtnDegree;
        final RadioButton radiobtndms;
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_data, null);
        AlertDialog.Builder buildersearch = new AlertDialog.Builder(MainActivity.this);
        buildersearch.setView(dialogView);
        spinner = (Spinner) dialogView.findViewById(R.id.spn_cds);
        spinner.setSelection(data.currentCoordinate);
        radiobtnDegree = (RadioButton) dialogView.findViewById(R.id.radio_decimal);
        radiobtndms = (RadioButton) dialogView.findViewById(R.id.radio_dms);
        edtLatitude = (EditText) dialogView.findViewById(R.id.edt_latitude);
        edtLongtitude = (EditText) dialogView.findViewById(R.id.edt_longtitude);
        if (datastyle == LatStyle.DEGREE) {
            radiobtnDegree.setChecked(true);
        } else {
            radiobtndms.setChecked(true);
        }
        radioGroup = (RadioGroup) dialogView.findViewById(R.id.radgroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == radiobtnDegree.getId()) {
                    datastyle = LatStyle.DEGREE;
                    data.setCurrentLatFormat(LatStyle.DEGREE);
                    edtLatitude.setHint("39.9631757");
                    edtLongtitude.setHint("116.4002442");
                } else {
                    datastyle = LatStyle.DMS;
                    data.setCurrentLatFormat(LatStyle.DMS);
                    edtLatitude.setHint("30.345245");
                    edtLongtitude.setHint("120.142835");

                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View view,
                                       int position, long id) {
                coordstyle = position;
                data.setCurrentCoordinate(position);
            }

            @Override
            public void onNothingSelected(AdapterView parent) {
            }
        });
        buildersearch.setTitle("经纬度查询");
        buildersearch.setPositiveButton("查看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!edtLatitude.getText().toString().isEmpty() && !edtLongtitude.getText().toString().isEmpty()) {
                    LatLng point = new LatLng(Double.parseDouble(edtLatitude.getText().toString()), Double.parseDouble(edtLongtitude.getText().toString()));
                    LatLng bd = ComanLngConvertBdLngt(point, coordstyle, datastyle);
                    PointData pointData = new PointData();
                    pointData.setAddress("");
                    DecimalFormat df = new DecimalFormat("#.0000000");
                    pointData.setLatitude(edtLatitude.getText().toString());
                    pointData.setLongitude(edtLongtitude.getText().toString());
                    pointData.setBaidulatitude(String.valueOf(df.format(bd.latitude)));
                    pointData.setBaidulongitude(String.valueOf(df.format(bd.longitude)));
                    if (coordstyle == LatStyle.GPSSYTELE) {
                        if (datastyle == LatStyle.DEGREE) {
                            pointData.setWgslatitude(edtLatitude.getText().toString());
                            pointData.setWgslongitude(edtLongtitude.getText().toString());
                        } else {
                            pointData.setWgslatitude(String.valueOf(df.format(ConvertLatlng.convertToDecimalByString(edtLatitude.getText().toString()))));
                            pointData.setWgslongitude(String.valueOf(df.format(ConvertLatlng.convertToDecimalByString(edtLongtitude.getText().toString()))));
                        }
                    }
                    pointData.setName("");
                    data.createPointData(data.findOrderByName("我的收藏"), pointData);
                    ShowData showData = new ShowData();
                    showData.setTitle(pointData.getName());
                    showData.setLatitude(pointData.getBaidulatitude());
                    showData.setLongitude(pointData.getBaidulongitude());
                    showData.setCdstyle(LatStyle.BAIDUMAPSTYELE);
                    showData.setDatastyle(LatStyle.DEGREE);
                    showData.setPointid(pointData.getId());
                    showData.setFileid(data.findOrderByName("我的收藏").getId());
                    data.getmDaoSession().getShowDataDao().insert(showData);
                    PointDataParcel pp = setPointdataParcel(data.findOrderByName("我的收藏"), pointData);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(MARKBUNDLE, pp);
                    addMark(bd, bundle);
                }
            }
        });
        buildersearch.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        buildersearch.create().show();
    }

    BaiduMap.OnMapStatusChangeListener onMapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        /**
         * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
         * @param status 地图状态改变开始时的地图状态
         */
        public void onMapStatusChangeStart(MapStatus status) {
        }

        /**
         * 地图状态变化中
         * @param status 当前地图状态
         */
        public void onMapStatusChange(MapStatus status) {
            if (TAG_SAVEPOINT) {
                previous.setPosition(status.target);
                DecimalFormat df = new DecimalFormat("#.0000000");
                TextView textView = new TextView(MainActivity.this);
                textView.setText(String.valueOf(df.format(status.target.latitude)) + "," + String.valueOf(df.format(status.target.longitude)));
                textView.setBackgroundResource(R.drawable.v4_bg_ballon);
                textView.setTextColor(getResources().getColor(R.color.white));
                InfoWindow infoWindow = new InfoWindow(textView, status.target, -50);
                mBaiduMap.showInfoWindow(infoWindow);
            }
        }

        /**
         * 地图状态改变结束
         * @param status 地图状态改变结束后的地图状态
         */
        public void onMapStatusChangeFinish(MapStatus status) {
            if (TAG_SAVEPOINT) {
                previous.setPosition(status.target);
                DecimalFormat df = new DecimalFormat("#.0000000");
                TextView textView = new TextView(MainActivity.this);
                textView.setText(String.valueOf(df.format(status.target.latitude)) + "," + String.valueOf(df.format(status.target.longitude)));
                textView.setBackgroundResource(R.drawable.v4_bg_ballon);
                textView.setTextColor(getResources().getColor(R.color.white));
                InfoWindow infoWindow = new InfoWindow(textView, status.target, -50);
                mBaiduMap.showInfoWindow(infoWindow);
                pointlat=status.target.latitude;
                pointlng=status.target.longitude;
                getaddress(new LatLng(pointlat,pointlng));
            }
        }
    };

    private void getaddress(LatLng bdlat) {

        Log.d("address", "sfsa");
        GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(geolistener);
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(bdlat));
    }
    OnGetGeoCoderResultListener geolistener = new OnGetGeoCoderResultListener() {
        // 反地理编码查询结果回调函数
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null
                    || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // 没有检测到结果
                Toast.makeText(MainActivity.this, "抱歉，未能找到结果",
                        Toast.LENGTH_LONG).show();
            }
            pointAdress = result.getAddress();
            tv_mappoint_adress.setText(pointAdress);
        }

        // 地理编码查询结果回调函数
        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null
                    || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // 没有检测到结果
            }
        }
    };

    // 调起百度地图导航
    private void startnavi(LatLng latLng) {
        LatLng latLng1 = new LatLng(latitude, lontitude);
        NaviParaOption para = new NaviParaOption()
                .startPoint(latLng1).endPoint(latLng);
        try {
            BaiduMapNavigation.setSupportWebNavi(true);
            BaiduMapNavigation.openBaiduMapNavi(para, this);
        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            showDialog();
        }
    }

    public void showDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OpenClientUtil.getLatestBaiduMapApp(MainActivity.this);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    public void createAD() {

        AppActivity.setActionBarColorTheme(AppActivity.ActionBarColorTheme.ACTION_BAR_BLUE_THEME);
//        另外，也可设置动作栏中单个元素的颜色, 颜色参数为四段制，0xFF(透明度, 一般填FF)DE(红)DA(绿)DB(蓝)
//        AppActivity.getActionBarColorTheme().set[Background|Title|Progress|Close]Color(0xFFDEDADB);

        // 创建广告View
        String adPlaceId = "2431165";
        adView = new AdView(this, adPlaceId);
        // 设置监听器
        adView.setListener(new AdViewListener() {
            public void onAdSwitch() {
                Log.w("", "onAdSwitch");
            }

            public void onAdShow(JSONObject info) {
                // 广告已经渲染出来
                Log.w("", "onAdShow " + info.toString());
            }

            public void onAdReady(AdView adView) {
                // 资源已经缓存完毕，还没有渲染出来
                Log.w("", "onAdReady " + adView);
            }

            public void onAdFailed(String reason) {
                Log.w("", "onAdFailed " + reason);
            }

            public void onAdClick(JSONObject info) {
                // Log.w("", "onAdClick " + info.toString());

            }
        });

        appxBannerContainer.addView(adView);
    }

    public void startCount() {
        boolean isWear = getPackageManager().hasSystemFeature("android.hardware.type.watch");

        Log.e("TEST", "isWear: " + isWear);

        Log.e("TEAT", "manufacturer: " + Build.MANUFACTURER);

        // 设置AppKey
        // StatService.setAppKey("a9e2ad84a2"); // appkey必须在mtj网站上注册生成，该设置建议在AndroidManifest.xml中填写，代码设置容易丢失

        /*
         * 设置渠道的推荐方法。该方法同setAppChannel（String）， 如果第三个参数设置为true（防止渠道代码设置会丢失的情况），将会保存该渠道，每次设置都会更新保存的渠道，
         * 如果之前的版本使用了该函数设置渠道，而后来的版本需要AndroidManifest.xml设置渠道，那么需要将第二个参数设置为空字符串,并且第三个参数设置为false即可。
         * appChannel是应用的发布渠道，不需要在mtj网站上注册，直接填写就可以 该参数也可以设置在AndroidManifest.xml中
         */
//        StatService.setAppChannel(this, "RepleceWithYourChannel", true);
        // 测试时，可以使用1秒钟session过期，这样不断的间隔1S启动退出会产生大量日志。
        StatService.setSessionTimeOut(30);
        // setOn也可以在AndroidManifest.xml文件中填写，BaiduMobAd_EXCEPTION_LOG，打开崩溃错误收集，默认是关闭的
        StatService.setOn(this, StatService.EXCEPTION_LOG);
        /*
         * 设置启动时日志发送延时的秒数<br/> 单位为秒，大小为0s到30s之间<br/> 注：请在StatService.setSendLogStrategy之前调用，否则设置不起作用
         *
         * 如果设置的是发送策略是启动时发送，那么这个参数就会在发送前检查您设置的这个参数，表示延迟多少S发送。<br/> 这个参数的设置暂时只支持代码加入，
         * 在您的首个启动的Activity中的onCreate函数中使用就可以。<br/>
         */
        StatService.setLogSenderDelayed(0);
        /*
         * 用于设置日志发送策略<br /> 嵌入位置：Activity的onCreate()函数中 <br />
         *
         * 调用方式：StatService.setSendLogStrategy(this,SendStrategyEnum. SET_TIME_INTERVAL, 1, false); 第二个参数可选：
         * SendStrategyEnum.APP_START SendStrategyEnum.ONCE_A_DAY SendStrategyEnum.SET_TIME_INTERVAL 第三个参数：
         * 这个参数在第二个参数选择SendStrategyEnum.SET_TIME_INTERVAL时生效、 取值。为1-24之间的整数,即1<=rtime_interval<=24，以小时为单位 第四个参数：
         * 表示是否仅支持wifi下日志发送，若为true，表示仅在wifi环境下发送日志；若为false，表示可以在任何联网环境下发送日志
         */
        StatService.setSendLogStrategy(this, SendStrategyEnum.SET_TIME_INTERVAL, 1, false);
        // 调试百度统计SDK的Log开关，可以在Eclipse中看到sdk打印的日志，发布时去除调用，或者设置为false
        StatService.setDebugOn(true);
        String sdkVersion = StatService.getSdkVersion();
    }

    private PointDataParcel setPointdataParcel(Files files, PointData pointData) {
        PointDataParcel pp = new PointDataParcel();
        pp.setActivity(WayponitActivity.DATAMANAGERACTIVITY);
        pp.setAddress(pointData.getAddress());
        pp.setPointname(pointData.getName());
        pp.setAltitude(pointData.getAltitude());
        pp.setBaiduLongitude(pointData.getBaidulongitude());
        pp.setBaiduLatitude(pointData.getBaidulatitude());
        pp.setWgsLatitude(pointData.getWgslatitude());
        pp.setWgsLongitude(pointData.getWgslongitude());
        pp.setDescribe(pointData.getDescribe());
        pp.setPointdataid(pointData.getId());
        pp.setFileid(files.getId());
        return pp;
    }

    private void RegisterEventBus() {
        EventBus.getDefault().registerSticky(this);
    }

    private void UnregisterEventBus() {
        try {
            EventBus.getDefault().unregister(this);
        } catch (Throwable t) {
            //this may crash if registration did not go through. just be safe
        }
    }

    public void onEventMainThread(ServiceEvents.LocationUpdate bdLocation) {
        BDLocation location = bdLocation.location;
        bdLocationForpw = location;
        LatLng gc02Lng = new LatLng(location.getLatitude(), location.getLongitude());
        converter.from(CoordinateConverter.CoordType.COMMON);
        converter.coord(gc02Lng);
        bdLng = converter.convert();
        if (latitude == 0.0) {
            latitude = bdLng.latitude;
            lontitude = bdLng.longitude;
            setMapCenter(latitude, lontitude);
        } else {
            latitude = bdLng.latitude;
            lontitude = bdLng.longitude;
        }
        showLocation(location, bdLng);
        if (popupWindow != null) {
            DisplayLocationInfo(popupWindow, location, bdLng);
        }
    }

    private Intent serviceIntent;

    public void startService() {
        serviceIntent = new Intent();
        serviceIntent.setClass(this, LocationService.class);
        startService(serviceIntent);
    }
}


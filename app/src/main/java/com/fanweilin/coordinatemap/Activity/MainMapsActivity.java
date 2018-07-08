package com.fanweilin.coordinatemap.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.mapapi.utils.OpenClientUtil;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.fanweilin.coordinatemap.Class.LatStyle;
import com.fanweilin.coordinatemap.Class.MapsType;
import com.fanweilin.coordinatemap.Class.PointDataParcel;
import com.fanweilin.coordinatemap.Class.ShowPointStyle;
import com.fanweilin.coordinatemap.Class.SpfOlMap;
import com.fanweilin.coordinatemap.Class.StringToPoint;
import com.fanweilin.coordinatemap.Class.UserVip;
import com.fanweilin.coordinatemap.DataModel.BaiduDataApi;
import com.fanweilin.coordinatemap.DataModel.BaiduHttpControl;
import com.fanweilin.coordinatemap.DataModel.BaiduUserMapDataBean;
import com.fanweilin.coordinatemap.DataModel.Constants;
import com.fanweilin.coordinatemap.DataModel.Poi;
import com.fanweilin.coordinatemap.MapSource.BingMapsTileSource;
import com.fanweilin.coordinatemap.MapSource.GaoDeMapsTileSource;
import com.fanweilin.coordinatemap.MapSource.GoogleMaps;
import com.fanweilin.coordinatemap.MapSource.GoogleMapsSatellite;
import com.fanweilin.coordinatemap.MapSource.GoogleMapsTileSource;
import com.fanweilin.coordinatemap.MapSource.GoolgeMapsTerrain;
import com.fanweilin.coordinatemap.MapSource.TianDituMapsTile;
import com.fanweilin.coordinatemap.MapSource.TianDituSatellite;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.Trace.utils.BitmapUtil;
import com.fanweilin.coordinatemap.computing.Computer;
import com.fanweilin.coordinatemap.computing.ConvertLatlng;
import com.fanweilin.coordinatemap.computing.Distance;
import com.fanweilin.coordinatemap.computing.GaussXYDeal;
import com.fanweilin.coordinatemap.computing.JZLocationConverter;
import com.fanweilin.coordinatemap.computing.Location3TheConvert;
import com.fanweilin.coordinatemap.fragment.BaiduMapFragment;
import com.fanweilin.coordinatemap.fragment.OsmdroidFragment;
import com.fanweilin.coordinatemap.widget.MyViewPagerHome;
import com.fanweilin.greendao.CoordinateData;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.Olfiles;
import com.fanweilin.greendao.PointData;
import com.fanweilin.greendao.ShowData;
import com.fanweilin.greendao.ShowDataDao;

import com.fanweilin.greendao.SqlPolygon;
import com.fanweilin.greendao.SqlPolyline;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.qcloud.presentation.business.LoginBusiness;
import com.tencent.qcloud.presentation.event.FriendshipEvent;
import com.tencent.qcloud.presentation.event.GroupEvent;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.presentation.event.RefreshEvent;
import com.tencent.qcloud.presentation.presenter.SplashPresenter;
import com.tencent.qcloud.presentation.viewfeatures.SplashView;
import com.tencent.qcloud.tlslibrary.activity.PhonePwdLoginActivity;
import com.tencent.qcloud.tlslibrary.service.TLSService;
import com.tencent.qcloud.tlslibrary.service.TlsBusiness;
import com.tencent.qcloud.ui.NotifyDialog;


import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;

import java.net.URISyntaxException;
import java.util.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import timchat.model.FriendshipInfo;
import timchat.model.GroupInfo;
import timchat.model.UserInfo;
import timchat.ui.ChatActivity;
import timchat.ui.customview.DialogActivity;
import timchat.ui.customview.MapManagerActivity;

public class MainMapsActivity extends AppCompatActivity implements View.OnClickListener, OnGetSuggestionResultListener, SplashView, TIMCallBack {
    public static String DISTANCEACTIVITY = "distanceactivity";
    public static String DISTANCELAT = "distanceAtoB";
    public static String GETPOINTDATAPARCE = "getpointdataparcel";
    public static String DATAMANAGERACTIVITY = "datamanageractivity";
    public static String OnlineMap = "onlinemap";
    public static String MAPMANAGERACTIVITY="MapManangerActivity";
    public static int  MAYOLTYPE=0;
    public Toolbar toolbar;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle mDrawerToggle;
    private int coordstyle;
    private int datastyle;

    private RelativeLayout appxBannerContainer;
    private FloatingActionButton fabLocation;
    private FloatingActionButton fabPoint;
    private FloatingActionButton fabDistance;
    private FloatingActionButton fabArea;
    private FloatingActionButton fabUndo;
    private FloatingActionButton fabSure;
    private FloatingActionButton fabCancle;
    private FloatingActionButton fabUser;
    RelativeLayout rlArea;
    public TextView tvArea;
    //  popupwindow
    private View popupWindow;
    PopupWindow popupWindowDismiss;
    private BDLocation bdLocationForpw;
    String address = "";
    String altitude = "";
    //当前位置bd经纬度
    public LatLng gcjLng = null;
    public LatLng bdLng = null;
    //当前位置wgs84经纬度
    private JZLocationConverter.LatLng wgsLng;
    private List<Fragment> fragments;
    //viewpager
    private MyViewPagerHome mViewPager;
    private BaiduMapFragment bdfragment;
    private OsmdroidFragment osfragment;
    private static final int REQUEST = 1;
    private static final int REQUESTMeasrue = 2;
    public static final int REQUESTDATASERVER = 3;
    //当前地图信息
    private SharedPreferences spfmaps;
    //当前坐标系
    public int mapcoordinate;
    //当前地图id
    public int mapstyle;
    //位置查询
    private SuggestionSearch mSuggestionSearch = null;
    private List<Map<String, Object>> listdata;
    private PoiSearch mPoiSearch = null;
    private String city = "北京";
    private Handler mHandler;
    //地图点选
    TextView tvCenter;
    ImageView ivCenter;
    String centerdistance;

    TextView tv_mappoint_adress;
    private String pointAdress;
    private RelativeLayout mapPoint;
    private boolean TAG_SAVEPOINT = false;
    //os地图中心经纬度
    Double wgscenterlat;
    Double wgscenterlon;
    //百度地图中心经纬度
    Double pointlat;
    Double pointlng;
    //
    public TextView tvarea;
    private SharedPreferences spfVip;
    //百度location
    private CoordinateConverter converter;
    public LocationClient bdLocationClient;
    private BDLocationListener bdLocationListener;
    //平面坐标spf
    private SharedPreferences spfcoor;
    Long coorid;
    //
   private ProgressDialog dialog;
    private BottomNavigationBar bottomNavigationBar;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        openOlmap(intent);
        setmapCenter();
        showLineDistance(getIntent());
        setlocation(getIntent());
        showdata(getIntent());
        loginout(getIntent());
    }
    private void loginout(Intent intent){
        String activity;
        activity = intent.getStringExtra("ACTIVITY");
        if ( activity != null) {
            SharedPreferences.Editor editor=data.spfOlMapSet.edit();
            editor.putInt(SpfOlMap.MAPTYPE,1);
            editor.commit();
            clearall();
            bottomNavigationBar.setVisibility(View.GONE);
            toolbar.setTitle("");
        }
    }

   private void openOlmap(Intent intent){
        String name;
        name=intent.getStringExtra(OnlineMap);
        if(name!=null){
            getOlmapdata();
        }

   }
   private void setmapCenter(){
       Intent intent=getIntent();
       double latitude=intent.getDoubleExtra("latitude",-0.1);
       double longitude=intent.getDoubleExtra("longitude",-0.1);
     if(latitude>0){
         osfragment.setMapcenter(new GeoPoint(latitude,longitude));
         bdfragment.setMapCenter(new LatLng(latitude,longitude));
     }

   }
    private void showLineDistance(Intent intent) {
        String name;
        name = intent.getStringExtra(DISTANCEACTIVITY);
        if (name != null) {
            switch (mapcoordinate) {
                case Location3TheConvert.Baidu:
                    bdfragment.showDistance(intent);
                    break;
                case Location3TheConvert.GCJ2:
                    osfragment.showDistance(intent, Location3TheConvert.WGS84);
                    break;
                case Location3TheConvert.WGS84:
                    osfragment.showDistance(intent, Location3TheConvert.WGS84);
                    break;
            }

        }

    }

    private void setlocation(Intent intent) {
        switch (mapcoordinate) {
            case Location3TheConvert.Baidu:
                bdfragment.setlocation(intent);
                break;
            case Location3TheConvert.GCJ2:
                osfragment.setlocaiton(intent, Location3TheConvert.GCJ2);
                break;
            case Location3TheConvert.WGS84:
                osfragment.setlocaiton(intent, Location3TheConvert.WGS84);
                break;
        }

    }

    public void showdata(Intent intent) {
        String data = intent.getStringExtra("data");
        if (data != null && data.equals("more")) {
            switch (mapcoordinate) {
                case Location3TheConvert.Baidu:
                    bdfragment.showdata();
                    bdfragment.showAllMarks();
                    break;
                case Location3TheConvert.GCJ2:
                    osfragment.showdata(Location3TheConvert.GCJ2);
                    break;
                case Location3TheConvert.WGS84:
                    osfragment.showdata(Location3TheConvert.WGS84);
                    break;
            }

        }
    }

    public void clearall() {
        bdfragment.clearall();
        osfragment.clearall();
        DaoSession mDaoSession = data.getmDaoSession();
        ShowDataDao showDataDao = mDaoSession.getShowDataDao();
        showDataDao.deleteAll();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_maps);
        //开启定位服务
        init();

    }

    public void init() {
        //轨迹
        BitmapUtil.init();
        //viewpager
        fragments = new ArrayList<Fragment>();
        bdfragment = new BaiduMapFragment();
        osfragment = new OsmdroidFragment();
        fragments.add(bdfragment);
        fragments.add(osfragment);
        mViewPager =  findViewById(R.id.vp_maps);
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(fragmentAdapter);
        converter = new CoordinateConverter();
        dialog = new ProgressDialog(MainMapsActivity.this);
        dialog.setIndeterminate(true);

        //设置toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        if(data.spfOlMapSet.getInt(SpfOlMap.MAPTYPE,1)==2){
               toolbar.setTitle(data.spfOlMapSet.getString(SpfOlMap.MAPNAME,""));
               toolbar.setTitle(data.spfOlMapSet.getString(SpfOlMap.MAPNAME,""));
        }

        spfVip = getSharedPreferences(UserVip.SPFNAME, Context.MODE_PRIVATE);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View head = navigationView.getHeaderView(0);
        TextView tvlog = head.findViewById(R.id.tv_mainactivity_log);
        //设置用户名
        if(UserInfo.getInstance().getId()!= null){
            tvlog.setText(UserInfo.getInstance().getId());
        }
        setupDrawerContent(navigationView);
        //侧滑菜单设置
        drawerLayout = findViewById(R.id.drawerlayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        drawerLayout.addDrawerListener(mDrawerToggle);
        //设置浮动按钮
        fabLocation = findViewById(R.id.fab_location);
        fabPoint = findViewById(R.id.fab_point);
        fabDistance = findViewById(R.id.fab_distance);
        fabArea = findViewById(R.id.fab_area);
        fabUndo = findViewById(R.id.fab_undo);
        fabSure = findViewById(R.id.fab_sure);
        fabCancle = findViewById(R.id.fab_cancel);
        fabUser = findViewById(R.id.fab_user);
        rlArea = findViewById(R.id.rl_area);
        tvArea = findViewById(R.id.tv_area);
        fabUser.setOnClickListener(this);
        fabLocation.setOnClickListener(this);
        fabPoint.setOnClickListener(this);
        fabDistance.setOnClickListener(this);
        fabArea.setOnClickListener(this);
        fabUndo.setOnClickListener(this);
        fabSure.setOnClickListener(this);
        fabCancle.setOnClickListener(this);
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        //baidu统计,广告
        startCount();
        appxBannerContainer = findViewById(R.id.appx_banner_container);
        //设置地图选项
        spfmaps = getSharedPreferences("spfmaps", Context.MODE_PRIVATE);
        mapcoordinate = spfmaps.getInt("coordiname", Location3TheConvert.Baidu);
        mapstyle = spfmaps.getInt("mapstyle", 0);
        int mapmodel = spfmaps.getInt("mapmodel", 0);
        if (mapmodel == 0) {
            MapView.setMapCustomEnable(false);
        }
        if (mapcoordinate == Location3TheConvert.Baidu) {
            mViewPager.setCurrentItem(0);
        } else {
            mViewPager.setCurrentItem(1);
        }
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
// 接收消息
                switch (msg.what) {
                    case 0:
                        JZLocationConverter.LatLng latLng = getLatLng();
                        osfragment.setMapcenter(new GeoPoint(latLng.getLatitude(), latLng.getLongitude()));
                        if(mapcoordinate!=Location3TheConvert.Baidu){
                            osfragment.showdata(mapcoordinate);
                               osfragment.showAllMarks();
                        }
                        break;
                    case 1:
                        osfragment.showLocation(currentlocation, getLatLng());
                        break;
                    case 2:
                        DisplayLocationInfo(popupWindow, currentlocation, gcjLng);
                        break;
                }
            }
        };
        //软件更新
//        update();
        //位置查询
        initdata();
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        //
        Button btn_mapPoint = findViewById(R.id.btn_main_pointsave);
        Button btncancel = findViewById(R.id.btn_main_cancel);
        btncancel.setOnClickListener(this);
        btn_mapPoint.setOnClickListener(this);
        mapPoint = findViewById(R.id.rl_main_mappoint);
        ivCenter = findViewById(R.id.iv_main_center);
        tvCenter = findViewById(R.id.tv_main_center_lng);
        tv_mappoint_adress = findViewById(R.id.tv_main_mappointAdress);
        //开启广告
        isshowBananer();
        //位置初始化化
        initLocation();
        spfcoor = getSharedPreferences("spfcoor", Context.MODE_PRIVATE);
        coorid = spfcoor.getLong("coorid", -1);
        //面积
        tvarea = findViewById(R.id.tv_area);
        //底部toolbar
       bottomNavigationBar =findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar
                .setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar
                .setActiveColor(R.color.light_blue_500) //设置选中的颜色
                .setInActiveColor(R.color.light_blue_500);

        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.message,"消息"))
                           .addItem(new BottomNavigationItem(R.mipmap.icon_up,"上传数据"))
                           .addItem(new BottomNavigationItem(R.mipmap.listdata,"数据"))
                           .addItem(new BottomNavigationItem(R.mipmap.exit,"退出"))
                           .initialise();
        if(data.spfOlMapSet.getInt(SpfOlMap.MAPTYPE,1)==2){
            bottomNavigationBar.setVisibility(View.VISIBLE);
        }
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {
                switch (position){
                    case 0:
                        ChatActivity.navToChat(MainMapsActivity.this,data.spfOlMapSet.getString(SpfOlMap.MAPID,""), TIMConversationType.Group);
                        break;
                    case 1:
                        Intent intent1=new Intent(MainMapsActivity.this,FileManagerActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent=new Intent(MainMapsActivity.this,DataServerManagerActivity.class);
                        intent.putExtra(DataServerManagerActivity.FILENAME,data.spfOlMapSet.getString(SpfOlMap.MAPNAME,""));
                        startActivityForResult(intent,REQUESTDATASERVER);
                        break;
                    case 3:
                        SharedPreferences.Editor editor=data.spfOlMapSet.edit();
                        editor.putInt(SpfOlMap.MAPTYPE,1);
                        editor.commit();
                        clearall();
                        bottomNavigationBar.setVisibility(View.GONE);
                        toolbar.setTitle("");
                        break;
                }
            }
            @Override
            public void onTabUnselected(int position) {
            }
            @Override
            public void onTabReselected(int position) {
                switch (position){
                    case 0:
                        ChatActivity.navToChat(MainMapsActivity.this,data.spfOlMapSet.getString(SpfOlMap.MAPID,""), TIMConversationType.Group);
                        break;
                    case 1:
                        Intent intent1=new Intent(MainMapsActivity.this,FileManagerActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent=new Intent(MainMapsActivity.this,DataServerManagerActivity.class);
                        intent.putExtra(DataServerManagerActivity.FILENAME,data.spfOlMapSet.getString(SpfOlMap.MAPNAME,""));
                        startActivity(intent);
                        break;
                    case 3:
                        SharedPreferences.Editor editor=data.spfOlMapSet.edit();
                        editor.putInt(SpfOlMap.MAPTYPE,1);
                        editor.commit();
                        clearall();
                        bottomNavigationBar.setVisibility(View.GONE);
                        toolbar.setTitle("");
                        break;
                }

            }
        });

    }


    private void initLocation() {
        bdLocationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int time = Integer.parseInt(prefs.getString("gps_rate", "1"));
        option.setCoorType("gcj02");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(time * 1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(true);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        bdLocationListener = new BDLocationListener();
        bdLocationClient.setLocOption(option);
        bdLocationClient.start();
        bdLocationClient.registerLocationListener(bdLocationListener);
        wgsLng = new JZLocationConverter.LatLng(0, 0);
    }


    private class BDLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null || bdfragment.mMapView == null || osfragment.mapView == null) {
                return;
            }
            currentlocation = bdLocation;
            if (currentlocation.getCity() != null) {
                city = currentlocation.getCity();
            }
            bdLocationForpw = currentlocation;
            wgsLng = JZLocationConverter.gcj02ToWgs84(new JZLocationConverter.LatLng(currentlocation.getLatitude(), currentlocation.getLongitude()));
            bdLng = Location3TheConvert.ConverToBaidu(currentlocation.getLatitude(), currentlocation.getLongitude(), Location3TheConvert.GCJ2);
            gcjLng = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
            if (isFirstLoc) {
                isFirstLoc = false;
                bdfragment.setMapCenter(gcjLng);
                Message msg = new Message();
                msg.what = 0;
                mHandler.sendMessage(msg);

            }
            if (gcjLng != null) {
                bdfragment.showLocation(currentlocation, gcjLng);
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);

            }

            if (popupWindow != null) {
                Message msg = new Message();
                msg.what = 2;
                mHandler.sendMessage(msg);

            }
        }

    }

    public class FragmentAdapter extends FragmentPagerAdapter {
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
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
                    intent.setClass(MainMapsActivity.this, FileManagerActivity.class);
                    startActivity(intent);
                    break;
                case R.id.menu_main_compass:
                    Intent toolBoxIntent = new Intent();
                    toolBoxIntent.setClass(MainMapsActivity.this, MainActivity.class);
                    startActivity(toolBoxIntent);
                    break;
            }
            return true;
        }
    };

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_computer:
                                community();
                                break;
                            case R.id.nav_taobao:
                                taobao();
                                break;
                            case R.id.nav_down:
                                download();
                                break;
                            case R.id.nav_about:
                                setting();
                                break;
                            case R.id.nav_maptype:
                                setmaptype();
                                break;
                            case R.id.nav_help:
                                help();
                                break;
                            case R.id.nav_back:
                                TlsBusiness.logout(UserInfo.getInstance().getId());
                                UserInfo.getInstance().setId(null);
                                MessageEvent.getInstance().clear();
                                FriendshipInfo.getInstance().clear();
                                GroupInfo.getInstance().clear();
                                SharedPreferences.Editor editor=data.spfOlMapSet.edit();
                                editor.putInt(SpfOlMap.MAPTYPE,1);
                                editor.commit();
                                clearall();
                                finish();
                                break;
                        }
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void help() {
        Intent intent = new Intent();
        intent.setClass(MainMapsActivity.this, WebActivity.class);
        startActivity(intent);
    }

    private void setting() {
        Intent intent = new Intent();
        intent.setClass(MainMapsActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    private void setmaptype() {
        Intent intent = new Intent();
        intent.setClass(MainMapsActivity.this, GuassActivity.class);
        startActivity(intent);
    }

    public void isshowBananer() {
        int vip = spfVip.getInt(UserVip.SPFVIP, 1);
        Log.d("vip", String.valueOf(vip));
        if (vip > 1) {
            if (data.time >= 2) {
                // update();
            }
        } else {
            if (data.time >= 2) {
                createAD();
            } else {
                data.settime();
            }
        }

    }





    private void download() {
        Intent intent = new Intent();
        intent.setClass(MainMapsActivity.this, Bdoffilin_activity.class);
        startActivity(intent);
    }

    private void community() {
        Intent intent = new Intent();
        intent.setClass(MainMapsActivity.this, AddFileActivity.class);
        startActivity(intent);
    }


    public void taobao() {
        Intent intent = new Intent();
       intent.setClass(MainMapsActivity.this, TaoBaoActivity.class);
        intent.setClass(MainMapsActivity.this, TaoBaoActivity.class);
        startActivity(intent);
    }

    private void ShowDialog() {
        Spinner spinner;
        Spinner spinnerXy;
        datastyle = data.currentLatFormat;
        coordstyle = data.currentCoordinate;
        final EditText edtLatitude;
        final EditText edtLongtitude;
        final EditText edtName;
        final EditText edtXyName;
        RadioGroup radioGroup;
        final RadioButton radiobtnDegree;
        final RadioButton radiobtndms;
        final RadioButton radiobtnxy;
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_data, null);
        AlertDialog.Builder buildersearch = new AlertDialog.Builder(MainMapsActivity.this);
        buildersearch.setView(dialogView);
        spinner = dialogView.findViewById(R.id.spn_cds);
        spinnerXy = dialogView.findViewById(R.id.spn_xy);
        final List<CoordinateData> coordinateDatas;
        coordinateDatas=data.getmDaoSession().getCoordinateDataDao().loadAll();
        ArrayList <String> mItems=new ArrayList<String>();
        int showdata=-1;
        for (int i=0;i<coordinateDatas.size();i++){
            mItems.add(coordinateDatas.get(i).getName());
            if (coorid==coordinateDatas.get(i).getId()){
                showdata=i;
            }
        }
        spinnerXy.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mItems));
        if (showdata>-1){
            spinnerXy.setSelection(showdata);
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
        spinner.setSelection(data.currentCoordinate);
        final LinearLayout latLinearLayout =  dialogView.findViewById(R.id.ll_latitude);
        final LinearLayout logtLinearLayout =  dialogView.findViewById(R.id.ll_longitude);
        final LinearLayout llLatlon=  dialogView.findViewById(R.id.ll_latlon);
        final LinearLayout llXy= dialogView. findViewById(R.id.ll_xy);
        radiobtnDegree =  dialogView.findViewById(R.id.radio_decimal);
        radiobtndms =  dialogView.findViewById(R.id.radio_dms);
        radiobtnxy =  dialogView.findViewById(R.id.radio_xy);
        edtLatitude =  dialogView.findViewById(R.id.edt_latitude);
        edtLongtitude =  dialogView.findViewById(R.id.edt_longtitude);
        edtName=dialogView.findViewById(R.id.edt_name);
        edtXyName=dialogView.findViewById(R.id.edt_xyname);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        edtName.setText(formatter.format(new Date()));
        edtXyName.setText(formatter.format(new Date()));
        final EditText latitudedu =  dialogView.findViewById(R.id.latidude_du);
        final EditText latitudefen =  dialogView.findViewById(R.id.latidude_fen);
        final EditText latitudemiao =  dialogView.findViewById(R.id.latidude_miao);
        final EditText longtitudedu = dialogView.findViewById(R.id.longgitude_du);
        final EditText longtitudefen =  dialogView.findViewById(R.id.longgitude_fen);
        final EditText longtitudemiao =  dialogView.findViewById(R.id.longgitude_miao);
        final EditText editx= dialogView.findViewById(R.id.editx);
        final EditText edity=  dialogView.findViewById(R.id.edity);
        switch (datastyle){
            case LatStyle.DEGREE:
                radiobtnDegree.setChecked(true);
                edtLatitude.setVisibility(View.VISIBLE);
                edtLongtitude.setVisibility(View.VISIBLE);
                latLinearLayout.setVisibility(View.GONE);
                logtLinearLayout.setVisibility(View.GONE);
                break;
            case LatStyle.DMS:
                radiobtndms.setChecked(true);
                edtLatitude.setVisibility(View.GONE);
                edtLongtitude.setVisibility(View.GONE);
                latLinearLayout.setVisibility(View.VISIBLE);
                logtLinearLayout.setVisibility(View.VISIBLE);
                break;
            case LatStyle.XY:
                 radiobtnxy.setChecked(true);
                 llLatlon.setVisibility(View.GONE);
                 llXy.setVisibility(View.VISIBLE);
                break;
        }

        radioGroup =dialogView.findViewById(R.id.radgroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (i == radiobtnDegree.getId()) {
                    llLatlon.setVisibility(View.VISIBLE);
                    llXy.setVisibility(View.GONE);
                    edtLatitude.setVisibility(View.VISIBLE);
                    edtLongtitude.setVisibility(View.VISIBLE);
                    latLinearLayout.setVisibility(View.GONE);
                    logtLinearLayout.setVisibility(View.GONE);
                    datastyle = LatStyle.DEGREE;
                    data.setCurrentLatFormat(LatStyle.DEGREE);
                } else if (i==radiobtndms.getId()){
                    llLatlon.setVisibility(View.VISIBLE);
                    llXy.setVisibility(View.GONE);
                    edtLatitude.setVisibility(View.GONE);
                    edtLongtitude.setVisibility(View.GONE);
                    latLinearLayout.setVisibility(View.VISIBLE);
                    logtLinearLayout.setVisibility(View.VISIBLE);
                    datastyle = LatStyle.DMS;
                    data.setCurrentLatFormat(LatStyle.DMS);
                }else {
                    llLatlon.setVisibility(View.GONE);
                    llXy.setVisibility(View.VISIBLE);
                    datastyle = LatStyle.XY;
                    data.setCurrentLatFormat(LatStyle.XY);
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
        spinnerXy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor=spfcoor.edit();
                coorid=coordinateDatas.get(position).getId();
                editor.putLong("coorid",coorid);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        buildersearch.setTitle("经纬度查询");
        buildersearch.setPositiveButton("查看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                double maplatitude = 0;
                double maplongitude = 0;
                String name="";
                if(datastyle==LatStyle.XY){
                    if(!editx.getText().toString().isEmpty() && !edity.getText().toString().isEmpty()){
                        CoordinateData coordinateData=data.findOrerById(coorid);
                        double x=Double.parseDouble(editx.getText().toString())+coordinateData.getDifx();
                        double y=Double.parseDouble(edity.getText().toString())+coordinateData.getDify();
                        double[] ll= GaussXYDeal.GaussToBL(x,y,coordinateData.getMidlat());
                        maplatitude=ll[0];
                        maplongitude=ll[1];
                    }
                name=edtXyName.getText().toString();
                }
                else if (datastyle == LatStyle.DEGREE) {
                    if (!edtLatitude.getText().toString().isEmpty() && !edtLongtitude.getText().toString().isEmpty()) {
                        maplatitude = Double.parseDouble(edtLatitude.getText().toString());
                        maplongitude = Double.parseDouble(edtLongtitude.getText().toString());
                        name=edtName.getText().toString();
                    }
                } else {
                    double latdu;
                    double latfen;
                    double latmiao;
                    if (!latitudedu.getText().toString().equals("")) {
                        latdu = Double.parseDouble(latitudedu.getText().toString());
                    } else {
                        latdu = 0;
                    }
                    if (!latitudefen.getText().toString().equals("")) {
                        latfen = Double.parseDouble(latitudefen.getText().toString());
                    } else {
                        latfen = 0;
                    }
                    if (!latitudemiao.getText().toString().equals("")) {
                        latmiao = Double.parseDouble(latitudemiao.getText().toString());
                    } else {
                        latmiao = 0;
                    }
                    maplatitude = ConvertLatlng.convertToDecimal(latdu, latfen, latmiao);
                    double londu;
                    double lonfen;
                    double lonmiao;
                    if (!longtitudedu.getText().toString().equals("")) {
                        londu = Double.parseDouble(longtitudedu.getText().toString());
                    } else {
                        londu = 0;
                    }
                    if (!longtitudefen.getText().toString().equals("")) {
                        lonfen = Double.parseDouble(longtitudefen.getText().toString());
                    } else {
                        lonfen = 0;
                    }
                    if (!longtitudemiao.getText().toString().equals("")) {
                        lonmiao = Double.parseDouble(longtitudemiao.getText().toString());
                    } else {
                        lonmiao = 0;
                    }
                    maplongitude = ConvertLatlng.convertToDecimal(londu, lonfen, lonmiao);
                    name=edtName.getText().toString();
                }
                if(maplatitude<-90|maplatitude>90){
                    Toast.makeText(MainMapsActivity.this,"纬度取值范围-90-90",Toast.LENGTH_LONG).show();
                    return;
                }
                if(maplongitude<-180|maplongitude>180){
                    Toast.makeText(MainMapsActivity.this,"经度取值范围-180-180",Toast.LENGTH_LONG).show();
                    return;
                }
                GeoPoint wgs=null;
                GeoPoint gcj=null;
                if (datastyle==LatStyle.XY){
                     wgs = Location3TheConvert.ConverToWGS84(maplatitude, maplongitude, 0);
                     gcj = Location3TheConvert.ConverToGCJ2(maplatitude, maplongitude, 0);
                }else {
                     wgs = Location3TheConvert.ConverToWGS84(maplatitude, maplongitude, coordstyle);
                     gcj = Location3TheConvert.ConverToGCJ2(maplatitude, maplongitude, coordstyle);
                }
                PointData pointData = new PointData();
                pointData.setAddress("");
                DecimalFormat df = new DecimalFormat("0.0000000");
                pointData.setName(name);
                pointData.setLatitude(String.valueOf(df.format(maplatitude)));
                pointData.setLongitude(String.valueOf(df.format(maplongitude)));
                pointData.setGcjlatitude(String.valueOf(df.format(gcj.getLatitude())));
                pointData.setGcjlongitude(String.valueOf(df.format(gcj.getLongitude())));
                pointData.setWgslatitude(String.valueOf(df.format(wgs.getLatitude())));
                pointData.setWgslongitude(String.valueOf(df.format(wgs.getLongitude())));
                data.createPointData(data.findOrderByName("我的收藏"), pointData);
                data.createShowdata(pointData);
              /*  ShowData showData = new ShowData();
                showData.setTitle(pointData.getName());
                //
                showData.setBaidulatitude(String.valueOf(df.format(gcj.getLatitude())));
                showData.setBaidulongitude(String.valueOf(df.format(gcj.getLongitude())));
                showData.setWgslatitude(String.valueOf(df.format(wgs.getLatitude())));
                showData.setWgslongitude(String.valueOf(df.format(wgs.getLongitude())));
                showData.setStyle(ShowPointStyle.PONIT);
                showData.setPointid(pointData.getId());
                showData.setFileid(data.findOrderByName("我的收藏").getId());*/
                PointDataParcel pp = setPointdataParcel(data.findOrderByName("我的收藏").getId(), pointData);
            /*    data.getmDaoSession().getShowDataDao().insert(showData);*/
                switch (mapcoordinate) {
                    case Location3TheConvert.Baidu:
                        bdfragment.addMark(new LatLng(gcj.getLatitude(),gcj.getLongitude()), pp);
                        break;
                    case Location3TheConvert.GCJ2:
                        osfragment.addMark(gcj, pp);
                        break;
                    case Location3TheConvert.WGS84:
                        osfragment.addMark(wgs, pp);
                        break;
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
   private boolean FabIsShow=false;
    private boolean FabDo=false;
public void showFabDo(){
        if(FabDo){
            FabDo=false;
            Animation sacleAnim = AnimationUtils.loadAnimation(this, R.anim.rl_top_out);
            rlArea.startAnimation(sacleAnim);
            rlArea.setVisibility(View.GONE);
            fabUndo.hide();
            fabSure.hide();
            fabCancle.hide();
            fabLocation.show();

        }else {
            FabDo=true;
            Animation sacleAnim = AnimationUtils.loadAnimation(this, R.anim.rl_top_in);
            rlArea.startAnimation(sacleAnim);
            rlArea.setVisibility(View.VISIBLE);
            if(bdfragment.distance==1){
                tvarea.setText("距离为：0米");
            }else if(bdfragment.distance==2){
                tvarea.setText("面积为：0平方米");
            }

            fabSure.show();
            fabUndo.show();
            fabCancle.show();
            fabLocation.hide();
        }
    }

private void showFab(){
    if(FabIsShow){
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.float_rotateclose);
        operatingAnim.setFillAfter(true);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        fabLocation.startAnimation(operatingAnim);
        FabIsShow=false;
        fabPoint.hide();
        fabDistance.hide();
        fabArea.hide();
    }else {
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.float_rotateopen);
        operatingAnim.setFillAfter(true);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        fabLocation.startAnimation(operatingAnim);
        FabIsShow=true;
        fabPoint.show();
        fabDistance.show();
        fabArea.show();
    }

}
private int huhLOGINSUCCESS=1;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_location:
                if(mapcoordinate==Location3TheConvert.Baidu){
                    showFab();
                }else {
                    popupWindow = showpopupwindow();

                }
                break;
            case R.id.fab_point:
                showFab();
                popupWindow = showpopupwindow();
                break;
            case R.id.fab_distance:
                bdfragment.distanceOpen();
                showFab();
                showFabDo();
                break;
            case R.id.fab_area:
                if(mapstyle<2){
                    bdfragment.areaOpen();
                }
                showFab();
                showFabDo();
                break;
            case R.id.fab_undo:
                bdfragment.undoArea();
                bdfragment.moveIcon.setVisibility(View.GONE);
                break;
            case R.id.fab_cancel:
                bdfragment.clearArea();
                   break;
            case R.id.fab_sure:
                showMeasure();
                break;
            case R.id.btn_main_pointsave:
                TAG_SAVEPOINT = false;
                tvCenter.setVisibility(View.GONE);
                ivCenter.setVisibility(View.GONE);
                bdSavePoint();
                appxBannerContainer.setVisibility(View.VISIBLE);
                mapPoint.setVisibility(View.GONE);
                if(data.spfOlMapSet.getInt(SpfOlMap.MAPTYPE,1)==2){
                    bottomNavigationBar.setVisibility(View.VISIBLE);
                }
                fabLocation.show();
                break;
            case R.id.btn_main_cancel:
                TAG_SAVEPOINT = false;
                tvCenter.setVisibility(View.GONE);
                ivCenter.setVisibility(View.GONE);
                appxBannerContainer.setVisibility(View.VISIBLE);
                if(data.spfOlMapSet.getInt(SpfOlMap.MAPTYPE,1)==2){
                    bottomNavigationBar.setVisibility(View.VISIBLE);
                }
                mapPoint.setVisibility(View.GONE);
                bdfragment.mBaiduMap.setOnMapStatusChangeListener(bdfragment.mClusterManager);
                fabLocation.show();
                break;
            case R.id.fab_user:
               presenter = new SplashPresenter(this);
               presenter.startMain();
           /*   Intent intent =new Intent();
              intent.setClass(this,TracingActivity.class);
              startActivity(intent);*/
                break;
        }
    }
    private void showMeasure() {
                 switch (bdfragment.measureDistance.getType()){
                     case 1:
                         if(bdfragment.measureDistance.measurePoints.size()<2){
                             Toast.makeText(MainMapsActivity.this,"点数少于2，无法生成线段",Toast.LENGTH_SHORT).show();
                             return;
                         }
                         break;
                     case 2:
                         if(bdfragment.measureDistance.measurePoints.size()<3){
                             Toast.makeText(MainMapsActivity.this,"点数少于3，无法生成面积",Toast.LENGTH_SHORT).show();
                             return;
                         }
                         break;
                 }
                Intent intent=new Intent();
                intent.putExtra(MeasureActivity.SELECTACTIVITY,"mainActivity");
                intent.putExtra(MeasureActivity.PARCELDATA,bdfragment.measureDistance);
                intent.setClass(MainMapsActivity.this,MeasureActivity.class);
                startActivityForResult(intent,REQUESTMeasrue);
    }

    public void bdSavePoint(){
        DecimalFormat df = new DecimalFormat("0.0000000");
        PointDataParcel pointData = new PointDataParcel();
        pointData.setAddress(pointAdress);
        pointData.setGcjLatitude(String.valueOf(df.format(pointlat)));
        pointData.setGcjLongitude(String.valueOf(df.format(pointlng)));
        if (mapcoordinate==Location3TheConvert.Baidu){
            JZLocationConverter.LatLng wgsLng = JZLocationConverter.gcj02ToWgs84(new JZLocationConverter.LatLng(pointlat, pointlng));
            pointData.setWgsLatitude(String.valueOf(df.format(wgsLng.getLatitude())));
            pointData.setWgsLongitude(String.valueOf(df.format(wgsLng.getLongitude())));
        }else {
            pointData.setWgsLatitude(String.valueOf(df.format(wgscenterlat)));
            pointData.setWgsLongitude(String.valueOf(df.format(wgscenterlon)));
        }
        pointData.setPointname(null);
        pointData.setActivity(WayponitActivity.MAIACTIVTY);
        Intent intent = new Intent();
        intent.putExtra(WayponitActivity.POINTDATA, pointData);
        if(data.spfOlMapSet.getInt(SpfOlMap.MAPTYPE,1)==2){
            intent.setClass(MainMapsActivity.this, OlWayActivity.class);
        }else {
            intent.setClass(MainMapsActivity.this, WayponitActivity.class);
        }

        startActivity(intent);
        bdfragment.mBaiduMap.setOnMapStatusChangeListener(bdfragment.mClusterManager);
    }
    private View showpopupwindow() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.locationpopupwindow, null);
        popupWindowDismiss = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        DisplayLocationInfo(view,bdLocationForpw, gcjLng);
        popupWindowDismiss.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindowDismiss.setOutsideTouchable(true);
       //popupWindowDismiss.setAnimationStyle(R.style.anim_popupwindow_center);
        popupWindowDismiss.showAsDropDown(toolbar);
        popupWindowDismiss.setFocusable(true);
        return view;

    }

    private int SELECT = 0;

    public void DisplayLocationInfo(View view, BDLocation locationInfo, final LatLng gcjLng) {
        if (locationInfo == null) {
            Toast.makeText(MainMapsActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
        } else {
            TextView tvtime = view.findViewById(R.id.et_address);
            TextView tvNotice = view.findViewById(R.id.et_notice_text);
            final TextView tvLat = view.findViewById(R.id.et_lat_text);
            final TextView tvLong = view.findViewById(R.id.et_lon_text);
            TextView tvAccuracy = view.findViewById(R.id.et_accuracy_text);
            TextView tvSpeed = view.findViewById(R.id.et_speed_text);
            TextView tvAltitude = view.findViewById(R.id.et_altitude_text);
            TextView tvDerection = view.findViewById(R.id.et_direction_text);
            final TextView tvwgs = view.findViewById(R.id.tv_wgs);
            final TextView tvbaidu = view.findViewById(R.id.tv_baidu);
            final TextView tvlatitude = view.findViewById(R.id.tv_latitude);
            final TextView tvlongitude = view.findViewById(R.id.tv_longtitude);
            address=locationInfo.getTime();
            String notice;
            //0 gps 1 net;
            Boolean isGps=false;
            if (locationInfo.hasSateNumber()) {
                isGps=false;
                notice = "GPS未开启";
            } else {
                isGps=true;
                notice = "GPS已开启";
            }
            tvtime.setText(address);
            tvNotice.setText(notice);
            DecimalFormat dfthree = new DecimalFormat("0.0");
            if ( isGps) {
                altitude = String.valueOf(dfthree.format(locationInfo.getAltitude()));
                tvAltitude.setText(altitude + "米");//海拔
                tvAccuracy.setText(String.valueOf(dfthree.format(locationInfo.getRadius())) + "米");
                tvSpeed.setText(String.valueOf(dfthree.format(locationInfo.getSpeed())) + "km/h");
                tvDerection.setText(String.valueOf(dfthree.format(locationInfo.getDirection())));
            }else {
                tvAltitude.setText( "无");//海拔
                tvAccuracy.setText(String.valueOf(dfthree.format(locationInfo.getRadius())) + "米");
                tvSpeed.setText("无");
                tvDerection.setText("无");
            }
            final DecimalFormat df = new DecimalFormat("0.0000000");
            final String wgsLatitude;
            final String wgsLongitude;
            final String gcjLatitude;
            final String gcjLongitude;
            final String baiduLatidude = String.valueOf((df.format(bdLng.latitude)));
            final String baiduLongitude = String.valueOf(df.format(bdLng.longitude));
            gcjLatitude = String.valueOf(df.format(gcjLng.latitude));
            gcjLongitude = String.valueOf(df.format(gcjLng.longitude));
            wgsLatitude = String.valueOf((df.format(wgsLng.getLatitude())));
            wgsLongitude = String.valueOf(df.format(wgsLng.getLongitude()));
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            final String  format=prefs.getString("coordinatedisplayformat","1");
                if (SELECT == 0) {
                tvlatitude.setText("WGS纬度");
                tvlongitude.setText("WGS经度");
                tvwgs.setTextColor(getResources().getColor(R.color.white));
                tvbaidu.setTextColor(getResources().getColor(R.color.colorPrimary));

                    if(format.equals("1")){
                        tvLat.setText(wgsLatitude);
                        tvLong.setText(wgsLongitude);
                    }else {
                        tvLat.setText(ConvertLatlng.convertToSexagesimal(Double.parseDouble(wgsLatitude)));
                        tvLong.setText(ConvertLatlng.convertToSexagesimal(Double.parseDouble(wgsLongitude)));
                    }

            } else {
                    tvlatitude.setText("baidu纬度");
                    tvlongitude.setText("baidu经度");
                    tvbaidu.setTextColor(getResources().getColor(R.color.white));
                    tvwgs.setTextColor(getResources().getColor(R.color.colorPrimary));
                    if (format.equals("1")) {
                        tvLat.setText(baiduLatidude);
                        tvLong.setText(baiduLongitude);
                    } else {
                        tvLat.setText(ConvertLatlng.convertToSexagesimal(bdLng.latitude));
                        tvLong.setText(ConvertLatlng.convertToSexagesimal(Double.parseDouble(baiduLongitude)));
                    }
                }

            Button btnSave = view.findViewById(R.id.btn_save);
            Button btnSaveByMove = view.findViewById(R.id.btn_save_move);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PointDataParcel pointData = new PointDataParcel();
                    pointData.setAddress("");
                    pointData.setWgsLatitude(wgsLatitude);
                    pointData.setWgsLongitude(wgsLongitude);
                    pointData.setGcjLongitude(gcjLongitude);
                    pointData.setGcjLatitude(gcjLatitude);
                    pointData.setAltitude(altitude);
                    pointData.setPointname(address);
                    pointData.setActivity(WayponitActivity.MAIACTIVTY);
                    Intent intent = new Intent();
                    intent.putExtra(WayponitActivity.POINTDATA, pointData);
                    if(data.spfOlMapSet.getInt(SpfOlMap.MAPTYPE,1)==2){
                        intent.setClass(MainMapsActivity.this, OlWayActivity.class);
                    }else {
                        intent.setClass(MainMapsActivity.this, WayponitActivity.class);
                    }

                    startActivity(intent);

                }
            });
            btnSaveByMove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TAG_SAVEPOINT = true;
                    popupWindowDismiss.dismiss();
                    tvCenter.setVisibility(View.VISIBLE);
                    ivCenter.setVisibility(View.VISIBLE);
                    bottomNavigationBar.setVisibility(View.GONE);
                    fabLocation.hide();
                    if(mapcoordinate==Location3TheConvert.Baidu){
                        LatLng gcjLatLng=bdfragment.mBaiduMap.getMapStatus().target;
                        pointlat =gcjLatLng.latitude;
                        pointlng = gcjLatLng.longitude;
                        centerdistance = Computer.distanceFomat(DistanceUtil.getDistance(gcjLng,gcjLatLng));
                        DecimalFormat df = new DecimalFormat("0.0000000");
                        tvCenter.setText("gcj:"+String.valueOf(df.format(pointlat)) + "," + String.valueOf(df.format(pointlng)));
                        getaddress(gcjLatLng);
                    }else {
                        IGeoPoint pointCenter=osfragment.mapView.getMapCenter();
                        Double distance = Distance.GetDistance(getLatLng().getLatitude(),getLatLng().getLongitude(),pointCenter.getLatitude(),
                                pointCenter.getLongitude());
                        centerdistance=Computer.distanceFomat(distance);
                        DecimalFormat df = new DecimalFormat("0.0000000");
                        tvCenter.setText(String.valueOf(df.format(pointCenter.getLatitude())) + "," + String.valueOf(df.format(pointCenter.getLongitude())));
                        GeoPoint gcj=Location3TheConvert.ConverToGCJ2(pointCenter.getLatitude(),pointCenter.getLongitude(),mapcoordinate);
                        GeoPoint wgs=Location3TheConvert.ConverToWGS84(pointCenter.getLatitude(),pointCenter.getLongitude(),mapcoordinate);
                        wgscenterlat=wgs.getLatitude();
                        wgscenterlon=wgs.getLongitude();
                        getaddress(new LatLng(gcj.getLatitude(),gcj.getLongitude()));
                    }
                    bdfragment.mBaiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
                    mapPoint.setVisibility(View.VISIBLE);
                    osfragment.mapView.setMapListener(new DelayedMapListener(new MapListener() {
                        @Override
                        public boolean onScroll(ScrollEvent event) {
                            if (TAG_SAVEPOINT) {
                                IGeoPoint pointCenter=osfragment.mapView.getMapCenter();
                                Double distance = Distance.GetDistance(getLatLng().getLatitude(),getLatLng().getLongitude(),pointCenter.getLatitude(),
                                        pointCenter.getLongitude());
                                centerdistance=Computer.distanceFomat(distance);
                                DecimalFormat df = new DecimalFormat("0.0000000");
                                tvCenter.setText(String.valueOf(df.format(pointCenter.getLatitude())) + "," + String.valueOf(df.format(pointCenter.getLongitude())));
                                GeoPoint wgs=Location3TheConvert.ConverToWGS84(pointCenter.getLatitude(),pointCenter.getLongitude(),mapcoordinate);
                                GeoPoint gcj=Location3TheConvert.ConverToGCJ2(pointCenter.getLatitude(),pointCenter.getLongitude(),mapcoordinate);
                                pointlat =gcj.getLatitude();
                                pointlng = gcj.getLongitude();
                                wgscenterlat=wgs.getLatitude();
                                wgscenterlon=wgs.getLongitude();
                                getaddress(new LatLng(gcj.getLatitude(),gcj.getLongitude()));
                            }
                            return true;
                        }
                        @Override
                        public boolean onZoom(ZoomEvent event) {
                            return false;
                        }
                    },200));
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
                    tvLat.setText(String.valueOf((wgsLatitude)));
                    tvLong.setText(String.valueOf((wgsLongitude)));
                    if(format.equals("1")){
                        tvLat.setText(wgsLatitude);
                        tvLong.setText(wgsLongitude);
                    }else {
                        tvLat.setText(ConvertLatlng.convertToSexagesimal(Double.parseDouble(wgsLatitude)));
                        tvLong.setText(ConvertLatlng.convertToSexagesimal(Double.parseDouble(wgsLongitude)));
                    }
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
                    if (format.equals("1")) {
                        tvLat.setText(baiduLatidude);
                        tvLong.setText(baiduLongitude);
                    } else {
                        tvLat.setText(ConvertLatlng.convertToSexagesimal(Double.parseDouble(baiduLatidude)));
                        tvLong.setText(ConvertLatlng.convertToSexagesimal(Double.parseDouble(baiduLongitude)));
                    }
                }
            });
        }
    }
    private void getaddress(LatLng bdlat) {

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
                Toast.makeText(MainMapsActivity.this, "抱歉，未能找到结果",
                        Toast.LENGTH_LONG).show();
            }
            pointAdress = result.getAddress();
            tv_mappoint_adress.setText(centerdistance + pointAdress);
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
    BaiduMap.OnMapStatusChangeListener onMapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {

        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {
            if (TAG_SAVEPOINT) {
                // previous.setPosition(status.target);
                centerdistance = Computer.distanceFomat(DistanceUtil.getDistance(gcjLng, mapStatus.target));
                DecimalFormat df = new DecimalFormat("0.0000000");
                pointlat = mapStatus.target.latitude;
                pointlng = mapStatus.target.longitude;
                tvCenter.setText("gcj:" + String.valueOf(df.format(mapStatus.target.latitude)) + "," + String.valueOf(df.format(mapStatus.target.longitude)));
                getaddress(new LatLng(pointlat, pointlng));
            }
        }

        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

        }


        /**
         * 地图状态变化中
         * @param status 当前地图状态
         */
        public void onMapStatusChange(MapStatus status) {
            if (TAG_SAVEPOINT) {
                centerdistance = Computer.distanceFomat(DistanceUtil.getDistance(gcjLng, status.target));
                DecimalFormat df = new DecimalFormat("0.0000000");
                tvCenter.setText("gcj:"+String.valueOf(df.format(status.target.latitude)) + "," + String.valueOf(df.format(status.target.longitude)));
                pointAdress = "正在解析地址";
                tv_mappoint_adress.setText(centerdistance + pointAdress);
            }
        }

        /**
         * 地图状态改变结束
         * @param status 地图状态改变结束后的地图状态
         */
        public void onMapStatusChangeFinish(MapStatus status) {
            if (TAG_SAVEPOINT) {
                centerdistance = Computer.distanceFomat(DistanceUtil.getDistance(gcjLng, status.target));
                DecimalFormat df = new DecimalFormat("0.0000000");
                tvCenter.setText("gcj:"+String.valueOf(df.format(status.target.latitude)) + "," + String.valueOf(df.format(status.target.longitude)));
                pointlat = status.target.latitude;
                pointlng = status.target.longitude;
                getaddress(new LatLng(pointlat, pointlng));

            }
        }
    };
    private DataAdpter myadpter;
    ListView listview;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        /*MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(menuItem);
        listview= findViewById(R.id.list_search);
        myadpter=new DataAdpter(this);
        listview.setAdapter(myadpter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchaddress(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               searchaddress(newText);
                return true;
            }
        });
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                listdata.clear();
                myadpter.notifyDataSetChanged();
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                return true;  // Return true to expand action view
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LatLng bd= (LatLng) listdata.get(position).get("latlng");
                GeoPoint gcj=new GeoPoint(bd.latitude,bd.longitude);
                PointData pointData = new PointData();
                pointData.setAddress((String) listdata.get(position).get("address"));
                DecimalFormat df = new DecimalFormat("0.0000000");
                pointData.setGcjlatitude(df.format(bd.latitude));
                pointData.setGcjlongitude(String.valueOf(df.format(bd.longitude)));
                GeoPoint wgs = Location3TheConvert.ConverToWGS84(bd.latitude, bd.longitude,Location3TheConvert.GCJ2);
                pointData.setWgslatitude(String.valueOf(df.format(wgs.getLatitude())));
                pointData.setWgslongitude(String.valueOf(df.format(wgs.getLongitude())));
                pointData.setName((String) listdata.get(position).get("name"));
                data.createPointData(data.findOrderByName("我的收藏"), pointData);
                ShowData showData = new ShowData();
                showData.setTitle(pointData.getName());
                //
                showData.setBaidulatitude(String.valueOf(df.format(bd.latitude)));
                showData.setBaidulongitude(String.valueOf(df.format(bd.longitude)));
                showData.setWgslatitude(String.valueOf(df.format(wgs.getLatitude())));
                showData.setWgslongitude(String.valueOf(df.format(wgs.getLongitude())));

                showData.setPointid(pointData.getId());
                showData.setFileid(data.findOrderByName("我的收藏").getId());
                PointDataParcel pp = setPointdataParcel(data.findOrderByName("我的收藏").getId(), pointData);
                data.getmDaoSession().getShowDataDao().insert(showData);
                listdata.clear();
                myadpter.notifyDataSetChanged();
                switch (mapcoordinate) {
                    case Location3TheConvert.Baidu:
                        bdfragment.addMark(bd, pp);
                        break;
                    case Location3TheConvert.GCJ2:
                        osfragment.addMark(gcj, pp);
                        break;
                    case Location3TheConvert.WGS84:
                        osfragment.addMark(wgs, pp);
                        break;
                }

            }
        });*/
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!bdLocationClient.isStarted()){
            bdLocationClient.restart();
        }
        StatService.onResume(this);

    }

    @Override
    protected void onDestroy() {
        if(bdLocationClient!=null){
            bdLocationClient.stop();
        }
        BaiduMapNavigation.finish(this);
        dialog.dismiss();
        mPoiSearch.destroy();
        BitmapUtil.clear();
        super.onDestroy();
    }


    public  BDLocation currentlocation;

    public JZLocationConverter.LatLng getLatLng() {
        JZLocationConverter.LatLng latLng = null;
        switch (mapcoordinate) {
            case Location3TheConvert.Baidu:
                latLng = new JZLocationConverter.LatLng(gcjLng.latitude,gcjLng.longitude);
                break;
            case Location3TheConvert.GCJ2:
                latLng = new JZLocationConverter.LatLng(gcjLng.latitude,gcjLng.longitude);
                break;
            case Location3TheConvert.WGS84:
                latLng =  JZLocationConverter.gcj02ToWgs84(new JZLocationConverter.LatLng(currentlocation.getLatitude(), currentlocation.getLongitude()));
                             break;
        }
        return latLng;
    }

    public boolean isFirstLoc = true;

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST) {
                Bundle mBundle;
                mBundle = data.getExtras();
                int type = mBundle.getInt(MapsType.MAPNAME);
                if (type > 1) {
                    mViewPager.setCurrentItem(1);
                    if(FabIsShow){
                        showFab();

                    }
                    if(FabDo){
                        showFabDo();
                    }
                } else {
                    mViewPager.setCurrentItem(0);
                }
                switch (type) {
                    case 0:
                        if(mapcoordinate!=Location3TheConvert.Baidu){
                            bdfragment.showdata();
                        }
                        SharedPreferences.Editor editor = spfmaps.edit();
                        mapcoordinate = Location3TheConvert.Baidu;
                        mapstyle=0;
                        editor.putInt("coordiname", mapcoordinate);
                        editor.putInt("mapstyle", 0);
                        editor.commit();
                        bdfragment.mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                        break;
                    case 1:
                        if(mapcoordinate!=Location3TheConvert.Baidu){
                            bdfragment.showdata();
                        }
                        SharedPreferences.Editor editor1 = spfmaps.edit();
                        mapcoordinate = Location3TheConvert.Baidu;
                        mapstyle=1;
                        editor1.putInt("coordiname", mapcoordinate);
                        editor1.putInt("mapstyle", 1);
                        editor1.commit();
                        bdfragment.mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                        break;
                    case 2:
                        SharedPreferences.Editor editor2 = spfmaps.edit();
                        mapcoordinate = Location3TheConvert.GCJ2;
                        mapstyle=2;
                        editor2.putInt("mapstyle", 2);
                        editor2.putInt("coordiname", mapcoordinate);
                        editor2.commit();
                        osfragment.mapView.setTileSource(new GoogleMaps());
                        osfragment.showdata(mapcoordinate);
                        break;
                    case 3:
                        SharedPreferences.Editor editor3 = spfmaps.edit();
                        mapcoordinate = Location3TheConvert.GCJ2;
                        mapstyle=3;
                        editor3.putInt("mapstyle", 3);
                        editor3.putInt("coordiname", mapcoordinate);
                        editor3.commit();
                        osfragment.mapView.setTileSource(new GoolgeMapsTerrain());
                        osfragment.showdata(mapcoordinate);
                        break;
                    case 4:
                        SharedPreferences.Editor editor4 = spfmaps.edit();
                        mapcoordinate = Location3TheConvert.GCJ2;
                        mapstyle=4;
                        editor4.putInt("mapstyle", 4);
                        editor4.putInt("coordiname", mapcoordinate);
                        editor4.commit();
                        osfragment.mapView.setTileSource(new GoogleMapsSatellite());
                        osfragment.showdata(mapcoordinate);
                        break;
                    case 5:
                        SharedPreferences.Editor editor5 = spfmaps.edit();
                        mapcoordinate = Location3TheConvert.GCJ2;
                        mapstyle=5;
                        editor5.putInt("mapstyle", 5);
                        editor5.putInt("coordiname", mapcoordinate);
                        editor5.commit();
                        osfragment.mapView.setTileSource(new GoogleMapsTileSource());
                        osfragment.showdata(mapcoordinate);
                        break;
                    case 6:
                        SharedPreferences.Editor editor6 = spfmaps.edit();
                        mapcoordinate = Location3TheConvert.WGS84;
                        mapstyle=6;
                        editor6.putInt("mapstyle", 6);
                        editor6.putInt("coordiname", mapcoordinate);
                        editor6.commit();
                        osfragment.mapView.setTileSource(new BingMapsTileSource());
                        osfragment.showdata(mapcoordinate);
                        break;
                    case 7:
                        SharedPreferences.Editor editor7 = spfmaps.edit();
                        mapcoordinate = Location3TheConvert.GCJ2;
                        mapstyle=7;
                        editor7.putInt("mapstyle", 7);
                        editor7.putInt("coordiname", mapcoordinate);
                        editor7.commit();
                        osfragment.mapView.setTileSource(new GaoDeMapsTileSource());
                        osfragment.showdata(mapcoordinate);
                        break;
                    case 8:
                        SharedPreferences.Editor editor8 = spfmaps.edit();
                        mapcoordinate = Location3TheConvert.WGS84;
                        mapstyle=8;
                        editor8.putInt("mapstyle", 8);
                        editor8.putInt("coordiname", mapcoordinate);
                        editor8.commit();
                        osfragment.mapView.setTileSource(new TianDituSatellite());
                        osfragment.showdata(mapcoordinate);
                        break;
                    case 9:
                        SharedPreferences.Editor editor9 = spfmaps.edit();
                        mapcoordinate = Location3TheConvert.WGS84;
                        mapstyle=9;
                        editor9.putInt("mapstyle", 9);
                        editor9.putInt("coordiname", mapcoordinate);
                        editor9.commit();
                        osfragment.mapView.setTileSource(new TianDituMapsTile());
                        osfragment.showdata(mapcoordinate);
                        break;
                }
                osfragment.mapView.invalidate();
            }
            else if(requestCode==REQUESTMeasrue){
                bdfragment.clearmeare();
                bdfragment.setlocation(data);
            }else if(requestCode==REQUESTDATASERVER){
                  getOlmapdata();
            }
        }
        if (LOGIN_RESULT_CODE == requestCode) {
            Log.d("tag", "login error no " + TLSService.getInstance().getLastErrno());
            if (0 == TLSService.getInstance().getLastErrno()){
                String id = TLSService.getInstance().getLastUserIdentifier();
                UserInfo.getInstance().setId(id);
                UserInfo.getInstance().setUserSig(TLSService.getInstance().getUserSig(id));
                navToHome();
            } else if (resultCode == RESULT_CANCELED){
            }
        }
    }

    public PointDataParcel setPointdataParcel(long fileID, PointData pointData) {
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
        pp.setFileid(fileID);
        return pp;
    }
    public PointDataParcel setPointdataParcel(Olfiles files, PointData pointData) {
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
    //调起百度地图导航
    public void startnavi(final LatLng latLng) {
        LayoutInflater inflater=LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.popmap, null);
        ImageView Baidu=view.findViewById(R.id.img_baidu);
        ImageView Gaode=view.findViewById(R.id.img_gaode);
        if(!isAvilible(this, "com.autonavi.minimap")){
            Gaode.setImageResource(R.mipmap.gaode_unavailable);
        }
        if(!isAvilible(this, "com.baidu.BaiduMap")){
           Baidu.setImageResource(R.mipmap.baidu_unavailable);
        }
        LinearLayout llBaidu=view.findViewById(R.id.ll_baidu);
        LinearLayout llGaode=view.findViewById(R.id.ll_gaode);
        llBaidu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng bd=new LatLng(gcjLng.latitude,gcjLng.longitude);
                NaviParaOption para = new NaviParaOption()
                        .startPoint(bd).endPoint(latLng);
                try {
                    BaiduMapNavigation.setSupportWebNavi(true);
                    BaiduMapNavigation.openBaiduMapNavi(para, MainMapsActivity.this);
                } catch (BaiduMapAppNotSupportNaviException e) {
                    e.printStackTrace();
                    showDialog();
                }
            }
        });
        llGaode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //sourceApplication
                    Intent intent = Intent.getIntent("androidamap://navi?sourceApplication=公司的名称（随意写）&poiname=我的目的地&lat=" + latLng.latitude + "&lon=" + latLng.longitude + "&dev=0");
                    startActivity(intent);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.setView(view).show();
    }
    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }
    public void showDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OpenClientUtil.getLatestBaiduMapApp(getApplicationContext());
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
    //位置查询
    private void initdata(){
        listdata=new ArrayList<Map<String,Object>>();
    }
    public void searchaddress(String address){

//        mPoiSearch.searchInCity((new PoiCitySearchOption())
//                .city(city)
//                .keyword(address).pageNum(0).pageCapacity(20));
        mSuggestionSearch
                .requestSuggestion((new SuggestionSearchOption())
                        .keyword(address).city(city));
    }
    public class DataAdpter extends BaseAdapter {
        private LayoutInflater inflater;
        public DataAdpter(Context context) {
            inflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return listdata.size();
        }

        @Override
        public Object getItem(int position) {
            return listdata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            TextView addressname;
            TextView addresdescribe;
            view = inflater.inflate(R.layout.address_item, null);
            addressname = view.findViewById(R.id.tv_mapsname);
            addresdescribe = view.findViewById(R.id.tv_mapssize);
            String mapname= (String) listdata.get(position).get("name");
            String address= (String) listdata.get(position).get("address");
            addressname.setText(mapname);
            addresdescribe.setText(address);
            return view;
        }
    }
    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        listdata.clear();
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null) {
                if(!info.city.isEmpty()){
                    double distance = DistanceUtil.getDistance(info.pt,bdLng);
                    String dis=Computer.distanceFomat(distance);
                    String address=info.city+info.district+"("+dis+")";
                    Map<String,Object> map=new HashMap<String,Object>();
                    map.put("address",address);
                    map.put("latlng",info.pt);
                    map.put("name",info.key);
                    listdata.add(map);
                }
            }
            myadpter.notifyDataSetChanged();
        }
    }
    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
        public void onGetPoiResult(PoiResult result){
            //获取POI检索结果
            listdata.clear();
            if(result.getTotalPoiNum()>0){
                List<PoiInfo> poiInfos=result.getAllPoi();
                for (PoiInfo poiInfo:poiInfos){
                    Map<String,Object> map=new HashMap<String,Object>();
                    map.put("address",poiInfo.address);
                    map.put("latlng",poiInfo.location);
                    map.put("name",poiInfo.name);
                    listdata.add(map);

                }
                myadpter.notifyDataSetChanged();
            }
        }
        public void onGetPoiDetailResult(PoiDetailResult result){
            //获取Place详情页检索结果
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };
    BannerView bv;
    private void createAD(){

            this.bv = new BannerView(this, ADSize.BANNER, Constants.APP_ID, Constants.TXBannerID);
            // 注意：如果开发者的banner不是始终展示在屏幕中的话，请关闭自动刷新，否则将导致曝光率过低。
            // 并且应该自行处理：当banner广告区域出现在屏幕后，再手动loadAD。
            bv.setRefresh(20);
            bv.setADListener(new AbstractBannerADListener() {

                @Override
                public void onNoAD(AdError error) {
                    Log.i(
                            "AD_DEMO",
                            String.format("Banner onNoAD，eCode = %d, eMsg = %s", error.getErrorCode(),
                                    error.getErrorMsg()));
                }


                @Override
                public void onADReceiv() {
                    Log.i("AD_DEMO", "ONBannerReceive");
                }
            });
            appxBannerContainer.addView(bv);
            this.bv.loadAD();
        }
    /**
     * 跳转到主界面
     */

    SplashPresenter presenter;
    private int LOGIN_RESULT_CODE = 100;
    @Override
    public void navToHome() {
        //登录之前要初始化群和好友关系链缓存
        TIMUserConfig userConfig = new TIMUserConfig();
        userConfig.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                Intent intent = new Intent(MainMapsActivity.this, DialogActivity.class);
                startActivity(intent);
            }

            @Override
            public void onUserSigExpired() {
                    //票据过期，需要重新登录
                    new NotifyDialog().show(getString(R.string.tls_expire), getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            logout();
                        }
                    });
            }
                })
                .setConnectionListener(new TIMConnListener() {
                    @Override
                    public void onConnected() {
                    }

                    @Override
                    public void onDisconnected(int code, String desc) {
                    }

                    @Override
                    public void onWifiNeedAuth(String name) {
                    }
                });

        //设置刷新监听
        RefreshEvent.getInstance().init(userConfig);
        userConfig = FriendshipEvent.getInstance().init(userConfig);
        userConfig = GroupEvent.getInstance().init(userConfig);
        userConfig = MessageEvent.getInstance().init(userConfig);
        TIMManager.getInstance().setUserConfig(userConfig);
        LoginBusiness.loginIm(UserInfo.getInstance().getId(), UserInfo.getInstance().getUserSig(), this);
    }

    /**
     * 跳转到登录界面
     */
    @Override
    public void navToLogin() {
        Intent intent = new Intent(getApplicationContext(), PhonePwdLoginActivity.class);
        startActivityForResult(intent, LOGIN_RESULT_CODE);

    }

    /**
     * 是否已有用户登录
     */
    @Override
    public boolean isUserLogin() {
        return UserInfo.getInstance().getId()!= null && (!TLSService.getInstance().needLogin(UserInfo.getInstance().getId()));
    }

    /**
     * imsdk登录失败后回调
     */
    @Override
    public void onError(int i, String s) {
            switch (i) {
            case 6208:
                //离线状态下被其他终端踢下线
                NotifyDialog dialog = new NotifyDialog();
                dialog.show(getString(R.string.kick_logout), getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navToHome();
                    }
                });
                break;
            case 6200:
                Toast.makeText(this,getString(R.string.login_error_timeout), Toast.LENGTH_SHORT).show();
                navToLogin();
                break;
            default:
                Toast.makeText(this,getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                navToLogin();
                break;
        }

    }

    /**
     * imsdk登录成功后回调
     */
    @Override
    public void onSuccess() {
        //初始化消息监听
        MessageEvent.getInstance();
        Intent intent = new Intent( MainMapsActivity.this,  MapManagerActivity.class);
        getOlvip();
        startActivity(intent);

    }
    public void getOlvip(){
        Retrofit retrofit = BaiduHttpControl.getInstance(getApplicationContext()).getRetrofit();
        BaiduDataApi baiduDataApi = retrofit.create(BaiduDataApi.class);
        if(UserInfo.getInstance().getId()!= null){
            baiduDataApi.RxGetAllDatas(UserInfo.getInstance().getId()+"$",Constants.geomapVip,Constants.ak)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<BaiduUserMapDataBean>() {

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(BaiduUserMapDataBean baiduUserMapDataBean) {
                            List<Poi> pois=baiduUserMapDataBean.getPois();
                            if(pois!=null){
                                 Poi poi=pois.get(0);
                                SharedPreferences.Editor editor=spfVip.edit();
                                editor.putString(UserVip.USERRNAME,poi.getTitle());
                                editor.putInt(UserVip.SPFVIP,poi.getVip());
                                editor.commit();
                            }
                        }
                    });
        }

    }
    private void getOlmapdata(){
        dialog.show();
        String mapid=data.spfOlMapSet.getString(SpfOlMap.MAPID,null);
        if(mapid!=null){
            Retrofit retrofit = BaiduHttpControl.getInstance(getApplicationContext()).getRetrofit();
            BaiduDataApi baiduDataApi = retrofit.create(BaiduDataApi.class);
            List<Poi> poilist=new ArrayList<>();
            getAlldata(baiduDataApi,0,mapid,poilist);
        }
    }
    private void getAlldata(final BaiduDataApi baiduDataApi, final int page_index , final String map_id, final List<Poi> allpois){
        baiduDataApi.RxGetAllDatas(map_id+"$",page_index,200,Constants.geomapid,Constants.ak)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaiduUserMapDataBean>() {

            @Override
            public void onError(Throwable e) {

            }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
            public void onNext(BaiduUserMapDataBean baiduUserMapDataBean) {
                List<Poi> pois=baiduUserMapDataBean.getPois();
                if(pois!=null){
                    for(int i=0;i<pois.size();i++){
                        allpois.add(pois.get(i));
                    }
                    if (pois.size()<200){
                        loadData(allpois);
                        bottomNavigationBar.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }else {
                        getAlldata(baiduDataApi,page_index+1,map_id,allpois);
                    }
                }else {
                    loadData(allpois);
                    bottomNavigationBar.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }



            }
        });
    }
    private void loadData(List<Poi> pois){
          String filename=data.spfOlMapSet.getString(SpfOlMap.MAPID,null);
          Olfiles file=data.findOrderOlByName(filename);
          String mapname=data.spfOlMapSet.getString(SpfOlMap.MAPNAME,"");
          toolbar.setTitle(mapname);
           if(file!=null){
              data.deleteFile(file);
           }
           clearall();
           Olfiles files=new Olfiles();
           files.setTitle(filename);
           data.getmDaoSession().getOlfilesDao().insert(files);
           if(pois!=null){
               setData(files,pois);
           }else {
               return;
           }

           if(mapcoordinate==Location3TheConvert.Baidu){
               bdfragment.showdata();
           }else {
               osfragment.showdata(mapcoordinate);
           }
        }
 private void setData(Olfiles files,List<Poi>pois){
     data.getDb().beginTransaction();
     DecimalFormat df = new DecimalFormat("0.0000000");
     for (Poi poi:pois){
         PointData pointData = new PointData();
         if(poi.getTitle()==null){
             pointData.setName("未命名");
         }else {
             pointData.setName(poi.getTitle());
         }
         pointData.setAddress(poi.getAddress());
         double maplatitude=poi.getGcj_location()[1];
         double maplongitude=poi.getGcj_location()[0];
         pointData.setGcjlatitude(String.valueOf(maplatitude));
         pointData.setGcjlongitude(String.valueOf(maplongitude));
         pointData.setDescribe(poi.getDescribe());
         pointData.setGuid(poi.getId());
         pointData.setMarkerid(poi.getMarkerid());
         GeoPoint wgs = Location3TheConvert.ConverToWGS84(maplatitude, maplongitude, Location3TheConvert.GCJ2);
         pointData.setWgslatitude(String.valueOf(df.format(wgs.getLatitude())));
         pointData.setWgslongitude(String.valueOf(df.format(wgs.getLongitude())));
         data.createPointData(files, pointData);
         ShowData showData = new ShowData();
         showData.setTitle(pointData.getName());
         switch (poi.getDatatype()){
             case 0:
                 showData.setBaidulatitude(pointData.getGcjlatitude());
                 showData.setBaidulongitude(pointData.getGcjlongitude());
                 showData.setWgslatitude(String .valueOf(wgs.getLatitude()));
                 showData.setWgslongitude(String.valueOf(wgs.getLongitude()));
                 showData.setPointid(pointData.getId());
                 showData.setFileid(files.getId());
                 showData.setStyle(ShowPointStyle.PONIT);
                 data.getmDaoSession().getShowDataDao().insert(showData);
                 break;
             case 1:
                 SqlPolyline sqlPolyline=new SqlPolyline();
                 sqlPolyline.setName(poi.getTitle());
                 sqlPolyline.setDescribe(poi.getDescribe());
                 String[]points=poi.getPolygons().split(";");
                 List<GeoPoint> geoPointList=new ArrayList<>();
                 for(int i=0;i<points.length;i++){
                     String []point=points[i].split(",");
                     GeoPoint geoPoint=Location3TheConvert.ConverToGCJ2(Double.valueOf(point[1]),Double.valueOf(point[0]),LatStyle.BAIDUMAPSTYELE);
                     geoPointList.add(geoPoint);
                 }
                 sqlPolyline.setPoints(StringToPoint.getGeoPoints(geoPointList,LatStyle.OTHERS));
                 data.createPolyline(files, sqlPolyline);

                 showData.setPointid(sqlPolyline.getId());
                 showData.setStyle(ShowPointStyle.LINE);
                 showData.setFileid(files.getId());
                 data.getmDaoSession().getShowDataDao().insert( showData);
                 break;
             case 2:
                 SqlPolygon sqlPolygon=new SqlPolygon();
                 sqlPolygon.setName(poi.getTitle());
                 sqlPolygon.setDescribe(poi.getDescribe());
                 String[]polyGonpoints=poi.getPolygons().split(";");
                 List<GeoPoint> geoPoints=new ArrayList<>();
                 for(int i=0;i<polyGonpoints.length;i++){
                     String []point=polyGonpoints[i].split(",");
                     GeoPoint geoPoint=Location3TheConvert.ConverToGCJ2(Double.valueOf(point[1]),Double.valueOf(point[0]),LatStyle.BAIDUMAPSTYELE);
                      geoPoints.add(geoPoint);
                 }
                 sqlPolygon.setPoints(StringToPoint.getGeoPoints(geoPoints,LatStyle.OTHERS));
                 data.createPolygon(files, sqlPolygon);
                 showData.setPointid(sqlPolygon.getId());
                 showData.setStyle(ShowPointStyle.POLGON);
                 showData.setFileid(files.getId());
                 data.getmDaoSession().getShowDataDao().insert( showData);
                 break;
         }


     }
     data.getDb().setTransactionSuccessful();
     data.getDb().endTransaction();
 }
    }


package com.fanweilin.coordinatemap.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.clusterutil.clustering.Cluster;
import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
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
import com.baidu.mapapi.utils.OpenClientUtil;
import com.fanweilin.coordinatemap.Class.LatStyle;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.ConvertLatlng;
import com.fanweilin.coordinatemap.computing.DataItem;
import com.fanweilin.coordinatemap.widget.ZoomControlsView;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.ShowDataDao;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BaiduMap.OnMapLoadedCallback {
    public Toolbar toolbar;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle mDrawerToggle;
    public LocationClient mLocationClient = null;
    public BaiduMap mBaiduMap;
    public MapView mMapView;
    public BDLocationListener myListener = new MyLocationListener();
    public double latitude;
    public double lontitude;
    public ZoomControlsView zoomControlsView;
    private CoordinateConverter converter;
    private BitmapDescriptor bitmap;
    private BitmapDescriptor bitmaplocation;
    private ClusterManager<MyItem> mClusterManager;
    private ImageButton imgLayerChange;
    private ImageButton imgClearall;
    private ImageButton imgMyloncation;
    private boolean MAP_STYLE = true;
    private int coordstyle;
    private int datastyle;
    private NavigationView mNavigationView;

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
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //mAnimationDrawable.stop();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // mAnimationDrawable.start();
            }
        };
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);
        initLocation();
        mLocationClient.start();
        mLocationClient.registerLocationListener(myListener);
        showdata();
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
                                finish();
                                break;

                        }
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
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
        builder.setMessage("本软件为第一版,难免会存在一些bug和不足。如果你在使用过程中发现问题或有好的建议可以联系作者。\n897481601@qq.com");
        builder.setTitle("关于");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }

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
                        data app = (data) getApplication();
                        DaoSession mDaoSession = app.getmDaoSession();
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
        LatLngBounds mLatLngBounds;
        Cursor cursor;
        SQLiteDatabase db;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        data app = (data) getApplication();
        DaoSession mDaoSession = app.getmDaoSession();
        ShowDataDao showDataDao = mDaoSession.getShowDataDao();
        db = app.getDb();
        cursor = db.query(showDataDao.getTablename(), showDataDao.getAllColumns(), null, null, null, null, null);
        List<MyItem> items = new ArrayList<>();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                DataItem mDataItem = new DataItem();
                int CoordStyle = cursor.getInt(cursor.getColumnIndex(ShowDataDao.Properties.Cdstyle.columnName));
                int DataStyle = cursor.getInt(cursor.getColumnIndex(ShowDataDao.Properties.Datastyle.columnName));
                String name = cursor.getString(cursor.getColumnIndex(ShowDataDao.Properties.Title.columnName));
                double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(ShowDataDao.Properties.Latitude.columnName)).toString());
                double lon = Double.parseDouble(cursor.getString(cursor.getColumnIndex(ShowDataDao.Properties.Longitude.columnName)).toString());
                LatLng point = new LatLng(lat, lon);
                LatLng bdpoint = ComanLngConvertBdLngt(point, CoordStyle, DataStyle);
                mDataItem.setName(name);
                mDataItem.setLatLng(point);
                items.add(new MyItem(bdpoint, mDataItem));
                builder.include(bdpoint);
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
        name = intent.getStringExtra("activityname");
        if (name != null) {
            if (name.equals(test)) {
                LatLng point = new LatLng(intent.getDoubleExtra("Lat", 0.0), intent.getDoubleExtra("Lng", 0.0));
                int coordstyle = intent.getIntExtra("CoordStyle", 0);
                int datastyle = intent.getIntExtra("DataStyle", 0);
                Log.d("test1_main", String.valueOf(datastyle));
                LatLng baidupoint = ComanLngConvertBdLngt(point, coordstyle, datastyle);
                Bundle bundle = new Bundle();
                bundle.putInt(MAKER_STYLE, INTENT_MARKER);
                bundle.putDouble("latitude", point.latitude);
                bundle.putDouble("longitude", point.longitude);
                bundle.putString("name", intent.getStringExtra("name"));
                OverlayOptions options2 = new MarkerOptions().position(baidupoint).icon(bitmaplocation).extraInfo(bundle);
                mBaiduMap.addOverlay(options2);
                setMapCenter(baidupoint.latitude, baidupoint.longitude);
                return true;

            }
        }
        return false;
    }

    private void init() {
        converter = new CoordinateConverter();
        imgLayerChange = (ImageButton) findViewById(R.id.imgbtn_layer);
        imgClearall = (ImageButton) findViewById(R.id.imgbtn_clearall);
        imgMyloncation = (ImageButton) findViewById(R.id.imgbtn_mylocation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);
        zoomControlsView = (ZoomControlsView) findViewById(R.id.activity_main_zoomcontrols);
        mBaiduMap = mMapView.getMap();
        zoomControlsView.setBaiduMap(mMapView);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(14f));
        bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.location_pin);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        setupDrawerContent(mNavigationView);
        mClusterManager = new ClusterManager<>(this, mBaiduMap);
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(onClusterClickListener);
        mClusterManager.setOnClusterItemClickListener(onClusterItemClickListener);
        mBaiduMap.setOnMarkerClickListener(mClusterManager);
        bitmaplocation = BitmapDescriptorFactory
                .fromResource(R.mipmap.location);
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                PopupWindowmark(marker);
                return false;
            }
        });
        imgLayerChange.setOnClickListener(this);
        imgClearall.setOnClickListener(this);
        imgMyloncation.setOnClickListener(this);

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
            LatLngBounds latLngBounds = null;
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            List<MyItem> list = new ArrayList<MyItem>();
            list = (List<MyItem>) cluster.getItems();
            for (int i = 0; i < list.size(); i++) {
                builder.include(list.get(i).getPosition());
            }
            latLngBounds = builder.build();
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(latLngBounds));
            return false;
        }
    };

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
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
        mLocationClient.setLocOption(option);
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // test(location);
            if (latitude == 0.0) {
                latitude = location.getLatitude();
                lontitude = location.getLongitude();
                setMapCenter(latitude, lontitude);
            } else {
                latitude = location.getLatitude();
                lontitude = location.getLongitude();
            }
            showLocation(location);
        }

    }

    private void showLocation(BDLocation location) {
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
        mBaiduMap.setMyLocationConfigeration(config);
    }

    private void setMapCenter(double latitude, double lontitude) {
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newLatLng(new LatLng(latitude, lontitude));
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
                    intent.setClass(MainActivity.this, FileManagerActivity.class);
                    startActivity(intent);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        bitmaplocation.recycle();
        BaiduMapNavigation.finish(this);
        mMapView.onDestroy();//销毁地图
    }

    public LatLng ComanLngConvertBdLngt(LatLng lat, int coordstyle, int datastyle) {
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

    private final int INTENT_MARKER = 1;
    private final int DIALOG_MARKER = 2;
    private final String MAKER_STYLE = "marker_style";

    private void addMark(LatLng bdlat, LatLng orlat, int markerstyle) {
        Bundle bundle = new Bundle();
        bundle.putInt(MAKER_STYLE, markerstyle);
        bundle.putDouble("latitude", orlat.latitude);
        bundle.putDouble("longitude", orlat.longitude);
        OverlayOptions options2 = new MarkerOptions().position(bdlat).icon(bitmaplocation).extraInfo(bundle);
        mBaiduMap.addOverlay(options2);
        setMapCenter(bdlat.latitude, bdlat.longitude);
    }

    public void showpopupwindows(final MyItem item) {
        LayoutInflater inflater = getLayoutInflater();
        android.support.v7.widget.CardView card;
        TextView lat;
        TextView lng;
        TextView name;
        ImageView iv;
        View view = inflater.inflate(R.layout.popupwindow, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        name = (TextView) view.findViewById(R.id.tv_name);
        lat = (TextView) view.findViewById(R.id.tv_lat);
        lng = (TextView) view.findViewById(R.id.tv_lng);
        card = (CardView) view.findViewById(R.id.card_popup);
        iv = (ImageView) view.findViewById(R.id.iv_goto);
        name.setText(item.getDataItem().getName());
        DecimalFormat df = new DecimalFormat("#.0000000");
        lat.setText(String.valueOf(df.format(item.getDataItem().getLatLng().latitude)));
        lng.setText(String.valueOf(df.format(item.getDataItem().getLatLng().longitude)));
        card.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK)
                    popupWindow.dismiss();
                return false;
            }
        });
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startnavi(item.getPosition());
            }
        });
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(zoomControlsView);
        popupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
    }

    public void showpopupwindows(final Marker marker, Bundle bundle) {
        LayoutInflater inflater = getLayoutInflater();
        android.support.v7.widget.CardView card;
        TextView lat;
        TextView lng;
        TextView name;
        ImageView iv;
        View view = inflater.inflate(R.layout.popupwindow, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        name = (TextView) view.findViewById(R.id.tv_name);
        lat = (TextView) view.findViewById(R.id.tv_lat);
        lng = (TextView) view.findViewById(R.id.tv_lng);
        card = (CardView) view.findViewById(R.id.card_popup);
        iv = (ImageView) view.findViewById(R.id.iv_goto);
        if (bundle.getInt(MAKER_STYLE) == INTENT_MARKER) {
            name.setText(bundle.getString("name"));
        }
        DecimalFormat df = new DecimalFormat("#.0000000");
        lat.setText(String.valueOf(df.format(bundle.getDouble("latitude"))));
        lng.setText(String.valueOf(df.format(bundle.getDouble("longitude"))));
        card.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK)
                    popupWindow.dismiss();
                return false;
            }
        });
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startnavi(marker.getPosition());
            }
        });
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(zoomControlsView);
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
        datastyle = LatStyle.DEGREE;
        final EditText edtLatitude;
        final EditText edtLongtitude;
        RadioGroup radioGroup;
        final RadioButton radiobtnDecimal;
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_data, null);
        AlertDialog.Builder buildersearch = new AlertDialog.Builder(MainActivity.this);
        buildersearch.setView(dialogView);
        spinner = (Spinner) dialogView.findViewById(R.id.spn_cds);
        edtLatitude = (EditText) dialogView.findViewById(R.id.edt_latitude);
        edtLongtitude = (EditText) dialogView.findViewById(R.id.edt_longtitude);
        radiobtnDecimal = (RadioButton) dialogView.findViewById(R.id.radio_decimal);
        radioGroup = (RadioGroup) dialogView.findViewById(R.id.radgroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == radiobtnDecimal.getId()) {
                    datastyle = LatStyle.DEGREE;
                    edtLatitude.setHint("39.9631757745");
                    edtLongtitude.setHint("116.4002442204");
                } else {
                    datastyle = LatStyle.DMS;
                    edtLatitude.setHint("30.3452458");
                    edtLongtitude.setHint("120.1428354");

                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View view,
                                       int position, long id) {
                coordstyle = position;
            }

            @Override
            public void onNothingSelected(AdapterView parent) {
// TODO Auto-generated method stub
            }
        });
        buildersearch.setTitle("经纬度查询");
        buildersearch.setPositiveButton("查看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (edtLatitude.getText().toString().isEmpty() != true && edtLongtitude.getText().toString().isEmpty() != true) {
                    LatLng point = new LatLng(Double.parseDouble(edtLatitude.getText().toString()), Double.parseDouble(edtLongtitude.getText().toString()));
                    Log.d("showss", String.valueOf(point.latitude));
                    Log.d("showss", String.valueOf(point.longitude));
                    LatLng bd = ComanLngConvertBdLngt(point, coordstyle, datastyle);
                    addMark(bd, point, DIALOG_MARKER);
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

    private String address;

    private void getaddress(LatLng bdlat) {
        address = "";
        Log.d("address", "sfsa");
        GeoCoder geoCoder = GeoCoder.newInstance();
        //
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            // 反地理编码查询结果回调函数
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null
                        || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                    Toast.makeText(MainActivity.this, "抱歉，未能找到结果",
                            Toast.LENGTH_LONG).show();
                }
                Toast.makeText(getApplicationContext(),
                        "位置：" + result.getAddress(), Toast.LENGTH_LONG)
                        .show();
                address = result.getAddress();
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
        // 设置地理编码检索监听者
        geoCoder.setOnGetGeoCodeResultListener(listener);
        //
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(bdlat));
        Log.d("address", address);
    }

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
}
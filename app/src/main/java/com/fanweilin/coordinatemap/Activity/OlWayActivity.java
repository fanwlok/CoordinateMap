package com.fanweilin.coordinatemap.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mobstat.StatService;
import com.baidubce.BceClientException;
import com.baidubce.BceServiceException;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.callback.BosProgressCallback;
import com.baidubce.services.bos.model.BosObjectSummary;
import com.baidubce.services.bos.model.ListObjectsRequest;
import com.baidubce.services.bos.model.ListObjectsResponse;
import com.baidubce.services.bos.model.PutObjectRequest;
import com.baidubce.services.bos.model.PutObjectResponse;
import com.bumptech.glide.Glide;
import com.fanweilin.coordinatemap.Class.Marker;
import com.fanweilin.coordinatemap.Class.PointDataParcel;
import com.fanweilin.coordinatemap.Class.SpfOlMap;
import com.fanweilin.coordinatemap.Class.UserVip;
import com.fanweilin.coordinatemap.DataModel.BaiduDataApi;
import com.fanweilin.coordinatemap.DataModel.BaiduDataClass;
import com.fanweilin.coordinatemap.DataModel.BaiduHttpControl;
import com.fanweilin.coordinatemap.DataModel.Constants;
import com.fanweilin.coordinatemap.DataModel.ReasonCreate;
import com.fanweilin.coordinatemap.DataModel.RetryWithDelay;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.ConvertLatlng;
import com.fanweilin.coordinatemap.computing.JZLocationConverter;
import com.fanweilin.coordinatemap.computing.Location3TheConvert;
import com.fanweilin.coordinatemap.widget.NoScrollGridView;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.Olfiles;
import com.fanweilin.greendao.PointData;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import retrofit2.Retrofit;
import top.zibin.luban.Luban;

import static com.fanweilin.coordinatemap.R.dimen.space_size;

public class OlWayActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String MAIACTIVTY = "mainactivity";
    public static final String DATAMANAGERACTIVITY = "datananageractivty";
    private Toolbar toolbar;
    private Button btncameral;
    private ArrayList<String> imagePaths = null;
    private ArrayList<String> imagePrePaths = null;
    private NoScrollGridView gridView;
    private int columnWidth;
    private GridAdapter gridAdapter;
    public static final String POINTDATA = "pointdata";
    private AutoCompleteTextView pointname;
    private AutoCompleteTextView describe;
    private EditText wsgEdit;
    private EditText baiduEdit;
    private EditText altitude;
    private TextView tvAddress;
    private ImageButton imgBtn;

    private PointDataParcel pointData;
    private int putImageSize = 0;
    PointData mpointdata;
    Files mFiles;
    ProgressDialog dialog;
    private int REID = Marker.blue;
    List<String> urls = new ArrayList<>();
    List<String> photoPath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wayponit);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用  getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(data.spfOlMapSet.getString(SpfOlMap.MAPNAME, ""));


        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setTitle("正在上传数据");

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.waypoint_menu_save:
                        if (MAIACTIVTY.equals(pointData.getActivity())) {
                            String name = pointname.getText().toString();
                            if (name.isEmpty()) {
                                Toast.makeText(OlWayActivity.this, "名字不能为空", Toast.LENGTH_SHORT).show();
                            } else {
                                mpointdata.setMarkerid(REID);
                                mpointdata.setDescribe(describe.getText().toString());
                                mpointdata.setName(name);
                                mpointdata.setAddress(tvAddress.getText().toString());
                                putdata(mpointdata);
                            }


                        } else if (DATAMANAGERACTIVITY.equals(pointData.getActivity())) {
                            String name = pointname.getText().toString();
                            if (name.isEmpty()) {
                                Toast.makeText(OlWayActivity.this, "名字不能为空", Toast.LENGTH_SHORT).show();
                            } else {
                                mpointdata.setName(name);
                                mpointdata.setDescribe(describe.getText().toString());
                                mpointdata.setMarkerid(REID);
                                updata(mpointdata);

                            }


                        }
                        break;
                }
                return true;
            }
        });
    }

    private void updataLocal(PointData mpointdata) {
        data.updataPointdata(mpointdata);
        data.deletePictureDateByList(mpointdata.getPictureItems());
        PointDataParcel pp = new PointDataParcel();
        pp = setPointdataParcel(mpointdata, data.findOrderByName(data.currentFilename).getId());
        Intent intent = new Intent();
        intent.putExtra(MainMapsActivity.GETPOINTDATAPARCE, pp);
        intent.setClass(OlWayActivity.this, MainMapsActivity.class);
        intent.putExtra("data", "one");
        data.createShowdata(mpointdata);
        intent.putExtra(MainMapsActivity.DATAMANAGERACTIVITY, "com.fanweilin.coordinatemap.Activity.DataManagerActivity");
        startActivity(intent);
    }

    private void updata(final PointData pointData) {
        final String mapid = data.spfOlMapSet.getString(SpfOlMap.MAPID, null);
        dialog.show();
        Retrofit retrofit = BaiduHttpControl.getInstance(getApplicationContext()).getRetrofit();
        BaiduDataApi baiduDataApi = retrofit.create(BaiduDataApi.class);
        final BaiduDataClass baiduDataClass = new BaiduDataClass();
        baiduDataClass.setId(pointData.getGuid());
        baiduDataClass.setTitle(pointData.getName());
        baiduDataClass.setAddress(pointData.getAddress());
        baiduDataClass.setLatitude(Double.parseDouble(pointData.getGcjlatitude()));
        baiduDataClass.setLongitude(Double.parseDouble(pointData.getGcjlongitude()));
        baiduDataClass.setCoord_type(2);
        baiduDataClass.setDescribe(pointData.getDescribe());
        baiduDataClass.setGeotable_id(Constants.geomapid);
        baiduDataClass.setUsermap_id(mapid);
        baiduDataClass.setTags(mapid);
        baiduDataClass.setAk(Constants.ak);
        int markerid = Marker.getMarkerId(pointData);
        baiduDataApi.Rxupdate(baiduDataClass.getId(), baiduDataClass.getTitle(), baiduDataClass.getAddress(), baiduDataClass.getTags(),
                baiduDataClass.getLatitude(),
                baiduDataClass.getLongitude(), baiduDataClass.getCoord_type(),
                baiduDataClass.getDescribe(), baiduDataClass.getGeotable_id(),
                baiduDataClass.getAk(), baiduDataClass.getUsermap_id(), null, 0, markerid).
                retryWhen(new RetryWithDelay()).
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<ReasonCreate>() {
                    @Override
                    public void accept(ReasonCreate reasonCreate) throws Exception {
                        if (reasonCreate.getStatus() == 0) {
                            if (photoPath.size() > 0) {
                                putFile(getBucketName(), reasonCreate.getId(), photoPath, mapid, pointData, false);
                            } else {
                                dialog.dismiss();
                                updataLocal(pointData);
                            }

                        }

                    }

                });
    }

    private PointDataParcel setPointdataParcel(PointData pointData, long filesID) {
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
        pp.setFileid(filesID);
        return pp;
    }

    private void init() {
        gridView = findViewById(R.id.grv_photo);
        toolbar = findViewById(R.id.toolbar);
        describe = findViewById(R.id.edt_describe);
        pointname = findViewById(R.id.edt_pointname);
        btncameral = findViewById(R.id.btn_cameral);
        baiduEdit = findViewById(R.id.edt_baidu);
        wsgEdit = findViewById(R.id.edt_wgs);
        altitude = findViewById(R.id.edt_altitude);
        tvAddress = findViewById(R.id.waypoint_tv_address);
        imgBtn = findViewById(R.id.img_btn_marker);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPointMarker();
            }
        });
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 3 ? 3 : cols;
        gridView.setNumColumns(cols);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int columnSpace = getResources().getDimensionPixelOffset(space_size);
        columnWidth = (screenWidth - columnSpace * (cols - 1)) / cols;
        btncameral.setOnClickListener(this);
        initdata();
        imgBtn.setImageResource(Marker.getResource(REID));
        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PhotoPreview.builder()
                        .setPhotos(imagePaths)
                        .setCurrentItem(position)
                        .setShowDeleteButton(false)
                        .start(OlWayActivity.this);
              /*  PhotoPreviewIntent intent = new PhotoPreviewIntent(WayponitActivity.this);
                intent.setCurrentItem(position);
                intent.setPhotoPaths(imagePaths);
                startActivityForResult(intent, REQUEST_PREVIEW_CODE);*/
            }
        });

        loadAdpater(imagePrePaths);
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };

            if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, 0);
            }
        }
    }

    public void setPointMarker() {
        View view = LayoutInflater.from(this).inflate(R.layout.list_img, null);
        ImageButton imgBlue = view.findViewById(R.id.img_blue);
        ImageButton imgRed = view.findViewById(R.id.img_red);
        ImageButton imgGreen = view.findViewById(R.id.img_green);
        ImageButton imgYellow = view.findViewById(R.id.img_yellow);
        ImageButton imgZs = view.findViewById(R.id.img_zs);
        final ImageView imageView = view.findViewById(R.id.img_select);
        imageView.setImageResource(Marker.getResource(REID));

        class imgClick implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.img_blue:
                        REID = Marker.blue;
                        imageView.setImageResource(Marker.REBLUEID);
                        break;
                    case R.id.img_red:
                        REID = Marker.red;
                        imageView.setImageResource(Marker.REREDID);
                        break;
                    case R.id.img_green:
                        REID = Marker.green;
                        imageView.setImageResource(Marker.REGREENID);
                        break;
                    case R.id.img_yellow:
                        REID = Marker.yellow;
                        imageView.setImageResource(Marker.REYEID);
                        break;
                    case R.id.img_zs:
                        REID = Marker.zs;
                        imageView.setImageResource(Marker.REZS);
                        break;
                }
            }
        }

        imgBlue.setOnClickListener(new imgClick());
        imgRed.setOnClickListener(new imgClick());
        imgGreen.setOnClickListener(new imgClick());
        imgYellow.setOnClickListener(new imgClick());
        imgZs.setOnClickListener(new imgClick());


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle("设置图标颜色");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imgBtn.setImageResource(Marker.getResource(REID));

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    public void initdata() {
        mFiles = new Files();
        mpointdata = new PointData();
        Intent intent = this.getIntent();
        imagePrePaths = new ArrayList<>();
        pointData = new PointDataParcel();
        pointData = intent.getParcelableExtra(POINTDATA);
        LatLng gcjlat = null;
        if (pointData.getGcjLatitude() != null) {
            gcjlat = new LatLng(Double.parseDouble(pointData.getGcjLatitude()), Double.parseDouble(pointData.getGcjLongitude()));
        } else {
            JZLocationConverter.LatLng gcj = JZLocationConverter.wgs84ToGcj02(new JZLocationConverter.LatLng(Double.parseDouble(pointData.getWgsLatitude()), Double.parseDouble(pointData.getWgsLongitude())));
            gcjlat = new LatLng(gcj.getLatitude(), gcj.getLongitude());
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String format = prefs.getString("coordinatedisplayformat", "1");
        DecimalFormat df = new DecimalFormat("0.0000000");
        if (MAIACTIVTY.equals(pointData.getActivity())) {
            Files files = data.findOrderByName(data.currentFilename);
            if (files.getMarkerid() != null) {
                REID = files.getMarkerid();
            }
            LatLng bdLng = Location3TheConvert.ConverToBaidu(Double.parseDouble(pointData.getWgsLatitude()), Double.parseDouble(pointData.getWgsLongitude()), Location3TheConvert.WGS84);
            if (format.equals("1")) {
                wsgEdit.setText(pointData.getWgsLatitude() + "," + pointData.getWgsLongitude());
                if (pointData.getGcjLatitude() != null) {
                    baiduEdit.setText(String.valueOf(df.format(bdLng.latitude) + "," + String.valueOf(df.format(bdLng.longitude))));
                }


            } else {
                wsgEdit.setText(ConvertLatlng.convertToSexagesimal(Double.parseDouble(pointData.getWgsLatitude())) + ","
                        + ConvertLatlng.convertToSexagesimal(Double.parseDouble(pointData.getWgsLongitude())));
                if (pointData.getGcjLatitude() != null) {
                    baiduEdit.setText(ConvertLatlng.convertToSexagesimal(bdLng.latitude) +
                            "," + ConvertLatlng.convertToSexagesimal(bdLng.longitude));
                }

            }
            describe.setText(pointData.getPointname());
            altitude.setText(pointData.getAltitude());
            tvAddress.setText(pointData.getAddress());
            /**/
            if (pointData.getGcjLatitude() != null) {
                mpointdata.setGcjlatitude(pointData.getGcjLatitude());
                mpointdata.setGcjlongitude(pointData.getGcjLongitude());
            }

            mpointdata.setWgslatitude(pointData.getWgsLatitude());
            mpointdata.setWgslongitude(pointData.getWgsLongitude());


        } else if (DATAMANAGERACTIVITY.equals(pointData.getActivity())) {
            mFiles = data.findOrderById(pointData.getFileid());
            if (mFiles != null) {
                if (mFiles.getMarkerid() != null) {
                    REID = mFiles.getMarkerid();
                }
            }

            mpointdata = data.findPointDataDaoById(pointData.getPointdataid());
            getPhotoUrl(mpointdata.getGuid());
            if (mpointdata.getMarkerid() != null) {
                REID = mpointdata.getMarkerid();
            }
            if (format.equals("1")) {
                wsgEdit.setText(pointData.getWgsLatitude() + "," + pointData.getWgsLongitude());
                if (pointData.getGcjLatitude() != null) {
                    baiduEdit.setText(pointData.getGcjLatitude() + "," + pointData.getGcjLongitude());
                }
            } else {
                wsgEdit.setText(ConvertLatlng.convertToSexagesimal(Double.parseDouble(pointData.getWgsLatitude())) + ","
                        + ConvertLatlng.convertToSexagesimal(Double.parseDouble(pointData.getWgsLongitude())));
                if (pointData.getGcjLatitude() != null) {
                    baiduEdit.setText(ConvertLatlng.convertToSexagesimal(Double.parseDouble(pointData.getGcjLatitude())) +
                            "," + ConvertLatlng.convertToSexagesimal(Double.parseDouble(pointData.getGcjLongitude())));
                }
            }
            pointname.setText(pointData.getPointname());
            describe.setText(pointData.getDescribe());
            altitude.setText(pointData.getAltitude());
            tvAddress.setText(pointData.getAddress());
        }
        if (TextUtils.isEmpty(tvAddress.getText().toString())) {
            getaddress(gcjlat);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.acticty_waypoint_menu, menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cameral:
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setShowCamera(true)
                        .setShowGif(true)
                        .setPreviewEnabled(false)
                        .start(this, PhotoPicker.REQUEST_CODE);
               /* PhotoPickerIntent intent = new PhotoPickerIntent(WayponitActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult(intent, REQUEST_CODE_GET_CAMERAL);*/
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                photoPath.clear();
                photoPath.addAll(data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS));
                loadAdpater(data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS));
            }
        }

    }

    private void loadAdpater(List<String> paths) {
        if (imagePaths == null) {
            imagePaths = new ArrayList<>();
        }
        imagePaths.clear();
        imagePaths.addAll(urls);
        imagePaths.addAll(paths);
        try {
            JSONArray obj = new JSONArray(imagePaths);
            Log.e("--", obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gridAdapter == null) {
            gridAdapter = new GridAdapter(imagePaths);
            gridView.setAdapter(gridAdapter);
        } else {
            gridAdapter.notifyDataSetChanged();
        }
    }

    private class GridAdapter extends BaseAdapter {
        private ArrayList<String> listUrls;

        public GridAdapter(ArrayList<String> listUrls) {
            this.listUrls = listUrls;
        }

        @Override
        public int getCount() {
            return listUrls.size();
        }

        @Override
        public String getItem(int position) {
            return listUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_image, null);
                imageView = convertView.findViewById(R.id.imageView);
                convertView.setTag(imageView);
                // 重置ImageView宽高
                BitmapFactory.Options options = new BitmapFactory.Options();
                //设置为true,表示解析Bitmap对象，该对象不占内存
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(getItem(position), options);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(columnWidth, (int) (columnWidth));
                imageView.setLayoutParams(params);
            } else {
                imageView = (ImageView) convertView.getTag();
            }

            if (position > urls.size() - 1) {
                Glide.with(OlWayActivity.this)
                        .load(new File(getItem(position)))
                        .thumbnail(0.1f)
                        .into(imageView);
            } else {
                Glide.with(OlWayActivity.this)
                        .load(Uri.parse(imagePaths.get(position)))
                        .thumbnail(0.1f)
                        .into(imageView);
            }

            return convertView;
        }
    }

    public void onResume() {


        StatService.onResume(this);
        super.onResume();
    }

    public void onPause() {


        StatService.onPause(this);
        super.onPause();
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
                Toast.makeText(OlWayActivity.this, "抱歉，未能找到结果",
                        Toast.LENGTH_LONG).show();
            }

            tvAddress.setText(result.getAddress());
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

    private void putdata(final PointData pointData) {
        SharedPreferences spf = getSharedPreferences(UserVip.SPFNAME, Context.MODE_PRIVATE);
        int vip = spf.getInt(UserVip.SPFVIP, 1);
        final String mapid = data.spfOlMapSet.getString(SpfOlMap.MAPID, null);
        Olfiles files = data.findOrderOlByName(mapid);
        List<PointData> pointDatas = files.getPointolItems();
        if (pointDatas.size() > UserVip.getSize(vip)) {
            Toast.makeText(this, "当前用户云端数据最多" + String.valueOf(UserVip.getSize(vip)), Toast.LENGTH_SHORT).show();
        } else {
            dialog.show();
            Retrofit retrofit = BaiduHttpControl.getInstance(getApplicationContext()).getRetrofit();
            BaiduDataApi baiduDataApi = retrofit.create(BaiduDataApi.class);
            final BaiduDataClass baiduDataClass = new BaiduDataClass();
            baiduDataClass.setTitle(pointData.getName());
            baiduDataClass.setAddress(pointData.getAddress());
            baiduDataClass.setLatitude(Double.parseDouble(pointData.getGcjlatitude()));
            baiduDataClass.setLongitude(Double.parseDouble(pointData.getGcjlongitude()));
            baiduDataClass.setCoord_type(2);
            baiduDataClass.setDescribe(pointData.getDescribe());
            baiduDataClass.setGeotable_id(Constants.geomapid);
            baiduDataClass.setUsermap_id(mapid);
            baiduDataClass.setTags(mapid);
            baiduDataClass.setAk(Constants.ak);
            int markerid = Marker.getMarkerId(pointData);
            baiduDataApi.RxCreatedata(baiduDataClass.getTitle(), baiduDataClass.getAddress(), baiduDataClass.getTags(),
                    baiduDataClass.getLatitude(),
                    baiduDataClass.getLongitude(), baiduDataClass.getCoord_type(),
                    baiduDataClass.getDescribe(), baiduDataClass.getGeotable_id(),
                    baiduDataClass.getAk(), baiduDataClass.getUsermap_id(), null, 0, markerid)
                    .retryWhen(new RetryWithDelay())
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ReasonCreate>() {

                        @Override
                        public void onError(Throwable e) {
                            String error;
                            if (e instanceof TimeoutException || e instanceof SocketTimeoutException
                                    || e instanceof ConnectException) {
                                error = "网路错误";
                            } else if (e instanceof JsonSyntaxException) {
                                error = "Json格式出错了";
                                //假如导致这个异常触发的原因是服务器的问题，那么应该让服务器知道，所以可以在这里
                                //选择上传原始异常描述信息给服务器
                            } else {
                                error = e.getMessage();
                            }
                            Toast.makeText(OlWayActivity.this, error, Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ReasonCreate reasonCreate) {
                            if (reasonCreate.getStatus() == 0) {
                                if (imagePaths.size() > 0) {
                                    putFile(getBucketName(), reasonCreate.getId(), imagePaths, mapid, pointData, true);
                                } else {
                                    dialog.dismiss();
                                    savePoint(mapid, pointData, reasonCreate.getId());
                                }

                            }
                        }
                    });
        }

    }

    private void savePoint(String filename, PointData pointData, String Olid) {
        pointData.setGuid(Olid);
        data.createPointData(data.findOrderOlByName(filename), pointData);
        PointDataParcel pp = new PointDataParcel();
        pp = setPointdataParcel(mpointdata, data.findOrderOlByName(filename).getId());
        Intent intent = new Intent();
        intent.putExtra(MainMapsActivity.GETPOINTDATAPARCE, pp);
        intent.setClass(OlWayActivity.this, MainMapsActivity.class);
        intent.putExtra("data", "one");
        data.createShowdata(pointData);
        intent.putExtra(MainMapsActivity.DATAMANAGERACTIVITY, "com.fanweilin.coordinatemap.Activity.DataManagerActivity");
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            finishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void finishActivity() {
        if (MAIACTIVTY.equals(pointData.getActivity())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("退出");
            builder.setMessage("数据尚未保存是否退出");
            builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        } else {
            finish();
            ;
        }
    }

    public String getBucketName() {
        return "jwddw";
    }

    private String BJ = "bj.bcebos.com";
    private String GZ = "gz.bcebos.com";
    private String SU = "su.bcebos.com";

    public void putFile(final String BucketName, final String pointid, final List<String> filepaths, final String mapid, final PointData pointData, final boolean isNew) {
        Observable.just(filepaths).subscribeOn(Schedulers.io())
                .map(new Function<List<String>, List<File>>() {
                    @Override
                    public List<File> apply(List<String> list) throws Exception {
                        return Luban.with(OlWayActivity.this).load(list).get();
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Observer<List<File>>() {


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
                               public void onNext(List<File> files) {
                                   try {
                                       BosClientConfiguration config = new BosClientConfiguration();
                                       config.setCredentials(new DefaultBceCredentials(Constants.BOSAK, Constants.BOSSK));
                                       config.setEndpoint(GZ);    //传入Bucket所在区域域名
                                       final BosClient client = new BosClient(config);
                                       for (File file : files) {
                                           PutObjectResponse putObjectResponseFromFile = client.putObject(BucketName, getFileName(pointid, file), file);
                                           PutObjectRequest request = new PutObjectRequest(BucketName, getFileName(pointid, file), file);
                                           request.setProgressCallback(new BosProgressCallback<PutObjectRequest>() {
                                               @Override
                                               public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                                                   super.onProgress(request, currentSize, totalSize);
                                                   if (currentSize == totalSize) {
                                                       ++putImageSize;
                                                       dialog.setMessage(String.valueOf("已上传" + putImageSize + "张照片"));
                                                       if (putImageSize == filepaths.size()) {
                                                           dialog.dismiss();
                                                           if (isNew) {
                                                               savePoint(mapid, pointData, pointid);
                                                           } else {
                                                               updataLocal(pointData);
                                                           }
                                                       }
                                                   }
                                               }
                                           });
                                           String eTag = client.putObject(request).getETag();
                                       }
                                   } catch (BceServiceException e) {
                                       System.out.println("Error ErrorCode: " + e.getErrorCode());
                                       System.out.println("Error RequestId: " + e.getRequestId());
                                       System.out.println("Error StatusCode: " + e.getStatusCode());
                                       System.out.println("Error ErrorType: " + e.getErrorType());
                                       System.out.println("Error Message: " + e.getMessage());
                                   } catch (BceClientException bce) {
                                       System.out.println(bce.getMessage());
                                   }
                               }
                           }
                );

    }

    public String getFileName(String pointID, File file) {
        final String mapid = data.spfOlMapSet.getString(SpfOlMap.MAPID, null);
        String key = mapid + "/" + pointID + "/" + file.getName();
        return key;

    }

    public String getOlFileName(String pointID) {
        final String mapid = data.spfOlMapSet.getString(SpfOlMap.MAPID, null);
        String key = mapid + "/" + pointID + "/";
        return key;
    }

    public void getPhotoUrl(final String pointID) {
        BosClientConfiguration config = new BosClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(Constants.BOSAK, Constants.BOSSK));
        config.setEndpoint(GZ);    //传入Bucket所在区域域名
        final BosClient client = new BosClient(config);
        Observable.just(pointID).subscribeOn(Schedulers.io()).flatMap(new Function<String, ObservableSource<List<String>>>() {
                                                                          @Override
                                                                          public ObservableSource<List<String>> apply(String s) throws Exception {
                                                                              ListObjectsRequest listObjectsRequest = new ListObjectsRequest(getBucketName());

                                                                              // 设置参数
                                                                              listObjectsRequest.setMaxKeys(100);
                                                                              listObjectsRequest.setPrefix(getOlFileName(pointID));

                                                                              // 获取指定Bucket下符合上述条件的所有Object信息
                                                                              ListObjectsResponse listing = client.listObjects(listObjectsRequest);
                                                                              List<BosObjectSummary> summaryList = listing.getContents();
                                                                              List<String> stringList = new ArrayList<>();
                                                                              for (BosObjectSummary objectSummary : summaryList) {
                                                                                  URL url = client.generatePresignedUrl(getBucketName(), objectSummary.getKey(), -1);
                                                                                  urls.add(url.toString());
                                                                                  stringList.add(url.toString());
                                                                              }
                                                                              return Observable.just(stringList);
                                                                          }
                                                                      }

        )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        String error;
                        if (e instanceof TimeoutException || e instanceof SocketTimeoutException
                                || e instanceof ConnectException) {
                            error = "网路错误";
                        } else if (e instanceof JsonSyntaxException) {
                            error = "Json格式出错了";
                            //假如导致这个异常触发的原因是服务器的问题，那么应该让服务器知道，所以可以在这里
                            //选择上传原始异常描述信息给服务器
                        } else {
                            error = e.getMessage();
                        }
                        Toast.makeText(OlWayActivity.this, error, Toast.LENGTH_SHORT).show();
                    }


                    @Override
                    public void onNext(List<String> list) {
                        list.clear();
                        loadAdpater(list);
                    }
                });
    }
}

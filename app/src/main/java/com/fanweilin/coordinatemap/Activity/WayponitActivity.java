package com.fanweilin.coordinatemap.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
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
import android.widget.SimpleCursorAdapter;
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
import com.bumptech.glide.Glide;
import com.fanweilin.coordinatemap.Class.LatStyle;
import com.fanweilin.coordinatemap.Class.Marker;
import com.fanweilin.coordinatemap.Class.PointDataParcel;
import com.fanweilin.coordinatemap.Class.ShowPointStyle;
import com.fanweilin.coordinatemap.Class.SpfOlMap;
import com.fanweilin.coordinatemap.Class.UserVip;
import com.fanweilin.coordinatemap.DataModel.BaiduDataApi;
import com.fanweilin.coordinatemap.DataModel.BaiduDataClass;
import com.fanweilin.coordinatemap.DataModel.BaiduHttpControl;
import com.fanweilin.coordinatemap.DataModel.Constants;
import com.fanweilin.coordinatemap.DataModel.CoordianteApi;
import com.fanweilin.coordinatemap.DataModel.HttpControl;
import com.fanweilin.coordinatemap.DataModel.ReasonCreate;
import com.fanweilin.coordinatemap.DataModel.Register;
import com.fanweilin.coordinatemap.DataModel.RetryWithDelay;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.ConvertLatlng;
import com.fanweilin.coordinatemap.computing.JZLocationConverter;
import com.fanweilin.coordinatemap.computing.Location3TheConvert;
import com.fanweilin.coordinatemap.widget.NoScrollGridView;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.FilesDao;
import com.fanweilin.greendao.Olfiles;
import com.fanweilin.greendao.OlfilesDao;
import com.fanweilin.greendao.PictureData;
import com.fanweilin.greendao.PointData;
import com.fanweilin.greendao.ShowData;
import com.fanweilin.greendao.ShowDataDao;
import com.fanweilin.greendao.SqlPolygon;
import com.fanweilin.greendao.SqlPolyline;
import com.tencent.qcloud.sdk.Constant;

import org.greenrobot.greendao.annotation.NotNull;
import org.json.JSONArray;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import retrofit2.Retrofit;
import static com.fanweilin.coordinatemap.R.dimen.space_size;

public class WayponitActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String MAIACTIVTY = "mainactivity";
    public static final String DATAMANAGERACTIVITY = "datananageractivty";
    private Toolbar toolbar;
    private Button btncameral;
    public static final int REQUEST_CODE_GET_CAMERAL = 1;
    private static final int REQUEST_PREVIEW_CODE = 2;
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
    private DaoSession mDaoSession;
    private SQLiteDatabase db;
    private PointDataParcel pointData;
    private Cursor cursor;
    SimpleCursorAdapter adapter;
    private AppCompatSpinner spinner;
    PointData mpointdata;
    Files mFiles;
    private List<String> listName = new ArrayList<String>();
    ProgressDialog dialog;
    private int maptype;
    private int REID=Marker.blue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wayponit);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用  getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spinner = new AppCompatSpinner(getSupportActionBar().getThemedContext());
        int[] to = {android.R.id.text1};
        String[] from = {FilesDao.Properties.Title.columnName};
        adapter = new SimpleCursorAdapter(this, R.layout.spinnerlist, cursor, from,
                to);
        adapter.setDropDownViewResource( R.layout.spinnerlist);
        spinner.setAdapter(adapter);
        if(MAIACTIVTY.equals(pointData.getActivity())){
            if(maptype==1){
                toolbar.addView(spinner);
            }else {
                toolbar.setTitle(data.spfOlMapSet.getString(SpfOlMap.MAPNAME,""));
            }
        }else {
            toolbar.addView(spinner);
        }



        dialog = new ProgressDialog(WayponitActivity.this);
        dialog.setIndeterminate(true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (MAIACTIVTY.equals(pointData.getActivity())) {
                    cursor.moveToPosition(position);
                    data.setCurretFilename(cursor.getString(cursor.getColumnIndex(FilesDao.Properties.Title.columnName)));
                } else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if(maptype==1){
                setSpinnerItemSelectedByValue(spinner, mFiles.getTitle());
            }
            }
        });


        if (MAIACTIVTY.equals(pointData.getActivity())) {
            setSpinnerItemSelectedByValue(spinner, data.currentFilename);
        } else {
            if(maptype==1){
                setSpinnerItemSelectedByValue(spinner, mFiles.getTitle());
            }

        }

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.waypoint_menu_save:
                        if (MAIACTIVTY.equals(pointData.getActivity())) {
                            String name = pointname.getText().toString();
                            if(name.isEmpty()){
                                Toast.makeText(WayponitActivity.this,"名字不能为空",Toast.LENGTH_SHORT).show();
                            }else {
                                mpointdata.setMarkerid(REID);
                                mpointdata.setDescribe(describe.getText().toString());
                                mpointdata.setName(name);
                                mpointdata.setAddress(tvAddress.getText().toString());
                                    data.createPointData(data.findOrderByName(data.currentFilename), mpointdata);
                                    db.beginTransaction();
                                    for (int i = 0; i < imagePaths.size(); i++) {
                                        PictureData pictureData = new PictureData();
                                        pictureData.setPath(imagePaths.get(i));
                                        data.ctreatePictureDate(mpointdata, pictureData);
                                    }
                                    db.setTransactionSuccessful();
                                    db.endTransaction();
                                    PointDataParcel pp=new PointDataParcel();
                                    pp=setPointdataParcel(mpointdata,data.findOrderByName(data.currentFilename).getId());
                                    Intent intent = new Intent();
                                    intent.putExtra(MainMapsActivity.GETPOINTDATAPARCE, pp);
                                    intent.setClass(WayponitActivity.this, MainMapsActivity.class);
                                    intent.putExtra("data", "one");
                                    data.createShowdata(mpointdata);
                                    intent.putExtra(MainMapsActivity.DATAMANAGERACTIVITY, "com.fanweilin.coordinatemap.Activity.DataManagerActivity");
                                    startActivity(intent);

                            }

                            

                        } else if (DATAMANAGERACTIVITY.equals(pointData.getActivity())) {
                            String name = pointname.getText().toString();
                            if(name.isEmpty()){
                                Toast.makeText(WayponitActivity.this,"名字不能为空",Toast.LENGTH_SHORT).show();
                            }else {
                                mpointdata.setName(name);
                                mpointdata.setDescribe(describe.getText().toString());
                                mpointdata.setMarkerid(REID);
                                data.updataPointdata(mpointdata);
                                data.deletePictureDateByList(mpointdata.getPictureItems());
                                mpointdata.resetPictureItems();
                                db.beginTransaction();
                                for (int i = 0; i < imagePaths.size(); i++) {
                                    PictureData pictureData = new PictureData();
                                    pictureData.setPath(imagePaths.get(i));
                                    data.ctreatePictureDate(mpointdata, pictureData);
                                }
                                db.setTransactionSuccessful();
                                db.endTransaction();
                                PointDataParcel pp=new PointDataParcel();
                                pp=setPointdataParcel(mpointdata,data.findOrderByName(data.currentFilename).getId());
                                Intent intent = new Intent();
                                intent.putExtra(MainMapsActivity.GETPOINTDATAPARCE, pp);
                                intent.setClass(WayponitActivity.this, MainMapsActivity.class);
                                intent.putExtra("data", "one");
                                data.createShowdata(mpointdata);
                                intent.putExtra(MainMapsActivity.DATAMANAGERACTIVITY, "com.fanweilin.coordinatemap.Activity.DataManagerActivity");
                                startActivity(intent);
                            }


                        }
                        break;
                    case R.id.waypoint_menu_new:
                        AlertDialog.Builder builder = new AlertDialog.Builder(WayponitActivity.this);
                        final AppCompatEditText editText = new AppCompatEditText(WayponitActivity.this);
                        builder.setTitle("请输入文件名").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Files files = new Files();
                                String filename = editText.getText().toString();
                                if (!filename.isEmpty()) {
                                    if (!isNamere(cursor, filename)) {
                                        files.setTitle(filename);
                                        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
                                        String comment = "创建于 " + df.format(new Date());
                                        files.setDate(comment);
                                        getFileDao().insert(files);
                                        cursor.requery();
                                        setSpinnerItemSelectedByValue(spinner, filename);
                                    }
                                }
                            }
                        }).show();
                        break;
                }
                return true;
            }
        });
    }
    private  PointDataParcel setPointdataParcel(PointData pointData,long filesID){
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
     ;
        gridView = findViewById(R.id.grv_photo);
        toolbar = findViewById(R.id.toolbar);
        describe = findViewById(R.id.edt_describe);
        pointname = findViewById(R.id.edt_pointname);
        btncameral = findViewById(R.id.btn_cameral);
        baiduEdit = findViewById(R.id.edt_baidu);
        wsgEdit = findViewById(R.id.edt_wgs);
        altitude = findViewById(R.id.edt_altitude);
        tvAddress = findViewById(R.id.waypoint_tv_address);
        imgBtn=findViewById(R.id.img_btn_marker);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPointMarker();
            }
        });
        maptype=data.spfOlMapSet.getInt(SpfOlMap.MAPTYPE,1);
       // spinner = (AppCompatSpinner) findViewById(R.id.spinner_filename);
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
     /*   cols = cols < 3 ? 3 : cols;*/
        cols=2;
        gridView.setNumColumns(cols);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int columnSpace = getResources().getDimensionPixelOffset(space_size);
        columnWidth = (screenWidth - columnSpace * (cols - 1)) / cols;
        btncameral.setOnClickListener(this);
        initdata();
        getListdata();
        imgBtn.setImageResource(Marker.getResource(REID));
        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PhotoPreview.builder()
                        .setPhotos(imagePaths)
                        .setCurrentItem(position)
                        .setShowDeleteButton(false)
                        .start(WayponitActivity.this);
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

            if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(permissions, 0);
            }
        }
    }
    public void setPointMarker(){
        View view = LayoutInflater.from(this).inflate(R.layout.list_img, null);
        ImageButton imgBlue=view.findViewById(R.id.img_blue);
        ImageButton imgRed=view.findViewById(R.id.img_red);
        ImageButton imgGreen=view.findViewById(R.id.img_green);
        ImageButton imgYellow=view.findViewById(R.id.img_yellow);
        ImageButton imgZs=view.findViewById(R.id.img_zs);
        final ImageView imageView=view.findViewById(R.id.img_select);
            imageView.setImageResource(Marker.getResource(REID));

        class imgClick implements View.OnClickListener{

            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.img_blue:
                        REID=Marker.blue;
                        imageView.setImageResource(Marker.REBLUEID);
                        break;
                    case R.id.img_red:
                        REID=Marker.red;
                        imageView.setImageResource(Marker.REREDID);
                        break;
                    case R.id.img_green:
                        REID=Marker.green;
                        imageView.setImageResource(Marker.REGREENID);
                        break;
                    case R.id.img_yellow:
                        REID=Marker.yellow;
                        imageView.setImageResource(Marker.REYEID);
                        break;
                    case R.id.img_zs:
                        REID=Marker.zs;
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
        mDaoSession = data.getmDaoSession();
        db = data.getDb();
        mFiles = new Files();
        mpointdata = new PointData();
        Intent intent = this.getIntent();
        imagePrePaths = new ArrayList<>();
        pointData = new PointDataParcel();
        pointData = intent.getParcelableExtra(POINTDATA);
        LatLng gcjlat=null;
        if(pointData.getGcjLatitude()!=null){
            gcjlat=new LatLng(Double.parseDouble(pointData.getGcjLatitude()),Double.parseDouble(pointData.getGcjLongitude()));
        }else {
            JZLocationConverter.LatLng gcj=JZLocationConverter.wgs84ToGcj02(new JZLocationConverter.LatLng(Double.parseDouble(pointData.getWgsLatitude()),Double.parseDouble(pointData.getWgsLongitude())));
            gcjlat=new LatLng(gcj.getLatitude(),gcj.getLongitude());
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
         String  format=prefs.getString("coordinatedisplayformat","1");
        DecimalFormat df = new DecimalFormat("0.0000000");
        if (MAIACTIVTY.equals(pointData.getActivity())) {
            Files files=data.findOrderByName(data.currentFilename);
            if(files.getMarkerid()!=null){
                REID=files.getMarkerid();
            }
            LatLng bdLng = Location3TheConvert.ConverToBaidu(Double.parseDouble(pointData.getWgsLatitude()),Double.parseDouble( pointData.getWgsLongitude()), Location3TheConvert.WGS84);
            if(format.equals("1")){
                wsgEdit.setText(pointData.getWgsLatitude() + "," + pointData.getWgsLongitude());
                if(pointData.getGcjLatitude()!=null){
                    baiduEdit.setText(String.valueOf(df.format(bdLng.latitude) + "," + String.valueOf(df.format(bdLng.longitude))));
                }



            }else {
                wsgEdit.setText(ConvertLatlng.convertToSexagesimal(Double.parseDouble(pointData.getWgsLatitude()))+ ","
                        + ConvertLatlng.convertToSexagesimal(Double.parseDouble(pointData.getWgsLongitude())));
                if(pointData.getGcjLatitude()!=null){
                    baiduEdit.setText(ConvertLatlng.convertToSexagesimal(bdLng.latitude)+
                            "," + ConvertLatlng.convertToSexagesimal(bdLng.longitude));
                }

            }
            describe.setText(pointData.getPointname());
            altitude.setText(pointData.getAltitude());
            tvAddress.setText(pointData.getAddress());
            /**/
            if(pointData.getGcjLatitude()!=null){
                mpointdata.setGcjlatitude(pointData.getGcjLatitude());
                mpointdata.setGcjlongitude(pointData.getGcjLongitude());
            }

            mpointdata.setWgslatitude(pointData.getWgsLatitude());
            mpointdata.setWgslongitude(pointData.getWgsLongitude());


        } else if (DATAMANAGERACTIVITY.equals(pointData.getActivity())) {
            mFiles = data.findOrderById(pointData.getFileid());
            if(mFiles!=null){
                if(mFiles.getMarkerid()!=null){
                    REID=mFiles.getMarkerid();
                }
            }

            mpointdata = data.findPointDataDaoById(pointData.getPointdataid());
            if(mpointdata.getMarkerid()!=null){
                REID=mpointdata.getMarkerid();
            }
            List<PictureData> pictureItems = new ArrayList<PictureData>();
            pictureItems = mpointdata.getPictureItems();
            for (int i = 0; i < pictureItems.size(); i++) {
                imagePrePaths.add(pictureItems.get(i).getPath());
                Log.d("i", pictureItems.get(i).getPath());

            }
            if(format.equals("1")){
                wsgEdit.setText(pointData.getWgsLatitude() + "," + pointData.getWgsLongitude());
                if(pointData.getGcjLatitude()!=null){
                    baiduEdit.setText(pointData.getGcjLatitude() + "," + pointData.getGcjLongitude());
                }
            }else {
                wsgEdit.setText(ConvertLatlng.convertToSexagesimal(Double.parseDouble(pointData.getWgsLatitude()))+ ","
                        + ConvertLatlng.convertToSexagesimal(Double.parseDouble(pointData.getWgsLongitude())));
                if(pointData.getGcjLatitude()!=null){
                    baiduEdit.setText(ConvertLatlng.convertToSexagesimal(Double.parseDouble(pointData.getGcjLatitude() ))+
                            "," + ConvertLatlng.convertToSexagesimal(Double.parseDouble(pointData.getGcjLongitude())));
                }
            }
            pointname.setText(pointData.getPointname());
            describe.setText(pointData.getDescribe());
            altitude.setText(pointData.getAltitude());
            tvAddress.setText(pointData.getAddress());
        }
        if(TextUtils.isEmpty(tvAddress.getText().toString())){
            getaddress(gcjlat);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.acticty_waypoint_menu, menu);
        return true;
    }

    private void getListdata() {
        cursor = db.query(getFileDao().getTablename(), getFileDao().getAllColumns(), null, null, null, null, null);
        if (cursor.moveToFirst()) {
            String filename;
            int nameColumnIndex = cursor.getColumnIndex(FilesDao.Properties.Title.columnName);
            filename = cursor.getString(nameColumnIndex);
            listName.add(filename);
            while (cursor.moveToNext()) {
                filename = cursor.getString(nameColumnIndex);
                listName.add(filename);
            }
        }
    }
    private FilesDao getFileDao() {
        return mDaoSession.getFilesDao();
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
                        .setPhotoCount(1000)
                        .setShowCamera(true)
                        .setShowGif(true)
                        .setPreviewEnabled(false)
                        .setSelected(imagePaths)
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
      public class  spinnerAdpter extends BaseAdapter{

          @Override
          public int getCount() {
              return 0;
          }

          @Override
          public Object getItem(int position) {
              return null;
          }

          @Override
          public long getItemId(int position) {
              return 0;
          }

          @Override
          public View getView(int position, View convertView, ViewGroup parent) {
              return null;
          }
      }
    public void setSpinnerItemSelectedByValue(AppCompatSpinner spinner, String value) {
        int k = cursor.getCount();
        int i = 0;
        for (cursor.moveToFirst(); i < k; cursor.moveToNext()) {
            int nameColumnIndex = cursor.getColumnIndex(FilesDao.Properties.Title.columnName);
            String filename = cursor.getString(nameColumnIndex);
            if (value.equals(filename)) {
                spinner.setSelection(i, true);// 默认选中项
                data.setCurretFilename(value);
                break;
            }
            i++;
        }
    }

    public boolean isNamere(Cursor cursor, String name) {
        int i = 0;
        for (cursor.moveToFirst(); i < cursor.getCount(); cursor.moveToNext()) {
            int nameColumnIndex = cursor.getColumnIndex(FilesDao.Properties.Title.columnName);
            String filename = cursor.getString(nameColumnIndex);
            if (name.equals(filename)) {
                Toast.makeText(this, "文件已存在", Toast.LENGTH_LONG).show();
                return true;
            }
            i++;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

     /*   switch (requestCode) {
            case REQUEST_PREVIEW_CODE:
                if (resultCode == RESULT_OK) {
                    loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT));
                }
                break;
            case REQUEST_CODE_GET_CAMERAL:
                if (resultCode == RESULT_OK) {
                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT));
                }
                break;
            default:
                break;
        }*/
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {

                loadAdpater(data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS));
            }
        }

    }

    private void loadAdpater(ArrayList<String> paths) {
        if (imagePaths == null) {
            imagePaths = new ArrayList<>();
        }
        imagePaths.clear();
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
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(columnWidth,(int)(columnWidth*1.6));
                imageView.setLayoutParams(params);
            } else {
                imageView = (ImageView) convertView.getTag();
            }
            Glide.with(WayponitActivity.this)
                    .load(new File(getItem(position)))
                    .thumbnail(0.1f)
                    .into(imageView);
        /*    Glide.with(WayponitActivity.this)
                    .load(new File(getItem(position)))
                    .placeholder(R.mipmap.ac0)
                    .error(R.mipmap.default_error)
                    .fitCenter()
                    .crossFade()
                    .into(imageView);*/
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
                Toast.makeText(WayponitActivity.this, "抱歉，未能找到结果",
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

private void savePoint(String filename,PointData pointData){
    data.createPointData(data.findOrderOlByName(filename), pointData);
    PointDataParcel pp=new PointDataParcel();
    pp=setPointdataParcel(mpointdata,data.findOrderOlByName(filename).getId());
    Intent intent = new Intent();
    intent.putExtra(MainMapsActivity.GETPOINTDATAPARCE, pp);
    intent.setClass(WayponitActivity.this, MainMapsActivity.class);
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
    public void finishActivity(){
        if (MAIACTIVTY.equals(pointData.getActivity())){
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
        }else {
            finish();;
        }
    }
}
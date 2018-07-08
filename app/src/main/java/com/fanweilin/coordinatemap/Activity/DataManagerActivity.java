package com.fanweilin.coordinatemap.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mobstat.StatService;
import com.fanweilin.coordinatemap.Class.FilesSetting;
import com.fanweilin.coordinatemap.Class.LatStyle;
import com.fanweilin.coordinatemap.Class.Marker;
import com.fanweilin.coordinatemap.Class.PointDataParcel;
import com.fanweilin.coordinatemap.Class.SpfOlMap;
import com.fanweilin.coordinatemap.Class.StringToPoint;
import com.fanweilin.coordinatemap.Class.UserVip;
import com.fanweilin.coordinatemap.DataModel.BaiduDataApi;
import com.fanweilin.coordinatemap.DataModel.BaiduDataClass;
import com.fanweilin.coordinatemap.DataModel.BaiduHttpControl;
import com.fanweilin.coordinatemap.DataModel.Constants;
import com.fanweilin.coordinatemap.DataModel.ReasonCreate;
import com.fanweilin.coordinatemap.DataModel.RetryWithDelay;
import com.fanweilin.coordinatemap.Measure.MeasureDistance;
import com.fanweilin.coordinatemap.R;

import com.fanweilin.coordinatemap.computing.ConvertLatlng;
import com.fanweilin.coordinatemap.computing.Location3TheConvert;
import com.fanweilin.coordinatemap.widget.CheckableRelativeLayout;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.Olfiles;
import com.fanweilin.greendao.PictureData;
import com.fanweilin.greendao.PointData;
import com.fanweilin.greendao.ShowDataDao;
import com.fanweilin.greendao.SqlPolygon;
import com.fanweilin.greendao.SqlPolyline;
import com.zyyoona7.lib.EasyPopup;
import com.zyyoona7.lib.HorizontalGravity;
import com.zyyoona7.lib.VerticalGravity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import static com.fanweilin.coordinatemap.Measure.Measure.setMeasuerParcel;


public class DataManagerActivity extends AppCompatActivity implements View.OnClickListener, android.support.v7.widget.SearchView.OnQueryTextListener {
    private ListView mListView;
    private DataAdpter mDataAdpter;
    private boolean show = false;
    private List<Map<String, Object>> mData;
    private List<Map<String, Object>> mBackData;
    private LinearLayout layoutShow;
    private CheckableRelativeLayout selectALL;
    private boolean isSelectALL;
    //onclick
    private Button btnShow;
    private Button btnCancle;
    private Button btnDelete;
    private Button btnEdit;
    private Button btnAll;
    private Button btnmore;
    private RelativeLayout rlEdit;
    private RelativeLayout rlTopbar;
    private android.support.v7.widget.SearchView searchView;
    private Files files;
    private Toolbar toolbar;
    private List<PointData> pointDatas;
    private List<SqlPolygon> polygons;
    private List<SqlPolyline> polylines;
    private int datastyle;
    private int coordstyle;
    public static final String FILENAME = "filename";
    public static final String SUBTITLE = "subtitle";
    public static final String Id = "id";

    //checkbox
    private CheckBox name;
    private CheckBox wgs;
    private CheckBox altitude;
    private CheckBox bd;
    private CheckBox describe;
    private CheckBox address;
    private CheckBox photo;
    private int type=0;
   //
    private AppCompatSpinner spinner;
    private  ProgressDialog dialog;
    private int maptype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_data_manager);
        init();
        getData();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spinner = new AppCompatSpinner(getSupportActionBar().getThemedContext());
        String[] strs={"兴趣点","线段","面积"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinnerlist,strs);
        adapter.setDropDownViewResource( R.layout.spinnerlist);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        type=0;
                        getData();
                        mDataAdpter.notifyDataSetChanged();
                        break;
                    case 1:
                        type=1;
                        getData();
                        mDataAdpter.notifyDataSetChanged();
                        break;
                    case 2:
                        type=2;
                        getData();
                        mDataAdpter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        toolbar.addView(spinner);
        mDataAdpter = new DataAdpter(this);
        mListView.setAdapter(mDataAdpter);
        mListView.setOnItemClickListener(new onListItemClick());
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_add:
                        ShowDialog();
                        break;
                    case R.id.item_output:
                        importfile();
                        break;
                    case R.id.item_datemanager_set:
                        DialogSetting();
                }
                return false;
            }
        });

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
        View dialogView = inflater.inflate(R.layout.dialog_add_pointdata, null);
        AlertDialog.Builder buildersearch = new AlertDialog.Builder(DataManagerActivity.this);
        buildersearch.setView(dialogView);
        spinner = dialogView.findViewById(R.id.spn_cds);
        spinner.setSelection(data.currentCoordinate);
        radiobtnDegree = dialogView.findViewById(R.id.radio_decimal);
        radiobtndms = dialogView.findViewById(R.id.radio_dms);
        final EditText edtname = dialogView.findViewById(R.id.edt_pointname);
        edtLatitude = dialogView.findViewById(R.id.edt_latitude);
        edtLongtitude = dialogView.findViewById(R.id.edt_longtitude);
        if (datastyle == LatStyle.DEGREE) {
            radiobtnDegree.setChecked(true);
        } else {
            radiobtndms.setChecked(true);
        }
        radioGroup = dialogView.findViewById(R.id.radgroup);
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
        buildersearch.setTitle("新增点");
        buildersearch.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!TextUtils.isEmpty(edtLatitude.getText().toString()) && !TextUtils.isEmpty(edtLongtitude.getText().toString())) {
                    LatLng point = new LatLng(Double.parseDouble(edtLatitude.getText().toString()), Double.parseDouble(edtLongtitude.getText().toString()));
                    LatLng bd = Location3TheConvert.ComanLngConvertBdLngt(point, coordstyle, datastyle);
                    PointData pointData = new PointData();
                    pointData.setName(edtname.getText().toString());
                    pointData.setLatitude(edtLatitude.getText().toString());
                    pointData.setLongitude(edtLongtitude.getText().toString());
                    DecimalFormat df = new DecimalFormat("#.0000000");
                    pointData.setBaidulatitude(String.valueOf(df.format(bd.latitude)));
                    pointData.setBaidulongitude(String.valueOf(df.format(bd.longitude)));
                    pointData.setAddress("");
                    if (coordstyle == LatStyle.GPSSYTELE) {
                        if (datastyle == LatStyle.DEGREE) {
                            pointData.setWgslatitude(edtLatitude.getText().toString());
                            pointData.setWgslongitude(edtLongtitude.getText().toString());
                        } else {
                            pointData.setWgslatitude(String.valueOf(df.format(ConvertLatlng.convertToDecimalByString(edtLatitude.getText().toString()))));
                            pointData.setWgslongitude(String.valueOf(df.format(ConvertLatlng.convertToDecimalByString(edtLongtitude.getText().toString()))));
                        }
                    }
                    data.createPointData(files, pointData);
                    files.resetPointItems();
                    getData();
                    mDataAdpter.notifyDataSetChanged();
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

    public void init() {
        files = new Files();
        mData = new ArrayList<>();
        dialog = new ProgressDialog(DataManagerActivity.this);
        dialog.setIndeterminate(false);
        dialog.setCanceledOnTouchOutside(false);
        toolbar = findViewById(R.id.toolbar);
        btnCancle = findViewById(R.id.btn_cancel);
        btnShow = findViewById(R.id.btn_show);
        btnDelete = findViewById(R.id.btn_delete);
        btnEdit = findViewById(R.id.btn_edit);
        btnAll = findViewById(R.id.btn_all);
        btnmore= findViewById(R.id.btn_more);
        mListView = findViewById(R.id.data_manager_list);
        layoutShow = findViewById(R.id.layoutshow);
        rlTopbar = findViewById(R.id.data_manager_topbar);
        rlEdit = findViewById(R.id.rl_server);
        btnCancle.setOnClickListener(this);
        btnShow.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnAll.setOnClickListener(this);
        btnmore.setOnClickListener(this);
        mListView.setTextFilterEnabled(true);
        selectALL = findViewById(R.id.select_all);
        maptype=data.spfOlMapSet.getInt(SpfOlMap.MAPTYPE,1);
        isSelectALL = false;
        pointDatas = new ArrayList<PointData>();
        polygons=new ArrayList<SqlPolygon>();
        polylines=new ArrayList<SqlPolyline>();
        String filename;
        Intent intent = getIntent();
        filename = intent.getStringExtra(FILENAME);
        files = data.findOrderByName(filename);
        files.resetPointItems();
        files.resetPolyItems();
        files.resetPolygonItems();
    }


    public void getData() {
        if(mBackData!=null){
            mBackData.clear();
        }
        if(mData!=null){
            mData.clear();
        }
        files.resetPointItems();
        files.resetPolyItems();
        files.resetPolygonItems();
        pointDatas = files.getPointItems();
        polygons = files.getPolygonItems();
        polylines = files.getPolyItems();
        switch (type) {
            case 0:
                int count = pointDatas.size();
                for (int i = 0; i < count; i++) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(FILENAME, pointDatas.get(i).getName());
                    map.put(SUBTITLE, pointDatas.get(i).getAddress());
                    map.put(Id, i);
                    mData.add(map);
                }
                mBackData = mData;
                break;
            case 1:
                int linesize = polylines.size();
                for (int i = 0; i < linesize; i++) {
                    double distance=setMeasuerParcel(polylines.get(i)).getDistance();
                    DecimalFormat df = new DecimalFormat("0.000");
                    String dis;
                    if(distance<1000){
                        dis=df.format(distance)+"米";
                    }else {
                        dis=df.format(distance/1000)+"千米";

                    }
                    String info="距离为"+dis;
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put(FILENAME, polylines.get(i).getName());
                        map.put(SUBTITLE, info);
                        map.put(Id, i);
                        mData.add(map);
                }
                    mBackData = mData;
                    break;
                    case 2:
                        int gonsize = polygons.size();
                        for (int i = 0; i < gonsize; i++) {
                            double farea=setMeasuerParcel(polygons.get(i)).getArea();
                            DecimalFormat df = new DecimalFormat("0.0000");
                            DecimalFormat df1 = new DecimalFormat("0.0");
                            String area=null;
                            if(farea<1000000){
                                area=df1.format(farea)+"平方米";
                            }else {
                                area=df.format(farea/1000000)+"平方千米";
                            }

                            String mu=df.format(farea/666.6666666);
                           String info="面积为："+area+"("+mu+"亩)";
                                Map<String, Object> map = new HashMap<String, Object>();
                            map.put(FILENAME, polygons.get(i).getName());
                            map.put(SUBTITLE, info);
                            map.put(Id, i);
                            mData.add(map);
                        }
                        mBackData = mData;
                        break;
                }

    }

    public void DialogSetting() {
        View dialogSetview = LayoutInflater.from(DataManagerActivity.this).inflate(R.layout.dialog_file_output_setting, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(DataManagerActivity.this);
        builder.setTitle("设置").setView(dialogSetview);
        name = dialogSetview.findViewById(R.id.cb_point_name);
        wgs = dialogSetview.findViewById(R.id.cb_point_wgs);
        altitude = dialogSetview.findViewById(R.id.cb_point_altitude);
        bd = dialogSetview.findViewById(R.id.cb_point_bd);
        describe = dialogSetview.findViewById(R.id.cb_point_describe);
        address = dialogSetview.findViewById(R.id.cb_point_adress);
        photo = dialogSetview.findViewById(R.id.cb_point_photo);
        name.setChecked(FilesSetting.NAME_IS_DOWN);
        wgs.setChecked(FilesSetting.WGS_IS_DOWN);
        altitude.setChecked(FilesSetting.ALTITUDE_IS_DOWN);
        bd.setChecked(FilesSetting.BAIDU_IS_DOWN);
        describe.setChecked(FilesSetting.DESCRIBE_IS_DOWN);
        address.setChecked(FilesSetting.ADDRESS_IS_DOWN);
        photo.setChecked(FilesSetting.PHOTO_IS_DOWN);
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                FilesSetting.NAME_IS_DOWN = name.isChecked();
                data.spfSetName(FilesSetting.NAME_IS_DOWN);
                FilesSetting.WGS_IS_DOWN = wgs.isChecked();
                data.spfSetWgs(FilesSetting.WGS_IS_DOWN);
                FilesSetting.ALTITUDE_IS_DOWN = altitude.isChecked();
                data.spfSetAltitude(FilesSetting.ALTITUDE_IS_DOWN);
                FilesSetting.BAIDU_IS_DOWN = bd.isChecked();
                data.spfSetBd(FilesSetting.BAIDU_IS_DOWN);
                FilesSetting.DESCRIBE_IS_DOWN = describe.isChecked();
                data.spfSetDescribe(FilesSetting.DESCRIBE_IS_DOWN);
                FilesSetting.ADDRESS_IS_DOWN = address.isChecked();
                data.spfSetAddress(FilesSetting.ADDRESS_IS_DOWN);
                FilesSetting.PHOTO_IS_DOWN = photo.isChecked();
                data.spfSetPhoto(FilesSetting.PHOTO_IS_DOWN);

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }
private void importf(final String filename){
    dialog.show();
    Observable.create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> emitter) throws Exception {

            File dirs = new File(Environment.getExternalStorageDirectory(), "经纬度定位");
            if (!dirs.exists()) {
                dirs.mkdirs();
            }
            File filedirs = new File(dirs.getPath(), filename);
            if (!filedirs.exists()) {
                filedirs.mkdirs();
            }
            File file = new File(filedirs.getPath(), filename + ".csv");
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStreamWriter outStream = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outStream);
            for (int i = 0; i < pointDatas.size(); i++) {
                StringBuilder context = new StringBuilder();
                if (FilesSetting.NAME_IS_DOWN) {
                    context.append(pointDatas.get(i).getName());
                }
                if (FilesSetting.WGS_IS_DOWN) {
                    if (TextUtils.isEmpty(pointDatas.get(i).getWgslatitude())) {
                        context.append("," + pointDatas.get(i).getWgslatitude() + "," + pointDatas.get(i).getWgslongitude());
                    } else {
                        context.append("," + pointDatas.get(i).getWgslatitude() + "," + pointDatas.get(i).getWgslongitude());
                    }
                }

                if (FilesSetting.BAIDU_IS_DOWN) {
                    LatLng bdLng = Location3TheConvert.ConverToBaidu(Double.parseDouble(pointDatas.get(i).getWgslatitude()),Double.parseDouble( pointDatas.get(i).getWgslongitude()), Location3TheConvert.WGS84);
                    DecimalFormat df = new DecimalFormat("0.0000000");
                    context.append("," + String.valueOf(df.format(bdLng.latitude))+ "," + String.valueOf(df.format(bdLng.longitude)));
                }
                if (FilesSetting.ALTITUDE_IS_DOWN) {
                    context.append("," + pointDatas.get(i).getAltitude());
                }
                if (FilesSetting.DESCRIBE_IS_DOWN) {
                    context.append("," + pointDatas.get(i).getDescribe());
                }
                if (FilesSetting.ADDRESS_IS_DOWN) {
                    context.append("," + pointDatas.get(i).getAddress());
                }
                if (FilesSetting.PHOTO_IS_DOWN) {
                    List<PictureData> pictureItems = new ArrayList<PictureData>();
                    pictureItems = pointDatas.get(i).getPictureItems();
                    for (int item = 0; item < pictureItems.size(); item++) {
                        File fromFile = new File(pictureItems.get(item).getPath());
                        File toFileDir = new File(filedirs, pointDatas.get(i).getName());
                        if (!toFileDir.exists()) {
                            toFileDir.mkdirs();
                        }
                        File toFile = new File(toFileDir, fromFile.getName());

                        if (!toFile.exists()) {
                            toFile.createNewFile();
                        }
                        copyFile(fromFile, toFile);
                    }
                }
                bufferedWriter.write(context.toString());
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }
            bufferedWriter.close();
            outStream.close();
            emitter.onNext(filedirs.getAbsolutePath());
        }
    /*    @Override
        public void call(Subscriber<? super String> subscriber) {
            try {

                File dirs = new File(Environment.getExternalStorageDirectory(), "经纬度定位");
                if (!dirs.exists()) {
                    dirs.mkdirs();
                }
                File filedirs = new File(dirs.getPath(), filename);
                if (!filedirs.exists()) {
                    filedirs.mkdirs();
                }
                File file = new File(filedirs.getPath(), filename + ".csv");
                if (!file.exists()) {
                    file.createNewFile();
                }
                OutputStreamWriter outStream = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(outStream);
                for (int i = 0; i < pointDatas.size(); i++) {
                    StringBuilder context = new StringBuilder();
                    if (FilesSetting.NAME_IS_DOWN) {
                        context.append(pointDatas.get(i).getName());
                    }
                    if (FilesSetting.WGS_IS_DOWN) {
                        if (TextUtils.isEmpty(pointDatas.get(i).getWgslatitude())) {
                            context.append("," + pointDatas.get(i).getWgslatitude() + "," + pointDatas.get(i).getWgslongitude());
                        } else {
                            context.append("," + pointDatas.get(i).getWgslatitude() + "," + pointDatas.get(i).getWgslongitude());
                        }
                    }

                    if (FilesSetting.BAIDU_IS_DOWN) {
                        LatLng bdLng = Location3TheConvert.ConverToBaidu(Double.parseDouble(pointDatas.get(i).getWgslatitude()),Double.parseDouble( pointDatas.get(i).getWgslongitude()), Location3TheConvert.WGS84);
                        DecimalFormat df = new DecimalFormat("0.0000000");
                        context.append("," + String.valueOf(df.format(bdLng.latitude))+ "," + String.valueOf(df.format(bdLng.longitude)));
                    }
                    if (FilesSetting.ALTITUDE_IS_DOWN) {
                        context.append("," + pointDatas.get(i).getAltitude());
                    }
                    if (FilesSetting.DESCRIBE_IS_DOWN) {
                        context.append("," + pointDatas.get(i).getDescribe());
                    }
                    if (FilesSetting.ADDRESS_IS_DOWN) {
                        context.append("," + pointDatas.get(i).getAddress());
                    }
                    if (FilesSetting.PHOTO_IS_DOWN) {
                        List<PictureData> pictureItems = new ArrayList<PictureData>();
                        pictureItems = pointDatas.get(i).getPictureItems();
                        for (int item = 0; item < pictureItems.size(); item++) {
                            File fromFile = new File(pictureItems.get(item).getPath());
                            File toFileDir = new File(filedirs, pointDatas.get(i).getName());
                            if (!toFileDir.exists()) {
                                toFileDir.mkdirs();
                            }
                            File toFile = new File(toFileDir, fromFile.getName());

                            if (!toFile.exists()) {
                                toFile.createNewFile();
                            }
                            copyFile(fromFile, toFile);
                        }
                    }
                    bufferedWriter.write(context.toString());
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                }
                bufferedWriter.close();
                outStream.close();
                subscriber.onNext(filedirs.getAbsolutePath());
            } catch (FileNotFoundException e) {
                return;
            } catch (IOException e) {
                return;
            }

        }*/
    }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {


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
                public void onNext(String s) {
                  dialog.dismiss();
                    Toast.makeText(DataManagerActivity.this, "存储位置" + s, Toast.LENGTH_LONG).show();
                    refresh(s);
                }
            });


}
    public void importfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DataManagerActivity.this);
        final AppCompatEditText editText = new AppCompatEditText(this);
        editText.setText(files.getTitle());
        builder.setTitle("请输入文件名").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String filename=editText.getText().toString();
                importf(filename);
            }

        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();

    }
    public void refresh(String path){
        File f = new File(path);
        File file[] = f.listFiles();
        for (int i=0; i < file.length; i++)
        {

            if (file[i].isDirectory()){
                refresh(file[i].getAbsolutePath());
            }else {
                MediaScannerConnection.scanFile(this, new String[] {file[i].getAbsolutePath()}, null, null);
            }
        }
    }
    public void copyFile(File oldPath, File newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            if (oldPath.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    @Override
    public void onClick(View view) {
        SparseBooleanArray checkedArray = new SparseBooleanArray();
        checkedArray = mListView.getCheckedItemPositions();
        final SQLiteDatabase db = data.getDb();
        switch (view.getId()) {
            case R.id.btn_edit:
                if(type==0){
                    if (!show) {
                        show = true;
                        rlEdit.setVisibility(View.GONE);
                        layoutShow.setVisibility(View.VISIBLE);
                        mDataAdpter.notifyDataSetChanged();
                        uncheckedAll();

                    }
                }

                break;
            case R.id.btn_show:
                try {
                    db.beginTransaction();
                    for (int i = 0; i < checkedArray.size(); i++) {
                        if (checkedArray.valueAt(i)) {
                            int k = checkedArray.keyAt(i);
                            int id = (int) mData.get(k).get(Id);
                           data.createShowdata(pointDatas.get(id));
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                Intent intent = new Intent();
                intent.putExtra("data", "more");
                intent.setClass(DataManagerActivity.this, MainMapsActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_more:
                 showMore(checkedArray,db);
                break;
            case R.id.btn_cancel:
                show = false;
                selectALL.setVisibility(View.GONE);
                rlEdit.setVisibility(View.VISIBLE);
                layoutShow.setVisibility(View.GONE);
                mDataAdpter.notifyDataSetChanged();
                uncheckedAll();
                break;
            case R.id.btn_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(DataManagerActivity.this);
                builder.setTitle("数据删除");
                builder.setMessage("确定删除吗？");
                final SparseBooleanArray finalCheckedArray = checkedArray;
                final int len = mListView.getCount();
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            db.beginTransaction();
                            for (int i = 0; i < len; i++) {
                                if (finalCheckedArray.valueAt(i)) {
                                    int k = finalCheckedArray.keyAt(i);
                                    int id = (int) mData.get(k).get(Id);
                                    data.deletepointdata(pointDatas.get(id));
                                }
                            }
                            db.setTransactionSuccessful();
                        } finally {
                            db.endTransaction();
                        }
                        getData();
                        mDataAdpter.notifyDataSetChanged();
                        uncheckedAll();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                break;
            case R.id.btn_all:
                if (isSelectALL) {
                    for (int i = 0; i < mData.size(); i++) {
                        mListView.setItemChecked(i, false);
                        isSelectALL = false;
                    }

                } else {
                    for (int i = 0; i < mData.size(); i++) {
                        mListView.setItemChecked(i, true);
                        isSelectALL = true;
                    }
                }
        }
    }
    private EasyPopup mPopupMore ;
private void showMore(final SparseBooleanArray checkedArray , final SQLiteDatabase db){
    if(mPopupMore==null){
        String[] dataItems = {"生成线", "上传至云图"};
        List<String> datas = new ArrayList<>();
        Collections.addAll(datas,  dataItems);
        mPopupMore= new EasyPopup(this)
            .setContentView(R.layout.popup_more,600,ViewGroup.LayoutParams.WRAP_CONTENT)
            .setAnimationStyle(R.style.anim_menu_bottombar)
            //是否允许点击PopupWindow之外的地方消失
            .setFocusAndOutsideEnable(true)
            .createPopup();
        ListView listView=mPopupMore.getView(R.id.list);
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,datas));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        createLine(checkedArray,db);
                        mPopupMore.dismiss();
                        break;
                    case 1:
                        int maptype=data.spfOlMapSet.getInt(SpfOlMap.MAPTYPE,1);
                  if(maptype==2){
                      mPopupMore.dismiss();
                      upPointdatas(checkedArray);

                  }else {
                      mPopupMore.dismiss();
                      Toast.makeText(DataManagerActivity.this,"请先打开在线地图",Toast.LENGTH_SHORT).show();
                  }
                        break;
                }
            }
        });

    }
    LinearLayout ll=findViewById(R.id.ll_data_manager);
   mPopupMore.showAtAnchorView(ll,VerticalGravity.ABOVE, HorizontalGravity.RIGHT,0,10);
}

private void createLine( SparseBooleanArray checkedArray ,SQLiteDatabase db){
    if(checkedArray.size()>1){
            final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
            String name= df.format(new Date());
            SqlPolyline sqlPolyline=new SqlPolyline();
            sqlPolyline.setName(name);
            sqlPolyline.setDescribe(files.getTitle());
            List<LatLng> points=new ArrayList<>();
            int selectnumer=0;
            for (int i = 0; i < checkedArray.size(); i++) {
                if (checkedArray.valueAt(i)) {
                    selectnumer++;
                    int k = checkedArray.keyAt(i);
                    int id = (int) mData.get(k).get(Id);
                    LatLng point=new LatLng(Double.valueOf(pointDatas.get(id).getWgslatitude()),Double.valueOf(pointDatas.get(id).getWgslongitude()));
                    points.add(point);
                }
            }
            if(selectnumer>1){
                sqlPolyline.setPoints(StringToPoint.getBdPoints(points,LatStyle.GPSSYTELE));
                data.createPolyline(files,sqlPolyline);
                Toast.makeText(this,"线段已生成，在线段中查看",Toast.LENGTH_SHORT).show();
            }else {
                data.deleteSqlpolyline(sqlPolyline);
                Toast.makeText(this,"点的数量少于2个，无法生成线段",Toast.LENGTH_SHORT).show();
            }

        show = false;
        selectALL.setVisibility(View.GONE);
        rlEdit.setVisibility(View.VISIBLE);
        layoutShow.setVisibility(View.GONE);
        mDataAdpter.notifyDataSetChanged();
        uncheckedAll();
    }else {
        Toast.makeText(this,"点的数量少于2个，无法生成线段",Toast.LENGTH_SHORT).show();
    }

}
private void upPointdatas(SparseBooleanArray checkedArray ){
    SharedPreferences spf = getSharedPreferences(UserVip.SPFNAME, Context.MODE_PRIVATE);
    int  vip = spf.getInt(UserVip.SPFVIP, 1);
    dialog.show();
    dialog.setTitle("正在上传数据");
    String mapid=data.spfOlMapSet.getString(SpfOlMap.MAPID,null);
    Olfiles files=data.findOrderOlByName(mapid);
    List<PointData>mPointDatas= files.getPointolItems();
    int len=mListView.getCount();
    int time=0;
    boolean isDismiss=false;
    List<PointData> pointDataList=new ArrayList<>();
    for (int i = 0; i < len; i++) {
        if (checkedArray.valueAt(i)) {
            int k = checkedArray.keyAt(i);
            int id = (int) mData.get(k).get(Id);
            pointDataList.add(pointDatas.get(id));
        }
    }
    if(mPointDatas.size()+pointDataList.size()> UserVip.getSize(vip)){
        Toast.makeText(this,"当前用户云端数据最多"+String.valueOf(UserVip.getSize(vip)),Toast.LENGTH_SHORT).show();
    }else {
        updata(pointDataList,mapid);
    }
}
//当前上传数据
int i=0;
private void updata(final List<PointData> pointDataList, final String mapid){

Observable.fromIterable(pointDataList).subscribeOn(Schedulers.io()).flatMap(new Function<PointData, Observable<ReasonCreate>>() {
    @Override
    public Observable<ReasonCreate> apply(PointData pointData) throws Exception {
        Retrofit retrofit = BaiduHttpControl.getInstance(getApplicationContext()).getRetrofit();
        BaiduDataApi baiduDataApi = retrofit.create(BaiduDataApi.class);
        BaiduDataClass baiduDataClass=new BaiduDataClass();
        baiduDataClass.setTitle(pointData.getName());
        baiduDataClass.setAddress(pointData.getAddress());
        if(pointData.getGcjlatitude()==null){
            baiduDataClass.setLatitude(Double.parseDouble(pointData.getWgslatitude()));
            baiduDataClass.setLongitude(Double.parseDouble(pointData.getWgslongitude()));
            baiduDataClass.setCoord_type(1);
        }else {
            baiduDataClass.setLatitude(Double.parseDouble(pointData.getGcjlatitude()));
            baiduDataClass.setLongitude(Double.parseDouble(pointData.getGcjlongitude()));
            baiduDataClass.setCoord_type(2);
        }
        baiduDataClass.setDescribe(pointData.getDescribe());
        baiduDataClass.setGeotable_id(Constants.geomapid);
        baiduDataClass.setUsermap_id(mapid);
        baiduDataClass.setTags(mapid);
        baiduDataClass.setAk(Constants.ak);
        int markerid= Marker.getMarkerId(pointData);
        return  baiduDataApi.RxCreatedata(baiduDataClass.getTitle(),baiduDataClass.getAddress(),baiduDataClass.getTags(),
                baiduDataClass.getLatitude(),
                baiduDataClass.getLongitude(),baiduDataClass.getCoord_type(),
                baiduDataClass.getDescribe(),baiduDataClass.getGeotable_id(),
                baiduDataClass.getAk(),baiduDataClass.getUsermap_id(),null,0,markerid);
    }
}).retryWhen(new RetryWithDelay())
        .observeOn(AndroidSchedulers.mainThread()).
            subscribe(new Observer<ReasonCreate>() {

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
                public void onNext(ReasonCreate reasonCreate) {
                    if(reasonCreate.getStatus()==0){
                        i++;
                        dialog.setMessage("已上传"+String.valueOf(i)+"个数据");
                        if(i==pointDataList.size()){
                            dialog.setMessage("已上传完成");
                            dialog.dismiss();
                        }
                    }
                }
            });
}

    private void updata(PointData pointData, String mapid, final boolean dismiss){
            Retrofit retrofit = BaiduHttpControl.getInstance(getApplicationContext()).getRetrofit();
            BaiduDataApi baiduDataApi = retrofit.create(BaiduDataApi.class);
            BaiduDataClass baiduDataClass=new BaiduDataClass();
            baiduDataClass.setTitle(pointData.getName());
            baiduDataClass.setAddress(pointData.getAddress());
            if(pointData.getGcjlatitude()==null){
                baiduDataClass.setLatitude(Double.parseDouble(pointData.getWgslatitude()));
                baiduDataClass.setLongitude(Double.parseDouble(pointData.getWgslongitude()));
                baiduDataClass.setCoord_type(1);
            }else {
                baiduDataClass.setLatitude(Double.parseDouble(pointData.getGcjlatitude()));
                baiduDataClass.setLongitude(Double.parseDouble(pointData.getGcjlongitude()));
                baiduDataClass.setCoord_type(2);
            }

            baiduDataClass.setDescribe(pointData.getDescribe());
            baiduDataClass.setGeotable_id(Constants.geomapid);
            baiduDataClass.setUsermap_id(mapid);
            baiduDataClass.setTags(mapid);
            baiduDataClass.setAk(Constants.ak);
            int markerid= Marker.getMarkerId(pointData);
            baiduDataApi.RxCreatedata(baiduDataClass.getTitle(),baiduDataClass.getAddress(),baiduDataClass.getTags(),
                    baiduDataClass.getLatitude(),
                    baiduDataClass.getLongitude(),baiduDataClass.getCoord_type(),
                    baiduDataClass.getDescribe(),baiduDataClass.getGeotable_id(),
                    baiduDataClass.getAk(),baiduDataClass.getUsermap_id(),null,0,markerid).
                    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<ReasonCreate>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ReasonCreate reasonCreate) {
                            if(reasonCreate.getStatus()==0){
                                if(dismiss){
                                    dialog.dismiss();
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_datamanager, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        Filter filter = mDataAdpter.getFilter();
        if (mDataAdpter instanceof Filterable) {
            if (s == null || s.length() == 0) {
                filter.filter("");
            } else {
                filter.filter(s);
            }

        }
        return true;
    }

    public class ViewHolder {
        TextView name;
        TextView subtitle;
        CheckBox checkBox;
    }

    public class DataAdpter extends BaseAdapter implements Filterable {
        private LayoutInflater inflater;
        private MyFilter mFilter;

        public DataAdpter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            ViewHolder holder;
            if ((view == null)) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.list_data, null);
                holder.name = view.findViewById(R.id.name);
                holder.subtitle = view.findViewById(R.id.tv_subtitle);
                holder.checkBox = view.findViewById(R.id.checkbox);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if(mData.get(i).get(FILENAME)==null){

            }else {
                holder.name.setText(mData.get(i).get(FILENAME).toString());
            }

            if(type==0){
                if (TextUtils.isEmpty(pointDatas.get(i).getAddress())) {
                    holder.subtitle.setText(pointDatas.get((Integer) mData.get(i).get(Id)).getLatitude() + "," + pointDatas.get((Integer) mData.get(i).get(Id)).getLongitude());

                } else {
                    holder.subtitle.setText(pointDatas.get((Integer) mData.get(i).get(Id)).getAddress().toString());
                }

            }else {
                holder.subtitle.setText((mData.get(i).get(SUBTITLE).toString()));
            }

            if (show) {
                holder.checkBox.setVisibility(View.VISIBLE);
            } else {
                holder.checkBox.setVisibility(View.GONE);
            }
            return view;
        }

        @Override
        public Filter getFilter() {
            if (null == mFilter) {
                mFilter = new MyFilter();
            }
            return mFilter;
        }

        // 自定义Filter类
        class MyFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Map<String, Object>> newData = new ArrayList<Map<String, Object>>();
                FilterResults filterResults = new FilterResults();
                String filterString = charSequence.toString().trim()
                        .toLowerCase();
                if (TextUtils.isEmpty(filterString)) {
                    newData = mBackData;
                } else {
                    // 过滤出新数据
                    for (int i = 0; i < mBackData.size(); i++) {
                        String str = null;
                        str = mBackData.get(i).get(FILENAME).toString();
                        if (-1 != str.toLowerCase().indexOf(filterString)) {
                            newData.add(mBackData.get(i));
                        }
                    }

                }
                filterResults.values = newData;
                filterResults.count = newData.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mData = (List<Map<String, Object>>) filterResults.values;
                if (filterResults.count > 0) {
                    mDataAdpter.notifyDataSetChanged(); // 通知数据发生了改变
                } else {
                    mDataAdpter.notifyDataSetInvalidated(); // 通知数据失效
                }
            }
        }
    }

    private void onListItemClick(final long dataid ) {
        ListView listView = new ListView(this);
        listView.setAdapter(new DialogListAdapter(this));
        final AlertDialog.Builder builder = new AlertDialog.Builder(DataManagerActivity.this);
        final AlertDialog dialog = builder.setView(listView).show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (type) {
                    case 0:
                        final PointData pointData = data.findPointDataDaoById(dataid);
                        PointDataParcel pp = new PointDataParcel();
                        switch (position) {
                            case 0:
                                pp = setPointdataParcel(pointData);
                                Intent intent = new Intent();
                                intent.putExtra(MainMapsActivity.GETPOINTDATAPARCE, pp);
                                intent.setClass(DataManagerActivity.this, MainMapsActivity.class);
                                intent.putExtra("data", "one");
                                data.createShowdata(pointData);
                                intent.putExtra(MainMapsActivity.DATAMANAGERACTIVITY, "com.fanweilin.coordinatemap.Activity.DataManagerActivity");
                                startActivity(intent);
                                dialog.dismiss();
                                break;
                            case 1:
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(DataManagerActivity.this);
                                builder1.setTitle("是否删除数据").setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        data.deletepointdata(pointData);
                                        files.resetPointItems();
                                        getData();
                                        mDataAdpter.notifyDataSetChanged();
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                                dialog.dismiss();
                                break;
                            case 2:
                                pp = setPointdataParcel(pointData);
                                Intent intentWaypoint = new Intent();
                                intentWaypoint.putExtra(WayponitActivity.POINTDATA, pp);
                                intentWaypoint.setClass(DataManagerActivity.this, WayponitActivity.class);
                                startActivity(intentWaypoint);
                                break;
                        }
                        break;
                    case 1:
                        final SqlPolyline sqlPolyline=data.findPolyLineByID(dataid);
                        switch (position) {
                            case 0:
                                data.createShowdata( sqlPolyline);
                                Intent intent = new Intent();
                                intent.putExtra("datatype",2);
                                intent.putExtra("id",dataid);
                                intent.setClass(DataManagerActivity.this, MainMapsActivity.class);
                                intent.putExtra("data", "one");
                                intent.putExtra(MainMapsActivity.DATAMANAGERACTIVITY, "com.fanweilin.coordinatemap.Activity.DataManagerActivity");
                                startActivity(intent);
                                dialog.dismiss();
                                break;
                            case 1:
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(DataManagerActivity.this);
                                builder1.setTitle("是否删除数据").setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        data.deleteSqlpolyline(sqlPolyline);
                                        getData();
                                        mDataAdpter.notifyDataSetChanged();
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                                dialog.dismiss();
                                break;
                            case 2:
                                MeasureDistance  measureDistance=setMeasuerParcel(sqlPolyline);
                                Intent intentMeasure=new Intent();
                                intentMeasure.putExtra(MeasureActivity.SELECTACTIVITY,MeasureActivity.DATAMANAGERACTIVITY);
                                intentMeasure.putExtra(MeasureActivity.PARCELDATA,measureDistance);
                                intentMeasure.setClass(DataManagerActivity.this,MeasureActivity.class);
                                intentMeasure.putExtra("filesid",sqlPolyline.getPolyToFileID());
                                startActivity(intentMeasure);
                                break;
                        }
                        break;
                    case 2:
                        final SqlPolygon sqlPolygon=data.findPolyGonByID(dataid);
                        switch (position) {
                            case 0:
                                data.createShowdata(sqlPolygon);
                                Intent intent = new Intent();
                                intent.putExtra("datatype",3);
                                intent.putExtra("id",dataid);
                                intent.setClass(DataManagerActivity.this, MainMapsActivity.class);
                                intent.putExtra("data", "one");
                                intent.putExtra(MainMapsActivity.DATAMANAGERACTIVITY, "com.fanweilin.coordinatemap.Activity.DataManagerActivity");
                                startActivity(intent);
                                dialog.dismiss();
                                break;
                            case 1:
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(DataManagerActivity.this);
                                builder1.setTitle("是否删除数据").setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        data.deleteSqlPolygon(sqlPolygon);
                                        getData();
                                        mDataAdpter.notifyDataSetChanged();
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                                dialog.dismiss();
                                break;
                            case 2:
                                MeasureDistance  measureDistance=setMeasuerParcel(sqlPolygon);
                                Intent intentMeasure=new Intent();
                                intentMeasure.putExtra(MeasureActivity.SELECTACTIVITY,MeasureActivity.DATAMANAGERACTIVITY);
                                intentMeasure.putExtra(MeasureActivity.PARCELDATA,measureDistance);
                                intentMeasure.setClass(DataManagerActivity.this,MeasureActivity.class);
                                intentMeasure.putExtra("filesid",sqlPolygon.getPolyGonToFileID());
                                startActivity(intentMeasure);
                                break;
                        }
                        break;
                }
            }

            }


        );
    }

    private PointDataParcel setPointdataParcel(PointData pointData) {
        PointDataParcel pp = new PointDataParcel();
        pp.setActivity(WayponitActivity.DATAMANAGERACTIVITY);
        pp.setAddress(pointData.getAddress());
        pp.setPointname(pointData.getName());
        pp.setAltitude(pointData.getAltitude());
        pp.setGcjLatitude(pointData.getGcjlatitude());
        pp.setGcjLongitude(pointData.getGcjlongitude());
        pp.setWgsLatitude(pointData.getWgslatitude());
        pp.setWgsLongitude(pointData.getWgslongitude());
        pp.setDescribe(pointData.getDescribe());
        pp.setPointdataid(pointData.getId());
        pp.setFileid(files.getId());
        return pp;
    }

    public String[] dataItems = {"显示", "删除", "编辑"};

    public class DialogListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public DialogListAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return dataItems.length;
        }

        @Override
        public Object getItem(int position) {
            return dataItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.list_datamanger_dialog, null);
            TextView textView = view.findViewById(R.id.tv_datamanager_dialog);
            textView.setText(dataItems[position]);
            return view;
        }
    }

    public class onListItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (!show) {
                long id=-1;
                switch (type){
                    case 0:
                        id=pointDatas.get((Integer) mData.get(i).get(Id)).getId();
                        break;
                    case 1:
                        id=polylines.get((Integer) mData.get(i).get(Id)).getId();
                        break;
                    case 2:
                        id=polygons.get((Integer) mData.get(i).get(Id)).getId();
                        break;
                }
                onListItemClick(id);
            }
        }
    }


    public void checkall(View view) {
        if (isSelectALL) {
            for (int i = 0; i < mData.size(); i++) {
                mListView.setItemChecked(i, false);
                isSelectALL = false;
            }

        } else {
            for (int i = 0; i < mData.size(); i++) {
                mListView.setItemChecked(i, true);
                isSelectALL = true;
            }
        }
    }

    public void uncheckedAll() {
        for (int i = 0; i < mData.size(); i++) {
            mListView.setItemChecked(i, false);
            isSelectALL = false;
        }
        selectALL.setChecked(false);

    }

    private ShowDataDao getShowDataDao() {
        DaoSession mDaoSession;
        mDaoSession = data.getmDaoSession();
        return mDaoSession.getShowDataDao();
    }

    public void onResume() {
        super.onResume();

        StatService.onResume(this);
    }

    public void onPause() {
        super.onPause();

        StatService.onPause(this);
    }

    public void back(View view) {
        finish();
    }


}

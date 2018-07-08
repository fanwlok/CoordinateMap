package com.fanweilin.coordinatemap.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.fanweilin.coordinatemap.Class.LatStyle;
import com.fanweilin.coordinatemap.Class.ShowPointStyle;
import com.fanweilin.coordinatemap.Class.SpfOlMap;
import com.fanweilin.coordinatemap.Class.StringToPoint;
import com.fanweilin.coordinatemap.Class.UserVip;
import com.fanweilin.coordinatemap.DataModel.BaiduDataApi;
import com.fanweilin.coordinatemap.DataModel.BaiduDataClass;
import com.fanweilin.coordinatemap.DataModel.BaiduHttpControl;
import com.fanweilin.coordinatemap.DataModel.Constants;
import com.fanweilin.coordinatemap.DataModel.ReasonCreate;
import com.fanweilin.coordinatemap.Measure.MeasureDistance;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.JZLocationConverter;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.FilesDao;
import com.fanweilin.greendao.Olfiles;
import com.fanweilin.greendao.PointData;
import com.fanweilin.greendao.ShowData;
import com.fanweilin.greendao.ShowDataDao;
import com.fanweilin.greendao.SqlPolygon;
import com.fanweilin.greendao.SqlPolyline;
import com.fanweilin.greendao.Sqlpoint;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class MeasureActivity extends AppCompatActivity {
    public static final String MAIACTIVTY = "mainActivity";
    public static final String DATAMANAGERACTIVITY = "dataManagerActivty";
    public static final String SELECTACTIVITY="ACTIVITY";
    public static final String PARCELDATA ="parceldata";
    private AutoCompleteTextView name;
    private AutoCompleteTextView describe;
    private TextView tvInfo;
    private TextView tvmes;
    private Toolbar toolbar;
    private AppCompatSpinner spinner;
    private Cursor cursor;
    private List<String> listName = new ArrayList<String>();
    Files mFiles;
    private String Activity=null;
    private MeasureDistance measureDistance;
    private int maptype;
    private String mapid;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        initView();
        initdata();

    }
    private void  initView(){
        name= findViewById(R.id.edt_measurename);
        describe= findViewById(R.id.edt_measuredescribe);
        tvInfo= findViewById(R.id.tv_info);
        tvmes= findViewById(R.id.tv_mes);
        toolbar= findViewById(R.id.toolbar);
        dialog = new ProgressDialog(MeasureActivity.this);
        dialog.setIndeterminate(true);

    }
    private void initdata(){
        maptype=data.spfOlMapSet.getInt(SpfOlMap.MAPTYPE,1);
       mapid=data.spfOlMapSet.getString(SpfOlMap.MAPID,null);
        measureDistance=new MeasureDistance();
        Activity=getIntent().getStringExtra(SELECTACTIVITY);
        measureDistance= (MeasureDistance) getIntent().getSerializableExtra(PARCELDATA);
        String info=null;
        if(measureDistance.getType()==1){
                double distance=measureDistance.getDistance();
                DecimalFormat df = new DecimalFormat("0.000");
                String dis;
                if(distance<1000){
                    dis=df.format(distance)+"米";
                }else {
                    dis=df.format(distance/1000)+"千米";

                }
                info="距离为"+dis;
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
            info="面积为："+area+"("+mu+"亩)";
        }
       tvmes.setText(info);
        getShowDataDao();
        getListdata();
        mFiles = new Files();
        SimpleCursorAdapter adapter;
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

        if (MAIACTIVTY.equals(Activity)) {
            setSpinnerItemSelectedByValue(spinner, data.currentFilename);
        } else {
            long id=getIntent().getLongExtra("filesid",-1);
            mFiles = data.findOrderById(id);
            setSpinnerItemSelectedByValue(spinner, mFiles.getTitle());
            name.setText(measureDistance.getName());
            describe.setText(measureDistance.getDescribe());
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (MAIACTIVTY.equals(Activity)) {
                    cursor.moveToPosition(position);
                    data.setCurretFilename(cursor.getString(cursor.getColumnIndex(FilesDao.Properties.Title.columnName)));
                } else {
                    setSpinnerItemSelectedByValue(spinner, mFiles.getTitle());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(maptype==1){
            toolbar.addView(spinner);
        }else {
            toolbar.setTitle(data.spfOlMapSet.getString(SpfOlMap.MAPNAME,""));
        }

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.waypoint_menu_save:
                        if(measureDistance.getType()==1) {
                            SqlPolyline sqlPolyline=null;
                            if (MAIACTIVTY.equals(Activity)) {
                                if(maptype==1){
                                savePolyLine(sqlPolyline);
                                }else {
                                upPolyLine(sqlPolyline);
                                }

                            } else {
                                sqlPolyline =data.findPolyLineByID(measureDistance.getId());
                                String linename = name.getText().toString();
                                sqlPolyline.setName(linename);
                                sqlPolyline.setDescribe(describe.getText().toString());
                                data.updataSqlPolyline(sqlPolyline);
                            }
                        }else if(measureDistance.getType()==2){
                            SqlPolygon sqlPolygone=null;
                            if (MAIACTIVTY.equals(Activity)) {
                                      if(maptype==1){
                                          savePolyGon(sqlPolygone);
                                      }else {
                                          upPolyGon(sqlPolygone);
                                      }
                        } else {
                            sqlPolygone = data.findPolyGonByID(measureDistance.getId());
                            String linename = name.getText().toString();
                            sqlPolygone.setName(linename);
                            sqlPolygone.setDescribe(describe.getText().toString());
                            data.updataSqlPolygon(sqlPolygone);
                        }
                        }
                        break;
                       case R.id.waypoint_menu_new:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MeasureActivity.this);
                        final AppCompatEditText editText = new AppCompatEditText(MeasureActivity.this);
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
    private void finishResult(SqlPolyline sqlPolyline){
        ShowData polyshowData = new ShowData();
        polyshowData .setTitle(sqlPolyline.getName());
        polyshowData.setPointid(sqlPolyline.getId());
        polyshowData.setStyle(ShowPointStyle.LINE);
        if(maptype==1){
            polyshowData.setFileid(sqlPolyline.getPolyToFileID());
        }else {
            polyshowData.setFileid(sqlPolyline.getPolyToOlFileID());
        }

        getShowDataDao().insert(polyshowData);
        Intent intent = new Intent();
        intent.putExtra("id",sqlPolyline.getId());
        intent.putExtra("datatype",2);
        intent.putExtra("data", "one");
        intent.putExtra(MainMapsActivity.DATAMANAGERACTIVITY, "com.fanweilin.coordinatemap.Activity.DataManagerActivity");
        //startActivity(intent);
        setResult(RESULT_OK,intent);
        finish();
    }
    private void finishResult(SqlPolygon sqlPolygon){
        ShowData polyshowData = new ShowData();
        polyshowData .setTitle(sqlPolygon.getName());
        polyshowData.setPointid(sqlPolygon.getId());
        polyshowData.setStyle(ShowPointStyle.POLGON);
        if(maptype==1){
            polyshowData.setFileid(sqlPolygon.getPolyGonToFileID());
        }else {
            polyshowData.setFileid(sqlPolygon.getPolyGonToOlFileID());
        }

        getShowDataDao().insert(polyshowData);
        Intent intent = new Intent();
        intent.putExtra("id",sqlPolygon.getId());
        intent.putExtra("datatype",3);
        intent.putExtra("data", "one");
        intent.putExtra(MainMapsActivity.DATAMANAGERACTIVITY, "com.fanweilin.coordinatemap.Activity.DataManagerActivity");
        //startActivity(intent);
        setResult(RESULT_OK,intent);
        finish();
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
    private void getListdata() {
        cursor = data.getDb().query(getFileDao().getTablename(), getFileDao().getAllColumns(), null, null, null, null, null);
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
    private ShowDataDao getShowDataDao() {
        DaoSession mDaoSession;
        mDaoSession = data.getmDaoSession();
        return mDaoSession.getShowDataDao();
    }
    private FilesDao getFileDao() {
        return data.getmDaoSession().getFilesDao();
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
    private void savePolyLine(SqlPolyline sqlPolyline){
            String linename = name.getText().toString();
            sqlPolyline = new SqlPolyline();
            sqlPolyline.setName(linename);
            sqlPolyline.setDescribe(describe.getText().toString());
            sqlPolyline.setDistance(measureDistance.getDistance());
             List<LatLng> ponints = measureDistance.getBaiduPoints();
             sqlPolyline.setPoints(StringToPoint.getBdPoints(ponints, LatStyle.OTHERS));
            if(maptype==1){
                data.createPolyline(data.findOrderByName(data.currentFilename), sqlPolyline);
            }else {
                data.createPolyline(data.findOrderOlByName(mapid), sqlPolyline);
            }
            finishResult(sqlPolyline);
    }
private void upPolyLine(final SqlPolyline sqlPolyline){
    Olfiles files=data.findOrderOlByName(mapid);
    List<PointData>pointDatas = files.getPointolItems();
    List<LatLng> points = measureDistance.getBaiduPoints();
    LatLngBounds mLatLngBounds;
    LatLngBounds.Builder builder=new LatLngBounds.Builder();
    String polygons="";
    for (int i=0;i<points.size();i++){
        builder.include(points.get(i));
        polygons+=points.get(i).longitude+",";
        polygons+=points.get(i).latitude;
        if(i<points.size()-1){
            polygons+=";";
        }
    }
    mLatLngBounds=builder.build();
    if(pointDatas.size()> UserVip.NORMAL){
        Toast.makeText(this,"当前用户云端数据最多200",Toast.LENGTH_SHORT).show();
    }else {
        dialog.show();
        Retrofit retrofit = BaiduHttpControl.getInstance(getApplicationContext()).getRetrofit();
        BaiduDataApi baiduDataApi = retrofit.create(BaiduDataApi.class);
        final BaiduDataClass baiduDataClass=new BaiduDataClass();
        baiduDataClass.setTitle(name.getText().toString());
        baiduDataClass.setAddress("");
        baiduDataClass.setLatitude(mLatLngBounds.getCenter().latitude);
        baiduDataClass.setLongitude(mLatLngBounds.getCenter().longitude);
        baiduDataClass.setCoord_type(2);
        baiduDataClass.setDescribe(describe.getText().toString());
        baiduDataClass.setGeotable_id(Constants.geomapid);
        baiduDataClass.setUsermap_id(mapid);
        baiduDataClass.setTags(mapid);
        baiduDataClass.setAk(Constants.ak);
        baiduDataClass.setPolygons(polygons);
        baiduDataClass.setDatatype(1);
        baiduDataApi.RxCreatedata(baiduDataClass.getTitle(),baiduDataClass.getAddress(),baiduDataClass.getTags(),
                baiduDataClass.getLatitude(),
                baiduDataClass.getLongitude(),baiduDataClass.getCoord_type(),
                baiduDataClass.getDescribe(),baiduDataClass.getGeotable_id(),
                baiduDataClass.getAk(),baiduDataClass.getUsermap_id(),polygons,baiduDataClass.getDatatype(),1).
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Observer<ReasonCreate>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ReasonCreate reasonCreate) {
                        Toast.makeText(MeasureActivity.this,reasonCreate.getMessage(),Toast.LENGTH_SHORT);
                        if(reasonCreate.getStatus()==0){
                            Toast.makeText(MeasureActivity.this,"保存成功",Toast.LENGTH_SHORT);
                            dialog.dismiss();
                            savePolyLine(sqlPolyline);
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

}
private void savePolyGon(SqlPolygon sqlPolygon){
        String  areaname = name.getText().toString();
        sqlPolygon = new SqlPolygon();
        sqlPolygon.setName(areaname);
        sqlPolygon.setDescribe(describe.getText().toString());
        sqlPolygon.setDistance(measureDistance.getArea());
        sqlPolygon.setDistance(measureDistance.getDistance());
         List<LatLng> ponints = measureDistance.getBaiduPoints();
         sqlPolygon.setPoints(StringToPoint.getBdPoints(ponints,LatStyle.OTHERS));
        if(maptype==1){
            data.createPolygon(data.findOrderByName(data.currentFilename),sqlPolygon);
        }else {
            data.createPolygon(data.findOrderByName(mapid),sqlPolygon);
        }
        finishResult(sqlPolygon);

}
private void upPolyGon(final SqlPolygon sqlPolygon){
    Olfiles files=data.findOrderOlByName(mapid);
    List<PointData>pointDatas = files.getPointolItems();
    List<LatLng> points = measureDistance.getBaiduPoints();
    String polygons="";
    for (int i=0;i<points.size();i++){
        polygons=polygons+points.get(i).longitude+",";
        polygons+=points.get(i).latitude;
        if(i<points.size()-1){
            polygons+=";";
        }
    }
    if(pointDatas.size()>500){
        Toast.makeText(this,"当前用户云端数据最多500",Toast.LENGTH_SHORT).show();
    }else {
        dialog.show();
        Retrofit retrofit = BaiduHttpControl.getInstance(getApplicationContext()).getRetrofit();
        BaiduDataApi baiduDataApi = retrofit.create(BaiduDataApi.class);
        final BaiduDataClass baiduDataClass=new BaiduDataClass();
        baiduDataClass.setTitle(name.getText().toString());
        baiduDataClass.setAddress("");
        baiduDataClass.setLatitude(points.get(0).latitude);
        baiduDataClass.setLongitude(points.get(0).longitude);
        baiduDataClass.setCoord_type(2);
        baiduDataClass.setDescribe(describe.getText().toString());
        baiduDataClass.setGeotable_id(Constants.geomapid);
        baiduDataClass.setUsermap_id(mapid);
        baiduDataClass.setTags(mapid);
        baiduDataClass.setAk(Constants.ak);
        baiduDataClass.setPolygons(polygons);
        baiduDataClass.setDatatype(2);
        baiduDataApi.RxCreatedata(baiduDataClass.getTitle(),baiduDataClass.getAddress(),baiduDataClass.getTags(),
                baiduDataClass.getLatitude(),
                baiduDataClass.getLongitude(),baiduDataClass.getCoord_type(),
                baiduDataClass.getDescribe(),baiduDataClass.getGeotable_id(),
                baiduDataClass.getAk(),baiduDataClass.getUsermap_id(),polygons,baiduDataClass.getDatatype(),1).
                subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Observer<ReasonCreate>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ReasonCreate reasonCreate) {
                        Toast.makeText(MeasureActivity.this,reasonCreate.getMessage(),Toast.LENGTH_SHORT);
                        if(reasonCreate.getStatus()==0){
                            Toast.makeText(MeasureActivity.this,"保存成功",Toast.LENGTH_SHORT);
                            dialog.dismiss();
                            savePolyGon(sqlPolygon);
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

}
}

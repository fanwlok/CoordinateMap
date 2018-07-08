package com.fanweilin.coordinatemap.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.fanweilin.coordinatemap.Class.FilesSetting;
import com.fanweilin.coordinatemap.Class.LatStyle;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.Location3TheConvert;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.PictureData;
import com.fanweilin.greendao.PointData;
import com.fanweilin.greendao.SqlPolygon;
import com.fanweilin.greendao.SqlPolyline;
import com.fanweilin.greendao.Sqlpoint;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.reactivestreams.Subscriber;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ImportAcitivity extends AppCompatActivity implements View.OnClickListener{
    private RadioButton mtvTypeCsv;
    private RadioButton mtvTypeCsvMore;
    private RadioButton mtvTypeKml;
    private RadioGroup mradioGroup;
    private EditText meditName;
    private TextView mtvPath;
    private Toolbar toolbar;
    private Files files;
    private Button btnimport;
    private Button btncancel;
    private ProgressDialog dialog;
    List<PointData> pointDatas;
    List<SqlPolyline> polylines;
    List<SqlPolygon> polygons;
    private int ExportType=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_acitivity);
        initView();
        intData();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void initView(){
        mradioGroup=findViewById(R.id.radio_type);
        mtvTypeCsv=findViewById(R.id.radio_csv);
        mtvTypeCsvMore=findViewById(R.id.radio_csv_more);
        mtvTypeKml=findViewById(R.id.radio_kml);
        mtvPath=findViewById(R.id.tv_path_conten);
        meditName=findViewById(R.id.et_name);
        toolbar=findViewById(R.id.toolbar);
        btnimport=findViewById(R.id.btn_import);
        btncancel=findViewById(R.id.btn_cancel);
        btncancel.setOnClickListener(this);
        btnimport.setOnClickListener(this);


    }
    private void intData(){
        Intent intent=getIntent();
        long id=intent.getLongExtra("id",0);
        files=data.findOrderById(id);
        dialog = new ProgressDialog(ImportAcitivity.this);
        dialog.setIndeterminate(false);
        meditName.setText(files.getTitle());
        toolbar.setTitle(files.getTitle());
        mtvPath.setText(data.BASE_PATH);
        pointDatas=files.getPointItems();
        polylines=files.getPolyItems();
        polygons=files.getPolygonItems();
        mradioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
               if(checkedId==mtvTypeCsv.getId()){
                  ExportType=1;
               }else if(checkedId==mtvTypeCsvMore.getId()){
                   ExportType=2;
               }else {
                   ExportType=3;
               }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_import, menu);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_import:
                exportCSVFile();
                break;
        }
    }

    private void exportCSVFile(){
        dialog.show();
        final String filename=meditName.getText().toString();

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
                    context.append(pointDatas.get(i).getName());
                    context.append(","+importCoord(pointDatas.get(i),1));
                    context.append("," + pointDatas.get(i).getDescribe());
                    context.append("," + pointDatas.get(i).getAltitude());
                    context.append("," + pointDatas.get(i).getAddress());
                    if(ExportType==2){
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
                Toast.makeText(ImportAcitivity.this, "存储位置" + s, Toast.LENGTH_LONG).show();
                refresh(s);
            }
        });
    }
    private String  importCoord(PointData pointData,int coordtype){
        String s="";

        if(coordtype==1){
            if (TextUtils.isEmpty(pointData.getWgslatitude())){
                GeoPoint point;
               point=Location3TheConvert.ConverToWGS84(Double.valueOf(pointData.getWgslatitude()),
                       Double.valueOf(pointData.getWgslongitude()),Location3TheConvert.GCJ2);
                DecimalFormat df = new DecimalFormat("0.0000000");
                s=String.valueOf(df.format(point.getLatitude()))+ "," + String.valueOf(df.format(point.getLongitude()));
            }else {
                s=pointData.getWgslatitude() + "," + pointData.getWgslongitude();
            }
        }else {
            if(TextUtils.isEmpty(pointData.getGcjlatitude())){
                LatLng point;
                point=Location3TheConvert.ConverToBaidu(Double.valueOf(pointData.getWgslatitude()),
                        Double.valueOf(pointData.getWgslongitude()),Location3TheConvert.GCJ2);
                DecimalFormat df = new DecimalFormat("0.0000000");
                s=String.valueOf(df.format(point.latitude))+ "," + String.valueOf(df.format(point.longitude));
            }else {
                LatLng point;
                point=Location3TheConvert.ConverToBaidu(Double.valueOf(pointData.getGcjlatitude()),
                        Double.valueOf(pointData.getGcjlongitude()),Location3TheConvert.GCJ2);
                DecimalFormat df = new DecimalFormat("0.0000000");
                s=String.valueOf(df.format(point.latitude))+ "," + String.valueOf(df.format(point.longitude));
            }

        }
        return s;
    }
    private void  importKml(String path){
        KmlDocument kmlDocument=new KmlDocument();
        MapView mapView=new MapView(this);
        for(int i=0;i<pointDatas.size();i++){
            Marker marker=new Marker(mapView);
            GeoPoint point;
            marker.setTitle(pointDatas.get(i).getName());
            if (TextUtils.isEmpty(pointDatas.get(i).getWgslatitude())){
                point=Location3TheConvert.ConverToWGS84(Double.valueOf(pointDatas.get(i).getWgslatitude()),
                        Double.valueOf(pointDatas.get(i).getWgslongitude()),Location3TheConvert.GCJ2);

            }else {
                point=new GeoPoint(Double.valueOf(pointDatas.get(i).getWgslatitude()),
                        Double.valueOf(pointDatas.get(i).getWgslongitude()));
            }
            marker.setPosition(point);
            kmlDocument.mKmlRoot.addOverlay(marker,kmlDocument);

        }
        for(int i=0;i<polylines.size();i++){
            Polyline polyline=new Polyline();
            List<Sqlpoint> sqlpointList=polylines.get(i).getPointpolyItems();
            List<GeoPoint> points=new ArrayList<>();
            for(int j=0;j<sqlpointList.size();j++){
                GeoPoint point=Location3TheConvert.ConverToWGS84(sqlpointList.get(j).getLatitude(),sqlpointList.get(j).getLongitude(), LatStyle.OTHERS);
                points.add(point);
            }
            polyline.setTitle(polylines.get(i).getName());
            polyline.setPoints(points);
            kmlDocument.mKmlRoot.addOverlay(polyline,kmlDocument);

        }
        for(int i=0;i<polygons.size();i++){
            Polyline polyline=new Polyline();
            List<Sqlpoint> sqlpointList=polylines.get(i).getPointpolyItems();
            List<GeoPoint> points=new ArrayList<>();
            for(int j=0;j<sqlpointList.size();j++){
                GeoPoint point=Location3TheConvert.ConverToWGS84(sqlpointList.get(j).getLatitude(),sqlpointList.get(j).getLongitude(), LatStyle.OTHERS);
                points.add(point);
            }
            if(sqlpointList.size()>0){
                GeoPoint point=Location3TheConvert.ConverToWGS84(sqlpointList.get(0).getLatitude(),sqlpointList.get(0).getLongitude(), LatStyle.OTHERS);
                points.add(point);
            }
            polyline.setTitle(polylines.get(i).getName());
            polyline.setPoints(points);
            kmlDocument.mKmlRoot.addOverlay(polyline,kmlDocument);
        }

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
}

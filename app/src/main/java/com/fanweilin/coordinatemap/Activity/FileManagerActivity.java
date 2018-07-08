package com.fanweilin.coordinatemap.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mobstat.StatService;
import com.fanweilin.coordinatemap.Class.LatStyle;
import com.fanweilin.coordinatemap.Class.UriToPathUtil;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.ConvertLatlng;
import com.fanweilin.coordinatemap.computing.Location3TheConvert;
import com.fanweilin.coordinatemap.fragment.CloudFragmen;
import com.fanweilin.coordinatemap.fragment.LocalFragment;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.FilesDao;
import com.fanweilin.greendao.PictureData;
import com.fanweilin.greendao.PointData;

import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileManagerActivity extends AppCompatActivity {
    private static final int REQUEST = 1;
    private DaoSession mDaoSession;
    public Cursor cursor;
    private SQLiteDatabase db;
    private Toolbar toolbar;
    private  List<Fragment> fragments;
    private String[]title={"本地","备份"};
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public LocalFragment localFragment;
    public CloudFragmen cloudFragment;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
     ProgressDialog dialog;
    public WindowManager wm;
    public int screenWidth;
    public int screenHeight;

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        manageData(intent);
        getData(intent);
        localFragment.fresh();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(new MyOnMenuItemClick());
        Intent intent = getIntent();
        getData(intent);
        String name=intent.getStringExtra("activityname");
        if(name!=null){
            if (name.contentEquals("addfileactivity")){
                manageData(intent);
            }

        }

    }
   private void getData(Intent intent){
           String mFilePath;
           Uri uri = intent.getData();
           if (uri==null){
               return;
           }
           String str = Uri.decode(uri.getEncodedPath());
       if (str != null) {
         mFilePath= UriToPathUtil.getRealFilePath(this,uri);
         pathToData(mFilePath);
       }
   }
    private void init() {
        db = data.getDb();
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        mDaoSession = data.getmDaoSession();
        cursor = db.query(getFileDao().getTablename(), getFileDao().getAllColumns(), null, null, null, null, null);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("文件管理");
        fragments=new ArrayList<>();
        localFragment=new LocalFragment();
        fragments.add(localFragment);
        cloudFragment=new CloudFragmen();
        fragments.add(cloudFragment);
        tabLayout = findViewById(R.id.tl_file);
        viewPager= findViewById(R.id.viewpager_file);
        FragmentManager fm=getSupportFragmentManager();
        myFragmentPagerAdapter=new MyFragmentPagerAdapter(fm);
        viewPager.setAdapter(myFragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        wm = getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter{

        private MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }
    private FilesDao getFileDao() {
        return mDaoSession.getFilesDao();
    }


    public void importfile() {
        Intent intent = new Intent();
        intent.setClass(FileManagerActivity.this, AddFileActivity.class);
        startActivityForResult(intent, REQUEST);
    }
    private void newfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FileManagerActivity.this);
        final AppCompatEditText editText = new AppCompatEditText(this);
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
                        localFragment.fresh();
                    }
                }
            }

        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
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
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private class MyOnMenuItemClick implements Toolbar.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.filemamager_menu_new:
                    newfile();
                    break;
                case R.id.filemanager_import:
                    importfile();
                    break;
            }
            return false;
        }
    }

    private void manageData( Intent intent) {
        String path;
        String title;
        Bundle bundle;
        bundle = intent.getExtras();
        if(bundle==null){
            return;
        }
        path = bundle.getString("path");
        String pathname = bundle.getString("title");
        if(path==null){
            return;
        }
        File file=new File(path);
        if(file.isDirectory()){
            title=pathname;
        }else {
            title=pathname.substring(0,pathname .lastIndexOf("."));
        }

        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "创建于 " + df.format(new Date());
        int coordstyle = bundle.getInt("CoordStyle");
        int datastyle = bundle.getInt("DataStyle");
        if (data.findOrderByName(title)!=null){
            Files files=data.findOrderByName(title);
            files.setTitle(title);
            files.setPath(path);
            files.setDate(comment);
            files.setCdstyle(coordstyle);
            files.setDatastyle(datastyle);
            files.update();
            setData(title, coordstyle, datastyle, path, files);
        }else {
            Files files=new Files();
            files.setTitle(title);
            files.setPath(path);
            files.setDate(comment);
            files.setCdstyle(coordstyle);
            files.setDatastyle(datastyle);
            getFileDao().insert(files);
            setData(title, coordstyle, datastyle, path, files);
        }
    }
    private void pathToData(String path){
        File file=new File(path);
        String title;
        String pathname =file.getName();
            title=pathname;


        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "创建于 " + df.format(new Date());
        int coordstyle = LatStyle.DEGREE;
        int datastyle = LatStyle.GPSSYTELE;
        if (data.findOrderByName(title)!=null){
            Files files=data.findOrderByName(title);
            files.setTitle(title);
            files.setPath(path);
            files.setDate(comment);
            files.setCdstyle(coordstyle);
            files.setDatastyle(datastyle);
            files.update();
            setData(title, coordstyle, datastyle, path, files);
        }else {
            Files files=new Files();
            files.setTitle(title);
            files.setPath(path);
            files.setDate(comment);
            files.setCdstyle(coordstyle);
            files.setDatastyle(datastyle);
            getFileDao().insert(files);
            setData(title, coordstyle, datastyle, path, files);
        }
    }
    public void setData(String title, int CoordStyle, int DataStyle, String path, Files files) {
        File file = new File(path);
        List<String> photopath=new ArrayList<String>();
        data.getDb().beginTransaction();
        if (file.isDirectory()) {
            File[] datafiles = file .listFiles();
            for(int i=0;i<datafiles.length;i++){
                if(datafiles[i].isDirectory()){
                    photopath.add(datafiles[i].getAbsolutePath());
                }else if(file.getName().endsWith(".kml")){
                    KmlParcel(files,file) ;
                }else {
                    setTextData(title,CoordStyle,DataStyle,datafiles[i].getAbsolutePath(),files);
                }
            }
        } else if (file.getName().endsWith(".kml")){
            KmlParcel(files,file);
        }else if(file.getName().endsWith(".kmz")){
            KmlParcel(files,file);
        }
        else {
            setTextData(title,CoordStyle,DataStyle,path,files);
        }
            for(int j=0;j<photopath.size();j++){
            File photofile=new File(photopath.get(j));
            String name =photofile.getName();
                List<PointData>  pointData=new ArrayList<PointData>();
                pointData=data.findPointDataDaoByName(name);
            if(pointData.size()>0){
                File [] picture=photofile.listFiles();
                for (int k=0;k<picture.length;k++){
                    PictureData pictureData=new PictureData();
                    pictureData.setPath(picture[k].getAbsolutePath());
                    data.ctreatePictureDate(pointData.get(0),pictureData);
                }
            }
        }
        data.getDb().setTransactionSuccessful();
        data.getDb().endTransaction();

    }
private void KmlParcel(Files files,File path){

    KmlDocument kmlDocument = new KmlDocument();
    if(path.getName().endsWith(".kml")){
        kmlDocument.parseKMLFile(path);
        kmlDocument.mKmlRoot.buildOverlay(files,null,kmlDocument);
    }else {
        kmlDocument.parseKMZFile(path);
        kmlDocument.mKmlRoot.buildOverlay(files,null,kmlDocument);
    }


}

  private void setTextData(String title, int CoordStyle, int DataStyle, String path, Files files){
        FileReader fr;
        BufferedReader bfr = null;
        try {
            fr = new FileReader(path);
            bfr = new BufferedReader(fr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        try {
            if (bfr != null) {
                int i=0;
                while ((line = bfr.readLine()) != null) {
                    i++;
                    PointData pointData = new PointData();
                    String[] split;
                    split = line.split(",");
                    if(split.length<3){
                        Toast.makeText(this,"第"+String.valueOf(i)+"行"+"格式错误",Toast.LENGTH_SHORT).show();
                        continue;
                    }else {
                        pointData.setName(split[0]);
                        pointData.setLatitude(split[1]);
                        pointData.setLongitude(split[2]);
                    }
                    if(split.length>=4){
                        pointData.setDescribe(split[3]);
                    }
                  ; if(split.length==5){
                      try{
                          pointData.setMarkerid(Integer.valueOf(split[4]));
                      }catch (NumberFormatException E){
                          String content="第"+String.valueOf(i)+"行数据错误，颜色类型只能为数字";
                          Toast.makeText(this,content,Toast.LENGTH_SHORT).show();
                          continue;
                      }
                    }
                    LatLng lng = null;
                  try {
                      if (DataStyle == LatStyle.DEGREE) {
                          double lat = Double.parseDouble(split[1]);
                          double lon = Double.parseDouble(split[2]);
                          lng = new LatLng(lat, lon);
                      } else {
                          double latitude = ConvertLatlng.convertToDecimalByString(split[1]);
                          double longitude = ConvertLatlng.convertToDecimalByString(split[2]);
                          lng = new LatLng(latitude, longitude);
                      }
                  }catch (NumberFormatException E){
                      String content="第"+String.valueOf(i)+"行数据错误";
                      Toast.makeText(this,content,Toast.LENGTH_SHORT).show();
                      continue;
                  }
                    LatLng baiduLng=Location3TheConvert.ConverToBaidu(lng.latitude,lng.longitude,CoordStyle);
                    GeoPoint wgspoint=Location3TheConvert.ConverToWGS84(lng.latitude,lng.longitude,CoordStyle);
                    DecimalFormat df = new DecimalFormat("0.0000000");
                    pointData.setWgslatitude(String.valueOf(df.format(wgspoint.getLatitude())));
                    pointData.setWgslongitude(String.valueOf(df.format(wgspoint.getLongitude())));
                    pointData.setBaidulatitude(String.valueOf(df.format(baiduLng.latitude)));
                    pointData.setBaidulongitude(String.valueOf(df.format(baiduLng.longitude)));
                    pointData.setAddress("");
                    data.createPointData(files, pointData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filemanager_menu, menu);
        return true;
    }

    public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }


}

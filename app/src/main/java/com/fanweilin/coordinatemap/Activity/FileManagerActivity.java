package com.fanweilin.coordinatemap.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mobstat.StatService;
import com.fanweilin.coordinatemap.Class.CoordianteApi;
import com.fanweilin.coordinatemap.Class.FileStyle;
import com.fanweilin.coordinatemap.Class.HttpControl;
import com.fanweilin.coordinatemap.Class.LatStyle;
import com.fanweilin.coordinatemap.Class.Register;
import com.fanweilin.coordinatemap.Class.User;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.ConvertLatlng;
import com.fanweilin.coordinatemap.fragment.CloudFragmen;
import com.fanweilin.coordinatemap.fragment.LocalFragment;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.FilesDao;
import com.fanweilin.greendao.PointData;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FileManagerActivity extends AppCompatActivity {
    private static final int REQUEST = 1;
    private DaoSession mDaoSession;
    public Cursor cursor;
    private ListView listView;
    private SQLiteDatabase db;
    private Toolbar toolbar;
    private List<Map<String,Object>> mData = new ArrayList<Map<String, Object>>();
    private FileStyle mStyle;
    private  List<Fragment> fragments;
    private String[]title={"本地","备份"};
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Boolean show=false;
    private ProgressDialog dialog;
    private SharedPreferences spfUser;
    public LocalFragment localFragment;
    public CloudFragmen cloudFragment;
    public boolean islog=true;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        init();
        atempLog();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(new MyOnMenuItemClick());
    }

    private void init() {
        spfUser = getSharedPreferences(User.SPFNAEM, Context.MODE_APPEND);
        db = data.getDb();
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        mDaoSession = data.getmDaoSession();
        cursor = db.query(getFileDao().getTablename(), getFileDao().getAllColumns(), null, null, null, null, null);
        mStyle = new FileStyle();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("文件管理");
        fragments=new ArrayList<Fragment>();
        localFragment=new LocalFragment();
        fragments.add(localFragment);
        cloudFragment=new CloudFragmen();
        fragments.add(cloudFragment);
        tabLayout = (TabLayout) findViewById(R.id.tl_file);
        viewPager= (ViewPager) findViewById(R.id.viewpager_file);
        FragmentManager fm=getSupportFragmentManager();
        myFragmentPagerAdapter=new MyFragmentPagerAdapter(fm);
        viewPager.setAdapter(myFragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }
    public void atempLog(){
       Retrofit retrofit = HttpControl.getInstance(this).getRetrofit();
        CoordianteApi coordianteApi= retrofit.create(CoordianteApi.class);
        coordianteApi.Rxislog().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Subscriber<Register>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Register register) {
                   switch (register.getCode()){
                       case 400:
                           log();
                           break;
                   }
            }
        });
    }
    public void log(){
        String username=spfUser.getString(User.UERNAME, "");
        String password=spfUser.getString(User.PASSWORD, "");
        Retrofit retrofit = HttpControl.getInstance(this).getRetrofit();
        CoordianteApi login = retrofit.create(CoordianteApi.class);
        login.RxLog(username, password).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Register>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Register register) {
                        }
                });
    }
    public class MyFragmentPagerAdapter extends FragmentPagerAdapter{

        public MyFragmentPagerAdapter(FragmentManager fm) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path;
        String title;
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST) {
                Bundle mBundle;
                mBundle = data.getExtras();
                path = mBundle.getString("path");
                title = mBundle.getString("title");
                final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
                String comment = "Added on " + df.format(new Date());
                int coordstyle = mBundle.getInt("CoordStyle");
                int datastyle = mBundle.getInt("DataStyle");
                if (!isNamere(cursor, title)) {
                    // Files files = new Files(null, title, path, comment, coordstyle, datastyle,0,0);
                    Files files=new Files();
                    files.setTitle(title);
                    files.setPath(path);
                    files.setDate(comment);
                    files.setCdstyle(coordstyle);
                    files.setDatastyle(datastyle);
                    getFileDao().insert(files);
                   localFragment.fresh();
                    setData(title, coordstyle, datastyle, path, files);
                }

            }
        }

    }
    public void setData(String title, int CoordStyle, int DataStyle, String path, Files files) {
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
            data.getDb().beginTransaction();
            if (bfr != null) {
                while ((line = bfr.readLine()) != null) {
                    PointData pointData = new PointData();
                    String[] split;
                    split = line.split(",");
                    pointData.setName(split[0]);
                    pointData.setLatitude(split[1]);
                    pointData.setLongitude(split[2]);
                    double lat = Double.parseDouble(split[1]);
                    double lon = Double.parseDouble(split[2]);
                    LatLng lng = new LatLng(lat, lon);
                    LatLng baiduLng = MainActivity.ComanLngConvertBdLngt(lng, CoordStyle, DataStyle);
                    DecimalFormat df = new DecimalFormat("#.0000000");
                    pointData.setBaidulatitude(String.valueOf(df.format(baiduLng.latitude)));
                    pointData.setBaidulongitude(String.valueOf(df.format(baiduLng.longitude)));
                    pointData.setAddress("");
                    if(CoordStyle==LatStyle.GPSSYTELE){
                        if (DataStyle==LatStyle.DEGREE){
                            pointData.setWgslatitude(split[1]);
                            pointData.setWgslongitude(split[2]);
                        }else {
                            pointData.setWgslatitude(String.valueOf(df.format(ConvertLatlng.convertToDecimalByString(split[1]))));
                            pointData.setWgslongitude(String.valueOf(df.format(ConvertLatlng.convertToDecimalByString(split[2]))));
                        }
                    }
                    data.createPointData(files, pointData);
                }
            }
            data.getDb().setTransactionSuccessful();
            data.getDb().endTransaction();
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

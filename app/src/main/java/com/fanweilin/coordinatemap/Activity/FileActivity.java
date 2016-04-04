package com.fanweilin.coordinatemap.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mobstat.StatService;
import com.fanweilin.coordinatemap.Class.FileStyle;
import com.fanweilin.coordinatemap.Class.LatStyle;
import com.fanweilin.coordinatemap.R;
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

public class FileActivity extends AppCompatActivity {
    private static final int REQUEST = 1;
    private DaoSession mDaoSession;
    private Cursor cursor;
    private ListView listView;
    private SQLiteDatabase db;
    private Toolbar toolbar;
    private List<String> listName = new ArrayList<String>();
    private FileStyle mStyle;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(new MyOnMenuItemClick());
    }

    private void init() {
        db = data.getDb();
        mDaoSession = data.getmDaoSession();
        getListdata();
        mStyle = new FileStyle();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("文件管理");
        listView = (ListView) findViewById(R.id.file_manager_listview);
        listView.setOnItemLongClickListener(new MyItemLongclick());
        listView.setOnItemClickListener(new MyOnItemclick());
        String[] from = {FilesDao.Properties.Title.columnName, FilesDao.Properties.Date.columnName};
        int[] to = {android.R.id.text1, android.R.id.text2};
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from,
                to);
        listView.setAdapter(adapter);

    }

    private FilesDao getFileDao() {
        return mDaoSession.getFilesDao();
    }


    public void importfile() {
        Intent intent = new Intent();
        intent.setClass(FileActivity.this, AddFileActivity.class);
        startActivityForResult(intent, REQUEST);
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


    private class MyItemLongclick implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, final long l) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FileActivity.this);
            builder.setTitle("文件删除");
            builder.setMessage("确定删除吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(data.findOrderById(l).getTitle().equals("我的收藏")){
                        Toast.makeText(FileActivity.this, "文件不能删除", Toast.LENGTH_SHORT).show();
                    }
                    else{data.deleteFile(data.findOrderById(l));}
                    cursor.requery();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
            return true;
        }
    }

    private class MyOnItemclick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String filepath;
            String title;
            int coordstyle;
            int datastyle;
            cursor.moveToPosition(i);
            filepath = cursor.getString(cursor.getColumnIndex(FilesDao.Properties.Path.columnName));
            title = cursor.getString(cursor.getColumnIndex(FilesDao.Properties.Title.columnName));
            coordstyle = cursor.getInt(cursor.getColumnIndex(FilesDao.Properties.Cdstyle.columnName));
            datastyle = cursor.getInt(cursor.getColumnIndex(FilesDao.Properties.Datastyle.columnName));
            Intent intent = new Intent();
            intent.putExtra("path", filepath);
            intent.putExtra(DataManagerActivity.FILENAME, title);
            intent.putExtra("CoordStyle", coordstyle);
            intent.putExtra("DataStyle", datastyle);
            intent.setClass(FileActivity.this, DataManagerActivity.class);
            startActivity(intent);
        }
    }

    private void newfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FileActivity.this);
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
                        String comment = "Added on " + df.format(new Date());
                        files.setDate(comment);
                        getFileDao().insert(files);
                        cursor.requery();
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
                    Files files = new Files(null, title, path, comment, coordstyle, datastyle);
                    getFileDao().insert(files);
                    cursor.requery();
                    getData(title, coordstyle, datastyle, path, files);
                }

            }
        }

    }

    public void getData(String title, int CoordStyle, int DataStyle, String path, Files files) {
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

package com.fanweilin.coordinatemap.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.fanweilin.coordinatemap.Class.FileStyle;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.FilesDao;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileManagerActivity extends Activity {
    private static final int REQUEST = 1;
    private DaoSession mDaoSession;
    private Cursor cursor;
    private ListView listView;
    private SQLiteDatabase db;
    private List<String> listName = new ArrayList<String>();
    private FileStyle mStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        setupDateBase();
        String[] from = {FilesDao.Properties.Title.columnName, FilesDao.Properties.Date.columnName};
        int[] to = {android.R.id.text1, android.R.id.text2};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from,
                to);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new MyItemLongclick());
        listView.setOnItemClickListener(new MyOnItemclick());
    }

    private void setupDateBase() {
        data app = (data) getApplication();
        mDaoSession = app.getmDaoSession();
        db = app.getDb();
        mStyle = new FileStyle();
        listView = (ListView) findViewById(R.id.file_manager_listview);
        getdate();
    }

    private FilesDao getFileDao() {
        return mDaoSession.getFilesDao();
    }

    public void back(View view) {
        finish();
    }

    public void add(View view) {
        Intent intent = new Intent();
        intent.setClass(FileManagerActivity.this, AddFileActivity.class);
        startActivityForResult(intent, REQUEST);
    }

    public void more(View view) {

    }

    private void getdate() {

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
            AlertDialog.Builder builder = new AlertDialog.Builder(FileManagerActivity.this);
            builder.setTitle("文件删除");
            builder.setMessage("确定删除吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getFileDao().deleteByKey(l);
                    cursor.requery();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.create().show();
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
            intent.putExtra("title", title);
            intent.putExtra("CoordStyle", coordstyle);
            intent.putExtra("DataStyle", datastyle);
            intent.setClass(FileManagerActivity.this, DataManagerActivity.class);
            startActivity(intent);
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
                Files files = new Files(null, title, path, comment, coordstyle, datastyle);
                Log.d("test", String.valueOf(coordstyle));
                Log.d("test", String.valueOf(datastyle));
                getFileDao().insert(files);
                cursor.requery();
            }
        }
    }

}

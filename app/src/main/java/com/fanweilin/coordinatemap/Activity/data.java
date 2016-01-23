package com.fanweilin.coordinatemap.Activity;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.fanweilin.greendao.DaoMaster;
import com.fanweilin.greendao.DaoSession;

/**
 * Created by Administrator on 2015/12/24 0024.
 */
public class data extends Application {
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    @Override
    public void onCreate() {
        super.onCreate();
        setdata();
    }
    private void setdata() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "files_db", null);
        db = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }
    public  DaoMaster getmDaoMaster() {
        return mDaoMaster;
    }

    public DaoSession getmDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}

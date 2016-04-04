package com.fanweilin.coordinatemap.Activity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.baidu.mapapi.clusterutil.projection.Point;
import com.fanweilin.greendao.DaoMaster;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.FilesDao;
import com.fanweilin.greendao.PictureData;
import com.fanweilin.greendao.PictureDataDao;
import com.fanweilin.greendao.PointData;
import com.fanweilin.greendao.PointDataDao;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/12/24 0024.
 */
public class data extends Application {
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;
    private static SQLiteDatabase db;
    private static FilesDao mFilesDao;
    private static PointDataDao mPointDataDao;
    private static PictureDataDao mPictureDataDao;
    public static final int WGS=0;
    public static final int BAIDU=1;//百度
    public static final int OTHERS = 2;//其他;
    public static final int DEGREE = 1;//小数
    public static final int DMS = 2;//度分秒
    public static String currentFilename;
    public static int currentCoordinate;
    public static int currentLatFormat;
    private final String Set="set";
    private static SharedPreferences spf;
    private SharedPreferences.Editor edit;
    private static final String FILENAME="filename";
    private static final String COORDINATE="coorditnate";
    private static final String LATFORMAT="LATFORMAT";
    @Override
    public void onCreate() {
        super.onCreate();
        spf=getSharedPreferences(Set, Context.MODE_APPEND);
        currentFilename = spf.getString(FILENAME, "我的收藏");
        currentCoordinate=spf.getInt(COORDINATE, WGS);
        currentLatFormat=spf.getInt(LATFORMAT,DEGREE);
        edit=spf.edit();
        setdata();
    }
    public static void setCurretFilename(String filename){
        SharedPreferences.Editor edit=spf.edit();
        currentFilename=filename;
        edit.putString(FILENAME,currentFilename);
        edit.commit();
    };
    public static void setCurrentCoordinate(int coordinate){
        SharedPreferences.Editor edit=spf.edit();
        currentCoordinate=coordinate;
        edit.putInt(COORDINATE, currentCoordinate);
        edit.commit();
    }
    public static void setCurrentLatFormat(int latFormat){
        SharedPreferences.Editor edit=spf.edit();
        currentLatFormat=latFormat;
        edit.putInt(LATFORMAT, currentLatFormat);
        edit.commit();
    }
    private void setdata() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "files_db", null);
        db = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        mFilesDao = mDaoSession.getFilesDao();
        mPointDataDao = mDaoSession.getPointDataDao();
        mPictureDataDao = mDaoSession.getPictureDataDao();
        if (mDaoSession.getFilesDao().count() == 0) {
            Files file = new Files();
            file.setTitle("我的收藏");
            file.setDate("默认数据存储文件");
            mDaoSession.getFilesDao().insert(file);
        }
    }

    public static DaoMaster getmDaoMaster() {
        return mDaoMaster;
    }

    public static DaoSession getmDaoSession() {
        return mDaoSession;
    }

    public static SQLiteDatabase getDb() {
        return db;
    }

    @Override
    public void onTerminate() {
        db.close();
        super.onTerminate();
    }


    public static Files createFiles(String filename) {
        Files files = new Files();
        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "Added on " + df.format(new Date());
        files.setDate(comment);
        files.setTitle(filename);
        return mDaoSession.getFilesDao().load(mDaoSession.getFilesDao().insert(files));
    }

    public static Files updateFiles(Files files) {

        mFilesDao.update(files);
        return files;
    }

    public static Files findOrderByName(String title) {
        Files files = mFilesDao.queryBuilder()
                .where(FilesDao.Properties.Title.eq(title))
                .unique();
        return files;
    }

    public static Files findOrderById(long fileId) {
        return mFilesDao.load(fileId);
    }

    public static PointData findPointDataDaoById(long PointDataId) {
        return mPointDataDao.load(PointDataId);
    }

    public static  PointData findPointDataDaoByName(String PointName) {
        return mPointDataDao.queryBuilder()
                .where(PointDataDao.Properties.Name.eq(PointName))
                .unique();
    }

    public static  PointData createPointData(Files files, PointData pointData) {
        pointData.setFiles(files);
        return mPointDataDao.load(mPointDataDao.insert(pointData));
    }
    public static void updataPointdata(PointData pointData){
        mPointDataDao.update(pointData);
    }
    public static void deletePonitdataById(long PointDataId){
        mPointDataDao.deleteByKey(PointDataId);
    }
    public static  PictureData ctreatePictureDate(PointData pointData,PictureData pictureData){
        pictureData.setPointData(pointData);
        return mPictureDataDao.load(mPictureDataDao.insert(pictureData));
    }
    public  static void deleteFile(Files files){
        deletePointdata(files.getPointItems());
        mFilesDao.delete(files);
    }
    public static void deletePointdata(List<PointData> pointDatas){
        db.beginTransaction();
        for (int i=0;i<pointDatas.size();i++){
            deltepointdata(pointDatas.get(i));
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    public static void deltepointdata(PointData pointData){
        pointData.resetPictureItems();
        deletePictureDateByList(pointData.getPictureItems());
        mPointDataDao.delete(pointData);
    }
    public static void deletePictureDateByList(List<PictureData> pathItems){
        db.beginTransaction();
                for (int i=0;i<pathItems.size();i++){
                    mPictureDataDao.delete(pathItems.get(i));
                }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}

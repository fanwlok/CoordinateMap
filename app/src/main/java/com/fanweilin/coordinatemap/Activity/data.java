package com.fanweilin.coordinatemap.Activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.os.Build;
import android.util.DisplayMetrics;


import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.BaseRequest;
import com.baidu.trace.model.OnCustomAttributeListener;
import com.baidu.trace.model.ProcessOption;
import com.fanweilin.coordinatemap.BuildConfig;
import com.fanweilin.coordinatemap.Class.FilesSetting;
import com.fanweilin.coordinatemap.Class.ShowPointStyle;
import com.fanweilin.coordinatemap.Class.UserVip;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.Trace.utils.CommonUtil;
import com.fanweilin.coordinatemap.Trace.utils.NetUtil;
import com.fanweilin.greendao.CoordinateData;
import com.fanweilin.greendao.CoordinateDataDao;
import com.fanweilin.greendao.DaoMaster;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.FilesDao;
import com.fanweilin.greendao.Olfiles;
import com.fanweilin.greendao.OlfilesDao;
import com.fanweilin.greendao.PictureData;
import com.fanweilin.greendao.PictureDataDao;
import com.fanweilin.greendao.PointData;
import com.fanweilin.greendao.PointDataDao;
import com.fanweilin.greendao.ShowData;
import com.fanweilin.greendao.ShowDataDao;
import com.fanweilin.greendao.SqlPolygon;
import com.fanweilin.greendao.SqlPolygonDao;
import com.fanweilin.greendao.SqlPolyline;
import com.fanweilin.greendao.SqlPolylineDao;
import com.fanweilin.greendao.Sqlpoint;
import com.fanweilin.greendao.SqlpointDao;
import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMOfflinePushListener;
import com.tencent.imsdk.TIMOfflinePushNotification;
import com.tencent.qalsdk.sdk.MsfSdkUtils;
import com.threshold.rxbus2.RxBus;

import org.greenrobot.greendao.query.QueryBuilder;
import org.osmdroid.config.Configuration;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.android.schedulers.AndroidSchedulers;
import timchat.utils.Foreground;

import static android.R.id.message;

/**
 * Created by Administrator on 2015/12/24 0024.
 */
public class data extends MultiDexApplication {
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;
    private static SQLiteDatabase db;
    private static FilesDao mFilesDao;
    private static OlfilesDao mOlFilesDao;
    private static PointDataDao mPointDataDao;
    private static PictureDataDao mPictureDataDao;
    private static CoordinateDataDao mCoordinateDataDao;
    private static SqlPolygonDao mSqlPolygonDao;
    private static SqlPolylineDao  mSqlPolylineDao;
    private static SqlpointDao mSqlpointDao;
    public static final int WGS=0;
    public static final int BAIDU=1;//百度
    public static final int OTHERS = 2;//其他;
    public static final int DEGREE = 1;//小数
    public static final int DMS = 2;//度分秒
    public static String currentFilename;
    public static int currentCoordinate;
    public static int currentLatFormat;
    public static int time;
    private final String Set="set";
    private static SharedPreferences spf;
    private static SharedPreferences spfFileSet;//文件导出设置
    private static SharedPreferences spfMapSet;//地图选择设置
    public static SharedPreferences spfOlMapSet;//地图选择设置
    private static final String FILENAME="filename";
    private static final String TIME="time";
    private static final String COORDINATE="coorditnate";
    private static final String LATFORMAT="LATFORMAT";
    public static String BASE_PATH;
    public static String CUSTOM_CONFIG;
    //腾讯通信
    private  static Context mContext;
    /**
     * 轨迹客户端
     */
    private AtomicInteger mSequenceGenerator = new AtomicInteger();
    private Notification notification = null;
    public SharedPreferences trackConf = null;

    public LBSTraceClient mClient = null;

    /**
     * 轨迹服务
     */
    private LocRequest locRequest = null;
    public Trace mTrace = null;

    /**
     * 轨迹服务ID
     */
    public long serviceId =165018;

    /**
     * Entity标识
     */
    public String entityName = "17757383557";

    public boolean isRegisterReceiver = false;

    /**
     * 服务是否开启标识
     */
    public boolean isTraceStarted = false;

    /**
     * 采集是否开启标识
     */
    public boolean isGatherStarted = false;

    public static int screenWidth = 0;

    public static int screenHeight = 0;
    //
    private static data mApp;

    private static SharedPreferences mPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT>=22){
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
        mApp = this;
        mContext = getApplicationContext();
         mPrefs= PreferenceManager.getDefaultSharedPreferences(this);
        initSpf();
        initspfFileSetting();
        initspfOlmap();
        setdata();
        initfile();
        //baidu初始化
        initBaidu();
        //osmdroid初始化
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        Configuration.getInstance().setOsmdroidBasePath(new File(BASE_PATH));
        Configuration.getInstance().setOsmdroidTileCache(new File(BASE_PATH,"title"));
        //腾讯通信初始化
        initencent();
        RxBus.setMainScheduler(AndroidSchedulers.mainThread());
    }
    public static data get() {
        return mApp;
    }

    public static SharedPreferences getPrefs() {
        return mPrefs;
    }
    private void initBaidu(){
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.GCJ02);
        entityName = CommonUtil.getImei(this);

        // 若为创建独立进程，则不初始化成员变量
        if ("com.baidu.track:remote".equals(CommonUtil.getCurProcessName(getApplicationContext()))) {
            return;
        }
        initNotification();
        mClient = new LBSTraceClient(mContext);
        mTrace = new Trace(serviceId, "17757383557");
        mTrace.setNotification(notification);

        trackConf = getSharedPreferences("track_conf", MODE_PRIVATE);
        locRequest = new LocRequest(serviceId);

        mClient.setOnCustomAttributeListener(new OnCustomAttributeListener() {
            @Override
            public Map<String, String> onTrackAttributeCallback() {
                Map<String, String> map = new HashMap<>();
                map.put("key1", "value1");
                map.put("key2", "value2");
                return map;
            }

            @Override
            public Map<String, String> onTrackAttributeCallback(long locTime) {
                System.out.println("onTrackAttributeCallback, locTime : " + locTime);
                Map<String, String> map = new HashMap<>();
                map.put("key1", "value1");
                map.put("key2", "value2");
                return map;
            }
        });

        clearTraceStatus();
    }
    public void getCurrentLocation(OnEntityListener entityListener, OnTrackListener trackListener) {
        // 网络连接正常，开启服务及采集，则查询纠偏后实时位置；否则进行实时定位
        if (NetUtil.isNetworkAvailable(mContext)
                && trackConf.contains("is_trace_started")
                && trackConf.contains("is_gather_started")
                && trackConf.getBoolean("is_trace_started", false)
                && trackConf.getBoolean("is_gather_started", false)) {
            LatestPointRequest request = new LatestPointRequest(getTag(), serviceId, entityName);
            ProcessOption processOption = new ProcessOption();
            processOption.setNeedDenoise(true);
            processOption.setRadiusThreshold(100);
            request.setProcessOption(processOption);
            mClient.queryLatestPoint(request, trackListener);
        } else {
            mClient.queryRealTimeLoc(locRequest, entityListener);
        }
    }
    private void initNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        Intent notificationIntent = new Intent(this, TracingActivity.class);

        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.ic_add_marker);

        // 设置PendingIntent
        builder.setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, 0))
                .setLargeIcon(icon)
                .setContentTitle("经纬度定位") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.icon_end) // 设置状态栏内的小图标
                .setContentText("服务正在运行...") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
    }

    /**
     * 获取屏幕尺寸
     */
    private void getScreenSize() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
    }
    private void clearTraceStatus() {
        if (trackConf.contains("is_trace_started") || trackConf.contains("is_gather_started")) {
            SharedPreferences.Editor editor = trackConf.edit();
            editor.remove("is_trace_started");
            editor.remove("is_gather_started");
            editor.apply();
        }
    }

    /**
     * 初始化请求公共参数
     *
     * @param request
     */
    public void initRequest(BaseRequest request) {
        request.setTag(getTag());
        request.setServiceId(serviceId);
    }

    public int getTag() {
        return mSequenceGenerator.incrementAndGet();
    }

    private void initencent(){
     Foreground.init(this);

     if(MsfSdkUtils.isMainProcess(this)) {
         TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
             @Override
             public void handleNotification(TIMOfflinePushNotification notification) {
                 if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify){
                     //消息被设置为需要提醒
                     notification.doNotify(getApplicationContext(), R.mipmap.earth_globe_48px);
                 }
             }
         });
     }
 }
    public static Context getContext() {
        return mContext;
    }

    private void initfile(){
        File dirs = new File(Environment.getExternalStorageDirectory(), "经纬度定位");
        BASE_PATH=dirs.getAbsolutePath();
        if (!dirs.exists()) {
            dirs.mkdirs();
        }
        File filedirs = new File(dirs.getPath(), "title");
        if (!filedirs.exists()) {
            filedirs.mkdirs();
        }
    }

    //SPF设置
    public void initSpf(){
        SharedPreferences spfVIP = getSharedPreferences(UserVip.SPFNAME, Context.MODE_PRIVATE);
        spf=getSharedPreferences(Set, Context.MODE_PRIVATE);
        currentFilename = spf.getString(FILENAME, "我的收藏");
        currentCoordinate=spf.getInt(COORDINATE, WGS);
        currentLatFormat=spf.getInt(LATFORMAT, DEGREE);
        time=spf.getInt(TIME, 0);
    }
    public static void setCurretFilename(String filename){
        SharedPreferences.Editor edit=spf.edit();
        currentFilename=filename;
        edit.putString(FILENAME,currentFilename);
        edit.commit();
    }

    public static void settime(){
        SharedPreferences.Editor edit=spf.edit();
        time++;
        edit.putInt(TIME, time);
        edit.commit();
    }

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
    //spfFile设置
    public void initspfFileSetting(){
        spfFileSet=getSharedPreferences("putout_set", Context.MODE_PRIVATE);
        FilesSetting.NAME_IS_DOWN=spfFileSet.getBoolean(FilesSetting.NAME, true);
        FilesSetting.WGS_IS_DOWN=spfFileSet.getBoolean(FilesSetting.WGS, true);
        FilesSetting.ALTITUDE_IS_DOWN=spfFileSet.getBoolean(FilesSetting.ALTITUDE, false);
        FilesSetting.BAIDU_IS_DOWN=spfFileSet.getBoolean(FilesSetting.BAIDU,false);
        FilesSetting.DESCRIBE_IS_DOWN=spfFileSet.getBoolean(FilesSetting.DESCRIBE,true);
        FilesSetting.ADDRESS_IS_DOWN=spfFileSet.getBoolean(FilesSetting.ADDRESS,false);
        FilesSetting.PHOTO_IS_DOWN=spfFileSet.getBoolean(FilesSetting.PHOTO,false);
    }
    //spfolmap
    private void initspfOlmap(){
        spfOlMapSet=getSharedPreferences("spfOlMap",Context.MODE_PRIVATE);
    }
    public void initspfMapSetting(){
        spfFileSet=getSharedPreferences("map_set", Context.MODE_PRIVATE);

    }
    public static void spfSetName(Boolean name){
        SharedPreferences.Editor edit=spfFileSet.edit();
        edit.putBoolean(FilesSetting.NAME,name);
        edit.commit();
    }
    public static void spfSetWgs(Boolean wgs){
        SharedPreferences.Editor edit=spfFileSet.edit();
        edit.putBoolean(FilesSetting.WGS,wgs);
        edit.commit();
    }
    public static void spfSetAltitude(Boolean altitude){
        SharedPreferences.Editor edit=spfFileSet.edit();
        edit.putBoolean(FilesSetting.ALTITUDE,altitude);
        edit.commit();
    }
    public static void spfSetBd(Boolean bd){
        SharedPreferences.Editor edit=spfFileSet.edit();
        edit.putBoolean(FilesSetting.BAIDU,bd);
        edit.commit();
    }
    public static void spfSetDescribe(Boolean describe){
        SharedPreferences.Editor edit=spfFileSet.edit();
        edit.putBoolean(FilesSetting.DESCRIBE,describe);
        edit.commit();
    }
    public static void spfSetAddress(Boolean address){
        SharedPreferences.Editor edit=spfFileSet.edit();
        edit.putBoolean(FilesSetting.ADDRESS,address);
        edit.commit();
    }
    public static void spfSetPhoto(Boolean photo){
        SharedPreferences.Editor edit=spfFileSet.edit();
        edit.putBoolean(FilesSetting.PHOTO,photo);
        edit.commit();
    }
    //
    private void setdata() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "files_db", null);
        db=helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        mFilesDao = mDaoSession.getFilesDao();
        mOlFilesDao = mDaoSession.getOlfilesDao();
        mPointDataDao = mDaoSession.getPointDataDao();
        mPictureDataDao = mDaoSession.getPictureDataDao();
        mCoordinateDataDao=mDaoSession.getCoordinateDataDao();
        mSqlpointDao=mDaoSession.getSqlpointDao();
        mSqlPolygonDao=mDaoSession.getSqlPolygonDao();
        mSqlPolylineDao=mDaoSession.getSqlPolylineDao();
        if (mDaoSession.getFilesDao().count() == 0) {
            Files file = new Files();
            file.setTitle("我的收藏");
            file.setDate("默认数据存储文件");
            mDaoSession.getFilesDao().insert(file);
        }
        if (mCoordinateDataDao.count()==0){
            CoordinateData coordinateData=new CoordinateData();
            coordinateData.setName("国家2000坐标系");
            coordinateData.setMidlat(120.0);
            coordinateData.setDify(0.0);
            coordinateData.setDifx(0.0);
            mCoordinateDataDao.insert(coordinateData);
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

  //数据库
    public static Files createFiles(String filename) {

        Files files = new Files();
        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "创建于 " + df.format(new Date());
        files.setDate(comment);
        files.setTitle(filename);
        files.setStatus(0);
        return mDaoSession.getFilesDao().load(mDaoSession.getFilesDao().insert(files));
    }
    public static Files findOrderByName(String title) {
        Files files = mFilesDao.queryBuilder()
                .where(FilesDao.Properties.Title.eq(title))
                .unique();
        return files;
    }
    public  static void deleteFile(Files files){
        files.resetPointItems();
        files.resetPolygonItems();
        files.resetPolyItems();
        deletePointdata(files.getPointItems());
        deleteSqlpolyline(files.getPolyItems());
        deleteSqlPolygon(files.getPolygonItems());
        mFilesDao.delete(files);
    }
    public static Files updateFiles(Files files) {
        mFilesDao.update(files);
        return files;
    }
    public static Olfiles findOrderOlByName(String name) {
        Olfiles files = mOlFilesDao.queryBuilder()
                .where(OlfilesDao.Properties.Title.eq(name))
                .unique();
        return files;
    }
    public  static void deleteFile(Olfiles files){
        files.resetPointolItems();
        files.resetPolyOlItems();
        files.getPolygonOlItems();
        deletePointdata(files.getPointolItems());
        deleteSqlpolyline(files.getPolyOlItems());
        deleteSqlPolygon(files.getPolygonOlItems());
        mOlFilesDao.delete(files);
    }
   public static CoordinateData createCoorData(CoordinateData cd){
       return mCoordinateDataDao.load(mDaoSession.getCoordinateDataDao().insert(cd));
   }
    public  static  CoordinateData updateCoor(CoordinateData cd){
         mCoordinateDataDao.update(cd);
        return cd;
    }
    public static  CoordinateData findOrerById(long coorId){
        return mCoordinateDataDao.load(coorId);
    }
    public static void deleteCoorDataById(long coorId){
        mCoordinateDataDao.deleteByKey(coorId);
    }

   public static CoordinateData findCoordinateByName(String name){
       CoordinateData coordinateData=mCoordinateDataDao.queryBuilder()
               .where(CoordinateDataDao.Properties.Name.eq(name))
               .unique();
       return coordinateData;
   }

    public static List<Files> findFilesByStatus(int num){
       return mFilesDao.queryBuilder().where(FilesDao.Properties.Status.lt(num)).build().list();
    }
    public static List<Files> findOlFilesByStatus(int num){
        return mFilesDao.queryBuilder().where(FilesDao.Properties.Status.eq(num)).build().list();
    }

    public static Files findOrderById(long fileId) {
        return mFilesDao.load(fileId);
    }

    public static PointData findPointDataDaoById(long PointDataId) {
        return mPointDataDao.load(PointDataId);
    }

    public static  List<PointData> findPointDataDaoByName(String PointName) {
        return mPointDataDao.queryBuilder()
                .where(PointDataDao.Properties.Name.eq(PointName))
                .build().list();
    }
    public static  PointData createPointData(Files files, PointData pointData) {
        pointData.setStatus(0);
        pointData.setFiles(files);
        pointData.setDate(new Date());
        return mPointDataDao.load(mPointDataDao.insert(pointData));
    }
    public static  PointData createPointData(Olfiles files, PointData pointData) {
        pointData.setStatus(0);
        pointData.setOlfiles(files);
        return mPointDataDao.load(mPointDataDao.insert(pointData));
    }
    public static void updataPointdata(PointData pointData){
        pointData.setStatus(-1);
        mPointDataDao.update(pointData);
    }
    public static void deletePonitdataById(long PointDataId){
        mPointDataDao.deleteByKey(PointDataId);
    }
    public static  PictureData ctreatePictureDate(PointData pointData,PictureData pictureData){
        pictureData.setPointData(pointData);
        return mPictureDataDao.load(mPictureDataDao.insert(pictureData));
    }

    public static void deletePointdata(List<PointData> pointDatas){
        db.beginTransaction();
        for (int i=0;i<pointDatas.size();i++){
            deletepointdata(pointDatas.get(i));
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    public static void deletepointdata(PointData pointData){
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
 //point 操作
    public static Sqlpoint findPointByID(long pointId){
        return mSqlpointDao.load(pointId);
    }
    public static void deletePoint(long PointId){
        mSqlpointDao.deleteByKey(PointId);
    }
    public static void deletePoint(Sqlpoint sqlpoint){
        mSqlpointDao.delete(sqlpoint);
    }
    public static Sqlpoint createPoint(SqlPolygon sqlPolygon,Sqlpoint sqlpoint){
        sqlpoint.setSqlPolygon(sqlPolygon);
        return mSqlpointDao.load(mSqlpointDao.insert(sqlpoint));
    }
    public static void updataPoint(Sqlpoint sqlpoint){
         mSqlpointDao.update(sqlpoint);
    }
    public static Sqlpoint createPoint(SqlPolyline sqlPolygline, Sqlpoint sqlpoint){
        sqlpoint.setSqlPolyline(sqlPolygline);
        return mSqlpointDao.load(mSqlpointDao.insert(sqlpoint));
    }
    //
    public static SqlPolyline findPolyLineByID(long id){
        return mSqlPolylineDao.load(id);
    }
    public static SqlPolyline createPolyline(Files files,SqlPolyline sqlPolyline){
        sqlPolyline.setFiles(files);
        return mSqlPolylineDao.load(mSqlPolylineDao.insert(sqlPolyline));
    } public static SqlPolyline createPolyline(Olfiles files,SqlPolyline sqlPolyline){
        sqlPolyline.setOlfiles(files);
        return mSqlPolylineDao.load(mSqlPolylineDao.insert(sqlPolyline));
    }
    public  static void updataSqlPolyline(SqlPolyline sqlPolyline){
        mSqlPolylineDao.update(sqlPolyline);
    }
    public static void deleteSqlpolyline(SqlPolyline sqlPolyline){
        sqlPolyline.resetPointpolyItems();
        deletePointByList(sqlPolyline.getPointpolyItems());
        mSqlPolylineDao.delete(sqlPolyline);

    }
    public static void deleteSqlpolyline(List<SqlPolyline> polylinetItems){
        db.beginTransaction();
        for (int i=0;i<polylinetItems.size();i++){
            mSqlPolylineDao.delete(polylinetItems.get(i));
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    public static void deletePointByList(List<Sqlpoint> pointItems){
        db.beginTransaction();
        for (int i=0;i<pointItems.size();i++){
            mSqlpointDao.delete(pointItems.get(i));
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    public static SqlPolygon findPolyGonByID(long id){
        return mSqlPolygonDao.load(id);
    }
    public static SqlPolygon createPolygon(Files files,SqlPolygon SqlPolygon){
        SqlPolygon.setFiles(files);
        return mSqlPolygonDao.load(mSqlPolygonDao.insert(SqlPolygon));
    }
    public static SqlPolygon createPolygon(Olfiles files,SqlPolygon SqlPolygon){
        SqlPolygon.setOlfiles(files);
        return mSqlPolygonDao.load(mSqlPolygonDao.insert(SqlPolygon));
    }
    public  static void updataSqlPolygon(SqlPolygon SqlPolygon){
        mSqlPolygonDao.update(SqlPolygon);
    }
    public static void deleteSqlPolygon(SqlPolygon SqlPolygon){
        SqlPolygon.resetPointGonItems();
        deletePointByList(SqlPolygon.getPointGonItems());
        mSqlPolygonDao.delete(SqlPolygon);

    }
    public static void deleteSqlPolygon(List<SqlPolygon> polygontItems){
        db.beginTransaction();
        for (int i=0;i<polygontItems.size();i++){
            mSqlPolygonDao.delete(polygontItems.get(i));
        }
        db.setTransactionSuccessful();
        db.endTransaction();

    }
    public static void createShowdata(PointData pointData){
        ShowData showData = new ShowData();
        showData.setTitle(pointData.getName());
        //
        showData.setBaidulatitude(pointData.getGcjlatitude());
        showData.setBaidulongitude(pointData.getGcjlongitude());
        showData.setWgslatitude(pointData.getWgslatitude());
        showData.setWgslongitude(pointData.getWgslongitude());
        showData.setStyle(ShowPointStyle.PONIT);
        showData.setPointid(pointData.getId());
        showData.setFileid(pointData.getFileId());
        QueryBuilder qb = getmDaoSession().getShowDataDao().queryBuilder();
        qb.where(qb.and(ShowDataDao.Properties.Pointid.eq(pointData.getId()),ShowDataDao.Properties.Style.eq(ShowPointStyle.PONIT)));
        if(qb.list().size()==0){
            data.getmDaoSession().getShowDataDao().insert(showData);
        }

    }
    public static void createShowdata(SqlPolyline sqlPolyline){
        ShowData polyshowData = new ShowData();
        polyshowData .setTitle(sqlPolyline.getName());
        polyshowData.setPointid(sqlPolyline.getId());
        polyshowData.setStyle(ShowPointStyle.LINE);
        polyshowData.setFileid(sqlPolyline.getFiles().getId());
        QueryBuilder qb = getmDaoSession().getShowDataDao().queryBuilder();
        qb.where(qb.and(ShowDataDao.Properties.Pointid.eq( sqlPolyline.getId()),ShowDataDao.Properties.Style.eq(ShowPointStyle.LINE)));
        if(qb.list().size()==0){
            data.getmDaoSession().getShowDataDao().insert( polyshowData);
        }
    }
    public static void createShowdata(SqlPolygon sqlPolygon){
        ShowData polyshowData = new ShowData();
        polyshowData .setTitle(sqlPolygon.getName());
        polyshowData.setPointid(sqlPolygon.getId());
        polyshowData.setStyle(ShowPointStyle.POLGON);
        polyshowData.setFileid(sqlPolygon.getFiles().getId());
        QueryBuilder qb = getmDaoSession().getShowDataDao().queryBuilder();
        qb.where(qb.and(ShowDataDao.Properties.Pointid.eq(sqlPolygon.getId()),ShowDataDao.Properties.Style.eq(ShowPointStyle.POLGON)));
        if(qb.list().size()==0){
            data.getmDaoSession().getShowDataDao().insert( polyshowData);
        }
    }
}

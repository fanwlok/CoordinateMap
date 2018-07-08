package com.fanweilin.coordinatemap.DataModel;


import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/6/14 0014.
 */
public interface CoordianteApi {
    //注册
    @GET("index/index/reglog?")
    Observable<Register> RxRegister(@Query("username") String username, @Query("password") String password, @Query("email") String email);
   //登陆
    @GET("index/index/log?")
    Observable<Register> RxLog(@Query("username") String username, @Query("password") String password);
  //清除文件
    @GET("index/user/deletefiles?")
    Observable<Register> Rxdeletefiles(@Query("files[]")String...files);
    //删除数据
    @GET("index/user/delpointdatas?")
    Observable<FilesClass> Rxdeletedatas(@Query("filename") String filename, @Query("ids[]")Long...ids);
  //获取文件
    @GET("index/user/getmyfiles")
    Observable<List<FilesClass>> Rxgetfiles();
 //同步数据
    @POST("index/user/downmydatas")
    Observable<List<PointDataClient>> Rxdownmydatas(@Query("filename") String filename,@Body List<PointDataClient> pointDatas);
  //获取文件内容
    @GET("index/user/getmyfiledatas?")
    Observable<ResponseBody> Rxgetdata(@Query("filename") String filename);
    //判断是否登陆
    @GET("index/user/islog()")
    Observable<Register>  Rxislog();
    //获取全部上传数据id
    @GET("index/user/getdataid")
    Observable<List<IdsClass>> Rxgetdataid();
   //无用
    @POST("index/user/finduser")
    Observable<List<PointData>> Rxfinduser(@Body List<PointData> pointdata);
   //无用
    @POST("index/user/updateltu")
    Observable<Register> Rxupdata(@Body List<IdsClass> ids);
   //备份数据
    @POST("index/user/synchrodata")
    Observable<List<IdsClass>> Rxputdata(@Body List<PointDataClient> pointDataClients);


}

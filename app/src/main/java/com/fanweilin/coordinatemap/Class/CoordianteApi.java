package com.fanweilin.coordinatemap.Class;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/6/14 0014.
 */
public interface CoordianteApi {
    @GET("index/index/reglog?")
    Observable<Register> RxRegister(@Query("username") String username, @Query("password") String password, @Query("email") String email);

    @GET("index/index/log?")
    Observable<Register> RxLog(@Query("username") String username, @Query("password") String password);

    @GET("index/user/finduser?")
    Observable<Register> Rxfinduser(@Query("username") String username);
}

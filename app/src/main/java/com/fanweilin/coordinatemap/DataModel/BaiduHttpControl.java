package com.fanweilin.coordinatemap.DataModel;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2018/2/16.
 */

public class BaiduHttpControl {
    private static BaiduHttpControl mInstance;
    Retrofit retrofit;

    public static BaiduHttpControl getInstance(Context context) {
        if (mInstance == null) {
            synchronized (BaiduHttpControl.class) {
                if (mInstance == null)
                    mInstance = new BaiduHttpControl(context);
            }
        }
        return mInstance;
    }

    public BaiduHttpControl(Context context) {


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                 .retryOnConnectionFailure(true)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)//写入超时设置，
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        return chain.proceed(chain.request() // originalRequest
                                .newBuilder()
                                .addHeader("Content-Type", "application/x-www-form-urlencoded ")
                                .build());
                    }
                })
                .build();


        retrofit = new Retrofit.Builder().
                 baseUrl(Constants.BAIDU_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())//添加 json 转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加 RxJava 适配器
                .build();

    }
    public Retrofit getRetrofit(){
        return retrofit;
    }
}
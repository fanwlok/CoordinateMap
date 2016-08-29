package com.fanweilin.coordinatemap.Class;

import android.content.Context;

import com.fanweilin.coordinatemap.computing.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016/6/18 0018.
 */
public class HttpControl {

    private static HttpControl mInstance;
     Retrofit retrofit;

    public static HttpControl getInstance(Context context) {
        if (mInstance == null) {
            synchronized (HttpControl.class) {
                if (mInstance == null)
                    mInstance = new HttpControl(context);
            }
        }
        return mInstance;
    }

    public HttpControl(Context context) {


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .cookieJar(new CookieManger(context))
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        return chain.proceed(chain.request() // originalRequest
                                .newBuilder()
                                .addHeader("accept", "application/json")
                                .build());
                    }
                })
                .build();


        retrofit = new Retrofit.Builder().
                baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())//添加 json 转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//添加 RxJava 适配器
                .build();

    }
public Retrofit getRetrofit(){
    return retrofit;
}
}
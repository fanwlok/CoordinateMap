package com.fanweilin.coordinatemap.DataModel;


import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2018/2/12.
 */

public interface BaiduDataApi {
    @FormUrlEncoded
    @POST("geodata/v4/poi/create")
    Observable<ReasonCreate> RxCreatedata(@Field("title") String title,
                                          @Field("address") String address,
                                          @Field("tags") String tags,
                                          @Field("latitude") double latitude,
                                          @Field("longitude") double longitude,
                                          @Field("coord_type") int  coord_type,
                                          @Field("describe") String  describe,
                                          @Field("geotable_id") String geotable_id,
                                          @Field("ak") String ak,
                                          @Field("usermap_id") String  usermap_id,
                                          @Field("polygons") String  polygons,
                                          @Field("datatype") int 	datatype,
                                          @Field("markerid") int markerid
                                           );
    @FormUrlEncoded
    @POST("geodata/v4/poi/update")
    Observable<ReasonCreate> Rxupdate(@Field("id") String id,
                                      @Field("title") String title,
                                          @Field("address") String address,
                                          @Field("tags") String tags,
                                          @Field("latitude") double latitude,
                                          @Field("longitude") double longitude,
                                          @Field("coord_type") int  coord_type,
                                          @Field("describe") String  describe,
                                          @Field("geotable_id") String geotable_id,
                                          @Field("ak") String ak,
                                          @Field("usermap_id") String  usermap_id,
                                          @Field("polygons") String  polygons,
                                          @Field("datatype") int 	datatype,
                                          @Field("markerid") int markerid
    );


    @FormUrlEncoded
    @POST("geodata/v4/poi/delete")
    Observable<ReasonCreate> Rxdeletedata(
            @Field("ids") String ids,
            @Field("geotable_id") String geotable_id,
            @Field("ak") String ak

    );
    @FormUrlEncoded
    @POST("geodata/v4/poi/create")
    Observable<ReasonCreate> RxCreateVIP(@Field("title") String title,
                                          @Field("latitude") double latitude,
                                          @Field("longitude") double longitude,
                                          @Field("coord_type") int  coord_type,
                                          @Field("vip") int vip,
                                          @Field("geotable_id") String geotable_id,
                                          @Field("ak") String ak

    );
    @GET("geodata/v4/poi/list?")
    Observable<BaiduUserMapDataBean> RxGetAllDatas(@Query("tags") String tags,
                                                   @Query("page_index") int page_index,
                                                   @Query("page_size") int page_size,
                                                   @Query("geotable_id") String geotable_id,
                                                   @Query("ak") String ak);
    @GET("geodata/v4/poi/list?")
    Observable<BaiduUserMapDataBean> RxGetAllDatas(@Query("title") String title,
                                                   @Query("geotable_id") String geotable_id,
                                                   @Query("ak") String ak
    );
    @GET("geocoder/v2/")
    Observable<BaiduAddress> RxGetCity(@Query("callback") String renderReverse,
                                                   @Query("location") String location,
                                                   @Query("coordtype") String coordtype,
                                                   @Query("output") String output,
                                                   @Query("ak") String ak)
        ;
}

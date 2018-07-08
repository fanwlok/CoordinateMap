package com.fanweilin.coordinatemap.Class;

import android.content.Context;

import com.fanweilin.coordinatemap.DataModel.User;



/**
 * Created by Administrator on 2018/4/9.
 */

public class UserVip {
    public static String SPFNAME="uservip";

    public static int NORMAL =200;
    public static int Vip1 =1000;
    public static int Vip2=2000;
    /*
    * 1 NORMAL
    * 2 500
    * 3 2000
    * */
    public static int getSize(int vip){
        if(vip<10){
            return NORMAL;
        }else if(vip==10){
            return Vip1;
        } else
        {
            return Vip2;
        }
    }
    public static String USERRNAME;
    public static String SPFVIP="VIP";

}

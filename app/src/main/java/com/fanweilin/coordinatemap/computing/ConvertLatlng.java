package com.fanweilin.coordinatemap.computing;

/**
 * Created by Administrator on 2015/12/29 0029.
 */

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class ConvertLatlng {
    public static int sdu;
    public static int sfen;
    public static double smiao;

    //经纬度度分秒转换为小数
    public static double convertToDecimal(double du, double fen, double miao) {
        if (du < 0)
            return -(Math.abs(du) + (Math.abs(fen) + (Math.abs(miao) / 60)) / 60);

        return Math.abs(du) + (Math.abs(fen) + (Math.abs(miao) / 60)) / 60;

    }

    //以字符串形式输入经纬度的转换
    public static double convertToDecimalByString(String latlng) {
          latlng=latlng+"0000";
        double du = Double.parseDouble(latlng.substring(0, latlng.indexOf(".")));
        double fen = Double.parseDouble(latlng.substring(latlng.indexOf(".") + 1, latlng.indexOf(".") + 3));
        double miao = Double.parseDouble(latlng.substring(latlng.indexOf(".") + 3,latlng.indexOf(".") + 5)+"."+latlng.substring(latlng.indexOf(".") + 5));
        if (du < 0)
            return -(Math.abs(du) + (fen + (miao / 60)) / 60);
        return du + (fen + (miao / 60)) / 60;

    }
    //将小数转换为度分秒
    public static String convertToSexagesimal(double num) {

        sdu = (int) Math.floor(Math.abs(num));    //获取整数部分
        double temp = getdPoint(Math.abs(num)) * 60;
        sfen = (int) Math.floor(temp); //获取整数部分
        smiao = getdPoint(temp) * 60;
        DecimalFormat dfthree = new DecimalFormat("00.000");
        if (num < 0){
            sdu=sdu*(-1);
        }


        return sdu + "°" + sfen + "′" +String.valueOf(dfthree.format(smiao)) + "″";

    }
    //获取小数部分
    public static double getdPoint(double num) {
        double d = num;
        int fInt = (int) d;
        BigDecimal b1 = new BigDecimal(Double.toString(d));
        BigDecimal b2 = new BigDecimal(Integer.toString(fInt));
        double dPoint = b1.subtract(b2).floatValue();
        return dPoint;
    }
}
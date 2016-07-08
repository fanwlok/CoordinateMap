package com.fanweilin.coordinatemap.computing;

import java.text.DecimalFormat;

import static java.lang.Math.*;

/**
 * Created by Administrator on 2016/7/2 0002.
 */
public class Computer {
    //坐标正算
    public static double[] coordinateFrontComputer(double x, double y, double angel, double distance) {
        double endx = x + Math.cos(Math.toRadians(angel)) * distance;
        double endy = y + Math.sin(Math.toRadians(angel)) * distance;
        double[] c = {endx, endy};
        return c;
    }

    //坐标反算
    public static double[] coordinateBackComputer(double beginx, double beginy, double endx, double endy) {

        double distance = Math.hypot(endx - beginx, endy - beginy);
        double angel = 180 - 90 * signum(endy - beginy) - Math.toDegrees(Math.atan((endx - beginx) / (endy - beginy)));
        double[] c = {distance, angel};
        return c;
    }

    //距离格式转换，大于1000单位km,小于1000单位m
    public static String distanceFomat(double distance) {
        String textDistance;
        if (distance >= 1000) {
            DecimalFormat df = new DecimalFormat("0.000");
            distance = distance / 1000;
            textDistance = "距离:" + String.valueOf(df.format(distance)) + "km" + "  ";
        } else {
            DecimalFormat df = new DecimalFormat("0.0");
            textDistance = "距离:" + String.valueOf(df.format(distance)) + "m" + "  ";
        }
        return textDistance;
    }
}

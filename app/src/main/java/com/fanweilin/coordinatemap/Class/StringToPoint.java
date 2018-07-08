package com.fanweilin.coordinatemap.Class;

import com.baidu.mapapi.model.LatLng;
import com.fanweilin.coordinatemap.computing.JZLocationConverter;
import com.fanweilin.coordinatemap.computing.Location3TheConvert;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/6.
 */

public class StringToPoint {

    public static List<LatLng> getBdPoints(String s){
        List<LatLng> bdPoints=new ArrayList<>();
        if (s==null)
            return bdPoints;
        String[] points=s.split(";");
       for(int i=0;i<points.length;i++){
           try{String[]point=points[i].split(",");
           LatLng latLng=new LatLng(Double.valueOf(point[0]),Double.valueOf(point[1]));
           bdPoints.add(latLng);}
           catch (NumberFormatException e){

           }
       }
        return bdPoints;

    }
    public static String getGeoPoints(List<GeoPoint> pointList,int style){
        StringBuffer points=new StringBuffer();
        for(int k=0;k<pointList.size();k++){
            GeoPoint point= Location3TheConvert.ConverToGCJ2(pointList.get(k).getLatitude(),pointList.get(k).getLongitude(), style);
            points.append(String.valueOf(point.getLatitude()));
            points.append(",");
            points.append(String.valueOf(point.getLongitude()));
            if(k<pointList.size()-1){
                points.append(";");
            }
        }
        return points.toString();
    }
    public static String getBdPoints(List<LatLng>  pointList,int style){
        StringBuffer points=new StringBuffer();
        for(int k=0;k<pointList.size();k++){
            GeoPoint point= Location3TheConvert.ConverToGCJ2(pointList.get(k).latitude,pointList.get(k).longitude, style);
            points.append(String.valueOf(point.getLatitude()));
            points.append(",");
            points.append(String.valueOf(point.getLongitude()));
            if(k<pointList.size()-1){
                points.append(";");
            }
        }
        return points.toString();
    }
    public static List<GeoPoint> getGeoPoints(String s){
        if(s==null)
            return null;
        List<GeoPoint> geoPoints=new ArrayList<>();
        String[] points=s.split(";");
        for(int i=0;i<points.length;i++) {
            try {
                String[] point = points[i].split(",");
                GeoPoint latLng = new GeoPoint(Double.valueOf(point[0]), Double.valueOf(point[1]));
                geoPoints.add(latLng);
            }
           catch (NumberFormatException e){

                }
        }
        return  geoPoints;

    }
}

package com.fanweilin.coordinatemap.Measure;

import com.baidu.mapapi.model.LatLng;
import com.fanweilin.coordinatemap.Class.StringToPoint;
import com.fanweilin.coordinatemap.computing.JZLocationConverter;
import com.fanweilin.greendao.SqlPolygon;
import com.fanweilin.greendao.SqlPolyline;
import com.fanweilin.greendao.Sqlpoint;

import org.osmdroid.util.GeoPoint;

import java.util.List;

/**
 * Created by Administrator on 2017/7/24.
 */

public class Measure {
public static MeasureDistance setMeasuerParcel(SqlPolygon sqlPolygon){
        MeasureDistance  measureDistance=new MeasureDistance();
        measureDistance.setId(sqlPolygon.getId());
        measureDistance.setName(sqlPolygon.getName());
        measureDistance.setDescribe(sqlPolygon.getDescribe());
        measureDistance.setType(2);
        List<LatLng> sqlpoints=StringToPoint.getBdPoints(sqlPolygon.getPoints());
        for(int i=0;i<sqlpoints.size();i++){
            MeasurePoint point=new MeasurePoint();
            point.setLat(sqlpoints.get(i).latitude);
            point.setLon(sqlpoints.get(i).longitude);
            measureDistance.addPoint(point);
        }
        return  measureDistance;
    }
   public static MeasureDistance setMeasuerParcel(SqlPolyline sqlPolyline){
       MeasureDistance  measureDistance=new MeasureDistance();
       measureDistance.setId(sqlPolyline.getId());
       measureDistance.setName(sqlPolyline.getName());
       measureDistance.setDescribe(sqlPolyline.getDescribe());
        measureDistance.setType(1);
       List<LatLng> sqlpoints=StringToPoint.getBdPoints(sqlPolyline.getPoints());
        for(int i=0;i<sqlpoints.size();i++){
            MeasurePoint point=new MeasurePoint();
            point.setLat(sqlpoints.get(i).latitude);
            point.setLon(sqlpoints.get(i).longitude);
            measureDistance.addPoint(point);
        }
        return measureDistance;
    }
}

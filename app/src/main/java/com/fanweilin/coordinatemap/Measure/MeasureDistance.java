package com.fanweilin.coordinatemap.Measure;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.fanweilin.coordinatemap.computing.Computer;
import com.fanweilin.coordinatemap.computing.Distance;
import com.fanweilin.coordinatemap.computing.GaussXYDeal;
import com.fanweilin.coordinatemap.computing.Point;

import org.osmdroid.util.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/13 0013.
 */

public class MeasureDistance implements Serializable {
    //type 1距离 2面积
    private int type;
    private long id;
    private String name;
    private String describe;
    public List<MeasurePoint> measurePoints;

    public MeasureDistance() {

        this.measurePoints = new ArrayList<MeasurePoint>();
    }
    public void clear(){
        measurePoints.clear();
    }
    public void setType(int t){
        this.type=t;
    }
    public int getType(){
        return this.type;
    }
    public void addPoint(MeasurePoint point){
        switch (type){
            case 1:
                measurePoints.add(point);
                break;
            case 2:
                if (measurePoints.size()<3){
                    measurePoints.add(point);
                }
                if(measurePoints.size()>3){
                    measurePoints.add(measurePoints.size()-1,point);
                    measurePoints.add(measurePoints.size()-2,average(measurePoints.get(getLastIndex()-1),measurePoints.get(getLastIndex()-2)));
                    measurePoints.set(measurePoints.size()-1,average(measurePoints.get(getLastIndex()-1),measurePoints.get(0)));
                } else if (measurePoints.size()==3){
                    measurePoints.add(1,average(measurePoints.get(0),measurePoints.get(1)));
                    measurePoints.add(3,average(measurePoints.get(2),measurePoints.get(3)));
                    measurePoints.add(average(measurePoints.get(4),measurePoints.get(0)));
                }
                break;
        }

    }
 public List<GeoPoint> getGeoPoints(){
     List<GeoPoint> points=new ArrayList<GeoPoint>();
     for (MeasurePoint point:measurePoints
          ) {
         GeoPoint geoPoint=new GeoPoint(point.getLat(),point.getLon());
         points.add(geoPoint);
     }
     return points;
 }
 public MeasurePoint average(MeasurePoint point1,MeasurePoint point2){
     MeasurePoint point=new MeasurePoint();
     point.setLat((point1.getLat()+point2.getLat())/2);
     point.setLon((point1.getLon()+point2.getLon())/2);
     return  point;
 }
  public List<LatLng>  getBaiduPoints(){
        List<LatLng> points=new ArrayList<LatLng>();
        for (MeasurePoint point:measurePoints
                ) {
            LatLng bdPoint=new LatLng(point.getLat(),point.getLon());
            points.add(bdPoint);
        }
        return points;
    }
    public int getLastIndex(){
        return measurePoints.size()-1;
    }
    public double getDistance(){
        double distance=0;
        List<LatLng> points=getBaiduPoints();
      for (int i=0;i<points.size();i++){
          if (i>0){
              distance+=  DistanceUtil.getDistance(points.get(i),points.get(i-1));
          }
        }
        return distance;
    }

    public double getArea(){
       List<Point> points=new ArrayList<>();
        double midlongitude=measurePoints.get(0).getLon();
        for(int i=0;i<measurePoints.size();i++){
            Point point=new Point();
            double[]XY= GaussXYDeal.GaussToBLToGauss(measurePoints.get(i).getLat(),measurePoints.get(i).getLon(),midlongitude);
            point.setX(XY[0]);
            point.setY(XY[1]);
            points.add(point);
        }
        return Computer.area(points);
    }
    public void deleteLastPoint(){
        measurePoints.remove(getLastIndex());
    }
    public void back(){
        switch (type){
            case 1:
                if(measurePoints.size()>0){
                    measurePoints.remove(getLastIndex());
                }
                break;
            case 2:
                if(measurePoints.size()>6){
                    measurePoints.remove(getLastIndex()-1);
                    measurePoints.remove(getLastIndex()-1);
                    measurePoints.set(measurePoints.size()-1,average(measurePoints.get(getLastIndex()-1),measurePoints.get(0)));
                }else if(measurePoints.size()==1)
                {
                    measurePoints.remove(0);
                }else if(measurePoints.size()==6){
                    measurePoints.remove(getLastIndex());
                    measurePoints.remove(getLastIndex());
                    measurePoints.remove(getLastIndex());
                    measurePoints.remove(getLastIndex()-1);
                }else if(measurePoints.size()==2){
                    measurePoints.remove(getLastIndex());
                }
                break;
        }


    }
    public LatLng getLastBdPoint(){
        int lastIndex=measurePoints.size()-1;
        LatLng bdLatLng=new LatLng(measurePoints.get(lastIndex).getLat(),measurePoints.get(lastIndex).getLon());
        return bdLatLng;
    }
    public GeoPoint getLastGeoPoint(){
        int lastIndex=measurePoints.size()-1;
        GeoPoint geoPoint=new  GeoPoint(measurePoints.get(lastIndex).getLat(),measurePoints.get(lastIndex).getLon());
        return geoPoint;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}

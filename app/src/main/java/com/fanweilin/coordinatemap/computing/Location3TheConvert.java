package com.fanweilin.coordinatemap.computing;


import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.fanweilin.coordinatemap.Class.LatStyle;

import org.osmdroid.util.GeoPoint;

/**
 * Created by Administrator on 2016/9/14 0014.
 */
public class Location3TheConvert {
    public static final int WGS84=0;
    public static  final int Baidu=1;
    public static final int GCJ2=2;
    //转换成baidu坐标系
    public static LatLng ConverToBaidu(double latitude ,double longitude, int style){
        CoordinateConverter converter = new CoordinateConverter();
         LatLng bdlatLng=null;
           switch (style){
               case Baidu:
                   bdlatLng=new LatLng(latitude,longitude);
                   break;
               case WGS84:
                   converter.from(CoordinateConverter.CoordType.GPS);
                   converter.coord(new LatLng(latitude,longitude));
                   bdlatLng=converter.convert();
                   break;
               case GCJ2:
                   converter.from(CoordinateConverter.CoordType.COMMON);
                   converter.coord(new LatLng(latitude,longitude));
                   bdlatLng=converter.convert();
                   break;

           }
        return bdlatLng;
    }

    //转换成GCJ-2坐标系
    public static GeoPoint ConverToGCJ2(double latitude,double longitude,int style){
        GeoPoint geoPoint=null;
        JZLocationConverter.LatLng latLng=null;
        switch (style){
            case Baidu:
               latLng=JZLocationConverter.bd09ToGcj02(new JZLocationConverter.LatLng(latitude,longitude));
                geoPoint=new GeoPoint(latLng.getLatitude(),latLng.getLongitude());
                break;
            case WGS84:
                latLng=JZLocationConverter.wgs84ToGcj02(new JZLocationConverter.LatLng(latitude,longitude));
                geoPoint=new GeoPoint(latLng.getLatitude(),latLng.getLongitude());
                break;
            case GCJ2:
                geoPoint=new GeoPoint(latitude,longitude);
                break;
        }
        return geoPoint;
    }
    //转换成WGS84坐标系
    public static GeoPoint ConverToWGS84(double latitude,double longitude,int style){
        GeoPoint geoPoint=null;
        JZLocationConverter.LatLng latLng=null;
        switch (style){
            case Baidu:
                latLng=JZLocationConverter.bd09ToWgs84(new JZLocationConverter.LatLng(latitude,longitude));
                geoPoint=new GeoPoint(latLng.getLatitude(),latLng.getLongitude());
                break;
            case WGS84:
                geoPoint=new GeoPoint(latitude,longitude);
                break;
            case GCJ2:
                latLng=JZLocationConverter.gcj02ToWgs84(new JZLocationConverter.LatLng(latitude,longitude));
                geoPoint=new GeoPoint(latLng.getLatitude(),latLng.getLongitude());
                break;
        }
        return geoPoint;
    }
    public static LatLng ComanLngConvertBdLngt(LatLng lat, int coordstyle, int datastyle) {
        LatLng bdLng = null;
        CoordinateConverter converter = new CoordinateConverter();
        switch (coordstyle) {
            case LatStyle.GPSSYTELE://84
                if (datastyle == LatStyle.DEGREE) {
                    converter.from(CoordinateConverter.CoordType.GPS);
                    converter.coord(lat);
                    bdLng = converter.convert();
                } else {
                    ConvertLatlng convertLatlng = new ConvertLatlng();
                    double latitude = ConvertLatlng.convertToDecimalByString(String.valueOf(lat.latitude));
                    double longtitude = ConvertLatlng.convertToDecimalByString(String.valueOf(lat.longitude));
                    LatLng latdimacal = new LatLng(latitude, longtitude);
                    converter.from(CoordinateConverter.CoordType.GPS);
                    converter.coord(latdimacal);
                    bdLng = converter.convert();
                }
                break;
            case LatStyle.BAIDUMAPSTYELE://bd
                if (datastyle == LatStyle.DEGREE) {
                    return lat;
                } else {
                    ConvertLatlng convertLatlng = new ConvertLatlng();
                    double latitude = ConvertLatlng.convertToDecimalByString(String.valueOf(lat.latitude));
                    double longtitude = ConvertLatlng.convertToDecimalByString(String.valueOf(lat.longitude));
                    bdLng = new LatLng(latitude, longtitude);
                }
                break;
            case LatStyle.OTHERS://
                if (datastyle == LatStyle.DEGREE) {
                    converter.from(CoordinateConverter.CoordType.COMMON);
                    converter.coord(lat);
                    bdLng = converter.convert();
                } else {
                    converter.from(CoordinateConverter.CoordType.COMMON);
                    ConvertLatlng convertLatlng = new ConvertLatlng();
                    double latitude = ConvertLatlng.convertToDecimalByString(String.valueOf(lat.latitude));
                    double longtitude = ConvertLatlng.convertToDecimalByString(String.valueOf(lat.longitude));
                    LatLng latdimacal = new LatLng(latitude, longtitude);
                    converter.coord(latdimacal);
                    bdLng = converter.convert();
                }
                break;
        }
        return bdLng;
    }
}

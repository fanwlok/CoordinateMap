package com.fanweilin.coordinatemap.Class;

import com.fanweilin.coordinatemap.R;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.PointData;

/**
 * Created by Administrator on 2018/5/15.
 */

public class Marker {
    public static final int blue=1;
    public static final int red=2;
    public static final int green=3;
    public static final int yellow=4;
    public static final int zs=5;
    public static int REBLUEID= R.mipmap.blu_blank_map_pin_48px_552642_easyicon;
    public static int REREDID=R.mipmap.red_blank_48px_553042_easyicon;
    public static int REGREENID=R.mipmap.gr_icon;
    public static int REYEID=R.mipmap.yl_icon;
    public static int REZS=R.mipmap.zs_icon;
    public static int getResource(int color){
        switch (color){
            case 1:
                return REBLUEID;
            case 2:
                return REREDID;
            case 3:
                return REGREENID;
            case 4:
                return REYEID;
            case 5:
                return REZS;
                default:
                    return REBLUEID;

        }
    }
  public static int  getResource(PointData pointData){
        int REID=REBLUEID;
        if(pointData.getMarkerid()!=null){
           REID=getResource(pointData.getMarkerid());
        }else {
            Files files =pointData.getFiles();
            if(files.getMarkerid()!=null){
                REID=getResource(files.getMarkerid());
            }
        }
        return REID;
    }
public static int getMarkerId(PointData pointData){
    int REID=blue;
    if(pointData.getMarkerid()!=null){
        REID=pointData.getMarkerid();
    }else {
        Files files =pointData.getFiles();
        if(files.getMarkerid()!=null){
            REID=files.getMarkerid();
        }
    }
    return REID;
}
}


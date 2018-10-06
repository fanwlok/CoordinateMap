package com.fanweilin.coordinatemap.MapSource;

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.MapTileIndex;

/**
 * Created by Administrator on 2016/9/11 0011.
 */
public class GoogleMapsSatellite extends OnlineTileSourceBase {
private static final String[] BASE_URL = {
        "http://mt0.google.cn/vt/lyrs=s&hl=zh-CN&gl=CN&scale=2&src=app",
        "http://mt1.google.cn/vt/lyrs=s&hl=zh-CN&gl=CN&scale=2&src=app",
        "http://mt2.google.cn/vt/lyrs=s&hl=zh-CN&gl=CN&scale=2&src=app",
        "http://mt3.google.cn/vt/lyrs=s&hl=zh-CN&gl=CN&scale=2&src=app",
        };
private static final String NAME ="google卫星图";

public GoogleMapsSatellite() {
        super(NAME, 1, 20, 256,
        "", BASE_URL);
        }

@Override
public String getTileURLString(final long pMapTileIndex) {
        return getBaseUrl() + "&x=" + MapTileIndex.getX(pMapTileIndex) + "&y=" + MapTileIndex.getY(pMapTileIndex) +
                "&z=" + MapTileIndex.getZoom(pMapTileIndex)+mImageFilenameEnding;
}

}

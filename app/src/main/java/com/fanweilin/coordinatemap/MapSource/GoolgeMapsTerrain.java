package com.fanweilin.coordinatemap.MapSource;


import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.MapTileIndex;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class GoolgeMapsTerrain extends OnlineTileSourceBase {
    private static final String[] BASE_URL = {
            "http://mt0.google.cn/vt/lyrs=t,r&hl=zh-CN&gl=cn&scale=4&src=app",
            "http://mt1.google.cn/vt/lyrs=t,r&hl=zh-CN&gl=cn&scale=4&src=app",
            "http://mt2.google.cn/vt/lyrs=t,r&hl=zh-CN&gl=cn&scale=4&src=app",
            "http://mt3.google.cn/vt/lyrs=t,r&hl=zh-CN&gl=cn&scale=4&src=app",


    };
    private static final String NAME ="google地形图";

    public  GoolgeMapsTerrain () {
        super(NAME, 1, 20, 256,
                "", BASE_URL);
    }

    @Override
    public String getTileURLString(final long pMapTileIndex) {
        return getBaseUrl() + "&x=" + MapTileIndex.getX(pMapTileIndex) + "&y=" + MapTileIndex.getY(pMapTileIndex) +
                "&z=" + MapTileIndex.getZoom(pMapTileIndex)+mImageFilenameEnding;
    }
}

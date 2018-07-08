package com.fanweilin.coordinatemap.MapSource;

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.MapTileIndex;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class GoogleMaps extends OnlineTileSourceBase {
    private static final String[] BASE_URL = {
            "http://mt0.google.cn/vt/lyrs=m,traffic&hl=zh-CN&gl=cnp&scale=2",
    };
    private static final String NAME ="google地图";

    public  GoogleMaps() {
        super(NAME, 1, 22, 256,
                "", BASE_URL);
    }

    @Override
    public String getTileURLString(final long pMapTileIndex) {
        return getBaseUrl() + "&x=" + MapTileIndex.getX(pMapTileIndex) + "&y=" + MapTileIndex.getY(pMapTileIndex) +
                "&z=" + MapTileIndex.getZoom(pMapTileIndex)+mImageFilenameEnding;
    }
}

package com.fanweilin.coordinatemap.MapSource;

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.MapTileIndex;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class GaoDeMapsTileSource  extends OnlineTileSourceBase {
    private static final String[] BASE_URL = {
            "http://wprd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=2&style=7"
    };
    private static final String NAME ="gaodemaps";
    public GaoDeMapsTileSource() {
        super(NAME, 1, 19, 256,
                "", BASE_URL);
    }
    @Override
    public String getTileURLString(final long pMapTileIndex) {
      return getBaseUrl() + "&x=" + MapTileIndex.getX(pMapTileIndex) + "&y=" + MapTileIndex.getY(pMapTileIndex) +
              "&z=" + MapTileIndex.getZoom(pMapTileIndex)+mImageFilenameEnding;
    }
}

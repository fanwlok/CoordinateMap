package com.fanweilin.coordinatemap.MapSource;

import org.osmdroid.tileprovider.tilesource.QuadTreeTileSource;
import org.osmdroid.util.MapTileIndex;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class BingMapsTileSource extends QuadTreeTileSource {

    private static final String[] BASE_URL = {
            "http://a0.ortho.tiles.virtualearth.net/tiles/a"
              };
    private static final String NAME ="BING卫星图";
    public BingMapsTileSource() {
        super(NAME, 1, 19, 256, "", BASE_URL);
    }

    public String getTileURLString(final long pMapTileIndex) {
        return getBaseUrl() + quadTree(pMapTileIndex)+".jpg?g=45";
    }
}

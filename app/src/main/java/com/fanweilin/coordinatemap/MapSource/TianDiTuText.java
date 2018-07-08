package com.fanweilin.coordinatemap.MapSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.MapTileIndex;

/**
 * Created by Administrator on 2016/9/14 0014.
 */
public class TianDiTuText extends OnlineTileSourceBase {
    private static final String[] BASE_URL = {
            "http://t0.tianditu.cn/DataServer?T=cva_w"
    };
    private static final String NAME ="天地图文字";
    public  TianDiTuText () {
        super(NAME, 3,18, 256,
                "", BASE_URL);
    }
    @Override
    public String getTileURLString(final long pMapTileIndex) {
        return getBaseUrl() + "&X=" + MapTileIndex.getX(pMapTileIndex) + "&Y=" + MapTileIndex.getY(pMapTileIndex) +
                "&L=" + MapTileIndex.getZoom(pMapTileIndex)+mImageFilenameEnding;
    }
}

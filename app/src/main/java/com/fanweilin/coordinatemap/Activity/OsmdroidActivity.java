package com.fanweilin.coordinatemap.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fanweilin.coordinatemap.R;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class OsmdroidActivity extends AppCompatActivity {
    private MapView map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osmdroid);
        init();
    }
    private void init(){

           map= (MapView) findViewById(R.id.map);
           map.setTileSource(TileSourceFactory.MAPNIK);
        //定位当前位置，北京市西长安街复兴路
        GeoPoint center = new GeoPoint(39.901873, 116.326655);


    }
}

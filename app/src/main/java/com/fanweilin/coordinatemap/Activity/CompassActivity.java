package com.fanweilin.coordinatemap.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.widget.CompassView;

public class CompassActivity extends AppCompatActivity {
    private CompassView compass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        compass = (CompassView) findViewById(R.id.compass);
    }
    @Override
    public void onResume(){
        super.onResume();
        compass.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        compass.onPause();
    }

}

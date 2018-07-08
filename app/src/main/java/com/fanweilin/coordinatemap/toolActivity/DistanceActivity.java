package com.fanweilin.coordinatemap.toolActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fanweilin.coordinatemap.Activity.MainMapsActivity;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.Distance;

import java.text.DecimalFormat;

public class DistanceActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText beginLatitude;
    private EditText beginLongitude;
    private EditText endLatitude;
    private EditText endlongitude;
    private Button btnComputer;
    private Button btnShow;
    private TextView textView;
    private Toolbar toolbar;
    private SharedPreferences spf;
    private final String SPFNAME = "distancespf";
    private final String SPFBGLAT = "beginlatitude";
    private final String SPFBGLON = "beginlongitude";
    private final String SPFENDLAT = "endlatitude";
    private final String SPFENDLON = "endlongitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void init()

    {
        toolbar = findViewById(R.id.toolbar);
        beginLatitude = findViewById(R.id.begin_latitude);
        beginLongitude = findViewById(R.id.begin_longitude);
        endLatitude = findViewById(R.id.end_latitude);
        endlongitude = findViewById(R.id.end_longitude);
        btnComputer = findViewById(R.id.btn_distance_computer);
        btnShow = findViewById(R.id.btn_distance_show);
        textView = findViewById(R.id.tv_distance_content);
        btnShow.setOnClickListener(this);
        btnComputer.setOnClickListener(this);
        spf = getSharedPreferences(SPFNAME, Context.MODE_APPEND);
        beginLatitude.setText(spf.getString(SPFBGLAT, ""));
        beginLongitude.setText(spf.getString(SPFBGLON, ""));
        endLatitude.setText(spf.getString(SPFENDLAT, ""));
        endlongitude.setText(spf.getString(SPFENDLON, ""));



    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_distance_computer:
                if (!isnull()) {
                    Toast.makeText(DistanceActivity.this, "数据不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    double beginLat = Double.parseDouble(beginLatitude.getText().toString());
                    double beginLon = Double.parseDouble(beginLongitude.getText().toString());
                    double endLat = Double.parseDouble(endLatitude.getText().toString());
                    double endLon = Double.parseDouble(endlongitude.getText().toString());
                    double s = Distance.GetDistance(beginLat, beginLon, endLat, endLon);
                    puteditor();
                    String distance;
                    if (s >= 1000) {
                        DecimalFormat df = new DecimalFormat("#.00000");
                        distance = "距离为:"+String.valueOf(df.format(s / 1000)) + "km";
                    } else {
                        DecimalFormat df = new DecimalFormat("#.00");
                        distance = "距离为:"+String.valueOf(df.format(s)) + "m";
                    }
                    textView.setText(distance);
                }
                break;
            case R.id.btn_distance_show:
                if (!isnull()) {
                    Toast.makeText(DistanceActivity.this, "数据不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    double beginWgsLat = Double.parseDouble(beginLatitude.getText().toString());
                    double beginWgsLon = Double.parseDouble(beginLongitude.getText().toString());
                    double endWgsLat = Double.parseDouble(endLatitude.getText().toString());
                    double endWgsLon = Double.parseDouble(endlongitude.getText().toString());
                    double[] data = {beginWgsLat, beginWgsLon, endWgsLat, endWgsLon};
                    puteditor();
                    Intent intent = new Intent();
                    intent.putExtra(MainMapsActivity.DISTANCELAT, data);
                    intent.putExtra(MainMapsActivity.DISTANCEACTIVITY, "DistanceActivity");
                    intent.setClass(DistanceActivity.this, MainMapsActivity.class);
                    startActivity(intent);
                }

                break;
        }
    }

    public void puteditor() {
        SharedPreferences.Editor editor = spf.edit();
        editor.putString(SPFBGLAT, beginLatitude.getText().toString());
        editor.putString(SPFBGLON, beginLongitude.getText().toString());
        editor.putString(SPFENDLAT, endLatitude.getText().toString());
        editor.putString(SPFENDLON, endlongitude.getText().toString());
        editor.commit();
    }

    public boolean isnull() {
        boolean tag = true;
        if (TextUtils.isEmpty(beginLatitude.getText().toString())) {
            tag = false;
        }
        if (TextUtils.isEmpty(beginLongitude.getText().toString())) {
            tag = false;
        }
        if (TextUtils.isEmpty(endLatitude.getText().toString())) {
            tag = false;
        }
        if (TextUtils.isEmpty(endlongitude.getText().toString())) {
            tag = false;
        }
        return tag;
    }
}

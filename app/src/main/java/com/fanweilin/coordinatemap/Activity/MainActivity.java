package com.fanweilin.coordinatemap.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.fanweilin.coordinatemap.Compass.CompassActivity;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.gpsTest.GpsTestActivity;
import com.fanweilin.coordinatemap.widget.MapAdapter;
import com.fanweilin.coordinatemap.widget.MyAdpter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.jackyh.watermarkcamera.ui.WatermarkCameraActivity;


public class MainActivity extends AppCompatActivity {
     private CardView mCardView;
     private List<Map<String,Object>> mDatas;
     private RecyclerView recyclerView;
     private MyAdpter myAdpter;
    private final int REQUEST_PHONE_PERMISSIONS = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intData();
        initView();
    }
    private void initView(){
         Toolbar toolbar = findViewById(R.id.toolbar);
         toolbar.setNavigationIcon(R.mipmap.earth_globe_48px);
         toolbar.setTitle("经纬度定位");
        mCardView=findViewById(R.id.card_map);
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(MainActivity.this,MainMapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        recyclerView=  findViewById(R.id.recycle_main);
        recyclerView.setLayoutManager( new GridLayoutManager(this, 2));
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        myAdpter=new MyAdpter(this,mDatas);
        recyclerView.setAdapter(myAdpter);
        myAdpter.setOnItemClickListener(new MapAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = recyclerView.getChildAdapterPosition(view);

                switch (position){
                    case 0:
                        Intent intent=new Intent();
                        intent.setClass(MainActivity.this, GpsTestActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent1=new Intent();
                        intent1.setClass(MainActivity.this, CompassActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        final List<String> permissionsList = new ArrayList<>();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if ((checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
                                permissionsList.add(Manifest.permission.CAMERA);
                            if (permissionsList.size() == 0) {
                                Intent intent2 = new Intent();
                                intent2.setClass(MainActivity.this, WatermarkCameraActivity.class);
                                startActivity(intent2);
                            } else {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_PHONE_PERMISSIONS);
                            }
                        }else {
                            Intent intent2 = new Intent();
                            intent2.setClass(MainActivity.this, WatermarkCameraActivity.class);
                            startActivity(intent2);
                        }
                        break;
                    case 3:
                        Intent intent3=new Intent();
                        intent3.setClass(MainActivity.this, ToolBoxActivity.class);
                        startActivity(intent3);
                        break;
                    case 4:
                        Intent intent4=new Intent();
                        intent4.putExtra("url","https://www.kancloud.cn/fwlok88/fwl");
                        intent4.setClass(MainActivity.this,WebActivity.class);
                        startActivity(intent4);
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view) {

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent2 = new Intent();
                    intent2.setClass(MainActivity.this, WatermarkCameraActivity.class);
                    startActivity(intent2);
                } else {
                    Toast.makeText(this, getString(R.string.need_permission), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
private void intData(){
        mDatas=new ArrayList<>();
        String [] iconnames={"GPS状态","指南针","水印相机","计算工具","帮助"};
        int [] imgID={R.mipmap.main_gpstest,R.mipmap.main_composs,R.mipmap.main_photo,R.mipmap.main_measure,R.mipmap.help,};
        for(int i=0;i<5;i++){
             Map map=new HashMap();
             map.put("name",iconnames[i]);
             map.put("imgid",imgID[i]);
             mDatas.add(map);
        }
}

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}

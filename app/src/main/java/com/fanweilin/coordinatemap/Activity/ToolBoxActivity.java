package com.fanweilin.coordinatemap.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanweilin.coordinatemap.Compass.CompassActivity;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.toolActivity.ConvertActivity;
import com.fanweilin.coordinatemap.toolActivity.CoordinateActivity;
import com.fanweilin.coordinatemap.toolActivity.DistanceActivity;
import com.fanweilin.coordinatemap.widget.MyGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolBoxActivity extends AppCompatActivity {
   private MyGridView gridView;
    private List<Map<String ,Object>> mapList;
    private Toolbar toolbar;
   private GridAdapter gridAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_box);
        init();
        getadpterdata();
        gridAdapter=new GridAdapter(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gridView.setAdapter(gridAdapter);
    }
    private void init(){
        toolbar = findViewById(R.id.toolbar);
        gridView= findViewById(R.id.line_grideview);
        gridView.setOnItemClickListener(new GridItemClick());
    }
    private void getadpterdata(){
        mapList=new ArrayList<Map<String, Object>>();
        int[] ivId={R.mipmap.computer_compass,R.mipmap.computer_transformation,
                R.mipmap.computer_coordinate,R.mipmap.computer_distance};
        String[] tvContent={"指南针","经纬度换算","坐标正反算","距离计算"};
        for (int i=0;i<=3;i++){
            Map<String ,Object> map=new HashMap<String ,Object>();
            map.put("iv",ivId[i]);
            map.put("tv",tvContent[i]);
            mapList.add(map);
        }

    }
    private class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        public GridAdapter(Context context){
           inflater=LayoutInflater.from(context);
       }
        @Override
        public int getCount() {
            return mapList.size();
        }

        @Override
        public Object getItem(int position) {
            return mapList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           convertView=inflater.inflate(R.layout.griditem,null);
            ImageView iv= convertView.findViewById(R.id.iv);
            TextView tv= convertView.findViewById(R.id.tv);
            iv.setImageResource((Integer) mapList.get(position).get("iv"));
            tv.setText((String)mapList.get(position).get("tv"));
            return convertView;
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    private class GridItemClick implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent=new Intent();
            switch (position){
                case 0:
                    intent.setClass(ToolBoxActivity.this, CompassActivity.class);
                    break;
                case 1:
                    intent.setClass(ToolBoxActivity.this, ConvertActivity.class);
                    break;
                case 2:
                    intent.setClass(ToolBoxActivity.this, CoordinateActivity.class);
                    break;
                case 3:
                    intent.setClass(ToolBoxActivity.this,DistanceActivity.class);
                    break;
            }
            startActivity(intent);
        }
    }
}

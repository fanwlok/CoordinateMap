package com.fanweilin.coordinatemap.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Overlay;
import com.fanweilin.coordinatemap.Class.MapsType;
import com.fanweilin.coordinatemap.Class.PointDataParcel;
import com.fanweilin.coordinatemap.DataModel.User;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.JZLocationConverter;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.ShowDataDao;
import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;
import com.qq.e.comm.constants.Constants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView listView;
    private List<Map<String, Object>> listdata;
    private SharedPreferences spfmaps;
    private SharedPreferences spf;
    private int mapstyle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void init() {
        spfmaps = getSharedPreferences("spfmaps", Context.MODE_PRIVATE);
        spf = getSharedPreferences(User.SPFNAEM, Context.MODE_PRIVATE);
        mapstyle = spfmaps.getInt("mapstyle", 0);
        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.list_map);
        toolbar.setTitle("地图选择");
        getListData();
        listView.setAdapter(new DataAdpter(this));
        listView.setOnItemClickListener(new ListItemclick());
    }
    public void getListData() {
        listdata = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            switch (i) {
                case 0:
                    map.put("mapname", "百度矢量");
                    break;
                case 1:
                    map.put("mapname", "百度卫星");
                    break;
                case 2:
                    map.put("mapname", "Google地图");
                    break;
                case 3:
                    map.put("mapname", "Google地形图");
                    break;
                case 4:
                    map.put("mapname", "Google卫星图");
                    break;
                case 5:
                    map.put("mapname", "Google卫星混合图");
                    break;
                case 6:
                    map.put("mapname", "Bing高清卫星图");
                    break;
                case 7:
                    map.put("mapname", "高德地图");
                    break;
                case 8:
                    map.put("mapname", "天地图卫星图");
                    break;
                case 9:
                    map.put("mapname", "天地图");
                    break;
            }
            listdata.add(map);
        }
    }

  public class ListItemclick implements  AdapterView.OnItemClickListener{

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                finishWithResult(position);
      }
  }
    public class DataAdpter extends BaseAdapter {
        private LayoutInflater inflater;
        public DataAdpter(Context context) {
            inflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return listdata.size();
        }

        @Override
        public Object getItem(int position) {
            return listdata.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            TextView mapsname;
            ImageView imageView;
            view = inflater.inflate(R.layout.list_item_map, null);
            mapsname = view.findViewById(R.id.tv_mapsname);
            imageView= view.findViewById(R.id.img_mapcheck);
            String mapname= (String) listdata.get(position).get("mapname");
            if(mapstyle==position){
                imageView.setImageResource(R.mipmap.check_64px);
            }
            mapsname.setText(mapname);
            return view;
        }
    }
    private void finishWithResult(int maptype) {
        Bundle conDate = new Bundle();
        conDate.putInt(MapsType.MAPNAME, maptype);
        Intent intent = new Intent();
        intent.putExtras(conDate);
        setResult(RESULT_OK, intent);
        finish();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}

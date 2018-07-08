package com.fanweilin.coordinatemap.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.GaussXYDeal;
import com.fanweilin.greendao.CoordinateData;
import com.fanweilin.greendao.CoordinateDataDao;
import com.fanweilin.greendao.DaoSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuassActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }
    private Toolbar toolbar;
    private ListView listView;
    private List<CoordinateData> coordinateDatas;
    private List<Map<String, Object>> listdata;
    private SharedPreferences spfcoor;
    private long coorid;
    private DaoSession mDaoSession;
    private DataAdpter listAdpter;
    private EditText editx;
    private EditText edity;
    private Button btn;
    private TextView tx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guass);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(new MyOnMenuItemClick());
    }
    private void init(){
        editx= findViewById(R.id.editx);
        edity= findViewById(R.id.edity  );
        tx= findViewById(R.id.tv_content);
        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.list_guass);
        spfcoor = getSharedPreferences("spfcoor", Context.MODE_PRIVATE);
        coordinateDatas=new ArrayList<CoordinateData>();
        listdata=new ArrayList<Map<String,Object>>();
        coorid=spfcoor.getLong("coorid",-1);
        mDaoSession = data.getmDaoSession();
        getlistdata();
        listAdpter=new DataAdpter(this);
        listView.setAdapter(listAdpter);
        listView.setOnItemClickListener(new onListItemClick());
    }
    public void getlistdata(){
        listdata.clear();
        coordinateDatas.clear();
        coordinateDatas=getCoordinateDao().loadAll();
        for(int i=0;i<coordinateDatas.size();i++){
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("name",coordinateDatas.get(i).getName());
            map.put("id",coordinateDatas.get(i).getId());
            map.put("midlat",coordinateDatas.get(i).getMidlat());
            map.put("difx",coordinateDatas.get(i).getDifx());
            map.put("dify",coordinateDatas.get(i).getDify());
            listdata.add(map);
        }

    }
    private CoordinateDataDao getCoordinateDao() {
        return mDaoSession.getCoordinateDataDao();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_coordinate, menu);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }



    private class MyOnMenuItemClick implements Toolbar.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.item_add:
                    editCoordinate(-1);
                    break;
            }
            return false;
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
            TextView coordinatename;
            ImageView imageView;
            view = inflater.inflate(R.layout.list_item_map, null);
            coordinatename = view.findViewById(R.id.tv_mapsname);
            imageView= view.findViewById(R.id.img_mapcheck);
            String mapname= (String) listdata.get(position).get("name");
            if(coorid==(long)listdata.get(position).get("id")){
                imageView.setImageResource(R.mipmap.check_64px);
            }
            coordinatename.setText(mapname);
            return view;
        }
    }
    public class onListItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                long id= (long) listdata.get(i).get("id");
                onListItemClick(id);
        }
    }
    public String[] dataItems = {"选择", "删除", "编辑"};
    public class DialogListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public DialogListAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return dataItems.length;
        }

        @Override
        public Object getItem(int position) {
            return dataItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.list_datamanger_dialog, null);
            TextView textView = view.findViewById(R.id.tv_datamanager_dialog);
            textView.setText(dataItems[position]);
            return view;
        }
    }

    private void onListItemClick(final long id) {
        ListView listView = new ListView(this);
        listView.setAdapter(new DialogListAdapter(this));
        final AlertDialog.Builder builder = new AlertDialog.Builder(GuassActivity.this);
        final AlertDialog dialog = builder.setView(listView).show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                final SharedPreferences.Editor editor= spfcoor.edit();
                switch (position) {
                    case 0:
                        editor.putLong("coorid",id);
                        editor.commit();
                        coorid=id;
                        listAdpter.notifyDataSetChanged();
                        dialog.dismiss();
                        break;
                    case 1:
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(GuassActivity.this);
                        builder1.setTitle("是否删除数据").setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                data.deleteCoorDataById(id);
                                if (coorid==id){
                                    editor.putLong("coorid",-1);
                                    editor.commit();
                                }
                                getlistdata();
                              listAdpter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                        dialog.dismiss();
                        break;
                    case 2:
                        editCoordinate(id);
                        dialog.dismiss();
                        break;
                }
            }
        });

    }
    public  void editCoordinate(final long id ){

        LayoutInflater inflater= getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_coordinate, null);
        final EditText editmidlat= dialogView.findViewById(R.id.et_midlat);
        final EditText editlat= dialogView.findViewById(R.id.et_lat);
        final EditText editlon= dialogView.findViewById(R.id.et_lon);
        final EditText editx= dialogView.findViewById(R.id.et_x);
        final EditText edity= dialogView.findViewById(R.id.et_y);
        final EditText editname= dialogView.findViewById(R.id.et_name);
        AlertDialog.Builder buildersearch = new AlertDialog.Builder(GuassActivity.this);
        buildersearch.setView(dialogView);
        if(id>-1){
            CoordinateData coordinateData=data.findOrerById(id);
            editname.setText(String.valueOf(coordinateData.getName()));
            editmidlat.setText(String.valueOf(coordinateData.getMidlat()));
            editlat.setText(String.valueOf(coordinateData.getLat()));
            editlon.setText(String.valueOf(coordinateData.getLon()));
            editx.setText(String.valueOf(coordinateData.getX()));
            edity.setText(String.valueOf(coordinateData.getY()));
        }

        buildersearch.setTitle("新增坐标系");
        buildersearch.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

          try {
              if (id > -1) {

                  CoordinateData coordinateData = data.findOrerById(id);
                  if (coordinateData.getName().equals("国家2000坐标系")) {
                      double midlat = Double.parseDouble(editmidlat.getText().toString());
                      coordinateData.setMidlat(midlat);
                  } else {
                      double midlat = Double.parseDouble(editmidlat.getText().toString());
                      double lat = Double.parseDouble(editlat.getText().toString());
                      double lon = Double.parseDouble(editlon.getText().toString());
                      double x = Double.parseDouble(editx.getText().toString());
                      double y = Double.parseDouble(edity.getText().toString());
                      String name = editname.getText().toString();
                      double[] xy = GaussXYDeal.DifXy(lat, lon, x, y, midlat);
                      coordinateData.setName(name);
                      coordinateData.setMidlat(midlat);
                      coordinateData.setDifx(xy[0]);
                      coordinateData.setDify(xy[1]);
                      coordinateData.setLat(lat);
                      coordinateData.setLon(lon);
                      coordinateData.setX(x);
                      coordinateData.setY(y);
                  }
                  data.updateCoor(coordinateData);
              } else {
                  double midlat = Double.parseDouble(editmidlat.getText().toString());
                  double lat = Double.parseDouble(editlat.getText().toString());
                  double lon = Double.parseDouble(editlon.getText().toString());
                  double x = Double.parseDouble(editx.getText().toString());
                  double y = Double.parseDouble(edity.getText().toString());
                  String name = editname.getText().toString();
                  double[] xy = GaussXYDeal.DifXy(lat, lon, x, y, midlat);
                  CoordinateData coordinateData = new CoordinateData();
                  coordinateData.setName(name);
                  coordinateData.setMidlat(midlat);
                  coordinateData.setDifx(xy[0]);
                  coordinateData.setDify(xy[1]);
                  coordinateData.setLat(lat);
                  coordinateData.setLon(lon);
                  coordinateData.setX(x);
                  coordinateData.setY(y);
                  data.createCoorData(coordinateData);
              }
          }catch (Exception e) {
              Toast.makeText(GuassActivity.this,"数据格式错误",Toast.LENGTH_SHORT).show();
          }
                getlistdata();
                listAdpter.notifyDataSetChanged();
            }
        });
        buildersearch.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        buildersearch.create().show();
    }

}


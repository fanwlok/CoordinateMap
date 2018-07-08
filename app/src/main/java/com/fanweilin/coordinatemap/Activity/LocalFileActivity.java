package com.fanweilin.coordinatemap.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fanweilin.coordinatemap.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalFileActivity extends AppCompatActivity {
    private String path;
    private ListView list;
    private Myadpter adapter;
    private List<Map<String, Object>> mData;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_file);
        initView();
        initData();
    }
    private void initView(){
        toolbar= findViewById(R.id.toolbar);
        list= findViewById(R.id.list);


    }
    private void initData(){
        //本地路径
        path=data.BASE_PATH;
        //toolbar设置
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_addfile_help:
                        onHelpClick();
                        break;
                }
                return false;
            }
        });
        //listview设置
        mData =getData();
        adapter = new Myadpter(this);
        list.setAdapter(adapter);
        // list.setOnItemClickListener(new onListItemClick());
    }
    private void onHelpClick(){
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_addfile, menu);
        return true;
    }
    public class Myadpter extends BaseAdapter {
        LayoutInflater inflater;

        public Myadpter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public class ViewHolder {
            ImageView img;
            TextView titleview;
            TextView pathview;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder ;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.list_file, null);
                holder.img = view.findViewById(R.id.img);
                holder.titleview = view.findViewById(R.id.title);
                holder.pathview = view.findViewById(R.id.path);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.img.setBackgroundResource((Integer) mData.get(i).get("img"));
            holder.titleview.setText((String) mData.get(i).get("title"));
            holder.pathview.setText((String) mData.get(i).get("path"));
            return view;
        }

    }
    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;
        File f = new File(path);
        File[] files = f.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                map = new HashMap<String, Object>();
                map.put("title", files[i].getName());
                map.put("path", files[i].getAbsolutePath());
                if (files[i].isDirectory()) {
                    map.put("img", R.mipmap.ex_folder);

                } else {
                    map.put("img", R.mipmap.ex_doc);
                }
                list.add(map);
            }
        }
        return list;
    }
}

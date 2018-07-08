package com.fanweilin.coordinatemap.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.fanweilin.coordinatemap.Class.LatStyle;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.greendao.ShowData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFileActivity extends AppCompatActivity implements View.OnClickListener{
    private final String mDir = Environment.getExternalStorageDirectory().getPath();
    private List<Map<String, Object>> mData;
    private String path;
    private ListView list;
    private Myadpter adapter;
    private int CoordStyle = LatStyle.GPSSYTELE;
    private int DataStye = LatStyle.DEGREE;
    private Toolbar toolbar;
    private Button btnShow;
    private Button btnCancle;
    private Button btnShare;
    private Button btnEdit;
    private Button btnAll;
    private RelativeLayout rlEdit;
    private LinearLayout layoutShow;
    private boolean show = false;
    private boolean isSelectALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);
        init();

    }
    private void init(){
        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_addfile_help:
                        onClick();
                        break;
                    case R.id.item_refreshfile:
                        refresh(data.BASE_PATH);
                        Toast.makeText(AddFileActivity.this,"电脑端无法看到文件,请先刷新",Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        path = data.BASE_PATH;
        mData = getData();
        list= findViewById(R.id.list);
        adapter = new Myadpter(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new onListItemClick());
        rlEdit = findViewById(R.id.rl_server);
        layoutShow = findViewById(R.id.layoutshow);
        btnCancle = findViewById(R.id.btn_cancel);
        btnShow = findViewById(R.id.btn_show);
        btnShare = findViewById(R.id.btn_delete);
        btnEdit = findViewById(R.id.btn_edit);
        btnAll = findViewById(R.id.btn_all);
        btnCancle.setOnClickListener(this);
        btnShow.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnAll.setOnClickListener(this);
        isSelectALL = false;
    }
    public void refresh(String path){
        File f = new File(path);
        File file[] = f.listFiles();
        for (int i=0; i < file.length; i++)
        {

            if (file[i].isDirectory()){
                refresh(file[i].getAbsolutePath());
            }else {
                MediaScannerConnection.scanFile(this, new String[] {file[i].getAbsolutePath()}, null, null);
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_addfile, menu);
        return true;
    }
    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;
        File f = new File(path);
        File[] files = f.listFiles();
        if (!path.equals("mDir")) {
            map = new HashMap<String, Object>();
            map.put("title", "返回上一级");
            map.put("img", R.mipmap.icon_folder);
            map.put("path", f.getParent());
            list.add(map);
        }
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                map = new HashMap<String, Object>();
                map.put("title", files[i].getName());
                map.put("path", files[i].getAbsolutePath());
                if (files[i].isDirectory()) {
                    map.put("img", R.mipmap.icon_folder);

                } else {
                    map.put("img", R.mipmap.icon_unknown1);
                }
                list.add(map);
            }
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_edit:
                if (!show) {
                    show = true;
                    rlEdit.setVisibility(View.GONE);
                    layoutShow.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    uncheckedAll();

                }
                break;
            case R.id.btn_show:
                SparseBooleanArray checkedArray = new SparseBooleanArray();
                checkedArray = list.getCheckedItemPositions();
                for (int i = 0; i < checkedArray.size(); i++) {
                    if (checkedArray.valueAt(i)) {
                        int k = checkedArray.keyAt(i);
                        String path = (String) mData.get(k).get("path");
                        String title = (String) mData.get(k).get("title");
                        File f = new File(path);
                        if (mData.get(k).get("img").equals(R.mipmap.icon_folder)) {setdatastyel(path, title);
                        } else if (title.endsWith(".txt")) {
                            setdatastyel(path, title);
                        } else if (title.endsWith(".csv")) {
                            setdatastyel(path, title);
                        } else if (title.endsWith(".kml")) {
                            setdatastyel(path, title);
                        }else if(title.endsWith(".kmz")){
                            setdatastyel(path, title);
                        }
                    }
                }
                break;
            case R.id.btn_cancel:
                show = false;
                rlEdit.setVisibility(View.VISIBLE);
                layoutShow.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                uncheckedAll();
                break;
            case R.id.btn_delete:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
              //  shareIntent.
                SparseBooleanArray checkedArrays = new SparseBooleanArray();
                checkedArrays = list.getCheckedItemPositions();
                File f;
                for (int i = 0; i < checkedArrays.size(); i++) {
                    if (checkedArrays.valueAt(i)) {
                        int k = checkedArrays.keyAt(i);
                        String path = (String) mData.get(k).get("path");
                        String title = (String) mData.get(k).get("title");
                        f = new File(path);
                      if (mData.get(k).get("img").equals(R.mipmap.icon_folder)) {
                          Toast.makeText(this,"只能分享文件",Toast.LENGTH_LONG).show();
                        } else{
                          Uri u = Uri.fromFile(f);
                          shareIntent.putExtra(Intent.EXTRA_STREAM,  u );
                          shareIntent.setType("*/*");
                          startActivity(Intent.createChooser(shareIntent, "分享到"));
                        }
                    }
                }
                break;
            case R.id.btn_all:
                if (isSelectALL) {
                    for (int i = 0; i < mData.size(); i++) {
                        list.setItemChecked(i, false);
                        isSelectALL = false;
                    }

                } else {
                    for (int i = 0; i < mData.size(); i++) {
                        list.setItemChecked(i, true);
                        isSelectALL = true;
                    }
                }
                break;

        }
    }
    public void uncheckedAll() {
        for (int i = 0; i < mData.size(); i++) {
            list.setItemChecked(i, false);
            isSelectALL = false;
        }

    }
    private class onListItemClick implements AdapterView.OnItemClickListener {
       @Override
       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           if (!show){
               path = (String) mData.get(position).get("path");
               String title = (String) mData.get(position).get("title");
               if (position == 0) {
                   for (int i = 0; i < mData.size(); i++) {
                       list.setItemChecked(i, false);
                   }
                   File file = new File(path);
                   if (file.getParent() == null) {
                       finish();
                   } else if (mData.get(position).get("img").equals(R.mipmap.icon_folder) ) {
                       mData = getData();
                       adapter.notifyDataSetChanged();
                   }

               }
               if (mData.get(position).get("img").equals(R.mipmap.icon_folder) ) {
                   mData = getData();
                   mData = getData();
                   adapter.notifyDataSetChanged();
               } else if (title.endsWith(".txt")) {
                   setdatastyel(path, title);
               }else if(title.endsWith(".csv")){
                   setdatastyel(path, title);
               }
               else if(title.endsWith(".kml")){
                   setdatastyel(path, title);
               }
           }
           }

   }

    public void onClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFileActivity.this);
        builder.setTitle("导入说明");
        builder.setMessage(R.string.import_help);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public class ViewHolder {
        ImageView img;
        TextView titleview;
        TextView pathview;
        CheckBox checkBox;
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


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder ;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.list_file, null);
                holder.img = view.findViewById(R.id.img);
                holder.titleview = view.findViewById(R.id.title);
                holder.pathview = view.findViewById(R.id.path);
                holder.checkBox = view.findViewById(R.id.checkbox);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.img.setBackgroundResource((Integer) mData.get(i).get("img"));
            holder.titleview.setText((String) mData.get(i).get("title"));
            holder.pathview.setText((String) mData.get(i).get("path"));
            if (show) {
                holder.checkBox.setVisibility(View.VISIBLE);
            } else {
                holder.checkBox.setVisibility(View.GONE);
            }
            return view;
        }

    }

    private void finishWithResult(String path, String title, int coordstyle, int datastyle) {
        Bundle conDate = new Bundle();
        conDate.putString("path", path);
        conDate.putString("title", title);
        conDate.putInt("CoordStyle", coordstyle);
        conDate.putInt("DataStyle", datastyle);
        Intent intent = new Intent();
        intent.putExtras(conDate);
        intent.setClass(this,FileManagerActivity.class);
        intent.putExtra("activityname","addfileactivity");
        startActivity(intent);
        finish();
    }

    public void setdatastyel(final String path, final String title) {
        final RadioGroup rgcoord;
        final RadioGroup rgdata;
        View view = getLayoutInflater().inflate(R.layout.dialog_file_setting, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFileActivity.this);
        builder.setView(view);
        builder.setTitle("数据格式设置");
        rgcoord = view.findViewById(R.id.rg_codstyle);
        rgdata = view.findViewById(R.id.rg_datastyle);


        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (rgcoord.getCheckedRadioButtonId()) {
                    case R.id.rb_wgs84:
                        CoordStyle = LatStyle.GPSSYTELE;
                        break;
                    case R.id.rb_bd:
                        CoordStyle = LatStyle.BAIDUMAPSTYELE;
                        break;
                    case R.id.rb_other:
                        CoordStyle = LatStyle.OTHERS;
                        break;
                }
                switch (rgdata.getCheckedRadioButtonId()) {
                    case R.id.rb_decimal:
                        DataStye = LatStyle.DEGREE;
                        break;
                    case R.id.rb_dms:
                        DataStye = LatStyle.DMS;
                        break;
                }
                finishWithResult(path, title, CoordStyle, DataStye);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public void onResume() {
        super.onResume();

        StatService.onResume(this);
    }

    public void onPause() {
        super.onPause();

        StatService.onPause(this);
    }
}

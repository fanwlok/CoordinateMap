package com.fanweilin.coordinatemap.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.fanweilin.coordinatemap.Class.LatStyle;
import com.fanweilin.coordinatemap.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFileActivity extends AppCompatActivity {
    private final String mDir = Environment.getExternalStorageDirectory().getPath();
    private List<Map<String, Object>> mData;
    private String path;
    private ListView list;
   private Myadpter adapter;
    private int CoordStyle = LatStyle.GPSSYTELE;
    private int DataStye = LatStyle.DEGREE;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
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
                }
                return false;
            }
        });
        path = mDir;
        mData = getData();
        list= (ListView) findViewById(R.id.list);
        adapter = new Myadpter(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new onListItemClick());

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
            map.put("img", R.mipmap.ex_folder);
            map.put("path", f.getParent());
            list.add(map);
        }
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

   private class onListItemClick implements AdapterView.OnItemClickListener {
       @Override
       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           path = (String) mData.get(position).get("path");
           String title = (String) mData.get(position).get("title");
           if (position == 0) {
               File file = new File(path);
               if (file.getParent() == null) {
                   finish();
               } else if ((Integer) mData.get(position).get("img") == R.mipmap.ex_folder) {
                   mData = getData();
                   adapter.notifyDataSetChanged();


               }
           }
           if ((Integer) mData.get(position).get("img") == R.mipmap.ex_folder) {
               mData = getData();
               mData = getData();
               adapter.notifyDataSetChanged();
           } else if (title.endsWith(".txt")) {
               setdatastyel(path, title);
           }
       }
   }

    public void onClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFileActivity.this);
        builder.setTitle("帮助");
        builder.setMessage("导入文本格式为txt格式.\n小数形式例:\n1,30.42454758,120.54652514\n2,30.42556235,120.88745624"
        +"\n度分秒格式例:\n1,30.45253,120.36478\n2,30.44257,120.55786\n坐标系导入数据后在设置.");
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
                holder.img = (ImageView) view.findViewById(R.id.img);
                holder.titleview = (TextView) view.findViewById(R.id.title);
                holder.pathview = (TextView) view.findViewById(R.id.path);
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

    private void finishWithResult(String path, String title, int coordstyle, int datastyle) {
        Bundle conDate = new Bundle();
        conDate.putString("path", path);
        conDate.putString("title", title);
        conDate.putInt("CoordStyle", coordstyle);
        conDate.putInt("DataStyle", datastyle);
        Intent intent = new Intent();
        intent.putExtras(conDate);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void setdatastyel(final String path, final String title) {
        final RadioGroup rgcoord;
        final RadioGroup rgdata;
        View view = getLayoutInflater().inflate(R.layout.dialog_file_setting, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFileActivity.this);
        builder.setView(view);
        builder.setTitle("数据格式设置");
        rgcoord = (RadioGroup) view.findViewById(R.id.rg_codstyle);
        rgdata = (RadioGroup) view.findViewById(R.id.rg_datastyle);


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

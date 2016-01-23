package com.fanweilin.coordinatemap.Activity;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fanweilin.coordinatemap.Class.LatStyle;
import com.fanweilin.coordinatemap.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFileActivity extends ListActivity implements View.OnClickListener {
    private final String mDir = Environment.getExternalStorageDirectory().getPath();
    private List<Map<String, Object>> mData;
    private String path;
    private Button btn;
    private int CoordStyle = LatStyle.GPSSYTELE;
    private int DataStye = LatStyle.DEGREE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);
        btn = (Button) findViewById(R.id.btn_help);
        path = mDir;
        mData = getData();
        Myadpter adapter = new Myadpter(this);
        setListAdapter(adapter);
        btn.setOnClickListener(this);

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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        path = (String) mData.get(position).get("path");
        String title = (String) mData.get(position).get("title");
        if (position == 0) {
            File file = new File(path);
            if (file.getParent() == null) {
                finish();
            } else if ((Integer) mData.get(position).get("img") == R.mipmap.ex_folder) {
                mData = getData();
                Myadpter adapter = new Myadpter(this);
                setListAdapter(adapter);

            }
        }
        if ((Integer) mData.get(position).get("img") == R.mipmap.ex_folder) {
            mData = getData();
            Myadpter adapter = new Myadpter(this);
            setListAdapter(adapter);
        } else if (title.endsWith(".txt")) {
            setdatastyel(path,title);
        }
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFileActivity.this);
        builder.setTitle("帮助");
        builder.setMessage("导入文本格式为:.txt\n文本数据格式为:名称,纬度,经度\n坐标系支持WGS84坐标系（GPS设备采集的原始GPS坐标）," +
                "百度地图坐标系,其他坐标系包括（google地图、soso地图、aliyun地图、mapabc地图和amap地图所用坐标)\n本软件支持小数、" +
                "度分秒两种格式（注：当导入度分秒格式时请转换成如下形式如30.452734,表示为30度45分27.34秒");
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
            ViewHolder holder = null;
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

    public void back(View view) {
        finish();
    }

}

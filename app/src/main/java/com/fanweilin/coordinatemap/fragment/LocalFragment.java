package com.fanweilin.coordinatemap.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanweilin.coordinatemap.Activity.DataManagerActivity;
import com.fanweilin.coordinatemap.Activity.FileManagerActivity;
import com.fanweilin.coordinatemap.Activity.ImportAcitivity;
import com.fanweilin.coordinatemap.Activity.MainMapsActivity;
import com.fanweilin.coordinatemap.Activity.data;
import com.fanweilin.coordinatemap.Class.Marker;
import com.fanweilin.coordinatemap.Class.ShowPointStyle;
import com.fanweilin.coordinatemap.Class.SpfOlMap;
import com.fanweilin.coordinatemap.DataModel.CoordianteApi;
import com.fanweilin.coordinatemap.DataModel.HttpControl;
import com.fanweilin.coordinatemap.DataModel.IdsClass;
import com.fanweilin.coordinatemap.DataModel.PointDataClient;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.FilesDao;
import com.fanweilin.greendao.PointData;
import com.fanweilin.greendao.PointDataDao;
import com.fanweilin.greendao.ShowData;
import com.fanweilin.greendao.ShowDataDao;
import com.fanweilin.greendao.SqlPolygon;
import com.fanweilin.greendao.SqlPolyline;
import com.zyyoona7.lib.EasyPopup;
import com.zyyoona7.lib.HorizontalGravity;
import com.zyyoona7.lib.VerticalGravity;

import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;

public class LocalFragment extends Fragment  {
    ListView listView;
    public Cursor cursor;
    private SQLiteDatabase db;
    private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
    private DaoSession mDaoSession;
    private Boolean show = false;
    public LocalListAdpter adapter;
    public LinearLayout ll;
    public LinearLayout fll;

    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void init() {
        mDaoSession = data.getmDaoSession();
        db = data.getDb();
        getListdata();
        adapter = new LocalListAdpter(getActivity());
        dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        init();
        View view = inflater.inflate(R.layout.fragment_local, container, false);
        ll = view.findViewById(R.id.ll_local);
        fll=view.findViewById(R.id.fragment_local);
        listView = view.findViewById(R.id.lv_fragment);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new MyItemLongclick());
        listView.setOnItemClickListener(new MyOnItemclick());
        return view;
    }

    public FilesDao getFilesDao() {
        return data.getmDaoSession().getFilesDao();
    }


    public class ViewHolder {
        ImageView imgMarker;
        TextView name;
        TextView subtitle;
        ImageButton btn;
    }

    public class LocalListAdpter extends BaseAdapter {
        private LayoutInflater inflater;

        public LocalListAdpter(Context context) {
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
            long id = (long) mData.get(i).get("id");
            final Files files=data.findOrderById(id);
            ViewHolder holder;
            if ((view == null)) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.list_file_item, null);
                holder.name = view.findViewById(R.id.name);
                holder.subtitle = view.findViewById(R.id.tv_subtitle);
                holder.btn=view.findViewById(R.id.img_btn_more);
                holder.imgMarker=view.findViewById(R.id.img_marker);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.name.setText(mData.get(i).get("filename").toString());
            holder.subtitle.setText(mData.get(i).get("data").toString());
            holder.imgMarker.setImageResource(Marker.getResource((Integer) mData.get(i).get("color")));
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPouup(files);

                }
            });
            return view;
        }
    }
public void  showPouup(final Files files){
    ListView listView = new ListView(getActivity());
    listView.setAdapter(new DialogListAdapter(getActivity()));
    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    final AlertDialog dialog = builder.setView(listView).show();
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:
                    dialog.dismiss();
                    showAllfiles(files);
                    break;
                case 1:
                    dialog.dismiss();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("文件删除");
                    builder.setMessage("确定删除吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (files.getTitle().equals("我的收藏")) {
                                Toast.makeText(getActivity(), "默认文件不能删除", Toast.LENGTH_SHORT).show();
                            } else {
                                data.deleteFile(files);
                            }
                            fresh();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                    break;
                case 2:
                    importFile(files);
                    dialog.dismiss();
                        break;
                case 3:
                    dialog.dismiss();
                    setfileMarker(files);
                    break;
            }

        }
    });
}
    private int selectMarker;
public void setfileMarker(final Files files){
    View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_img, null);
    ImageButton imgBlue=view.findViewById(R.id.img_blue);
    ImageButton imgRed=view.findViewById(R.id.img_red);
    ImageButton imgGreen=view.findViewById(R.id.img_green);
    ImageButton imgYellow=view.findViewById(R.id.img_yellow);
    ImageButton imgZs=view.findViewById(R.id.img_zs);
    final ImageView imageView=view.findViewById(R.id.img_select);
    if(files.getMarkerid()==null){

    }else {
        imageView.setImageResource(Marker.getResource(files.getMarkerid()));

    }

    class imgClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_blue:
                selectMarker=Marker.blue;
                imageView.setImageResource(Marker.REBLUEID);
                break;
            case R.id.img_red:
                selectMarker=Marker.red;
                imageView.setImageResource(Marker.REREDID);
                break;
            case R.id.img_green:
                selectMarker=Marker.green;
                imageView.setImageResource(Marker.REGREENID);
                break;
            case R.id.img_yellow:
                selectMarker=Marker.yellow;
                imageView.setImageResource(Marker.REYEID);
                break;
            case R.id.img_zs:
                selectMarker=Marker.zs;
                imageView.setImageResource(Marker.REZS);
                break;
        }
        }
    }

    imgBlue.setOnClickListener(new imgClick());
    imgRed.setOnClickListener(new imgClick());
    imgGreen.setOnClickListener(new imgClick());
    imgYellow.setOnClickListener(new imgClick());
    imgZs.setOnClickListener(new imgClick());


    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setView(view);
    builder.setTitle("设置文件默认图标");
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            files.setMarkerid(selectMarker);
            data.updateFiles(files);
            getListdata();
            adapter.notifyDataSetChanged();

        }
    });
    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        }
    });
    builder.show();
}
private void showAllfiles(Files files){
    List<PointData> pointDatas=files.getPointItems();
    List<SqlPolyline> polylines=files.getPolyItems();
    List<SqlPolygon>polygons=files.getPolygonItems();
    try {
        db.beginTransaction();
        for (int i = 0; i < pointDatas.size(); i++) {
            /*    ShowData showData = new ShowData();
                showData.setTitle(pointDatas.get(i).getName());
                //showdata
                showData.setBaidulatitude(pointDatas.get(i).getGcjlatitude());
                showData.setBaidulongitude(pointDatas.get(i).getGcjlongitude());
                showData.setWgslatitude(pointDatas.get(i).getWgslatitude());
                showData.setWgslongitude(pointDatas.get(i).getWgslongitude());
                showData.setPointid(pointDatas.get(i).getId());
                showData.setFileid(files.getId());
                showData.setStyle(ShowPointStyle.PONIT);
                getShowDataDao().insert(showData);*/
            data.createShowdata(pointDatas.get(i));
            }

        for(int i=0;i<polylines.size();i++){
          /*  ShowData polyshowData = new ShowData();
            polyshowData .setTitle(polylines.get(i).getName());
            polyshowData.setPointid(polylines.get(i).getId());
            polyshowData.setStyle(ShowPointStyle.LINE);
            polyshowData.setFileid(files.getId());
            getShowDataDao().insert(polyshowData);*/
          data.createShowdata(polylines.get(i));
        }

        for(int i=0;i<polygons.size();i++){
       /*     ShowData polyshowData = new ShowData();
            polyshowData .setTitle(polygons.get(i).getName());
            polyshowData.setPointid(polygons.get(i).getId());
            polyshowData.setStyle(ShowPointStyle.POLGON);
            polyshowData.setFileid(files.getId());
            getShowDataDao().insert(polyshowData);*/
        data.createShowdata(polygons.get(i));
        }
        db.setTransactionSuccessful();
        db.endTransaction();

    } finally {

    }
    Intent intent = new Intent();
    intent.putExtra("data", "more");
    intent.setClass(getActivity(), MainMapsActivity.class);
    startActivity(intent);
}
    private ShowDataDao getShowDataDao() {
        DaoSession mDaoSession;
        mDaoSession = data.getmDaoSession();
        return mDaoSession.getShowDataDao();
    }
    public String[] fileitems = {"显示", "删除", "导出","设置图标"};

    public class DialogListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public DialogListAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return fileitems.length;
        }

        @Override
        public Object getItem(int position) {
            return fileitems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.list_datamanger_dialog, null);
            TextView textView = view.findViewById(R.id.tv_datamanager_dialog);
            textView.setText(fileitems[position]);
            return view;
        }
    }
    private class MyItemLongclick implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, final long l) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("文件删除");
            builder.setMessage("确定删除吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    long id = (long) mData.get(i).get("id");
                    if (data.findOrderById(id).getTitle().equals("我的收藏")) {
                        Toast.makeText(getActivity(), "文件不能删除", Toast.LENGTH_SHORT).show();
                    } else {
                        data.deleteFile(data.findOrderById(id));
                    }
                    fresh();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
            return true;
        }
    }

    private class MyOnItemclick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (!show) {
                String filepath;
                String title;
                int coordstyle;
                int datastyle;
                cursor.moveToPosition(i);
                filepath = cursor.getString(cursor.getColumnIndex(FilesDao.Properties.Path.columnName));
                title = cursor.getString(cursor.getColumnIndex(FilesDao.Properties.Title.columnName));
                coordstyle = cursor.getInt(cursor.getColumnIndex(FilesDao.Properties.Cdstyle.columnName));
                datastyle = cursor.getInt(cursor.getColumnIndex(FilesDao.Properties.Datastyle.columnName));
                Intent intent = new Intent();
                intent.putExtra("path", filepath);
                intent.putExtra(DataManagerActivity.FILENAME, title);
                intent.putExtra("CoordStyle", coordstyle);
                intent.putExtra("DataStyle", datastyle);
                intent.setClass(getActivity(), DataManagerActivity.class);
                startActivity(intent);
            }
        }
    }

    public void fresh() {
        mData.clear();
        getListdata();
        adapter.notifyDataSetChanged();
        for (int i = 0; i < mData.size(); i++) {
            listView.setItemChecked(i, false);
        }
    }
private void importFile(Files files){
        Intent intent=new Intent();
        intent.setClass(getActivity(), ImportAcitivity.class);
        intent.putExtra("id",files.getId());
        startActivity(intent);
}
    private FilesDao getFileDao() {
        return mDaoSession.getFilesDao();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }

    public void getListdata() {
        if(mData!=null){
            mData.clear();
        }
        cursor = db.query(getFileDao().getTablename(), getFileDao().getAllColumns(), null, null, null, null, null);
        while (cursor.moveToNext()) {
            String filename;
            String data;
            long id;
            int markercolor;
            int nameColumnIndex = cursor.getColumnIndex(FilesDao.Properties.Title.columnName);
            int dataColumnIndex = cursor.getColumnIndex(FilesDao.Properties.Date.columnName);
            int idColunmIndex = cursor.getColumnIndex(FilesDao.Properties.Id.columnName);
            int color=cursor.getColumnIndex(FilesDao.Properties.Markerid.columnName);
                Map<String, Object> map = new HashMap<String, Object>();
                filename = cursor.getString(nameColumnIndex);
                data = cursor.getString(dataColumnIndex);
                id = cursor.getLong(idColunmIndex);
                color=cursor.getInt(color);
                map.put("filename", filename);
                map.put("data", data);
                map.put("id", id);
                map.put("color", color);
                mData.add(map);
        }
    }
}

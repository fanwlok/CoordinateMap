package com.fanweilin.coordinatemap.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanweilin.coordinatemap.Activity.DataManagerActivity;
import com.fanweilin.coordinatemap.Activity.FileManagerActivity;
import com.fanweilin.coordinatemap.Activity.data;
import com.fanweilin.coordinatemap.Class.CoordianteApi;
import com.fanweilin.coordinatemap.Class.HttpControl;
import com.fanweilin.coordinatemap.Class.Register;
import com.fanweilin.coordinatemap.DataModel.IdsClass;
import com.fanweilin.coordinatemap.DataModel.PointDataClient;
import com.fanweilin.coordinatemap.DataModel.PointDataGet;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.FilesDao;
import com.fanweilin.greendao.PointData;
import com.fanweilin.greendao.PointDataDao;

import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LocalFragment extends Fragment implements View.OnClickListener {
    ListView listView;
    public Cursor cursor;
    private SQLiteDatabase db;
    private List<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
    private DaoSession mDaoSession;
    private Boolean show = false;
    private Boolean isSelectALL = false;
    public LocalListAdpter adapter;
    public LinearLayout ll;
    private RelativeLayout rl;
    private Button btnEdit;
    private Button btnCancel;
    private Button btndelete;
    private Button btnput;
    private Button btnAll;
    private ProgressDialog dialog;
    private FileManagerActivity fileManagerActivity;

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
        fileManagerActivity= (FileManagerActivity) this.getActivity();
        correctData();
    }
 public void correctData(){
     Retrofit retrofit = HttpControl.getInstance(getContext()).getRetrofit();
     CoordianteApi getids = retrofit.create(CoordianteApi.class);
     getids.Rxgetdataid().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Subscriber<List<IdsClass>>() {
         @Override
         public void onCompleted() {

         }

         @Override
         public void onError(Throwable e) {
             Log.e("ex","网络异常");
         }

         @Override
         public void onNext(List<IdsClass> ids) {
             QueryBuilder qb = getPointDataDao().queryBuilder().where(PointDataDao.Properties.Status.eq(9));
             List<PointData> list = qb.list();
             List<Long> guids=new ArrayList<Long>();
             for (IdsClass idsClass:ids){
                 guids.add(idsClass.getId());
             }
             try {
                 db.beginTransaction();
                 for(PointData pointData:list){
                     Long id=pointData.getGuid();
                     if(!guids.contains(id)){
                         pointData.setStatus(-1);
                         pointData.update();
                     }
                 }
                 db.setTransactionSuccessful();
             }finally {
                 db.endTransaction();
             }


         }
     });
 }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        init();
        View view = inflater.inflate(R.layout.fragment_local, container, false);
        rl = (RelativeLayout) view.findViewById(R.id.rl_local);
        ll = (LinearLayout) view.findViewById(R.id.ll_local);
        listView = (ListView) view.findViewById(R.id.lv_fragment);
        btnEdit = (Button) view.findViewById(R.id.btn_local_edit);
        btnCancel = (Button) view.findViewById(R.id.btn_local_cancel);
        btndelete = (Button) view.findViewById(R.id.btn_local_delete);
        btnput = (Button) view.findViewById(R.id.btn_local_put);
        btnAll = (Button) view.findViewById(R.id.btn_local_all);
        btnEdit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btndelete.setOnClickListener(this);
        btnAll.setOnClickListener(this);
        btnput.setOnClickListener(this);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new MyItemLongclick());
        listView.setOnItemClickListener(new MyOnItemclick());
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_local_edit:
                ishowcheckbox();
                break;
            case R.id.btn_local_cancel:
                ishowcheckbox();
                break;
            case R.id.btn_local_delete:
                delete();
                break;
            case R.id.btn_local_put:
                if (fileManagerActivity.islog){
                    put();
                }else {
                    Toast.makeText(getActivity(),"请先登陆",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_local_all:
                checkall();
                break;
        }

    }

    public void put() {
        dialog.show();
        Retrofit retrofit = HttpControl.getInstance(getContext()).getRetrofit();
        CoordianteApi putdata = retrofit.create(CoordianteApi.class);
        int len = listView.getCount();
        SparseBooleanArray checkedArray = new SparseBooleanArray();
        checkedArray = listView.getCheckedItemPositions();
        final List<PointDataClient> listdata = new ArrayList<PointDataClient>();
        for (int i = 0; i < len; i++) {
            if (checkedArray.valueAt(i)) {
                int k = checkedArray.keyAt(i);
                long id = (long) mData.get(k).get("id");
                QueryBuilder qb = getPointDataDao().queryBuilder().where(PointDataDao.Properties.Status.lt(8));
                Join file = qb.join(PointDataDao.Properties.Fileid, Files.class);
                file.where(FilesDao.Properties.Id.eq(id));
                List<PointData> list = qb.list();
                for (PointData pointData : list) {
                    PointDataClient pointDataClient = new PointDataClient();
                    pointDataClient.setFilename(data.findOrderById(id).getTitle());
                    pointDataClient.setAltitude(pointData.getAltitude());
                    pointDataClient.setPointname(pointData.getName());
                    pointDataClient.setAddress(pointData.getAddress());
                    pointDataClient.setWgslatitude(pointData.getWgslatitude());
                    pointDataClient.setWgslongitude(pointData.getWgslongitude());
                    pointDataClient.setBaidulatitude(pointData.getBaidulatitude());
                    pointDataClient.setBaidulongitude(pointData.getBaidulongitude());
                    pointDataClient.setDescribe(pointData.getDescribe());
                    pointDataClient.setPointid(pointData.getId());
                    pointDataClient.setGuid(pointData.getGuid());
                    listdata.add(pointDataClient);
                }
            }
        }
        if (listdata.size() == 0) {
            Toast.makeText(getActivity(), "没有数据要更新", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } else {
            putdata.Rxputdata(listdata).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<IdsClass>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(List<IdsClass> idsClasses) {
                    Toast.makeText(getActivity(), "备份完成", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    fileManagerActivity.cloudFragment.getListdata();
                    try {
                        db.beginTransaction();
                        for (IdsClass idsClass : idsClasses) {
                            PointData pointData = data.findPointDataDaoById(idsClass.getClientid());
                            pointData.setStatus(9);
                            pointData.setGuid(idsClass.getServerid());
                            pointData.update();
                        }
                        db.setTransactionSuccessful();
                    }finally {
                        db.endTransaction();
                    }

                }
            });

        }
        }

    public FilesDao getFilesDao() {
        return data.getmDaoSession().getFilesDao();
    }

    public PointDataDao getPointDataDao() {
        return data.getmDaoSession().getPointDataDao();
    }

    public void checkall() {
        if (isSelectALL) {
            for (int i = 0; i < mData.size(); i++) {
                listView.setItemChecked(i, false);
                isSelectALL = false;
            }

        } else {
            for (int i = 0; i < mData.size(); i++) {
                listView.setItemChecked(i, true);
                isSelectALL = true;
            }
        }
    }
    public String[] dataItems = { "删除", "取消"};
    public void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("文件删除");
        builder.setMessage("确定删除吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletedata();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
    public class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public ListAdapter(Context context) {
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
            TextView textView = (TextView) view.findViewById(R.id.tv_datamanager_dialog);
            textView.setText(dataItems[position]);
            if (dataItems[position].equals("删除")){
                textView.setTextColor(getResources().getColor(R.color.blue_500));
            }
            return view;
        }
    }
   public void deletedata(){
       int len = listView.getCount();
       SparseBooleanArray checkedArray = new SparseBooleanArray();
       checkedArray = listView.getCheckedItemPositions();
       try {
           db.beginTransaction();
           for (int i = 0; i < len; i++) {
               if (checkedArray.valueAt(i)) {
                   int k = checkedArray.keyAt(i);
                   long id = (long) mData.get(k).get("id");
                   data.deleteFile(data.findOrderById(id));
               }
           }
           db.setTransactionSuccessful();
       } finally {
           db.endTransaction();
       }
       fresh();
   }
    public void ishowcheckbox() {
        if (show) {
            rl.setVisibility(View.VISIBLE);
            ll.setVisibility(View.GONE);
            show = false;
        } else {
            rl.setVisibility(View.GONE);
            ll.setVisibility(View.VISIBLE);
            show = true;
        }
        fresh();
    }

    public class ViewHolder {
        TextView name;
        TextView subtitle;
        CheckBox checkBox;
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

            ViewHolder holder;
            if ((view == null)) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.list_data, null);
                holder.name = (TextView) view.findViewById(R.id.name);
                holder.subtitle = (TextView) view.findViewById(R.id.tv_subtitle);
                holder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.name.setText(mData.get(i).get("filename").toString());
            holder.subtitle.setText(mData.get(i).get("data").toString());

            if (show) {
                holder.checkBox.setVisibility(View.VISIBLE);
            } else {
                holder.checkBox.setVisibility(View.GONE);
            }
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
            isSelectALL = false;
        }
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

        cursor = db.query(getFileDao().getTablename(), getFileDao().getAllColumns(), null, null, null, null, null);
        while (cursor.moveToNext()) {
            String filename;
            String data;
            long id;
            int nameColumnIndex = cursor.getColumnIndex(FilesDao.Properties.Title.columnName);
            int dataColumnIndex = cursor.getColumnIndex(FilesDao.Properties.Date.columnName);
            int idColunmIndex = cursor.getColumnIndex(FilesDao.Properties.Id.columnName);
            Map<String, Object> map = new HashMap<String, Object>();
            filename = cursor.getString(nameColumnIndex);
            data = cursor.getString(dataColumnIndex);
            id = cursor.getLong(idColunmIndex);
            map.put("filename", filename);
            map.put("data", data);
            map.put("id", id);
            mData.add(map);
        }
    }
}

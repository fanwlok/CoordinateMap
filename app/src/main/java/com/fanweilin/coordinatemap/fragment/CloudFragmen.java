package com.fanweilin.coordinatemap.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanweilin.coordinatemap.Activity.DataServerManagerActivity;
import com.fanweilin.coordinatemap.Activity.FileManagerActivity;
import com.fanweilin.coordinatemap.Activity.data;
import com.fanweilin.coordinatemap.DataModel.CoordianteApi;
import com.fanweilin.coordinatemap.DataModel.HttpControl;
import com.fanweilin.coordinatemap.DataModel.Register;
import com.fanweilin.coordinatemap.DataModel.FilesClass;
import com.fanweilin.coordinatemap.DataModel.IdsClass;
import com.fanweilin.coordinatemap.DataModel.PointDataClient;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.FilesDao;
import com.fanweilin.greendao.PointData;
import com.fanweilin.greendao.PointDataDao;

import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

public class CloudFragmen extends Fragment implements View.OnClickListener {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }

    private void init() {
        mDaoSession = data.getmDaoSession();
        db = data.getDb();
        fileManagerActivity= (FileManagerActivity) this.getActivity();

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
        View view = inflater.inflate(R.layout.fragment_cloud, container, false);
        rl = view.findViewById(R.id.rl_local);
        ll = view.findViewById(R.id.ll_local);
        listView = view.findViewById(R.id.lv_fragment);
        btnEdit = view.findViewById(R.id.btn_local_edit);
        btnCancel = view.findViewById(R.id.btn_local_cancel);
        btndelete = view.findViewById(R.id.btn_local_delete);
        btnput = view.findViewById(R.id.btn_local_put);
        btnAll = view.findViewById(R.id.btn_local_all);
        btnEdit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btndelete.setOnClickListener(this);
        btnAll.setOnClickListener(this);
        btnput.setOnClickListener(this);
        listView.setAdapter(adapter);
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
                down();
                break;
            case R.id.btn_local_all:
                checkall();
                break;
        }

    }

    public void down() {
        Retrofit retrofit = HttpControl.getInstance(getContext()).getRetrofit();
        CoordianteApi down = retrofit.create(CoordianteApi.class);
        int len = listView.getCount();
        SparseBooleanArray checkedArray = new SparseBooleanArray();
        checkedArray = listView.getCheckedItemPositions();
        dialog.show();
        if (len == 0) {
            Toast.makeText(getActivity(), "没有数据要同步", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }else {
            for (int i = 0; i < len; i++) {
                final List<PointDataClient> listdata = new ArrayList<PointDataClient>();
                if (checkedArray.valueAt(i)) {
                    int k = checkedArray.keyAt(i);
                    final String filename=mData.get(k).get("filename").toString();
                    if( data.findOrderByName(mData.get(k).get("filename").toString())!=null){
                        long id = data.findOrderByName(mData.get(k).get("filename").toString()).getId();
                        QueryBuilder qb = getPointDataDao().queryBuilder().where(PointDataDao.Properties.Status.eq(9));
                        Join file = qb.join(PointDataDao.Properties.FileId, Files.class);
                        file.where(FilesDao.Properties.Id.eq(id));
                        List<PointData> list = qb.list();
                        for (PointData pointData : list) {
                            PointDataClient pointDataClient = new PointDataClient();
                            pointDataClient.setFilename(data.findOrderById(id).getTitle());
                            listdata.add(pointDataClient);
                        }
                    }
                    down.Rxdownmydatas(filename,listdata)
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<PointDataClient>>() {

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<PointDataClient> pointDataClients) {
                            if(data.findOrderByName(filename)!=null){
                                Files files=data.findOrderByName(filename);
                                copydata(files,pointDataClients);

                            }else {
                                Files files=data.createFiles(filename);
                                copydata(files,pointDataClients);
                            }
                            fileManagerActivity.localFragment.fresh();
                        }
                    });
                }

            }

        }

    }
   public void copydata(Files files,List<PointDataClient> pointDataClients) {
       try {
           db.beginTransaction();
           List<IdsClass> ids=new ArrayList<IdsClass>();
           for (PointDataClient pointDataClient : pointDataClients) {
               PointData pointData = new PointData();
               pointData.setName(pointDataClient.getPointname());
               pointData.setAddress(pointDataClient.getAddress());
               pointData.setDescribe(pointDataClient.getDescribe());
               pointData.setAltitude(pointDataClient.getAltitude());
               pointData.setWgslongitude(pointDataClient.getWgslongitude());
               pointData.setWgslatitude(pointDataClient.getWgslatitude());
               pointData.setBaidulongitude(pointDataClient.getBaidulongitude());
               pointData.setBaidulatitude(pointDataClient.getBaidulatitude());
               Log.d("dfd",String.valueOf(pointDataClient.getId()));
               data.createPointData(files, pointData);
               pointData.setStatus(9);
               getPointDataDao().update(pointData);
           }
           db.setTransactionSuccessful();
       } finally {
           db.endTransaction();
           dialog.dismiss();
       }


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

    public void delete() {
        dialog.show();
        final int len = listView.getCount();
        if (len > 0) {
            SparseBooleanArray checkedArray = new SparseBooleanArray();
            checkedArray = listView.getCheckedItemPositions();
            final List<String> list = new ArrayList<String>();
            for (int i = 0; i < len; i++) {
                if (checkedArray.valueAt(i)) {
                    int k = checkedArray.keyAt(i);
                    String filename = mData.get(k).get("filename").toString();
                    list.add(filename);
                }
            }
            final int size = list.size();
            final String[] files = list.toArray(new String[size]);
            Retrofit retrofit = HttpControl.getInstance(getContext()).getRetrofit();
            CoordianteApi deletefiles = retrofit.create(CoordianteApi.class);
            deletefiles.Rxdeletefiles(files).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Register>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(Register register) {
                    if (register.getCode() == 200) {
                        try {
                            db.beginTransaction();
                            for (int i = 0; i < size; i++) {
                                if (data.findOrderByName(files[i]) != null) {
                                    Files file = data.findOrderByName(files[i]);
                                    List<PointData> pointDatas = file.getPointItems();
                                    for (PointData pointData : pointDatas) {
                                        pointData.setStatus(0);
                                        getPointDataDao().update(pointData);
                                    }
                                }
                            }
                            db.setTransactionSuccessful();
                        } finally {
                            db.endTransaction();
                        }
                        getListdata();
                        dialog.dismiss();
                    }

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }

            });
        }else{
            Toast.makeText(getActivity(),"请选择文件",Toast.LENGTH_SHORT).show();
        }
    }
    public FilesDao getFilesDao() {
        return data.getmDaoSession().getFilesDao();
    }
    public PointDataDao getPointDao() {
        return data.getmDaoSession().getPointDataDao();
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
                holder.name = view.findViewById(R.id.name);
                holder.subtitle = view.findViewById(R.id.tv_subtitle);
                holder.checkBox = view.findViewById(R.id.checkbox);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.name.setText(mData.get(i).get("filename").toString());
            holder.subtitle.setText(mData.get(i).get("date").toString());

            if (show) {
                holder.checkBox.setVisibility(View.VISIBLE);
            } else {
                holder.checkBox.setVisibility(View.GONE);
            }
            return view;
        }
    }


    private class MyOnItemclick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (!show) {
                String path;
                String title;
                title = mData.get(i).get("filename").toString();
                path = mData.get(i).get("path").toString();
                Intent intent = new Intent();
                intent.putExtra(DataServerManagerActivity.FILENAME, title);
                intent.setClass(getActivity(), DataServerManagerActivity.class);
                startActivity(intent);
            }
            }
        }

    public void fresh() {
        adapter.notifyDataSetChanged();
        for (int i = 0; i < mData.size(); i++) {
            listView.setItemChecked(i, false);
            isSelectALL = false;
        }
    }

    private FilesDao getFileDao() {
        return mDaoSession.getFilesDao();
    }

    public void getListdata() {
        if(mData.size()>0){
            mData.clear();
        }
        Retrofit retrofit = HttpControl.getInstance(getContext()).getRetrofit();
        CoordianteApi getfiles= retrofit.create(CoordianteApi.class);
        getfiles.Rxgetfiles().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<FilesClass>>() {

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<FilesClass> filesClasses) {
                for(FilesClass filesClass:filesClasses){
                    Map<String, Object> map = new HashMap<String, Object>();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date time=formatter.parse(filesClass.getDate());
                        File file=newFile(filesClass.getFilename(),time.getTime()) ;
                        map.put("filename",filesClass.getFilename());
                        map.put("date", filesClass.getDate());
                        map.put("id", filesClass.getId());
                        map.put("path",file.getAbsolutePath());
                        mData.add(map);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }
                fresh();
            }
        });
    }
    public File newFile(String filename,long date){
        try {
            File dirs = new File(Environment.getExternalStorageDirectory(), "经纬度定位");
            if (!dirs.exists()) {
                dirs.mkdirs();
            }
            File filedirs = new File(dirs.getPath(), "backups");
            if (!filedirs.exists()) {
                filedirs.mkdirs();
            }
            File file = new File(filedirs.getPath(), filename + ".txt");
            if (!file.exists()) {
                file.createNewFile();
                getFileData(file,filename);
            }else if (date>file.lastModified()){
                getFileData(file,filename);
            }

            return file;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
    public void getFileData(final File file,String filename){

            Retrofit retrofit = HttpControl.getInstance(getContext()).getRetrofit();
            CoordianteApi getfiles= retrofit.create(CoordianteApi.class);
        getfiles.Rxgetdata(filename).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).subscribe(new Observer<ResponseBody>() {


            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                String content= null;
                try {
                    content = responseBody.string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileWriter fileWritter = null;
                try {
                    fileWritter = new FileWriter(file.getAbsolutePath());
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWritter);
                    bufferedWriter.write(content);
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

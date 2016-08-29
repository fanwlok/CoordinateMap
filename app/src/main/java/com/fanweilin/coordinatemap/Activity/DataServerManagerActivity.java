package com.fanweilin.coordinatemap.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.fanweilin.coordinatemap.Class.CoordianteApi;
import com.fanweilin.coordinatemap.Class.HttpControl;
import com.fanweilin.coordinatemap.Class.Register;
import com.fanweilin.coordinatemap.DataModel.FilesClass;
import com.fanweilin.coordinatemap.DataModel.PointDataClient;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.ConvertLatlng;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.PointData;
import com.fanweilin.greendao.PointDataDao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.query.Query;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DataServerManagerActivity extends AppCompatActivity implements View.OnClickListener, android.support.v7.widget.SearchView.OnQueryTextListener {
    private ListView mListView;
    private TextView mTextView;
    private String title;
    private DataAdpter mDataAdpter;
    private ConvertLatlng convertLatlng;
    private boolean show = false;
    private List<Map<String, Object>> mData;
    private List<Map<String, Object>> mBackData;
    private LinearLayout layoutShow;
    private RelativeLayout rlEdit;
    private boolean isSelectALL;
    private Button btnEdit;
    private Button btnAll;
    private Button btnDown;
    private Button btnCancle;
    private Button btndelete;
    private String filename;
    private SQLiteDatabase db;
    private android.support.v7.widget.SearchView searchView;
    private Files files;
    private Toolbar toolbar;
    List<PointDataClient> pointDatas;
    public static final String FILENAME = "filename";
    public static final String SUBTITLE = "subtitle";
    public static final String PATH = "path";
    public static final String Id = "id";
    //服务器数据ID
    public static final String SERVERID = "SERVERID";

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_server_manager);
        init();
        getData();
        toolbar.setTitle(filename);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDataAdpter = new DataAdpter(this);
        mListView.setAdapter(mDataAdpter);

    }


    public void init() {
        db = data.getDb();
        files = new Files();
        mData = new ArrayList<>();
        convertLatlng = new ConvertLatlng();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rlEdit = (RelativeLayout) findViewById(R.id.rl_server);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnCancle = (Button) findViewById(R.id.btn_cancel);
        btnAll = (Button) findViewById(R.id.btn_all);
        btndelete = (Button) findViewById(R.id.btn_delete);
        mListView = (ListView) findViewById(R.id.data_manager_list);
        layoutShow = (LinearLayout) findViewById(R.id.layoutshow);
        btnEdit.setOnClickListener(this);
        btnCancle.setOnClickListener(this);
        btnAll.setOnClickListener(this);
        btndelete.setOnClickListener(this);
        mListView.setTextFilterEnabled(true);
        isSelectALL = false;
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);


    }


    public void getData() {
        mData.clear();
        Intent intent = getIntent();
        filename = intent.getStringExtra(FILENAME);
        File file = new File(intent.getStringExtra(PATH));
        StringBuffer sb = new StringBuffer();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        pointDatas = new ArrayList<PointDataClient>();
        pointDatas = gson.fromJson(sb.toString(), new TypeToken<Collection<PointDataClient>>() {
        }.getType());
        int count = pointDatas.size();
        for (int i = 0; i < count; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(FILENAME, pointDatas.get(i).getPointname());
            map.put(SUBTITLE, pointDatas.get(i).getAddress());
            map.put(Id, i);
            map.put(SERVERID,pointDatas.get(i).getId());
            Log.d("dfd",String.valueOf(pointDatas.get(i).getId()));
            mData.add(map);
        }
        mBackData = mData;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_edit:
                if (!show) {
                    show = true;
                    rlEdit.setVisibility(View.GONE);
                    layoutShow.setVisibility(View.VISIBLE);
                    mDataAdpter.notifyDataSetChanged();
                    uncheckedAll();

                }
                break;
            case R.id.btn_cancel:
                cancel();
                break;
            case R.id.btn_delete:
                delete();
                break;
            case R.id.btn_down:
                break;
            case R.id.btn_all:
                if (isSelectALL) {
                    for (int i = 0; i < mData.size(); i++) {
                        mListView.setItemChecked(i, false);
                        isSelectALL = false;
                    }

                } else {
                    for (int i = 0; i < mData.size(); i++) {
                        mListView.setItemChecked(i, true);
                        isSelectALL = true;
                    }
                }
                break;

        }
    }

    public void cancel() {
        show = false;
        rlEdit.setVisibility(View.VISIBLE);
        layoutShow.setVisibility(View.GONE);
        mDataAdpter.notifyDataSetChanged();
        uncheckedAll();
    }

    public void delete() {
        dialog.show();
        SparseBooleanArray checkedArray = new SparseBooleanArray();
        checkedArray = mListView.getCheckedItemPositions();
       int len = mListView.getCount();
         final List<Long> ids = new ArrayList<Long>();
        final List<Integer> deIds=new ArrayList<Integer>();
        for (int i = 0; i < len; i++) {
            if (checkedArray.valueAt(i)) {
                int k = checkedArray.keyAt(i);
                deIds.add(k);
                Long id = (Long) mData.get(k).get(SERVERID);
                ids.add(id);
            }


        }
       final Long[] idss = ids.toArray(new Long[ids.size()]);
        Retrofit retrofit = HttpControl.getInstance(DataServerManagerActivity.this).getRetrofit();
        CoordianteApi deldatas = retrofit.create(CoordianteApi.class);
        deldatas.Rxdeletedatas(filename, idss).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<FilesClass>() {
            @Override
            public void call(FilesClass filesClass) {
                downFileData(filesClass.getFilename(),filesClass.getDate());

                try {
                    db.beginTransaction();
                    for (Long id : idss) {
                        Query query = getPointDataDao().queryBuilder().where(
                                PointDataDao.Properties.Guid.eq(id))
                                .build();
                        List<PointData> pointDatas = query.list();
                        for (PointData pointdata : pointDatas) {
                            pointdata.setStatus(0);
                            getPointDataDao().update(pointdata);
                        }

                    }
                    db.setTransactionSuccessful();
                }
                    finally {
                        db.endTransaction();
                    }




            }
//            @Override
//            public void call(Register register) {
//                  if(register.getCode()==200){
//                      Toast.makeText(DataServerManagerActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
//                      dialog.dismiss();
//
//                  }
//            }
        });
    }
    public PointDataDao getPointDataDao() {
        return data.getmDaoSession().getPointDataDao();
    }
    private File downFileData(String filename, String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date time = formatter.parse(date);
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
                downFileData(file, filename);
            } else if (time.getTime() > file.lastModified()) {
                downFileData(file, filename);
            }
            return file;

        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }
    public void downFileData(final File file,String filename){

        Retrofit retrofit = HttpControl.getInstance(DataServerManagerActivity.this).getRetrofit();
        CoordianteApi getfiles= retrofit.create(CoordianteApi.class);
        getfiles.Rxgetdata(filename).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResponseBody>() {
            @Override
            public void call(ResponseBody responseBody) {
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
                getData();
                mDataAdpter.notifyDataSetChanged();
                dialog.dismiss();
            }

        }) ;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dataservermanager, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        Filter filter = mDataAdpter.getFilter();
        if (mDataAdpter instanceof Filterable) {
            if (s == null || s.length() == 0) {
                filter.filter("");
            } else {
                filter.filter(s);
            }

        }
        return true;
    }

    public class ViewHolder {
        TextView name;
        TextView subtitle;
        CheckBox checkBox;
    }

    public class DataAdpter extends BaseAdapter implements Filterable {
        private LayoutInflater inflater;
        private MyFilter mFilter;

        public DataAdpter(Context context) {
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
            holder.name.setText(mData.get(i).get(FILENAME).toString());
            if (TextUtils.isEmpty(pointDatas.get((Integer) mData.get(i).get(Id)).getAddress())) {
                holder.subtitle.setText(pointDatas.get((Integer) mData.get(i).get(Id)).getWgslatitude() + "," + pointDatas.get((Integer) mData.get(i).get(Id)).getWgslongitude());

            } else {
                holder.subtitle.setText(pointDatas.get((Integer) mData.get(i).get(Id)).getAddress().toString());
            }

            if (show) {
                holder.checkBox.setVisibility(View.VISIBLE);
            } else {
                holder.checkBox.setVisibility(View.GONE);
            }
            return view;
        }


        @Override
        public Filter getFilter() {
            if (null == mFilter) {
                mFilter = new MyFilter();
            }
            return mFilter;
        }

        // 自定义Filter类
        class MyFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Map<String, Object>> newData = new ArrayList<Map<String, Object>>();
                FilterResults filterResults = new FilterResults();
                String filterString = charSequence.toString().trim()
                        .toLowerCase();
                if (TextUtils.isEmpty(filterString)) {
                    newData = mBackData;
                } else {
                    // 过滤出新数据
                    for (int i = 0; i < mBackData.size(); i++) {
                        String str = null;
                        str = mBackData.get(i).get(FILENAME).toString();
                        if (-1 != str.toLowerCase().indexOf(filterString)) {
                            newData.add(mBackData.get(i));
                        }
                    }

                }
                filterResults.values = newData;
                filterResults.count = newData.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mData = (List<Map<String, Object>>) filterResults.values;
                if (filterResults.count > 0) {
                    mDataAdpter.notifyDataSetChanged(); // 通知数据发生了改变
                } else {
                    mDataAdpter.notifyDataSetInvalidated(); // 通知数据失效
                }
            }
        }
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
    public void getFileData(final File file,String filename) {

        Retrofit retrofit = HttpControl.getInstance(DataServerManagerActivity.this).getRetrofit();
        CoordianteApi getfiles = retrofit.create(CoordianteApi.class);
        getfiles.Rxgetdata(filename).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).subscribe(new Action1<ResponseBody>() {
            @Override
            public void call(ResponseBody responseBody) {
                String content = null;
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




    public void uncheckedAll(){
        for (int i = 0; i < mData.size(); i++) {
            mListView.setItemChecked(i, false);
            isSelectALL = false;
        }

    }


    public void onResume() {
        super.onResume();

        StatService.onResume(this);
    }

    public void onPause() {
        super.onPause();

        StatService.onPause(this);
    }

    public void back(View view) {
        finish();
    }


}

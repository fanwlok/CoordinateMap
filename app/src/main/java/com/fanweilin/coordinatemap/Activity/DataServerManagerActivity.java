package com.fanweilin.coordinatemap.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.BosObjectSummary;
import com.baidubce.services.bos.model.ListObjectsRequest;
import com.baidubce.services.bos.model.ListObjectsResponse;
import com.fanweilin.coordinatemap.Class.SpfOlMap;
import com.fanweilin.coordinatemap.DataModel.BaiduDataApi;
import com.fanweilin.coordinatemap.DataModel.BaiduHttpControl;
import com.fanweilin.coordinatemap.DataModel.Constants;
import com.fanweilin.coordinatemap.DataModel.CoordianteApi;
import com.fanweilin.coordinatemap.DataModel.HttpControl;
import com.fanweilin.coordinatemap.DataModel.ReasonCreate;
import com.fanweilin.coordinatemap.DataModel.RetryWithDelay;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.greendao.Olfiles;
import com.fanweilin.greendao.PointData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

public class DataServerManagerActivity extends AppCompatActivity implements View.OnClickListener, android.support.v7.widget.SearchView.OnQueryTextListener {
    private ListView mListView;
    private DataAdpter mDataAdpter;
    private boolean show = false;
    private List<Map<String, Object>> mData;
    private List<Map<String, Object>> mBackData;
    private LinearLayout layoutShow;
    private RelativeLayout rlEdit;
    private boolean isSelectALL;
    private Button btnEdit;
    private Button btnAll;
    private Button btnCancle;
    private Button btndelete;
    private String fileid;
    private String filename;
    private SQLiteDatabase db;
    private android.support.v7.widget.SearchView searchView;
    private Olfiles files;
    private Toolbar toolbar;
    private List<PointData> pointDatas;
    public static final String FILENAME = "filename";
    public static final String SUBTITLE = "subtitle";
    public static final String Id = "id";
    //服务器数据ID
    public static final String SERVERID = "SERVERID";

    private ProgressDialog dialog;
    private int deleteNum=0;

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
        files = new Olfiles();
        mData = new ArrayList<>();
        toolbar =  findViewById(R.id.toolbar);
        rlEdit =  findViewById(R.id.rl_server);
        btnEdit = findViewById(R.id.btn_edit);
        btnCancle =  findViewById(R.id.btn_cancel);
        btnAll = findViewById(R.id.btn_all);
        btndelete = findViewById(R.id.btn_delete);
        mListView =  findViewById(R.id.data_manager_list);
        mListView.setOnItemClickListener(new ListItemClick());
        layoutShow =  findViewById(R.id.layoutshow);
        btnEdit.setOnClickListener(this);
        btnCancle.setOnClickListener(this);
        btnAll.setOnClickListener(this);
        btndelete.setOnClickListener(this);
        mListView.setTextFilterEnabled(true);
        isSelectALL = false;
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        fileid=data.spfOlMapSet.getString(SpfOlMap.MAPID,"");
        filename=data.spfOlMapSet.getString(SpfOlMap.MAPNAME,"");
        files = data.findOrderOlByName(fileid);
        files.resetPointolItems();
    }

    public void getData() {
        mData.clear();
        mData.clear();
        files.resetPointolItems();
        pointDatas = files.getPointolItems();
        int count = pointDatas.size();
        for (int i = 0; i < count; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(FILENAME, pointDatas.get(i).getName());
       String s= pointDatas.get(i).getName();
            map.put(SUBTITLE, pointDatas.get(i).getAddress());
            map.put(Id,i);
            map.put(SERVERID,pointDatas.get(i).getGuid());
            mData.add(map);
        }
        mBackData = mData;
    }

    public class ListItemClick implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(!show){
                int dataId= (int) mData.get(position).get(Id);
                onListItemClick(dataId);
            }

        }
    }
    private void onListItemClick( int dataid ){
        Intent intent=new Intent(DataServerManagerActivity.this,MainMapsActivity.class);
        intent.putExtra("latitude",Double.valueOf(pointDatas.get(dataid).getGcjlatitude()));
        intent.putExtra("longitude",Double.valueOf(pointDatas.get(dataid).getGcjlongitude()));
        startActivity(intent);
        finish();

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
        final List<String > ids = new ArrayList<String>();
        final List<Integer> deIds=new ArrayList<Integer>();
        for (int i = 0; i < len; i++) {
            if (checkedArray.valueAt(i)) {
                int k = checkedArray.keyAt(i);
                deIds.add(k);
                String id = (String) mData.get(k).get(SERVERID);
                ids.add(id);
            }
        }

        if(ids.size()>=1000){
            Toast.makeText(this,"一次最多删除一千条数据",Toast.LENGTH_SHORT);
            return;
        }else if(ids.size()==0) {

        }else {
           deleteData(ids);

        }
    }
    private void  deletPhotos(String id){

    }
    public void deleteData(final List<String> ids){
        deleteNum=0;
        BosClientConfiguration config = new BosClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(Constants.BOSAK, Constants.BOSSK));
        config.setEndpoint(GZ);    //传入Bucket所在区域域名
        final BosClient client = new BosClient(config);

        Retrofit retrofit = BaiduHttpControl.getInstance(getApplicationContext()).getRetrofit();
        final BaiduDataApi baiduDataApi = retrofit.create(BaiduDataApi.class);
        Observable.fromIterable(ids).subscribeOn(Schedulers.io()).flatMap(new Function<String, Observable<String>>() {
            @Override
            public Observable<String> apply(String s) throws Exception {

                // 构造ListObjectsRequest请求
                ListObjectsRequest listObjectsRequest = new ListObjectsRequest(getBucketName());

                // 设置参数
                listObjectsRequest.setMaxKeys(100);
                listObjectsRequest.setPrefix(getOlFileName(s));

                // 获取指定Bucket下符合上述条件的所有Object信息
                ListObjectsResponse listing = client.listObjects(listObjectsRequest);
                List<BosObjectSummary> summaryList = listing.getContents();
                for (BosObjectSummary objectSummary : summaryList) {
                    client.deleteObject(getBucketName(),objectSummary.getKey());
                }
                return Observable.just(s);
            }

        }).flatMap(new Function<String, Observable<ReasonCreate>>() {
            @Override
            public Observable<ReasonCreate> apply(String s) throws Exception {
                deletPhotos(s);
                return baiduDataApi.Rxdeletedata(s, Constants.geomapid,Constants.ak);
            }

        }).retryWhen(new RetryWithDelay()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ReasonCreate>() {

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
                    public void onNext(ReasonCreate reasonCreate) {
                        deleteNum++;
                        if(deleteNum==ids.size()){
                            dialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        }else {
                            dialog.setMessage("已删除"+String.valueOf(deleteNum)+"条数据");
                        }

                    }
                });
    }
    public String getOlFileName(String pointID){
        final String mapid=data.spfOlMapSet.getString(SpfOlMap.MAPID,null);
        String key=mapid+"/"+pointID+"/";
        return key;
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
                holder.name = view.findViewById(R.id.name);
                holder.subtitle = view.findViewById(R.id.tv_subtitle);
                holder.checkBox = view.findViewById(R.id.checkbox);
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
    private String GZ="gz.bcebos.com";
    public String getBucketName(){
        return "jwddw";
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

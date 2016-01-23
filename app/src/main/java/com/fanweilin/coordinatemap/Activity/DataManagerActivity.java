package com.fanweilin.coordinatemap.Activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.fanweilin.coordinatemap.Class.LatStyle;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.ConvertLatlng;
import com.fanweilin.coordinatemap.widget.CheckableRelativeLayout;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.ShowData;
import com.fanweilin.greendao.ShowDataDao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManagerActivity extends Activity implements View.OnClickListener, SearchView.OnQueryTextListener {
    private ListView mListView;
    private TextView mTextView;
    private String title;
    private DataAdpter mDataAdpter;
    private ConvertLatlng convertLatlng;
    private boolean show = false;
    private List<Map<String, Object>> mData;
    private List<Map<String, Object>> mBackData;
    private LinearLayout layoutShow;
    private CheckableRelativeLayout selectALL;
    private boolean isSelectALL;
    private android.support.v7.widget.AppCompatButton btnShow;
    private android.support.v7.widget.AppCompatButton btnCancle;
    private RelativeLayout rlTopbar;
    private RelativeLayout rlSearch;
    private SearchView searchView;
    private boolean searchShow = false;
    private int CoordStyle;
    private int DataStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_data_manager);
        init();
        getData();
        mTextView.setText(title);
        mDataAdpter = new DataAdpter(this);
        mListView.setAdapter(mDataAdpter);
        mListView.setOnItemClickListener(new onListItemClick());

    }

    public void init() {
        mData = new ArrayList<>();
        convertLatlng = new ConvertLatlng();
        btnCancle = (AppCompatButton) findViewById(R.id.btn_cancel);
        btnShow = (AppCompatButton) findViewById(R.id.btn_show);
        mListView = (ListView) findViewById(R.id.data_manager_list);
        mTextView = (TextView) findViewById(R.id.data_manager_text_filname);
        layoutShow = (LinearLayout) findViewById(R.id.layoutshow);
        rlTopbar = (RelativeLayout) findViewById(R.id.data_manager_topbar);
        searchView = (SearchView) findViewById(R.id.search_view);
        rlSearch = (RelativeLayout) findViewById(R.id.rl_search);
        btnCancle.setOnClickListener(this);
        btnShow.setOnClickListener(this);
        searchView.setOnQueryTextListener(this);
        mListView.setTextFilterEnabled(true);
        selectALL = (CheckableRelativeLayout) findViewById(R.id.select_all);
        isSelectALL = false;
    }


    public List<Map<String, Object>> getData() {
        String path;
        Intent intent = this.getIntent();
        title = intent.getStringExtra("title");
        path = intent.getStringExtra("path");
        CoordStyle = intent.getIntExtra("CoordStyle", LatStyle.GPSSYTELE);
        DataStyle = intent.getIntExtra("DataStyle", LatStyle.DEGREE);
        FileReader fr;
        BufferedReader bfr = null;
        try {
            fr = new FileReader(path);
            bfr = new BufferedReader(fr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        try {
            if (bfr != null) {
                while ((line = bfr.readLine()) != null) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    String[] split;
                    split = line.split(",");
                    map.put("name", split[0]);
                    map.put("latitude", split[1]);
                    map.put("longitude", split[2]);
                    mData.add(map);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mBackData = mData;
        return mData;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_show:
                SparseBooleanArray checkedArray = mListView.getCheckedItemPositions();
                getShowDataDao().deleteAll();
                data data = (com.fanweilin.coordinatemap.Activity.data) getApplication();
                SQLiteDatabase db = data.getDb();
                db.beginTransaction();
                for (int i = 0; i < checkedArray.size(); i++) {
                    if (checkedArray.valueAt(i)) {
                        Map<String, Object> map = new HashMap<>();
                        map = mData.get(i);
                        Log.d("DataMANAGER", String.valueOf(CoordStyle));
                        Log.d("DataMANAGER", String.valueOf(DataStyle));
                        Log.d("test1_datachlick", String.valueOf(DataStyle));
                        ShowData showData = new ShowData(null, (String) map.get("name"), (String) map.get("latitude"), (String) map.get("longitude"), CoordStyle, DataStyle);
                        getShowDataDao().insert(showData);
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                Intent intent = new Intent();
                intent.putExtra("data", "more");
                intent.setClass(DataManagerActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_cancel:
                show = false;
                selectALL.setVisibility(View.GONE);
                layoutShow.setVisibility(View.GONE);
                mDataAdpter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
//        if (s == null || s.length() == 0) {
//           mListView.clearTextFilter();
//        } else {
//            mListView.setFilterText(s);
//        }
//        return true;
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
        TextView latitude;
        TextView longitude;
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
                holder.latitude = (TextView) view.findViewById(R.id.latitude);
                holder.longitude = (TextView) view.findViewById(R.id.longitude);
                holder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.name.setText((String) mData.get(i).get("name"));
            holder.latitude.setText(mData.get(i).get("latitude") + ",");
            holder.longitude.setText((String) mData.get(i).get("longitude"));
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
                        str = mBackData.get(i).get("name").toString();
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
                    mDataAdpter.notifyDataSetChanged();  // 通知数据发生了改变
                } else {
                    mDataAdpter.notifyDataSetInvalidated(); // 通知数据失效
                }
            }
        }
    }

    public class onListItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (!show) {
                double Lat = Double.parseDouble((String) mData.get(i).get("latitude"));
                double Lng = Double.parseDouble((String) mData.get(i).get("longitude"));
                Intent intent = new Intent();
                intent.putExtra("Lat", Lat);
                intent.putExtra("Lng", Lng);
                intent.putExtra("name", mData.get(i).get("name").toString());
                intent.putExtra("activityname", "com.fanweilin.coordinatemap.Activity.DataManagerActivity");
                intent.putExtra("data", "one");
                intent.putExtra("CoordStyle", CoordStyle);
                intent.putExtra("DataStyle", DataStyle);
                Log.d("test1_data", String.valueOf(DataStyle));
                ComponentName componentName = new ComponentName(DataManagerActivity.this, MainActivity.class);
                intent.setComponent(componentName);
                startActivity(intent);
            }
        }
    }

    public void search(View view) {
        if (searchShow) {
            searchShow = false;
            rlSearch.setVisibility(View.GONE);
        } else {

            searchShow = true;
            rlSearch.setVisibility(View.VISIBLE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    public void navigation(View view) {
        if (!show) {
            show = true;
            if (rlSearch.getVisibility() == View.VISIBLE) {
                rlSearch.setVisibility(View.GONE);
            }
            selectALL.setVisibility(View.VISIBLE);
            layoutShow.setVisibility(View.VISIBLE);
            mDataAdpter.notifyDataSetChanged();

        }
    }

    public void checkall(View view) {
        if (isSelectALL) {
            for (int i = 0; i < mData.size(); i++) {
                mListView.setItemChecked(i, false);
                isSelectALL = false;
            }
            selectALL.setChecked(false);

        } else {
            for (int i = 0; i < mData.size(); i++) {
                mListView.setItemChecked(i, true);
                isSelectALL = true;
            }
            selectALL.setChecked(true);
        }
    }


    private ShowDataDao getShowDataDao() {
        DaoSession mDaoSession;
        data app = (com.fanweilin.coordinatemap.Activity.data) getApplication();
        mDaoSession = app.getmDaoSession();
        return mDaoSession.getShowDataDao();
    }


    public void back(View view) {
        finish();
    }


}

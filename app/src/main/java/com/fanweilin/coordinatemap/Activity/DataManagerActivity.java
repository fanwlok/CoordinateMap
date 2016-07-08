package com.fanweilin.coordinatemap.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mobstat.StatService;
import com.fanweilin.coordinatemap.Class.FilesSetting;
import com.fanweilin.coordinatemap.Class.LatStyle;
import com.fanweilin.coordinatemap.Class.PointDataParcel;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.computing.ConvertLatlng;
import com.fanweilin.coordinatemap.widget.CheckableRelativeLayout;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.PictureData;
import com.fanweilin.greendao.PointData;
import com.fanweilin.greendao.ShowData;
import com.fanweilin.greendao.ShowDataDao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManagerActivity extends AppCompatActivity implements View.OnClickListener, android.support.v7.widget.SearchView.OnQueryTextListener {
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
    private android.support.v7.widget.SearchView searchView;
    private boolean searchShow = false;
    private int CoordStyle;
    private int DataStyle;
    private Files files;
    private Toolbar toolbar;
    private List<PointData> pointDatas;
    private int datastyle;
    private int coordstyle;
    public static final String FILENAME = "filename";
    public static final String SUBTITLE = "subtitle";
    public static final String Id = "id";
    private android.support.v7.widget.AppCompatButton btndelete;
    //checkbox
    private CheckBox name;
    private CheckBox wgs;
    private CheckBox altitude;
    private CheckBox bd;
    private CheckBox describe;
    private CheckBox address;
    private CheckBox photo;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_data_manager);
        init();
        getData();
        toolbar.setTitle(files.getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDataAdpter = new DataAdpter(this);
        mListView.setAdapter(mDataAdpter);
        mListView.setOnItemClickListener(new onListItemClick());
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_edit:
                        if (!show) {
                            show = true;
                            selectALL.setVisibility(View.VISIBLE);
                            layoutShow.setVisibility(View.VISIBLE);
                            mDataAdpter.notifyDataSetChanged();

                        }
                        break;
                    case R.id.item_add:
                        ShowDialog();
                        break;
                    case R.id.item_output:
                        importfile();
                        break;
                    case R.id.item_datemanager_set:
                        ;
                        DialogSetting();
                }
                return false;
            }
        });

    }

    private void ShowDialog() {
        Spinner spinner;
        datastyle = data.currentLatFormat;
        coordstyle = data.currentCoordinate;
        final EditText edtLatitude;
        final EditText edtLongtitude;
        RadioGroup radioGroup;
        final RadioButton radiobtnDegree;
        final RadioButton radiobtndms;
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_pointdata, null);
        AlertDialog.Builder buildersearch = new AlertDialog.Builder(DataManagerActivity.this);
        buildersearch.setView(dialogView);
        spinner = (Spinner) dialogView.findViewById(R.id.spn_cds);
        spinner.setSelection(data.currentCoordinate);
        radiobtnDegree = (RadioButton) dialogView.findViewById(R.id.radio_decimal);
        radiobtndms = (RadioButton) dialogView.findViewById(R.id.radio_dms);
        final EditText edtname = (EditText) dialogView.findViewById(R.id.edt_pointname);
        edtLatitude = (EditText) dialogView.findViewById(R.id.edt_latitude);
        edtLongtitude = (EditText) dialogView.findViewById(R.id.edt_longtitude);
        if (datastyle == LatStyle.DEGREE) {
            radiobtnDegree.setChecked(true);
        } else {
            radiobtndms.setChecked(true);
        }
        radioGroup = (RadioGroup) dialogView.findViewById(R.id.radgroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == radiobtnDegree.getId()) {
                    datastyle = LatStyle.DEGREE;
                    data.setCurrentLatFormat(LatStyle.DEGREE);
                    edtLatitude.setHint("39.9631757");
                    edtLongtitude.setHint("116.4002442");
                } else {
                    datastyle = LatStyle.DMS;
                    data.setCurrentLatFormat(LatStyle.DMS);
                    edtLatitude.setHint("30.345245");
                    edtLongtitude.setHint("120.142835");

                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View view,
                                       int position, long id) {
                coordstyle = position;
                data.setCurrentCoordinate(position);
            }

            @Override
            public void onNothingSelected(AdapterView parent) {
            }
        });
        buildersearch.setTitle("新增点");
        buildersearch.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (edtLatitude.getText().toString().isEmpty() != true && edtLongtitude.getText().toString().isEmpty() != true) {
                    LatLng point = new LatLng(Double.parseDouble(edtLatitude.getText().toString()), Double.parseDouble(edtLongtitude.getText().toString()));
                    LatLng bd = MainActivity.ComanLngConvertBdLngt(point, coordstyle, datastyle);
                    PointData pointData = new PointData();
                    pointData.setName(edtname.getText().toString());
                    pointData.setLatitude(edtLatitude.getText().toString());
                    pointData.setLongitude(edtLongtitude.getText().toString());
                    DecimalFormat df = new DecimalFormat("#.0000000");
                    pointData.setBaidulatitude(String.valueOf(df.format(bd.latitude)));
                    pointData.setBaidulongitude(String.valueOf(df.format(bd.longitude)));
                    pointData.setAddress("");
                    if (coordstyle == LatStyle.GPSSYTELE) {
                        if (datastyle == LatStyle.DEGREE) {
                            pointData.setWgslatitude(edtLatitude.getText().toString());
                            pointData.setWgslongitude(edtLongtitude.getText().toString());
                        } else {
                            pointData.setWgslatitude(String.valueOf(df.format(ConvertLatlng.convertToDecimalByString(edtLatitude.getText().toString()))));
                            pointData.setWgslongitude(String.valueOf(df.format(ConvertLatlng.convertToDecimalByString(edtLongtitude.getText().toString()))));
                        }
                    }
                    data.createPointData(files, pointData);
                    files.resetPointItems();
                    getData();
                    mDataAdpter.notifyDataSetChanged();
                }
            }
        });
        buildersearch.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        buildersearch.create().show();
    }

    public void init() {
        files = new Files();
        mData = new ArrayList<>();
        convertLatlng = new ConvertLatlng();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnCancle = (AppCompatButton) findViewById(R.id.btn_cancel);
        btnShow = (AppCompatButton) findViewById(R.id.btn_show);
        mListView = (ListView) findViewById(R.id.data_manager_list);
        layoutShow = (LinearLayout) findViewById(R.id.layoutshow);
        rlTopbar = (RelativeLayout) findViewById(R.id.data_manager_topbar);

        btnCancle.setOnClickListener(this);
        btnShow.setOnClickListener(this);
        mListView.setTextFilterEnabled(true);
        selectALL = (CheckableRelativeLayout) findViewById(R.id.select_all);
        isSelectALL = false;
        btndelete = (AppCompatButton) findViewById(R.id.btn_delete);
        btndelete.setOnClickListener(this);
    }


    public void getData() {
        mData.clear();
        String filename;
        Intent intent = getIntent();
        filename = intent.getStringExtra(FILENAME);
        files = data.findOrderByName(filename);
        files.resetPointItems();
        pointDatas = new ArrayList<PointData>();
        pointDatas = files.getPointItems();
        int count = pointDatas.size();
        for (int i = 0; i < count; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(FILENAME, pointDatas.get(i).getName());
            map.put(SUBTITLE, pointDatas.get(i).getAddress());
            map.put(Id, i);
            mData.add(map);
        }
        mBackData = mData;
    }

    public void DialogSetting() {
        View dialogSetview = LayoutInflater.from(DataManagerActivity.this).inflate(R.layout.dialog_file_output_setting, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(DataManagerActivity.this);
        builder.setTitle("设置").setView(dialogSetview);
        name = (CheckBox) dialogSetview.findViewById(R.id.cb_point_name);
        wgs = (CheckBox) dialogSetview.findViewById(R.id.cb_point_wgs);
        altitude = (CheckBox) dialogSetview.findViewById(R.id.cb_point_altitude);
        bd = (CheckBox) dialogSetview.findViewById(R.id.cb_point_bd);
        describe = (CheckBox) dialogSetview.findViewById(R.id.cb_point_describe);
        address = (CheckBox) dialogSetview.findViewById(R.id.cb_point_adress);
        photo = (CheckBox) dialogSetview.findViewById(R.id.cb_point_photo);
        name.setChecked(FilesSetting.NAME_IS_DOWN);
        wgs.setChecked(FilesSetting.WGS_IS_DOWN);
        altitude.setChecked(FilesSetting.ALTITUDE_IS_DOWN);
        bd.setChecked(FilesSetting.BAIDU_IS_DOWN);
        describe.setChecked(FilesSetting.DESCRIBE_IS_DOWN);
        address.setChecked(FilesSetting.ADDRESS_IS_DOWN);
        photo.setChecked(FilesSetting.PHOTO_IS_DOWN);
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                FilesSetting.NAME_IS_DOWN = name.isChecked();
                data.spfSetName(FilesSetting.NAME_IS_DOWN);
                FilesSetting.WGS_IS_DOWN = wgs.isChecked();
                data.spfSetWgs(FilesSetting.WGS_IS_DOWN);
                FilesSetting.ALTITUDE_IS_DOWN = altitude.isChecked();
                data.spfSetAltitude(FilesSetting.ALTITUDE_IS_DOWN);
                FilesSetting.BAIDU_IS_DOWN = bd.isChecked();
                data.spfSetBd(FilesSetting.BAIDU_IS_DOWN);
                FilesSetting.DESCRIBE_IS_DOWN = describe.isChecked();
                data.spfSetDescribe(FilesSetting.DESCRIBE_IS_DOWN);
                FilesSetting.ADDRESS_IS_DOWN = address.isChecked();
                data.spfSetAddress(FilesSetting.ADDRESS_IS_DOWN);
                FilesSetting.PHOTO_IS_DOWN = photo.isChecked();
                data.spfSetPhoto(FilesSetting.PHOTO_IS_DOWN);

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    public void importfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DataManagerActivity.this);
        final AppCompatEditText editText = new AppCompatEditText(this);
        editText.setText(files.getTitle());
        builder.setTitle("请输入文件名").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    File dirs = new File(Environment.getExternalStorageDirectory(), "经纬度定位");
                    if (!dirs.exists()) {
                        dirs.mkdirs();
                    }
                    File filedirs = new File(dirs.getPath(), editText.getText().toString());
                    if (!filedirs.exists()) {
                        filedirs.mkdirs();
                    }
                    File file = new File(filedirs.getPath(), editText.getText().toString() + ".csv");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    OutputStreamWriter outStream = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
                    BufferedWriter bufferedWriter = new BufferedWriter(outStream);
                    for (int i = 0; i < pointDatas.size(); i++) {
                        StringBuilder context = new StringBuilder();
                        if (FilesSetting.NAME_IS_DOWN) {
                            context.append(pointDatas.get(i).getName());
                        }
                        if (FilesSetting.WGS_IS_DOWN) {
                            if (TextUtils.isEmpty(pointDatas.get(i).getWgslatitude())) {
                                context.append("," + pointDatas.get(i).getWgslatitude() + "," + pointDatas.get(i).getWgslongitude());
                            } else {
                                context.append("," + pointDatas.get(i).getWgslatitude() + "," + pointDatas.get(i).getWgslongitude());
                            }
                        }

                        if (FilesSetting.BAIDU_IS_DOWN) {
                            context.append("," + pointDatas.get(i).getBaidulatitude() + "," + pointDatas.get(i).getBaidulongitude());
                        }
                        if (FilesSetting.ALTITUDE_IS_DOWN) {
                            context.append("," + pointDatas.get(i).getAltitude());
                        }
                        if (FilesSetting.DESCRIBE_IS_DOWN) {
                            context.append("," + pointDatas.get(i).getDescribe());
                        }
                        if (FilesSetting.ADDRESS_IS_DOWN) {
                            context.append("," + pointDatas.get(i).getAddress());
                        }
                        if (FilesSetting.PHOTO_IS_DOWN) {
                            List<PictureData> pictureItems = new ArrayList<PictureData>();
                            pictureItems = pointDatas.get(i).getPictureItems();
                            for (int item = 0; item < pictureItems.size(); item++) {
                                File fromFile = new File(pictureItems.get(item).getPath());
                                File toFileDir = new File(filedirs, pointDatas.get(i).getName());
                                if (!toFileDir.exists()) {
                                    toFileDir.mkdirs();
                                }
                                File toFile = new File(toFileDir, fromFile.getName());

                                if (!toFile.exists()) {
                                    toFile.createNewFile();
                                }
                                copyFile(fromFile, toFile);
                            }
                        }
                        bufferedWriter.write(context.toString());
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                    }
                    bufferedWriter.close();
                    outStream.close();
                    Toast.makeText(DataManagerActivity.this, "存储位置" + filedirs.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    return;
                } catch (IOException e) {
                    return;
                }

            }

        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();

    }

    public void copyFile(File oldPath, File newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            if (oldPath.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    @Override
    public void onClick(View view) {
        SparseBooleanArray checkedArray = new SparseBooleanArray();
        checkedArray = mListView.getCheckedItemPositions();
        final SQLiteDatabase db = data.getDb();
        switch (view.getId()) {
            case R.id.btn_show:
                try {
                    db.beginTransaction();
                    for (int i = 0; i < checkedArray.size(); i++) {
                        if (checkedArray.valueAt(i)) {
                            int k = checkedArray.keyAt(i);
                            int id = (int) mData.get(k).get(Id);
//                        ShowData showData = new ShowData(null, pointDatas.get(id).getName(), pointDatas.get(id).getBaidulatitude(),
//                                pointDatas.get(id).getBaidulongitude(), LatStyle.BAIDUMAPSTYELE, LatStyle.DEGREE);
                            ShowData showData = new ShowData();
                            showData.setTitle(pointDatas.get(id).getName());
                            showData.setLatitude(pointDatas.get(id).getBaidulatitude());
                            showData.setLongitude(pointDatas.get(id).getBaidulongitude());
                            showData.setCdstyle(LatStyle.BAIDUMAPSTYELE);
                            showData.setDatastyle(LatStyle.DEGREE);
                            showData.setPointid(pointDatas.get(i).getId());
                            showData.setFileid(files.getId());
                            getShowDataDao().insert(showData);
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
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
            case R.id.btn_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(DataManagerActivity.this);
                builder.setTitle("数据删除");
                builder.setMessage("确定删除吗？");
                final SparseBooleanArray finalCheckedArray = checkedArray;
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            db.beginTransaction();
                            for (int i = 0; i < finalCheckedArray.size(); i++) {
                                if (finalCheckedArray.valueAt(i)) {
                                    int k = finalCheckedArray.keyAt(i);
                                    int id = (int) mData.get(k).get(Id);
                                    data.deletepointdata(pointDatas.get(id));
                                }
                            }
                            db.setTransactionSuccessful();
                        } finally {
                            db.endTransaction();
                        }
                        getData();
                        mDataAdpter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_datamanager, menu);
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
//            holder.latitude.setText(mData.get(i).get("latitude") + ",");
//            holder.longitude.setText((String) mData.get(i).get("longitude"));
            if (pointDatas.get(i).getAddress().isEmpty()) {
                holder.subtitle.setText(pointDatas.get(i).getLatitude() + "," + pointDatas.get(i).getLongitude());

            } else {
                holder.subtitle.setText(pointDatas.get(i).getAddress().toString());
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

    private void onListItemClick(final PointData pointData) {
        ListView listView = new ListView(this);
        listView.setAdapter(new DialogListAdapter(this));
        final AlertDialog.Builder builder = new AlertDialog.Builder(DataManagerActivity.this);
        final AlertDialog dialog = builder.setView(listView).show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PointDataParcel pp = new PointDataParcel();
                switch (position) {
                    case 0:
                        pp = setPointdataParcel(pointData);
                        Intent intent = new Intent();
                        intent.putExtra(MainActivity.GETPOINTDATAPARCE, pp);
                        intent.setClass(DataManagerActivity.this, MainActivity.class);
                        intent.putExtra("data", "one");
                        ShowData showData = new ShowData();
                        showData.setTitle(pointData.getName());
                        showData.setLatitude(pointData.getBaidulatitude());
                        showData.setLongitude(pointData.getBaidulongitude());
                        showData.setCdstyle(LatStyle.BAIDUMAPSTYELE);
                        showData.setDatastyle(LatStyle.DEGREE);
                        showData.setPointid(pointData.getId());
                        showData.setFileid(files.getId());
                        getShowDataDao().insert(showData);
                        intent.putExtra(MainActivity.DATAMANAGERACTIVITY, "com.fanweilin.coordinatemap.Activity.DataManagerActivity");
                        startActivity(intent);
                        dialog.dismiss();
                        break;
                    case 1:
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(DataManagerActivity.this);
                        builder1.setTitle("是否删除数据").setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                data.deletepointdata(pointData);
                                files.resetPointItems();
                                getData();
                                mDataAdpter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                        dialog.dismiss();
                        break;
                    case 2:
                        pp = setPointdataParcel(pointData);
                        Intent intentWaypoint = new Intent();
                        intentWaypoint.putExtra(WayponitActivity.POINTDATA, pp);
                        intentWaypoint.setClass(DataManagerActivity.this, WayponitActivity.class);
                        startActivity(intentWaypoint);
                        dialog.dismiss();
                        break;
                }
            }
        });

    }

    private PointDataParcel setPointdataParcel(PointData pointData) {
        PointDataParcel pp = new PointDataParcel();
        pp.setActivity(WayponitActivity.DATAMANAGERACTIVITY);
        pp.setAddress(pointData.getAddress());
        pp.setPointname(pointData.getName());
        pp.setAltitude(pointData.getAltitude());
        pp.setBaiduLongitude(pointData.getBaidulongitude());
        pp.setBaiduLatitude(pointData.getBaidulatitude());
        pp.setWgsLatitude(pointData.getWgslatitude());
        pp.setWgsLongitude(pointData.getWgslongitude());
        pp.setDescribe(pointData.getDescribe());
        pp.setPointdataid(pointData.getId());
        pp.setFileid(files.getId());
        return pp;
    }

    public String[] dataItems = {"显示", "删除", "编辑"};

    public class DialogListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public DialogListAdapter(Context context) {
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
            return view;
        }
    }

    public class onListItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (!show) {
                PointData pointData = pointDatas.get(i);
                onListItemClick(pointData);

            }
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
        mDaoSession = data.getmDaoSession();
        return mDaoSession.getShowDataDao();
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

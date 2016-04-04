package com.fanweilin.coordinatemap.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.fanweilin.coordinatemap.Class.PointDataParcel;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.widget.NoScrollGridView;
import com.fanweilin.greendao.DaoSession;
import com.fanweilin.greendao.Files;
import com.fanweilin.greendao.FilesDao;
import com.fanweilin.greendao.PictureData;
import com.fanweilin.greendao.PointData;
import com.foamtrace.photopicker.PhotoPickerActivity;
import com.foamtrace.photopicker.PhotoPreviewActivity;
import com.foamtrace.photopicker.SelectModel;
import com.foamtrace.photopicker.intent.PhotoPickerIntent;
import com.foamtrace.photopicker.intent.PhotoPreviewIntent;

import org.json.JSONArray;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.fanweilin.coordinatemap.R.dimen.space_size;

public class WayponitActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String MAIACTIVTY = "mainactivity";
    public static final String DATAMANAGERACTIVITY = "datananageractivty";
    private Toolbar toolbar;
    private Button btncameral;
    public static final int REQUEST_CODE_GET_CAMERAL = 1;
    private static final int REQUEST_PREVIEW_CODE = 2;
    private ArrayList<String> imagePaths = null;
    private ArrayList<String> imagePrePaths = null;
    private NoScrollGridView gridView;
    private int columnWidth;
    private GridAdapter gridAdapter;
    public static final String POINTDATA = "pointdata";
    private EditText pointname;
    private EditText describe;
    private EditText wsgEdit;
    private EditText baiduEdit;
    private EditText altitude;
    private TextView tvAddress;
    private DaoSession mDaoSession;
    private SQLiteDatabase db;
    private PointDataParcel pointData;
    private Cursor cursor;
    SimpleCursorAdapter adapter;
    private AppCompatSpinner spinner;
    PointData mpointdata;
    Files mFiles;
    private List<String> listName = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wayponit);
        init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用  getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spinner = new AppCompatSpinner(getSupportActionBar().getThemedContext());
        int[] to = {android.R.id.text1};
        String[] from = {FilesDao.Properties.Title.columnName};
        adapter = new SimpleCursorAdapter(this, R.layout.spinnerlist, cursor, from,
                to);
        adapter.setDropDownViewResource( R.layout.spinnerlist);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (MAIACTIVTY.equals(pointData.getActivity())) {
                    cursor.moveToPosition(position);
                    data.setCurretFilename(cursor.getString(cursor.getColumnIndex(FilesDao.Properties.Title.columnName)));
                } else {
                    setSpinnerItemSelectedByValue(spinner, mFiles.getTitle());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        toolbar.addView(spinner);




        if (MAIACTIVTY.equals(pointData.getActivity())) {
            setSpinnerItemSelectedByValue(spinner, data.currentFilename);
        } else {
            setSpinnerItemSelectedByValue(spinner, mFiles.getTitle());
        }

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.waypoint_menu_save:
                        if (MAIACTIVTY.equals(pointData.getActivity())) {
                            mpointdata.setDescribe(describe.getText().toString());
                            String name = pointname.getText().toString();
                            mpointdata.setName(name);
                            data.createPointData(data.findOrderByName(data.currentFilename), mpointdata);

                        } else if (DATAMANAGERACTIVITY.equals(pointData.getActivity())) {
                            mpointdata.setName(pointname.getText().toString());
                            mpointdata.setDescribe(describe.getText().toString());
                            data.deletePictureDateByList(mpointdata.getPictureItems());
                            mpointdata.resetPictureItems();
                        }
                        db.beginTransaction();
                        for (int i = 0; i < imagePaths.size(); i++) {
                            PictureData pictureData = new PictureData();
                            pictureData.setPath(imagePaths.get(i));
                            data.ctreatePictureDate(mpointdata, pictureData);
                        }
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        finish();
                        break;
                    case R.id.waypoint_menu_new:
                        AlertDialog.Builder builder = new AlertDialog.Builder(WayponitActivity.this);
                        final AppCompatEditText editText = new AppCompatEditText(WayponitActivity.this);
                        builder.setTitle("请输入文件名").setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Files files = new Files();
                                String filename = editText.getText().toString();
                                if (!filename.isEmpty()) {
                                    if (!isNamere(cursor, filename)) {
                                        files.setTitle(filename);
                                        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
                                        String comment = "Added on " + df.format(new Date());
                                        files.setDate(comment);
                                        getFileDao().insert(files);
                                        cursor.requery();
                                        setSpinnerItemSelectedByValue(spinner, filename);
                                    }
                                }
                            }
                        }).show();
                        break;
                }
                return true;
            }
        });
    }
    private void init() {
        gridView = (NoScrollGridView) findViewById(R.id.grv_photo);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        describe = (EditText) findViewById(R.id.edt_describe);
        pointname = (EditText) findViewById(R.id.edt_pointname);
        btncameral = (Button) findViewById(R.id.btn_cameral);
        baiduEdit = (EditText) findViewById(R.id.edt_baidu);
        wsgEdit = (EditText) findViewById(R.id.edt_wgs);
        altitude = (EditText) findViewById(R.id.edt_altitude);
        tvAddress = (TextView) findViewById(R.id.waypoint_tv_address);
       // spinner = (AppCompatSpinner) findViewById(R.id.spinner_filename);
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 3 ? 3 : cols;
        gridView.setNumColumns(cols);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int columnSpace = getResources().getDimensionPixelOffset(space_size);
        columnWidth = (screenWidth - columnSpace * (cols - 1)) / cols;
        btncameral.setOnClickListener(this);
        initdata();
        getListdata();


        // preview
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhotoPreviewIntent intent = new PhotoPreviewIntent(WayponitActivity.this);
                intent.setCurrentItem(position);
                intent.setPhotoPaths(imagePaths);
                startActivityForResult(intent, REQUEST_PREVIEW_CODE);
            }
        });


        loadAdpater(imagePrePaths);
    }

    public void initdata() {
        mDaoSession = data.getmDaoSession();
        db = data.getDb();
        mFiles = new Files();
        mpointdata = new PointData();
        Intent intent = this.getIntent();
        imagePrePaths = new ArrayList<>();
        pointData = new PointDataParcel();
        pointData = intent.getParcelableExtra(POINTDATA);
        if (MAIACTIVTY.equals(pointData.getActivity())) {
            wsgEdit.setText(pointData.getWgsLatitude() + "," + pointData.getWgsLongitude());
            baiduEdit.setText(pointData.getBaiduLatitude() + "," + pointData.getBaiduLongitude());
            altitude.setText(pointData.getAltitude());
            tvAddress.setText(pointData.getAddress());
            mpointdata.setBaidulatitude(pointData.getBaiduLatitude());
            mpointdata.setBaidulongitude(pointData.getBaiduLongitude());
            mpointdata.setWgslatitude(pointData.getWgsLatitude());
            mpointdata.setWgslongitude(pointData.getWgsLongitude());
            mpointdata.setAddress(pointData.getAddress());

        } else if (DATAMANAGERACTIVITY.equals(pointData.getActivity())) {
            mFiles = data.findOrderById(pointData.getFileid());
            mpointdata = data.findPointDataDaoById(pointData.getPointdataid());
            List<PictureData> pictureItems = new ArrayList<PictureData>();
            pictureItems = mpointdata.getPictureItems();
            for (int i = 0; i < pictureItems.size(); i++) {
                imagePrePaths.add(pictureItems.get(i).getPath());
                Log.d("i", pictureItems.get(i).getPath());

            }
            pointname.setText(pointData.getPointname());
            describe.setText(pointData.getDescribe());
            wsgEdit.setText(pointData.getWgsLatitude() + "," + pointData.getWgsLongitude());
            baiduEdit.setText(pointData.getBaiduLatitude() + "," + pointData.getBaiduLongitude());
            altitude.setText(pointData.getAltitude());
            tvAddress.setText(pointData.getAddress());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.acticty_waypoint_menu, menu);
        return true;
    }

    private void getListdata() {
        cursor = db.query(getFileDao().getTablename(), getFileDao().getAllColumns(), null, null, null, null, null);
        if (cursor.moveToFirst()) {
            String filename;
            int nameColumnIndex = cursor.getColumnIndex(FilesDao.Properties.Title.columnName);
            filename = cursor.getString(nameColumnIndex);
            listName.add(filename);
            while (cursor.moveToNext()) {
                filename = cursor.getString(nameColumnIndex);
                listName.add(filename);
            }
        }
    }

    private FilesDao getFileDao() {
        return mDaoSession.getFilesDao();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cameral:
                PhotoPickerIntent intent = new PhotoPickerIntent(WayponitActivity.this);
                intent.setSelectModel(SelectModel.MULTI);
                intent.setShowCarema(true); // 是否显示拍照
                intent.setMaxTotal(9); // 最多选择照片数量，默认为9
                intent.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
//                ImageConfig config = new ImageConfig();
//                config.minHeight = 400;
//                config.minWidth = 400;
//                config.mimeType = new String[]{"image/jpeg", "image/png"};
//                config.minSize = 1 * 1024 * 1024; // 1Mb
//                intent.setImageConfig(config);
                startActivityForResult(intent, REQUEST_CODE_GET_CAMERAL);
                break;

        }
    }
      public class  spinnerAdpter extends BaseAdapter{

          @Override
          public int getCount() {
              return 0;
          }

          @Override
          public Object getItem(int position) {
              return null;
          }

          @Override
          public long getItemId(int position) {
              return 0;
          }

          @Override
          public View getView(int position, View convertView, ViewGroup parent) {
              return null;
          }
      }
    public void setSpinnerItemSelectedByValue(AppCompatSpinner spinner, String value) {
        int k = cursor.getCount();
        int i = 0;
        for (cursor.moveToFirst(); i < k; cursor.moveToNext()) {
            int nameColumnIndex = cursor.getColumnIndex(FilesDao.Properties.Title.columnName);
            String filename = cursor.getString(nameColumnIndex);
            if (value.equals(filename)) {
                spinner.setSelection(i, true);// 默认选中项
                data.setCurretFilename(value);
                break;
            }
            i++;
        }
    }

    public boolean isNamere(Cursor cursor, String name) {
        int i = 0;
        for (cursor.moveToFirst(); i < cursor.getCount(); cursor.moveToNext()) {
            int nameColumnIndex = cursor.getColumnIndex(FilesDao.Properties.Title.columnName);
            String filename = cursor.getString(nameColumnIndex);
            if (name.equals(filename)) {
                Toast.makeText(this, "文件已存在", Toast.LENGTH_LONG).show();
                return true;
            }
            i++;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch (requestCode) {
            case REQUEST_PREVIEW_CODE:
                if (resultCode == RESULT_OK) {
                    loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT));
                }
                break;
            case REQUEST_CODE_GET_CAMERAL:
                if (resultCode == RESULT_OK) {
                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT));
                }
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadAdpater(ArrayList<String> paths) {
        if (imagePaths == null) {
            imagePaths = new ArrayList<>();
        }
        imagePaths.clear();
        imagePaths.addAll(paths);

        try {
            JSONArray obj = new JSONArray(imagePaths);
            Log.e("--", obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gridAdapter == null) {
            gridAdapter = new GridAdapter(imagePaths);
            gridView.setAdapter(gridAdapter);
        } else {
            gridAdapter.notifyDataSetChanged();
        }
    }

    private class GridAdapter extends BaseAdapter {
        private ArrayList<String> listUrls;

        public GridAdapter(ArrayList<String> listUrls) {
            this.listUrls = listUrls;
        }

        @Override
        public int getCount() {
            return listUrls.size();
        }

        @Override
        public String getItem(int position) {
            return listUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_image, null);
                imageView = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(imageView);
                // 重置ImageView宽高
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(columnWidth, columnWidth);
                imageView.setLayoutParams(params);
            } else {
                imageView = (ImageView) convertView.getTag();
            }
            Glide.with(WayponitActivity.this)
                    .load(new File(getItem(position)))
                    .placeholder(R.mipmap.default_error)
                    .error(R.mipmap.default_error)
                    .centerCrop()
                    .crossFade()
                    .into(imageView);
            return convertView;
        }
    }
    public void onResume() {


        StatService.onResume(this);
        super.onResume();
    }

    public void onPause() {


        StatService.onPause(this);
        super.onPause();
    }
}

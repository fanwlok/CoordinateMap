package com.fanweilin.coordinatemap.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.fragment.CityList_Fragment;
import com.fanweilin.coordinatemap.fragment.DownFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.hoang8f.android.segmented.SegmentedGroup;

public class Bdoffilin_activity extends FragmentActivity implements MKOfflineMapListener, CityList_Fragment.OnHeadlineSelectedListener {
    private List<String> City = null;
    private Map<String, List<String>> AllCity = null;
    private List<String> Hotcity = null;
    private List<Fragment> fragments;
    private CityList_Fragment cityfragment;
    private DownFragment downfragment;
    private ViewPager mViewPager;
    private RadioButton rbCity;
    private RadioButton rbDown;
     public MKOfflineMap mOffline;
    private info.hoang8f.android.segmented.SegmentedGroup segmentgroup;
    public ArrayList<MKOLUpdateElement> localMapList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bdoffline);
        mOffline=new MKOfflineMap();
        mOffline.init(this);
        initView();

    }

    @Override
    protected void onDestroy() {
        mOffline.destroy();
        super.onDestroy();
    }

    private void initView() {
        City = new ArrayList<String>();
        Hotcity = new ArrayList<String>();
        AllCity = new HashMap<String, List<String>>();
        segmentgroup = (SegmentedGroup) findViewById(R.id.segmented2);
        rbCity = (RadioButton) findViewById(R.id.rb_city);
        rbDown = (RadioButton) findViewById(R.id.rb_down);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        GetFragList();
        FragmentAdapter Fadpter = new FragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(Fadpter);
        mViewPager.setCurrentItem(0);
        segmentgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rbDown.isChecked()) {
                    mViewPager.setCurrentItem(1);
                } else {
                    mViewPager.setCurrentItem(0);
                }
            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        rbCity.setChecked(true);
                        break;
                    case 1:
                        rbDown.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<MKOLUpdateElement>();
        }
    }

    public void GetFragList() {
        fragments = new ArrayList<Fragment>();
        GetData();
        cityfragment = new CityList_Fragment().newInstance(City, AllCity, Hotcity);
        fragments.add(cityfragment);
        downfragment = new DownFragment();
        fragments.add(downfragment);
    }

    private void GetData() {
        ArrayList<MKOLSearchRecord> records2 = mOffline.getOfflineCityList();
        if (records2 != null) {
            for (MKOLSearchRecord r : records2) {
                Log.d("CITY", r.cityName);
                String city = r.cityName + "," + r.cityID + ","
                        + this.formatDataSize(r.size);
                List<String> chlildcitylist = new ArrayList<String>();
                if (r.childCities != null) {
                    City.add(city);
                    Log.d("CITY", r.cityName);
                    ArrayList<MKOLSearchRecord> childrecord = r.childCities;
                    for (MKOLSearchRecord child : childrecord) {
                        chlildcitylist.add(child.cityName + "," + child.cityID + "," + this.formatDataSize(child.size));
                    }
                    AllCity.put(city, chlildcitylist);
                } else {
                      Log.d("hotcity",city);
                    Hotcity.add(city);
                }

            }

        }

    }


    @Override
    public void onArticleSelected(String cityid) {

          mOffline.start(Integer.parseInt(cityid));
          Toast.makeText(this, "开始下载离线地图. cityid: " + cityid, Toast.LENGTH_SHORT)
                .show();
        mViewPager.setCurrentItem(1);
    }


    public class FragmentAdapter extends FragmentPagerAdapter {
        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }


    public String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    class City {
        City(String string) {
            city = string.split(",");
        }

        String[] city;

        public int getCityid() {
            return Integer.getInteger(city[1]);
        }

        public String getCityname() {
            return city[0];
        }

        public String getCitysize() {
            return city[2];
        }
    }

    public Map<String, Object> getcity(String string) {
        String[] city;
        Map<String, Object> map = new HashMap<String, Object>();
        city = string.split(",");
        map.put("cityname", city[0]);
        map.put("cityid", city[1]);
        map.put("citysize", city[2]);
        return map;
    }
    @Override
    public void onGetOfflineMapState(int type, int state) {
        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                // 处理下载进度更新提示
                if (update != null) {

                   downfragment. updateView();
                }
            }
            break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                // MKOLUpdateElement e = mOffline.getUpdateInfo(state);

                break;
            default:
                break;
        }

    }
    public void back(View view) {
        finish();
    }
}

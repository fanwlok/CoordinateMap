package com.fanweilin.coordinatemap.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.fanweilin.coordinatemap.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityList_Fragment extends Fragment {
    private ExpandableListView mExlistVIEW;
    private List<String> City = null;
    private Map<String, List<String>> AllCity = null;
    private List<String> HotCity = null;
    private ListView mListView;

    OnHeadlineSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        void onArticleSelected(String cityid);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_city_list, container, false);
        CityExAdapter myadpter = new CityExAdapter(getActivity());
        HotCityAdapter hotCityAdapter = new HotCityAdapter(getActivity());
        mListView = view.findViewById(R.id.lv_hotcity);
        mExlistVIEW = view.findViewById(R.id.el_city);
        mListView.setAdapter(hotCityAdapter);
        mExlistVIEW.setAdapter(myadpter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String city = HotCity.get(position);
                String cityid = (String) getcity(city).get("cityid");
                mCallback.onArticleSelected(cityid);
            }
        });
        mExlistVIEW.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String city = AllCity.get(City.get(groupPosition)).get(childPosition);
                String cityid= (String) getcity(city).get("cityid");
                mCallback.onArticleSelected(cityid);
                return false;
            }
        });
        return view;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            HotCity = new ArrayList<String>();
            City = new ArrayList<String>();
            AllCity = new HashMap<String, List<String>>();
            City = bundle.getStringArrayList("city");
            SerializableMap serializableMap = (SerializableMap) bundle.get("map");
            AllCity = serializableMap.getMap();
            HotCity = bundle.getStringArrayList("hotcity");
            for (int i = 0; i < HotCity.size(); i++) {
                Log.d("CY", HotCity.get(i));
            }
        }
    }

    public static CityList_Fragment newInstance(List<String> citylist, Map<String, List<String>> childcitylist, List<String> hotcity) {
        CityList_Fragment f = new CityList_Fragment();
        Bundle args = new Bundle();
        args.putStringArrayList("hotcity", (ArrayList<String>) hotcity);
        args.putStringArrayList("city", (ArrayList<String>) citylist);
        final SerializableMap map = new SerializableMap();
        map.setMap(childcitylist);
        args.putSerializable("map", map);
        f.setArguments(args);
        return f;
    }

    public static class SerializableMap implements Serializable {

        private Map<String, List<String>> map;

        public Map<String, List<String>> getMap() {
            return map;
        }

        public void setMap(Map<String, List<String>> map) {
            this.map = map;
        }
    }

    public class HotCityAdapter extends BaseAdapter {
        LayoutInflater inflater;
        String cityid;

        HotCityAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return HotCity.size();
        }

        @Override
        public Object getItem(int position) {
            return HotCity.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_child, null);
            }
            Button btndown = convertView.findViewById(R.id.btn_down);
            TextView tvchild = convertView.findViewById(R.id.tv_childcity);
            TextView tvsize = convertView.findViewById(R.id.tv_size);
           String city = HotCity.get(position);
            Map<String, Object> map = new HashMap<String, Object>();
            map = getcity(city);
            tvchild.setText((String) map.get("cityname"));
            tvsize.setText((String) map.get("citysize"));
            cityid = (String) map.get("cityid");
            Log.d("cityidhot", (String) map.get("cityid"));
            return convertView;
        }
    }

    public class CityExAdapter extends BaseExpandableListAdapter {
        LayoutInflater inflater;
        String cityid;

        CityExAdapter(Context context) {
            inflater = LayoutInflater.from(context);

        }

        @Override
        public int getGroupCount() {
            return City.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            Log.d("CITY", City.get(groupPosition));
            return AllCity.get(City.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return City.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return AllCity.get(City.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_parent, null);
            }
            TextView tv = convertView
                    .findViewById(R.id.tv_city);
            tv.setText(City.get(groupPosition));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_child, null);
            }
            Button btndown = convertView.findViewById(R.id.btn_down);
            TextView tvchild = convertView.findViewById(R.id.tv_childcity);
            TextView tvsize = convertView.findViewById(R.id.tv_size);
            String city = AllCity.get(City.get(groupPosition)).get(childPosition);
            tvchild.setText((String) getcity(city).get("cityname"));
            tvsize.setText((String) getcity(city).get("citysize"));
            return convertView;
        }


        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
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
}

package com.fanweilin.coordinatemap.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.fanweilin.coordinatemap.Activity.Bdoffilin_activity;
import com.fanweilin.coordinatemap.Activity.MainMapsActivity;
import com.fanweilin.coordinatemap.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/19 0019.
 */
public class DownFragment extends Fragment {
    private ArrayList<MKOLUpdateElement> localMapList = null;
    private LocalMapAdapter lAdapter = null;
    Bdoffilin_activity bd;
        private TextView stateView;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frament_down, container, false);
        ListView localMapListView = view.findViewById(R.id.localmaplist);
        stateView= view.findViewById(R.id.state);
        lAdapter = new LocalMapAdapter(getActivity());
        localMapListView.setAdapter(lAdapter);
        return view;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bd=( Bdoffilin_activity)getActivity();
         localMapList=bd.localMapList;
    }

    public class LocalMapAdapter extends BaseAdapter {
        LayoutInflater inflater;

        LocalMapAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return localMapList.size();
        }

        @Override
        public Object getItem(int index) {
            return localMapList.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(int index, View view, ViewGroup arg2) {
            MKOLUpdateElement e = (MKOLUpdateElement) getItem(index);
            view = inflater.inflate(R.layout.offline_localmap_list, null);
            initViewItem(view, e);
            return view;
        }

        void initViewItem(View view, final MKOLUpdateElement e) {
            Button display = view.findViewById(R.id.display);
            Button remove = view.findViewById(R.id.remove);
            TextView title = view.findViewById(R.id.title);
            TextView update = view.findViewById(R.id.update);
            TextView ratio = view.findViewById(R.id.ratio);
            ratio.setText(e.ratio + "%");
            title.setText(e.cityName);
            if (e.update) {
                update.setText("可更新");
            } else {
                update.setText("最新");
            }
            if (e.ratio != 100) {
                display.setEnabled(false);
            } else {
                display.setEnabled(true);
            }
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    bd.mOffline.remove(e.cityID);
                    updateView();
                }
            });
            display.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("x", e.geoPt.longitude);
                    intent.putExtra("y", e.geoPt.latitude);
                    intent.setClass(getActivity(), MainMapsActivity.class);
                    startActivity(intent);
                }
            });
        }

    }
//
    public void updateView() {
        localMapList = bd.mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<MKOLUpdateElement>();
        }
        lAdapter.notifyDataSetChanged();
    }

}

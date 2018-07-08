package com.fanweilin.coordinatemap.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fanweilin.coordinatemap.Class.SpfOlMap;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.widget.MapAdapter;
import com.tencent.imsdk.ext.group.TIMGroupCacheInfo;
import com.tencent.qcloud.presentation.event.GroupEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;

import timchat.model.GroupInfo;
import timchat.model.GroupProfile;
import timchat.model.ProfileSummary;
import timchat.ui.CreateGroupActivity;
import timchat.ui.GroupListActivity;

public class OnlineMap extends AppCompatActivity implements Observer {
    Toolbar toolbar;
    FloatingActionButton fabMap;
    private List<ProfileSummary> list=new ArrayList<>();
    RecyclerView recyclerView;
    MapAdapter mapAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_map);
        getdata();
        initview();
        setSupportActionBar(toolbar);
        GroupEvent.getInstance().addObserver(this);
    }
    private void initview(){
        toolbar =  findViewById(R.id.toolbar);
        recyclerView=  findViewById(R.id.ry_map);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        mapAdapter=new MapAdapter(this,list);
        recyclerView.setAdapter(mapAdapter);
        mapAdapter.setOnItemClickListener(new MapAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = recyclerView.getChildAdapterPosition(view);
                ProfileSummary profileSummary=list.get(position);
                Intent intent=new Intent(OnlineMap.this,MainMapsActivity.class);
                intent.putExtra(MainMapsActivity.OnlineMap,MainMapsActivity.OnlineMap);
                SharedPreferences.Editor editor=data.spfOlMapSet.edit();
                editor.putString(SpfOlMap.MAPID,profileSummary.getIdentify());
                editor.putInt(SpfOlMap.MAPTYPE,2);
                editor.putString(SpfOlMap.MAPNAME,profileSummary.getName());
                editor.commit();
                MainMapsActivity.MAYOLTYPE=1;
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view) {

            }
        });

    }


public void getdata(){
    list = GroupInfo.getInstance().getGroupListByType(GroupInfo.publicGroup);
}
    private void addMap(){
        showGroups(GroupInfo.publicGroup);
    }
    private void showGroups(String type){
        Intent intent = new Intent(OnlineMap.this, CreateGroupActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof GroupEvent){
            if (data instanceof GroupEvent.NotifyCmd){
                GroupEvent.NotifyCmd cmd = (GroupEvent.NotifyCmd) data;
                switch (cmd.type){
                    case DEL:
                        delGroup((String) cmd.data);
                        break;
                    case ADD:
                        addGroup((TIMGroupCacheInfo) cmd.data);
                        break;
                    case UPDATE:
                        updateGroup((TIMGroupCacheInfo) cmd.data);
                        break;
                }
            }
        }

    }
    private void delGroup(String groupId){
        Iterator<ProfileSummary> it = list.iterator();
        while (it.hasNext()){
            ProfileSummary item = it.next();
            if (item.getIdentify().equals(groupId)){
                it.remove();
                mapAdapter.notifyDataSetChanged();
                return;
            }
        }
    }


    private void addGroup(TIMGroupCacheInfo info){
        if (info!=null && info.getGroupInfo().getGroupType().equals(GroupInfo.publicGroup)){
            GroupProfile profile = new GroupProfile(info);
            list.add(profile);
            mapAdapter.notifyDataSetChanged();
        }

    }

    private void updateGroup(TIMGroupCacheInfo info){
        delGroup(info.getGroupInfo().getGroupId());
        addGroup(info);
    }
}

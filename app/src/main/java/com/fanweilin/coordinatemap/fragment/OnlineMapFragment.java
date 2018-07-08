package com.fanweilin.coordinatemap.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fanweilin.coordinatemap.Activity.MainMapsActivity;
import com.fanweilin.coordinatemap.Activity.OnlineMap;
import com.fanweilin.coordinatemap.Activity.data;
import com.fanweilin.coordinatemap.Class.SpfOlMap;
import com.fanweilin.coordinatemap.Class.UserVip;
import com.fanweilin.coordinatemap.R;
import com.fanweilin.coordinatemap.widget.MapAdapter;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.group.TIMGroupCacheInfo;
import com.tencent.imsdk.ext.group.TIMGroupDetailInfo;
import com.tencent.imsdk.ext.group.TIMGroupManagerExt;
import com.tencent.qcloud.presentation.event.GroupEvent;
import com.tencent.qcloud.tlslibrary.service.TLSService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import timchat.model.GroupInfo;
import timchat.model.GroupProfile;
import timchat.model.ProfileSummary;
import timchat.ui.CreateGroupActivity;
import timchat.ui.SearchGroupActivity;

import static com.tencent.open.utils.Global.getSharedPreferences;

/**
 * Created by Administrator on 2018/3/13.
 */

public class OnlineMapFragment extends Fragment implements Observer {
    Toolbar toolbar;
    private List<ProfileSummary> list=new ArrayList<>();
    RecyclerView recyclerView;
    MapAdapter mapAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getdata();
        View view = inflater.inflate(R.layout.activity_online_map, container, false);
        toolbar =  view.findViewById(R.id.toolbar);
        toolbar.setTitle("在线地图");
        toolbar.inflateMenu(R.menu.menu_fragment_olinemap);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_new:
                        addMap();
                        break;
                    case R.id.item_jion:
                        Intent intent = new Intent(getActivity(), SearchGroupActivity.class);
                        getActivity().startActivity(intent);
                        break;

                }
                return true;
            }
        });

        GroupEvent.getInstance().addObserver(this);
        recyclerView=  view.findViewById(R.id.ry_map);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        mapAdapter=new MapAdapter(getActivity(),list);
        recyclerView.setAdapter(mapAdapter);
        mapAdapter.setOnItemClickListener(new MapAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = recyclerView.getChildAdapterPosition(view);
                ProfileSummary profileSummary=list.get(position);
                Intent intent=new Intent(getActivity(),MainMapsActivity.class);
                intent.putExtra(MainMapsActivity.OnlineMap,MainMapsActivity.OnlineMap);
                SharedPreferences.Editor editor= data.spfOlMapSet.edit();
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

        GroupEvent.getInstance().addObserver(this);
        return view;

    }
    public void getdata(){
        list = GroupInfo.getInstance().getGroupListByType(GroupInfo.publicGroup);
    }
    private void addMap(){
        final String myid = TLSService.getInstance().getLastUserIdentifier();
        List<String> groupList=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            groupList.add(list.get(i).getIdentify());
        }
        TIMValueCallBack<List<TIMGroupDetailInfo>> cb= new TIMValueCallBack<List<TIMGroupDetailInfo>>(){

            @Override
            public void onError(int i, String s) {
                 showGroups(GroupInfo.publicGroup);
            }

            @Override
            public void onSuccess(List<TIMGroupDetailInfo> infoList) {
                int mapnum=0;
                SharedPreferences spf = data.get().getSharedPreferences(UserVip.SPFNAME, Context.MODE_PRIVATE);
                int  vip = spf.getInt(UserVip.SPFVIP, 1);
                for(TIMGroupDetailInfo info : infoList){
                    if(myid.equals(info.getGroupOwner())){
                        mapnum ++;
                    }
                }
                if(mapnum>=1){
                    if(vip>=8){
                        if(mapnum>=5){
                            Toast.makeText(getActivity(),"当前用户仅允许创建5副地图",Toast.LENGTH_SHORT).show();
                        }else {
                            showGroups(GroupInfo.publicGroup);
                        }

                    }

                }else {
                    showGroups(GroupInfo.publicGroup);
                }
            }
        };
        TIMGroupManagerExt.getInstance().getGroupDetailInfo(
                groupList, //需要获取信息的群组Id列表
                cb);

    }
    private void showGroups(String type){
        Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
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

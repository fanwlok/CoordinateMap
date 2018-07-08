package com.fanweilin.coordinatemap.widget;

/**
 * Created by Administrator on 2018/2/22.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanweilin.coordinatemap.R;
import com.ruffian.library.RTextView;

import java.util.List;

import timchat.model.ProfileSummary;

/**
 * Created by Administrator on 2017/12/24.
 */

public  class MapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener{
    private Context mContext;
    private List<ProfileSummary> list;
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view);
        void onItemLongClick(View view);
    }
    private MapAdapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public void setOnItemClickListener(MapAdapter.OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }
    public MapAdapter(Context context ,List<ProfileSummary> list){
        this.mContext=context;
        this.list=list;
    }
    @Override
    public int getItemViewType(int position) {
        return 0;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext
        ).inflate(R.layout.list_onlinemap, parent,
                false);//这个布局就是一个imageview用来显示地圖；
      MapAdapter.MyViewHolder holder = new MapAdapter.MyViewHolder(view);

        //给布局设置点击和长点击监听
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return holder;

    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView mapName;
        private TextView mapID;
        private TextView mapShare;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            mapName = itemView.findViewById(R.id.map_name);
            mapID = itemView.findViewById(R.id.tv_id);
            mapShare=itemView.findViewById(R.id.tv_share);

        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final String ID=list.get(position).getIdentify();
        ((MyViewHolder)holder).tv_name.setText(getlastText(list.get(position).getName()));
        ((MapAdapter.MyViewHolder)holder).mapName.setText(list.get(position).getName());
        ((MapAdapter.MyViewHolder)holder).mapID.setText(list.get(position).getIdentify());
        ((MyViewHolder)holder). mapShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  sendtext(ID);
            }
        });
    }

    public void sendtext(String str){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        if(str!=null){
            intent.putExtra(Intent.EXTRA_TEXT,str);
        }else{
            intent.putExtra(Intent.EXTRA_TEXT,"");
        }
        intent.setType("text/plain");//设置分享发送的数据类型
        //未指定选择器，部分定制系统首次选择后，后期将无法再次改变
// startActivity(intent);
        //指定选择器选择使用有发送文本功能的App

        mContext.startActivity(Intent.createChooser(intent,mContext.getResources().getText(R.string.app_name)));
    }

   private String getlastText(String s){
        return s.substring(s.length()-1,s.length());
   }
    @Override
    public int getItemCount()
    {
        return list.size();
    }
    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
        return false;
    }
}
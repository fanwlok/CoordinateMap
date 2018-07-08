package com.fanweilin.coordinatemap.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanweilin.coordinatemap.R;

import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2018/6/21.
 */

public class  MyAdpter extends RecyclerView.Adapter<ViewHolder>implements View.OnClickListener, View.OnLongClickListener{
    private Context mContext;
    private List<Map<String,Object>> list;
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view);
        void onItemLongClick(View view);
    }
    public MyAdpter (Context context ,List<Map<String,Object>> list){
        this.mContext=context;
        this.list=list;
    }
    private MapAdapter.OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public void setOnItemClickListener(MapAdapter.OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }
    class MyViewHolder extends ViewHolder {
        private ImageView imgIcon;
        private TextView tvname;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvname= itemView.findViewById(R.id.tv_name);
            imgIcon = itemView.findViewById(R.id.img_icon);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext
        ).inflate(R.layout.recycel_item_map, parent,
                false);//这个布局就是一个imageview用来显示地圖；
        MyAdpter.MyViewHolder holder = new MyAdpter .MyViewHolder(view);

        //给布局设置点击和长点击监听
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ((MyViewHolder)holder).imgIcon.setImageResource((Integer) list.get(position).get("imgid"));
        ((MyViewHolder)holder).tvname.setText((String) list.get(position).get("name"));
    }

    @Override
    public int getItemCount() {
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
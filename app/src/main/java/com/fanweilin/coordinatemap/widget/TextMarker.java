package com.fanweilin.coordinatemap.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanweilin.coordinatemap.R;

/**
 * Created by Administrator on 2018/4/22.
 */

public class TextMarker extends RelativeLayout {
    private TextView mTvname;
    private ImageView imageView;
    public TextMarker(Context context) {
        super(context);
        init();
    }
    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.text_marker, null);
        imageView=view.findViewById(R.id.img_marker);
        mTvname=view.findViewById(R.id.tv_name);
        addView(view);
    }
    public void setText(String name){
        mTvname.setText(name);
    }
    public void setImge(int Re){
        imageView.setImageResource(Re);
    }
}

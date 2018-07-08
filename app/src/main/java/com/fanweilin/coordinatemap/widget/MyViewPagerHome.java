package com.fanweilin.coordinatemap.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/9/16 0016.
 */
public class MyViewPagerHome extends ViewPager {

    public MyViewPagerHome(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public MyViewPagerHome(Context context) {
        super(context);

    }


    @Override
    public boolean onTouchEvent(MotionEvent arg0) {

        return false;

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {

        return false;

    }
}
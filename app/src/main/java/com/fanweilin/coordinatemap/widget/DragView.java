package com.fanweilin.coordinatemap.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017/6/4.
 */

public class DragView extends RelativeLayout {
    private int lastX;
    private int lastY;
    int offX;
    int offY;
    int movex=0;
    int movey=0;
    private DragViewListener dragViewListener;
    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        return true;
    }
    public boolean onTouchEvent(MotionEvent event) {
        //获取到手指处的横坐标和纵坐标
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                dragViewListener.onDoun(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                //计算移动的距离
                offX = x - lastX;
                offY = y - lastY;
                ViewCompat.setTranslationX(this,movex+offX);
                ViewCompat.setTranslationY(this,movey+offY);
                dragViewListener.onDrag(x,y);
                break;
            case MotionEvent.ACTION_UP:
                movex+=offX;
                movey+=offY;
                dragViewListener.onUp(x,y);
                break;

        }
        return true;
    }

    public void  setpositionByPx(int x,int y){
        int []xy=new int[2];
        getLocationInWindow(xy);
        movex+=x-xy[0]-getWidth()/2;
        movey+=y-xy[1];
        ViewCompat.setTranslationX(this,movex);
        ViewCompat.setTranslationY(this,movey);
    }

    public void setDragListener(DragViewListener dragViewListener) {
        this.dragViewListener= dragViewListener;
    }
    public interface DragViewListener {
        void onDoun(int RawX, int RawY);
        void onDrag(int RawX, int RawY);
        void onUp(int RawX, int RawY);
    }
}

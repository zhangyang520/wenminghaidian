package com.zhjy.hdcivilization.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2016/8/1.
 */
public class NoScrollView extends ScrollView{

    private float lastX;

    private float lastY;

    private OnOverScolledListener onOverScolledListener;

    public NoScrollView(Context context) {
        super(context);
    }

    public NoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnOverScolledListener(OnOverScolledListener onOverScolledListener){
        this.onOverScolledListener = onOverScolledListener;
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {

        return false;
    }

    public interface OnOverScolledListener{
        void onOverl();
    }


//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        boolean result = true;
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                lastX = ev.getX();
//                lastY = ev.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int distanceX =(int) Math.abs( ev.getX() - lastX);
//                int distanceY = (int) Math.abs(ev.getY()-lastY);
//
//                if(distanceX>distanceY && distanceX>10){
//                    result = false;
//                }else{
//                    result = true;
//                }
//                break;
//            default:
//                break;
//        }
//        return result;
//    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
       return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
//        System.out.println("scrollY :"+scrollY+"...clampedY:"+clampedY);
        if(scrollY > 0 && clampedY){
            if(onOverScolledListener != null){
                onOverScolledListener.onOverl();
            }
        }

    }
}

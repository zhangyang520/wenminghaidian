package com.zhjy.hdcivilization.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @项目名：AroundYou
 * @类名称：AutoStateViewPager
 * @类描述： 可以同时解决ListView和ViewPager以及ViewPager和SlidingMenu同时冲突的焦点获取问题
 * @创建人：HXF
 * @修改人：
 * @创建时间：2015-6-24 上午10:04:01
 */
public class MainViewPager extends ViewPager {

    private int abc = 1;
    private float mLastMotionX;

    private int totalSize;
    public MainViewPager(Context context) {
        super(context);
    }

    public MainViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                abc = 1;
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
            /* System.out.println("为点击的那个点mLastMotionX=" + mLastMotionX); */
                if (abc == 1) {
                    if (x - mLastMotionX > 5 && totalSize>0 && (getCurrentItem()%totalSize) == 0) {
                        abc = 0;
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }else if(x - mLastMotionX > 5 && (getCurrentItem()) == 0){
                        abc = 0;
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return super.onTouchEvent(arg0);
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }
}

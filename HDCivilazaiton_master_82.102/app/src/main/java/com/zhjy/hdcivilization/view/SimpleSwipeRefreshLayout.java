package com.zhjy.hdcivilization.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ScrollView;


/**
 * @author :huangxianfeng on 2016/6/3.
 */
public class SimpleSwipeRefreshLayout extends SwipeRefreshLayout {

    private View view;
    private int refreshState=SWIPE_REFRESH_FINISHED;
    public static final int SWIPE_REFRESHINT=1;//正在刷新中....
    public static final int SWIPE_REFRESH_FINISHED=2;//正在刷新结束....

    public SimpleSwipeRefreshLayout(Context context) {
        super(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        System.out.println("SimpleSwipeRefreshLayout mTouchSlop: "+mTouchSlop);
    }


    public void setChild(View view) {
        this.view = view;
    }

    @Override
    public boolean canChildScrollUp() {
        boolean flag;

        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else if(view instanceof ScrollView){
                if(view != null && view.getScrollY()>0){
                    flag= true;
                }else{
                    flag= false;
                }
                return flag;
            }else{
                return ViewCompat.canScrollVertically(view, -1) || view.getScrollY() > 0;
            }
        } else {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else if(view instanceof ScrollView){
                if(view != null && view.getScrollY()>0){
                    flag= true;
                }else{
                    flag= false;
                }
                return flag;
            }else{
                System.out.println("canChildScrollUp ..ViewCompat.canScrollVertically:"+ViewCompat.canScrollVertically(view, -1));
                return ViewCompat.canScrollVertically(view, -1);
            }
        }
//        if (view != null && view instanceof AbsListView) {
//            final AbsListView absListView = (AbsListView) view;
//            flag= absListView.getChildCount() > 0
//                    && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
//                    .getTop() < absListView.getPaddingTop());
//        }else if(view != null && view instanceof RecyclerView){
//            return ViewCompat.canScrollVertically(view, -1);
//        }else{
//            if(view != null && view.getScrollY()>0){
//                flag= true;
//            }else{
//                flag= false;
//            }
//        }
//        return flag;
    }


    // 记录viewPager是否拖拽的标记
    private boolean mIsVpDragger;
    int mTouchSlop;
    public SimpleSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        System.out.println("SimpleSwipeRefreshLayout mTouchSlop: "+mTouchSlop);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("SimpleSwipeRefreshLayout onInterceptTouchEvent ACTION_DOWN");
                // 记录手指按下的位置
                startY = ev.getY();
                startX = ev.getX();
                // 初始化标记
                mIsVpDragger = false;
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("SimpleSwipeRefreshLayout onInterceptTouchEvent ACTION_MOVE");
                // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
                if(mIsVpDragger) {
                    return false;
                }

                // 获取当前手指位置
                float endY = ev.getY();
                float endX = ev.getX();
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);
                System.out.println("distanceX > mTouchSlop:"+(distanceX > mTouchSlop)+"....distanceX > distanceY:"+(distanceX > distanceY));
                System.out.println("mTouchSlop:"+mTouchSlop+"....distanceX:"+distanceX+"...distanceY:"+distanceY);
                // 如果X轴位移大于Y轴位移，那么将事件交给viewPager处理。 distanceX > mTouchSlop && distanceX > distanceY
                if( distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsVpDragger = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                System.out.println("SimpleSwipeRefreshLayout onInterceptTouchEvent ACTION_UP or ACTION_CANCEL");
                // 初始化标记
                mIsVpDragger = false;
                break;
        }
        System.out.println("SimpleSwipeRefreshLayout onInterceptTouchEvent parent");
        // 如果是Y轴位移大于X轴，事件交给swipeRefreshLayout处理。
        return super.onInterceptTouchEvent(ev);
    }

    float startX;
    float startY;
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        boolean flag=false;
//        switch(ev.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                startX=ev.getRawX();
//                startY=ev.getRawY();
//                System.out.println("SimpleSwipeRefreshLayout onTouchEvent ACTION_DOWN:(x,y)"+"("+startX+","+startY+")");
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                if(Math.abs(ev.getRawX()-startX)!=0){
//                    float k=Math.abs(ev.getRawY()-startY)/Math.abs(ev.getRawX()-startX);
//                    if(k>=1){
//                        flag=super.onTouchEvent(ev);
//                    }
//                    System.out.println("SimpleSwipeRefreshLayout ACTION_MOVE k:"+k+"...(x,y)"+"("+ev.getRawX()+","+ev.getRawY()+")");
//                }
//                break;
//
//            case MotionEvent.ACTION_UP:
//                flag=super.onTouchEvent(ev);
//                break;
//        }
//        return flag;
//    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
//        System.out.println("SimpleSwipeRefreshLayout onNestedScrollAccepted .......");
        super.onNestedScrollAccepted(child, target, axes);
    }

    public int getRefreshState() {
        return refreshState;
    }

    public void setRefreshState(int refreshState) {
        this.refreshState = refreshState;
    }
}

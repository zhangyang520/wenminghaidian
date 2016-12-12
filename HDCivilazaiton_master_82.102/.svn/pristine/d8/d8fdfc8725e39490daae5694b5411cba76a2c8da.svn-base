package com.zhjy.hdcivilization.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.holder.NoticePagerHolder;


/**
 * 思路:
 *     先进行初始化viewPager header
 *     关键是进行复写onInterecptTounchEvent方法:是否拦截竖直方向的滑动事件,
 *           如果拦截进行交给onTounchEvent滑动处理：进行具体的处理ViewPager的伸缩问题！
 *
 *      至于onInterceptTounchEvent方法中什么是判断拦截的依据:
 *          (1)滑动的区域必须在headview的范围之外,y<currentHeight
 *          (2):必须是竖直方向
 *          (3):已经展开，dy<-tounchSlop
 *          (4):recycleView的首个可视的top位置为0,dy>tounchSlop
 *
 *
 * @author :zhangyang on 2016/6/3.
 */
public class SticklayoutSwipeRefreshLayout extends SwipeRefreshLayout {

    private View view;
    private LinearLayout headView;//包含viewPager的头部
    private LinearLayoutManager linearLayoutManager;
    private int initHeadHeight;//初始的frameLayout的高度
    private int isIntercepted=0;//表示是否拦截:0 表示不拦截,1 表示拦截
    private int STATUS_EXPANED=1;//代表已经展开装态
    private int STATUS_COLLAPSE=2;//代表已经合并状态
    private int status=STATUS_EXPANED;
    private LinearLayout viewPagerTop;
    private FrameLayout viewPager;

    private NoticePagerHolder noticePagerHolder;
    private boolean isStickLayoutEnable=true;
    public SticklayoutSwipeRefreshLayout(Context context) {
        super(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        //+ "..headView height:" + headView.getMeasuredHeight()
        System.out.println("SticklayoutSwipeRefreshLayout mTouchSlop: " + mTouchSlop);
    }

    public NoticePagerHolder getNoticePagerHolder() {
        return noticePagerHolder;
    }

    public void setNoticePagerHolder(NoticePagerHolder noticePagerHolder) {
        this.noticePagerHolder = noticePagerHolder;
    }

    // 记录viewPager是否拖拽的标记
    private boolean mIsVpDragger;
    int mTouchSlop;
    public SticklayoutSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        headView=(LinearLayout)findViewById(R.id.frame_layout);
        //+"..headView height:"+(headView==null)
        System.out.println("SticklayoutSwipeRefreshLayout mTouchSlop: "+mTouchSlop);
    }

    public void setChild(View view) {
        this.view = view;
    }

    public LinearLayout getHeadView() {
        return headView;
    }

    public void setHeadView(LinearLayout headView) {
        this.headView = headView;
        initHeadHeight=headView.getMeasuredHeight();
        System.out.println("SticklayoutSwipeRefreshLayout setHeadView  height: " + headView.getMeasuredHeight());
    }

    public LinearLayoutManager getLinearLayoutManager() {
        return linearLayoutManager;
    }

    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    public LinearLayout getViewPagerTop() {
        return viewPagerTop;
    }

    public void setViewPagerTop(LinearLayout viewPagerTop) {
        this.viewPagerTop = viewPagerTop;
        this.viewPager=(FrameLayout)this.viewPagerTop.findViewById(R.id.rl_adjust_screen);
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
                return ViewCompat.canScrollVertically(view, -1);
            }
        }
    }

    /**
     *  至于onInterceptTounchEvent方法中什么是判断拦截的依据:
     *          (1)滑动的区域必须在headview的范围之外,y<currentHeight
     *          (2):必须是竖直方向
     *          (3):已经展开，dy<-tounchSlop
     *          (4):recycleView的首个可视的top位置为0,dy>tounchSlop
     */
    float startX;
    float startY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isStickLayoutEnable){
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // 记录手指按下的位置
                    startY = ev.getY();
                    startX = ev.getX();
                    // 初始化标记
                    mIsVpDragger = false;
                    isIntercepted=0;
                    System.out.println("SimpleSwipeRefreshLayout onInterceptTouchEvent ACTION_DOWN startY:"+startY);
                    break;
                case MotionEvent.ACTION_MOVE:

                    // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
                    if(mIsVpDragger) {
                        return false;
                    }
                    // 获取当前手指位置
                    float endY = ev.getY();
                    float endX = ev.getX();
                    float distanceX =endX - startX;
                    float distanceY = endY - startY;
                    System.out.println("SimpleSwipeRefreshLayout onInterceptTouchEvent ACTION_MOVE endY:"+endY);
                    //判断如果是横向滑动
                    System.out.println("onInterceptTouchEvent distanceY:"+distanceY+"..mTouchSlop:"+mTouchSlop+"..startY:"+startY);
                    //distanceX > mTouchSlop &&
                    if( Math.abs(distanceX) > Math.abs(distanceY)) {
                        mIsVpDragger = true;
                        return false;
                    }

                    System.out.println("onInterceptTouchEvent status==STATUS_EXPANED:"+(status==STATUS_EXPANED)+"...distanceY<-mTouchSlop:"+//
                            (distanceY<-mTouchSlop)+"...isRecycleViewFirstTop:"+isRecycleViewFirstTop()+"..distanceY>mTouchSlop:"+(distanceY>mTouchSlop)+"..getCurrentHeadHeight()<=initHeadHeight:"+(getCurrentHeadHeight()<=initHeadHeight));
                    if(endY<getCurrentHeadHeight()){
                        System.out.println("-------------------1");
                        //如果小于headview的高度
                        isIntercepted=0;
                    }else if(Math.abs(distanceX)> Math.abs(distanceY)){
                        System.out.println("-------------------2");
                        isIntercepted=0;
                    }else if(status==STATUS_EXPANED && distanceY<0){
                        //如果已经展开,且是向上滑动
                        System.out.println("-------------------3");
                        isIntercepted=1;
                    }else if(isRecycleViewFirstTop() && distanceY>=0 && getCurrentHeadHeight()<initHeadHeight){
                        System.out.println("-------------------4 ");
                        isIntercepted=1;
                    }else{
                        System.out.println("-------------------5");
                        isIntercepted=0;
                    }

                    //
                    startX=endX;
                    startY=endY;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if(noticePagerHolder!=null){
                        noticePagerHolder.start();
                    }
                    System.out.println("SimpleSwipeRefreshLayout onInterceptTouchEvent ACTION_UP or ACTION_CANCEL");
                    // 初始化标记
                    mIsVpDragger = false;
                    isIntercepted=0;
                    break;
            }
            System.out.println("SimpleSwipeRefreshLayout onInterceptTouchEvent isIntercepted:"+isIntercepted);
            if(isIntercepted==1){
                return true;
            }else{
                // 如果是Y轴位移大于X轴，事件交给swipeRefreshLayout处理。
                return super.onInterceptTouchEvent(ev);
            }
        }else{
            //不进行拦截
            return super.onInterceptTouchEvent(ev);
        }
    }

    /**
     *是否recycleView的第一个孩子的top值为0
     * @return
     */
    private boolean isRecycleViewFirstTop() {
        if(linearLayoutManager.findFirstVisibleItemPosition()==0){
            if(((LoadMoreRecyclerView) view).getChildAt(0).getTop()>=0){
                return  true;
            }
        }
        return false;
    }

    private float getCurrentHeadHeight() {
        int currentHeight=headView.getLayoutParams().height;
        return currentHeight;
    }

    /**
     * 进行复写onTounchEvent函数
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isIntercepted==1) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX=ev.getX();
                    startY=getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float dx=ev.getX()-startX;
                    float dy=ev.getY()-startY;
                    float currentHeight=getCurrentHeadHeight()+dy;
                    setHeadViewHeight(currentHeight);
                    startX=ev.getX();
                    startY=ev.getY();
                    break;

                case MotionEvent.ACTION_UP:
                    if(noticePagerHolder!=null){
                        noticePagerHolder.start();
                    }
                    int destHeight=0;
                    if(getCurrentHeadHeight()<=0.5*initHeadHeight && getCurrentHeadHeight()>0){
                        //如果释放时的currentHeight<0.5*initHeadHeight
                        status=STATUS_COLLAPSE;//状态重新设置
                        destHeight=0;
                    }else if(getCurrentHeadHeight()>0.5*initHeadHeight && getCurrentHeadHeight()<=initHeadHeight){
                        status=STATUS_EXPANED;
                        destHeight=initHeadHeight;
                    }
                    int startHeight=(int)getCurrentHeadHeight();
                    ValueAnimator valueAnimator=ValueAnimator.ofFloat(startHeight, destHeight);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            //对headView的重新设定
                            float value =(float)animation.getAnimatedValue();
                            headView.getLayoutParams().height=(int)value;
                            headView.requestLayout();
                            if(viewPager!=null){
                                viewPager.getLayoutParams().height=(int)value;
                                viewPager.requestLayout();
                            }
                        }
                    });
                    valueAnimator.setDuration(500);
                    valueAnimator.start();
                    break;
            }
            return true;
        }else{
            return super.onTouchEvent(ev);
        }
    }

    /**
     * 进行设置高度
     * 关键对高度的范围进行限制
     * @param currentHeight
     */
    private void setHeadViewHeight(float currentHeight) {

        //对高度的范围进行限定
        if(currentHeight<=0){
            currentHeight=0;
        }else if(currentHeight>initHeadHeight){
            currentHeight=initHeadHeight;
        }

        //状态进行设置
        if(currentHeight==0){
            status=STATUS_COLLAPSE;
        }else{
            status=STATUS_EXPANED;
        }

        headView.getLayoutParams().height=(int)currentHeight;
        headView.requestLayout();

        if(viewPager!=null){
            viewPager.getLayoutParams().height=(int)currentHeight;
            viewPager.requestLayout();
        }
    }

    public boolean isStickLayoutEnable() {
        return isStickLayoutEnable;
    }

    public void setIsStickLayoutEnable(boolean isStickLayoutEnable) {
        this.isStickLayoutEnable = isStickLayoutEnable;
    }
}

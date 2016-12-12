package com.zhjy.hdcivilization.holder;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewPropertyAnimator;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.activity.CiviStateDetailActivity;
import com.zhjy.hdcivilization.activity.NoticeDetailActivity;
import com.zhjy.hdcivilization.entity.ActivityType;
import com.zhjy.hdcivilization.entity.HDC_Notice;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.inner.BaseHolder;
import com.zhjy.hdcivilization.utils.BitmapUtil;
import com.zhjy.hdcivilization.utils.ScreenUtil;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.MainViewPager;

import java.util.LinkedList;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/7/25.
 * 轮播图Holder定义
 */
public class NoticePagerHolder extends BaseHolder<HDC_Notice> {

    private FrameLayout rlPager;

    public ViewPager mainViewPager;

    private View viewBluePoint;

    private LinearLayout llGroup;

    private TextView pic_title;

    private RelativeLayout rl;

    private int mPointWidth;

    private SwitchRunnable switchRunnable=new SwitchRunnable();

    private MainViewPagerAdapter mainViewPagerAdapter;

    private Activity activity;

    private String activityType;

    private int currentPosition;

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View initView() {
        View pagerHeader = View.inflate(UiUtils.getInstance().getContext(), R.layout.main_pager_list_carousel,null);
        rlPager = (FrameLayout)pagerHeader.findViewById(R.id.rl_adjust_screen);
        rlPager.getLayoutParams().height = ScreenUtil.getInstance().getHeight(UiUtils.getInstance().getContext(),360,240);

        //要使用的ViewPager
        mainViewPager = (ViewPager)pagerHeader.findViewById(R.id.main_view_pager);
        viewBluePoint = pagerHeader.findViewById(R.id.view_new_point);// 默认小圆点放置的位置
        llGroup = (LinearLayout) pagerHeader.findViewById(R.id.ll_point_group);
        pic_title = (TextView) pagerHeader.findViewById(R.id.pic_title);
        rl = (RelativeLayout) pagerHeader.findViewById(R.id.rl);

        //事件的监听
        initEvent();

        System.out.println("NoticePagerHolder initView start");

        return pagerHeader;
    }

    private void initEvent() {
        //对ViewPager设置监听事件
        mainViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (getDatas() != null && getDatas().size() > 0) {
                    currentPosition = position % getDatas().size();
                    if (currentPosition != (getDatas().size() - 1)) {
                        int len = (int) ((mPointWidth * positionOffset) + currentPosition
                                * mPointWidth);
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewBluePoint.getLayoutParams();
                        params.leftMargin = len;
                        viewBluePoint.setLayoutParams(params);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (getDatas() != null && getDatas().size() > 0) {
                    //手动滑动的时候，进行设置切换
//                    mainViewPager.setCurrentItem(position);
                    currentPosition = position % getDatas().size();
                    pic_title.setText(getDatas().get(currentPosition).getTitle());
                    if (currentPosition == (getDatas().size() - 1)) {
                        int len = currentPosition * mPointWidth;
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewBluePoint.getLayoutParams();
                        params.leftMargin = len;
                        viewBluePoint.setLayoutParams(params);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        //设置ViewPager的触摸事件
        mainViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        System.out.println("SwitchRunnable ACTION_DOWN");
                        stop();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        System.out.println("SwitchRunnable ACTION_MOVE");
                        break;
                    case MotionEvent.ACTION_UP:
                        System.out.println("SwitchRunnable ACTION_UP");
                        start();
                        break;
                }
                return false;
            }
        });

    }

    //进行轮播启动
    public void start() {
        synchronized (NoticePagerHolder.class){
            if(switchRunnable!=null){
                switchRunnable.start();
            }
        }
    }

    //进行轮播暂停
    public void stop() {
        synchronized (NoticePagerHolder.class){
            if(switchRunnable!=null){
                switchRunnable.stop();
            }
        }
    }

    @Override
    public void refreshView(HDC_Notice hdc_carousel) {

    }

    @Override
    public void setDatas(List<HDC_Notice> hdc_carousels) {
        super.setDatas(hdc_carousels);
        //设置ViewPager上面的小圆点
        if (getDatas().size() >0){
            mainViewPager.setVisibility(View.VISIBLE);
            rl.setVisibility(View.VISIBLE);
            initDotLayout();
            //对ViewPager设置适配器
            if(mainViewPagerAdapter==null){
                pic_title.setText(getDatas().get(0).getTitle());
                mainViewPagerAdapter = new MainViewPagerAdapter();
//                mainViewPager.setTotalSize(getDatas().size());
                mainViewPager.setAdapter(mainViewPagerAdapter);
                mainViewPager.setCurrentItem(0);
            }
        }else{
            mainViewPager.setVisibility(View.GONE);
            rl.setVisibility(View.GONE);
        }
    }

    //隐藏ViewPager
    public void hiddenViewPager(){
        mainViewPager.setVisibility(View.GONE);
        rl.setVisibility(View.GONE);
        stop();
    }

    //ViewPager小圆点 及ViewPager的适配器
    private void initDotLayout() {
        llGroup.removeAllViews();
        for (int i = 0; i < getDatas().size(); i++) {
            View point = new View(UiUtils.getInstance().getContext());
            point.setBackgroundResource(R.drawable.icon_dot_empty);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ScreenUtil.getInstance().dp2Px(UiUtils.getInstance().getContext(), 6),
                    ScreenUtil.getInstance().dp2Px(UiUtils.getInstance().getContext(), 6));
            if (i > 0) {
                params.leftMargin = ScreenUtil.getInstance().dp2Px(UiUtils.getInstance().getContext(), 4);
            }
            point.setLayoutParams(params);
            llGroup.addView(point);
        }
        if (getDatas().size() !=1){
            llGroup.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @SuppressWarnings("deprecation")
                        @Override
                        public void onGlobalLayout() {
                            llGroup.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            mPointWidth = llGroup.getChildAt(1).getLeft() - llGroup.getChildAt(0).getLeft();
                        }
                    });
        }
    }


    class MainViewPagerAdapter extends PagerAdapter {
        //定义imageView集合
        private LinkedList<ImageView> imageViews= new LinkedList<ImageView>();

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //将此对象存储到集合中
            imageViews.add((ImageView)object);
            container.removeView((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // 对ViewPager页号求模取出View列表中要显示的项
            currentPosition=position%getDatas().size();
            ImageView imageView = null;
            //j进行判断集合数据是否为空
            if (imageViews.size()==0){
                //如果为零
                imageView = new ImageView(UiUtils.getInstance().getContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }else{
                imageView=imageViews.remove(0);
            }
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            System.out.println("SwitchRunnable ACTION_DOWN");
                            stop();
                            break;
                    }
                    return false;
                }
            });
            imageView.setOnClickListener(new MyViewOnClickListener(currentPosition) {
                @Override
                public void onClick(View v) {
                    if (activityType.equals(ActivityType.ACTIVITYNOTICE.getType())) {
                        Intent intent = new Intent(activity, NoticeDetailActivity.class);
                        intent.putExtra(NoticeDetailActivity.ITEM_ID_AND_TYPE, getDatas().get(this.position).getItemId());
                        intent.putExtra(NoticeDetailActivity.IS_FROM_LUNBO, true);
                        activity.startActivity(intent);
                    }
                }
            });
            if (getDatas().get(currentPosition).getImgEntity()!=null){
                BitmapUtil.getInstance().displayImg(imageView, UrlParamsEntity.WUCHEN_XU_IP_FILE + getDatas().get(currentPosition).getImgEntity().getImgUrl());
            }else{
                BitmapUtil.getInstance().displayImg(imageView, UrlParamsEntity.WUCHEN_XU_IP_FILE + "");
            }
            container.addView(imageView);
            return imageView;
        }
    }

    private boolean flag=false;
    /**
     * @author :huangxianfeng on 2016/7/25.
     * 定义runnable接口
     */
    class SwitchRunnable implements Runnable{

        @Override
        public void run() {
            synchronized (NoticePagerHolder.class){
                if (flag){
                    System.out.println("SwitchRunnable run");
                    //先进行删除当前的任务
                    UiUtils.getInstance().getHandler().removeCallbacks(this);
                    int currentItem = mainViewPager.getCurrentItem();
                    currentItem++;
                    mainViewPager.setCurrentItem(currentItem);
                    System.out.println("MainPagerHolder...currentItem = " + currentItem);
                    UiUtils.getInstance().getHandler().postDelayed(this, 4000);
                }
            }
        }


        //进行启动
        public void start(){
            if(!flag){
                System.out.println("SwitchRunnable start");
                UiUtils.getInstance().getHandler().removeCallbacks(this);
                UiUtils.getInstance().getHandler().postDelayed(this,4000);
                flag=true;
            }
        }

        //进行停止
        public void stop(){
            if (flag){
                System.out.println("SwitchRunnable stop");
                UiUtils.getInstance().getHandler().removeCallbacks(this);
                flag=false;
            }
        }
    }

    abstract  class MyViewOnClickListener implements View.OnClickListener{
        int position;
        public MyViewOnClickListener(int position){
            this.position=position;
        }

    }
}

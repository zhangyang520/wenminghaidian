package com.zhjy.hdcivilization.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.CommentDao;
import com.zhjy.hdcivilization.entity.HDC_CommentDetail;
import com.zhjy.hdcivilization.entity.ImgEntity;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.utils.BitmapUtil;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.MainViewPager;
import com.zhjy.hdcivilization.view.MaskView;

import java.util.LinkedList;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/9/13.
 */
public class CommentImgListActivity extends BaseActivity {

    private MainViewPager viewPager;
    public static String ITEM_ID_AND_TYPE = "ITEM_ID_AND_TYPE";//条目的id和类型
    public static String ITEM_ID = "item_id";

    private TextView textView1;
    private TextView textView2;
    private String itemId;
    //    private String itemIdAndType;
    private HDC_CommentDetail commentDetail;
    List<ImgEntity> urlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contentView= UiUtils.getInstance().inflate(R.layout.activity_comment_img_list);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        int leftWidth =  UiUtils.getInstance().getDefaultWidth();
        itemId = getIntent().getStringExtra(ITEM_ID);//
//        itemIdAndType=getIntent().getStringExtra(ITEM_ID_AND_TYPE);//
        commentDetail = CommentDao.getInstance().getHDC_CommentDetailLists(itemId);
        textView1 = (TextView)findViewById(R.id.text_view1);
        textView2 = (TextView)findViewById(R.id.text_view2);
        urlList = commentDetail.getImgUrlList();
        textView2.setText(urlList.size()+"");
        System.out.println("urlList =" + urlList.toString());
        viewPager = (MainViewPager) findViewById(R.id.comment_img_viewpager);
        viewPager.getLayoutParams().height= (int) Math.round(leftWidth * (double) 0.7);
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position % urlList.size());
                textView1.setText(position % urlList.size()+1+"");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initInitevnts() {

    }

    ImgEntity currentData;

    class MyPagerAdapter extends PagerAdapter {
        private int currentPosition;

        private LinkedList<ImageView> imageViews = new LinkedList<ImageView>();

        @Override
        public int getCount() {
            return urlList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            imageViews.add((ImageView) object);
            container.removeView((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            // 对ViewPager页号求模取出View列表中要显示的项
            currentPosition = ((position) % urlList.size());
            currentData = urlList.get(currentPosition);
            ImageView imageView = null;
            //j进行判断集合数据是否为空
            if (imageViews.size() == 0) {
                //如果为零
                imageView = new ImageView(UiUtils.getInstance().getContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.requestLayout();
            } else {
                imageView = imageViews.remove(0);
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommentImgListActivity.this.finish();
                }
            });
            if (urlList.get(currentPosition) != null) {
                BitmapUtil.getInstance().displayImg(imageView, UrlParamsEntity.WUCHEN_XU_IP_FILE + urlList.get(currentPosition).getImgUrl());
            } else {
                BitmapUtil.getInstance().displayImg(imageView, UrlParamsEntity.WUCHEN_XU_IP_FILE + "");
            }
            container.addView(imageView);
            return imageView;
        }
    }

}

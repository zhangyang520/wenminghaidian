package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nineoldandroids.view.ViewPropertyAnimator;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.fragment.CiviCommentHotFragment;
import com.zhjy.hdcivilization.fragment.CiviCommentMineFragment;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.manager.FragmentManger;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.ToolUtils;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.OKPopup;

import java.nio.InvalidMarkException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/7/27.
 *           文明评论
 */
public class CiviCommentActivity extends BaseActivity implements View.OnClickListener {

    private ImageView btnBack;
    private RelativeLayout hot, mine,rl_back,rl_submit_gambit;
    private ViewPager viewPager;
    private View indicate_line;
    private List<Fragment> fragmentList;
    private ImageView imgHot, imgMine;
    private int lineWidth;//indicate_line的宽度
    private int index;
    private CiviCommentHotFragment hotFragment;
    private CiviCommentMineFragment mineFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contentView= UiUtils.getInstance().inflate(R.layout.activity_civicomment);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        btnBack = (ImageView) findViewById(R.id.btn_back);
        rl_submit_gambit = (RelativeLayout) findViewById(R.id.rl_submit_gambit);
        hot = (RelativeLayout) findViewById(R.id.tab_hot_gambit);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        mine = (RelativeLayout) findViewById(R.id.tab_mine_gambit);
        viewPager = (ViewPager) findViewById(R.id.vp_comment);

        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int  viewPagerHeight=viewPager.getMeasuredHeight();
                fragmentList = new ArrayList<Fragment>();
                hotFragment = (CiviCommentHotFragment) FragmentManger.getInstance().getFragment(FragmentManger.FragmentEnum.CivilCommentHot.getName());
                hotFragment.setActivity(CiviCommentActivity.this);

                mineFragment = (CiviCommentMineFragment) FragmentManger.getInstance().getFragment(FragmentManger.FragmentEnum.CivilCommentMine.getName());
                mineFragment.setActivity(CiviCommentActivity.this);

                hotFragment .setInitRecycleViewHeight(viewPagerHeight);
                mineFragment.setInitRecycleViewHeight(viewPagerHeight);

                fragmentList.add(hotFragment);
                fragmentList.add(mineFragment);
                viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
            }
        });
        indicate_line = findViewById(R.id.indicate_line);

        imgHot = (ImageView) findViewById(R.id.img_hot);
        imgMine = (ImageView) findViewById(R.id.img_mine);

        hot.setOnClickListener(this);
        mine.setOnClickListener(this);
        rl_submit_gambit.setOnClickListener(this);
        calculateIndicateLineWidth();
    }

    @Override
    protected void initInitevnts() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int targetPosition = lineWidth * position + positionOffsetPixels / 2;
                ViewPropertyAnimator.animate(indicate_line).translationX(targetPosition).setDuration(0);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        index = 0;
                        imgHot.setImageDrawable(getResources().getDrawable(R.drawable.hot_gambit_press));
                        imgMine.setImageDrawable(getResources().getDrawable(R.drawable.mine_gambit));
                        hotFragment.refreshList();
                        //继而进行判断是否有刷新或者加载更多进行中:
                        if (!hotFragment.isRequestintNetwork() && hotFragment.getNetWorkTime()) {
                            hotFragment.getRequestPagerData();
                        }
                        break;
                    case 1:
                        index = 1;
                        imgMine.setImageDrawable(getResources().getDrawable(R.drawable.mine_gambit_press));
                        imgHot.setImageDrawable(getResources().getDrawable(R.drawable.hot_gambit));
                        mineFragment.refreshList();
                        //继而进行判断是否有刷新或者加载更多进行中:
                        if (!mineFragment.isRequestintNetwork() && mineFragment.getNetWorkTime()) {
                            mineFragment.getRequestPagerData();
                        }

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 进行重新启动
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        if(hotFragment==null){
            hotFragment = (CiviCommentHotFragment) FragmentManger.getInstance().getFragment(FragmentManger.FragmentEnum.CivilCommentHot.getName());
            hotFragment.setActivity(CiviCommentActivity.this);
        }
        hotFragment.setActivity(CiviCommentActivity.this);

        if(mineFragment==null){
            mineFragment = (CiviCommentMineFragment) FragmentManger.getInstance().getFragment(FragmentManger.FragmentEnum.CivilCommentMine.getName());
            mineFragment.setActivity(CiviCommentActivity.this);
        }
        mineFragment.setActivity(CiviCommentActivity.this);
        hotFragment.refreshList();
        mineFragment.refreshList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_hot_gambit:
                if (index != 0) {
                    imgHot.setImageDrawable(getResources().getDrawable(R.drawable.hot_gambit_press));
                    imgMine.setImageDrawable(getResources().getDrawable(R.drawable.mine_gambit));
                    viewPager.setCurrentItem(0);
                }
                break;
            case R.id.tab_mine_gambit:
                if (index != 1) {
                    imgMine.setImageDrawable(getResources().getDrawable(R.drawable.mine_gambit_press));
                    imgHot.setImageDrawable(getResources().getDrawable(R.drawable.hot_gambit));
                    viewPager.setCurrentItem(1);
                }
                break;
            case R.id.rl_submit_gambit:
                try {
                    User user= UserDao.getInstance().getLocalUser();
                    if(Integer.parseInt(user.getIdentityState())>=Integer.parseInt(UserPermisson.ORDINARYSTATE.getType())){
                        startActivity(new Intent(CiviCommentActivity.this, GambitSubActivity.class));
                    }else{
                        UiUtils.getInstance().showToast(HDCivilizationConstants.FORBIDDEN_USER);
                    }
                } catch (ContentException e) {
                    e.printStackTrace();
                    OKPopup.getInstance().showPopup(CiviCommentActivity.this, new OKPopup.BtnClickListener() {
                        @Override
                        public void btnOk() {
                            Intent intent = new Intent(CiviCommentActivity.this, LoginActivity.class);
                            intent.putExtra(LoginActivity.ISFROM_OTHRES,true);
                            startActivity(intent);
                            OKPopup.getInstance().dismissDialog();
                        }
                    }, false, HDCivilizationConstants.NO_LOGIN);
                }
                break;
        }
    }

    /***
     * 适配器
     **/
    class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public void finishUpdate(ViewGroup container) {
//            container.removeAllViews();
            super.finishUpdate(container);
            //获取当前的视图是位于ViewGroup的第几个位置，用来更新对应的覆盖层所在的位置
        }

        @Override
        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);
        }
    }

    /**
     * 计算indicate_line的宽度
     */
    private void calculateIndicateLineWidth() {
        int screenWidth = CiviCommentActivity.this.getWindowManager().getDefaultDisplay().getWidth();
        lineWidth = screenWidth / 2;
        indicate_line.getLayoutParams().width = lineWidth;
        indicate_line.requestLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

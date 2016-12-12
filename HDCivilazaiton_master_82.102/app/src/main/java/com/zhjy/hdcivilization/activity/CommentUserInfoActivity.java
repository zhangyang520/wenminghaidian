package com.zhjy.hdcivilization.activity;

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
import android.widget.TextView;

import com.nineoldandroids.view.ViewPropertyAnimator;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.fragment.CiviCommentHotFragment;
import com.zhjy.hdcivilization.fragment.CiviCommentMineFragment;
import com.zhjy.hdcivilization.fragment.CommentJoinFragment;
import com.zhjy.hdcivilization.fragment.CommentSubFragment;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.manager.FragmentManger;
import com.zhjy.hdcivilization.utils.BitmapUtil;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/7/30.
 * 文明评论话题发布者信息界面
 *
 */
public class CommentUserInfoActivity extends BaseActivity implements View.OnClickListener {

    private ImageView btnBack;
    private CircleImageView userPic;
    private TextView name,nameType,joinNumber,subNumber;
    private ImageView imgJoin,imgSub;
    private View indicate_line;
    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    private RelativeLayout rl_back;
    //当前选中的项
    int currenttab=-1;
    private int lineWidth;//indicate_line的宽度
    private int index;
    private  CommentJoinFragment commentJoinFragment;
    private CommentSubFragment commentSubFragment;
    public static  String USER_ID_KEY="USER_ID_KEY";//传递参数的键

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        customLayout = R.layout.activity_comment_user_info;
        super.onCreate(savedInstanceState);
    }

    String targetUserId;
    @Override
    protected void initViews() {
        targetUserId=getIntent().getStringExtra(USER_ID_KEY);
        btnBack = (ImageView)findViewById(R.id.btn_back);
        rl_back = (RelativeLayout)findViewById(R.id.rl_back);
        userPic = (CircleImageView)findViewById(R.id.comment_user_pic);
        name = (TextView)findViewById(R.id.comment_user_name);
        nameType = (TextView)findViewById(R.id.comment_user_name_type);
        imgJoin = (ImageView)findViewById(R.id.img_join);
        joinNumber = (TextView)findViewById(R.id.join_bumber);
        imgSub = (ImageView)findViewById(R.id.img_sub);
        subNumber = (TextView)findViewById(R.id.sub_number);
        indicate_line = findViewById(R.id.indicate_line);
        viewPager = (ViewPager)findViewById(R.id.vp_comment_user);
        imgJoin.setOnClickListener(this);
        imgSub.setOnClickListener(this);

        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int viewPageHeight=viewPager.getMeasuredHeight();
                fragmentList = new ArrayList<Fragment>();
                commentJoinFragment= (CommentJoinFragment)FragmentManger.getInstance().getFragment(FragmentManger.FragmentEnum.CommentJoin.getName());
                //进行设置目标用户的id
                commentJoinFragment.setTargetUserId(targetUserId);
                commentJoinFragment.setJoinText(joinNumber);
                commentJoinFragment.setActivity(CommentUserInfoActivity.this);
                commentJoinFragment.setInitRecycleViewHeight(viewPageHeight);

                commentSubFragment = (CommentSubFragment) FragmentManger.getInstance().getFragment(FragmentManger.FragmentEnum.CommentSub.getName());
                //进行设置目标用户的id
                commentSubFragment.setTargetUserId(targetUserId);
                commentSubFragment.setSubText(subNumber);
                commentSubFragment.setActivity(CommentUserInfoActivity.this);
                commentSubFragment.setInitRecycleViewHeight(viewPageHeight);

                fragmentList.add(commentJoinFragment);
                fragmentList.add(commentSubFragment);
                viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

            }
        });
        calculateIndicateLineWidth();
    }

    @Override
    protected void initInitevnts() {
        //进行设置姓名和类型，头像
        User targetUser= UserDao.getInstance().getUserId(targetUserId);
        if(targetUser!=null){
            //进行修改
            String nickName="";
            if(targetUser.getNickName().matches("^[1][3,4,5,8,7][0-9]{9}$")){
                nickName=targetUser.getNickName().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
            }else{
                try {
                    User user=UserDao.getInstance().getLocalUser();
                    //进行判断对象是否和该条目的launcherUser一致
                    if(user.getUserId().equals(targetUser.getUserId())) {
                        //如果id一致 昵称是否为空 :y 手机号
                        nickName = targetUser.getNickName().equals("") ?//
                                user.getAccountNumber() :targetUser.getNickName();
                        if (nickName.matches("^[1][3,4,5,8,7][0-9]{9}$")) {
                            nickName = nickName.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                        }
                    }else{
                        nickName=targetUser.getNickName();
                    }
                } catch (ContentException e) {
                    e.printStackTrace();
                    nickName=targetUser.getNickName();
                }
            }
            name.setText(nickName);
            if(targetUser.getIdentityState().equals(UserPermisson.VOLUNTEER.getType())){
                nameType.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
            }else{
                nameType.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
            }
            joinNumber.setText(targetUser.getJoinThemeCount()+"");
            subNumber.setText(targetUser.getSubThemeCount()+"");
            BitmapUtil.getInstance().displayImg(userPic, UrlParamsEntity.WUCHEN_XU_IP_FILE+targetUser.getPortraitUrl());
        }

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
                        imgJoin.setImageDrawable(getResources().getDrawable(R.drawable.comment_user_info_press));
                        imgSub.setImageDrawable(getResources().getDrawable(R.drawable.comment_user_info_sub));
                        //commentJoinFragment
                        //继而进行判断是否有刷新或者加载更多进行中:
                        if (!commentJoinFragment.isRequestintNetwork() && commentJoinFragment.getNetWorkTime()) {
                            commentJoinFragment.getRequestPagerData();
                        }

                        break;
                    case 1:
                        index = 1;
                        imgSub.setImageDrawable(getResources().getDrawable(R.drawable.comment_user_info_sub_press));
                        imgJoin.setImageDrawable(getResources().getDrawable(R.drawable.comment_user_info));
                        //commentSubFragment
                        //继而进行判断是否有刷新或者加载更多进行中:
                        if (!commentSubFragment.isRequestintNetwork() && commentSubFragment.getNetWorkTime()) {
                            commentSubFragment.getRequestPagerData();
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
        if(commentJoinFragment==null){
            commentJoinFragment= (CommentJoinFragment)FragmentManger.getInstance().getFragment(FragmentManger.FragmentEnum.CommentJoin.getName());
        }
        commentJoinFragment.refreshList();
        if(commentSubFragment==null){
            commentSubFragment = (CommentSubFragment) FragmentManger.getInstance().getFragment(FragmentManger.FragmentEnum.CommentSub.getName());
        }
        commentSubFragment.refreshList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_join:
                if (index!=0){
                    imgJoin.setImageDrawable(getResources().getDrawable(R.drawable.comment_user_info_press));
                    imgSub.setImageDrawable(getResources().getDrawable(R.drawable.comment_user_info_sub));
                    viewPager.setCurrentItem(0);
                }
                break;
            case R.id.img_sub:
                if (index!=1){
                    imgSub.setImageDrawable(getResources().getDrawable(R.drawable.comment_user_info_sub_press));
                    imgJoin.setImageDrawable(getResources().getDrawable(R.drawable.comment_user_info));
                    viewPager.setCurrentItem(1);
                    }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /***适配器**/
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
            super.finishUpdate(container);
            //获取当前的视图是位于ViewGroup的第几个位置，用来更新对应的覆盖层所在的位置
            int currentItem=viewPager.getCurrentItem();
            if (currentItem==currenttab)
            {
                return ;
            }
            currenttab=viewPager.getCurrentItem();
        }
    }

    /**计算indicate_line的宽度*/
    private void calculateIndicateLineWidth() {
        int screenWidth = CommentUserInfoActivity.this.getWindowManager().getDefaultDisplay().getWidth();
        lineWidth = screenWidth/2;
        indicate_line.getLayoutParams().width = lineWidth;
        indicate_line.requestLayout();
    }

    /**
     * 进行设置参与话题的个数
     * @param
     */
    public void setCount(User user){
        joinNumber.setText(user.getJoinThemeCount() + "");
        subNumber.setText(user.getSubThemeCount()+"");
    }

}

package com.zhjy.hdcivilization.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nineoldandroids.view.ViewPropertyAnimator;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.fragment.SuperviseMineListFragment;
import com.zhjy.hdcivilization.fragment.SuperviseProblemFragment;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.manager.FragmentManger;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的文明监督中的列表
 */
public class MineSuperviseListActivity extends BaseActivity implements
        SuperviseMineListFragment.OnFragmentInteractionListener, View.OnClickListener {

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    ImageView btn_back;
    ImageView img_mysub,img_problem;
    RelativeLayout tab_hot_gambit,tab_mine_gambit,rl_back;
    View indicate_line;
    ViewPager vp_comment;
    int lineWidth;
    List<Fragment> fragmentList=new ArrayList<Fragment>();
    SuperviseFragmentAdapter superviseFragmentAdapter;
    SuperviseProblemFragment superviseProblemFragment;
    SuperviseMineListFragment superviseMineListFragment;
    int index;
    String itemId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        customLayout= R.layout.activity_supervise_mysub_list;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews(){
        //在推送的时候传递过来itemId；
        itemId = getIntent().getStringExtra(HDCivilizationConstants.SUPERVISE_ITEMID);

        btn_back=(ImageView)findViewById(R.id.btn_back);
        img_mysub=(ImageView)findViewById(R.id.img_mysub);
        img_problem=(ImageView)findViewById(R.id.img_problem);
        tab_hot_gambit=(RelativeLayout)findViewById(R.id.tab_hot_gambit);
        rl_back=(RelativeLayout)findViewById(R.id.rl_back);
        tab_mine_gambit=(RelativeLayout)findViewById(R.id.tab_mine_gambit);
        indicate_line=(View)findViewById(R.id.indicate_line);
        vp_comment=(ViewPager)findViewById(R.id.vp_comment);

        img_mysub.setOnClickListener(this);
        img_problem.setOnClickListener(this);
        //进行初始化长度
        initCalulateLength();
        if (itemId.equals(HDCivilizationConstants.ITEMID)){
            superviseMineListFragment=(SuperviseMineListFragment)FragmentManger.getInstance().getFragment(FragmentManger.FragmentEnum.SuperViseMine.getName());
            fragmentList.add(superviseMineListFragment);
        }else{
            superviseMineListFragment=(SuperviseMineListFragment)FragmentManger.getInstance().getFragment(FragmentManger.FragmentEnum.SuperViseMine.getName());
            superviseMineListFragment.setItemIdPush(itemId);
            fragmentList.add(superviseMineListFragment);
        }

        superviseProblemFragment=(SuperviseProblemFragment)FragmentManger.getInstance().getFragment(FragmentManger.FragmentEnum.SuperViseProblem.getName());
        fragmentList.add(superviseProblemFragment);

        //进行初始化
        superviseFragmentAdapter=new SuperviseFragmentAdapter(getSupportFragmentManager());
        vp_comment.setAdapter(superviseFragmentAdapter);
    }

    @Override
    protected void initInitevnts() {
        vp_comment.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int length=(int)(position*lineWidth+positionOffset*lineWidth);
                ViewPropertyAnimator.animate(indicate_line).translationX(length).setDuration(0);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        index=0;
                        //进行设置布局
                        img_mysub.setImageDrawable(getResources().getDrawable(R.drawable.supervise_mysub_press));
                        img_problem.setImageDrawable(getResources().getDrawable(R.drawable.supervise_problem));

                        //继而进行判断是否有刷新或者加载更多进行中:
                        if (!superviseMineListFragment.isRequestintNetwork() && superviseMineListFragment.getNetWorkTime()) {
                            superviseMineListFragment.getDataFromInternet();
                        }

                        break;

                    case 1:
                        index=1;
                        //进行设置布局
                        img_mysub.setImageDrawable(getResources().getDrawable(R.drawable.supervise_mysub));
                        img_problem.setImageDrawable(getResources().getDrawable(R.drawable.supervise_problem_press));

                        //继而进行判断是否有刷新或者加载更多进行中:
                        if (!superviseProblemFragment.isRequestintNetwork() && superviseProblemFragment.getNetWorkTime()) {
                            superviseProblemFragment.getDataFromInternet();
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        rl_back.setOnClickListener(this);
    }

    /**
     * 进行初始化长度
     */
    protected  void initCalulateLength(){
        lineWidth=getWindowManager().getDefaultDisplay().getWidth()/2;
        indicate_line.getLayoutParams().width=lineWidth;
        indicate_line.requestLayout();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;


            case R.id.img_mysub:
                if(index!=0){
                    //进行设置布局
                    img_mysub.setImageDrawable(getResources().getDrawable(R.drawable.supervise_mysub_press));
                    img_problem.setImageDrawable(getResources().getDrawable(R.drawable.supervise_problem));
                    vp_comment.setCurrentItem(0);
                    if (((SuperviseMineListFragment)fragmentList.get(0)).getNetWorkTime()){
                        ((SuperviseMineListFragment)fragmentList.get(0)).getDataFromInternet();
                    }
                }
                break;

            case R.id.img_problem:
                if(index!=1){
                    //进行设置布局
                    img_mysub.setImageDrawable(getResources().getDrawable(R.drawable.supervise_mysub));
                    img_problem.setImageDrawable(getResources().getDrawable(R.drawable.supervise_problem_press));
                    ((SuperviseProblemFragment)fragmentList.get(1)).getDataFromInternet();
                    vp_comment.setCurrentItem(1);
                    /****判断是否可以请求网络*****/
                    ((SuperviseProblemFragment)fragmentList.get(1)).getDataFromInternet();
                    if (((SuperviseProblemFragment)fragmentList.get(1)).getNetWorkTime()){
                        ((SuperviseProblemFragment)fragmentList.get(1)).getDataFromInternet();
                    }
                }
                break;
        }
    }


    public class SuperviseFragmentAdapter extends FragmentPagerAdapter {
        public SuperviseFragmentAdapter(FragmentManager fm) {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

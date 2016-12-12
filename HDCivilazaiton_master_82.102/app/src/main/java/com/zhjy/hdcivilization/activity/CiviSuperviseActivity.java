package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.MainNumberDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_MainNumber;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.OKPopup;

/**
 * zhangyang 2016-7-25
 *
 * 文明监督模块
 */
public class CiviSuperviseActivity extends BaseActivity implements View.OnClickListener {

    //整个区域的布局
    LinearLayout supervise_content_one_ll,supervise_content_two_ll,ll_two,ll_one,ll_line_two,ll_line_one;
    TextView supervise_content_one,supervise_content_two;
    ImageView iv_supervise_arrow_one,iv_supervise_arrow_two;
    ScrollView scrollView;
    TextView tv_arrow_one,tv_arrow_two;
    TextView civi_supervise_number;
    ImageView btn_back;
    ImageView iv_supervise_want_sub;
    RelativeLayout rl_txt_number,rl_back,rl_tv_mySubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contentView= UiUtils.getInstance().inflate(R.layout.activity_civisupervise);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        //进行获取linearLayout的布局
        supervise_content_one_ll=(LinearLayout)findViewById(R.id.supervise_content_one_ll);
        supervise_content_two_ll=(LinearLayout)findViewById(R.id.supervise_content_two_ll);
        ll_line_one=(LinearLayout)findViewById(R.id.ll_line_one);
        ll_line_two=(LinearLayout)findViewById(R.id.ll_line_two);
        ll_one=(LinearLayout)findViewById(R.id.ll_one);
        ll_two=(LinearLayout)findViewById(R.id.ll_two);
        supervise_content_two_ll.setOnClickListener(this);
        supervise_content_one_ll.setOnClickListener(this);

        //进行获取文本内容的布局
        supervise_content_one=(TextView)findViewById(R.id.supervise_content_one);
        supervise_content_two=(TextView)findViewById(R.id.supervise_content_two);

        scrollView=getScrollView(supervise_content_one);
        scrollView.setSmoothScrollingEnabled(true);
        //进行初始化content_one
//        supervise_content_one.getLayoutParams().height=getShortHeight();

        //进行获取图片
        iv_supervise_arrow_one=(ImageView)findViewById(R.id.iv_supervise_arrow_one);
        iv_supervise_arrow_two=(ImageView)findViewById(R.id.iv_supervise_arrow_two);

        //获取提示的文字、
        tv_arrow_one=(TextView)findViewById(R.id.tv_arrow_one);
        tv_arrow_two=(TextView)findViewById(R.id.tv_arrow_two);

        //数字提醒
        civi_supervise_number=(TextView)findViewById(R.id.civi_supervise_number);
        rl_txt_number=(RelativeLayout)findViewById(R.id.rl_txt_number);
        rl_back=(RelativeLayout)findViewById(R.id.rl_back);

        //进行获取返回键
        btn_back=(ImageView)findViewById(R.id.btn_back);
//        btn_back.setOnClickListener(this);
        rl_back.setOnClickListener(this);

        //进行获取我的提报
        rl_tv_mySubmit=(RelativeLayout)findViewById(R.id.rl_tv_mySubmit);
        rl_tv_mySubmit.setOnClickListener(this);

        iv_supervise_want_sub=(ImageView)findViewById(R.id.iv_supervise_want_sub);
        iv_supervise_want_sub.setOnClickListener(this);

        //进行初始化消息提醒个数
        initSuperviseCount();

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);//进行取消布局监听

                System.out.println("supervise_content_one measureHeight:" + supervise_content_one.getMeasuredHeight() + "..getShortHeight:" + getShortHeight());
                if (supervise_content_one.getMeasuredHeight() <= getShortHeight()) {
                    ll_line_one.setVisibility(View.GONE);
                } else if(supervise_content_one.getMeasuredHeight() > getShortHeight()){
                    if(supervise_content_one.getMeasuredHeight()-getShortHeight()<getOnLineHeight()){
                        ll_line_one.setVisibility(View.GONE);
                    }else{
                        ll_line_one.setVisibility(View.VISIBLE);
                        supervise_content_one.setMaxLines(6);
                        supervise_content_one.setEllipsize(TextUtils.TruncateAt.END);
                    }
                }

                System.out.println("supervise_content_two measureHeight:" + supervise_content_two.getMeasuredHeight() + "..getShortHeight:" + getShortHeight());
                if (supervise_content_two.getMeasuredHeight() <= getShortHeight()) {
                    ll_line_two.setVisibility(View.GONE);
                } else  if(supervise_content_two.getMeasuredHeight() > getShortHeight()){
                    if(supervise_content_two.getMeasuredHeight()-getShortHeight()<getOnLineHeight()){
                        ll_line_two.setVisibility(View.GONE);
                    }else{
                        ll_line_two.setVisibility(View.VISIBLE);
                        supervise_content_two.setMaxLines(6);
                        supervise_content_two.setEllipsize(TextUtils.TruncateAt.END);
                    }
                }
            }
        });
    }

    private void initSuperviseCount() {
        try {
            User user = UserDao.getInstance().getLocalUser();
            HDC_MainNumber hdc_mainNumber=//
                    MainNumberDao.getInstance().getNumberBy(user.getUserId());
            System.out.println("CiviSuperviseActivity getSuperviseCount:"+hdc_mainNumber.getSuperviseCount());
            if(hdc_mainNumber.getSuperviseCount()>0){
                rl_txt_number.setVisibility(View.VISIBLE);
                civi_supervise_number.setText(hdc_mainNumber.getSuperviseCount()+"");
            }else{
                rl_txt_number.setVisibility(View.INVISIBLE);
            }
        } catch (ContentException e) {
            e.printStackTrace();
            rl_txt_number.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void initInitevnts(){
        //进行初始化消息提醒个数
        initSuperviseCount();
    }

    //进行重新设置 文明监督消息提醒个数
    @Override
    protected void onRestart() {
        super.onRestart();
        initSuperviseCount();
    }

    /**
     * 进行获取scrollView
     * @param supervise_content_one
     * @return
     */
    private ScrollView getScrollView(View supervise_content_one) {

        ViewParent parentView=(ViewParent)supervise_content_one.getParent();
        if(parentView instanceof ViewGroup){
           ViewGroup groupView=(ViewGroup)parentView;
            if(groupView instanceof ScrollView){
                return (ScrollView)groupView;
            }else{
                return getScrollView(groupView);
            }
        }else{
            return null;
        }
    }

    boolean content_one_flag,content_two_flag;
    int clickState;
    @Override
    public void onClick(View v) {
        synchronized (CiviStateActivity.class) {
            switch (v.getId()){
                case R.id.supervise_content_one_ll:
                    Log.d(TAG,"onClick  supervise_content_one_ll");
                    clickState = 0;
                    //点击第一个按钮的时候
                    /**
                     *  继而处理自身的ui收起逻辑
                     *      根据content_one_flag
                     *          逻辑1
                     *        else
                     *          逻辑2
                     *     逻辑1：
                     *       startHeight：最高的高度
                     *       endHeight：初始的高度
                     *       进行值动画的更新:
                     *           textView的高度,和scrollView的测量高度(滑动)
                     *           最后进行结果的ui，以及设置textView的属性
                     */
                    if (content_two_flag) {
                        //如果第二个是打开着的 先关闭第二个
                        closeContentTwo();
                    } else {
                        processContentOne();
                    }
                    break;

                case R.id.supervise_content_two_ll:
                    Log.d(TAG,"onClick  supervise_content_two_ll");
                    clickState = 1;
                    //点击第二个按钮的时候
                    /**
                     * 先查看其它按钮的收起情况
                     *   如果其它按钮已经展开,进行收起,否则....
                     */
                    if (content_one_flag) {
                        closeContentOne();
                    } else {
                        processContentTwo();
                    }
                    break;


                case R.id.rl_back:
                    //返回键 // TODO: 2016/7/26
                    finish();
                    break;

                case R.id.rl_tv_mySubmit:
                    //我的上报 // TODO: 2016/7/26
                    try {
                        User user= UserDao.getInstance().getLocalUser();
                        System.out.println("tv_mySubmit user:"+user.toString());
                        if(user.getIdentityState().equals(UserPermisson.VOLUNTEER.getType())){
                            //只有是志愿者身份才能登录：
                            Intent myIntent = new Intent(CiviSuperviseActivity.this, MineSuperviseListActivity.class);
                            myIntent.putExtra(HDCivilizationConstants.SUPERVISE_ITEMID, HDCivilizationConstants.ITEMID);
                            startActivity(myIntent);
                        }else{
                            UiUtils.getInstance().showToast(HDCivilizationConstants.YOU_NOT_VOLUNTEER);
                            finish();
                        }
                    } catch (ContentException e){
                        e.printStackTrace();
//                        UiUtils.getInstance().showToast(e.getErrorContent());
                        //一般不会存在这种情况
                        OKPopup.getInstance().showPopup(CiviSuperviseActivity.this, new OKPopup.BtnClickListener() {
                            @Override
                            public void btnOk() {
                                finish();
                                Intent intent = new Intent(CiviSuperviseActivity.this, LoginActivity.class);
                                intent.putExtra(LoginActivity.ISFROM_OTHRES,true);
                                startActivity(intent);
                                OKPopup.getInstance().dismissDialog();
                            }
                        },true, HDCivilizationConstants.NO_LOGIN);
                    }
                    break;

                case R.id.iv_supervise_want_sub:
                    //我要提报
                    //先要进行判断用户是否登录
                    try {
                        User user= UserDao.getInstance().getLocalUser();
                        System.out.println("user iv_supervise_want_sub..."+user.toString());
                        if(user.getIdentityState().equals(UserPermisson.VOLUNTEER.getType())){
//                            //只有是志愿者身份才能登录：
                            Intent intent = new Intent(CiviSuperviseActivity.this, MySubSuperviseActivity.class);
                            startActivity(intent);
                        }else{
                            UiUtils.getInstance().showToast(HDCivilizationConstants.YOU_NOT_VOLUNTEER);
                            finish();
                        }
                    } catch (ContentException e) {
                        e.printStackTrace();
                        //一般不会存在这种情况
                        OKPopup.getInstance().showPopup(CiviSuperviseActivity.this, new OKPopup.BtnClickListener() {
                            @Override
                            public void btnOk() {
                                finish();
                                Intent intent = new Intent(CiviSuperviseActivity.this, LoginActivity.class);
                                intent.putExtra(LoginActivity.ISFROM_OTHRES,true);
                                startActivity(intent);
                                OKPopup.getInstance().dismissDialog();
                            }
                        }, true, HDCivilizationConstants.NO_LOGIN);
                    }
                    break;
            }
        }
    }

    /**
     * 进行处理第一个业务逻辑
     */
    private void processContentOne(){
        if(content_one_flag){
            closeContentOne();
        } else {
            openContentOne();
        }
    }

    /**
     * 打开内容1的界面
     */
    private void openContentOne(){
        int startHeight;
        int endHeight;
        content_one_flag=!content_one_flag;
        startHeight=getShortHeight();
        endHeight=getLongHeight(supervise_content_one);
        startValueAnimatorOne(startHeight,endHeight);
    }

    /**
     * 进行关闭内容一的界面
     */
    private void closeContentOne(){
        //继而处理第一个
        int startHeight;
        int endHeight;
        content_one_flag=!content_one_flag;
        startHeight=getLongHeight(supervise_content_one);
        endHeight=getShortHeight();
        startValueAnimatorOne(startHeight,endHeight);
    }

    /**
     * 进行处理第二个业务逻辑
     */
    private void processContentTwo() {
        if(content_two_flag){
            closeContentTwo();
        }else {
            openContentTwo();
        }
    }

    /**
     * 进行打开内容二的界面
     */
    private  void openContentTwo(){
        int startHeight;
        int endHeight;//继而处理第二个
        content_two_flag = !content_two_flag;
        startHeight = getShortHeight();
        endHeight = getLongHeight(supervise_content_two);
        startValueAnimatorTwo(startHeight, endHeight);
    }

    /**
     * 进行关闭内容二的界面
     */
    private void closeContentTwo(){
        int startHeight;
        int endHeight;//继而处理第二个
        content_two_flag=!content_two_flag;
        startHeight=getLongHeight(supervise_content_two);
        endHeight=getShortHeight();
        startValueAnimatorTwo(startHeight,endHeight);
    }
    /**
     * 动画函数
     * @param startHeight
     * @param endHeight
     */
    private void startValueAnimatorOne(int startHeight, int endHeight) {
        ValueAnimator valueAnimator= ValueAnimator.ofInt(startHeight, endHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //进行更新
                supervise_content_one.getLayoutParams().height=(Integer)valueAnimator.getAnimatedValue();
                supervise_content_one.requestLayout();
                scrollView.requestLayout();
            }
        });

        valueAnimator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                Log.d(TAG,"startValueAnimatorOne onAnimationStart...");
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Log.d(TAG,"startValueAnimatorOne onAnimationEnd...");
                if (content_one_flag) {
                    //展开
                    supervise_content_one.setMaxLines(Integer.MAX_VALUE);
                    iv_supervise_arrow_one.setImageDrawable(UiUtils.getInstance().getContext().getResources().getDrawable(R.drawable.uparrow_selector));
                    tv_arrow_one.setText(UiUtils.getInstance().getContext().getText(R.string.civiSupervise_shouqi));
                    Log.d(TAG, "startValueAnimatorOne onAnimationEnd 收起...ll_one top:" + ll_one.getTop());
                    scrollView.smoothScrollBy(0, ll_one.getTop());
                } else {
                    //关闭
                    supervise_content_one.setMaxLines(6);
                    iv_supervise_arrow_one.setImageDrawable(UiUtils.getInstance().getContext().getResources().getDrawable(R.drawable.downarrow_selector));
                    tv_arrow_one.setText(UiUtils.getInstance().getContext().getText(R.string.civiSupervise_seeall));
                    if(clickState== 1){
                       processContentTwo();
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.setDuration(500);
        valueAnimator.start();
    }


    /**
     * 动画函数
     * @param startHeight
     * @param endHeight
     */
    private void startValueAnimatorTwo(int startHeight, int endHeight) {
        ValueAnimator valueAnimator= ValueAnimator.ofInt(startHeight, endHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //进行更新
                supervise_content_two.getLayoutParams().height=(Integer)valueAnimator.getAnimatedValue();
                supervise_content_two.requestLayout();
                scrollView.requestLayout();
            }
        });

        valueAnimator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                Log.d(TAG,"startValueAnimatorTwo onAnimationStart...");
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Log.d(TAG,"startValueAnimatorTwo onAnimationEnd...");
                if (content_two_flag) {
                    //展开
                    supervise_content_two.setMaxLines(Integer.MAX_VALUE);
                    iv_supervise_arrow_two.setImageDrawable(UiUtils.getInstance().getContext().getResources().getDrawable(R.drawable.uparrow_selector));
                    tv_arrow_two.setText(UiUtils.getInstance().getContext().getText(R.string.civiSupervise_shouqi));
                    Log.d(TAG, "startValueAnimatorTwo onAnimationEnd 收起...ll_two top:" + ll_two.getTop());
                    scrollView.smoothScrollBy(0, ll_two.getTop());
//                    ViewPropertyAnimator.animate(scrollView).yBy(-ll_two.getTop()).setDuration(200);
                } else {
                    //关闭
                    supervise_content_two.setMaxLines(6);
                    iv_supervise_arrow_two.setImageDrawable(UiUtils.getInstance().getContext().getResources().getDrawable(R.drawable.downarrow_selector));
                    tv_arrow_two.setText(UiUtils.getInstance().getContext().getText(R.string.civiSupervise_seeall));

                    if(clickState==0){
                       processContentOne();
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        valueAnimator.setDuration(500);
        valueAnimator.start();
    }

    /**
     * 进行获取长的textView的高度
     * @return
     * @param supervise_content_one_ll
     */
    public int getLongHeight(TextView supervise_content_one_ll) {
        supervise_content_one_ll.setMaxLines(Integer.MAX_VALUE);
        int width=supervise_content_one_ll.getMeasuredWidth();
        supervise_content_one_ll.getLayoutParams().height=LinearLayout.LayoutParams.WRAP_CONTENT;
        int widthMeasureSpec=View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec= View.MeasureSpec.makeMeasureSpec(20000, View.MeasureSpec.AT_MOST);
        supervise_content_one_ll.measure(widthMeasureSpec, heightMeasureSpec);
        System.out.println("getLongHeight...:" + supervise_content_one_ll.getMeasuredHeight());
        return supervise_content_one_ll.getMeasuredHeight();
    }

    /**
     * 进行获取短的textView的高度
     * @return
     */
    public int getShortHeight() {
        TextView longTextView=new TextView(UiUtils.getInstance().getContext());
        longTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        longTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        longTextView.setLineSpacing(0, 1.25f);
        longTextView.setLines(6);
        int width=longTextView.getMeasuredWidth();
        int widthMeasureSpec=View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec= View.MeasureSpec.makeMeasureSpec(20000, View.MeasureSpec.AT_MOST);
        longTextView.measure(widthMeasureSpec, heightMeasureSpec);
        return longTextView.getMeasuredHeight();
    }

    public int getOnLineHeight(){
        TextView longTextView=new TextView(UiUtils.getInstance().getContext());
        longTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        longTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        longTextView.setLineSpacing(0, 1.25f);
        longTextView.setLines(1);
        int width=longTextView.getMeasuredWidth();
        int widthMeasureSpec=View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec= View.MeasureSpec.makeMeasureSpec(20000, View.MeasureSpec.AT_MOST);
        longTextView.measure(widthMeasureSpec, heightMeasureSpec);
        return longTextView.getMeasuredHeight();
    }
}

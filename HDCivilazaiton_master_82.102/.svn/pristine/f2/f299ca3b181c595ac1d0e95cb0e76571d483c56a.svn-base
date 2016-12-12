package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.utils.DisplayUtil;
import com.zhjy.hdcivilization.utils.UiUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author :huangxianfeng on 2016/8/2.
 * 志愿者报名
 */
public class VolunteerSignUpActivity extends BaseActivity implements View.OnClickListener {

    RelativeLayout volunteer_content_one,rl_scroll_one;
    RelativeLayout volunteer_content_two,rl_scroll_two;
    RelativeLayout volunteer_content_three,rl_scroll_three;
    RelativeLayout volunteer_content_four,rl_scroll_four;
    RelativeLayout rl_img,rl_back;

    RelativeLayout rl_top,rl_bottom;

    LinearLayout ll_text_total;

    TextView oneContent;
    TextView twoContent;
    TextView threeContent;
    TextView fourContent;

    ImageButton arrowOne;
    ImageButton arrowTwo;
    ImageButton arrowThree;
    ImageButton arrowFour;

    ScrollView scrollView;

    ImageButton onLineTrain;
    ImageView btnBack;
    ImageView applyVolunteer;
    LinearLayout ll_top;
    int shortHeight;//答案文本的最短的高度
    Map<Integer,ViewHolder> flagMaps=new ConcurrentHashMap<Integer,ViewHolder>();
    Map<Integer,Integer> indexMaps=new ConcurrentHashMap<Integer,Integer>();
    int textItemCount=4;

    //角标标识
    final int index_zero=0;
    final int index_one=1;
    final int index_two=2;
    final int index_three=3;

    final int MESSAGE_OPEN_ITEM_WHAT=101;//需要进行展开新的条目
    View scroll_line;
    int leftHeight;//scroll剩余的高度
    /**
     * 定义一个Handler
     */
    private Handler handler=new Handler(){
        @Override
        public  void handleMessage(Message message) {
            synchronized (VolunteerSignUpActivity.this){
                //需要进行加锁,否则会同步
                switch (message.what) {
                    case MESSAGE_OPEN_ITEM_WHAT:
                        Log.d(TAG,"MESSAGE_OPEN_ITEM_WHAT ....");
                        //进行遍历map集合,除了当前currentClickState之外,如果有flag为true，进行返回
                        for (Map.Entry<Integer, ViewHolder> entry : flagMaps.entrySet()) {
                            if(entry.getKey()!=currentClickState){
                                Log.d(TAG,"MESSAGE_OPEN_ITEM_WHAT entry.getKey()!=currentClickState ....index:"+entry.getKey());
                                if(entry.getValue().isItemFlag()){
                                    return ;
                                }
                            }
                        }
                        //进行处理当前情况下
                        processTextContent(flagMaps.get(currentClickState).getContentTextView(),currentClickState,flagMaps.get(currentClickState).getArrowBtn());
                        break;
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        customLayout= R.layout.activity_volunteer_sign_up;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {

        btnBack = (ImageView)findViewById(R.id.btn_back);
        rl_back = (RelativeLayout)findViewById(R.id.rl_back);
        onLineTrain = (ImageButton)findViewById(R.id.volunteer_on_line);

        //进行获取linearLayout的布局
        volunteer_content_one = (RelativeLayout)findViewById(R.id.volunteer_content_one);
        volunteer_content_two = (RelativeLayout)findViewById(R.id.volunteer_content_two);
        volunteer_content_three = (RelativeLayout)findViewById(R.id.volunteer_content_three);
        volunteer_content_four = (RelativeLayout)findViewById(R.id.volunteer_content_four);


        rl_scroll_four = (RelativeLayout)findViewById(R.id.rl_scroll_four);
        rl_scroll_one = (RelativeLayout)findViewById(R.id.rl_scroll_one);
        rl_scroll_three = (RelativeLayout)findViewById(R.id.rl_scroll_three);
        rl_scroll_two = (RelativeLayout)findViewById(R.id.rl_scroll_two);
        rl_top = (RelativeLayout)findViewById(R.id.rl_top);
        rl_bottom = (RelativeLayout)findViewById(R.id.rl_bottom);

        rl_img = (RelativeLayout)findViewById(R.id.rl_img);

        ll_top = (LinearLayout)findViewById(R.id.ll_top);
        ll_text_total = (LinearLayout)findViewById(R.id.ll_text_total);

        scrollView=(ScrollView)findViewById(R.id.scrollView);
        //获取内容显示的控件
        oneContent = (TextView)findViewById(R.id.volunteer_one_content);
        twoContent = (TextView)findViewById(R.id.volunteer_two_content);
        threeContent = (TextView)findViewById(R.id.volunteer_three_content);
        fourContent = (TextView)findViewById(R.id.volunteer_four_content);

        scrollView=getScrollView(oneContent);
        scrollView.setSmoothScrollingEnabled(true);

        //右边箭头显示
        arrowOne = (ImageButton)findViewById(R.id.volunteer_arrow_one);
        arrowTwo = (ImageButton)findViewById(R.id.volunteer_arrow_two);
        arrowThree = (ImageButton)findViewById(R.id.volunteer_arrow_three);
        arrowFour = (ImageButton)findViewById(R.id.volunteer_arrow_four);

        applyVolunteer = (ImageView)findViewById(R.id.iv_volunteer_want_sub);
        scroll_line = (View)findViewById(R.id.scroll_line);

//        final View fiveView=View.inflate(VolunteerSignUpActivity.this,R.layout.content_volunteer_five,null);
        final RelativeLayout fiveView=(RelativeLayout)findViewById(R.id.rl_scroll_five);
        ll_top.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ll_top.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //进行获取剩余的高度
                leftHeight=ll_top.getMeasuredHeight()-rl_top.getMeasuredHeight()-scroll_line.getMeasuredHeight()-rl_img.getMeasuredHeight()-rl_bottom.getMeasuredHeight();
                initAllTextView(fiveView);
            }
        });
        //进行初始化所有的文本答案的高度和进行消失
    }

    /**
     * 重新启动
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            User user= UserDao.getInstance().getLocalUser();
            if(user.getIdentityState().equals(UserPermisson.ORDINARYAPPLYING.getType())){
                //如果该用户变为普通用户申请志愿者中....进行退出
                finish();
            }
        } catch (ContentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 进行初始化所有的文本答案的高度和进行消失
     */
    private void initAllTextView(RelativeLayout fiveView) {

        ViewHolder txtContentViewHolder;
        //进行初始化map集合数据
        for(int i=0;i<=textItemCount-1;++i){
            txtContentViewHolder=new ViewHolder();
            switch (i){
                case index_zero:
                    txtContentViewHolder.setArrowBtn(arrowOne);
                    txtContentViewHolder.setContentTextView(oneContent);
                    txtContentViewHolder.setInitTextContentHeight(oneContent.getMeasuredHeight());
                    txtContentViewHolder.setItemFlag(false);
                    txtContentViewHolder.setVolunteer_rl_content(volunteer_content_one);
                    txtContentViewHolder.setRl_scroll(rl_scroll_one);
                    break;
                case index_one:
                    txtContentViewHolder.setArrowBtn(arrowTwo);
                    txtContentViewHolder.setContentTextView(twoContent);
                    txtContentViewHolder.setInitTextContentHeight(twoContent.getMeasuredHeight());
                    txtContentViewHolder.setItemFlag(false);
                    txtContentViewHolder.setVolunteer_rl_content(volunteer_content_two);
                    txtContentViewHolder.setRl_scroll(rl_scroll_two);
                    break;
                case index_two:
                    txtContentViewHolder.setArrowBtn(arrowThree);
                    txtContentViewHolder.setContentTextView(threeContent);
                    txtContentViewHolder.setInitTextContentHeight(threeContent.getMeasuredHeight());
                    txtContentViewHolder.setItemFlag(false);
                    txtContentViewHolder.setVolunteer_rl_content(volunteer_content_three);
                    txtContentViewHolder.setRl_scroll(rl_scroll_three);
                    break;
                case index_three:
                    txtContentViewHolder.setArrowBtn(arrowFour);
                    txtContentViewHolder.setContentTextView(fourContent);
                    txtContentViewHolder.setInitTextContentHeight(fourContent.getMeasuredHeight());
                    txtContentViewHolder.setItemFlag(false);
                    txtContentViewHolder.setVolunteer_rl_content(volunteer_content_four);
                    txtContentViewHolder.setRl_scroll(rl_scroll_four);
                    break;
            }
            flagMaps.put(i,txtContentViewHolder);
            shortHeight=getShortHeight();
            oneContent.getLayoutParams().height=shortHeight;
            oneContent.setVisibility(View.GONE);
            twoContent.getLayoutParams().height=shortHeight;
            twoContent.setVisibility(View.GONE);
            threeContent.getLayoutParams().height=shortHeight;
            threeContent.setVisibility(View.GONE);
            fourContent.getLayoutParams().height=shortHeight;
            fourContent.setVisibility(View.GONE);
            oneContent.requestLayout();
            twoContent.requestLayout();
            threeContent.requestLayout();
            fourContent.requestLayout();

            int itemInitHeight=getMeasureHeight(fiveView);//进行计算出除去内容的整个布局的高度
            if(leftHeight>=textItemCount*itemInitHeight){
                //
                scroll_line.setVisibility(View.INVISIBLE);
            }else{
                scroll_line.setVisibility(View.VISIBLE);
            }
            Log.d(TAG, "initAllTextView rl_scroll_oneHeight:" + rl_scroll_one.getMeasuredHeight()+"..itemInitHeight:"+itemInitHeight+"...rl_scroll_twoHeight:"+rl_scroll_two.getMeasuredHeight());
            Log.d(TAG, "initAllTextView rl_scroll_threeHeight:" + rl_scroll_three.getMeasuredHeight()+"..scrollView height:"+scrollView.getMeasuredHeight()+"...rl_scroll_fourHeight:"+rl_scroll_four.getMeasuredHeight());
        }
    }


    /**
     * 进行获取测量View的高度
     * @param view
     * @return
     */
    int getMeasureHeight(View view){
        int widthMeasure=View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightMeasure=View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        System.out.println("getMeasureHeight view is null:"+(view==null));
        view.measure(widthMeasure,heightMeasure);
        return view.getMeasuredHeight();
    }
    @Override
    protected void initInitevnts() {
        volunteer_content_one.setOnClickListener(this);
        volunteer_content_two.setOnClickListener(this);
        volunteer_content_three.setOnClickListener(this);
        volunteer_content_four.setOnClickListener(this);
        rl_back.setOnClickListener(this);
        onLineTrain.setOnClickListener(this);
        applyVolunteer.setOnClickListener(this);

        arrowOne.setOnClickListener(this);
        arrowTwo.setOnClickListener(this);
        arrowThree.setOnClickListener(this);
        arrowFour.setOnClickListener(this);

    }

    boolean oneContentFlag,twoContentFlag,threeContentFlag,fourContentFlag;
    int currentClickState;//针对固定志愿者答案条目的数字标识
    /**
     * 实现按钮的点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //点第一个按钮
            case R.id.volunteer_content_one:
                currentClickState =index_zero;
                //先进行查看其它有无关闭,如果没有关闭的,进行关闭
                preProcessOthers(currentClickState);
                break;

            //点第二个按钮
            case R.id.volunteer_content_two:
                currentClickState =index_one;
                preProcessOthers(currentClickState);
                break;

            //点第三个按钮
            case R.id.volunteer_content_three:
                currentClickState =index_two;
                preProcessOthers(currentClickState);
                break;

            //点第四个按钮
            case R.id.volunteer_content_four:
                currentClickState =index_three;
                preProcessOthers(currentClickState);
                break;
            //进行ImageButton上箭头设置的监听
            case R.id.volunteer_arrow_one:
                currentClickState =index_zero;
                preProcessOthers(currentClickState);
                break;
            case R.id.volunteer_arrow_two:
                currentClickState =index_one;
                preProcessOthers(currentClickState);
                break;
            case R.id.volunteer_arrow_three:
                currentClickState =index_two;
                preProcessOthers(currentClickState);
                break;
            case R.id.volunteer_arrow_four:
                currentClickState =index_two;
                preProcessOthers(currentClickState);
                break;





            //退出本页
            case R.id.rl_back:
                finish();
                break;

            //在线培训
            case R.id.volunteer_on_line:

                break;

            //申请成为志愿者
            case R.id.iv_volunteer_want_sub:
                startActivity(new Intent(VolunteerSignUpActivity.this,VolunteerSignUpFormActivity.class));
                break;
        }
    }

    /**
     * 进行预处理其它currentClickState的状态
     * @param currentClickState
     */
    private void preProcessOthers(int currentClickState){
        for(Map.Entry<Integer,ViewHolder> entry:flagMaps.entrySet()){
            if(entry.getKey()!=currentClickState){
                if(entry.getValue().isItemFlag()){
                    //如果已经展开,需要关闭
                    indexMaps.put(entry.getKey(),0);
                }
            }
        }

        if(indexMaps.isEmpty()){
            processTextContent(flagMaps.get(currentClickState).getContentTextView(),currentClickState,flagMaps.get(currentClickState).getArrowBtn());
        }else{
            for(Map.Entry<Integer,Integer> entry:indexMaps.entrySet()){
                processTextContent(flagMaps.get(entry.getKey()).getContentTextView(),entry.getKey(),flagMaps.get(entry.getKey()).getArrowBtn());
            }
        }
    }


    /**
     * 进行处理答案文本内容
     */
    private void processTextContent(TextView oneContent,int index,ImageButton arrowBtn) {
        int startHeight=0,endHeight=0;
        if (flagMaps.get(index).isItemFlag()){//为true时需要关闭，已经展开
            startHeight=getLongHeight(index);
            endHeight=getShortHeight();
        }else{//为false时需要展开,当前下拉状态为关闭
            startHeight=getShortHeight();
            endHeight=getLongHeight(index);
        }
        startValueAnimator(startHeight, endHeight, oneContent, index, arrowBtn);
    }

    /**
     * 第一个布局动画函数
     * @param startHeight
     * @param endHeight
     */
    private void startValueAnimator(int startHeight, final int endHeight,final TextView textContent, final int index,final ImageButton arrowBtn) {
        ValueAnimator valueAnimator= ValueAnimator.ofInt(startHeight, endHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //进行更新
                textContent.getLayoutParams().height=(Integer)valueAnimator.getAnimatedValue();
                textContent.requestLayout();
                scrollView.requestLayout();
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                textContent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!flagMaps.get(index).isItemFlag()) {
                    //状态已经展开
                    arrowBtn.setBackgroundResource(R.drawable.volunteer_arrow_up_selector);
                    scrollView.smoothScrollBy(0, flagMaps.get(index).getRl_scroll().getTop());
                    if(index==currentClickState){
                        //如果最后一个展开的为当前需要展开的,进行重新清除indexMaps
                        indexMaps.clear();
                    }
                }else{
                    //状态已经关闭
                    Log.d(TAG, "onAnimationEnd 条目关闭 index:" + index + "..top:" + flagMaps.get(index).getRl_scroll().getTop());
                    if(indexMaps.containsKey(index)){
                        Log.d(TAG, "onAnimationEnd 条目关闭 indexMaps put index:" + index + "..top:" + flagMaps.get(index).getRl_scroll().getTop());
                        indexMaps.put(index, flagMaps.get(index).getRl_scroll().getTop());
                        Message message=Message.obtain();
                        message.what=MESSAGE_OPEN_ITEM_WHAT;
                        handler.sendMessage(message);
                    }
                    textContent.setVisibility(View.GONE);
                    arrowBtn.setBackgroundResource(R.drawable.volunteer_arrow_selector);
                }
                //状态进行重新设置
                flagMaps.get(index).setItemFlag(!flagMaps.get(index).isItemFlag());

                int rl_scroll_height=flagMaps.get(index).getRl_scroll().getMeasuredHeight();
                Log.d(TAG,"scrollView MeasureHeight:"+scrollView.getMeasuredHeight()+"...layoutParams height:"+scrollView.getLayoutParams().height+"...index:"+index+"..rl_scroll_height:"+rl_scroll_height+"..lleftHeight:"+leftHeight);
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

    /**
     * 进行获取长的textView的高度
     * @return
     * @param
     */
    public int getLongHeight(int index) {
       return flagMaps.get(index).getInitTextContentHeight();
    }

    /**
     * 进行获取短的textView的高度
     * @return
     */
    public int getShortHeight() {
        TextView longTextView=new TextView(UiUtils.getInstance().getContext());
        longTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        longTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        longTextView.setLineSpacing(0f,1.25f);
        longTextView.setLines(0);
        int width=longTextView.getMeasuredWidth();
        int widthMeasureSpec=View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightMeasureSpec= View.MeasureSpec.makeMeasureSpec(20000, View.MeasureSpec.AT_MOST);
        longTextView.measure(widthMeasureSpec,heightMeasureSpec);
        return longTextView.getMeasuredHeight();
    }

    /**
     * 组件的封装类
     */
    class ViewHolder{
        RelativeLayout volunteer_rl_content,rl_scroll;//所有的答案条目的外面总布局
        TextView contentTextView;//内容的TextView
        ImageButton arrowBtn;//箭头的ImageBtn
        boolean itemFlag;//每个条目的展开情况
        int initTextContentHeight;


        public ViewHolder(RelativeLayout volunteer_rl_content, TextView contentTextView, ImageButton arrowBtn) {
            this.volunteer_rl_content = volunteer_rl_content;
            this.contentTextView = contentTextView;
            this.arrowBtn = arrowBtn;
        }

        public ViewHolder() {
        }

        public RelativeLayout getVolunteer_rl_content() {
            return volunteer_rl_content;
        }

        public void setVolunteer_rl_content(RelativeLayout volunteer_rl_content) {
            this.volunteer_rl_content = volunteer_rl_content;
        }

        public TextView getContentTextView() {
            return contentTextView;
        }

        public void setContentTextView(TextView contentTextView) {
            this.contentTextView = contentTextView;
        }

        public ImageButton getArrowBtn() {
            return arrowBtn;
        }

        public void setArrowBtn(ImageButton arrowBtn) {
            this.arrowBtn = arrowBtn;
        }

        public boolean isItemFlag() {
            return itemFlag;
        }

        public void setItemFlag(boolean itemFlag) {
            this.itemFlag = itemFlag;
        }

        public int getInitTextContentHeight() {
            return initTextContentHeight;
        }

        public void setInitTextContentHeight(int initTextContentHeight) {
            this.initTextContentHeight = initTextContentHeight;
        }

        public RelativeLayout getRl_scroll() {
            return rl_scroll;
        }

        public void setRl_scroll(RelativeLayout rl_scroll) {
            this.rl_scroll = rl_scroll;
        }
    }
}

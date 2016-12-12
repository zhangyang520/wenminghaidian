package com.zhjy.hdcivilization.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.utils.DisplayUtil;
import com.zhjy.hdcivilization.utils.UiUtils;

/**
 * Created by zhangyang on 2016/8/5.
 */
public class SuperviseProgressView extends RelativeLayout{

    private String TAG="SuperviseProgressView....";
    private int type=-2;

    //整体的布局(按钮和文字)
    private RelativeLayout rl_progress1;
    private RelativeLayout rl_progress2;
    private RelativeLayout rl_progress3;

    //进度条的线
    private TextView supervise_tv_progress_1;
    private TextView supervise_tv_progress_2;

    private Point txt_point1=new Point();//第一个状态的文本宽度
    private Point txt_point2=new Point();//第二个状态的文本宽度
    private Point txt_point3=new Point();//第三个状态的文本宽度

    private Context context;

    private int btnWithAndHeight;//圆圈的宽和高
    private int btnMargin;//线条和圆圈之间的margin值

    private String txtStatusContent1;//状态的文字
    private String txtStatusContent2;//状态的文字
    private String txtStatusContent3;//状态的文字

    private TextView textViewStatus1;//状态1的textView
    private TextView textViewStatus2;//状态2的textView
    private TextView textViewStatus3;//状态3的textView

    //文本和按钮组团的宽和高封装
    private Point group1TextBtnPoint=new Point();
    private Point group2TextBtnPoint=new Point();
    private Point group3TextBtnPoint=new Point();

    //进行设置坐标
    int x1,y1,x2,y2,x3,y3,x4,y4,x5,y5,x6,y6,x7,y7,x8,y8,x9,y9,x10,y10;
    //线的宽度和高度
    int lineWidth,lineHeight,lineMarginTop;
    int lineCount=2;
    public SuperviseProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }

    public SuperviseProgressView(Context context) {
        super(context);
        this.context=context;
    }

    public SuperviseProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("ProgressViewon Measure","onMeasure type:"+type);

        //进行初始化btnWithAndHeight
        btnWithAndHeight=(int)context.getResources().getDimension(R.dimen.btn_round_width);
        btnMargin=(int)context.getResources().getDimension(R.dimen.btn_margin);
//        lineMarginTop=(int)context.getResources().getDimension(R.dimen.line_margintop);
        lineMarginTop=btnWithAndHeight/2;
        lineHeight=(int)context.getResources().getDimension(R.dimen.supervise_progress_line_height);

        //进行初始化布局
        rl_progress1=(RelativeLayout)findViewById(R.id.rl_progress1);
        rl_progress2=(RelativeLayout)findViewById(R.id.rl_progress2);
        rl_progress3=(RelativeLayout)findViewById(R.id.rl_progress3);

        supervise_tv_progress_1=(TextView)findViewById(R.id.supervise_tv_progress_1);
        supervise_tv_progress_2=(TextView)findViewById(R.id.supervise_tv_progress_2);
        //根据状态设置文本内容
        setTextContentByStatus(type);

        //进行设置文本长和宽
        getTxtWidthAndHeight(txtStatusContent1,txt_point1);
        getTxtWidthAndHeight(txtStatusContent2, txt_point2);
        getTxtWidthAndHeight(txtStatusContent3,txt_point3);

        //进行初始化组团的宽和高
        initGroupPoint();
//        Log.d(TAG,"width1:"+txt_width1.x+"..height1:"+txt_width1.y);
//        Log.d(TAG,"width2:"+txt_width2.x+"..height2:"+txt_width2.y);
//        Log.d(TAG,"width3:"+txt_width3.x+"..height3:"+txt_width3.y);

        System.out.println("SuperviseProgressView onMeasure getX():"+getX());
        //最后设置父类的高度
        int measuerWidth= DisplayUtil.getScreenPoint(context).x-(int)(4*context.getResources().getDimension(R.dimen.edge_padding_left));
        int height1= (group1TextBtnPoint.y >= group2TextBtnPoint.y)?group1TextBtnPoint.y :group2TextBtnPoint.y;
        int maxheight=height1>=group3TextBtnPoint.y?height1:group3TextBtnPoint.y;


        //进行获取线的宽度
        lineWidth=(measuerWidth-((group1TextBtnPoint.x/2+btnWithAndHeight/2)+//
                                                         4*btnMargin+btnWithAndHeight+//
                                                                    (group3TextBtnPoint.x/2+btnWithAndHeight/2)))/lineCount;
        Log.d(TAG,"lineWidth:"+lineWidth+"...measuerWidth:"+measuerWidth);
        //第一个位置
        x1=0;
        y1=0;
        x2=group1TextBtnPoint.x;
        y2=maxheight;
        //第二个位置
        x3=(group1TextBtnPoint.x/2+btnWithAndHeight/2+btnMargin);
        y3=lineMarginTop;
        x4=x3+lineWidth;
        y4=y3+lineHeight;
        //第三个位置
        x5=x4-(group2TextBtnPoint.x/2-btnWithAndHeight/2-btnMargin);
        y5=0;
        x6=x5+group2TextBtnPoint.x;
        y6=y5+maxheight;
        //第四个位置
        x7=x6-(group2TextBtnPoint.x/2-btnWithAndHeight/2-btnMargin);
        y7=lineMarginTop;
        x8=x7+lineWidth;
        y8=y7+lineHeight;
        //第五个位置
        x9=x8-(group3TextBtnPoint.x/2-btnWithAndHeight/2-btnMargin);
        y9=0;
        x10=x9+group3TextBtnPoint.x;
        y10=y9+maxheight;
        setMeasuredDimension(measuerWidth,maxheight);
    }

    /**
     * 进行初始化组团的宽和高
     */
    private void initGroupPoint() {
        if(txt_point1.x>btnWithAndHeight){
            group1TextBtnPoint.x=txt_point1.x;
        }else{
            group1TextBtnPoint.x=btnWithAndHeight;
        }
        group1TextBtnPoint.y=txt_point1.y+btnWithAndHeight;

        if(txt_point2.x>btnWithAndHeight){
            group2TextBtnPoint.x=txt_point2.x;
        }else{
            group2TextBtnPoint.x=btnWithAndHeight;
        }
        group2TextBtnPoint.y=txt_point2.y+btnWithAndHeight;

        if(txt_point3.x>btnWithAndHeight){
            group3TextBtnPoint.x=txt_point3.x;
        }else{
            group3TextBtnPoint.x=btnWithAndHeight;
        }
        group3TextBtnPoint.y=txt_point3.y+btnWithAndHeight;
    }

    /**
     * 进行根据类型设置内容
     * @param type
     */
    private void setTextContentByStatus(int type) {
        switch (type) {
            case 0:
                txtStatusContent1="已上报";
                txtStatusContent2="未受理";
                txtStatusContent3="不通过";
                break;

            case 1:
                txtStatusContent1="已上报";
                txtStatusContent2="已受理";
                txtStatusContent3="已通过";
                break;

            case 2:
                txtStatusContent1="已上报";
                txtStatusContent2="已受理";
                txtStatusContent3="不通过";
                break;

            case 3:
                txtStatusContent1="已上报";
                txtStatusContent2="待复核";
                txtStatusContent3="待复核";
                break;

            case 4:
                txtStatusContent1="已上报";
                txtStatusContent2="待复核";
                txtStatusContent3="复核通过";
                break;

            case 5:
                txtStatusContent1="已上报";
                txtStatusContent2="待复核";
                txtStatusContent3="复核不通过";
                break;

            default:
                txtStatusContent1="已上报";
                txtStatusContent2="待复核";
                txtStatusContent3="复核不通过";
                break;
        }
        textViewStatus1.setText(txtStatusContent1);
        textViewStatus2.setText(txtStatusContent2);
        textViewStatus3.setText(txtStatusContent3);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d("ProgressViewon onLayout", "onMeasure type:" + type);

        Log.d("rl_progress1 onLayout","x1:"+x1+"..y1:"+y1+"...x2:"+x2+"...y2:"+y2);
//        rl_progress1=(RelativeLayout)findViewById(R.id.rl_progress1);
        rl_progress1.layout(x1, y1, x2, y2);
//        rl_progress2=(RelativeLayout)findViewById(R.id.rl_progress2);
        Log.d("supervise_tv_progress_1","x3:"+x3+"..y3:"+y3+"...x4:"+x4+"...y4:"+y4);
        supervise_tv_progress_1.layout(x3,y3,x4,y4);
//        rl_progress3=(RelativeLayout)findViewById(R.id.rl_progress3);
        rl_progress2.layout(x5,y5,x6,y6);
        Log.d("rl_progress2 onLayout", "x5:" + x5 + "..y5:" + y5 + "...x6:" + x6 + "...y6:" + y6);
//        supervise_tv_progress_1=(TextView)findViewById(R.id.supervise_tv_progress_1);
        supervise_tv_progress_2.layout(x7,y7,x8,y8);
        Log.d("supervise_tv_progress_2", "x7:" + x7 + "..y7:" + y7 + "...x8:" + x8 + "...y8:" + y8);
//        supervise_tv_progress_2=(TextView)findViewById(R.id.supervise_tv_progress_2);
        rl_progress3.layout(x9,y9,x10,y10);
        Log.d("rl_progress3", "x9:" + x9 + "..y9:" + y9 + "...x10:" + x10 + "...y10:" + y10);
    }

    /**
     * 进行返回文本的宽度和高度
     * @param txtContent
     * @return
     */
    private void getTxtWidthAndHeight(String txtContent,Point point){

        int widthSpec= MeasureSpec.makeMeasureSpec(20000,MeasureSpec.AT_MOST);
        int heightSpec= MeasureSpec.makeMeasureSpec(20000,MeasureSpec.AT_MOST);
        TextView txt=new TextView(context);
        txt.setText(txtContent);
        txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        txt.measure(widthSpec, heightSpec);
        point.x=txt.getMeasuredWidth();
        point.y=txt.getMeasuredHeight();
    }

    public TextView getTextViewStatus1() {
        return textViewStatus1;
    }

    public void setTextViewStatus1(TextView textViewStatus1) {
        this.textViewStatus1 = textViewStatus1;
    }

    public TextView getTextViewStatus2() {
        return textViewStatus2;
    }

    public void setTextViewStatus2(TextView textViewStatus2) {
        this.textViewStatus2 = textViewStatus2;
    }

    public TextView getTextViewStatus3() {
        return textViewStatus3;
    }

    public void setTextViewStatus3(TextView textViewStatus3) {
        this.textViewStatus3 = textViewStatus3;
    }
}

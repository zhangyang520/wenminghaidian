package com.zhjy.hdcivilization.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.ImgEntityDao;
import com.zhjy.hdcivilization.dao.SuperviseMySubListDao;
import com.zhjy.hdcivilization.entity.HDC_SuperviseMySubList;
import com.zhjy.hdcivilization.entity.ImgEntity;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.utils.BitmapUtil;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.MaskView;
import com.zhjy.hdcivilization.view.SuperviseProgressView;

import java.net.ConnectException;
import java.util.List;

/**
 * Created by zhangyang on 2016/9/17.
 */
public class MySuperviseListDetailActivity extends BaseActivity{

    private ImageView btn_back;
    private TextView tv_event_content;//事件类型内容:“道路破损”+“小广告”
    private TextView tv_time_content;//上报时间 "08-29 1:10"
    private TextView  tv_address_content;//详细地址的内容
    private TextView tv_belong_street_content;//所属街道
    private TextView tv_event_des_content;//事件描述
    private LinearLayout ll_image;//包含图片的集合
    private ImageView image_one,image_two,image_three;
    private RelativeLayout rl_shenhe_error;//审核失败的错误
    private TextView tv_event_error_content;//审核失败原因

    TextView supervise_tv_progress_1,//进度条
             supervise_tv_progress_2;
    TextView supervise_tv_content_1,supervise_tv_content_2,supervise_tv_content_3;//进度描述信息
    SuperviseProgressView supervise_rl_2;
    RelativeLayout rl_progress1,rl_progress2,rl_progress3;

    ImageView  supervise_iv_progress_1,supervise_iv_progress_2,//颜色进度
               supervise_iv_progress_3;
    public static  String ITEM_ID_KEY="ITEM_ID_KEY";
    private String event_delegate="     ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contentView= UiUtils.getInstance().inflate(R.layout.activity_supervise_list_detail);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews(){

        //进行获取itemid
        String itemId=getIntent().getStringExtra(ITEM_ID_KEY);
        System.out.println("itemId:"+itemId);
        if(itemId!=null && !itemId.equals("")){
            try {
                HDC_SuperviseMySubList hdc_superviseMySubList=SuperviseMySubListDao.getInstance().getItemBy(itemId);
                System.out.println("MySuperviseListDetailActivity HDC_SuperviseMySubList initViews:"+hdc_superviseMySubList.toString());
                //通过itemId进行获取
                tv_event_content=(TextView)findViewById(R.id.tv_event_content);
                tv_time_content=(TextView)findViewById(R.id.tv_time_content);
                tv_belong_street_content=(TextView)findViewById(R.id.tv_belong_street_content);
                tv_address_content=(TextView)findViewById(R.id.tv_address_content);
                tv_event_des_content=(TextView)findViewById(R.id.tv_event_des_content);
                ll_image=(LinearLayout)findViewById(R.id.ll_image);

                //错误的原因
                rl_shenhe_error=(RelativeLayout)findViewById(R.id.rl_shenhe_error);
                tv_event_error_content=(TextView)findViewById(R.id.tv_event_error_content);

                //图片信息
                image_one=(ImageView)findViewById(R.id.image_one);
                image_two=(ImageView)findViewById(R.id.image_two);
                image_three=(ImageView)findViewById(R.id.image_three);
                btn_back=(ImageView)findViewById(R.id.btn_back);

                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                //自定义组件
                supervise_tv_progress_1=(TextView)findViewById(R.id.supervise_tv_progress_1);
                supervise_tv_progress_2=(TextView)findViewById(R.id.supervise_tv_progress_2);
                supervise_tv_content_1=(TextView)findViewById(R.id.supervise_tv_content_1);
                supervise_tv_content_2=(TextView)findViewById(R.id.supervise_tv_content_2);
                supervise_tv_content_3=(TextView)findViewById(R.id.supervise_tv_content_3);

                supervise_rl_2=(SuperviseProgressView)findViewById(R.id.supervise_rl_2);

                supervise_rl_2.setTextViewStatus1(supervise_tv_content_1);
                supervise_rl_2.setTextViewStatus2(supervise_tv_content_2);
                supervise_rl_2.setTextViewStatus3(supervise_tv_content_3);

                //相对布局实例
                rl_progress1=(RelativeLayout)findViewById(R.id.rl_progress1);
                rl_progress2=(RelativeLayout)findViewById(R.id.rl_progress2);
                rl_progress3=(RelativeLayout)findViewById(R.id.rl_progress3);

                //ImageView
                supervise_iv_progress_1=(ImageView)findViewById(R.id.supervise_iv_progress_1);
                supervise_iv_progress_2=(ImageView)findViewById(R.id.supervise_iv_progress_2);
                supervise_iv_progress_3=(ImageView)findViewById(R.id.supervise_iv_progress_3);

                //进行设置数据
                initViewData(hdc_superviseMySubList);
            } catch (ConnectException e) {
                e.printStackTrace();
                finish();
                System.out.println("itemId:" + itemId+"...ConnectException");
            }
        }else{
            finish();
            System.out.println("itemId: is null:"+itemId);
        }
    }


    /**
     * 进行初始化数据
     * @param hdc_superviseMySubList
     */
    private void initViewData(final HDC_SuperviseMySubList hdc_superviseMySubList){
        //进行初始化数据
        tv_event_content.setText(hdc_superviseMySubList.getFirstEventType()+//
                                    event_delegate+hdc_superviseMySubList.getSecondEventType());

        System.out.println("hdc_superviseMySubList.getPublishTime():"+hdc_superviseMySubList.getPublishTime());
        //提交时间
        tv_time_content.setText(""+hdc_superviseMySubList.getPublishTime());

        //地址
        tv_address_content.setText(""+hdc_superviseMySubList.getAddress());

        //事件描述
        tv_event_des_content.setText(""+hdc_superviseMySubList.getDescription());

        System.out.println("hdc_superviseMySubList.getStreetBelong():" + hdc_superviseMySubList.getStreetBelong());
        tv_belong_street_content.setText("" + hdc_superviseMySubList.getStreetBelong());

        System.out.println("hdc_superviseMySubList.getItemIdAndType():" + hdc_superviseMySubList.getItemIdAndType());
        //进行展示图片
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //进行查找图片
                List<ImgEntity> datas =hdc_superviseMySubList.getImgEntity();
                if (datas != null && datas.size() > 0) {
                    System.out.println("datas imageList:"+datas.size());
                    ll_image.setVisibility(View.VISIBLE);
                    int leftWidth = UiUtils.getInstance().getDefaultWidth() -//
                            2 * UiUtils.getDimen(R.dimen.edge_padding_left) -//
                            2 * UiUtils.getDimen(R.dimen.supervise_image_magin_left);

                    int imageWidth = leftWidth / 3;
                    image_one.getLayoutParams().width = imageWidth;
                    image_one.getLayoutParams().height = Math.round(imageWidth * MaskView.heghtDividWidthRate);
                    image_two.getLayoutParams().width = imageWidth;
                    image_two.getLayoutParams().height = Math.round(imageWidth * MaskView.heghtDividWidthRate);
                    image_three.getLayoutParams().width = imageWidth;
                    image_three.getLayoutParams().height = Math.round(imageWidth * MaskView.heghtDividWidthRate);
                    image_one.requestLayout();
                    image_two.requestLayout();
                    image_three.requestLayout();

                    //进行显示图片
                    switch (datas.size()) {
                        case 1:
                            image_one.setVisibility(View.VISIBLE);
                            System.out.println("datas.get(0).getImgUrl():" + datas.get(0).getImgUrl());
                            BitmapUtil.getInstance().displayImg(image_one, datas.get(0).getImgUrl());
                            image_two.setVisibility(View.INVISIBLE);
                            image_three.setVisibility(View.INVISIBLE);
                            break;

                        case 2:
                            image_one.setVisibility(View.VISIBLE);
                            System.out.println("datas.get(0).getImgUrl():" + datas.get(0).getImgUrl());
                            BitmapUtil.getInstance().displayImg(image_one, datas.get(0).getImgUrl());
                            image_two.setVisibility(View.VISIBLE);
                            BitmapUtil.getInstance().displayImg(image_two, datas.get(1).getImgUrl());
                            System.out.println("datas.get(1).getImgUrl():" + datas.get(1).getImgUrl());
                            image_three.setVisibility(View.INVISIBLE);
                            break;

                        case 3:
                            image_one.setVisibility(View.VISIBLE);
                            BitmapUtil.getInstance().displayImg(image_one, datas.get(0).getImgUrl());
                            System.out.println("datas.get(1).getImgUrl():" + datas.get(0).getImgUrl());
                            image_two.setVisibility(View.VISIBLE);
                            BitmapUtil.getInstance().displayImg(image_two, datas.get(1).getImgUrl());
                            System.out.println("datas.get(1).getImgUrl():" + datas.get(1).getImgUrl());
                            image_one.setVisibility(View.VISIBLE);
                            BitmapUtil.getInstance().displayImg(image_three, datas.get(2).getImgUrl());
                            System.out.println("datas.get(1).getImgUrl():" + datas.get(2).getImgUrl());
                            break;
                    }
                } else {
                    System.out.println("datas imageList:"+0);
                    ll_image.setVisibility(View.GONE);
                }

                //进行设置进度信息
                int viewType =1;
                try {
                    viewType=Integer.parseInt(hdc_superviseMySubList.getProcessState());
                    if(viewType>=HDCivilizationConstants.SUBMIT_TASK_STATUS_0 && //
                                        viewType<=HDCivilizationConstants.SUBMIT_TASK_STATUS_5){
                        supervise_rl_2.setType(viewType);
                    }else{
                        viewType=HDCivilizationConstants.SUBMIT_TASK_STATUS_DEFAULT;
                        supervise_rl_2.setType(viewType);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    viewType=HDCivilizationConstants.SUBMIT_TASK_STATUS_DEFAULT;
                    supervise_rl_2.setType(viewType);
                }

                //进行绑定TextView
                supervise_rl_2.setTextViewStatus1(supervise_tv_content_1);
                supervise_rl_2.setTextViewStatus2(supervise_tv_content_2);
                supervise_rl_2.setTextViewStatus3(supervise_tv_content_3);
                int widthSpeec=View.MeasureSpec.makeMeasureSpec(4000, View.MeasureSpec.AT_MOST);
                int heightSpeec=View.MeasureSpec.makeMeasureSpec(4000, View.MeasureSpec.AT_MOST);
                supervise_rl_2.measure(widthSpeec,heightSpeec);
                supervise_rl_2.requestLayout();

                //进行背景的赋值:
                supervise_iv_progress_1.setImageDrawable(UiUtils.getInstance().getDrawable(R.drawable.supervise_icon_progress_press));
                supervise_iv_progress_2.setImageDrawable(UiUtils.getInstance().getDrawable(R.drawable.supervise_icon_progress_press));
                supervise_iv_progress_3.setImageDrawable(UiUtils.getInstance().getDrawable(R.drawable.supervise_icon_progress_press));

                //进行颜色的赋值
                supervise_tv_content_1.setTextColor(UiUtils.getInstance().//
                        getContext().getResources().getColor(R.color.supervise_progress_color_press));
                supervise_tv_content_2.setTextColor(UiUtils.getInstance().//
                        getContext().getResources().getColor(R.color.supervise_progress_color_press));
                supervise_tv_content_3.setTextColor(UiUtils.getInstance().//
                        getContext().getResources().getColor(R.color.supervise_progress_color_press));

                //进度横线的颜色的赋值
                supervise_tv_progress_1.setBackgroundColor(UiUtils.getInstance().//
                        getContext().getResources().getColor(R.color.supervise_progress_color_press));
                supervise_tv_progress_2.setBackgroundColor(UiUtils.getInstance().//
                        getContext().getResources().getColor(R.color.supervise_progress_color_press));

                switch (viewType) {
                    case HDCivilizationConstants.SUBMIT_TASK_STATUS_0://未接受 已上报
                    case HDCivilizationConstants.SUBMIT_TASK_STATUS_3:

                        rl_progress3.setVisibility(View.INVISIBLE);
                        rl_progress1.setVisibility(View.VISIBLE);
                        rl_progress2.setVisibility(View.VISIBLE);

                        supervise_tv_progress_1.setVisibility(View.VISIBLE);
                        supervise_tv_progress_2.setVisibility(View.INVISIBLE);
                        break;

                    case HDCivilizationConstants.SUBMIT_TASK_STATUS_1://通过  受理
                    case HDCivilizationConstants.SUBMIT_TASK_STATUS_2://未通过 未受理
                    case HDCivilizationConstants.SUBMIT_TASK_STATUS_4://复核通过 完成
                    case HDCivilizationConstants.SUBMIT_TASK_STATUS_5:

                        rl_progress3.setVisibility(View.VISIBLE);
                        rl_progress1.setVisibility(View.VISIBLE);
                        rl_progress2.setVisibility(View.VISIBLE);


                        supervise_tv_progress_1.setVisibility(View.VISIBLE);
                        supervise_tv_progress_2.setVisibility(View.VISIBLE);
                        break;

                    default :
                        //进行缺省值的判断:
                        rl_progress3.setVisibility(View.INVISIBLE);
                        rl_progress1.setVisibility(View.VISIBLE);
                        rl_progress2.setVisibility(View.INVISIBLE);

                        supervise_tv_progress_1.setVisibility(View.INVISIBLE);
                        supervise_tv_progress_2.setVisibility(View.INVISIBLE);
                        break;
                }

                if(viewType==HDCivilizationConstants.SUBMIT_TASK_STATUS_2 ||
                                viewType==HDCivilizationConstants.SUBMIT_TASK_STATUS_5){
                    //类型为2或者5
                    rl_shenhe_error.setVisibility(View.VISIBLE);
                    tv_event_error_content.setText(hdc_superviseMySubList.getUnPassReason());
                }else{
                    rl_shenhe_error.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void initInitevnts() {

    }
}

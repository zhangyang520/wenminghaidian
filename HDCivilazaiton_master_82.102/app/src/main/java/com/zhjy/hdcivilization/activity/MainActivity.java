package com.zhjy.hdcivilization.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.CivistateDao;
import com.zhjy.hdcivilization.dao.MainNumberDao;
import com.zhjy.hdcivilization.dao.NoticeDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.AppInfo;
import com.zhjy.hdcivilization.entity.HDC_CiviState;
import com.zhjy.hdcivilization.entity.HDC_MainNumber;
import com.zhjy.hdcivilization.entity.HDC_Notice;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.holder.MainPagerHolder;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.progressbar.KProgressHUD;
import com.zhjy.hdcivilization.protocol.AppCheckProtocol;
import com.zhjy.hdcivilization.protocol.CiviStateListProtocol;
import com.zhjy.hdcivilization.protocol.MainNumberProtocol;
import com.zhjy.hdcivilization.protocol.NoticeProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.SDCardUtil;
import com.zhjy.hdcivilization.utils.ScreenUtil;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.utils.VersionUtil;
import com.zhjy.hdcivilization.view.AutoTextView;
import com.zhjy.hdcivilization.view.OKPopup;
import com.zhjy.hdcivilization.view.WarningPopup;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {
    private AutoTextView textView;
    private int index = 0;
    private CiviStateListProtocol civiStateListProtocol=new CiviStateListProtocol();
    private FrameLayout frameLayout;
    private String keyName = "首页";
    private MainPagerHolder mainPagerHolder = new MainPagerHolder();
    private RelativeLayout rl_main;
    private RelativeLayout notice;
    private Button btnMine,btnAutoState,btnComment,btnSupervise;
    private Button btnSuperviseNum,btnCommentNum,btnAutostateNum,btnMineNum;
    private List<HDC_Notice> noticeList = new ArrayList<HDC_Notice>();
    private HDC_MainNumber mainNumber = new HDC_MainNumber();
    private List<HDC_CiviState> pageAdapterDatas =new ArrayList<HDC_CiviState>();
    boolean isLogin = false;
    int noticeIndex=-1;//通知公告的角标
    private LinearLayout linearLayouts;
    final int MESSAGE_DISPEAR_NUMBER=2000;

    private final int CHECK_SUCCESS=301;//检测成功
    private final int CHECK_FAILURE=302;//检测成功
    private boolean isNotifyRequestEnd=true,isViewPagerRequestEnd=true,isNumberNotifyEnd=true;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HDCivilizationConstants.CILIVIZATION_NOTICE:
                      synchronized (MainActivity.this){
                          //同步操作
                          Bundle bundle=msg.getData();
                          if(bundle!=null){
                              int actoinCode=bundle.getInt(HDCivilizationConstants.ACTION_CODE);
                              switch (actoinCode){
                                  case HDCivilizationConstants.CILIVIZATION_NOTICE_REQUEST:
                                      //进行处理，数据库的清除,重新保存
                                      NoticeDao.getInstance().clearAll();
                                      noticeList.clear();
                                      if(((List<HDC_Notice>) msg.obj).size() <= 0){
                                          //从服务器端请求的数据为空:
                                          UiUtils.getInstance().showToast("尚未发布通知公告信息!");
                                          textView.setText("");
                                      }else{
                                          //不为空
                                          NoticeDao.getInstance().saveAllNoticeList(((List<HDC_Notice>) msg.obj));
                                          noticeList=getFirstFourList(((List<HDC_Notice>) msg.obj));
                                          noticeIndex=0;
                                      }
                                      isNotifyRequestEnd=true;
                                      break;

                                  case HDCivilizationConstants.CILIVIZATION_NOTICE_TIMER:
                                      //进行处理,进行到下一个数据
                                      if(noticeList.size()<=0){
                                         //如果集合的长度<0
                                          noticeIndex=0;
                                          textView.setText("");
                                      }else{
                                          textView.next();
                                          textView.setText(noticeList.get(((noticeIndex) % noticeList.size())).getTitle());
                                          noticeIndex++;
                                          textView.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  /******应界面跳转更改至详情页****/
                                                  if(noticeList.size()>0){
                                                      Intent intent=new Intent(MainActivity.this,NotioceActivity.class);
                                                      if(noticeIndex==0){
                                                          intent.putExtra(NotioceActivity.ITEM_ID_AND_TYPE,noticeList.get((noticeIndex) % noticeList.size()).getItemId());
                                                          startActivity(intent);
                                                      }else{
                                                          intent.putExtra(NotioceActivity.ITEM_ID_AND_TYPE,noticeList.get((noticeIndex-1) % noticeList.size()).getItemId());
                                                          startActivity(intent);
                                                      }
                                                  }
                                              }
                                          });
                                      }
                                      System.out.println("CILIVIZATION_NOTICE_TIMER noticeIndex:"+noticeIndex);
                                      break;
                              }
                          }
                      }
                    break;

                case HDCivilizationConstants.CILIVIZATION_VIEWPAGER_REQUEST:
                    isViewPagerRequestEnd=true;
                    if(((List<HDC_CiviState>)msg.obj).size()<=0){
                        UiUtils.getInstance().showToast("尚未发布文明动态信息!");
                        String userId="";
                        try {
                            User user= UserDao.getInstance().getLocalUser();
                            userId=user.getUserId();
                        } catch (ContentException e) {
                            e.printStackTrace();
                            userId="";
                        }
                        CivistateDao.getInstance().clearAllBy(userId);
                        pageAdapterDatas.clear();
                    }else{
                        String userId="";
                        try {
                            User user= UserDao.getInstance().getLocalUser();
                            userId=user.getUserId();
                        } catch (ContentException e) {
                            e.printStackTrace();
                            userId="";
                        }
                        CivistateDao.getInstance().clearAll();
                        CivistateDao.getInstance().saveAllCivistateList((List<HDC_CiviState>)msg.obj);
                        pageAdapterDatas = getFirstFourList((List<HDC_CiviState>)msg.obj);
                    }
                    mainPagerHolder.setDatas(pageAdapterDatas);
                    if(pageAdapterDatas.size()>0){
                        mainPagerHolder.start();
                    }else{
                        mainPagerHolder.stop();
                    }
                    View contentView1 = mainPagerHolder.getContentView();
                    if (contentView1.getParent()!= null){
                        ((ViewGroup)contentView1.getParent()).removeView(contentView1);
                    }
                    frameLayout.addView(contentView1);
                    break;

                case HDCivilizationConstants.CILIVIZATION_NUMBER_REQUEST:
                    isNumberNotifyEnd=true;
                        mainNumber =(HDC_MainNumber)msg.obj;
                    try {
                        User user= UserDao.getInstance().getLocalUser();
                        notifyNumberUI(mainNumber,user);
                    } catch (ContentException e) {
                        e.printStackTrace();
                        dispearAllNumber();
                    }
                    break;

                case HDCivilizationConstants.ERROR_CODE:
                    int actionCode=msg.getData().getInt(HDCivilizationConstants.ACTION_CODE);
                    if(actionCode== HDCivilizationConstants.CILIVIZATION_NUMBER_REQUEST){
                        //如果是消息提醒数字
                        isNumberNotifyEnd=true;
                    }else if (actionCode== HDCivilizationConstants.CILIVIZATION_VIEWPAGER_REQUEST) {
                        isViewPagerRequestEnd=true;
                    }else if(actionCode== HDCivilizationConstants.CILIVIZATION_NOTICE_REQUEST){
                        isNotifyRequestEnd=true;
                    }
                    UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                    break;

                case MESSAGE_DISPEAR_NUMBER:
                    dispearAllNumber();
                    break;


                case CHECK_SUCCESS://检测成功:进行提示用户
                    //进行提示用户
                    final AppInfo appInfo=(AppInfo)msg.obj;
                    //先检测版本大小
                    try {
                        boolean flag= VersionUtil.isLowerJsonApk(appInfo.getAppVersionCode());
                        if(flag){
                            //如果当前版本小于服务器端最新版本:进行提示用户
                            WarningPopup.getInstance().showPopup(contentView, new WarningPopup.BtnClickListener() {
                                @Override
                                public void btnOk(){
                                    WarningPopup.getInstance().dismiss();
                                    //普通用户
                                    final KProgressHUD hud2 = KProgressHUD.create(MainActivity.this)
                                            .setStyle(KProgressHUD.Style.BAR_DETERMINATE)
                                            .setLabel("应用下载中...")
                                            .setCancellable(false);
                                    hud2.setMaxProgress(100);
                                    HttpUtils httpUtil=new HttpUtils();
                                    httpUtil.configSoTimeout(HDCivilizationConstants.NEWWORK_TIME_OUT);
                                    httpUtil.configTimeout(HDCivilizationConstants.NEWWORK_TIME_OUT);
                                    long currentTime=System.currentTimeMillis();
                                    final String FilePathName= SDCardUtil.getInstance().getDownloadPath()+ File.separator+currentTime+".apk";
                                    final HttpHandler handler=httpUtil.download(appInfo.getAppUrl(), FilePathName, true, new RequestCallBack<File>() {
                                        @Override
                                        public void onSuccess(ResponseInfo<File> responseInfo) {
                                            hud2.dismiss();
                                            UiUtils.getInstance().showToast("应用下载成功");
                                            //进行提示框是否安装:
                                            WarningPopup.getInstance().showPopup(MainActivity.this.contentView, new WarningPopup.BtnClickListener() {
                                                @Override
                                                public void btnOk() {
                                                    WarningPopup.getInstance().dismiss();
                                                    VersionUtil.installApk(MainActivity.this, FilePathName);
                                                }

                                                @Override
                                                public void btnCancel() {
                                                    WarningPopup.getInstance().dismiss();
                                                }
                                            }, true, true, "是否需要安装该应用");
                                        }

                                        @Override
                                        public void onFailure(HttpException e, String s) {
                                            getLoaderManager().destroyLoader(getTaskId());
                                            UiUtils.getInstance().showToast("应用下载失败");
                                            hud2.dismiss();
                                        }

                                        @Override
                                        public void onStart() {
                                            super.onStart();
                                            hud2.show();
                                        }

                                        @Override
                                        public void onLoading(long total, long current, boolean isUploading) {
                                            super.onLoading(total, current, isUploading);
                                            double rate = ((double) current) / ((double) total);
                                            hud2.setProgress((int) (rate * 100));
                                        }
                                    });

                                    hud2.setCancalListener(new KProgressHUD.CancalListener() {
                                        @Override
                                        public void cancel() {
                                            handler.cancel();
                                            hud2.dismiss();
                                        }
                                    });
                                }

                                @Override
                                public void btnCancel(){
                                    //取消
                                    WarningPopup.getInstance().dismiss();
                                }
                            },true,true,"最新版本是:"+appInfo.getAppVersionCode()+",是否下载?");
                        }else{
//                            UiUtils.getInstance().showToast("已经是最新版本!");
                        }
                    } catch (ContentException e) {
                        e.printStackTrace();
                        UiUtils.getInstance().showToast(e.getErrorContent());
                    }
                    break;

                case CHECK_FAILURE://检测失败:提示用户
                    UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                    break;
            }
            super.handleMessage(msg);
        }
    };


    /**
     * 进行获取前四个 datas集合的元素
     * @param datas
     * @return
     */
    private <T> List<T> getFirstFourList(List<T> datas) {
        List<T> tempDatas=new ArrayList<T>();
        if(datas.size()>0){
            if(datas.size()<=4){
                return datas;
            }else{
                for(int i=0;i< HDCivilizationConstants.MAIN_NOTICE_COUNT;++i){
                    tempDatas.add(datas.get(i));
                }
            }
        }
        return tempDatas;
    }

    /**
     * 重新修改
     * 需要进行权限的判断:
     *     如果是:< 用户权限: 进行退出登录:
     *            >=用户权限:
     *               1：志愿者权限:显示文明监督消息提示个数！
     *               2:显示文明动态,文明评论消息提示个数
     *
     *     否则:什么也不显示:
     * @param list
     */
    private void notifyNumberUI(HDC_MainNumber list,User user) {
           /***提示数字***/
          int currentState=Integer.parseInt(user.getIdentityState());
          int ordinaryState=Integer.parseInt(UserPermisson.ORDINARYSTATE.getType());
          int superviseState=Integer.parseInt(UserPermisson.VOLUNTEER.getType());
          if(currentState<//如果小于普通用户
                        ordinaryState){
              //那么什么也不显示
              UserDao.getInstance().updateAllUserLocalState(false);
              //消失所有的数字提示
              dispearAllNumber();
          }else{
              if(currentState==superviseState && list.getSuperviseCount()>0){//文明监督的个数
                  btnSuperviseNum.setVisibility(View.VISIBLE);
                  btnSuperviseNum.setText(list.getSuperviseCount() + "");
              }else{
                  //否则不显示
                  btnSuperviseNum.setVisibility(View.GONE);
              }

              if(list.getCommentCount()>0){//文明评论的个数
                  btnCommentNum.setText(list.getCommentCount()+"");
                  btnCommentNum.setVisibility(View.VISIBLE);
              }else{
                  //否则不显示
                  btnCommentNum.setVisibility(View.GONE);
              }

              if(list.getStateCount()>0){//进行获取文明动态的数据
                  btnAutostateNum.setText(list.getStateCount()+"");
                  btnAutostateNum.setVisibility(View.VISIBLE);
              }else{
                  btnAutostateNum.setVisibility(View.GONE);
              }

              //进行我的模块的显示
              if(currentState==superviseState){
                  //志愿者用户: 相加显示
                  if(list.getSuperviseCount()+list.getNotifyCount()>0){
                      btnMineNum.setText(list.getSuperviseCount()+list.getNotifyCount()+"");
                      btnMineNum.setVisibility(View.VISIBLE);
                  }else{
                      btnMineNum.setVisibility(View.GONE);
                  }
              }else{
                  //只显示通知公告个数
                  if(list.getNotifyCount()>0){
                      btnMineNum.setText(list.getNotifyCount()+"");
                      btnMineNum.setVisibility(View.VISIBLE);
                  }else{
                      btnMineNum.setVisibility(View.GONE);
                  }
              }

          }
    }

    private void dispearAllNumber() {
        btnSuperviseNum.setVisibility(View.GONE);
        btnCommentNum.setVisibility(View.GONE);
        btnAutostateNum.setVisibility(View.GONE);
        btnMineNum.setVisibility(View.GONE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        contentView= UiUtils.getInstance().inflate(R.layout.activity_main);
        super.onCreate(savedInstanceState);
//        requestPermission(HDCivilizationConstants.READ_EXTERNAL_STORAGE_REQUEST_CODE, "android.permission.READ_EXTERNAL_STORAGE", null, new Runnable() {
//            @Override
//            public void run() {
//                UiUtils.getInstance().showToast("请查看读写存储卡权限!");
//            }
//        });
    }

    /**
     * 进行重新启动
     */
    @Override
    protected void onRestart(){
        super.onRestart();
//        requestPermission(HDCivilizationConstants.READ_EXTERNAL_STORAGE_REQUEST_CODE, "android.permission.READ_EXTERNAL_STORAGE", null, new Runnable() {
//            @Override
//            public void run() {
//                UiUtils.getInstance().showToast("请查看读写存储卡权限!");
//            }
//        });
        if(isViewPagerRequestEnd){
            getViewPagerForServer();
        }
        if(isNotifyRequestEnd){
            getNoticeForServer();
        }

        if(isNumberNotifyEnd){
            getNumberForServer();
        }
        //重新进行初始化数字:
        try {
            User user= UserDao.getInstance().getLocalUser();
            HDC_MainNumber hdc_mainNumber= MainNumberDao.getInstance().getNumberBy(user.getUserId());
            notifyNumberUI(hdc_mainNumber, user);
        } catch (ContentException e) {
            e.printStackTrace();
            //未登录
            dispearAllNumber();
        }
    }


    //定义变量
    int screenWidth,btnLeftHeight;//屏幕宽度，按钮的高度
    int supervise_radius;//文明监督的半径
    //角度
    final float comment_jiaodu=(float)((35.0/180.0)*Math.PI);
    final float state_jiaodu=(float)((16.0/180.0)*Math.PI);
    final float mine_jiaodu=(float)((20.0/180.0)*Math.PI);
    final float red_number_jiaodu=(float)((30.0/180.0)*Math.PI);

    //文明评论,文明动态,我的与"文明监督"之间的半径比例关系
    float comment_radius_rate=0.8f;//文明评论半径与文明监督半径关系比例
    float comment_radius_rate_left=0.9f;//文明评论半径与文明监督半径交叉比例
    float state_radius_rate=0.95f;//文明动态半径与文明监督半径关系比例
    float state_radius_rate_left=0.9f;//文明动态半径与文明监督半径交叉比例
    float mine_radius_rate=0.6f;//我的半径与文明监督半径关系比例
    float mine_radius_rate_left=0.9f;//我的半径与文明监督半径交叉比例

    int redNumberRadius=UiUtils.getDimen(R.dimen.bth_red_number_width);
    //横向和属性空白之间的比例
    float rate_x_1=0.06f,rate_x_2=0.06f,//
            rate_y_1=0.25f,rate_y_2=0.15f;

    float rate_supervise_radius=0.1f;//文明监督背景的比例/实际半径
    float rate_supervise_background_width_height_1=0.9533f;
    float radius_supervise,radius_supervise_out;
    RelativeLayout rl_supervise_background,rl_comment,rl_mine,rl_civilization_mine,rl_civi_state,rl_civilization_supervise,rl_top;
    @Override
    protected void initViews() {
        textView = (AutoTextView)findViewById(R.id.switcher02);
        frameLayout = (FrameLayout)findViewById(R.id.frame_layout);
        rl_main =(RelativeLayout)findViewById(R.id.main_rl);
        linearLayouts = (LinearLayout)findViewById(R.id.linearlayout);
        rl_supervise_background=(RelativeLayout)findViewById(R.id.rl_supervise_background);
        rl_comment=(RelativeLayout)findViewById(R.id.rl_comment);
        rl_mine=(RelativeLayout)findViewById(R.id.rl_mine);
        rl_civilization_mine=(RelativeLayout)findViewById(R.id.rl_civilization_mine);
        rl_civi_state=(RelativeLayout)findViewById(R.id.rl_civi_state);
        rl_civilization_supervise=(RelativeLayout)findViewById(R.id.rl_civilization_supervise);
        rl_top=(RelativeLayout)findViewById(R.id.rl_top);
//        System.out.println("radius_supervise scressWidth:"+ UiUtils.getInstance().getDefaultWidth());
        linearLayouts.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                linearLayouts.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                WindowManager windowManager = (WindowManager) UiUtils.getInstance().getContext().getSystemService(Context.WINDOW_SERVICE);
                int scressWidth = windowManager.getDefaultDisplay().getWidth();
                frameLayout.getLayoutParams().height = ScreenUtil.getInstance().getHeight(UiUtils.getInstance().getContext(),1120,736);
                frameLayout.requestLayout();
                rl_main.getLayoutParams().height = (int)(scressWidth / (1.07f));
                rl_main.requestLayout();

//                System.out.println("btn_supervise_number height:"+btnSuperviseNum.getMeasuredHeight()+"..width:"+btnSuperviseNum.getMeasuredWidth());
                //进行获取main_rl的高度
                int main_rl_height=0;
                if(UiUtils.getInstance().getDefaultHeight()-rl_top.getMeasuredHeight()-rl_main.getLayoutParams().height-frameLayout.getLayoutParams().height<0 || //
                                                    UiUtils.getInstance().getDefaultHeight()-rl_top.getMeasuredHeight()-rl_main.getLayoutParams().height-frameLayout.getLayoutParams().height>0){
                    main_rl_height= UiUtils.getInstance().getDefaultHeight()-rl_top.getMeasuredHeight()-frameLayout.getLayoutParams().height;
//                    System.out.println("radius_supervise ...main_rl_height----:"+main_rl_height);
                }else{
                    main_rl_height= rl_main.getLayoutParams().height;
                }
//                System.out.println("radius_supervise main_rl_height:"+main_rl_height+//
//                                                "..rl_main.getLayoutParams().height:"+rl_main.getLayoutParams().height+//
//                                                                                     "...frame_layout:"+frameLayout.getLayoutParams().height+//
//                                                                                                        "..linearLayouts:"+linearLayouts.getMeasuredHeight()+"..screenHeight:"+ UiUtils.getInstance().getDefaultHeight()+"...rl_top--measureHeight:"+rl_top.getMeasuredHeight());
                //进行横向和竖向的条件:获取min(r1,r2)
                float r1=(float)((scressWidth*(1-rate_x_1-rate_x_2))/
                                     (((1+rate_supervise_radius+comment_radius_rate*comment_radius_rate_left)*Math.cos(comment_jiaodu))+//
                                                            (1+rate_supervise_radius+mine_radius_rate*mine_radius_rate_left)*Math.cos(mine_jiaodu)+//
                                                                                                                            mine_radius_rate+comment_radius_rate));

                float r2=(float)(((main_rl_height*(1-rate_y_2))/(1+//
                                                                (1+rate_supervise_radius+state_radius_rate*state_radius_rate_left)*Math.cos(state_jiaodu)+//
                                                                                                                                    state_radius_rate+rate_y_1)));

//                System.out.println("radius_supervise r1:"+r1+"..r2:"+r2);
                //进行获取最小的半径
                radius_supervise=Math.min(r1,r2);
//                System.out.println("radius_supervise:"+radius_supervise+"..comment_jiaodu:"+comment_jiaodu+"..state_jiaodu:"+state_jiaodu+"..mine_jiaodu:"+mine_jiaodu);
                radius_supervise_out=radius_supervise+radius_supervise*rate_supervise_radius;
                //进行设置各自的宽度和margin值

                //文明监督
                rl_supervise_background.getLayoutParams().width=//
                                                             (int)(radius_supervise*(rate_supervise_radius+1))*2;
                rl_supervise_background.getLayoutParams().height=//
                                                             (int)(rl_supervise_background.getLayoutParams().width/rate_supervise_background_width_height_1);
//                rl_supervise_background.getLayoutParams().width=(int)radius_supervise_out;

                rl_supervise_background.requestLayout();

                rl_civilization_supervise.getLayoutParams().height=(int)radius_supervise*2;
                rl_civilization_supervise.getLayoutParams().width=(int)radius_supervise*2;

                ((ViewGroup.MarginLayoutParams)rl_civilization_supervise.getLayoutParams()).topMargin=(int)((rate_y_1)*radius_supervise);
                rl_civilization_supervise.requestLayout();
                //设置btnSupervise的padding值
                btnSupervise.setPadding(btnSupervise.getPaddingLeft(), (int) (radius_supervise * 0.7), btnSupervise.getPaddingRight(), btnSupervise.getPaddingBottom());
                //进行修改文明监督红色标识的位置
                ((ViewGroup.MarginLayoutParams)btnSuperviseNum.getLayoutParams()).topMargin=//
                                        (int)(rl_supervise_background.getLayoutParams().height/2-radius_supervise*Math.sin(red_number_jiaodu))-redNumberRadius/2;
                ((ViewGroup.MarginLayoutParams)btnSuperviseNum.getLayoutParams()).rightMargin=//
                        (int)(rl_supervise_background.getLayoutParams().width/2-radius_supervise*Math.cos(red_number_jiaodu))-redNumberRadius/2;
                btnSuperviseNum.requestLayout();

                //文明评论
                rl_comment.getLayoutParams().height=(int)(comment_radius_rate*radius_supervise)*2;
                rl_comment.getLayoutParams().width=(int)(comment_radius_rate*radius_supervise)*2;
                ((ViewGroup.MarginLayoutParams)rl_comment.getLayoutParams()).topMargin=//
                                                             (int)(radius_supervise+rate_y_1*radius_supervise+//
                                                                             Math.sin(comment_jiaodu)*(1+comment_radius_rate*comment_radius_rate_left+rate_supervise_radius)*radius_supervise-//
                                                                                                                                                        comment_radius_rate*radius_supervise);
                ((ViewGroup.MarginLayoutParams)rl_comment.getLayoutParams()).leftMargin=//
                                                            (int)(scressWidth*0.5-(Math.cos(comment_jiaodu)*(1+comment_radius_rate*comment_radius_rate_left+rate_supervise_radius)*radius_supervise)- comment_radius_rate*radius_supervise);
//                System.out.println("radius_supervise rl_comment.getLayoutParams()).leftMargin:" + ((ViewGroup.MarginLayoutParams) rl_comment.getLayoutParams()).leftMargin + "...");//- comment_radius_rate*radius_supervise

                rl_comment.requestLayout();

                int btnCommentNumWidth=(int)((rl_comment.getLayoutParams().height/2-redNumberRadius/2))*2;

                ((ViewGroup.MarginLayoutParams)btnCommentNum.getLayoutParams()).topMargin=//
                                                            (int)(rl_comment.getLayoutParams().height/2-btnCommentNumWidth/2*Math.sin(red_number_jiaodu))-redNumberRadius/2;

                ((ViewGroup.MarginLayoutParams)btnCommentNum.getLayoutParams()).rightMargin=//
                                                           (int)(rl_comment.getLayoutParams().width/2-btnCommentNumWidth/2*Math.cos(red_number_jiaodu))-redNumberRadius/2;

                btnCommentNum.requestLayout();


                //我的模块
                rl_mine.getLayoutParams().height=(int)(mine_radius_rate*radius_supervise)*2;
                rl_mine.getLayoutParams().width=(int)(mine_radius_rate*radius_supervise)*2;

                ((ViewGroup.MarginLayoutParams)rl_mine.getLayoutParams()).topMargin=
                                                            (int)(radius_supervise+rate_y_1*radius_supervise+(1+mine_radius_rate*mine_radius_rate_left+rate_supervise_radius)*radius_supervise*Math.sin(mine_jiaodu)-//
                                                                                                                                                        mine_radius_rate*radius_supervise);

                ((ViewGroup.MarginLayoutParams)rl_mine.getLayoutParams()).rightMargin=//
                                                            (int)(scressWidth*0.5-Math.cos(mine_jiaodu)*(1+rate_supervise_radius+mine_radius_rate*mine_radius_rate_left)*radius_supervise-//
                                                                                                                                                        mine_radius_rate*radius_supervise);
                rl_mine.requestLayout();

                int width=(int)((rl_mine.getLayoutParams().height/2-redNumberRadius/2))*2;

                ((ViewGroup.MarginLayoutParams)btnMineNum.getLayoutParams()).topMargin=//
                        (int)(rl_mine.getLayoutParams().height/2-width/2*Math.sin(red_number_jiaodu))-redNumberRadius/2;

                ((ViewGroup.MarginLayoutParams)btnMineNum.getLayoutParams()).rightMargin=//
                        (int)(rl_mine.getLayoutParams().width/2-width/2*Math.cos(red_number_jiaodu))-redNumberRadius/2;

                btnMineNum.requestLayout();

                //文明动态
                rl_civi_state.getLayoutParams().height=(int)(state_radius_rate*radius_supervise)*2;
                rl_civi_state.getLayoutParams().width=(int)(state_radius_rate*radius_supervise)*2;

                ((ViewGroup.MarginLayoutParams)rl_civi_state.getLayoutParams()).topMargin=//
                                                            (int)((Math.cos(state_jiaodu)*(1+state_radius_rate*state_radius_rate_left+rate_supervise_radius))*radius_supervise-radius_supervise+
                                                                                                                        radius_supervise+rate_y_1*radius_supervise);

                ((ViewGroup.MarginLayoutParams)rl_civi_state.getLayoutParams()).leftMargin=//
                                                            (int)(0.5*scressWidth-(radius_supervise*state_radius_rate-//
                                                                                                                        Math.sin(state_jiaodu)*(1+state_radius_rate*state_radius_rate_left+rate_supervise_radius)*radius_supervise));

                rl_civi_state.requestLayout();
                int btnStateNumWidth=(int)((rl_civi_state.getLayoutParams().height/2-redNumberRadius/2))*2;
                ((ViewGroup.MarginLayoutParams)btnAutostateNum.getLayoutParams()).topMargin=//
                        (int)(rl_civi_state.getLayoutParams().height/2-btnStateNumWidth/2*Math.sin(red_number_jiaodu))-redNumberRadius/2;

                ((ViewGroup.MarginLayoutParams)btnAutostateNum.getLayoutParams()).rightMargin=//
                        (int)(rl_civi_state.getLayoutParams().width/2-btnStateNumWidth/2*Math.cos(red_number_jiaodu))-redNumberRadius/2;

                btnAutostateNum.requestLayout();
            }
        });

        //加载布局控件
        notice = (RelativeLayout)findViewById(R.id.main_notice_activity);//通知公告
        /***文明监督**/
        btnSupervise = (Button)findViewById(R.id.civilization_supervise);
        btnSuperviseNum = (Button)findViewById(R.id.btn_supervise_number);
        /***文明评论**/
        btnComment = (Button)findViewById(R.id.civilization_comment);
        btnCommentNum = (Button)findViewById(R.id.btn_comment_number);
        /***文明动态**/
        btnAutoState = (Button)findViewById(R.id.civilization_autostate);
        btnAutostateNum = (Button)findViewById(R.id.btn_autostate_number);
        /***我的**/
        btnMine = (Button)findViewById(R.id.civilization_mine);
        btnMineNum = (Button)findViewById(R.id.btn_mine_number);
        //进行启动定时器
        List<HDC_Notice> datas= NoticeDao.getInstance().getAllNoticeList();
        noticeList=getFirstFourList(datas);
        noticeIndex=0;

        //进行获取首页的轮播 进行初始化显示
        initViewPager();

        //进行初始化数字:
        try {
            User user= UserDao.getInstance().getLocalUser();
            HDC_MainNumber hdc_mainNumber= MainNumberDao.getInstance().getNumberBy(user.getUserId());
            notifyNumberUI(hdc_mainNumber,user);
        } catch (ContentException e) {
            e.printStackTrace();
        }
    }

    private void initViewPager() {
        mainPagerHolder.setActivity(MainActivity.this);
        List<HDC_CiviState> civistateList=getFirstFourList(CivistateDao.getInstance().getAllCivistateList());
        if(civistateList.size()>0){
            mainPagerHolder.setDatas(civistateList);
            mainPagerHolder.start();
            View contentView = mainPagerHolder.getContentView();
            if (contentView.getParent()!= null){
                ((ViewGroup)contentView.getParent()).removeView(contentView);
            }
            frameLayout.addView(contentView);
        }else{
            mainPagerHolder.stop();
        }
    }

    @Override
    protected void initInitevnts() {
        /*******第一屏数据加载*******/
        getViewPagerForServer();
        getNoticeForServer();
        getNumberForServer();
        checkAppInfo();
        //通知公告小喇叭
        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /***点击跳转的是通知公告的列表，通知公告的内容点击跳转的是单个的详情**/
                System.out.println("Teeeeeee_MainActivity-->NoticeActivity");
                startActivity(new Intent(MainActivity.this, NotioceActivity.class));

            }
        });

        //文明监督
        btnSupervise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    User user= UserDao.getInstance().getLocalUser();
                    System.out.println("user.getIdentityState().equals(UserPermisson.VOLUNTEER.getType()):"+user.getIdentityState().equals(UserPermisson.VOLUNTEER.getType()));
                    System.out.println("user.getVolunteerId().trim().equals(\"\"):"+user.getVolunteerId().trim().equals(""));
                    if(user.getIdentityState().equals(UserPermisson.VOLUNTEER.getType())){
                        Intent intent=new Intent(MainActivity.this,CiviSuperviseActivity.class);
                        startActivity(intent);
                    }else{
                        String identityState =  user.getIdentityState();
                        if (identityState.equals(UserPermisson.ORDINARYAPPLYING.getType())){
                            showPopup(identityState, HDCivilizationConstants.ORDINARYAPPLYING_);//正在申请成为志愿者
                        }else if (identityState.equals(UserPermisson.ORDINARYSTATE.getType())){
                            showPopup(identityState, HDCivilizationConstants.ORDINARYSTATE_);//普通用户先申请成为志愿者
                        }else if (identityState.equals(UserPermisson.ORDINARYSTOPSTATE.getType())){
                            showPopup(identityState, HDCivilizationConstants.ORDINARYSTOPSTATE_);//普通用户被禁用
                        }else if (identityState.equals(UserPermisson.DEFAULTSTATE.getType())){
                            showPopup(identityState, HDCivilizationConstants.DEFAULTSTATE_);//缺省状态
                        }else if (identityState.equals(UserPermisson.UNKNOW_VALUE.getType())){
                            showPopup(identityState, HDCivilizationConstants.UNKNOW_VALUE_);//用户不存在
                        }
                    }
                } catch (ContentException e) {
                    e.printStackTrace();
                    OKPopup.getInstance().showPopup(MainActivity.this, new OKPopup.BtnClickListener() {
                        @Override
                        public void btnOk() {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.putExtra(LoginActivity.ISFROM_OTHRES,true);
                            startActivity(intent);
                            OKPopup.getInstance().dismissDialog();
                        }
                    }, false, HDCivilizationConstants.NO_LOGIN);
                }
            }
        });

        //文明评论
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CiviCommentActivity.class));
            }
        });
        //文明动态
        btnAutoState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CiviStateActivity.class);
                startActivity(intent);
            }
        });

        String userType = "0";
        //我的
        btnMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 *    if(是否登录){(否)
                 *        登录界面
                 *    }else{
                 *       if(判断身份标示  == 0普通网友){0普通网友、1普通用户停用、2普通用户、3志愿者用户申请志愿者中、4志愿者
                 *             普通网友界面
                 *       }else if(判断身份标示 == 1普通用户停用){
                 *              普通用户停用弹出重新登录界面
                 *       }else if(判断身份显示 == 3普通用户申请志愿者中){
                 *              显示普通用户申请志愿者界面
                 *       }else if(判断身份显示 == 4志愿者用户){
                 *              显示志愿者用户界面
                 *       }
                 *    }
                 */
                try {
                    User user= UserDao.getInstance().getLocalUser();
                    if(Integer.parseInt(user.getIdentityState())<=
                                        Integer.parseInt(UserPermisson.ORDINARYSTOPSTATE.getType())){
                        //進行權限過低
                        UiUtils.getInstance().showToast(HDCivilizationConstants.FORBIDDEN_USER);
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.putExtra(LoginActivity.ISFROM_OTHRES, true);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(MainActivity.this,MineActivity.class);
                        startActivity(intent);
                    }
                } catch (ContentException e) {
                    e.printStackTrace();
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    intent.putExtra(LoginActivity.ISFROM_OTHRES, true);
                    startActivity(intent);
                }
            }
        });

        WindowManager wm =MainActivity.this.getWindowManager();
        int height = wm.getDefaultDisplay().getHeight();
        int rlHeight = height - (35+240);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,rlHeight);
        rl_main.setLayoutParams(params);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 需要做的事:发送消息
                Message message = new Message();
                message.what = HDCivilizationConstants.CILIVIZATION_NOTICE;
                Bundle bundle = new Bundle();
                bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.CILIVIZATION_NOTICE_TIMER);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }, 200, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //定时器进行取消!
        timer.cancel();

        //退出该页面时:进行设置所有的用户离线
        UserDao.getInstance().updateAllUserLocalState(false);
    }

    private void showPopup(final String identityState,String title){
        OKPopup.getInstance().showPopup(MainActivity.this, new OKPopup.BtnClickListener() {
            @Override
            public void btnOk() {
                if (identityState.equals(UserPermisson.ORDINARYAPPLYING.getType())){
                    OKPopup.getInstance().dismissDialog();
                }else if (identityState.equals(UserPermisson.ORDINARYSTATE.getType())){
                    startActivity(new Intent(MainActivity.this,VolunteerSignUpFormActivity.class));
                    OKPopup.getInstance().dismissDialog();
                }else if (identityState.equals(UserPermisson.ORDINARYSTOPSTATE.getType())){
                    OKPopup.getInstance().dismissDialog();
                }else if (identityState.equals(UserPermisson.DEFAULTSTATE.getType())){
                    OKPopup.getInstance().dismissDialog();
                }else if (identityState.equals(UserPermisson.UNKNOW_VALUE)){
                    OKPopup.getInstance().dismissDialog();
                }
            }
        }, false,title);
    }

    /******图片轮播网络请求*****/
    private void getViewPagerForServer() {
        isViewPagerRequestEnd=false;
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                String userId="";
                try {
                    try {
                        //进行获取本地用户
                        User user= UserDao.getInstance().getLocalUser();
                        userId=user.getUserId();
                    } catch (ContentException e) {
                        e.printStackTrace();
                        userId="";
                    }
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String,String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode","AROUND0007");
                    paramsMap.put("pagerNum", CiviStateActivity.PAGE_SIZE+"");
                    paramsMap.put("currentPager","1");
                    paramsMap.put("userId", userId);
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                    civiStateListProtocol.setUserId(userId);
                    civiStateListProtocol.setActionKeyName("加载文明动态信息失败");
                    message.obj = civiStateListProtocol.loadData(urlParamsEntity);
                    message.what = HDCivilizationConstants.CILIVIZATION_VIEWPAGER_REQUEST;
                    handler.sendMessage(message);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT,  e.getMessage());
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.CILIVIZATION_VIEWPAGER_REQUEST);
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (ContentException e) {
                    e.printStackTrace();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.CILIVIZATION_VIEWPAGER_REQUEST);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        });
    }

    /***通知公告网络请求****/
    private void getNoticeForServer() {
        isNotifyRequestEnd=false;
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                try {
                    String userId="";//用户的id
                    try {
                        //进行获取本地用户
                        User user= UserDao.getInstance().getLocalUser();
                        userId=user.getUserId();
                    } catch (ContentException e) {
                        e.printStackTrace();
                        userId="";
                    }
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode", "AROUND0027");
                    paramsMap.put("pagerNum", NotioceActivity.PAGE_SIZE+"");
                    paramsMap.put("currentPager", "1");
                    paramsMap.put("userId", userId);
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                    NoticeProtocol noticeProtocol=new NoticeProtocol();
                    noticeProtocol.setActionKeyName("加载通知公告信息失败");
                    message.obj = noticeProtocol.loadData(urlParamsEntity);
                    message.what = HDCivilizationConstants.CILIVIZATION_NOTICE;
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.CILIVIZATION_NOTICE_REQUEST);
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.CILIVIZATION_NOTICE_REQUEST);
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (ContentException e) {
                    e.printStackTrace();
//            int errorCode = e.getErrorCode();
//            if (errorCode==HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE){//权限过低
//
//            }
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT,  e.getErrorContent());
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.CILIVIZATION_NOTICE_REQUEST);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        });
    }
    /***数字更新网络请求**/
    private void getNumberForServer() {
        isNumberNotifyEnd=false;
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                User user= null;
                try {
                    //进行获取本地用户
                    user = UserDao.getInstance().getLocalUser();
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String,String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode", "AROUND0003");
//            System.out.println("userId ="+userId);
                    paramsMap.put("userId", user.getUserId());//userId
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    MainNumberProtocol mainNumberProtocol = new MainNumberProtocol();
                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                    mainNumberProtocol.setUserId(user.getUserId());
                    mainNumberProtocol.setUser(user);
                    mainNumberProtocol.setActionKeyName("提示数字加载失败");
                    message.obj = mainNumberProtocol.loadData(urlParamsEntity);
                    message.what = HDCivilizationConstants.CILIVIZATION_NUMBER_REQUEST;
                    handler.sendMessage(message);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT,  e.getMessage());
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.CILIVIZATION_NUMBER_REQUEST);
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (ContentException e) {
                    e.printStackTrace();
                    if(e.getErrorCode()== HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE){
                        //权限过低:进行提示用户--退出登录-给出用户选择!
                        //// TODO: 2016/8/31
                        //隐藏所有的数字提示
                        message.what=MESSAGE_DISPEAR_NUMBER;
                        handler.sendMessage(message);
                    }else{
                        if(user!=null){
                            message.what = HDCivilizationConstants.ERROR_CODE;
                            bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                            bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.CILIVIZATION_NUMBER_REQUEST);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                }
            }
        });
    }

    /***定时器，定时发送消息更新UI*/
   Timer timer = new Timer();


    /**
     * 进行检测appInfo
     */
    public void checkAppInfo(){
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                //进行执行
                Message message=new Message();
                Bundle bundle=new Bundle();
                try {
                    String userId="";//用户的id
                    try {
                        //进行获取本地用户
                        User user= UserDao.getInstance().getLocalUser();
                        userId=user.getUserId();
                    } catch (ContentException e) {
                        e.printStackTrace();
                        userId="";
                    }
                    AppCheckProtocol appCheckProtocol=new AppCheckProtocol();
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String,String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode","AROUND0030");
                    paramsMap.put("userId", userId);
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                    appCheckProtocol.setActionKeyName("检测更新失败");
                    message.obj = appCheckProtocol.loadData(urlParamsEntity);
                    message.what = CHECK_SUCCESS;
                    handler.sendMessage(message);
                } catch (JsonParseException e) {
                    e.printStackTrace();
//                    message.what = CHECK_FAILURE;
//                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
//                    message.setData(bundle);
//                    handler.sendMessage(message);
                } catch (ContentException e) {
                    e.printStackTrace();
//                    if(e.getErrorCode()== HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE){
//                        //按照AppCheckProtocol 解析的时候判断:只有普通用户被禁用    暂时先进行提示!// TODO: 2016/9/19
//                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
//                        //需要进行判断权限过低的情况
//                        message.what = CHECK_FAILURE;
//                        message.setData(bundle);
//                        handler.sendMessage(message);
//                    }else{
//                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
//                        //需要进行判断权限过低的情况
//                        message.what = CHECK_FAILURE;
//                        message.setData(bundle);
//                        handler.sendMessage(message);
//                    }
                }
            }
        });
    }
}

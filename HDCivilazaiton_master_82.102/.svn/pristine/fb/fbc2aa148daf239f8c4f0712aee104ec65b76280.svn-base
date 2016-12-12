package com.zhjy.hdcivilization.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.MainNumberDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_MainNumber;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.progressbar.KProgressHUD;
import com.zhjy.hdcivilization.protocol.MineProtocol;
import com.zhjy.hdcivilization.utils.BitmapUtil;
import com.zhjy.hdcivilization.utils.FileUtils;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.NetUtils;
import com.zhjy.hdcivilization.utils.SharedPreferencesManager;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.OKPopup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author :huangxianfeng on 2016/8/3.
 *         我的模块
 */
public class MineActivity extends BaseActivity implements //
                                    View.OnTouchListener, View.OnClickListener {

    private RelativeLayout mineSignName, mineMessage, mineNotice, mineUse, mineShare, mineSetting, mineAbout,rl_back;
    private RelativeLayout mineMineGold, mineMineSub, mineApplyCheck, mineGoldNumber, rl_mine;
    private ImageView imgSignName, imgMessage, imgNotice, imgUse, imgShare, imgSetting, imgAbout;
    private ImageView arrowSignName, arrowMessage, arrowNotice, arrowUse, arrowShare, arrowSetting, arrowAbout;
    private ImageView imgApplyCheck, imgMineGold, imgMineSub, arrowApplyCheck, arrowMineGold, arrowMineSub;
    private ImageView btnBack;
    private ImageView userPic;
    private TextView userName, userNameType, userInfo,goldNumber;

    private String userType, loginType="----";
    private LoginSuccessPopupWindow popupWindow;

    private View line4, line5, line6, line7;

    private User user;
    private Button btnSuperviseNum, btnNoticeNum;
    private KProgressHUD hud;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HDCivilizationConstants.REFRESH_PAGE:
                    UiUtils.getInstance().showToast("用户信息获取成功！");
                    initMineUserUI();
                    break;

                case HDCivilizationConstants.REFRESH_GOLD_NUMBER:
                    hud.dismiss();
                    int goldCoint=0;
                    try {
                        goldCoint=Integer.parseInt(user.getGoldCoin());
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                        goldCoint=0;
                    }
//                    if (goldCoint>HDCivilizationConstants.INIT_GOLD_COIN_VALUE_LARGE){
                        Intent intent = new Intent(MineActivity.this, MineGoldActivity.class);
                        intent.putExtra(MineGoldActivity.GOLDCOIN, goldCoint + "");
                        startActivity(intent);
//                    }else{
//                        UiUtils.getInstance().showToast("文明兑换值数量不足！");
//                    }
                    break;

                case HDCivilizationConstants.ERROR_CODE:
                    if (msg.getData().getInt(HDCivilizationConstants.ACTION_CODE) == HDCivilizationConstants.HEAD_DATA){
                        UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                        if(hud!=null){
                            hud.dismiss();
                        }
                    }else if (msg.getData().getInt(HDCivilizationConstants.ACTION_CODE) == HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE){
                        OKPopup.getInstance().showPopup(MineActivity.this, new OKPopup.BtnClickListener() {
                            @Override
                            public void btnOk() {
                                finish();
                                Intent intent = new Intent(MineActivity.this, LoginActivity.class);
                                intent.putExtra(LoginActivity.ISFROM_OTHRES,true);
                                startActivity(intent);
                                OKPopup.getInstance().dismissDialog();
                            }
                        }, false, HDCivilizationConstants.NO_LOGIN_);
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loginType = getIntent().getStringExtra("loginType");
        contentView= UiUtils.getInstance().inflate(R.layout.activity_mine);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        try {
            user = UserDao.getInstance().getLocalUser();
            if(loginType!=null && "login".equals(loginType)){
                //进行保存用户信息:
                    final Map<String,String> map=new HashMap<String,String>();
                    map.put("userId=",user.getUserId());
                    map.put("volunteerId=", user.getVolunteerId());
                    SimpleDateFormat formate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    map.put("loginTime=", formate.format(Calendar.getInstance().getTime()));
                    requestPermission(HDCivilizationConstants.READ_PHONE_STATE_REQUEST_CODE, "android.permission.READ_PHONE_STATE", new Runnable() {
                        @Override
                        public void run() {
                            TelephonyManager telephonyManager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
                            map.put("LineNumber=", telephonyManager.getLine1Number());
                            FileUtils.getInstance().saveCrashInfo2File(map);
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            //不允许获取字段
                            FileUtils.getInstance().saveCrashInfo2File(map);
                        }
                    });
            }
        } catch (ContentException e) {
            e.printStackTrace();
            UiUtils.getInstance().showToast(e.getMessage());
        }
        btnBack = (ImageView) findViewById(R.id.btn_back);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        userPic = (ImageView) findViewById(R.id.comment_user_pic);
        userName = (TextView) findViewById(R.id.comment_user_name);
        userNameType = (TextView) findViewById(R.id.comment_user_name_type);
        userInfo = (TextView) findViewById(R.id.mine_info_perfect);
        rl_mine = (RelativeLayout) findViewById(R.id.rl_mine);

        mineGoldNumber = (RelativeLayout) findViewById(R.id.rl_mine_gold_number);
        goldNumber = (TextView)findViewById(R.id.mine_mine_gold_number);

        line4 = findViewById(R.id.line4);
        line5 = findViewById(R.id.line5);
        line6 = findViewById(R.id.line6);
        line7 = findViewById(R.id.line7);

        //item布局的控件
        mineApplyCheck = (RelativeLayout) findViewById(R.id.rl_apply_check);
        mineMineGold = (RelativeLayout) findViewById(R.id.rl_mine_gold);
        mineMineSub = (RelativeLayout) findViewById(R.id.rl_mine_sub);

        mineSignName = (RelativeLayout) findViewById(R.id.rl_sign_name);
        mineNotice = (RelativeLayout) findViewById(R.id.rl_notice);
        mineUse = (RelativeLayout) findViewById(R.id.rl_use);
        mineShare = (RelativeLayout) findViewById(R.id.rl_share);
        mineSetting = (RelativeLayout) findViewById(R.id.rl_setting);
        mineAbout = (RelativeLayout) findViewById(R.id.rl_about);

        //提醒数目
        btnSuperviseNum = (Button) findViewById(R.id.btn_mine_supervise_number);
        btnNoticeNum = (Button) findViewById(R.id.btn_mine_notice_number);
        //图标的控件
        imgApplyCheck = (ImageView) findViewById(R.id.mine_img_apply_check);
        imgMineGold = (ImageView) findViewById(R.id.mine_img_mine_gold);
        imgMineSub = (ImageView) findViewById(R.id.mine_img_mine_sub);

        imgSignName = (ImageView) findViewById(R.id.mine_img_sign_name);
        imgNotice = (ImageView) findViewById(R.id.mine_img_notice);
        imgUse = (ImageView) findViewById(R.id.mine_img_use);
        imgShare = (ImageView) findViewById(R.id.mine_img_share);
        imgSetting = (ImageView) findViewById(R.id.mine_img_setting);
        imgAbout = (ImageView) findViewById(R.id.mine_img_about);

        //右边箭头控件
        arrowApplyCheck = (ImageView) findViewById(R.id.arrow_apply_check);
        arrowMineGold = (ImageView) findViewById(R.id.arrow_mine_gold);
        arrowMineSub = (ImageView) findViewById(R.id.arrow_mine_sub);

        arrowSignName = (ImageView) findViewById(R.id.arrow_sign_name);
        arrowNotice = (ImageView) findViewById(R.id.arrow_notice);
        arrowUse = (ImageView) findViewById(R.id.arrow_use);
        arrowShare = (ImageView) findViewById(R.id.arrow_share);
        arrowSetting = (ImageView) findViewById(R.id.arrow_setting);
        arrowAbout = (ImageView) findViewById(R.id.arrow_about);


        String lastLoginTime = (String)SharedPreferencesManager.get(UiUtils.getInstance().getContext(), HDCivilizationConstants.LASTLOGINTIME, "");
        System.out.println("LoginProtocol.running1.lastLoginTime="+lastLoginTime);
        System.out.println("LoginProtocol.running.lastLoginTime=" + (lastLoginTime == null) + ";=" + (lastLoginTime.equals("")));
        if ((lastLoginTime.trim().equals("") || lastLoginTime==null) &&
                                    (loginType!=null && "login".equals(loginType))){
            if (Integer.parseInt(user.getIdentityState())!=4){
                startActivity(new Intent(MineActivity.this, LoginSuccessActivity.class));
            }
        }

    }

    private void initMineUserUI(){
            if (UserPermisson.ORDINARYSTATE.getType().//
                                equals(user.getIdentityState())) {//普通网友
//                System.out.println("MineActivity_user.getIdentityState()普通网友"+user.getIdentityState());
                mineApplyCheck.setVisibility(View.GONE);
                mineMineGold.setVisibility(View.GONE);
                mineMineSub.setVisibility(View.GONE);
                mineSignName.setVisibility(View.VISIBLE);
                line4.setVisibility(View.GONE);
                line5.setVisibility(View.GONE);
                line6.setVisibility(View.GONE);
                line7.setVisibility(View.VISIBLE);
            }else if (UserPermisson.ORDINARYAPPLYING.getType().equals(user.getIdentityState())) {//申请查询
//                System.out.println("MineActivity_user.getIdentityState()申请查询"+user.getIdentityState());
                setVisibleToView();
            }else if (UserPermisson.VOLUNTEER.getType().equals(user.getIdentityState())) {//志愿者
//                System.out.println("MineActivity_user.getIdentityState()志愿者"+user.getIdentityState());
                mineApplyCheck.setVisibility(View.GONE);
                mineMineGold.setVisibility(View.VISIBLE);
                mineMineSub.setVisibility(View.VISIBLE);
                mineSignName.setVisibility(View.GONE);
                mineGoldNumber.setVisibility(View.VISIBLE);
                line4.setVisibility(View.GONE);
                line5.setVisibility(View.VISIBLE);
                line6.setVisibility(View.VISIBLE);
                line7.setVisibility(View.GONE);
            }else if(UserPermisson.ORDINARYSTOPSTATE.getType().equals(user.getIdentityState())){
//                System.out.println("MineActivity_user.getIdentityState()普通用户停用"+user.getIdentityState());
                //普通用户被停用...业务处理？？？？？
                OKPopup.getInstance().showPopup(MineActivity.this, new OKPopup.BtnClickListener() {
                    @Override
                    public void btnOk() {
                        finish();
                        Intent intent = new Intent(MineActivity.this, LoginActivity.class);
                        intent.putExtra(LoginActivity.ISFROM_OTHRES,true);
                        startActivity(intent);
                        OKPopup.getInstance().dismissDialog();
                    }
                }, false, HDCivilizationConstants.STATE_PROMIT);
            }else if(UserPermisson.UNKNOW_VALUE.getType().equals(user.getIdentityState()) ||
                                        UserPermisson.DEFAULTSTATE.getType().equals(user.getIdentityState())){
//                System.out.println("MineActivity_user.getIdentityState()缺省状态"+user.getIdentityState());
                //缺省状态....
            }
            //进行初始化头像和用户昵称
            initUserInfo(user);
    }

    private void setVisibleToView() {//设置申请查询的隐藏
        mineApplyCheck.setVisibility(View.VISIBLE);
        mineMineGold.setVisibility(View.GONE);
        mineMineSub.setVisibility(View.GONE);
        mineSignName.setVisibility(View.GONE);
        line4.setVisibility(View.GONE);
        line5.setVisibility(View.GONE);
        line6.setVisibility(View.VISIBLE);
        line7.setVisibility(View.GONE);
    }

    @Override
    protected void initInitevnts() {
        mineApplyCheck.setOnTouchListener(this);
        mineMineGold.setOnTouchListener(this);
        mineMineSub.setOnTouchListener(this);

        mineSignName.setOnTouchListener(this);
        mineNotice.setOnTouchListener(this);
        mineUse.setOnTouchListener(this);
        mineShare.setOnTouchListener(this);
        mineSetting.setOnTouchListener(this);
        mineAbout.setOnTouchListener(this);
        rl_back.setOnClickListener(this);
        rl_mine.setOnClickListener(this);
        grtDataForService();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            //申请查询
            case R.id.rl_apply_check:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imgApplyCheck.setBackgroundResource(R.drawable.mine_volunteer_sign_name_press);
                        arrowApplyCheck.setBackgroundResource(R.drawable.mine_arrow_right_press);
                        break;
                    case MotionEvent.ACTION_UP:
                        imgApplyCheck.setBackgroundResource(R.drawable.mine_volunteer_sign_name);
                        arrowApplyCheck.setBackgroundResource(R.drawable.mine_arrow_right);
                        startActivity(new Intent(MineActivity.this, ApplyCheckActivity.class));
                        break;
                }

                break;
            //我的金币
            case R.id.rl_mine_gold:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imgMineGold.setBackgroundResource(R.drawable.mine_mine_gold_press);
                        arrowMineGold.setBackgroundResource(R.drawable.mine_arrow_right_press);
                        break;
                    case MotionEvent.ACTION_UP:
                        imgMineGold.setBackgroundResource(R.drawable.mine_mine_gold);
                        arrowMineGold.setBackgroundResource(R.drawable.mine_arrow_right);
                        if (NetUtils.getInstance().checkNetwork(UiUtils.getInstance().getContext())){
                            hud = KProgressHUD.create(MineActivity.this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("信息更新中").setCancellable(false);
                            hud.setCancellable(false);
                            hud.show();
                            goldRefresh();
                        }else{
                            UiUtils.getInstance().showToast("请检查网络！");
                        }
                        break;
                }

                break;
            //我的上报
            case R.id.rl_mine_sub:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imgMineSub.setBackgroundResource(R.drawable.mine_mine_sub_press);
                        arrowMineSub.setBackgroundResource(R.drawable.mine_arrow_right_press);
                        break;
                    case MotionEvent.ACTION_UP:

                        imgMineSub.setBackgroundResource(R.drawable.mine_mine_sub);
                        arrowMineSub.setBackgroundResource(R.drawable.mine_arrow_right);
                        Intent intent = new Intent(MineActivity.this, MineSuperviseListActivity.class);
                        intent.putExtra(HDCivilizationConstants.SUPERVISE_ITEMID,HDCivilizationConstants.ITEMID);
                        startActivity(intent);
                        break;
                }
                break;
            //志愿者报名
            case R.id.rl_sign_name:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imgSignName.setBackgroundResource(R.drawable.mine_volunteer_sign_name_press);
                        arrowSignName.setBackgroundResource(R.drawable.mine_arrow_right_press);
                        break;
                    case MotionEvent.ACTION_UP:
                        imgSignName.setBackgroundResource(R.drawable.mine_volunteer_sign_name);
                        arrowSignName.setBackgroundResource(R.drawable.mine_arrow_right);
                        startActivity(new Intent(MineActivity.this, VolunteerSignUpActivity.class));
                        break;
                }

                break;
            //通知公告
            case R.id.rl_notice:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imgNotice.setBackgroundResource(R.drawable.mine_notice_press);
                        arrowNotice.setBackgroundResource(R.drawable.mine_arrow_right_press);
                        break;
                    case MotionEvent.ACTION_UP:
                        imgNotice.setBackgroundResource(R.drawable.mine_notice);
                        arrowNotice.setBackgroundResource(R.drawable.mine_arrow_right);
                        Intent intent = new Intent(MineActivity.this, NotioceActivity.class);
                        startActivity(intent);
                        break;
                }

                break;
            //使用指南
            case R.id.rl_use:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imgUse.setBackgroundResource(R.drawable.mine_use_guide_press);
                        arrowUse.setBackgroundResource(R.drawable.mine_arrow_right_press);
                        break;
                    case MotionEvent.ACTION_UP:
                        imgUse.setBackgroundResource(R.drawable.mine_use_guide);
                        arrowUse.setBackgroundResource(R.drawable.mine_arrow_right);
                        Intent intent = new Intent(MineActivity.this, UseGuideActivity.class);
                        startActivity(intent);
                        break;
                }

                break;
            //分享
            case R.id.rl_share:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imgShare.setBackgroundResource(R.drawable.mine_share_press);
                        arrowShare.setBackgroundResource(R.drawable.mine_arrow_right_press);
                        break;
                    case MotionEvent.ACTION_UP:
                        imgShare.setBackgroundResource(R.drawable.mine_share);
                        arrowShare.setBackgroundResource(R.drawable.mine_arrow_right);
                        startActivity(new Intent(MineActivity.this, ShareActivity.class));
                        break;
                }

                break;
            //设置
            case R.id.rl_setting:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imgSetting.setBackgroundResource(R.drawable.mine_setting_press);
                        arrowSetting.setBackgroundResource(R.drawable.mine_arrow_right_press);
                        break;
                    case MotionEvent.ACTION_UP:
                        imgSetting.setBackgroundResource(R.drawable.mine_setting);
                        arrowSetting.setBackgroundResource(R.drawable.mine_arrow_right);
                        startActivity(new Intent(MineActivity.this, SettingActivity.class));
                        break;
                }

                break;
            //关于
            case R.id.rl_about:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imgAbout.setBackgroundResource(R.drawable.mine_about_press);
                        arrowAbout.setBackgroundResource(R.drawable.mine_arrow_right_press);
                        break;
                    case MotionEvent.ACTION_UP:
                        imgAbout.setBackgroundResource(R.drawable.mine_about);
                        arrowAbout.setBackgroundResource(R.drawable.mine_arrow_right);
                        Intent intent = new Intent(MineActivity.this, AboutActivity.class);
                        intent.putExtra(HDCivilizationConstants.VERSIONUP,"");
                        startActivity(intent);
                        break;
                }
                break;
        }
        return false;
    }


    /**
     * 第一个请求服务器数据
     */
    private void grtDataForService() {
        initMineUserUI();
        if (NetUtils.getInstance().checkNetwork(UiUtils.getInstance().getContext())){
            /***开启提示发送中**/
//            try {
//                user = UserDao.getInstance().getLocalUser();
                ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        Message message = Message.obtain();
                        Bundle bundle = new Bundle();
                        try {
                            UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                            LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                            paramsMap.put("tranCode", "AROUND0028");
                            paramsMap.put("userId", user.getUserId());
                            urlParamsEntity.setParamsHashMap(paramsMap);
                            MineProtocol mineProtocol = new MineProtocol();
                            mineProtocol.setUser(user);
//                            urlParamsEntity.HDCURL ="http://192.168.83.90:9001/cs/supervision/Service/ServiceGate.jsp?";
                            urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                            mineProtocol.setUser(user);
                            mineProtocol.setActionKeyName("获取用户信息失败");
                            message.obj = mineProtocol.loadData(urlParamsEntity);
                            message.what = HDCivilizationConstants.REFRESH_PAGE;
                            handler.sendMessage(message);
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                            message.what = HDCivilizationConstants.ERROR_CODE;
                            bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                            bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.HEAD_DATA);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } catch (ContentException e) {
                            e.printStackTrace();
                            e.getErrorCode();
                            message.what = HDCivilizationConstants.ERROR_CODE;
                            bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                            if(e.getErrorCode()==HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE){
                                bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE);
                            }else{
                                bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.HEAD_DATA);
                            }
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                });
//            } catch (ContentException e) {
//                e.printStackTrace();
//                UiUtils.getInstance().showToast(e.getMessage());
//                finish();
//            }
        }else{
            UiUtils.getInstance().showToast("请检查网络！");
        }
    }


    private void goldRefresh() {
        if (NetUtils.getInstance().checkNetwork(UiUtils.getInstance().getContext())){
            /***开启提示发送中**/
//            try {
//                user = UserDao.getInstance().getLocalUser();
                initMineUserUI();
                ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        Message message = Message.obtain();
                        Bundle bundle = new Bundle();
                        try {
                            UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                            LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                            paramsMap.put("tranCode", "AROUND0028");
                            paramsMap.put("userId", user.getUserId());
                            urlParamsEntity.setParamsHashMap(paramsMap);
                            MineProtocol mineProtocol = new MineProtocol();
                            urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                            mineProtocol.setUser(user);
                            message.obj = mineProtocol.loadData(urlParamsEntity);
                            message.what = HDCivilizationConstants.REFRESH_GOLD_NUMBER;
                            handler.sendMessage(message);
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                            message.what = HDCivilizationConstants.ERROR_CODE;
                            bundle.putString(HDCivilizationConstants.ERROR_CONTENT, "获取金币信息失败" + e.getMessage());
                            bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.HEAD_DATA);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } catch (ContentException e) {
                            e.printStackTrace();
                            message.what = HDCivilizationConstants.ERROR_CODE;
                            bundle.putString(HDCivilizationConstants.ERROR_CONTENT, "获取金币信息失败" + e.getErrorContent());
                            bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.HEAD_DATA);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                });
//            } catch (ContentException e) {
//                e.printStackTrace();
//                UiUtils.getInstance().showToast(e.getMessage());
//                finish();
//            }
        }else{
            UiUtils.getInstance().showToast("请检查网络！");
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_mine:
                startActivity(new Intent(MineActivity.this, PersonalInfoActivity.class));
                break;

            //退出界面
            case R.id.rl_back:
                if((loginType!=null && "login".equals(loginType))){
                    finish();
                    Intent intent = new Intent(MineActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            user=UserDao.getInstance().getLocalUser();
            initMineUserUI();
        } catch (ContentException e) {
            e.printStackTrace();
        }
//        System.out.println("MineActivity_onRestart");
    }

    /**
     * 进行初始化用户信息
     *  //当数字为0的时候不显示数据

     //当数字为0的时候不显示数据
     * @param user
     */
    private void initUserInfo(User user) {
            //进行获取用户对象
            String nickName="";
            String portraitUrl="";
            if (user.getNickName().equals("")) {
                nickName = user.getAccountNumber();
            } else {
                nickName = user.getNickName();
            }
            portraitUrl = user.getPortraitUrl();

            //进行设置用户名:firstSignName
            userName.setText(nickName);

            //进行判断昵称或者头像都不为空的时候:不要显示未完善的资料!
            if((!user.getNickName().trim().equals("") && !user.getPortraitUrl().trim().equals("")) ||
                                                        user.getIdentityState().equals(UserPermisson.VOLUNTEER.getType())){
                userInfo.setVisibility(View.GONE);
            }else{
                userInfo.setVisibility(View.VISIBLE);
            }
            //进行显示用户类型
            if (user.getIdentityState().equals(UserPermisson.ORDINARYSTATE.getType()) ||
                                        user.getIdentityState().equals(UserPermisson.ORDINARYAPPLYING.getType())){
                  userNameType.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
                  mineGoldNumber.setVisibility(View.GONE);
                try {
                    HDC_MainNumber mainNumber=MainNumberDao.getInstance().getNumberBy(user.getUserId());
//                    System.out.println(mainNumber.toString());
                    if (mainNumber.getNotifyCount()<=0){
                        btnNoticeNum.setVisibility(View.GONE);
//                        System.out.println("setDataToUI noticeNum1=" + mainNumber.getNotifyCount());
                    }else{
                        btnNoticeNum.setVisibility(View.VISIBLE);
                        btnNoticeNum.setText(mainNumber.getNotifyCount()+"");
//                        System.out.println("setDataToUI noticeNum=" + mainNumber.getNotifyCount());
                    }
                } catch (ContentException e) {
                    e.printStackTrace();
                }
             }else if (user.getIdentityState().equals(UserPermisson.VOLUNTEER.getType())) {
                userNameType.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
                mineGoldNumber.setVisibility(View.VISIBLE);
                int goldCoin=0;
                try {
                     goldCoin=Integer.parseInt(user.getGoldCoin().trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    goldCoin=0;
                }
                goldNumber.setText(goldCoin+"");
                try {
                    HDC_MainNumber mainNumber=MainNumberDao.getInstance().getNumberBy(user.getUserId());
                    if (mainNumber.getSuperviseCount()<=0){
                        btnSuperviseNum.setVisibility(View.GONE);
//                        System.out.println("setDataToUI supervice1=" + mainNumber.getSuperviseCount());
                    }else{
//                        System.out.println("setDataToUI supervice="+mainNumber.getSuperviseCount());
                        btnSuperviseNum.setVisibility(View.VISIBLE);
                        btnSuperviseNum.setText(mainNumber.getSuperviseCount()+"");
                    }

                    if (mainNumber.getNotifyCount()<=0){
                        btnNoticeNum.setVisibility(View.GONE);
//                        System.out.println("setDataToUI noticeNum1=" + mainNumber.getNotifyCount());
                    }else{
                        btnNoticeNum.setVisibility(View.VISIBLE);
                        btnNoticeNum.setText(mainNumber.getNotifyCount()+"");
//                        System.out.println("setDataToUI noticeNum=" + mainNumber.getNotifyCount());
                    }
                } catch (ContentException e) {
                    e.printStackTrace();
                }

             }else{
                //非用户:进行提示用户需要:

            }

//             System.out.println("MINEActivity portraitUrl:"+portraitUrl);
            //进行初始化头像
            BitmapUtil.getInstance().displayUserPic(userPic,portraitUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && //
                        (loginType!=null && "login".equals(loginType))){
            finish();
            Intent intent = new Intent(MineActivity.this,MainActivity.class);
            startActivity(intent);
            return true;
        }
        //返回值
        return super.onKeyDown(keyCode,event);
    }
}
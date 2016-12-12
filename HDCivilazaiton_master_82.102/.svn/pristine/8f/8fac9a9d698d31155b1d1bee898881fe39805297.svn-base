package com.zhjy.hdcivilization.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.progressbar.KProgressHUD;
import com.zhjy.hdcivilization.protocol.LoginProtocol;
import com.zhjy.hdcivilization.protocol.SendCodeProtocol;
import com.zhjy.hdcivilization.utils.FileUtils;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.NetUtils;
import com.zhjy.hdcivilization.utils.SharedPreferencesManager;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.ToolUtils;
import com.zhjy.hdcivilization.utils.UiUtils;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author :huangxianfeng on 2016/8/2.
 * 手机登录界面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ImageView btnBack;
    private RelativeLayout rl_back;
    EditText edit_Number;
    EditText edit_Code;
    static Button send_Code;
    Button login;
    String number,code;
    private KProgressHUD hud;
    TextView timeCount;
    static LinearLayout aginSendCodeTime;
    static TimeCount time;
    private LoginSuccessPopupWindow popupWindow;
    private String channelId;
    private String codeError="验证码获取失败";
    private String loginError="登录失败";
    private Intent mIntent;
    public static String ISFROM_SPLASHACTIVITY="ISFROM_SPLASHACTIVITY";
    public static String ISFROM_OTHRES="ISFROM_OTHRES";
    private boolean isFromSplashActivity=false;
    private boolean isFromOthers=false;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HDCivilizationConstants.SEND_CODE:
                   String state = (String)msg.obj;
                    hud.dismiss();
                    SharedPreferencesManager.put(UiUtils.getInstance().getContext(), HDCivilizationConstants.IS_LOGIN, "true");
                    UiUtils.getInstance().showToast("验证码发送成功！");
                    break;

                case HDCivilizationConstants.LOAD_REQUEST:
                    hud.dismiss();
                    //登录成功直接进入"我的界面"
                    Intent intent = new Intent(LoginActivity.this,MineActivity.class);
                    intent.putExtra("loginType", "login");
                    startActivity(intent);
                    finish();
                    break;

                case HDCivilizationConstants.ERROR_CODE:
                    if (msg.getData().getInt(HDCivilizationConstants.ACTION_CODE) == HDCivilizationConstants.HEAD_DATA){
                        UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                        hud.dismiss();
                    }
                    break;
            }
        }
    };

    /**
     * 成功登陆我的界面
     */
    private void startActivitys() {
        if(isFromSplashActivity){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(LoginActivity.this,MineActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        customLayout = R.layout.activity_login;
        super.onCreate(savedInstanceState);

        isFromSplashActivity=getIntent().getBooleanExtra(ISFROM_SPLASHACTIVITY,false);
        isFromOthers=getIntent().getBooleanExtra(ISFROM_OTHRES,false);
    }

    @Override
    protected void initViews() {
        if (time==null){
            synchronized (LoginActivity.class){
                if (time==null){
                    time = new TimeCount(60000, 1000);
                }
            }
        }
        //百度唯一的标识
        channelId = (String) SharedPreferencesManager.get(UiUtils.getInstance().getContext(), HDCivilizationConstants.CHANNELID, "");
        btnBack = (ImageView)findViewById(R.id.btn_back);
        rl_back = (RelativeLayout)findViewById(R.id.rl_back);
        edit_Number = (EditText)findViewById(R.id.edit_iphone_number);
        edit_Code = (EditText)findViewById(R.id.edit_iphone_code);
        send_Code = (Button)findViewById(R.id.send_iphone_code);
        login = (Button)findViewById(R.id.login);
        timeCount = (TextView)findViewById(R.id.time_count);
        //进行初始化用户信息
        try {
            User user= UserDao.getInstance().getLastLoginUser();
            edit_Number.setText(user.getAccountNumber());
            edit_Code.setText(user.getSendCode());
        } catch (ConnectException e) {
            e.printStackTrace();
        }
        aginSendCodeTime = (LinearLayout)findViewById(R.id.agin_iphone_code);
        time.setTimeCount(timeCount);
        time.setSend_Code(send_Code);
        time.setAginSendCodeTime(aginSendCodeTime);

        System.out.println("service___time.Num = " + time.timeCountNum);
        if (time.timeCountNum==0){
            aginSendCodeTime.setVisibility(View.GONE);
            send_Code.setVisibility(View.VISIBLE);
        }else{
            aginSendCodeTime.setVisibility(View.VISIBLE);
            send_Code.setVisibility(View.GONE);
            timeCount.setText(time.timeCountNum+"");
        }
    }

    @Override
    protected void initInitevnts() {
        rl_back.setOnClickListener(this);
        login.setOnClickListener(this);
        send_Code.setOnClickListener(this);
        //TODO
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                if(!isFromOthers){
                    UserDao.getInstance().updateAllUserLocalState(false);
                    finish();
                    startActivitys();
                }else{
                    finish();
                }
                break;
            case R.id.send_iphone_code:
                sendCode();
                break;
            case R.id.login:
                LoginApp();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            //如果按返回键
            if(!isFromOthers){
                UserDao.getInstance().updateAllUserLocalState(false);
                startActivitys();
                finish();
            }else{
                finish();
            }
        }
        return true;
    }

    /**发送验证码**/
    private void sendCode() {
        if (!checkNumber()){
            boolean isMobileNO = ToolUtils.getInstance().isMobileNo(number);
            if (isMobileNO){
                /***60秒开始计时**/

                aginSendCodeTime.setVisibility(View.VISIBLE);
                send_Code.setVisibility(View.GONE);
                time.start();
                /***开启提示发送中**/
                hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("验证码发送中").setCancellable(false);
                hud.setCancellable(false);
                hud.show();
                final String imei = (String) SharedPreferencesManager.get(UiUtils.getInstance().getContext(), HDCivilizationConstants.IMEI, "");
                ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        try {
                            UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                            LinkedHashMap<String,String> paramsMap = new LinkedHashMap<String, String>();
                            paramsMap.put("tranCode","AROUND0014");
                            paramsMap.put("phoneNum", number);
                            paramsMap.put("sessionId", channelId);//百度云推送唯一标示
                            paramsMap.put("userId", "");
                            paramsMap.put("imei", imei);
                            urlParamsEntity.setParamsHashMap(paramsMap);
                            SendCodeProtocol sendCodeProtocol = new SendCodeProtocol();
                            urlParamsEntity.HDCURL= UrlParamsEntity.CURRENT_ID;
//                            urlParamsEntity.HDCURL="http://192.168.82.116:8080/cs/supervision/Service/ServiceGate.jsp?";
                            sendCodeProtocol.setActionKeyName(codeError);
                            message.obj=sendCodeProtocol.loadData(urlParamsEntity);
                            message.what = HDCivilizationConstants.SEND_CODE;
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
                            message.what = HDCivilizationConstants.ERROR_CODE;
                            bundle.putString(HDCivilizationConstants.ERROR_CONTENT,  e.getErrorContent());
                            bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.HEAD_DATA);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                });
            }else{
                UiUtils.getInstance().showToast("手机号码输入有误！");
            }
        }else{
            UiUtils.getInstance().showToast("手机号码不能为空！");
        }
    }


    /**登录App用户使用***/
    private void LoginApp() {
        if (!checkLoginName()){
            /****正则表达式验证手机号码**/
            boolean isMobileNO = ToolUtils.getInstance().isMobileNo(number);
            if (isMobileNO){
                if (NetUtils.getInstance().checkNetwork(UiUtils.getInstance().getContext())){
                    hud = KProgressHUD.create(LoginActivity.this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("登录中").setCancellable(false);
                    hud.setCancellable(false);
                    hud.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            hud.dismiss();
                            ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                                @Override
                                public void run() {
                                    Message message = new Message();
                                    Bundle bundle = new Bundle();
                                    try {
                                        UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                                        LinkedHashMap<String,String> paramsMap = new LinkedHashMap<String, String>();
                                        paramsMap.put("tranCode","AROUND0013");
                                        paramsMap.put("phoneNum",number);
                                        paramsMap.put("validateNum",code);
                                        paramsMap.put("userId", "");
                                        paramsMap.put("channelId", channelId);
                                        urlParamsEntity.setParamsHashMap(paramsMap);
                                        urlParamsEntity.HDCURL= UrlParamsEntity.CURRENT_ID;
                                        LoginProtocol loginProtocol = new LoginProtocol();
                                        loginProtocol.setNumber(number);
                                        loginProtocol.setSendCode(code);
                                        loginProtocol.setActionKeyName(loginError);
                                        message.obj = loginProtocol.loadData(urlParamsEntity);
                                        message.what = HDCivilizationConstants.LOAD_REQUEST;
                                        handler.sendMessage(message);
                                    } catch (JsonParseException e) {
                                        e.printStackTrace();
                                        message.what = HDCivilizationConstants.ERROR_CODE;
                                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT,e.getMessage());
                                        bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.HEAD_DATA);
                                        message.setData(bundle);
                                        handler.sendMessage(message);
                                    } catch (ContentException e) {
                                        e.printStackTrace();
                                        if(e.getErrorCode()== HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE){
                                            //权限过低的处理: // TODO: 2016/9/20
                                            message.what = HDCivilizationConstants.ERROR_CODE;
                                            bundle.putString(HDCivilizationConstants.ERROR_CONTENT,  e.getErrorContent());
                                            bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.HEAD_DATA);
                                            message.setData(bundle);
                                            handler.sendMessage(message);
                                        }else{
                                            message.what = HDCivilizationConstants.ERROR_CODE;
                                            bundle.putString(HDCivilizationConstants.ERROR_CONTENT,  e.getErrorContent());
                                            bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.HEAD_DATA);
                                            message.setData(bundle);
                                            handler.sendMessage(message);
                                        }

                                    }
                                }
                            });
                        }
                    }, 2000);
                }else{
                    UiUtils.getInstance().showToast("请查看网络！");
                }
            }else{
                UiUtils.getInstance().showToast("手机号码输入有误！");
            }
        }else{
            UiUtils.getInstance().showToast("手机号或者验证码为空");
        }
    }


    /****校验手机号码和验证码是否为空**/
    private boolean checkLoginName() {
        number = edit_Number.getText().toString().trim();
        code  = edit_Code.getText().toString().trim();
        return number.trim().equals("") || code.trim().equals("");
    }
    /****校验手机号码和验证码是否为空**/
    private boolean checkNumber() {
        number = edit_Number.getText().toString().trim();
        return number.trim().equals("");
    }

    /**
     * @author :huangxianfeng on 2016/8/2.
     * 计时器
     */
    public class TimeCount extends CountDownTimer {

        long timeCountNum;

        private TextView timeCount;

        private LinearLayout aginSendCodeTime;

        private Button send_Code;

        public void setAginSendCodeTime(LinearLayout aginSendCodeTime) {
            this.aginSendCodeTime = aginSendCodeTime;
        }

        public void setSend_Code(Button send_Code) {
            this.send_Code = send_Code;
        }

        public TextView getTimeCount() {
            return timeCount;
        }

        public void setTimeCount(TextView timeCount) {
            this.timeCount = timeCount;
        }

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onFinish() {//记时完毕
            // TODO Auto-generated method stub
            aginSendCodeTime.setVisibility(View.GONE);
            send_Code.setVisibility(View.VISIBLE);
            timeCountNum=0;
            System.out.println("onTick.....running111 = ");
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程中
            // TODO Auto-generated method stub
            if (millisUntilFinished / 1000 == 1) {
                timeCount.setText(1 + "");
            } else if (millisUntilFinished / 1000 > 1) {
                System.out.println("onTick.....running = "+ millisUntilFinished / 1000);
                timeCountNum = millisUntilFinished / 1000;
                timeCount.setText(millisUntilFinished / 1000 +"");
            }
        }
    }

}

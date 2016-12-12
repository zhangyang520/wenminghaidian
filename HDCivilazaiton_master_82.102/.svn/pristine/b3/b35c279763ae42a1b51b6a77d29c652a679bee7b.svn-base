package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.protocol.LoginProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.SharedPreferencesManager;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.UiUtils;

import java.net.ConnectException;
import java.util.LinkedHashMap;

/**
 * Created by zhangyang on 2016/8/31.
 */
public class SplashActivity extends BaseActivity {

    private String loginError="登录失败";

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HDCivilizationConstants.LOAD_REQUEST:
                    //登录成功:直接进入首页
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;

                case HDCivilizationConstants.ERROR_CODE:
                    //登录失败:
                    Intent intent2 = new Intent(SplashActivity.this, LoginActivity.class);
                    intent2.putExtra(LoginActivity.ISFROM_SPLASHACTIVITY,true);
                    startActivity(intent2);
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        customLayout= R.layout.activity_splash;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        if(!isTaskRoot()){
            finish();
        }else{
            try {
                final User user= UserDao.getInstance().getLastLoginUser();
//            //进行自动登录
                final String channelId = (String) SharedPreferencesManager.get(UiUtils.getInstance().getContext(), HDCivilizationConstants.CHANNELID, "");
                ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        try {
                            UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                            LinkedHashMap<String,String> paramsMap = new LinkedHashMap<String, String>();
                            paramsMap.put("tranCode","AROUND0013");
                            paramsMap.put("phoneNum",user.getAccountNumber());
                            paramsMap.put("validateNum",user.getSendCode());
                            paramsMap.put("userId", "");
                            paramsMap.put("channelId", channelId);
                            urlParamsEntity.setParamsHashMap(paramsMap);
                            urlParamsEntity.HDCURL= UrlParamsEntity.CURRENT_ID;
                            LoginProtocol loginProtocol = new LoginProtocol();
                            loginProtocol.setNumber(user.getAccountNumber());
                            loginProtocol.setSendCode(user.getSendCode());
                            loginProtocol.setActionKeyName("");
                            message.obj = loginProtocol.loadData(urlParamsEntity,2000);
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
            } catch (ConnectException e) {
                e.printStackTrace();
                //如果没有最新登录的用户,直接跳入到首页
                UiUtils.getInstance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        //BooleanExtra(ISFROM_SPLASHACTIVITY,false);
                        intent.putExtra(LoginActivity.ISFROM_SPLASHACTIVITY,true);
                        startActivity(intent);
                        finish();
                    }
                }, HDCivilizationConstants.SPLASH_DELAY_TIME_OUT);
            }
        }
    }

    @Override
    protected void initInitevnts() {

    }
}

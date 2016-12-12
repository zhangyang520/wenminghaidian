package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.SystemSettingDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.SystemSetting;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.progressbar.KProgressHUD;
import com.zhjy.hdcivilization.protocol.LoginOutProtocol;
import com.zhjy.hdcivilization.protocol.SettingPushTypeProtocol;
import com.zhjy.hdcivilization.utils.DataCleanManager;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.SharedPreferencesManager;
import com.zhjy.hdcivilization.utils.SysControl;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.WarningPopup;

import java.util.LinkedHashMap;


/**
 * @author :huangxianfeng on 2016/8/4.
 * 我的设置
 */
public class SettingActivity extends BaseActivity implements View.OnTouchListener {

    private RelativeLayout imgLoadMode,clearCahce,textSize,rl_login_out,rl_back;
    private ImageView arrowText,arrowImgLoad;
    private KProgressHUD hud;
    private TextView clearCacheSize;
    private ImageView btnBack;
    private TextView tv_textSize;
    private User user;
    private String userId;
    private CheckBox settingMessage;
    private boolean isSendMessage;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1001:
                    hud.dismiss();
                    UiUtils.getInstance().showToast("清除成功！");
                    break;

                case HDCivilizationConstants.SETTING_PUSHSETTING:
                    UiUtils.getInstance().showToast("设置成功！");
                    hud.dismiss();
                    break;

                case HDCivilizationConstants.LOGIN_OUT_APP:
                    SysControl.goToFirstActivity();
                    UserDao.getInstance().updateAllUserLocalState(false);
                    UiUtils.getInstance().showToast("退出成功！");
                    hud.dismiss();
                    Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
                    intent.putExtra(LoginActivity.ISFROM_SPLASHACTIVITY,true);
                    startActivity(intent);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contentView= UiUtils.getInstance().inflate(R.layout.activity_setting);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        btnBack = (ImageView)findViewById(R.id.btn_back);
        rl_back = (RelativeLayout)findViewById(R.id.rl_back);
        imgLoadMode = (RelativeLayout)findViewById(R.id.rl_img_load);
        clearCahce = (RelativeLayout)findViewById(R.id.rl_clrear_cache);
        textSize = (RelativeLayout)findViewById(R.id.rl_text_size);
        rl_login_out = (RelativeLayout)findViewById(R.id.rl_login_out);

//        settingMessage = (CheckBox)findViewById(R.id.is_send_message);
        clearCacheSize= (TextView)findViewById(R.id.arrow_apply_check);
        arrowText= (ImageView)findViewById(R.id.arrow_text_size);
        arrowImgLoad= (ImageView)findViewById(R.id.arrow_img_load);

        tv_textSize= (TextView)findViewById(R.id.text_size_setting);
        try {
            user = UserDao.getInstance().getLocalUser();
            userId=user.getUserId();
        } catch (ContentException e) {
            e.printStackTrace();
            userId="";
            UiUtils.getInstance().showToast(e.getMessage());
        }
        try {
            SystemSetting systemSetting = SystemSettingDao.getInstance().getSystemSetting(userId);
            System.out.println("systemSetting == null? = "+(systemSetting==null));
            if (systemSetting==null){
                System.out.println("systemSetting ="+(systemSetting==null));
                SystemSetting systemSetting1 = new SystemSetting();
                systemSetting1.setFontSize("中");
                systemSetting1.setUserId(userId);
                SystemSettingDao.getInstance().saveObj(systemSetting1);
                tv_textSize.setText("中");
            }
            tv_textSize.setText(systemSetting.getFontSize());
         } catch (ContentException e){
            e.printStackTrace();
            SystemSetting systemSetting1 = new SystemSetting();
            systemSetting1.setFontSize("中");
            systemSetting1.setUserId(userId);
            SystemSettingDao.getInstance().saveObj(systemSetting1);
            tv_textSize.setText("中");
            System.out.println("systemSetting...e.getMessage() = "+e.getMessage());
         }

        /**********获取缓存的大小，并显示缓存的size***********/
        try {
            String cacheSize = DataCleanManager.getInstance().getTotalCacheSize(UiUtils.getInstance().getContext());
            if ("0.0Byte".equals(cacheSize)){
                clearCacheSize.setText("0MB");
            }else{
                clearCacheSize.setText(cacheSize);
            }
            System.out.println("cacheSize = " +cacheSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initInitevnts() {

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgLoadMode.setOnTouchListener(this);
        clearCahce.setOnTouchListener(this);
        textSize.setOnTouchListener(this);
        textSize.setOnTouchListener(this);
//        settingMessage.setOnTouchListener(this);
        rl_login_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WarningPopup.getInstance().showPopup(//
                        contentView, //
                        new WarningPopup.BtnClickListener() {
                            @Override
                            public void btnOk() {
                                loginOutApp();
                            }

                            @Override
                            public void btnCancel() {
                                WarningPopup.getInstance().dismiss();
                            }
                        }, true, true, "是否要注销?");
            }
        });


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
//            case R.id.is_send_message://设置是否接收推送消息
//                if (settingMessage.isChecked()){//true
//                    System.out.println("settingMessage.isChecked()1 = " + settingMessage.isChecked());
//                    isSendMessage=settingMessage.isChecked();
//                    settingSendMessage(isSendMessage);
//                    settingMessage.setBackgroundResource(R.drawable.setting_is_send_message);
//
//                }else{//false
//                    isSendMessage=settingMessage.isChecked();
//                    System.out.println("settingMessage.isChecked()2 = "+settingMessage.isChecked());
//                    settingSendMessage(isSendMessage);
//                    settingMessage.setBackgroundResource(R.drawable.setting_is_send_message_press);
//                }
//                break;

            case R.id.rl_img_load:
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        arrowImgLoad.setBackgroundResource(R.drawable.mine_arrow_right_press);
                        break;
                    case MotionEvent.ACTION_UP:
                        arrowImgLoad.setBackgroundResource(R.drawable.mine_arrow_right);
                        break;
                }
                break;

            /***清除缓存**/
            case R.id.rl_clrear_cache:
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:

                        break;
                    case MotionEvent.ACTION_UP:
                        displayCancelCache();
                        break;
                }
                break;

            case R.id.rl_text_size://设置字体大小
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        arrowText.setBackgroundResource(R.drawable.mine_arrow_right_press);
                        break;
                    case MotionEvent.ACTION_UP:
                        arrowText.setBackgroundResource(R.drawable.mine_arrow_right);
                        Intent intent = new Intent(SettingActivity.this,TypeFaceSettingActivity.class);
                        startActivityForResult(intent, HDCivilizationConstants.REQUEST_CODE);
                        break;
                }
                break;

            case R.id.rl_login_out:
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        arrowText.setBackgroundResource(R.drawable.mine_arrow_right_press);
                        break;
                    case MotionEvent.ACTION_UP:
                        arrowText.setBackgroundResource(R.drawable.mine_arrow_right);
                        break;
                }
                break;
        }
        return false;
    }

    //注销App登录
    private void loginOutApp() {
        /***注销App发送中**/
        hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("注销中").setCancellable(false);
        hud.setCancellable(false);
        hud.show();
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                try {
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode", "AROUND0041");
                    paramsMap.put("userId", userId);
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    LoginOutProtocol loginOutProtocol = new LoginOutProtocol();
                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                    loginOutProtocol.setActionKeyName("退出登录失败");
                    message.obj = loginOutProtocol.loadData(urlParamsEntity);
                    message.what = HDCivilizationConstants.LOGIN_OUT_APP;
                    handler.sendMessage(message);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT,  e.getMessage());
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.HEAD_DATA);
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (ContentException e){
                    e.printStackTrace();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT,  e.getErrorContent());
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.HEAD_DATA);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        });
    }

    //是否能够发送通知的设置
    private void settingSendMessage(final boolean isSendMessage) {
        /***开启设置发送中**/
        hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("消息设置中").setCancellable(false);
        hud.setCancellable(false);
        hud.show();
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                try {
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode", "AROUND0042");
                    paramsMap.put("userId", userId);
                    if (isSendMessage == true) {
                        paramsMap.put("pushmsgFlag", "1");
                    } else {
                        paramsMap.put("pushmsgFlag", "0");
                    }
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    SettingPushTypeProtocol settingPushTypeProtocol = new SettingPushTypeProtocol();
                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
//                            urlParamsEntity.HDCURL="http://192.168.82.116:8080/cs/supervision/Service/ServiceGate.jsp?";
                    settingPushTypeProtocol.setActionKeyName("设置取消推动失败");
                    message.obj = settingPushTypeProtocol.loadData(urlParamsEntity);
                    message.what = HDCivilizationConstants.SETTING_PUSHSETTING;
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
    }


    /**
     * 清除缓存
     */
    private void displayCancelCache() {
        hud = KProgressHUD.create(SettingActivity.this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("正在清除缓存").setCancellable(false);
        hud.setCancellable(false);
        hud.show();
        try {
            DataCleanManager.getInstance().clearAllCache(UiUtils.getInstance().getContext());
            final String cacheSize = DataCleanManager.getInstance().getTotalCacheSize(UiUtils.getInstance().getContext());
            System.out.println("cacheSize = " + cacheSize);
            handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (cacheSize.equals("0.0Byte")) {
                            hud.dismiss();
                            clearCacheSize.setText("0MB");
                            UiUtils.getInstance().showToast("清除成功！");
                        }else{
                            UiUtils.getInstance().showToast("未完全清除!");
                            clearCacheSize.setText(cacheSize);
                        }
                }}, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==HDCivilizationConstants.REQUEST_CODE && resultCode==HDCivilizationConstants.LOAD_RESULT_CODE){
            String textSize = data.getStringExtra(HDCivilizationConstants.RESULT_CODE);
            System.out.println("textSize = " + textSize);
            SharedPreferencesManager.put(UiUtils.getInstance().getContext(), HDCivilizationConstants.TEXT_SIZE, textSize);

            try {
                SystemSetting systemSetting = SystemSettingDao.getInstance().getSystemSetting(userId);
                if (systemSetting!=null){
                    systemSetting.setFontSize(textSize);
                    systemSetting.setUserId(userId);
                    SystemSettingDao.getInstance().updateSize(systemSetting, userId, textSize);
                    tv_textSize.setText(textSize);
                }
            } catch (ContentException e) {
                e.printStackTrace();
                SystemSetting systemSetting1 = new SystemSetting();
                systemSetting1.setFontSize(textSize);
                systemSetting1.setUserId(userId);
                SystemSettingDao.getInstance().saveObj(systemSetting1);
                tv_textSize.setText(textSize);
            }

        }
    }
}

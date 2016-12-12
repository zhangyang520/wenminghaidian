package com.zhjy.hdcivilization.inner;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.zhjy.hdcivilization.receiver.HDCiviReceiver;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.SharedPreferencesManager;
import com.zhjy.hdcivilization.utils.SysControl;
import com.zhjy.hdcivilization.utils.UiUtils;

import java.util.HashMap;
import java.util.Map;


public abstract class BaseActivity extends AppCompatActivity {

    protected int customLayout = 0;
    protected  View contentView;
    public String TAG ="BaseActivtiy.class";
    private Map<Integer, Runnable> allowablePermissionRunnables = new HashMap<>();
    private Map<Integer, Runnable> disallowablePermissionRunnables = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "jkA4fj3haooGCX9M1xdzRTmR");
        /**获取IMEI号码**/
        String imeiSP = (String)SharedPreferencesManager.get(UiUtils.getInstance().getContext(), HDCivilizationConstants.IMEI, "");
        if (imeiSP.equals("") || imeiSP==null){
            requestPermission(HDCivilizationConstants.READ_PHONE_STATE_REQUEST_CODE, "android.permission.READ_PHONE_STATE", new Runnable() {
                @Override
                public void run() {
                    TelephonyManager mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String imei = mTm.getDeviceId();
                    System.out.println("imei = " + imei);
                    SharedPreferencesManager.put(UiUtils.getInstance().getContext(), HDCivilizationConstants.IMEI, imei);
                }
            }, new Runnable() {
                @Override
                public void run() {
                    System.out.println("BaseActivity permission READ_PHONE_STATE disallowable....");
                }
            });

        }

        if (customLayout != 0) {
            setContentView(UiUtils.getInstance().inflate(customLayout));
            SysControl.add(this);
        }else{
            setContentView(contentView);
            SysControl.add(this);
        }

        //进行给HDCiviReceiver设置currentActivity
        HDCiviReceiver.currentActivity=this;
        initViews();
        initInitevnts();
    }


    /**
     * 组件初始化
     */
    protected abstract void initViews();

    /**
     * 事件初始化
     */
    protected abstract void initInitevnts();


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public interface BeforeLogout {
        public void before();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        HDCiviReceiver.currentActivity=null;
        SysControl.removeCurrent(this);
    }

    /**
     * 请求权限
     * @param id 请求授权的id 唯一标识即可
     * @param permission 请求的权限
     * @param allowableRunnable 同意授权后的操作
     * @param disallowableRunnable 禁止权限后的操作
     */
    protected void requestPermission(int id, String permission, Runnable allowableRunnable, Runnable disallowableRunnable) {
        if(allowableRunnable!=null){
            allowablePermissionRunnables.put(id, allowableRunnable);
        }

        if (disallowableRunnable != null) {
            disallowablePermissionRunnables.put(id, disallowableRunnable);
        }

        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            //减少是否拥有权限checkCallPhonePermission != PackageManager.PERMISSION_GRANTED
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                //弹出对话框接收权限
                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{permission}, id);
                return;
            } else {
                if(allowableRunnable!=null){
                    allowableRunnable.run();
                }
            }
        } else {
            boolean result = PermissionChecker.checkSelfPermission(this, permission)
                    == PermissionChecker.PERMISSION_GRANTED;
            if(!result){
                //如果未授权
                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{permission}, id);
            }else{
                if(allowableRunnable!=null){
                    allowableRunnable.run();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Runnable allowRun = allowablePermissionRunnables.get(requestCode);
            if(allowRun!=null){
                allowRun.run();
            }

        } else {
            Runnable disallowRun = disallowablePermissionRunnables.get(requestCode);
            if(disallowRun!=null){
                disallowRun.run();
            }
        }
    }
}

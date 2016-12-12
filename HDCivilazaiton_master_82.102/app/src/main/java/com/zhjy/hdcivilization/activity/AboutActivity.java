package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.AppInfo;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.progressbar.KProgressHUD;
import com.zhjy.hdcivilization.protocol.AppCheckProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.SDCardUtil;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.utils.VersionUtil;
import com.zhjy.hdcivilization.view.WarningPopup;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * @author :huangxianfeng on 2016/8/4.
 * 关于
 */
public class AboutActivity extends BaseActivity {

    private Button subInfo;
    private Button btn_check_update;
    private ImageView btnBack;
    RelativeLayout rl_back;
    KProgressHUD hud1;
    private final int CHECK_SUCCESS=101;//检测成功
    private final int CHECK_FAILURE=102;//检测成功
    private String actionKeyName="软件版本号获取失败";
    private TextView tv_version;//版本
    private String versionUpGrade;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case CHECK_SUCCESS://检测成功:进行提示用户
                    hud1.dismiss();
                    //进行提示用户
                    final AppInfo appInfo=(AppInfo)message.obj;
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
                                        final KProgressHUD hud2 = KProgressHUD.create(AboutActivity.this)
                                                .setStyle(KProgressHUD.Style.BAR_DETERMINATE)
                                                .setLabel("应用下载中...")
                                                .setCancellable(false);
                                        hud2.setMaxProgress(100);
                                        HttpUtils httpUtil=new HttpUtils();
                                        httpUtil.configSoTimeout(HDCivilizationConstants.NEWWORK_TIME_OUT);
                                        httpUtil.configTimeout(HDCivilizationConstants.NEWWORK_TIME_OUT);
                                        long currentTime=System.currentTimeMillis();
                                        final String FilePathName= SDCardUtil.getInstance().getDownloadPath()+File.separator+currentTime+".apk";
                                        final HttpHandler handler=httpUtil.download(appInfo.getAppUrl(), FilePathName, true, new RequestCallBack<File>() {
                                            @Override
                                            public void onSuccess(ResponseInfo<File> responseInfo) {
                                                hud2.dismiss();
                                                UiUtils.getInstance().showToast("应用下载成功");
                                                //进行提示框是否安装:
                                                WarningPopup.getInstance().showPopup(contentView, new WarningPopup.BtnClickListener() {
                                                    @Override
                                                    public void btnOk() {
                                                        WarningPopup.getInstance().dismiss();
                                                        VersionUtil.installApk(AboutActivity.this, FilePathName);
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
//                                    }
                                }

                                @Override
                                public void btnCancel(){
                                    //取消
                                    WarningPopup.getInstance().dismiss();
                                }
                            },true,true,"最新版本是:"+appInfo.getAppVersionCode()+",是否下载?");
                        }else{
                            UiUtils.getInstance().showToast("已经是最新版本!");
                        }
                    } catch (ContentException e) {
                        e.printStackTrace();
                        UiUtils.getInstance().showToast(e.getErrorContent());
                    }
                    break;

                case CHECK_FAILURE://检测失败:提示用户
                    hud1.dismiss();
                    UiUtils.getInstance().showToast(message.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contentView = UiUtils.getInstance().inflate(R.layout.activity_about);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        //百度云推送通知控制版本升级标签
        versionUpGrade = getIntent().getStringExtra(HDCivilizationConstants.VERSIONUP);

        btnBack = (ImageView)findViewById(R.id.btn_back);
        subInfo = (Button)findViewById(R.id.sub_info);
        btn_check_update = (Button)findViewById(R.id.btn_check_update);
        tv_version = (TextView)findViewById(R.id.tv_version);
        rl_back = (RelativeLayout)findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_version.getPaint().setFakeBoldText(true);
        tv_version.setText("版本号"+ VersionUtil.getVersionName(this));

//        AssetManager mgr=getAssets();//得到AssetManager
//        Typeface tf=Typeface.createFromAsset(mgr, "fonts/ttf.ttf");//根据路径得到Typeface
//        tv_version.setTypeface(tf);//设置字体
        
        versionUpGrade(versionUpGrade);
    }

    //通知消息控制版本升级
    private void versionUpGrade(String versionUpGrade) {
        if (versionUpGrade.equals(HDCivilizationConstants.VERSIONUPGRADE)){
            try {
                final User user= UserDao.getInstance().getLocalUser();
                if(Integer.parseInt(user.getIdentityState())>=
                        Integer.parseInt(UserPermisson.ORDINARYSTATE.getType())){
                    //普通用户
                    hud1 = KProgressHUD.create(AboutActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("检测版本中...")
                            .setCancellable(false);
                    hud1.setCancellable(false);
                    hud1.show();
                    ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            //进行执行
                            Message message=new Message();
                            Bundle bundle=new Bundle();
                            try {
                                AppCheckProtocol appCheckProtocol=new AppCheckProtocol();
                                UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                                LinkedHashMap<String,String> paramsMap = new LinkedHashMap<String, String>();
                                paramsMap.put("tranCode","AROUND0030");
                                paramsMap.put("userId", user.getUserId());
                                urlParamsEntity.setParamsHashMap(paramsMap);
                                urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                                appCheckProtocol.setActionKeyName(actionKeyName);
                                message.obj = appCheckProtocol.loadData(urlParamsEntity);
                                message.what = CHECK_SUCCESS;
                                handler.sendMessage(message);
                            } catch (JsonParseException e) {
                                e.printStackTrace();
                                message.what = CHECK_FAILURE;
                                bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                                message.setData(bundle);
                                handler.sendMessage(message);
                            } catch (ContentException e) {
                                e.printStackTrace();
                                if(e.getErrorCode()== HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE){
                                    //按照AppCheckProtocol 解析的时候判断:只有普通用户被禁用  暂时先进行提示!// TODO: 2016/9/19
                                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                                    //需要进行判断权限过低的情况
                                    message.what = CHECK_FAILURE;
                                    message.setData(bundle);
                                    handler.sendMessage(message);
                                }else{
                                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                                    //需要进行判断权限过低的情况
                                    message.what = CHECK_FAILURE;
                                    message.setData(bundle);
                                    handler.sendMessage(message);
                                }
                            }
                        }
                    });
                }else{
                    //不是普通用户:需要进行提示用户退出 // TODO: 2016/9/19


                }
            } catch (ContentException e) {
                e.printStackTrace();
                UiUtils.getInstance().showToast(e.getErrorContent());
            }
        }
    }

    @Override
    protected void initInitevnts() {
        subInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this,SubInfoActivity.class));
            }
        });

        /**
         * 进行检测更新
         */
        btn_check_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final User user= UserDao.getInstance().getLocalUser();
                    if(Integer.parseInt(user.getIdentityState())>=
                                    Integer.parseInt(UserPermisson.ORDINARYSTATE.getType())){
                        //普通用户
                        hud1 = KProgressHUD.create(AboutActivity.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel("检测版本中...")
                                .setCancellable(false);
                        hud1.setCancellable(false);
                        hud1.show();
                        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                //进行执行
                                Message message=new Message();
                                Bundle bundle=new Bundle();
                                try {
                                    AppCheckProtocol appCheckProtocol=new AppCheckProtocol();
                                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                                    LinkedHashMap<String,String> paramsMap = new LinkedHashMap<String, String>();
                                    paramsMap.put("tranCode","AROUND0030");
                                    paramsMap.put("userId", user.getUserId());
                                    urlParamsEntity.setParamsHashMap(paramsMap);
                                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                                    appCheckProtocol.setActionKeyName(actionKeyName);
                                    message.obj = appCheckProtocol.loadData(urlParamsEntity);
                                    message.what = CHECK_SUCCESS;
                                    handler.sendMessage(message);
                                } catch (JsonParseException e) {
                                    e.printStackTrace();
                                    message.what = CHECK_FAILURE;
                                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                                    message.setData(bundle);
                                    handler.sendMessage(message);
                                } catch (ContentException e) {
                                    e.printStackTrace();
                                    if(e.getErrorCode()== HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE){
                                        //按照AppCheckProtocol 解析的时候判断:只有普通用户被禁用    暂时先进行提示!// TODO: 2016/9/19
                                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                                        //需要进行判断权限过低的情况
                                        message.what = CHECK_FAILURE;
                                        message.setData(bundle);
                                        handler.sendMessage(message);
                                    }else{
                                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                                        //需要进行判断权限过低的情况
                                        message.what = CHECK_FAILURE;
                                        message.setData(bundle);
                                        handler.sendMessage(message);
                                    }
                                }
                            }
                        });
                    }else{
                        //不是普通用户:需要进行提示用户退出 //// TODO: 2016/9/19
                        System.out.println("commen user:"+user.toString());
                    }
                } catch (ContentException e) {
                    e.printStackTrace();
                    //用户尚未登录 //// TODO: 2016/9/19
                    UiUtils.getInstance().showToast(e.getErrorContent());
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}

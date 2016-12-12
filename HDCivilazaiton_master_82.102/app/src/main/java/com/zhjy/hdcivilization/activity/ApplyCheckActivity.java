package com.zhjy.hdcivilization.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.ApplyCheckDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.ApplyState;
import com.zhjy.hdcivilization.entity.HDC_ApplyCheck;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.protocol.ApplyCheckProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.UiUtils;

import java.util.LinkedHashMap;

/**
 * @author :huangxianfeng on 2016/8/4.
 * 申请查询
 */
public class ApplyCheckActivity extends BaseActivity {

    private ImageView btnBack;
    private TextView date1,tv_applySuccess,applySuccessTime,tv_applying,applyingTime,tv_applySub;
    private ImageView img_applyFail1,img_applySuccess,img_applying,img_applySub;
    private TextView tv_applyFail1,applyFailTime1,dateSuccess,dateApplying,dateApplySub,applySubTime;
    private RelativeLayout applyFail,applySuccess,applyIng,applySub,rl_back;
    private String loadFirstPage = "数据加载失败,";

    private HDC_ApplyCheck hdc_applyCheck  = new HDC_ApplyCheck();
    private String keyName="申请查询";
    private String actionKeyName="申请信息查询失败";
    private User user;
    private ImageView imgLine1,imgLine2,imgLine3;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HDCivilizationConstants.REFRESH_PAGE:
                    hdc_applyCheck = (HDC_ApplyCheck)msg.obj;
                    if (hdc_applyCheck!=null){
                        applyCheckState(hdc_applyCheck);
//                        UiUtils.getInstance().showToast("申请查询成功！");
                    }else{
                        UiUtils.getInstance().showToast("申请信息查询失败！");
                    }
                    break;

                case HDCivilizationConstants.ERROR_CODE:
                     if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)== HDCivilizationConstants.REFRESH_PAGE){
                        UiUtils.getInstance().showToast( msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                    }
                    break;
            }
        }
    };

    private void applyCheckState(HDC_ApplyCheck applyCheck) {
        String state = applyCheck.getApprovalState();
        System.out.println("state=" + state);
        if (state.equals(ApplyState.APPLYSUB.getType())){//1提交申请
            applySub.setVisibility(View.VISIBLE);
            dateApplySub.setText(applyCheck.getApplyDate());
            applySubTime.setText(applyCheck.getApplyTime());
            applyIng.setVisibility(View.GONE);
            applySuccess.setVisibility(View.GONE);
            applyFail.setVisibility(View.GONE);
            //对竖线的控制
            imgLine1.setVisibility(View.GONE);
            imgLine2.setVisibility(View.GONE);
            imgLine3.setVisibility(View.GONE);
        }else if (state.equals(ApplyState.APPLYING.getType())){//2审核中
            //提交状态
            applySub.setVisibility(View.VISIBLE);
            dateApplySub.setText(applyCheck.getApplyDate());
            applySubTime.setText(applyCheck.getApplyTime());
            applyIng.setVisibility(View.VISIBLE);
            dateApplying.setText(applyCheck.getApplyDate());
            applyingTime.setText(applyCheck.getApplyTime());
            applySuccess.setVisibility(View.GONE);
            applyFail.setVisibility(View.GONE);
            //对竖线的控制
            imgLine1.setVisibility(View.GONE);
            imgLine2.setVisibility(View.GONE);
            imgLine3.setVisibility(View.VISIBLE);
        } else if (state.equals(ApplyState.APPLYFAIL.getType())){//3失败
            applySub.setVisibility(View.VISIBLE);
            dateApplySub.setText(applyCheck.getApplyDate());
            applySubTime.setText(applyCheck.getApplyTime());

            applyIng.setVisibility(View.VISIBLE);
            dateApplying.setText(applyCheck.getApplyDate());
            applyingTime.setText(applyCheck.getApplyTime());

            applySuccess.setVisibility(View.GONE);
//            dateSuccess.setText(applyCheck.getModifyDate());
//            applySuccessTime.setText(applyCheck.getModifyTime());
            applyFail.setVisibility(View.VISIBLE);
            date1.setText(applyCheck.getModifyDate());
            tv_applyFail1.setText("您的申请未通过,"+applyCheck.getDeterminereason());
            applyFailTime1.setText(applyCheck.getModifyTime());
            //对竖线的控制
            imgLine1.setVisibility(View.GONE);
            imgLine2.setVisibility(View.VISIBLE);
            imgLine3.setVisibility(View.VISIBLE);

        }else if (state.equals(ApplyState.APPLYSUCCESS.getType())){//4成功
            applySub.setVisibility(View.VISIBLE);
            dateApplySub.setText(applyCheck.getApplyDate());
            applySubTime.setText(applyCheck.getApplyTime());
            applyIng.setVisibility(View.VISIBLE);
            dateApplying.setText(applyCheck.getApplyDate());
            applyingTime.setText(applyCheck.getApplyTime());

            applySuccess.setVisibility(View.VISIBLE);
            dateSuccess.setText(applyCheck.getModifyDate());
            applySuccessTime.setText(applyCheck.getModifyTime());
            applyFail.setVisibility(View.GONE);

            //对竖线的控制
            imgLine1.setVisibility(View.GONE);
            imgLine2.setVisibility(View.VISIBLE);
            imgLine3.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        customLayout= R.layout.activity_apply_check;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        btnBack = (ImageView)findViewById(R.id.btn_back);

        imgLine1 = (ImageView)findViewById(R.id.img_line1);
        imgLine2 = (ImageView)findViewById(R.id.img_line2);
        imgLine3 = (ImageView)findViewById(R.id.img_line3);

        rl_back = (RelativeLayout)findViewById(R.id.rl_back);

        /**申请失败的情况下**/
        applyFail = (RelativeLayout)findViewById(R.id.apply_no_pass);
        date1 = (TextView)findViewById(R.id.date_apply_check_1);
        img_applyFail1 = (ImageView)findViewById(R.id.img_apply_check_1);
        tv_applyFail1=(TextView)findViewById(R.id.tv_apply_time_1);
        applyFailTime1 = (TextView)findViewById(R.id.apply_time_1);

        /****申请成功的状态**/
        applySuccess = (RelativeLayout)findViewById(R.id.apply_success);
        dateSuccess = (TextView)findViewById(R.id.date_apply_check_2);
        img_applySuccess = (ImageView)findViewById(R.id.img_apply_check_2);
        tv_applySuccess=(TextView)findViewById(R.id.tv_apply_time_2);
        applySuccessTime = (TextView)findViewById(R.id.apply_time_2);

        /***提交申请审核中**/
        applyIng = (RelativeLayout)findViewById(R.id.apply_applying);
        dateApplying = (TextView)findViewById(R.id.date_apply_check_3);
        img_applying = (ImageView)findViewById(R.id.img_apply_check_3);
        tv_applying=(TextView)findViewById(R.id.tv_apply_time_3);
        applyingTime = (TextView)findViewById(R.id.apply_time_3);

        /***成功提交申请***/
        applySub = (RelativeLayout)findViewById(R.id.sub_apply);
        dateApplySub = (TextView)findViewById(R.id.date_apply_check_4);
        img_applySub = (ImageView)findViewById(R.id.img_apply_check_4);
        tv_applySub=(TextView)findViewById(R.id.tv_apply_time_4);
        applySubTime = (TextView)findViewById(R.id.apply_time_4);

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /***********拿到用户对象得到对象Id*************/
        try {
            user = UserDao.getInstance().getLocalUser();
        } catch (ContentException e) {
            e.printStackTrace();
            //用户尚未登录 // TODO: 2016/9/19
            UiUtils.getInstance().showToast(e.getMessage());
            finish();
        }
    }

    @Override
    protected void initInitevnts() {
        HDC_ApplyCheck datas = ApplyCheckDao.getInstance().getApprovalState(user.getUserId());
        if (datas!=null){
            applyCheckState(datas);
            forgetServiceData();
        }else{
            forgetServiceData();
        }
    }

    /**
     * 请求网络
     */
    private void forgetServiceData() {

        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                try {
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String,String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode", "AROUND0018");
                    paramsMap.put("userId", user.getUserId());
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    ApplyCheckProtocol applyCheckProtocol = new ApplyCheckProtocol();
                    applyCheckProtocol.setUser(user);
                    applyCheckProtocol.setActionKeyName(actionKeyName);
                    urlParamsEntity.HDCURL= UrlParamsEntity.CURRENT_ID;
                    message.obj = applyCheckProtocol.loadData(urlParamsEntity);
                    message.what = HDCivilizationConstants.REFRESH_PAGE;
                    handler.sendMessage(message);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REFRESH_PAGE);
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (ContentException e) {
                    e.printStackTrace();
                    if (e.getErrorCode()== HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE) {
                        //权限过低时进行处理: 可能是普通志愿者 //// TODO: 2016/9/19

                        //也可能是普通用户被禁用:进行的业务处理

                    }else{
                        message.what = HDCivilizationConstants.ERROR_CODE;
                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                        bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REFRESH_PAGE);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }
            }
        });
    }
}

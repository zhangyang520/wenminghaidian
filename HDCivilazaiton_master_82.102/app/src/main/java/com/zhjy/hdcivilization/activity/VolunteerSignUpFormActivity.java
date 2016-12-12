package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.ApplyCheckDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.progressbar.KProgressHUD;
import com.zhjy.hdcivilization.protocol.UploadImgProtocol;
import com.zhjy.hdcivilization.protocol.VolunteerApplyFormProtocol;
import com.zhjy.hdcivilization.utils.BankInfo;
import com.zhjy.hdcivilization.utils.BitmapUtil;
import com.zhjy.hdcivilization.utils.FileUtils;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.ImageUtils;
import com.zhjy.hdcivilization.utils.MyRunnable;
import com.zhjy.hdcivilization.utils.NetUtils;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.ToolUtils;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.SelectPicPopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/8/3.
 * 志愿者报名填写的表单
 */
public class VolunteerSignUpFormActivity extends BaseActivity implements View.OnClickListener {

    private EditText editBankCode,editWeiXin,editPay,editName,editIdentityNumber,editAddress,editExperience;
    private GridView gridView;
    private RelativeLayout rl_back;
    private Button subSignUp;
    public String mImagePath;
    public String mPressImagePath;//压缩之后的文件的路径
    private String image1Path="",image2Path="";
    private SelectPicPopupWindow menuWindow;
    private ImageView btnBack;
    private ImageView handImg,handImgBg;
    private String name,address,experience,bankCode,weiXin,identityNumber,pay;
    private KProgressHUD hud;
    final int messageImgWhat=204;//消息的what码
    final int messageImgError=205;//消息的what码
    final int volunteerApplySuccess=206;//上传文明监督提报成功请求码
    final int volunteerApplyFailure=207;//上传文明监督提报成功失败码

    private List<String> imgPathList = new ArrayList<String>();
    List<String> uploadFilePath=new ArrayList<String>();
    int uploadImgCount = 2;//上传总的照片数
    private User user;

    private LinearLayout ll_name,ll_identity,ll_address,ll_bank_num,ll_weixin,ll_zhifubao;
    private RelativeLayout rl_experience,rl_portrait;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case messageImgWhat:
                    //进行判断
                    uploadFilePath.add((String)msg.obj);
//                    if(uploadFilePath.size()==uploadImgCount){
//                        //如果现在上传文件的个数==uploadImgCount-1
//                        //结束了图片的上传模块，进行开始上传内容的模块
//                        UiUtils.getInstance().showToast("进行开始上传内容的模块.....size:" + uploadFilePath.size());
////                            hud.dismiss();
//                        System.out.println("-----3");
//                        processSubmitData();
//                    }

                    processSubmitData2();
                    break;

                case messageImgError:
                    UiUtils.getInstance().showToast(msg.obj+"");
                    if(hud!=null){
                        hud.dismiss();
                    }
                    break;

                case volunteerApplySuccess:
                    System.out.println("-----6");
                    //提报成功
//                    UiUtils.getInstance().showToast("志愿者申请提交成功!");
                    if(hud!=null){
                        hud.dismiss();
                    }
                    //进行删除以前的记录
                    ApplyCheckDao.getInstance().clearApprovalState(user.getUserId());
                    finish();
                    break;

                case volunteerApplyFailure:
                    //提报失败
                    UiUtils.getInstance().showToast(msg.obj+"");
                    if(hud!=null){
                        hud.dismiss();
                    }
                    break;
            }
        }
    };



    /**
     * 进行第二种方式进行提交数据
     */
    private void processSubmitData2() {
        final StringBuilder stringBuilder=new StringBuilder();
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("tranCode","AROUND0017");
        stringBuilder.append("tranCode=").append("AROUND0017");
        params.addQueryStringParameter("userId", user.getUserId());
        stringBuilder.append("&userId=").append(user.getUserId());
        params.addQueryStringParameter("userName", name);
        stringBuilder.append("&userName=").append(user.getUserId());
        params.addQueryStringParameter("identityId", identityNumber);
        stringBuilder.append("&identityId=").append(identityNumber);
        params.addQueryStringParameter("addressName", address);
        stringBuilder.append("&addressName=").append(address);
        params.addQueryStringParameter("portraitUrlIds", getUploadImgStr(uploadFilePath));
        stringBuilder.append("&portraitUrlIds=").append(uploadFilePath);
        params.addQueryStringParameter("volunteerExp", editExperience.getText().toString().trim());
        stringBuilder.append("&volunteerExp=").append(editExperience.getText().toString().trim());
        params.addQueryStringParameter("bankNum", bankCode);
        stringBuilder.append("&bankNum=").append(bankCode);
        params.addQueryStringParameter("weChatNum", weiXin);
        stringBuilder.append("&weChatNum=").append(weiXin);
        params.addQueryStringParameter("zhiFBNum", pay);
        stringBuilder.append("&zhiFBNum=").append(pay);

        final VolunteerApplyFormProtocol volunteerApplyFormProtocol = new VolunteerApplyFormProtocol();
        volunteerApplyFormProtocol.setUserId(user.getUserId());
        HttpUtils http = new HttpUtils();
        http.configSoTimeout(HDCivilizationConstants.NEWWORK_TIME_OUT);
        http.configTimeout(HDCivilizationConstants.NEWWORK_TIME_OUT);
        http.send(HttpRequest.HttpMethod.POST,
                UrlParamsEntity.CURRENT_ID_1,
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        System.out.println("processSubmitData2  onStart......url:"+getRequestUrl()+"...stringBuilder:"+stringBuilder.toString());
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        System.out.println("processSubmitData2  onLoading......" + current + "/" + total);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        System.out.println("processSubmitData2  onSuccess......" + "upload response:" + responseInfo.result);
                        try {
                            volunteerApplyFormProtocol.setActionKeyName("志愿者申请提交失败");
                            volunteerApplyFormProtocol.parseJson(responseInfo.result);
                            Message messsage = Message.obtain();
                            messsage.what = volunteerApplySuccess;
                            handler.sendMessage(messsage);
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                            Message messsage = Message.obtain();
                            messsage.what = volunteerApplyFailure;//失败
                            messsage.obj = e.getMessage();
                            handler.sendMessage(messsage);
                        } catch (ContentException e) {
                            e.printStackTrace();
                            Message messsage = Message.obtain();
                            messsage.what = volunteerApplyFailure;//失败
                            messsage.obj = e.getErrorContent();
                            handler.sendMessage(messsage);
                        }
                    }

                    /*
                      onFailure: org.apache.http.conn.ConnectTimeoutException: Connect to /192.168.83.90:9001 timed out...errorcode:0
                      onFailure: org.apache.http.conn.HttpHostConnectException: Connection to http://192.168.83.90:9001 refused...errorcode:0
                      onFailure......error:0..getMessage:java.net.SocketTimeoutException...msg:java.net.SocketTimeoutException

                       uploadImgProtocol  onFailure......error:0..
                       getMessage:org.apache.http.conn.HttpHostConnectException: Connection to http://192.168.83.90:9001 refused...
                       msg:org.apache.http.conn.HttpHostConnectException: Connection to http://192.168.83.90:9001 refused
                     */
                    @Override
                    public void onFailure(HttpException error, String msg) {
//                        System.out.println("processSubmitData2  onFailure......"+"error:" + //
//                                error.getExceptionCode()+"..getMessage:"+error.getMessage()+"...msg:"+msg);
                        if(error.getMessage().contains("java.net.SocketTimeoutException")||
                                error.getMessage().contains("org.apache.http.conn.ConnectTimeoutException")){
                            Message messsage = Message.obtain();
                            messsage.what = volunteerApplyFailure;//失败
                            messsage.obj = "志愿者申请提交失败,链接超时!";
                            handler.sendMessage(messsage);
                        }else{
                            Message messsage = Message.obtain();
                            messsage.what = volunteerApplyFailure;//失败
                            messsage.obj = "志愿者申请提交失败!";
                            handler.sendMessage(messsage);
                        }
                    }
                });
    }
    /**
     * 进行获取集合中的拼接字符串
     * @param uploadFilePath
     * @return
     */
    private String getUploadImgStr(List<String> uploadFilePath){
        StringBuilder sb=new StringBuilder();
        for (int i=0;i<uploadFilePath.size();++i){
            //重新拼接.....
            sb.append(uploadFilePath.get(i));
        }
        return sb.toString();
    }

    private int currentImageIndex=0;//当前图片的角标
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        customLayout= R.layout.activity_volunteer_sign_up_form;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        btnBack = (ImageView)findViewById(R.id.btn_back);
        rl_back = (RelativeLayout)findViewById(R.id.rl_back);
        editName =(EditText)findViewById(R.id.edit_name);
        editIdentityNumber =(EditText)findViewById(R.id.edit_identity_number);
        editAddress =(EditText)findViewById(R.id.edit_address_deatail);
        editExperience =(EditText)findViewById(R.id.edit_experience_detail);
        editBankCode =(EditText)findViewById(R.id.edit_bank_code);
        editWeiXin =(EditText)findViewById(R.id.edit_weixin);
        editPay =(EditText)findViewById(R.id.edit_pay);
        subSignUp = (Button)findViewById(R.id.sub_sign_info);

        handImg = (ImageView)findViewById(R.id.hand_img_);
        handImgBg =(ImageView)findViewById(R.id.hand_img_bg);

        //Linearlayout
        ll_name =(LinearLayout)findViewById(R.id.ll_name);
        ll_name.setOnClickListener(this);
        ll_identity =(LinearLayout)findViewById(R.id.ll_identity);
        ll_identity.setOnClickListener(this);
        ll_address =(LinearLayout)findViewById(R.id.ll_address);
        ll_address.setOnClickListener(this);
        ll_bank_num =(LinearLayout)findViewById(R.id.ll_bank_num);
        ll_bank_num.setOnClickListener(this);
        ll_weixin =(LinearLayout)findViewById(R.id.ll_weixin);
        ll_weixin.setOnClickListener(this);
        ll_zhifubao =(LinearLayout)findViewById(R.id.ll_zhifubao);
        ll_zhifubao.setOnClickListener(this);

        rl_experience =(RelativeLayout)findViewById(R.id.rl_experience);
        rl_experience.setOnClickListener(this);
        rl_portrait =(RelativeLayout)findViewById(R.id.rl_portrait);
        rl_portrait.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_name:
            case R.id.ll_identity:
            case R.id.ll_address:
            case R.id.ll_bank_num:
            case R.id.ll_zhifubao:
            case R.id.rl_experience:
            case R.id.rl_portrait:
                ToolUtils.getInstance().closeKeyBoard(editBankCode);
                ToolUtils.getInstance().closeKeyBoard(editWeiXin);
                ToolUtils.getInstance().closeKeyBoard(editPay);
                ToolUtils.getInstance().closeKeyBoard(editName);
                ToolUtils.getInstance().closeKeyBoard(editIdentityNumber);
                ToolUtils.getInstance().closeKeyBoard(editAddress);
                ToolUtils.getInstance().closeKeyBoard(editExperience);
                break;
        }
    }

    /**
     * 进行检测信息
     * String reg = "[\\u4e00-\\u9fa5]+";//表示+表示一个或多个中文，
     * @return
     */
    public void checkInfo() throws ContentException {
        name = editName.getText().toString().trim();
        if(name.equals(editName.getHint())){
            name="";
        }
        int size="中".getBytes().length;
        String reg="[\\u4e00-\\u9fa5]+";
        String tipName="姓名";
        String regexTip=tipName+"必须输入中文";
        int maxLength= HDCivilizationConstants.VOLUNTEER_NAME_MAX_LENGTH*size;
        String maxTip=tipName+"最多"+ HDCivilizationConstants.VOLUNTEER_NAME_MAX_LENGTH+"个汉字";
        int minLength= HDCivilizationConstants.VOLUNTEER_NAME_MIN_LENGTH*size;
        String minTip=tipName+"最少"+ HDCivilizationConstants.VOLUNTEER_NAME_MIN_LENGTH+"个汉字";
        regexStr(name,reg,tipName,regexTip,maxLength,maxTip,minLength,minTip);


        //身份证
        identityNumber = editIdentityNumber.getText().toString().trim();
        String curYear = "" + Calendar.getInstance().get(Calendar.YEAR);
        int y3 = Integer.valueOf(curYear.substring(2, 3));
        int y4 = Integer.valueOf(curYear.substring(3, 4));

        reg="^(1[1-5]|2[1-3]|3[1-7]|4[1-6]|5[0-4]|6[1-5]|71|8[1-2])\\d{4}(19\\d{2}|20([0-" + (y3 - 1) + "][0-9]|" + y3 + "[0-" + y4
                + "]))(((0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])))\\d{3}([0-9]|x|X)$";
        tipName="身份证";
        regexTip="身份证格式不正确";
        maxLength=-1;
        maxTip="";
        minLength=-1;
        minTip=tipName+"不能为空!";
        regexStr(identityNumber,reg,tipName,regexTip,maxLength,maxTip,minLength,minTip);

//        regexStr("152624196706203033",reg,tipName,regexTip,maxLength,maxTip,minLength,minTip);
        //地址
        address = editAddress.getText().toString().trim();
        if(address.equals(editAddress.getHint())){
            address="";
        }
        //"([\\u4e00-\\u9fa5]{0,})(\\w{0,})"
        reg=null;
        tipName="地址";
        regexTip="地址必须是汉字和字符组合";
        maxLength= HDCivilizationConstants.VOLUNTEER_ADDRESS_MAX_LENGTH*size;
        maxTip=tipName+"最多"+ HDCivilizationConstants.VOLUNTEER_ADDRESS_MAX_LENGTH+"个汉字";
        minLength= HDCivilizationConstants.VOLUNTEER_ADDRESS_MIN_LENGTH*size;
        minTip=tipName+"不能为空!";
        regexStr(address,reg,tipName,regexTip,maxLength,maxTip,minLength,minTip);

        //经验
        experience = editExperience.getText().toString().trim();
        if(experience.equals(editExperience.getHint())){
            experience="";
        }

        //"([\\u4e00-\\u9fa5]{0,})(\\w{0,})"
        reg=null;
        tipName="经验";
        regexTip="经验必须是汉字和字符组合";
        maxLength= HDCivilizationConstants.VOLUNTEER_EXPERIENCE_MAX_LENGTH*size;
        maxTip=tipName+"最多"+ HDCivilizationConstants.VOLUNTEER_EXPERIENCE_MAX_LENGTH+"个汉字";
        minLength= HDCivilizationConstants.VOLUNTEER_EXPERIENCE_MIN_LENGTH*size;
        minTip=tipName+"不能为空!";
//        regexStr(experience,reg,tipName,regexTip,maxLength,maxTip,minLength,minTip);

        if(image1Path.equals("") || image2Path.equals("")){
            throw new ContentException("证件照不能为空!");
        }
        //银行卡号 "^\\d{19}$"
        bankCode= editBankCode.getText().toString().trim();

        reg="^\\d{16,19}$";
        tipName="银行卡号";
        regexTip=tipName+"必须是16位至19位数字";
        maxLength=19;
        maxTip=tipName+"最多"+ HDCivilizationConstants.VOLUNTEER_BANK_MAX_LENGTH+"个数字";
        minLength=16;
        minTip=tipName+"最少"+ HDCivilizationConstants.VOLUNTEER_BANK_MIN_LENGTH+"个数字";
        regexStr(bankCode,reg,tipName,regexTip,maxLength,maxTip,minLength,minTip);
        boolean bankInfo = BankInfo.BeiJingBank(bankCode.substring(0, 6));
        if (bankInfo==false){
            throw new ContentException("银行卡必须为北京银行");
        }
        //^[a-zA-Z0-9_]+$/
        weiXin = editWeiXin.getText().toString().trim();
//        if(weiXin.equals(editWeiXin.getHint())){
//            weiXin="";
//        }
//        reg="^[a-zA-Z\\d_]{5,}$";
//        tipName="微信号";
//        regexTip="微信号必须5位以上数字,字母,下划线组合";
//        maxLength=-1;
//        maxTip=tipName+"最多"+HDCivilizationConstants.VOLUNTEER_NAME_MAX_LENGTH+"个字符";
//        minLength=5;
//        minTip=tipName+"最少"+HDCivilizationConstants.VOLUNTEER_WEIXIN_MIN_LENGTH+"个字符";
//        regexStr(weiXin,reg,tipName,regexTip,maxLength,maxTip,minLength,minTip);



//        weiXin = editWeiXin.getText().toString().trim();
//        if(weiXin.equals(editWeiXin.getHint())){
//            weiXin="";
//        }
//        reg="^[a-zA-Z\\d_]{5,}$";
//        tipName="微信号";
//        regexTip="微信号必须5位以上数字,字母,下划线组合";
//        maxLength=-1;
//        maxTip=tipName+"最多"+HDCivilizationConstants.VOLUNTEER_NAME_MAX_LENGTH+"个数字";
//        minLength=5;
//        minTip=tipName+"最少"+HDCivilizationConstants.VOLUNTEER_NAME_MAX_LENGTH+"个字符";
//        regexStr(weiXin,reg,tipName,regexTip,maxLength,maxTip,minLength,minTip);

        //"^(1[1-5]|2[1-3]|3[1-7]|4[1-6]|5[0-4]|6[1-5]|71|8[1-2])\\d{4}(19\\d{2}|20([0-" + (y3 - 1) + "][0-9]|" + y3 + "[0-" + y4+ "]))(((0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])))\\d{3}([0-9]|x|X)$"
        //("^[1][3,4,5,8][0-9]{9}$"); "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        pay = editPay.getText().toString().trim();
//        if(pay.equals(editPay.getHint())){
//            pay="";
//        }
//        reg="^([1][3,4,5,8,7][0-9]{9})|(([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,})$";
//        tipName="支付宝";
//        regexTip="支付宝必须手机号或者邮箱";
//        maxLength=-1;
//        maxTip=tipName+"最多"+HDCivilizationConstants.VOLUNTEER_NAME_MAX_LENGTH+"个数字";
//        minLength=-1;
//        minTip=tipName+"最少"+HDCivilizationConstants.VOLUNTEER_NAME_MAX_LENGTH+"个字符";
//        regexStr(pay,reg,tipName,regexTip,maxLength,maxTip,minLength,minTip);
//        return name.equals("") || identityNumber.equals("") || address.equals("") || experience.equals("") || bankCode.equals("") || weiXin.equals("") || pay.equals("") || image1Path.equals("")||image2Path.equals("");
    }

    /**
     * 先进行判断是否为空
     *      继而进行判断正则表达式
     *         继而进行判断最大长度
     * @param reg
     * @param name
     * @param maxLength <0不判断长度
     */
    private void regexStr(String content,String reg,String name,String regexTip,int maxLength,String maxLengthTip,int minLength,String minTip) throws ContentException {
        if(content.trim().equals("")) {
            throw new ContentException(name+"不能为空!");
        }else{
            if(reg!=null && !content.matches(reg)){
                //不满足正则:
                throw new ContentException(regexTip);
            }else{
                if(maxLength>=0){
                    if(content.trim().getBytes().length>maxLength){
                        throw new ContentException(maxLengthTip);
                    }
                }

                if(minLength>=0){
                     if(content.trim().getBytes().length<minLength){
                        throw new ContentException(minTip);
                    }
                }
            }
        }
    }
    @Override
    protected void initInitevnts() {
        /**提交数据**/
        subSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    user = UserDao.getInstance().getLocalUser();
                    checkInfo();
                        if (NetUtils.getInstance().checkNetwork(UiUtils.getInstance().getContext())) {
                            hud = KProgressHUD.create(VolunteerSignUpFormActivity.this)
                                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                    .setLabel("志愿者申请中").setCancellable(false);
                            hud.setCancellable(false);
                            hud.show();
                            System.out.println("---------="+imgPathList.size());
                            uploadImg2();
                        }else{
                            UiUtils.getInstance().showToast("请查看网络!");
                        }

                   } catch (ContentException e) {
                      e.printStackTrace();
                      UiUtils.getInstance().showToast(e.getMessage());
                   }
            }
        });

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        handImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolUtils.getInstance().closeKeyBoard(editBankCode);
                ToolUtils.getInstance().closeKeyBoard(editWeiXin);
                ToolUtils.getInstance().closeKeyBoard(editPay);
                ToolUtils.getInstance().closeKeyBoard(editName);
                ToolUtils.getInstance().closeKeyBoard(editIdentityNumber);
                ToolUtils.getInstance().closeKeyBoard(editAddress);
                ToolUtils.getInstance().closeKeyBoard(editExperience);
                currentImageIndex=0;
                showPicturePopupWindow();//PopupWindow形式

            }
        });


        handImgBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolUtils.getInstance().closeKeyBoard(editBankCode);
                ToolUtils.getInstance().closeKeyBoard(editWeiXin);
                ToolUtils.getInstance().closeKeyBoard(editPay);
                ToolUtils.getInstance().closeKeyBoard(editName);
                ToolUtils.getInstance().closeKeyBoard(editIdentityNumber);
                ToolUtils.getInstance().closeKeyBoard(editAddress);
                ToolUtils.getInstance().closeKeyBoard(editExperience);
                currentImageIndex = 1;
                showPicturePopupWindow();//PopupWindow形式
            }
        });

    }

    /**
     * 图片上传2
     */
    private void uploadImg2(){
        uploadFilePath.clear();
        RequestParams params = new RequestParams(); // 默认编码UTF-8
        params.addQueryStringParameter("tranCode","AROUND0021");
        params.addQueryStringParameter("userId", user.getUserId());
        System.out.println("VolunteerSignUpFormActivity uploadImg2:...image1Path:"+//
                                                                image1Path+"....image2Path:"+image2Path);
        if(!image1Path.equals("")){
            params.addBodyParameter("image1Path" , new File(image1Path));
        }
        if(!image2Path.trim().equals("")){
            params.addBodyParameter("image2Path" , new File(image2Path));
        }

        HttpUtils http = new HttpUtils();
        http.configSoTimeout(HDCivilizationConstants.NEWWORK_TIME_OUT);
        http.configTimeout(HDCivilizationConstants.NEWWORK_TIME_OUT);
        http.send(HttpRequest.HttpMethod.POST,
                UrlParamsEntity.UPLOAD_IMG,
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        System.out.println("conn...");
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        if (isUploading) {
                            System.out.println("upload: " + current + "/" + total);
                        } else {
                            System.out.println("reply: " + current + "/" + total);
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        System.out.println("reply: " + responseInfo.result);
                        try {
                            Message message=Message.obtain();
                            UploadImgProtocol uploadImgProtocol=new UploadImgProtocol();
                            uploadImgProtocol.setUserId(user.getUserId());
                            uploadImgProtocol.setActionKeyName("志愿者申请提交失败");
                            message.obj=uploadImgProtocol.parseJson(responseInfo.result);
                            message.what=messageImgWhat;
                            handler.sendMessage(message);
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                            Message message=Message.obtain();
                            message.what=messageImgError;
                            message.obj=e.getMessage();
                            handler.sendMessage(message);
                        } catch (ContentException e) {
                            e.printStackTrace();
                            //此时为普通用户被禁用 降为普通用户 // TODO: 2016/9/20
                            if (e.getErrorCode()==HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE) {
                                Message message=Message.obtain();
                                message.what=messageImgError;
                                message.obj=e.getErrorContent();
                                handler.sendMessage(message);
                            }else{
                                Message message=Message.obtain();
                                message.what=messageImgError;
                                message.obj=e.getErrorContent();
                                handler.sendMessage(message);
                            }
                        }
                    }


                    @Override
                    public void onFailure(HttpException error, String msg) {
                        FileUtils.getInstance().saveCrashInfo2File(error);
                        if(error.getMessage().contains("java.net.SocketTimeoutException")||
                                error.getMessage().contains("org.apache.http.conn.ConnectTimeoutException")){
                            Message messsage = Message.obtain();
                            messsage.what = messageImgError;//失败
                            messsage.obj = "志愿者申请提交失败,链接超时!";
                            handler.sendMessage(messsage);
                        }else{
                            Message messsage = Message.obtain();
                            messsage.what = messageImgError;//失败
                            messsage.obj = "志愿者申请提交失败!";
                            handler.sendMessage(messsage);
                        }
                    }
                });
    }

    /**
     * 拍照或从图库选择图片(PopupWindow形式)
     */
    public void showPicturePopupWindow(){
        menuWindow = new SelectPicPopupWindow(this, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 隐藏弹出窗口
                menuWindow.dismiss();
                switch (v.getId()) {
                    case R.id.takePhotoBtn:// 拍照
                        System.out.println("VolunteerSignUpActivity1");
                        takePhoto();
                        break;
                    case R.id.pickPhotoBtn:// 相册选择图片
                        mImagePath=null;
                        pickPhoto();
                        break;
                    case R.id.cancelBtn:// 取消
                        break;
                    default:
                        break;
                }
            }
        });
        menuWindow.showAtLocation(findViewById(R.id.volunteer_sign_up_), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        // 执行拍照前，应该先判断SD卡是否存在
        /**
         * 通过指定图片存储路径，解决部分机型onActivityResult回调 data返回为null的情况
         */
        requestPermission(HDCivilizationConstants.CAMERA_REQUEST_CODE, "android.permission.CAMERA", new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(VolunteerSignUpFormActivity.this, TakeCameraActivity.class);
                //最多只有2张
                intent.putExtra(TakeCameraActivity.MAX_TAKE_IMAGE_COUNT, 1);
                VolunteerSignUpFormActivity.this.startActivityForResult(intent, MySubSuperviseActivity.TAKE_IMAGE_REQUEST_CODE);
            }
        }, new Runnable() {
            @Override
            public void run() {
                UiUtils.getInstance().showToast("请检测相机权限!");
            }
        });

    }

    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, MySubSuperviseActivity.PCIK_IMAGE_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MySubSuperviseActivity.SELECT_IMAGE_RESULT_CODE && resultCode == RESULT_OK){//照相判断的业务逻辑
            String imagePath = "";
            if(data != null && data.getData() != null){//有数据返回直接使用返回的图片地址
                imagePath = ImageUtils.getFilePathByFileUri(this, data.getData());
                startPhotoZoom(data.getData());
            }else{//无数据使用指定的图片路径s
                startPhotoZoom(Uri.fromFile(new File(mImagePath)));
            }
        }else if(requestCode == MySubSuperviseActivity.ZOOM_REQUEST_CODE){//缩减判断的逻辑
            //进行判断数据返回值
            if(data!=null && resultCode == MySubSuperviseActivity.ZOOM_RESULT_CODE){
                //进行图片压缩至比特流中
                mPressImagePath=data.getExtras().getString(MySubSuperviseActivity.FILE_PATH);
                if(currentImageIndex==0){
                    image1Path=mPressImagePath;
                    imgPathList.add(image1Path);
                    BitmapUtil.getInstance().displayImg(handImg,mPressImagePath);
                }else{
                    image2Path=mPressImagePath;
                    imgPathList.add(image2Path);
                    BitmapUtil.getInstance().displayImg(handImgBg,mPressImagePath);
                }
                //先进行去除
//                mpressImgPathList.add(0, mPressImagePath);
            }else{
                System.out.println("requestCode == ZOOM_REQUEST_CODE else.....");
            }
        }else if(MySubSuperviseActivity.PCIK_IMAGE_RESULT_CODE==requestCode && resultCode == RESULT_OK ){
            if(data != null && data.getData() != null){//有数据返回直接使用返回的图片地址
                startPhotoZoom(data.getData());
            }
        }else if(requestCode == MySubSuperviseActivity.TAKE_IMAGE_REQUEST_CODE && resultCode == MySubSuperviseActivity.TAKE_IMAGE_RESULT_CODE){
            ArrayList<String> name=data.getExtras().getStringArrayList(MySubSuperviseActivity.IMAGE_LIST_KEY_NAME);
            Log.d(TAG, "TAKE_IMAGE_RESULT_CODE name is null:" + (name != null));
            if(name!=null && name.size()>0){
                Log.d(TAG,"TAKE_IMAGE_RESULT_CODE name size:"+(name.size()));
                // TODO: 2016/8/16
                if(currentImageIndex==0){
                    image1Path=name.get(0);
                    imgPathList.add(image1Path);
                    BitmapUtil.getInstance().displayImg(handImg,image1Path);
                }else{
                    image2Path=name.get(0);
                    imgPathList.add(image2Path);
                    BitmapUtil.getInstance().displayImg(handImgBg,image2Path);
                }
//                mpressImgPathList.remove(currentImageIndex);
            }
        }
    }


    /**
     * 进行调用裁剪系统的功能
     * @param uri
     */
    public void startPhotoZoom(Uri uri){
        Intent intent=new Intent(this,CropperActivity.class);
        intent.setData(uri);
        startActivityForResult(intent, MySubSuperviseActivity.ZOOM_REQUEST_CODE);
    }

}

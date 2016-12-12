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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.progressbar.KProgressHUD;
import com.zhjy.hdcivilization.protocol.UploadImgProtocol;
import com.zhjy.hdcivilization.protocol.UserInfoEditProtocol;
import com.zhjy.hdcivilization.utils.BitmapUtil;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.NetUtils;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.CircleImageView;
import com.zhjy.hdcivilization.view.SelectPicPopupWindow;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/8/2.
 *         我的界面个人信息模块
 */
public class PersonalInfoActivity extends BaseActivity implements View.OnClickListener {

    //CircleImageView
    private ImageView btnBack;
    private RelativeLayout rl_back;
    private ImageView userPic;
    private TextView userName, userNameType;
    private TextView personalNumber;
    private EditText personalSign;
    private Button btnSavePersonal;
    private String signName;
    private String firstSignName;//第一次进来的昵称
    private String firstImage1Path;//第一次进来的图片(头像)的路径
    private SelectPicPopupWindow menuWindow;
    public String mPressImagePath;//压缩之后的文件的路径
    //    public String mImagePath;
    private int currentImageIndex = 0;//当前图片的角标
    private String image1Path = "";
    private List<String> imgPathList = new ArrayList<String>();
    private User user;//用户对象
    private int submitType;//提交类型 0:全部提交,1:上传图片,2:编辑字段,-1是缺省值

    final int messageImgWhat = 204;//消息的what码
    final int messageImgError = 205;//消息的what码
    final int submitDataSuccess = 206;//上传文明监督提报成功请求码
    final int submitDataFailure = 207;//上传文明监督提报成功失败码

    private String imgId;//后台传递给我的图片的id
    private KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        customLayout = R.layout.activity_personal_info;
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initViews() {
        btnBack = (ImageView) findViewById(R.id.btn_back);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        userPic = (ImageView) findViewById(R.id.comment_user_pic);
        userName = (TextView) findViewById(R.id.comment_user_name);
        userNameType = (TextView) findViewById(R.id.comment_user_name_type);

        personalNumber = (TextView) findViewById(R.id.personal_info_number);
        personalSign = (EditText) findViewById(R.id.personal_info_sign);
        btnSavePersonal = (Button) findViewById(R.id.save_personal);

        rl_back.setOnClickListener(this);
        btnSavePersonal.setOnClickListener(this);
        userPic.setOnClickListener(this);
    }

    @Override
    protected void initInitevnts() {
        //初始化数据
        initUserInfo();
    }

    /**
     * handler
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //消息的what的分类
            switch (msg.what) {
                case messageImgWhat:
                    //图片上传成功
                    imgId=(String)msg.obj;
                    submitData();
                    break;

                case messageImgError:
                    UiUtils.getInstance().showToast(msg.obj + "");
                    if(hud!=null){
                        hud.dismiss();
                    }
                    break;

                case submitDataSuccess:
                    //发送内容成功
                    switch (submitType) {
                        case 0://全部修改
//                            //进行设置用户名:firstSignName
//                            userName.setText(signName);
//                            //进行初始化头像
//                            BitmapUtil.getInstance().displayImg(userPic, user.getPortraitUrl());
//                            System.out.println("PersonalInfoActivity  handleMessage getPortraitUrl:"+user.getPortraitUrl());
//                            //需要进行重新赋值
//                            firstImage1Path = image1Path;
//                            firstSignName = signName;
                            user.setNickName(signName);
                            UserDao.getInstance().saveUpDate(user);
                            initUserInfo();
                            UiUtils.getInstance().showToast("用户信息修改成功!");
                            break;
                        case 1://图片修改
                            //进行初始化头像
//                            BitmapUtil.getInstance().displayImg(userPic, user.getPortraitUrl());
//                            System.out.println("PersonalInfoActivity  handleMessage getPortraitUrl:" + user.getPortraitUrl());
//                            firstImage1Path = image1Path;
                            initUserInfo();
                            UiUtils.getInstance().showToast("用户信息修改成功!");
                            break;

                        case 2://编辑字段修改
//                            //进行设置用户名:firstSignName
//                            firstSignName = signName;
//                            userName.setText(signName);
                            user.setNickName(signName);
                            UserDao.getInstance().saveUpDate(user);
                            initUserInfo();
                            UiUtils.getInstance().showToast("用户信息修改成功!");
                            break;
                    }
                    if(hud!=null){
                        hud.dismiss();
                    }
                    //提交类型为-1
                    submitType=-1;
                    finish();
                    break;

                case submitDataFailure:
                    //发送内容失败
                    if(hud!=null){
                        hud.dismiss();
                    }
                    UiUtils.getInstance().showToast(msg.obj + "");
                    break;
            }
        }
    };



    private void initUserInfo() {
        //进行获取用户对象
        try {
            //进行首次初始化
            user = UserDao.getInstance().getLocalUser();
            System.out.println("initUserInfo userToString:"+user.toString());
            firstSignName = user.getNickName();
            firstImage1Path = user.getPortraitUrl();
            image1Path = firstImage1Path;

            //进行设置用户名:firstSignName
            if(firstSignName.trim().equals("")){
                userName.setText(user.getAccountNumber());
            }else{
                userName.setText(firstSignName);
            }

            if(firstSignName.equals(user.getAccountNumber())){
                //如果昵称与手机号相同.那么为空字符串
                firstSignName="";
                personalSign.setText("");
            }else{
                personalSign.setText(firstSignName);
            }
            System.out.println("user.getAccountNumber():"+user.getAccountNumber());
            //账号
            personalNumber.setText(user.getAccountNumber());
            //进行显示用户类型
            if(user.getIdentityState().equals(UserPermisson.VOLUNTEER.getType())){
                userNameType.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
            }else{
                userNameType.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
            }
            //进行初始化头像
            BitmapUtil.getInstance().displayUserPic(userPic, firstImage1Path);
        } catch (ContentException e) {
            e.printStackTrace();
            finish();
            //用户未登录
            UiUtils.getInstance().showToast("未登录!");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;

            case R.id.save_personal://保存昵称数据的更新
                saveDataforService();
                break;

            case R.id.comment_user_pic://更换头像
                showPicturePopupWindow();
                break;
        }
    }


    /****
     * 保存数据到服务器
     */
    private void saveDataforService() {
        try {
            if (checkEditData()) {
                if (NetUtils.getInstance().checkNetwork(PersonalInfoActivity.this)) {
                    //进行判断提交类型
                    hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).
                                                        setLabel("用户信息提交中").setCancellable(false);
                    hud.setCancellable(false);
                    hud.show();
                    switch (submitType) {
                        case 0://一起进行提交
                            uploadImg();
                            break;

                        case 1://只是上传图片
                            uploadImg();
                            break;

                        case 2://上传文本
                            submitData();
                            break;
                    }
                }else{
                    UiUtils.getInstance().showToast("请查看网络!");
                }
            }
        } catch (ConnectException e) {
            e.printStackTrace();
            UiUtils.getInstance().showToast("未修改");
            finish();
            System.out.println("e printStack:"+e.getMessage());
        }
    }

    /**
     * 进行上传文本或者图片的id以及都上传
     */
    private void submitData() {
        final UserInfoEditProtocol userInfoEditProtocol = new UserInfoEditProtocol();
        RequestParams params = new RequestParams();
        StringBuilder sb=new StringBuilder();
        sb.append(UrlParamsEntity.CURRENT_ID);
        params.addQueryStringParameter("tranCode", "AROUND0016");
        sb.append("tranCode=AROUND0016");
        params.addQueryStringParameter("userId", user.getUserId());
        sb.append("&userId=" + user.getUserId());
        switch (submitType) {
            case 0://全部字段
                if(signName.trim().equals("")){
                    params.addQueryStringParameter("nickName", user.getAccountNumber());
                }else{
                    params.addQueryStringParameter("nickName", signName);
                }
                sb.append("&nickName=" + signName);
                params.addQueryStringParameter("imageId", imgId);
                sb.append("&imageId=" + imgId);
                break;

            case 1://头像
                params.addQueryStringParameter("imageId", imgId);
                sb.append("&imageId=" + imgId);
                break;

            case 2://字段
                if(signName.trim().equals("")){
                    params.addQueryStringParameter("nickName", user.getAccountNumber());
                }else{
                    params.addQueryStringParameter("nickName", signName);
                }
                sb.append("&nickName=" + signName);
                break;
        }
        System.out.println("processSubmitData2 url:" + sb.toString());

        userInfoEditProtocol.setUserId(user.getUserId());
        userInfoEditProtocol.setActivityUser(user);
        userInfoEditProtocol.setSubmitType(submitType);
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                UrlParamsEntity.CURRENT_ID,
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        System.out.println("processSubmitData2  onStart......");
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        System.out.println("processSubmitData2  onLoading......" + current + "/" + total);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        System.out.println("processSubmitData2  onSuccess......" + //
                                "upload response:" + responseInfo.result);
                        try {
                            userInfoEditProtocol.setActionKeyName("用户信息修改失败!");
                            userInfoEditProtocol.parseJson(responseInfo.result);
                            Message messsage = Message.obtain();
                            messsage.what = submitDataSuccess;
                            messsage.obj="用户信息修改成功!";
                            handler.sendMessage(messsage);
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                            Message messsage = Message.obtain();
                            messsage.what = submitDataFailure;//失败
                            messsage.obj = e.getMessage();
                            handler.sendMessage(messsage);
                        } catch (ContentException e) {
                            if(e.getErrorCode()==HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE){
                                //此时只有用户被禁用：// TODO: 2016/9/20
                                e.printStackTrace();
                                Message messsage = Message.obtain();
                                messsage.what = submitDataFailure;//失败
                                messsage.obj = e.getErrorContent();
                                handler.sendMessage(messsage);
                            }else{
                                e.printStackTrace();
                                Message messsage = Message.obtain();
                                messsage.what = submitDataFailure;//失败
                                messsage.obj = e.getErrorContent();
                                handler.sendMessage(messsage);
                            }
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
                        System.out.println("processSubmitData2  onFailure......" + "error:" + //
                                error.getExceptionCode() + "..getMessage:" + error.getMessage() + "...msg:" + msg);
                        if(error.getMessage().contains("java.net.SocketTimeoutException")||
                                error.getMessage().contains("org.apache.http.conn.ConnectTimeoutException")){
                            Message messsage = Message.obtain();
                            messsage.what = submitDataFailure;//失败
                            messsage.obj = "用户信息修改失败,链接超时!";
                            handler.sendMessage(messsage);
                        }else{
                            Message messsage = Message.obtain();
                            messsage.what = submitDataFailure;//失败
                            messsage.obj = "用户信息修改失败!";
                            handler.sendMessage(messsage);
                        }
                    }
                });
    }

    /**
     * 进行上传图片
     */
    private void uploadImg() {

        RequestParams params = new RequestParams(); // 默认编码UTF-8
        params.addQueryStringParameter("tranCode","AROUND0021");
        params.addQueryStringParameter("userId", user.getUserId());
        params.addBodyParameter("file" , new File(image1Path));

        HttpUtils http = new HttpUtils();
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
                            uploadImgProtocol.setActionKeyName("用户头像上传失败!");
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
                            Message message=Message.obtain();
                            message.what=messageImgError;
                            message.obj=e.getErrorContent();
                            handler.sendMessage(message);
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        if(error.getMessage().contains("java.net.SocketTimeoutException")||
                                error.getMessage().contains("org.apache.http.conn.ConnectTimeoutException")){
                            Message messsage = Message.obtain();
                            messsage.what = messageImgError;//失败
                            messsage.obj = "用户信息修改失败,链接超时!";
                            handler.sendMessage(messsage);
                        }else{
                            Message messsage = Message.obtain();
                            messsage.what = messageImgError;//失败
                            messsage.obj = "用户信息修改失败!";
                            handler.sendMessage(messsage);
                        }
                    }
                });
    }


    /**
     * 进行检测提交的数据
     * 先进行检测数据是否变化-->
     * 继而检测数据的合理性--->
     * 进行检测哪个数据发生了变化,就提交哪个数据。
     *
     * @return
     */
    public boolean checkEditData() throws ConnectException {
        boolean flag = false;
        signName = personalSign.getText().toString().trim();
        if (signName.equals(firstSignName) && //
                image1Path.equals(firstImage1Path)) {
            //都相等:提示用户内容必须修改
            if (signName.trim().equals("")) {
//                UiUtils.getInstance().showToast("昵称输入不能为空!");
                flag = false;
                throw new ConnectException("初始化可以为空");
            } else if (image1Path.equals("")) {
//                UiUtils.getInstance().showToast("头像不能为空!");
                flag = false;
                throw new ConnectException("头像没有改变");
            }
            throw new ConnectException("用户信息未修改!");
        } else {
            //昵称或者图像进行修改了
            if (!signName.equals(firstSignName) &&
                    !image1Path.equals(firstImage1Path)) {
                //昵称和头像都修改了
                submitType = 0;
                flag = true;
            } else if (!signName.equals(firstSignName) &&
                    image1Path.equals(firstImage1Path)) {
                //修改了昵称
                int size= 0;
                try {
                    size = "张".getBytes("GBK").length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    if (signName.trim().getBytes("GBK").length>
                                size*HDCivilizationConstants.USER_NAME_MAX_LENGTH) {
                        //输入昵称长度不能超过六个汉字
                        flag = false;
                        UiUtils.getInstance().showToast("昵称输入不能超过"+HDCivilizationConstants.USER_NAME_MAX_LENGTH+"汉字!");
                    } else {
                        submitType = 2;
                        flag = true;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (!image1Path.equals(firstImage1Path) &&
                    signName.equals(firstSignName)) {
                //修改了头像
                if (image1Path.equals("")) {
                    //头像变为空
                    flag = false;
                } else {
                    submitType = 1;
                    flag = true;
                }
            }
        }
        return flag;
    }

    /**
     * 拍照或从图库选择图片(PopupWindow形式)
     */
    public void showPicturePopupWindow() {
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
//                        mImagePath=null;
                        pickPhoto();
                        break;
                    case R.id.cancelBtn:// 取消
                        break;
                    default:
                        break;
                }
            }
        });
        menuWindow.showAtLocation(findViewById(R.id.personal_info), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
                Intent intent = new Intent(PersonalInfoActivity.this, TakeCameraActivity.class);
                //最多只有2张
                intent.putExtra(TakeCameraActivity.MAX_TAKE_IMAGE_COUNT, 1);
                intent.putExtra(TakeCameraActivity.LOGO_PORTRAIT,true);
                PersonalInfoActivity.this.startActivityForResult(intent, MySubSuperviseActivity.TAKE_IMAGE_REQUEST_CODE);
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
        /*if(requestCode == MySubSuperviseActivity.SELECT_IMAGE_RESULT_CODE && resultCode == RESULT_OK){//照相判断的业务逻辑
            String imagePath = "";
            if(data != null && data.getData() != null){//有数据返回直接使用返回的图片地址
                imagePath = ImageUtils.getFilePathByFileUri(this, data.getData());
                startPhotoZoom(data.getData());
            }else{//无数据使用指定的图片路径
                startPhotoZoom(Uri.fromFile(new File(mImagePath)));
            }
        }else */
        if (requestCode == MySubSuperviseActivity.ZOOM_REQUEST_CODE) {//缩减判断的逻辑
            //进行判断数据返回值
            if (data != null && resultCode == MySubSuperviseActivity.ZOOM_RESULT_CODE) {
                //进行图片压缩至比特流中
                mPressImagePath = data.getExtras().getString(MySubSuperviseActivity.FILE_PATH);
                if (currentImageIndex == 0) {
                    image1Path = mPressImagePath;
                    Log.d(TAG, "TAKE_IMAGE_RESULT_CODE name...image1Path:"+image1Path);
                    imgPathList.add(image1Path);
                    BitmapUtil.getInstance().displayUserPic(userPic, mPressImagePath);
                }
            } else {
                System.out.println("requestCode == ZOOM_REQUEST_CODE else.....");
            }
        } else if (MySubSuperviseActivity.PCIK_IMAGE_RESULT_CODE == requestCode && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {//有数据返回直接使用返回的图片地址
                startPhotoZoom(data.getData());
            }
        } else if (requestCode == MySubSuperviseActivity.TAKE_IMAGE_REQUEST_CODE && resultCode == MySubSuperviseActivity.TAKE_IMAGE_RESULT_CODE) {
            ArrayList<String> name = data.getExtras().getStringArrayList(MySubSuperviseActivity.IMAGE_LIST_KEY_NAME);
            Log.d(TAG, "TAKE_IMAGE_RESULT_CODE name is null:" + (name != null));
            if (name != null && name.size() > 0) {
                // TODO: 2016/8/16
                if (currentImageIndex == 0) {
                    image1Path = name.get(0);
                    Log.d(TAG, "TAKE_IMAGE_RESULT_CODE name size:" + (name.size())+"...image1Path:"+image1Path);
                    imgPathList.add(image1Path);
                    BitmapUtil.getInstance().displayUserPic(userPic, image1Path);
                }
            }
        }
    }


    /**
     * 进行调用裁剪系统的功能
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent(this, CropperActivity.class);
        intent.putExtra(CropperActivity.PORTRAIT_IMAGE,true);
        intent.setData(uri);
        startActivityForResult(intent, MySubSuperviseActivity.ZOOM_REQUEST_CODE);
    }

}

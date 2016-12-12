package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
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
import com.zhjy.hdcivilization.adapter.UploadImageAdapter;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.progressbar.KProgressHUD;
import com.zhjy.hdcivilization.protocol.SubmitThemeProtocol;
import com.zhjy.hdcivilization.protocol.SuperviseSubmitDataProtocol;
import com.zhjy.hdcivilization.protocol.UploadImgProtocol;
import com.zhjy.hdcivilization.utils.FileUtils;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.ImageUtils;
import com.zhjy.hdcivilization.utils.NetUtils;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.ToolUtils;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.SelectPicPopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/8/1.
 * 文明评论--话题发布
 */
public class GambitSubActivity extends BaseActivity implements View.OnClickListener {

    EditText edit_title, edit_content;
    GridView gridView;
    ImageButton btn_Ganmbit_Sub;
    ImageView btnBack;
    private RelativeLayout rl_back;
    private ArrayList<String> mpressImgPathList = new ArrayList<String>();
    private UploadImageAdapter adapter;
    private SelectPicPopupWindow menuWindow;
    public String mImagePath;
    public String mPressImagePath;//压缩之后的文件的路径
    public final static String IMAGE_UNSPECIFIED="image/*";//格式
    private KProgressHUD hud;
    //上传位置获取到的信息
    List<String> uploadFilePath=new ArrayList<String>();
    int uploadImgCount;//上传总的照片数
    private String title;
    private User user;
    final int messageImgWhat=204;//消息的what码
    final int messageImgError=205;//消息的what码
    final int submitDataSuccess=206;//上传文明监督提报成功请求码
    final int submitDataFailure=207;//上传文明监督提报成功失败码

    LinearLayout ll_img_des,ll_title,ll_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contentView= UiUtils.getInstance().inflate(R.layout.activity_gambit_sub);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        btnBack = (ImageView) findViewById(R.id.btn_back);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        edit_title = (EditText) findViewById(R.id.gambit_sub_edittext);
        gridView = (GridView) findViewById(R.id.gambit_sub_upload);
        edit_content = (EditText) findViewById(R.id.gambit_sub_content);
        btn_Ganmbit_Sub = (ImageButton) findViewById(R.id.btn_gambit_sub);

        // LinearLayout ll_img_des,ll_title,ll_content;
        ll_img_des = (LinearLayout) findViewById(R.id.ll_img_des);
        ll_img_des.setOnClickListener(this);
        ll_title = (LinearLayout) findViewById(R.id.ll_title);
        ll_title.setOnClickListener(this);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        ll_content.setOnClickListener(this);

        //进行初始化gridView的中的数据
        mpressImgPathList.add(null);
        adapter = new UploadImageAdapter(UiUtils.getInstance().getContext(),mpressImgPathList);
        adapter.setGridView(gridView);
        adapter.setImageNumber(HDCivilizationConstants.COMMENT_SUP_MAX_IMG);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(mItemClick);
        gridView.setOnItemLongClickListener(mItemLongClick);

        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ToolUtils.getInstance().closeKeyBoard(edit_content);
                ToolUtils.getInstance().closeKeyBoard(edit_title);
                return false;
            }
        });


        try {
            user = UserDao.getInstance().getLocalUser();
        } catch (ContentException e) {
            e.printStackTrace();
            UiUtils.getInstance().showToast(e.getMessage());
        }
    }

    @Override
    protected void initInitevnts() {
        btn_Ganmbit_Sub.setOnClickListener(this);
        rl_back.setOnClickListener(this);

    }

    /**
     * 上传图片GridView Item单击监听
     */
    private AdapterView.OnItemClickListener mItemClick = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            if(parent.getItemAtPosition(position) == null){ // 添加图片
                //showPictureDailog();//Dialog形式
                showPicturePopupWindow();//PopupWindow形式
            }else{
            }
        }
    };


    /**
     * 上传图片GridView Item长按监听
     */
    private AdapterView.OnItemLongClickListener mItemLongClick = new AdapterView.OnItemLongClickListener(){

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
            System.out.println("parent.getItemAtPosition(position) :" + parent.getItemAtPosition(position) + "..position:" + position);
            if(parent.getItemAtPosition(position) != null){ // 长按删除
                mpressImgPathList.remove(parent.getItemAtPosition(position));
                adapter.updateRemove(mpressImgPathList); // 刷新图片
            }
            return true;
        }
    };


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
                //先进行去除
                mpressImgPathList.remove(null);
                mpressImgPathList.add(0, mPressImagePath);
                adapter.update(mpressImgPathList); // 刷新图片
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
            if(name!=null && name.size()>=0){
                Log.d(TAG,"TAKE_IMAGE_RESULT_CODE name size:"+(name.size()));
                adapter.update(name);
                mpressImgPathList=adapter.getImagePathList();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.btn_gambit_sub:
                /****我的话题发布*****/
                subGambit();
                break;

            //ll_img_des,ll_title,ll_content;
            case R.id.ll_img_des:
            case R.id.ll_title:
            case R.id.ll_content:
                ToolUtils.getInstance().closeKeyBoard(edit_content);
                ToolUtils.getInstance().closeKeyBoard(edit_title);
                break;

        }
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            synchronized (MySubSuperviseActivity.class){
                switch(msg.what){
                    case messageImgWhat:
                        //进行判断
                        uploadFilePath.add((String)msg.obj);
                        processSubmitData2();
                        break;
                    case messageImgError:
                        UiUtils.getInstance().showToast(msg.obj+"");
                        if(hud!=null){
                            hud.dismiss();
                        }
                        break;

                    case submitDataSuccess:
                        //发布成功
                        UiUtils.getInstance().showToast("话题发布成功!");
                        if(hud!=null){
                            hud.dismiss();
                        }
                        finish();
                        break;

                    case submitDataFailure:
                        //提报失败
                        UiUtils.getInstance().showToast(msg.obj+"");
                        if(hud!=null){
                            hud.dismiss();
                        }
                        break;
                }
            }
        }
    };

    /**
     * 进行第二种方式进行提交数据
     */
    private void processSubmitData2() {
        RequestParams params = new RequestParams();
        StringBuilder sb=new StringBuilder();
        sb.append(UrlParamsEntity.CURRENT_ID);
        params.addQueryStringParameter("tranCode", "AROUND0012");
        sb.append("tranCode=AROUND0012");
        params.addQueryStringParameter("userId", user.getUserId());
        sb.append("&userId=" + user.getUserId());
        params.addQueryStringParameter("title", edit_title.getText().toString());
        sb.append("&title=" + edit_title.getText().toString());
        params.addQueryStringParameter("imgIds", getUploadImgStr(uploadFilePath));
        sb.append("&imgIds=" + getUploadImgStr(uploadFilePath));
        params.addQueryStringParameter("content", edit_content.getText().toString().trim());
        sb.append("&content=" + edit_content.getText().toString().trim());

        System.out.println("processSubmitData2 url:" + sb.toString());
        final SubmitThemeProtocol superviseSubmitDataProtocol = new SubmitThemeProtocol();
        superviseSubmitDataProtocol.setUserId(user.getUserId());
        HttpUtils http = new HttpUtils();
        http.configSoTimeout(HDCivilizationConstants.NEWWORK_TIME_OUT);
        http.configTimeout(HDCivilizationConstants.NEWWORK_TIME_OUT);
        http.send(HttpRequest.HttpMethod.POST,
                UrlParamsEntity.CURRENT_ID_1,
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
                            superviseSubmitDataProtocol.setActionKeyName("话题发布失败!");
                            superviseSubmitDataProtocol.parseJson(responseInfo.result);
                            Message messsage = Message.obtain();
                            messsage.what = submitDataSuccess;
                            handler.sendMessage(messsage);
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                            Message messsage = Message.obtain();
                            messsage.what = submitDataFailure;//失败
                            messsage.obj = e.getMessage();
                            handler.sendMessage(messsage);
                        } catch (ContentException e) {
                            e.printStackTrace();
                            if(e.getErrorCode()==HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE){
                                //处理用户权限过低: 普通用户被禁用 // TODO: 2016/9/20
                                Message messsage = Message.obtain();
                                messsage.what = submitDataFailure;//失败
                                messsage.obj = e.getErrorContent();
                                handler.sendMessage(messsage);
                            }else{
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
                            messsage.obj = "话题发布失败,链接超时!";
                            handler.sendMessage(messsage);
                        }else{
                            Message messsage = Message.obtain();
                            messsage.what = submitDataFailure;//失败
                            messsage.obj = "话题发布失败!";
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

    //话题发布
    private void subGambit() {
        ToolUtils.getInstance().closeKeyBoard(edit_title);
        try {
            checkSubmitData();
            //判断图片
            if (!(getUploadImgCount(mpressImgPathList)>0)){
                processSubmitData2();
            }else{
                System.out.println("mpressImgPathList2 ="+(mpressImgPathList==null));
                if (NetUtils.getInstance().checkNetwork(UiUtils.getInstance().getContext())) { //判断网络是否可用
                    hud = KProgressHUD.create(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("话题发布中").setCancellable(false);
                    hud.setCancellable(false);
                    hud.show();

                    uploadFilePath.clear();//进行清除集合上传文件的内容
                    uploadImgCount = getUploadImgCount(mpressImgPathList);
                    RequestParams params = new RequestParams(); // 默认编码UTF-8
                    params.addQueryStringParameter("tranCode","AROUND0021");
                    params.addQueryStringParameter("userId", user.getUserId());
                    for(int i=0;i<mpressImgPathList.size();++i) {
                        if (mpressImgPathList.get(i)!=null) {
                            params.addBodyParameter("file" + i, new File(mpressImgPathList.get(i)));
                        }
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
                                        uploadImgProtocol.setActionKeyName("话题发布失败");
                                        uploadImgProtocol.setUserId(user.getUserId());
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
                                        if(e.getErrorCode()==HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE){
                                            //此时的处理:只有普通用户被禁用! // TODO: 2016/9/20
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
                                    //java.net.SocketTimeoutException
                                    System.out.println("uploadImgProtocol  onFailure......" + "error:" + //
                                            error.getExceptionCode() + "..getMessage:" + error.getMessage() + "...msg:" + msg);
                                    FileUtils.getInstance().saveCrashInfo2File(error);
                                    if(error.getMessage().contains("java.net.SocketTimeoutException")||
                                            error.getMessage().contains("org.apache.http.conn.ConnectTimeoutException")){
                                        Message messsage = Message.obtain();
                                        messsage.what = messageImgError;//失败
                                        messsage.obj = "话题发布失败,链接超时!";
                                        handler.sendMessage(messsage);
                                    }else{
                                        Message messsage = Message.obtain();
                                        messsage.what = messageImgError;//失败
                                        messsage.obj = "话题发布失败!";
                                        handler.sendMessage(messsage);
                                    }
                                }
                            });
                }else{
                    UiUtils.getInstance().showToast("请查看网络！");
                }
            }

        } catch (ContentException e) {
            e.printStackTrace();
            UiUtils.getInstance().showToast(e.getMessage());
        }
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
        menuWindow.showAtLocation(findViewById(R.id.sub_gambit), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //检查内容是否正确
    private void checkSubmitData() throws ContentException {

        String name = edit_title.getText().toString().trim();
        if(name.equals(edit_title.getHint())){
            name="";
        }

        int size="中".getBytes().length;
        String reg=null;
        String tipName="标题";
        String regexTip="标题必须中文和字符组合";
        int maxLength= HDCivilizationConstants.SUBMIT_THEME_TITLE_MAX_LENGTH*size;
        String maxTip=tipName+"最多"+ HDCivilizationConstants.SUBMIT_THEME_TITLE_MAX_LENGTH+"个汉字";
        int minLength= HDCivilizationConstants.SUBMIT_THEME_TITLE_MIN_LENGTH*size;
        String minTip=tipName+"最少"+ HDCivilizationConstants.SUBMIT_THEME_TITLE_MIN_LENGTH+"个汉字";
        regexStr(name,reg,tipName,regexTip,maxLength,maxTip,minLength,minTip);



        //事件描述:
        String editContent= edit_content.getText().toString().trim();
        if(editContent.equals(edit_content.getHint())){
            editContent="";
        }
        //"([\\u4e00-\\u9fa5]{0,})(\\w{0,})"
         reg=null;
         tipName="内容描述";

         System.out.println("事件描述 字节数:"+"事件描述1bc".getBytes().length);
         regexTip="内容描述必须中文和字符组合";
         maxLength= HDCivilizationConstants.SUBMIT_THEME_DES_MAX_LENGTH*size;
         maxTip=tipName+"最多"+ HDCivilizationConstants.SUBMIT_THEME_DES_MAX_LENGTH+"个汉字";
         minLength= HDCivilizationConstants.SUBMIT_THEME_DES_MIN_LENGTH*size;
         minTip=tipName+"最少"+ HDCivilizationConstants.SUBMIT_THEME_DES_MIN_LENGTH+"个汉字";
        regexStr(editContent,reg,tipName,regexTip,maxLength,maxTip,minLength,minTip);

        //进行判断都为空的时候:
        if(name.equals("") && editContent.equals("")){
            throw new ContentException("标题,内容描述至少填一项!");
        }
    }

    /**
     * 验证正则
     * @param content
     * @param reg
     * @param name
     * @param regexTip
     * @param maxLength
     * @param maxLengthTip
     * @param minLength
     * @param minTip
     * @throws ContentException
     */
    private void regexStr(String content,String reg,String name,String regexTip,int maxLength,String maxLengthTip,int minLength,String minTip) throws ContentException {
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
    /**
     * 拍照获取图片
     */
    private void takePhoto() {
        // 执行拍照前，应该先判断SD卡是否存在
        // 执行拍照前，应该先判断SD卡是否存在
        /**
         * 通过指定图片存储路径，解决部分机型onActivityResult回调 data返回为null的情况
         */
        requestPermission(HDCivilizationConstants.CAMERA_REQUEST_CODE, "android.permission.CAMERA", new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(GambitSubActivity.this,TakeCameraActivity.class);
                intent.putStringArrayListExtra(MySubSuperviseActivity.IMAGE_LIST_KEY_NAME, adapter.getImagePathList());
                intent.putExtra(TakeCameraActivity.MAX_TAKE_IMAGE_COUNT, HDCivilizationConstants.COMMENT_SUP_MAX_IMG);
                GambitSubActivity.this.startActivityForResult(intent, MySubSuperviseActivity.TAKE_IMAGE_REQUEST_CODE);
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

    /**
     * 进行调用裁剪系统的功能
     * @param uri
     */
    public void startPhotoZoom(Uri uri){
        Intent intent=new Intent(this,CropperActivity.class);
        intent.setData(uri);
        startActivityForResult(intent, MySubSuperviseActivity.ZOOM_REQUEST_CODE);
    }

    /**
     * 进行获取上传照片的个数
     * @param mpressImgPathList
     * @return
     */
    private int getUploadImgCount(ArrayList<String> mpressImgPathList) {
        int count=0;
        for(String data:mpressImgPathList){
            if(data!=null){
                ++count;
            }
        }
        return count;
    }
}

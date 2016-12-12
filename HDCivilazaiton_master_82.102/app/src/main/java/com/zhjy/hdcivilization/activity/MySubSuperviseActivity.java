package com.zhjy.hdcivilization.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.adapter.UploadImageAdapter;
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
import com.zhjy.hdcivilization.protocol.GetVolunteerInfoProtocol;
import com.zhjy.hdcivilization.protocol.SuperviseSubmitDataProtocol;
import com.zhjy.hdcivilization.protocol.UploadImgProtocol;
import com.zhjy.hdcivilization.service.LocationService;
import com.zhjy.hdcivilization.utils.FileUtils;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.ImageUtils;
import com.zhjy.hdcivilization.utils.NetUtils;
import com.zhjy.hdcivilization.utils.SharedPreferencesManager;
import com.zhjy.hdcivilization.utils.ToolUtils;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.SelectPicPopupWindow;
import com.zhjy.hdcivilization.view.SupervisePositionPopup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文明监督的---我的上报
 */
public class MySubSuperviseActivity extends BaseActivity implements View.OnClickListener {

    public static final int PCIK_IMAGE_RESULT_CODE = 210;
    //点击选择...
    LinearLayout supervise_ll_next,supervise_ll_position,ll_event_type,ll_iv_position,ll_upload_img,ll_event_des;
    RelativeLayout rl_position,rl_back;
    ImageView supervise_iv_sub,iv_position,iv_event_type;
    GridView supervise_gv_upload;
    TextView tv_event_type_choose;
    EditText supervise_et_event_des,supervise_tv_position;
    ImageView btn_back;
    private ArrayList<String> mpressImgPathList = new ArrayList<String>();
    private UploadImageAdapter adapter;
    public final static int SELECT_IMAGE_RESULT_CODE = 200;//启动拍照的请求码
    public final static int TAKE_IMAGE_REQUEST_CODE = 206;//启动拍照的请求码
    public final static int TAKE_IMAGE_RESULT_CODE = 207;//启动拍照的请求码
    public final static int ZOOM_REQUEST_CODE = 201;//启动裁剪的请求码
    public final static int ZOOM_RESULT_CODE = 209;//启动裁剪的请求码
    public final static int EVENT_TYPE_REQUEST_CODE=202;//进入事件类型的请求码..
    public final static int EVENT_TYPE_RESULT_CODE=203;//进入事件类型的结束码..

    final int messageImgWhat=204;//消息的what码
    final int messageImgError=205;//消息的what码
    final int submitDataSuccess=206;//上传文明监督提报成功请求码
    final int submitDataFailure=207;//上传文明监督提报成功失败码
    final int submitDataNoMatch=203;//上传文明监督志愿者信息不匹配
    final int getVolunteerInfoSuccess=208;//进行获取志愿者信息成功码
    final int getVolunteerInfoFailure=209;//进行获取志愿者信息失败码

    private boolean isSubmitDataNoMatch;
    public final static String IMAGE_UNSPECIFIED="image/*";//格式
    public static final String IMAGE_LIST_KEY_NAME="imageList";
    public static final String FILE_PATH="FILE_PATH";
    private boolean isSupervise = true;
    /**
     * 自定义的PopupWindow
     */
    private SelectPicPopupWindow menuWindow;
    /**
     * 当前选择的图片的路径
     */
    public String mImagePath;
    public String mPressImagePath;//压缩之后的文件的路径

    //定位服务相关内容
    MyServiceConnecetion myServiceConnecetion;
    LocationService.LocationInterfaceClass locationInterfaceClass;
    Intent serviceIntent;

    //上传位置获取到的信息
    List<String> uploadFilePath=new ArrayList<String>();
    int uploadImgCount;//上传总的照片数
    private KProgressHUD hud;

    private String eventTypeIndex;
    int edPositionHeight,edPositionWidth,initEdPositionWidth;//地址的宽度和高度

    //用户
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        customLayout= R.layout.activity_my_sub_supervise;
        contentView= UiUtils.getInstance().inflate(R.layout.activity_my_sub_supervise);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        /**
         * 进行初始化view
         */
        supervise_ll_next=(LinearLayout)findViewById(R.id.supervise_ll_next);
        rl_back=(RelativeLayout)findViewById(R.id.rl_back);
        ll_event_des=(LinearLayout)findViewById(R.id.ll_event_des);
        ll_event_type=(LinearLayout)findViewById(R.id.ll_event_type);
        ll_iv_position=(LinearLayout)findViewById(R.id.ll_iv_position);
        ll_upload_img=(LinearLayout)findViewById(R.id.ll_upload_img);
        supervise_iv_sub= (ImageView) findViewById(R.id.supervise_iv_sub);
        iv_event_type= (ImageView) findViewById(R.id.iv_event_type);
        iv_position= (ImageView) findViewById(R.id.iv_position);
        supervise_gv_upload= (GridView) findViewById(R.id.supervise_gv_upload);
        supervise_et_event_des= (EditText) findViewById(R.id.supervise_et_event_des);

        //进行初始化gridView的中的数据
        mpressImgPathList.add(null);
        adapter=new UploadImageAdapter(UiUtils.getInstance().getContext(), mpressImgPathList);
        supervise_gv_upload.setAdapter(adapter);
        supervise_gv_upload.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ToolUtils.getInstance().closeKeyBoard(supervise_et_event_des);
                ToolUtils.getInstance().closeKeyBoard(supervise_tv_position);
                return false;
            }
        });
        supervise_gv_upload.setOnItemClickListener(mItemClick);
        supervise_gv_upload.setOnItemLongClickListener(mItemLongClick);

        tv_event_type_choose=(TextView)findViewById(R.id.tv_event_type_choose);
        supervise_tv_position=(EditText)findViewById(R.id.supervise_tv_position);
        rl_position=(RelativeLayout)findViewById(R.id.rl_position);
        btn_back=(ImageView)findViewById(R.id.btn_back);

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                edPositionHeight=supervise_tv_position.getMeasuredHeight();
                edPositionWidth=supervise_tv_position.getMeasuredWidth();
                initEdPositionWidth=edPositionWidth;
                edPositionWidth=edPositionWidth-edPositionWidth%getShortWidth("中");
                SharedPreferencesManager.put(MySubSuperviseActivity.this,"edPositionHeight",edPositionHeight);
                SharedPreferencesManager.put(MySubSuperviseActivity.this,"edPositionWidth",edPositionWidth);
                SharedPreferencesManager.put(MySubSuperviseActivity.this,"initEdPositionWidth",initEdPositionWidth);
            }
        });
        //进行绑定服务
        serviceIntent=new Intent(this,LocationService.class);
        startService(serviceIntent);
        myServiceConnecetion=new MyServiceConnecetion();
        bindService(serviceIntent, myServiceConnecetion, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("MySubSuperviseActivity onRestart .....");
    }

    int size;//每个汉字的字节数
    @Override
    protected void initInitevnts() {
        ll_event_type.setOnClickListener(this);
        ll_iv_position.setOnClickListener(this);
        supervise_iv_sub.setOnClickListener(this);
        rl_back.setOnClickListener(this);
        iv_event_type.setOnClickListener(this);
        rl_position.setOnClickListener(this);
        ll_upload_img.setOnClickListener(this);
        ll_event_des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolUtils.getInstance().closeKeyBoard(supervise_et_event_des);
                ToolUtils.getInstance().closeKeyBoard(supervise_tv_position);
            }
        });

        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ToolUtils.getInstance().closeKeyBoard(supervise_et_event_des);
                ToolUtils.getInstance().closeKeyBoard(supervise_tv_position);
                return false;
            }
        });

        size="中".getBytes().length;
        supervise_tv_position.addTextChangedListener(new TextWatcher() {
            String startStr;
            String lastStr="";
            int currentLine=0;
            int lastLine=currentLine;
            boolean isBackOrientation;//进行控制正向输入,还是反向输入
            boolean isTextType;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                startStr=s.toString();
                if(after==0){
                    isBackOrientation=true;
                }else{
                    isBackOrientation=false;
                }
                System.out.println("supervise_tv_position beforeTextChanged s:"+s+"..start:"+start+"..count:"+count+"..after:"+after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("supervise_tv_position onTextChanged s:"+s+"...start:"+start+"..before:"+before+"..count:"+count);
                if(!s.toString().contains(startStr) || startStr.trim().equals("")){
                    //如果一开始就大字符的输入
//                    System.out.println("supervise_tv_position 1 onTextChanged s.getByte length:"+s.toString().getBytes().length+"..size:"+size);
                    if(s.toString().getBytes().length>(HDCivilizationConstants.MAX_CIVI_SUPERVISE_POSITION_LENGTH*size)){
                        StringBuilder sb=new StringBuilder(s.toString());
//                        System.out.println("supervise_tv_position 11111111111 sb:"+sb.toString()+"..size:"+size);
                        for(int j=0;j<=sb.length();++j){
//                            System.out.println("supervise_tv_position 222222222 sb.substring(0,j):"+j+"..string:"+sb.substring(0,j));
                            if(sb.substring(0,j).getBytes().length>//
                                            (HDCivilizationConstants.MAX_CIVI_SUPERVISE_POSITION_LENGTH*size)){
                                lastStr=sb.substring(0,j-1);
                                break;
                            }else if(j==(sb.length())){
                                //如果为最后index
                                lastStr=sb.toString();
                            }
                        }
                        UiUtils.getInstance().showToast("详细地址最多"+ HDCivilizationConstants.MAX_CIVI_SUPERVISE_POSITION_LENGTH+"个汉字!");
                    }else{
                        lastStr="";
                    }
                }else{
                    //在期间输入的字符加上以前的字符串:超过了总的长度
//                    System.out.println("supervise_tv_position 2 onTextChanged s.getByte length:"+s.toString().getBytes().length+"..size:"+size);
                    if(s.toString().getBytes().length>(HDCivilizationConstants.MAX_CIVI_SUPERVISE_POSITION_LENGTH*size)){
                        StringBuilder sb=new StringBuilder(s.toString().substring(start,s.toString().length()));
//                        System.out.println("supervise_tv_position 333333333333 sb:"+sb.toString()+"..size:"+size);
                        for(int j=0;j<=sb.length();++j){
//                            System.out.println("supervise_tv_position 44444444444444 sb.substring(0,j):"+j+"..string:"+sb.substring(0,j));
                            if(s.toString().substring(0,j+start).getBytes().length>//
                                    (HDCivilizationConstants.MAX_CIVI_SUPERVISE_POSITION_LENGTH*size)){
                                lastStr=s.toString().substring(0, j + start-1);
                                break;
                            }else if(j==(sb.length())){
                                //如果为最后index
                                lastStr=s.toString();
                            }
                        }
                        UiUtils.getInstance().showToast("详细地址最多"+ HDCivilizationConstants.MAX_CIVI_SUPERVISE_POSITION_LENGTH+"个汉字!");
                    }else{
                        lastStr="";
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edPositionHeight==0){
                    edPositionHeight=(Integer)SharedPreferencesManager.get(MySubSuperviseActivity.this, "edPositionHeight", edPositionHeight);
                }
                if(edPositionWidth==0){
                    edPositionWidth=(Integer)SharedPreferencesManager.get(MySubSuperviseActivity.this,"edPositionWidth",edPositionWidth);
                }

                if(initEdPositionWidth==0){
                    initEdPositionWidth=(Integer)SharedPreferencesManager.get(MySubSuperviseActivity.this,"initEdPositionWidth",initEdPositionWidth);
                }
                if(!lastStr.equals("")){
                    supervise_tv_position.setText(lastStr);
                }
                supervise_tv_position.setSelection(supervise_tv_position.getText().toString().length());
                int shortWidth=getShortWidth(supervise_tv_position.getText().toString());
                if(!isBackOrientation){
                    //是否是纯汉字 "[\\u4e00-\\u9fa5]+"
                    if(supervise_tv_position.getText().toString().matches("[\\u4e00-\\u9fa5]+")){
                        if(!isTextType){
                            currentLine=shortWidth/initEdPositionWidth;
                        }else{
                            currentLine=shortWidth/edPositionWidth;
                        }
                        isTextType=true;
                        if(shortWidth%edPositionWidth>0){
                            rl_position.getLayoutParams().height=rl_position.getMeasuredHeight() + (currentLine-lastLine) * edPositionHeight;
                            rl_position.requestLayout();
                            lastLine=currentLine;
                        }
                    }else{
                        isTextType=false;
                        //不是纯汉字:包含数字,字母等.....
                        currentLine=shortWidth/initEdPositionWidth;
                        if(shortWidth%initEdPositionWidth>0){
                            rl_position.getLayoutParams().height=rl_position.getMeasuredHeight() + (currentLine-lastLine) * edPositionHeight;
                            rl_position.requestLayout();
                            lastLine=currentLine;
                        }
                    }

                }else{
                    //为相反方向
                    if(supervise_tv_position.getText().toString().matches("[\\u4e00-\\u9fa5]+")){
                        if(!isTextType){
                            currentLine=shortWidth/initEdPositionWidth;
                        }else{
                            currentLine=shortWidth/edPositionWidth;
                        }
                        isTextType=true;
                        if(shortWidth%edPositionWidth==0){
                            if(currentLine>0){
                                rl_position.getLayoutParams().height=rl_position.getMeasuredHeight() - edPositionHeight;
                                rl_position.requestLayout();
                                lastLine=currentLine-1;
                            }
                        }
                    }else{
                        isTextType=false;
                        //不是纯汉字:包含数字,字母等.....
                        System.out.println("MySubSuperviseActivity initEdPositionWidth:"+initEdPositionWidth);
                        currentLine=shortWidth/initEdPositionWidth;
                        if(shortWidth%initEdPositionWidth>0){
                            if(currentLine-lastLine<0){
                                rl_position.getLayoutParams().height=rl_position.getMeasuredHeight() + (currentLine-lastLine) * edPositionHeight;
                                rl_position.requestLayout();
                            }
                            lastLine=currentLine;
                        }
                    }
                }
            }
        });
    }

    /**
     * 进行获取短的textView的高度
     * @return
     */
    public int getShortWidth(String content) {
        TextView longTextView=new TextView(UiUtils.getInstance().getContext());
        longTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        longTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        longTextView.setSingleLine(true);
        longTextView.setText(content);
        int widthMeasureSpec=View.MeasureSpec.makeMeasureSpec(20000, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec= View.MeasureSpec.makeMeasureSpec(20000, View.MeasureSpec.AT_MOST);
        longTextView.measure(widthMeasureSpec, heightMeasureSpec);
        return longTextView.getMeasuredWidth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermission(HDCivilizationConstants.LOCATION_REQUEST_CODE, "android.permission.ACCESS_COARSE_LOCATION", //
                new Runnable() {
                    @Override
                    public void run() {

                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        UiUtils.getInstance().showToast("请检测定位权限!");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(myServiceConnecetion);
        stopService(serviceIntent);
    }

    /**
     * 上传图片GridView Item单击监听
     */
    private AdapterView.OnItemClickListener mItemClick = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if(parent.getItemAtPosition(position) == null){ // 添加图片
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
        if(requestCode == SELECT_IMAGE_RESULT_CODE && resultCode == RESULT_OK){//照相判断的业务逻辑
            String imagePath = "";
            if(data != null && data.getData() != null){//有数据返回直接使用返回的图片地址
                imagePath = ImageUtils.getFilePathByFileUri(this, data.getData());
                startPhotoZoom(data.getData());
            }else{//无数据使用指定的图片路径s
                startPhotoZoom(Uri.fromFile(new File(mImagePath)));
            }
        }else if(requestCode == ZOOM_REQUEST_CODE){//缩减判断的逻辑
            //进行判断数据返回值
            if(data!=null && resultCode == ZOOM_RESULT_CODE){
                //进行图片压缩至比特流中
                mPressImagePath=data.getExtras().getString(FILE_PATH);
                //先进行去除
                mpressImgPathList.remove(null);
                mpressImgPathList.add(0, mPressImagePath);
                adapter.update(mpressImgPathList); // 刷新图片
            }else{
                System.out.println("requestCode == ZOOM_REQUEST_CODE else.....");
            }
        }else if(requestCode==EVENT_TYPE_REQUEST_CODE && resultCode==EVENT_TYPE_RESULT_CODE) {//处理点击事件类型的出发事件

            String value = data.getExtras().getString(SuperviseEventTypeActivity.eventTypeKey);
            eventTypeIndex=data.getExtras().getString(SuperviseEventTypeActivity.eventTypeIndex);
            tv_event_type_choose.setTextColor(UiUtils.getInstance().getContext().getResources().getColor(R.color.gambit_sub_title));
            tv_event_type_choose.setText(value);
        }else if(PCIK_IMAGE_RESULT_CODE==requestCode && resultCode == RESULT_OK ){
            if(data != null && data.getData() != null){//有数据返回直接使用返回的图片地址
                startPhotoZoom(data.getData());
            }
        }else if(requestCode == TAKE_IMAGE_REQUEST_CODE && resultCode == TAKE_IMAGE_RESULT_CODE){
            ArrayList<String> name=data.getExtras().getStringArrayList(IMAGE_LIST_KEY_NAME);
            Log.d(TAG,"TAKE_IMAGE_RESULT_CODE name is null:"+(name!=null));
            if(name!=null && name.size()>=0){
                Log.d(TAG,"TAKE_IMAGE_RESULT_CODE name size:"+(name.size()));
                adapter.update(name);
                mpressImgPathList=adapter.getImagePathList();
            }
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
        menuWindow.showAtLocation(findViewById(R.id.choose_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
                   Intent intent = new Intent(MySubSuperviseActivity.this, TakeCameraActivity.class);
                   intent.putStringArrayListExtra(IMAGE_LIST_KEY_NAME, adapter.getImagePathList());
                   intent.putExtra(TakeCameraActivity.MAX_TAKE_IMAGE_COUNT, 3);
                   MySubSuperviseActivity.this.startActivityForResult(intent, TAKE_IMAGE_REQUEST_CODE);
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
        startActivityForResult(intent, PCIK_IMAGE_RESULT_CODE);
    }

    /**
     * 进行调用裁剪系统的功能
     * @param uri
     */
    public void startPhotoZoom(Uri uri){
        Intent intent=new Intent(this,CropperActivity.class);
        intent.setData(uri);
        startActivityForResult(intent, ZOOM_REQUEST_CODE);
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            synchronized (MySubSuperviseActivity.class){
                switch(msg.what){
                    case messageImgWhat:
                         //进行判断
                        System.out.println("messageImgWhat (String)msg.obj:"+(String)msg.obj);
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
                        if (((String)msg.obj).equals("0")){
                            UiUtils.getInstance().showToast("您今天的提报次数已用完了!");
                        }else{
                            //提报成功
                            HDC_MainNumber hdc_mainNumber;
                            try {
                                hdc_mainNumber= MainNumberDao.getInstance().getNumberBy(user.getUserId());
                                hdc_mainNumber.setSuperviseCount(hdc_mainNumber.getSuperviseCount()+1);
                                MainNumberDao.getInstance().saveNumber(hdc_mainNumber);
//                                //进行自动加金币
//                                user.setGoldCoin(user.getGoldCoin()+200);
//                                UserDao.getInstance().saveUpDate(user);
                            } catch (ContentException e) {
                                e.printStackTrace();
                                hdc_mainNumber=new HDC_MainNumber();
                                hdc_mainNumber.setSuperviseCount(hdc_mainNumber.getSuperviseCount()+1);
                                hdc_mainNumber.setUser(user.getUserId());
                                MainNumberDao.getInstance().saveNumber(hdc_mainNumber);
                            }
                            UiUtils.getInstance().showToast("提报成功!");
                        }
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

                    case getVolunteerInfoSuccess://获取志愿者信息成功
                        //执行上传图片的操作
                        uploadImg();
                        break;

                    case getVolunteerInfoFailure://获取志愿者信息失败
                        UiUtils.getInstance().showToast(msg.obj+"");
                        break;

                    case submitDataNoMatch:
                        isSubmitDataNoMatch=true;//志愿者信息错误!
                        UiUtils.getInstance().showToast(msg.obj+"");
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
        params.addQueryStringParameter("tranCode", "AROUND0004");
        params.addQueryStringParameter("eventCode",eventTypeIndex+"");
        params.addQueryStringParameter("address",supervise_tv_position.getText().toString().trim());
        params.addQueryStringParameter("longtitude", locationData.getLatitude());
        params.addQueryStringParameter("latitude", locationData.getLongitude());
        params.addQueryStringParameter("imgIds", getUploadImgStr(uploadFilePath));
        params.addQueryStringParameter("description", supervise_et_event_des.getText().toString().trim());
        System.out.println("MySubSuperviseActivity running user id = "+user.getUserId());
        params.addQueryStringParameter("userId", user.getUserId());
//        System.out.println("user.getVolunteerId() ="+user.getVolunteerId());
        params.addQueryStringParameter("volunteerId", user.getVolunteerId());

        final SuperviseSubmitDataProtocol superviseSubmitDataProtocol = new SuperviseSubmitDataProtocol();
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
                        System.out.println("processSubmitData2  onStart......url:"+getRequestUrl());
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
                            Message messsage = Message.obtain();
                            superviseSubmitDataProtocol.setActionKeyName("文明监督提报失败");
                            messsage.obj= superviseSubmitDataProtocol.parseJson(responseInfo.result);
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
                            if(e.getErrorCode()==HDCivilizationConstants.NO_MATCH_VOLLENTEER){
                                //志愿者信息不匹配:重新获取志愿者id
                                Message messsage = Message.obtain();
                                messsage.what = submitDataNoMatch;//失败
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
                                "org.apache.http.conn.ConnectTimeoutException".contains(error.getMessage())){
                            Message messsage = Message.obtain();
                            messsage.what = submitDataFailure;//失败
                            messsage.obj = "文明监督提报失败,连接超时!";
                            handler.sendMessage(messsage);
                        }else{
                            Message messsage = Message.obtain();
                            messsage.what = submitDataFailure;//失败
                            messsage.obj = "文明监督提报失败!";
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_event_type:
            case R.id.iv_event_type:
                  //点击事件类型
                ToolUtils.getInstance().closeKeyBoard(supervise_et_event_des);
                Intent intent=new Intent(this,SuperviseEventTypeActivity.class);
                startActivityForResult(intent, EVENT_TYPE_REQUEST_CODE);
                break;

            case R.id.ll_iv_position:
                //点击位置定位
                ToolUtils.getInstance().closeKeyBoard(supervise_et_event_des);
                processClickPosition();
                break;

            case R.id.supervise_iv_sub:
                System.out.println("user.getSuperviseNum()1");
                //点击提交的按钮
                /**
                 * 先进判断数据填写的状况
                 *    继而检测网络
                 *    继而进行轮次上传图片
                 *      继而进行提交数据至服务端
                 */
                 //进行检测是否有网
                try {
                     user= UserDao.getInstance().getLocalUser();
                    System.out.println("user.isSupervise="+isSupervise);
                    if(user.getIdentityState().equals(UserPermisson.VOLUNTEER.getType())){//当前用户权限不是志愿者权限
                        try {
                            if(checkSubmitData()){//检查内容是否合理性
                                if(NetUtils.getInstance().checkNetwork(UiUtils.getInstance().getContext())) {
                                    hud = KProgressHUD.create(this)
                                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                            .setLabel("文明监督内容上报中...")
                                            .setCancellable(false);
                                    hud.setCancellable(false);
                                    hud.show();
                                    uploadFilePath.clear();//进行清除上传文件的集合内容
                                    uploadImgCount=getUploadImgCount(mpressImgPathList);

                                    //下面是修改的!.....
//                                  mpressImgPathList.remove(null);
                                    System.out.println("mpressImgPathList size:" + //
                                            mpressImgPathList.size() + "..toString:" + mpressImgPathList.toString());
                                    //进行图片上传
                                    if(isSubmitDataNoMatch){
                                        //进行获取志愿者相关的id
                                        getVolunteerInfo();
                                    }else{
                                        if(!user.getVolunteerId().trim().equals("")){
                                            //进行先上传图片
                                            uploadImg();
                                        }else{
                                            //进行获取志愿者相关的id
                                            getVolunteerInfo();
                                        }
                                    }
                                }else {
                                    UiUtils.getInstance().showToast("请查看网络!");
                                }
                            }
                        } catch (ContentException e) {
                            UiUtils.getInstance().showToast(e.getErrorContent());
                        }
                    }else{
                        //用户权限不够: 以及志願者的id可能沒有
                        UiUtils.getInstance().showToast("当前用户权限不是志愿者权限!");
                    }
                } catch (ContentException e) {
                    e.printStackTrace();
                    UiUtils.getInstance().showToast(e.getErrorContent());
                }
                break;

            //进行返回键
            case R.id.rl_back:
                finish();
                break;

            case R.id.rl_position:
                 ToolUtils.getInstance().closeKeyBoard(supervise_et_event_des);
                 ToolUtils.getInstance().closeKeyBoard(supervise_tv_position);
                break;

            case R.id.ll_upload_img:
                ToolUtils.getInstance().closeKeyBoard(supervise_et_event_des);
                ToolUtils.getInstance().closeKeyBoard(supervise_tv_position);
                break;
        }
    }

    private void uploadImg() {
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
                        System.out.println("conn...uploadImg url:"+getRequestUrl());
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
                        FileUtils.getInstance().saveCrashInfo2File(error);
                        if(error.getMessage().contains("java.net.SocketTimeoutException")||
                                error.getMessage().contains("org.apache.http.conn.ConnectTimeoutException")){
                            Message messsage = Message.obtain();
                            messsage.what = messageImgError;//失败
                            messsage.obj = "文明监督提报失败,连接超时!";
                            handler.sendMessage(messsage);
                        }else{
                            Message messsage = Message.obtain();
                            messsage.what = messageImgError;//失败
                            messsage.obj = "文明监督提报失败!";
                            handler.sendMessage(messsage);
                        }
                        //java.net.SocketTimeoutException
                        System.out.println("uploadImgProtocol  onFailure......"+"error:" + //
                                error.getExceptionCode()+"..getMessage:"+error.getMessage()+"...msg:"+msg);
                    }
                });
    }


    /**
     * 进行获取
     */
    private void getVolunteerInfo(){
        RequestParams params = new RequestParams(); // 默认编码UTF-8
        params.addQueryStringParameter("tranCode","AROUND0025");
        params.addQueryStringParameter("userId", user.getUserId());
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                UrlParamsEntity.CURRENT_ID,
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
                            GetVolunteerInfoProtocol getVolunteerInfoProtocol=new GetVolunteerInfoProtocol();
                            getVolunteerInfoProtocol.setUserId(user.getUserId());
                            getVolunteerInfoProtocol.setOutUser(user);
                            getVolunteerInfoProtocol.setActionKeyName("提报信息失败");
                            message.obj=getVolunteerInfoProtocol.parseJson(responseInfo.result);
                            message.what=getVolunteerInfoSuccess;
                            handler.sendMessage(message);
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                            Message message=Message.obtain();
                            message.what=getVolunteerInfoFailure;
                            message.obj=e.getMessage();
                            handler.sendMessage(message);
                        } catch (ContentException e){
                            e.printStackTrace();
                            Message message=Message.obtain();
                            message.what=getVolunteerInfoFailure;
                            message.obj=e.getErrorContent();
                            handler.sendMessage(message);
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        //java.net.SocketTimeoutException
                        System.out.println("uploadImgProtocol  onFailure......"+"error:" + //
                                error.getExceptionCode()+"..getMessage:"+error.getMessage()+"...msg:"+msg);
                        if(error.getMessage().contains("java.net.SocketTimeoutException")||
                                error.getMessage().contains("org.apache.http.conn.ConnectTimeoutException")){
                            Message messsage = Message.obtain();
                            messsage.what = getVolunteerInfoFailure;//失败
                            messsage.obj = "文明监督提报失败,连接超时!";
                            handler.sendMessage(messsage);
                        }else{
                            Message messsage = Message.obtain();
                            messsage.what = getVolunteerInfoFailure;//失败
                            messsage.obj = "文明监督提报失败!";
                            handler.sendMessage(messsage);
                        }
                    }
                });
    }

    /**
     * 进行检测填写的内容的合理性
     * @return
     */
    private boolean checkSubmitData() throws ContentException {
        //首先检测事件类型有无选择
        String chooseStr= UiUtils.getInstance().getContext().getResources().getString(R.string.supervise_click_choose);
        if(!tv_event_type_choose.getText().toString().equals(chooseStr)){
            //进行判断是否选择详细地址 supervise_tv_position  supervise_click_position
            String positionStr= UiUtils.getInstance().getContext().getResources().getString(R.string.supervise_click_position);
            if(!supervise_tv_position.getText().toString().equals(positionStr) &&
                                !"".equals(supervise_tv_position.getText().toString().trim())){
                int size="中".getBytes().length;
                if(supervise_tv_position.getText().toString().trim().getBytes().length>
                                                        HDCivilizationConstants.SUBMIT_ADDRESS_MAX_LENGTH*size){
                    throw new ContentException("地理位置不能超过"+ HDCivilizationConstants.SUBMIT_ADDRESS_MAX_LENGTH+"汉字");
                }
                if(locationData==null){
                    //先进行判断 是否点击过地理位置信息 如果Null,进行获取
                    locationData=locationInterfaceClass.getLeastLocationData();
                    if(locationData==null){
                        throw new ContentException("定位失败,请查网络或者定位权限!");
                    }
                }

                //进行判断上传图片的个数
                if(getUploadImgCount(mpressImgPathList)>0){
                    //进行判断事件描述 supervise_et_event_des  et_hint_event_des
                    String et_hint_event_des= UiUtils.getInstance().getContext().getResources().getString(R.string.et_hint_event_des);
                    String inputString=supervise_et_event_des.getText().toString().trim();
//                    if(et_hint_event_des.equals(inputString)){
                        //判断是否包含原先的字段
//                        throw new ContentException("事件描述内容不能为空!");
//                    }else{
//                        if(inputString.trim().getBytes().length>
//                                  HDCivilizationConstants.SUPERVISE_EVENT_DES_LENGTH*size &&
//                                            inputString.trim().getBytes().length<HDCivilizationConstants.SUPERVISE_EVENT_DES_LENGTH_MAX*size){
//                            //正常
//                            return true;
//                        }else if(inputString.trim().length()<=0){
//                            throw new ContentException("事件描述内容不能为空!");
//                        }else{
//                            throw new ContentException("事件描述内容不能超过"+HDCivilizationConstants.SUPERVISE_EVENT_DES_LENGTH_MAX+"个汉字");
//                        }
//                    }
                }else{
                    throw new ContentException("上传图片为空!");
                }
            }else{
                throw new ContentException("请填写地理位置!");
            }
        }else{
            throw new ContentException("请选择事件类型!");
        }
        return  true;
    }


    /**
     * 进行获取上传照片的个数
     * @param mpressImgPathList
     * @return
     */
    private int getUploadImgCount(List<String> mpressImgPathList) {
        int count=0;
        for(String data:mpressImgPathList){
            if(data!=null){
                ++count;
            }
        }
        return count;
    }

    LocationService.LocationData locationData;
    /**
     * 进行处理点击定位位置的业务逻辑
     */
    private void processClickPosition(){
        /**
         * 首先进行判断myServiceConnectino是否为空
         *     继而进行判断是否有网
         *         判断是否有位置信息
         */
        if(locationInterfaceClass!=null){
            if(NetUtils.getInstance().checkNetwork(UiUtils.getInstance().getContext())){
                //进行判断网络
                locationData=locationInterfaceClass.getLeastLocationData();
                if(locationData!=null){
                    //进行判断获取的位置对象是否为空
                    SupervisePositionPopup.instance.showPopup(rl_position, new SupervisePositionPopup.SupervisePositionCallbackListener() {
                        @Override
                        public void getPosition(String position) {
                            supervise_tv_position.setText(position);
                            supervise_tv_position.setSelection(position.length());
                        }
                    },  locationData.getPositoinDetail());
                }else{
                    UiUtils.getInstance().showToast("定位失败,请查网络或者定位权限!");
                }
            }else{
                UiUtils.getInstance().showToast("请查看网络!");
            }
        }else{
            System.out.println("等待服务的绑定!");
        }

    }
    public class MyServiceConnecetion implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            locationInterfaceClass=(LocationService.LocationInterfaceClass)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

}

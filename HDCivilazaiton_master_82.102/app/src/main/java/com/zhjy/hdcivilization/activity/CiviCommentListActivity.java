package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.zhjy.hdcivilization.dao.CivistateDao;
import com.zhjy.hdcivilization.dao.CommentDao;
import com.zhjy.hdcivilization.dao.NoticeDao;
import com.zhjy.hdcivilization.dao.UserCommentListDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_CiviState;
import com.zhjy.hdcivilization.entity.HDC_CommentDetail;
import com.zhjy.hdcivilization.entity.HDC_Notice;
import com.zhjy.hdcivilization.entity.HDC_UserCommentList;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.progressbar.KProgressHUD;
import com.zhjy.hdcivilization.protocol.CommentListProtocol;
import com.zhjy.hdcivilization.protocol.ContentSendProtocol;
import com.zhjy.hdcivilization.protocol.MainNumberProtocol;
import com.zhjy.hdcivilization.utils.BitmapUtil;
import com.zhjy.hdcivilization.utils.DateUtil;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.ToolUtils;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.LoadMoreRecyclerView;
import com.zhjy.hdcivilization.view.OKPopup;
import com.zhjy.hdcivilization.view.SendCommentPopup;
import com.zhjy.hdcivilization.view.SimpleSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/8/8.
 *           文明评论列表
 */
public class CiviCommentListActivity extends BaseActivity {

    private LinearLayoutManager linearLayoutManager;
    private SimpleSwipeRefreshLayout mSwipeRefreshLayout;
    private LoadMoreRecyclerView recyclerView;
    private String refershName = "刷新评论信息失败";
    private String loadmoreName = "加载更多评论信息失败";
    private String loadFirstPage = "加载评论信息失败";
    private CommentListProtocol commentListProtocol = new CommentListProtocol();
    private CommentListAdapter commentListAdapter;
    private List<HDC_UserCommentList> datas = new ArrayList<HDC_UserCommentList>();

    private ImageButton bottom_shared;
    private ImageView btnBack;
    private Button enter_comment;
    private RelativeLayout rl_layout,rl_back;
    private final int PAGE_SIZE = 12;//固定个数
    private int firstPage = 1;
    private int currentPage = firstPage;
    public static String ITEMID = "item_id";//fatherItemId
    public static String ITEMUSER_ID = "item_user_id";//条目的用户id
    public static String ITEM_ID_AND_TYPE = "ITEM_ID_AND_TYPE";//条目的id和类型
    public static String SEND_CONTENT_TYPE="send_content_type";//不同类型的评论
    public static String THEMETYPE = "themeType";
    private String fatherItemId, itemUserId, itemIdAndType, themeType;
    private String userId;
    private String nickName;
    private String countNumber;
    private String sendContentType;
    private User user;
    private final int contentSendSuccess = 206;//发送评论的内容成功请求码
    private final int contentSendFailure = 207;//发送评论的内容失败请求码
    private TextView btnZan,btnShare;
    private TextView btnComment;
    private TextView tvCommentNum;
    private  String sendContent,content;
    private String commentItemId;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HDCivilizationConstants.REQUEST_FIRST_PAGE:
                    UserCommentListDao.getInstance().clearItemAndType(fatherItemId, itemIdAndType);
                    datas = (List<HDC_UserCommentList>) msg.obj;
                    if (datas.size() == 0) {
                        UiUtils.getInstance().showToast("尚未发表任何评论!");
                        datas.clear();
                    } else {
                        UserCommentListDao.getInstance().saveAll(datas);
                        setDataToView(datas);
                        getNumberForServer();
                    }
                    commentListAdapter.setDatas(datas);
                    recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                    currentPage = firstPage;
                    mSwipeRefreshLayout.setRefreshing(false);
                    //进行对应种类的消息提醒个数不显示
                    setItemTipCount();
                    break;

                case HDCivilizationConstants.REFRESH_PAGE:
                    UserCommentListDao.getInstance().clearItemAndType(fatherItemId, itemIdAndType);
                    datas = (List<HDC_UserCommentList>) msg.obj;
                    if (datas.size() == 0) {
                        UiUtils.getInstance().showToast("尚未发表任何评论!");
                        datas.clear();
                    } else {
                        UserCommentListDao.getInstance().saveAll(datas);
                        setDataToView(datas);
                        getNumberForServer();
                    }
                    currentPage = firstPage;
                    mSwipeRefreshLayout.setRefreshing(false);
                    commentListAdapter.setDatas(datas);
                    recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                    //将对应种类条目的消息提醒个数进行重置:
                    setItemTipCount();
                    break;

                case HDCivilizationConstants.LOAD_MORE:
                    if (((List<HDC_UserCommentList>) msg.obj).size() == 0){
                        UiUtils.getInstance().showToast("没有更多评论！");
                        recyclerView.notifyMoreFinish(true);
                    }else{
                        datas.addAll((List<HDC_UserCommentList>) msg.obj);
                        UserCommentListDao.getInstance().saveAll((List<HDC_UserCommentList>) msg.obj);
                        commentListAdapter.setDatas(datas);
                        recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(datas.size() - 1);
                        recyclerView.notifyMoreFinish(true);//设置可以继续加载更多数据
                    }
                    currentPage = currentPage + 1;
                    mSwipeRefreshLayout.setRefreshing(false);
                    getNumberForServer();
                    setItemTipCount();
                    break;

                case contentSendSuccess:
                    datas.add(0, (HDC_UserCommentList) msg.obj);
                    UserCommentListDao.getInstance().saveObj((HDC_UserCommentList) msg.obj);

                    //话题评论数量
                    List<HDC_CommentDetail> HDC_CommentDetails = CommentDao.getInstance().getHDC_CommentDetailList(fatherItemId);
                    for (HDC_CommentDetail data1 : HDC_CommentDetails) {
                        data1.setCommentCount(data1.getCommentCount() + 1);
                    }
                    CommentDao.getInstance().saveAll(HDC_CommentDetails);

                    //从数据库中重新查找相关数据，刷新UI显示
                    commentListAdapter.setDatas(datas);
                    recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                    SendCommentPopup.instance.dismissDialog();
                    if(hud!=null){
                        hud.dismiss();
                    }
                    if(themeType.equals(HDCivilizationConstants.THEME_TYPE) && itemUserId.equals(userId)){
                        //如果条目的用户的id==userId 对自己发送的评论
                        getNumberForServer();
                    }
                    break;
                case contentSendFailure:
//                    SendCommentPopup.instance.dismissDialog();  //暂时不消失
                    UiUtils.getInstance().showToast((String)msg.obj);
                    if(hud!=null){
                        hud.dismiss();
                    }
                    break;

                case HDCivilizationConstants.ERROR_CODE:
                    UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                    if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)== HDCivilizationConstants.LOAD_MORE){
                        recyclerView.notifyMoreFinish(true);
                    }else if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)== HDCivilizationConstants.REFRESH_PAGE){
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                    }else if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)== HDCivilizationConstants.REQUEST_FIRST_PAGE){
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                    }
                    break;
            }
        }
    };

    /**
     * 进行设置对应种类的
     *          消息提醒数目为0
     */
    private void setItemTipCount(){
        if(themeType.equals(HDCivilizationConstants.NEWS_TYPE)){
            //动态类型 将对应条目的id消息提醒个数进行设置为0
            HDC_CiviState data= CivistateDao.getInstance().getCiviState(fatherItemId,userId);
            if(data!=null){
                data.setTipCount(0);
                CivistateDao.getInstance().saveAllCivistateoObj(data);
            }

        }else if(themeType.equals(HDCivilizationConstants.THEME_TYPE)){
            //主题类型 将对应条目的id消息提醒个数进行设置为0
            List<HDC_CommentDetail> datas= //
                        CommentDao.getInstance().getHDC_CommentDetailList(fatherItemId);
            if(datas!=null && datas.size()>0) {
                for (HDC_CommentDetail data:datas){
                    data.setTypeCount(0);
                }
            }
            CommentDao.getInstance().saveAll(datas);

        }else if(themeType.equals(HDCivilizationConstants.NOTIFY_TYPE)){
            //通知公告
            HDC_Notice data= NoticeDao.getInstance().getNotice(fatherItemId,userId);
            if(data!=null){
                data.setTipCount(0);
                NoticeDao.getInstance().saveNoticeObj(data);
            }
        }else if(themeType.equals(HDCivilizationConstants.COMMENT_TYPE)){
            //评论类型 暂不处理
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        /*****获取用户对象******/
        try {
            user = UserDao.getInstance().getLocalUser();
            userId= user.getUserId();
            nickName = user.getNickName();
            countNumber = user.getAccountNumber();
        } catch (ContentException e) {
            e.printStackTrace();
            userId="";
            nickName="";
            countNumber="";
        }
        //话题itemid
        List<HDC_UserCommentList> list = UserCommentListDao.getInstance().getList(fatherItemId,itemIdAndType);
        if (list!=null && list.size()>0){//判断数据库中是否有数据
            datas=list;
            setDataToView(list);
        }
    }

    private void setDataToView(List<HDC_UserCommentList> datas) {
        commentListAdapter.setDatas(datas);
        recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contentView= UiUtils.getInstance().inflate(R.layout.activity_civi_comment_list);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        fatherItemId = getIntent().getStringExtra(ITEMID);//条目id
        itemUserId = getIntent().getStringExtra(ITEMUSER_ID);//条目用户的id
        itemIdAndType = getIntent().getStringExtra(ITEM_ID_AND_TYPE);
        themeType = getIntent().getStringExtra(THEMETYPE);
        sendContentType = getIntent().getStringExtra(SEND_CONTENT_TYPE);
        try {
            user = UserDao.getInstance().getLocalUser();
            userId = user.getUserId();
            nickName = user.getNickName();
            countNumber = user.getAccountNumber();
            System.out.println("initViews userId ="+userId+",nickName = "+nickName+",countNumber = "+countNumber);
        } catch (ContentException e) {
            e.printStackTrace();
            userId = "";
            nickName = "";
            countNumber="";
        }

        mSwipeRefreshLayout = (SimpleSwipeRefreshLayout) findViewById(R.id.comment_swipe_refresh);
        recyclerView = (LoadMoreRecyclerView) findViewById(R.id.civi_comment_list_recyclerview);
        linearLayoutManager = new LinearLayoutManager(UiUtils.getInstance().getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        //添加不同的linearLayoutManager防止刷新数据至上
        recyclerView.switchLayoutManager(new LinearLayoutManager(UiUtils.getInstance().getContext()));
        recyclerView.setPageSize(PAGE_SIZE);
        mSwipeRefreshLayout.setChild(recyclerView);

        btnBack = (ImageView) findViewById(R.id.btn_back);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        btnZan = (TextView) findViewById(R.id.bottom_logo_zan);
        btnComment = (TextView) findViewById(R.id.bottom_comment);
        btnShare = (TextView) findViewById(R.id.bottom_shared);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        enter_comment = (Button) findViewById(R.id.enter_comment);
        rl_layout = (RelativeLayout) findViewById(R.id.rl_layout);
        tvCommentNum = (TextView)findViewById(R.id.tv_comment_number);//评论数字提示

        /***隐藏三个按钮**/
        btnComment.setVisibility(View.GONE);
        tvCommentNum.setVisibility(View.GONE);
        btnZan.setVisibility(View.GONE);
        btnShare.setVisibility(View.GONE);

        if (commentListAdapter == null) {
            commentListAdapter = new CommentListAdapter(datas);
            recyclerView.setAdapter(commentListAdapter);
        }
    }

    //判断是否是主题发布人
    public String isInfoReadStatus() {
        //当themeType类型是通知公告和文明动态的时：直接返回否=“0”
        if (!themeType.equals(HDCivilizationConstants.NEWS_TYPE) || !themeType.equals(HDCivilizationConstants.NOTIFY_TYPE)) {
            if (userId.equals(itemUserId)) {
                return "1";
            } else {
                return "0";
            }
        } else {
            return "0";
        }
    }





    @Override
    protected void initInitevnts() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3, R.color.color4);
//        第一次请求数据的时候查看数据库的操作
         List<HDC_UserCommentList> list = UserCommentListDao.getInstance().getList(fatherItemId,itemIdAndType);

        if (list!=null && list.size()>0){//判断数据库中是否有数据
            setDataToView(list);
            getFristData();//请求第一屏的数据
            getRefreshData();//刷新最新数据
        }else{
            getFristData();//请求第一屏的数据
            getRefreshData();//刷新最新数据
        }

        recyclerView.setAutoLoadMoreEnable(true);
        recyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                try {
                                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                                    LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                                    paramsMap.put("tranCode", "AROUND0023");
                                    paramsMap.put("itemId", fatherItemId);
                                    paramsMap.put("userId", userId);
                                    paramsMap.put("pagerNum", PAGE_SIZE + "");
                                    paramsMap.put("currentPager", (currentPage + 1) + "");
                                    paramsMap.put("targetUserId", itemUserId);
                                    paramsMap.put("infoReadStatus", isInfoReadStatus());
                                    /****根据不同的类型查找不同的评论列表数据**/
                                    if (sendContentType.equals(HDCivilizationConstants.THEME_TYPE)){
                                        paramsMap.put("themeType", HDCivilizationConstants.THEME_TYPE);
                                    }else if (sendContentType.equals(HDCivilizationConstants.NEWS_TYPE)){
                                        paramsMap.put("themeType", HDCivilizationConstants.NEWS_TYPE);
                                    }else if (sendContentType.equals(HDCivilizationConstants.NOTIFY_TYPE)) {
                                        paramsMap.put("themeType", HDCivilizationConstants.NOTIFY_TYPE);
                                    }
                                    urlParamsEntity.setParamsHashMap(paramsMap);
                                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                                    //设置父类型
                                    commentListProtocol.setFatherItemId(fatherItemId);
                                    commentListProtocol.setItemIdAndType(itemIdAndType);
                                    commentListProtocol.setActionKeyName(loadmoreName);
                                    message.obj = commentListProtocol.loadData(urlParamsEntity);
                                    message.what = HDCivilizationConstants.LOAD_MORE;
                                    handler.sendMessage(message);
                                } catch (JsonParseException e) {
                                    e.printStackTrace();
                                    message.what = HDCivilizationConstants.ERROR_CODE;
                                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.LOAD_MORE);
                                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT,e.getMessage());
                                    message.setData(bundle);
                                    handler.sendMessage(message);
                                } catch (ContentException e) {
                                    e.printStackTrace();
                                    if(e.getErrorCode()== HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE){
                                        //权限过低的处理业务: 此时这种情况只能是 用户id不一致
                                        message.what = HDCivilizationConstants.ERROR_CODE;
                                        bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.LOAD_MORE);
                                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT,loadmoreName+"!");
                                        message.setData(bundle);
                                        handler.sendMessage(message);
                                    }else {
                                        message.what = HDCivilizationConstants.ERROR_CODE;
                                        bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.LOAD_MORE);
                                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                                        message.setData(bundle);
                                        handler.sendMessage(message);
                                    }
                                }
                            }
                        });
                    }
                }, 1000);
            }
        });

        /*******评论内容的发送*******/
        enter_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进行设置点击事件
                SendCommentPopup.instance.showPopup(CiviCommentListActivity.this, new SendCommentPopup.BtnSendCommentListener() {
                    @Override
                    public void sendEditComment(EditText editText) {
                        //进行获取发送内容的编辑框:
                        sendContent = editText.getText().toString();
                        try {
                            User user = UserDao.getInstance().getLocalUser();
                            if (sendContent.trim().equals("")) {
                                UiUtils.getInstance().showToast("请填写评论内容!");
                            } else {
                                int size = "中".getBytes().length;
                                if (sendContent.trim().getBytes().length <= HDCivilizationConstants.MIN_SEND_COMMENT_LENGTH*size) {
                                    //进行获取内容进行发送
                                    hud = KProgressHUD.create(CiviCommentListActivity.this)
                                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                            .setLabel("评论发表中...")
                                            .setCancellable(false);
                                    hud.setCancellable(false);
                                    hud.show();
                                    RequestParams params = new RequestParams();
//                                    params.addQueryStringParameter("tranCode", "AROUND0029");
                                    params.addQueryStringParameter("tranCode", "AROUND0052");
                                    params.addQueryStringParameter("userId", user.getUserId());
                                    /******************************/
                                    String content = ToolUtils.getInstance().filterEmoji(sendContent);
                                    /******************************/
                                    params.addQueryStringParameter("content", content);
                                    params.addQueryStringParameter("desItemId", fatherItemId);
                                    if (sendContentType.equals(HDCivilizationConstants.THEME_TYPE)){
                                        params.addQueryStringParameter("itemType", HDCivilizationConstants.THEME_TYPE);
                                    }else if (sendContentType.equals(HDCivilizationConstants.NEWS_TYPE)){
                                        params.addQueryStringParameter("itemType", HDCivilizationConstants.NEWS_TYPE);
                                    }else if (sendContentType.equals(HDCivilizationConstants.NOTIFY_TYPE)){
                                        params.addQueryStringParameter("itemType", HDCivilizationConstants.NOTIFY_TYPE);
                                    }
                                    if (nickName!= null && !nickName.equals("")){
                                        params.addQueryStringParameter("userName", nickName);
                                    }else{
                                        params.addQueryStringParameter("userName", countNumber);
                                    }

                                    //新增加的
                                    if(itemUserId.equals(userId)){
                                        //如果相等
                                        params.addQueryStringParameter("isNeedRead", "0");
                                    }else{
                                        params.addQueryStringParameter("isNeedRead", "1");
                                    }
                                    final ContentSendProtocol contentSendProtocol = new ContentSendProtocol();
                                    HttpUtils http = new HttpUtils();
                                    http.send(HttpRequest.HttpMethod.POST,
                                            UrlParamsEntity.HDCURL_UPLOAD_DATA,
                                            params,
                                            new RequestCallBack<String>() {

                                                @Override
                                                public void onStart() {
                                                    super.onStart();
                                                    System.out.println("http AROUND0029:"+getRequestUrl());
                                                }

                                                @Override
                                                public void onLoading(long total, long current, boolean isUploading) {
                                                    super.onLoading(total, current, isUploading);
                                                }

                                                @Override
                                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                                    try {
                                                        Message messsage = Message.obtain();
                                                        contentSendProtocol.setItemIdAndType(itemIdAndType);
                                                        contentSendProtocol.setFatherItemId(fatherItemId);
                                                        contentSendProtocol.setActionKeyName("评论失败!");
                                                        contentSendProtocol.setKeyName("评论");
                                                        messsage.obj = contentSendProtocol.parseJson(responseInfo.result);
                                                        messsage.what = contentSendSuccess;
                                                        handler.sendMessage(messsage);
                                                    } catch (JsonParseException e) {
                                                        e.printStackTrace();
                                                        Message messsage = Message.obtain();
                                                        messsage.what = contentSendFailure;//失败
                                                        messsage.obj = e.getMessage();
                                                        handler.sendMessage(messsage);
                                                    } catch (ContentException e) {
                                                        e.printStackTrace();
                                                        Message messsage = Message.obtain();
                                                        messsage.what = contentSendFailure;//失败
                                                        messsage.obj = e.getErrorContent();
                                                        handler.sendMessage(messsage);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(HttpException e, String s) {
                                                    Message messsage = Message.obtain();
                                                    messsage.what = contentSendFailure;//失败
                                                    messsage.obj = "评论失败!";
                                                    handler.sendMessage(messsage);

                                                }
                                            });
                                } else {
                                    UiUtils.getInstance().showToast("评论内容不能超过" + HDCivilizationConstants.MIN_SEND_COMMENT_LENGTH+"个字！");
                                }
                            }

                        } catch (ContentException e) {
                            e.printStackTrace();
                            ToolUtils.getInstance().closeKeyBoard(editText);
                            /******未登录的时候，提示用户去登录*****/
                            OKPopup.getInstance().showPopup(CiviCommentListActivity.this, new OKPopup.BtnClickListener() {
                                @Override
                                public void btnOk() {
                                    Intent intent = new Intent(CiviCommentListActivity.this, LoginActivity.class);
                                    intent.putExtra(LoginActivity.ISFROM_OTHRES,true);
                                    startActivity(intent);
                                    OKPopup.getInstance().dismissDialog();
                                }
                            }, false, HDCivilizationConstants.NO_LOGIN);
                        }
                    }
                });
            }
        });

    }

    private void getRefreshData() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        try {
                            UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                            LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                            paramsMap.put("tranCode", "AROUND0023");
                            paramsMap.put("itemId", fatherItemId);
                            paramsMap.put("userId", userId);
                            paramsMap.put("pagerNum", PAGE_SIZE + "");
                            paramsMap.put("currentPager", firstPage + "");
                            paramsMap.put("targetUserId", itemUserId);
                            paramsMap.put("infoReadStatus", isInfoReadStatus());
                            paramsMap.put("themeType", themeType);
                            urlParamsEntity.setParamsHashMap(paramsMap);
                            urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                            commentListProtocol.setItemIdAndType(itemIdAndType);
                            commentListProtocol.setFatherItemId(fatherItemId);
                            commentListProtocol.setActionKeyName(refershName);
                            message.obj = commentListProtocol.loadData(urlParamsEntity);
                            message.what = HDCivilizationConstants.REFRESH_PAGE;
                            handler.sendMessage(message);
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                            message.what = HDCivilizationConstants.ERROR_CODE;
                            bundle.putString(HDCivilizationConstants.ERROR_CONTENT,e.getMessage());
                            bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REFRESH_PAGE);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } catch (ContentException e) {
                            e.printStackTrace();
                            if(e.getErrorCode()== HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE){
                                //权限过低的处理业务: 此时这种情况只能是 用户id不一致
                                message.what = HDCivilizationConstants.ERROR_CODE;
                                bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REFRESH_PAGE);
                                bundle.putString(HDCivilizationConstants.ERROR_CONTENT,refershName+"!");
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }else {
                                message.what = HDCivilizationConstants.ERROR_CODE;
                                bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REFRESH_PAGE);
                                bundle.putString(HDCivilizationConstants.ERROR_CONTENT,e.getErrorContent());
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * 请求第一屏的数据
     */
    private void getFristData() {

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        try {
                            UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                            LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                            paramsMap.put("tranCode", "AROUND0023");
                            paramsMap.put("itemId", fatherItemId);//条目id
                            paramsMap.put("userId", userId);
                            paramsMap.put("pagerNum", PAGE_SIZE + "");
                            paramsMap.put("currentPager", firstPage + "");
                            paramsMap.put("targetUserId", itemUserId);
                            paramsMap.put("infoReadStatus", isInfoReadStatus());
                            paramsMap.put("themeType", themeType);
                            urlParamsEntity.setParamsHashMap(paramsMap);
                            urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                            commentListProtocol.setItemIdAndType(itemIdAndType);
                            commentListProtocol.setFatherItemId(fatherItemId);
                            commentListProtocol.setActionKeyName(loadFirstPage);
                            message.obj = commentListProtocol.loadData(urlParamsEntity);
                            message.what = HDCivilizationConstants.REQUEST_FIRST_PAGE;
                            handler.sendMessage(message);
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                            message.what = HDCivilizationConstants.ERROR_CODE;
                            bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REQUEST_FIRST_PAGE);
                            bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } catch (ContentException e) {
                            e.printStackTrace();
                            if (e.getErrorCode() == HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE) {
                                //权限过低的处理业务: 此时这种情况只能是 用户id不一致
                                message.what = HDCivilizationConstants.ERROR_CODE;
                                bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REQUEST_FIRST_PAGE);
                                bundle.putString(HDCivilizationConstants.ERROR_CONTENT, loadFirstPage + "!");
                                message.setData(bundle);
                                handler.sendMessage(message);
                            } else {
                                message.what = HDCivilizationConstants.ERROR_CODE;
                                bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REQUEST_FIRST_PAGE);
                                bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                        }
                    }
                });
            }
        });
    }

    KProgressHUD hud;//进度条

    /***
     * RecyclerView的适配器
     */
    class CommentListAdapter extends LoadMoreRecyclerView.Adapter<CommentListAdapter.MineHolder> {

        private List<HDC_UserCommentList> datas;

        private boolean mIsStagger;

        public void switchMode(boolean mIsStagger) {
            this.mIsStagger = mIsStagger;
        }

        public void addDatas(List<HDC_UserCommentList> datas) {
            datas.addAll(datas);
        }

        public List<HDC_UserCommentList> getDatas() {
            return datas;
        }

        public void setDatas(List<HDC_UserCommentList> datas) {
            this.datas = datas;
        }

        public CommentListAdapter(List<HDC_UserCommentList> datas) {
            this.datas = datas;
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public MineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(UiUtils.getInstance().getContext(), R.layout.listview_item_civi_comment_list, null);
            MineHolder holder = new MineHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MineHolder holder, final int position) {
            holder.itemView.setOnClickListener(new MyViewOnClickListener(position) {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CiviCommentListActivity.this, CiviCommentListDetailActivity.class);
                    intent.putExtra(CiviCommentListDetailActivity.ITEMIDTHEME, fatherItemId);//itemId条目id
                    intent.putExtra(CiviCommentListDetailActivity.ITEMID, datas.get(this.position).getItemId());//itemId条目id
                    intent.putExtra(CiviCommentListDetailActivity.ITEMUSER_ID, datas.get(this.position).getUser().getUserId());//条目的用户id
                    startActivity(intent);
                }
            });
            if(datas.get(position).getUser().getPortraitUrl()!=null &&  datas.get(position).getUser().getPortraitUrl().startsWith("http://")){
                BitmapUtil.getInstance().displayUserPic(holder.userPic, datas.get(position).getUser().getPortraitUrl());
            }else{
                BitmapUtil.getInstance().displayUserPic(holder.userPic, UrlParamsEntity.WUCHEN_XU_IP_FILE + datas.get(position).getUser().getPortraitUrl());
            }
            String nickName="";
            HDC_UserCommentList cd=datas.get(position);
            if(cd.getUser().getNickName().matches("^[1][3,4,5,8,7][0-9]{9}$")){
                nickName=cd.getUser().getNickName().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
            }else{
                try {
                    User user= UserDao.getInstance().getLocalUser();
                    //进行判断对象是否和该条目的launcherUser一致
                    if(user.getUserId().equals(cd.getUser().getUserId())) {
                        //如果id一致 昵称是否为空 :y 手机号
                        nickName = cd.getUser().getNickName().equals("") ?//
                                user.getAccountNumber() : cd.getUser().getNickName();
                        if (nickName.matches("^[1][3,4,5,8,7][0-9]{9}$")) {
                            nickName = nickName.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                        }
                    }else{
                        nickName=cd.getUser().getNickName();
                    }
                } catch (ContentException e) {
                    e.printStackTrace();
                    nickName=cd.getUser().getNickName();
                }
            }
            holder.userName.setText(nickName);
            if (datas.get(position).getUser().getIdentityState().equals(HDCivilizationConstants.FOUR)){
                holder.volunteerName.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
            }else if (!datas.get(position).getUser().getIdentityState().equals(HDCivilizationConstants.FOUR)){
                holder.volunteerName.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
            }
            /******************/
            String string =datas.get(position).getContent();
            ToolUtils.getInstance().setString(holder.content, string);
            /******************/
//            holder.content.setText(string);
            holder.time.setText(DateUtil.getInstance().getDayOrMonthOrYear1(datas.get(position).getPublishTime()));
            holder.totalCount.setText(datas.get(position).getCount() + "条回复");
        }

        class MineHolder extends LoadMoreRecyclerView.ViewHolder {
            private ImageView userPic;
            private TextView userName;
            private TextView content;
            private TextView totalCount;
            private TextView time;
            private TextView volunteerName;

            public MineHolder(View itemView) {
                super(itemView);
                userPic = (ImageView) itemView.findViewById(R.id.user_pic);
                userName = (TextView) itemView.findViewById(R.id.user_name);
                content = (TextView) itemView.findViewById(R.id.comment_content);
                time = (TextView) itemView.findViewById(R.id.time__);
                totalCount = (TextView) itemView.findViewById(R.id.totalCount);
                volunteerName = (TextView) itemView.findViewById(R.id.userPermiession_list);
            }
        }
    }

    /***数字更新网络请求**/
    private void getNumberForServer() {
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                User user = null;
                try {
                    //进行获取本地用户
                    user = UserDao.getInstance().getLocalUser();
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode", "AROUND0003");
//            System.out.println("userId ="+userId);
                    paramsMap.put("userId", user.getUserId());//userId
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    MainNumberProtocol mainNumberProtocol = new MainNumberProtocol();
                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                    mainNumberProtocol.setUserId(user.getUserId());
                    mainNumberProtocol.setUser(user);
                    mainNumberProtocol.loadData(urlParamsEntity);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                } catch (ContentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    abstract class MyViewOnClickListener implements View.OnClickListener{
        int position;

        public MyViewOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {

        }
    }

}

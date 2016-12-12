package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
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
import com.zhjy.hdcivilization.dao.CommentDao;
import com.zhjy.hdcivilization.dao.UserCommentListDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_CommentDetail;
import com.zhjy.hdcivilization.entity.HDC_UserCommentList;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
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
import com.zhjy.hdcivilization.view.CircleImageView;
import com.zhjy.hdcivilization.view.LoadMoreRecyclerView;
import com.zhjy.hdcivilization.view.OKPopup;
import com.zhjy.hdcivilization.view.SendCommentPopup;
import com.zhjy.hdcivilization.view.ShareCommentPopup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/8/9.
 *         文明评论列表详情
 */
public class CiviCommentListDetailActivity extends BaseActivity {

    private LoadMoreRecyclerView recyclerView;
    private TextView bottom_shared;
    private TextView btnZan;
    private CircleImageView detailUserPic;
    private TextView detailUserName,btnComment;
    private TextView detailConetnt;
    private TextView detailTime, tvCommentNum, userState;
    private String keyName = "评论详情";
    private String refershName = "刷新回复信息失败";
    private String loadmoreName = "加载更多回复信息失败";
    private String loadFirstPage = "加载回复信息失败";
    private CommentListProtocol commentListProtocol = new CommentListProtocol();
    private CommentListDetailAdapter commentAdapter;
    private List<HDC_UserCommentList> datas = new ArrayList<HDC_UserCommentList>();
    private LinearLayoutManager linearLayoutManager;
    private RelativeLayout rl_layout, rl_back;
    private Button enter_comment;
    public static String ITEMID = "item_id";//itemId
    public static String ITEMIDTHEME = "item_theme";//话题的id
    public static String ITEMUSER_ID = "item_user_id";//条目的用户id
    //  public static String ITEMUSER_ID_FATHER="item_user_id_father";//父类条目id
    public static String ITEM_ID_AND_TYPE = "ITEM_ID_AND_TYPE";//条目的id和类型
    private String fatherItemId, itemUserId, fatherItemIdAndType, itemIdTheme;
    private User user;
    private final int PAGE_SIZE = 12;//固定个数
    private int firstPage = 1;
    private int currentPage = firstPage;
    private String userId;
    private String nickName;
    private String countNumber;
    private final int contentSendSuccess = 206;//发送评论的内容成功请求码
    private final int contentSendFailure = 207;//发送评论的内容失败请求码

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HDCivilizationConstants.REQUEST_FIRST_PAGE:
                    datas = (List<HDC_UserCommentList>) msg.obj;
                    if (datas.size() == 0) {
                        UiUtils.getInstance().showToast("尚未发表任何回复!");
                        datas.clear();
                    } else {
                        commentAdapter.setDatas(datas);
                        recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        getNumberForServer();
                    }
                    break;
                case HDCivilizationConstants.LOAD_MORE:
                    datas = (List<HDC_UserCommentList>) msg.obj;
                    if (datas.size() == 0) {
                        UiUtils.getInstance().showToast("没有更多回复!");
                        recyclerView.notifyMoreFinish(true);
                        datas.clear();
                    } else {
                        datas.addAll((List<HDC_UserCommentList>) msg.obj);
                        UserCommentListDao.getInstance().saveAll(datas);
                        commentAdapter.setDatas(datas);
                        commentAdapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(datas.size() - 1);
                        recyclerView.notifyMoreFinish(true);
                    }
                    getNumberForServer();
                    break;

                case contentSendSuccess:
                    datas.add(0, (HDC_UserCommentList) msg.obj);
                    UserCommentListDao.getInstance().saveObj((HDC_UserCommentList) msg.obj);
                    HDC_UserCommentList userCommentList = UserCommentListDao.getInstance().getDatas(fatherItemId);
                    userCommentList.setCount(userCommentList.getCount() + 1);
                    UserCommentListDao.getInstance().saveUpDate(userCommentList);
                    //从数据库中重新查找相关数据，刷新UI显示
                    commentAdapter.setDatas(datas);
                    recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                    SendCommentPopup.instance.dismissDialog();
//                    UiUtils.getInstance().showToast("评论成功!");
                    /*****************************/
                    //这个是对最外层话题评论数加一的操作话题评论数量
                    List<HDC_CommentDetail> HDC_CommentDetails = CommentDao.getInstance().getHDC_CommentDetailList(itemIdTheme);
                    for (HDC_CommentDetail data1 : HDC_CommentDetails) {
                        data1.setCommentCount(data1.getCommentCount() + 1);
                    }
                    CommentDao.getInstance().saveAll(HDC_CommentDetails);

                    if (hud != null) {
                        hud.dismiss();
                    }

                    if (userId.equals(itemUserId)) {
                        //对条目的用户的id==userId
                        getNumberForServer();
                    }
                    break;

                case contentSendFailure:
//                    SendCommentPopup.instance.dismissDialog(); //暂时不消失
                    UiUtils.getInstance().showToast((String) msg.obj);
                    if (hud != null) {
                        hud.dismiss();
                    }
                    break;

                case HDCivilizationConstants.ERROR_CODE:
                    UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                    if (msg.getData().getInt(HDCivilizationConstants.ACTION_CODE) == HDCivilizationConstants.LOAD_MORE) {
                        recyclerView.notifyMoreFinish(true);
                    } else if (msg.getData().getInt(HDCivilizationConstants.ACTION_CODE) == HDCivilizationConstants.REQUEST_FIRST_PAGE) {
                        //请求第一页数据成功暂不处理！
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contentView = UiUtils.getInstance().inflate(R.layout.activity_civi_comment_list_detail);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        fatherItemId = getIntent().getStringExtra(ITEMID);//一级评论点击条目Id
        itemIdTheme = getIntent().getStringExtra(ITEMIDTHEME);//最外层的条目Id
        itemUserId = getIntent().getStringExtra(ITEMUSER_ID);//条目的用户Id
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        bottom_shared = (TextView) findViewById(R.id.bottom_shared);

        detailUserPic = (CircleImageView) findViewById(R.id.details_user_pic);
        detailUserName = (TextView) findViewById(R.id.detail_user_name);
        detailConetnt = (TextView) findViewById(R.id.detail_comment_content);
        detailTime = (TextView) findViewById(R.id.detail_time__);
        userState = (TextView) findViewById(R.id.detail_user_state);

        recyclerView = (LoadMoreRecyclerView) findViewById(R.id.comment_recyclerview);

        recyclerView.setPageSize(PAGE_SIZE);
        linearLayoutManager = new LinearLayoutManager(UiUtils.getInstance().getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.switchLayoutManager(new LinearLayoutManager(UiUtils.getInstance().getContext()));
        enter_comment = (Button) findViewById(R.id.enter_comment);//输入评论按钮
        rl_layout = (RelativeLayout) findViewById(R.id.rl_layout);
        btnComment = (TextView) findViewById(R.id.bottom_comment);//评论列表的按钮
        tvCommentNum = (TextView) findViewById(R.id.tv_comment_number);//评论数字提示
        btnZan = (TextView) findViewById(R.id.bottom_logo_zan);//点赞按钮

        btnComment.setVisibility(View.GONE);
        tvCommentNum.setVisibility(View.GONE);
        btnZan.setVisibility(View.GONE);
        bottom_shared.setVisibility(View.GONE);

        if (commentAdapter == null) {
            synchronized (CiviCommentListDetailActivity.class) {
                if (commentAdapter == null) {
                    commentAdapter = new CommentListDetailAdapter(datas);
                    commentAdapter.setDatas(datas);
                    recyclerView.setAdapter(commentAdapter);
                }
            }
        }
        setFatherData();
    }


    //对最上头部设置数据信息
    private void setFatherData() {
        HDC_UserCommentList userCommentList = UserCommentListDao.getInstance().findData(fatherItemId);
        if (userCommentList != null) {
            BitmapUtil.getInstance().displayImg(detailUserPic, UrlParamsEntity.WUCHEN_XU_IP_FILE + userCommentList.getUser().getPortraitUrl());
            detailUserName.setText(getNickName(userCommentList.getUser()));
//            detailUserName.setText(userCommentList.getUser().getNickName());
            if (Integer.parseInt(userCommentList.getUser().getIdentityState()) == 4) {
                userState.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
            } else if (Integer.parseInt(userCommentList.getUser().getIdentityState()) < 4) {
                userState.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
            }
            ToolUtils.getInstance().setString(detailConetnt,userCommentList.getContent());
//            detailConetnt.setText(userCommentList.getContent());
            detailTime.setText(DateUtil.getInstance().getDayOrMonthOrYear1(userCommentList.getPublishTime()));
        }
    }

    //判断是否是主题发布人
    public String isInfoReadStatus() {
        if (userId.equals(itemUserId)) {
            return "1";
        } else {
            return "0";
        }
    }


    /**
     * 进行获取昵称
     *
     * @param user1
     * @return
     */
    public String getNickName(User user1) {
        String nickName = "";
        if (user1.getNickName().matches("^[1][3,4,5,8,7][0-9]{9}$")) {
            nickName = user1.getNickName().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        } else {
            try {
                User user = UserDao.getInstance().getLocalUser();
                //进行判断对象是否和该条目的launcherUser一致
                if (user.getUserId().equals(user1.getUserId())) {
                    //如果id一致 昵称是否为空 :y 手机号
                    nickName = user1.getNickName().equals("") ?//
                            user.getAccountNumber() : user1.getNickName();
                    if (nickName.matches("^[1][3,4,5,8,7][0-9]{9}$")) {
                        nickName = nickName.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                    }
                } else {
                    nickName = user1.getNickName();
                }
            } catch (ContentException e) {
                e.printStackTrace();
                nickName = user1.getNickName();
            }
        }
        return nickName;
    }

    String content;
    @Override
    protected void initInitevnts() {
        /*****获取用户对象******/
        try {
            user = UserDao.getInstance().getLocalUser();
            userId = user.getUserId();
            nickName = user.getNickName();
            countNumber = user.getAccountNumber();
            System.out.println("initInitevnts userId =" + userId + ",nickName = " + nickName + ",countNumber = " + countNumber);
        } catch (ContentException e) {
            e.printStackTrace();
            userId = "";
            nickName = "";
            countNumber = "";
        }
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(HDCivilizationConstants.STATE, fatherItemIdAndType);
                finish();
            }
        });

        bottom_shared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCommentPopup.instance.showPopup(rl_layout);
            }
        });


        /**加载第一屏数据*/
        getFristData();
        //加载更多的数据
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
                                    paramsMap.put("itemId", fatherItemId);//条目id
                                    paramsMap.put("userId", userId);
                                    paramsMap.put("pagerNum", PAGE_SIZE + "");
                                    paramsMap.put("currentPager", (currentPage + 1) + "");
                                    paramsMap.put("targetUserId", itemUserId);
                                    paramsMap.put("infoReadStatus", isInfoReadStatus());
                                    paramsMap.put("themeType", "theme");
                                    urlParamsEntity.setParamsHashMap(paramsMap);
                                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                                    commentListProtocol.setFatherItemId(fatherItemId);
                                    commentListProtocol.setActionKeyName(loadmoreName);
                                    message.obj = commentListProtocol.loadData(urlParamsEntity);
                                    message.what = HDCivilizationConstants.LOAD_MORE;
                                    System.out.println("comment-----1");
                                    handler.sendMessage(message);
                                } catch (JsonParseException e) {
                                    e.printStackTrace();
                                    message.what = HDCivilizationConstants.ERROR_CODE;
                                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.LOAD_MORE);
                                    message.setData(bundle);
                                    System.out.println("comment-----2");
                                    handler.sendMessage(message);
                                } catch (ContentException e) {
                                    e.printStackTrace();
                                    if (e.getErrorCode() == HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE) {
                                        //此时权限过低:只能是前后用户的userId不匹配了 先进行提示信息 //// TODO: 2016/9/19
                                        message.what = HDCivilizationConstants.ERROR_CODE;
                                        bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.LOAD_MORE);
                                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT, loadmoreName + "!");
                                        message.setData(bundle);
                                        System.out.println("comment-----3");
                                        handler.sendMessage(message);
                                    } else {
                                        message.what = HDCivilizationConstants.ERROR_CODE;
                                        bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.LOAD_MORE);
                                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                                        message.setData(bundle);
                                        System.out.println("comment-----4");
                                        handler.sendMessage(message);
                                    }
                                }
                            }
                        });
                    }
                }, 1000);
            }
        });


        /*************评论内容发送***************/
        enter_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进行设置点击事件
                SendCommentPopup.instance.showPopup(CiviCommentListDetailActivity.this, new SendCommentPopup.BtnSendCommentListener() {
                    @Override
                    public void sendEditComment(EditText editText) {
                        //进行获取发送内容的编辑框:
                        final String sendContent = editText.getText().toString();
                        try {
                            User user = UserDao.getInstance().getLocalUser();
                            if (Integer.parseInt(user.getIdentityState()) <
                                    Integer.parseInt(UserPermisson.ORDINARYSTATE.getType())) {
                                UiUtils.getInstance().showToast("您的账号已被禁用!");
                            } else {
                                if (sendContent.trim().equals("")) {
                                    UiUtils.getInstance().showToast("请填写回复内容!");
                                } else {
                                    int size = "中".getBytes().length;
                                    if (sendContent.trim().getBytes().length <= HDCivilizationConstants.MIN_SEND_COMMENT_LENGTH * size) {
                                        //进行获取内容进行发送
                                        hud = KProgressHUD.create(CiviCommentListDetailActivity.this)
                                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                                .setLabel("回复发表中...")
                                                .setCancellable(false);
                                        hud.setCancellable(false);
                                        hud.show();
                                        System.out.println("sendContent = " + sendContent);
                                        RequestParams params = new RequestParams();
//                                        params.addQueryStringParameter("tranCode", "AROUND0029");
                                        params.addQueryStringParameter("tranCode", "AROUND0052");
                                        params.addQueryStringParameter("userId", userId);
                                        /******************************/
                                        String content = ToolUtils.getInstance().filterEmoji(sendContent);
                                        /******************************/
                                        params.addQueryStringParameter("content", content);
                                        params.addQueryStringParameter("desItemId", fatherItemId);
                                        params.addQueryStringParameter("itemType", HDCivilizationConstants.COMMENT_TYPE);
                                        if (nickName != null && !nickName.equals("")) {
                                            params.addQueryStringParameter("userName", nickName);
                                        } else {
                                            params.addQueryStringParameter("userName", countNumber);
                                        }
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
                                                    }

                                                    @Override
                                                    public void onLoading(long total, long current, boolean isUploading) {
                                                        super.onLoading(total, current, isUploading);
                                                    }

                                                    @Override
                                                    public void onSuccess(ResponseInfo<String> responseInfo) {
                                                        try {
                                                            Message messsage = Message.obtain();
                                                            contentSendProtocol.setFatherItemId(fatherItemId);
                                                            contentSendProtocol.setActionKeyName("回复失败!");
                                                            contentSendProtocol.setKeyName("回复");
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
                                                            //此时只能进行业务上判断
                                                            if (e.getErrorCode() == HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE) {
                                                                //权限过低判断:此时有"普通用户被禁用","用户的id前后不一致" //// TODO: 2016/9/19
                                                                Message messsage = Message.obtain();
                                                                messsage.what = contentSendFailure;//失败
                                                                messsage.obj = e.getErrorContent();
                                                                handler.sendMessage(messsage);
                                                            } else {
                                                                Message messsage = Message.obtain();
                                                                messsage.what = contentSendFailure;//失败
                                                                messsage.obj = e.getErrorContent();
                                                                handler.sendMessage(messsage);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(HttpException e, String s) {
                                                        Message messsage = Message.obtain();
                                                        messsage.what = contentSendFailure;//失败
                                                        messsage.obj = "回复失败!";
                                                        handler.sendMessage(messsage);
                                                    }
                                                });

                                    } else {
                                        UiUtils.getInstance().showToast("回复内容不能超过" + HDCivilizationConstants.MIN_SEND_COMMENT_LENGTH + "个字!");
                                    }
                                }
                            }

                        } catch (ContentException e) {
                            e.printStackTrace();
                            ToolUtils.getInstance().closeKeyBoard(editText);
                            OKPopup.getInstance().showPopup(CiviCommentListDetailActivity.this, new OKPopup.BtnClickListener() {
                                @Override
                                public void btnOk() {
                                    Intent intent = new Intent(CiviCommentListDetailActivity.this, LoginActivity.class);
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

    private void getFristData() {
        /***加载第一屏数据*****/
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                try {
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode", "AROUND0023");
                    System.err.println("itemIditemIditemId=" + fatherItemId);
                    paramsMap.put("itemId", fatherItemId);//条目id
                    paramsMap.put("userId", userId);
                    paramsMap.put("pagerNum", PAGE_SIZE + "");
                    paramsMap.put("currentPager", firstPage + "");
                    paramsMap.put("targetUserId", itemUserId);
                    paramsMap.put("infoReadStatus", isInfoReadStatus());
                    paramsMap.put("themeType", "comment");
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                    commentListProtocol.setFatherItemId(fatherItemId);
                    commentListProtocol.setActionKeyName(loadFirstPage);
                    message.obj = commentListProtocol.loadData(urlParamsEntity);
                    message.what = HDCivilizationConstants.REQUEST_FIRST_PAGE;
                    handler.sendMessage(message);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (ContentException e) {
                    e.printStackTrace();
                    if (e.getErrorCode() == HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE) {
                        //权限过低的时候,只能是前后台用户的id不一致,----- 先进行提示失败://// TODO: 2016/9/19
                        message.what = HDCivilizationConstants.ERROR_CODE;
                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT, loadFirstPage + "!");
                        message.setData(bundle);
                        handler.sendMessage(message);
                    } else {
                        message.what = HDCivilizationConstants.ERROR_CODE;
                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }
            }
        });


    }

    //进度条
    KProgressHUD hud;


    /***
     * RecyclerView的适配器
     */
    class CommentListDetailAdapter extends LoadMoreRecyclerView.Adapter<CommentListDetailAdapter.MineHolder> {

        private List<HDC_UserCommentList> datas;

        public List<HDC_UserCommentList> getDatas() {
            return datas;
        }

        public void setDatas(List<HDC_UserCommentList> datas) {
            this.datas = datas;
        }

        public CommentListDetailAdapter(List<HDC_UserCommentList> datas) {
            this.datas = datas;
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public MineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(UiUtils.getInstance().getContext(), R.layout.listview_item_civi_comment_list_detail, null);
            MineHolder holder = new MineHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MineHolder holder, int position) {
            if (datas.get(position).getUser().getPortraitUrl() != null && datas.get(position).getUser().getPortraitUrl().startsWith("http://")) {
                BitmapUtil.getInstance().displayUserPic(holder.userPic, datas.get(position).getUser().getPortraitUrl());
            } else {
                BitmapUtil.getInstance().displayUserPic(holder.userPic, UrlParamsEntity.WUCHEN_XU_IP_FILE + datas.get(position).getUser().getPortraitUrl());
            }

            String nickName = "";
            nickName = getNickName(datas.get(position).getUser());
            holder.userName.setText(nickName);
            /******************/
            String string =datas.get(position).getContent();
            ToolUtils.getInstance().setString(holder.content, string);
            /******************/

//            holder.content.setText(datas.get(position).getContent());
            System.out.println("holder.content:" + datas.get(position).getContent() + "...imgUrl:" + (UrlParamsEntity.WUCHEN_XU_IP_FILE + datas.get(position).getUser().getPortraitUrl()));
            holder.time.setText(DateUtil.getInstance().getDayOrMonthOrYear1(datas.get(position).getPublishTime()));
            if (datas.get(position).getUser().getIdentityState().equals(HDCivilizationConstants.FOUR)) {
                holder.userState.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
            } else if (!datas.get(position).getUser().getIdentityState().equals(HDCivilizationConstants.FOUR)) {
                holder.userState.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
            }
        }

        class MineHolder extends LoadMoreRecyclerView.ViewHolder {
            private ImageView userPic;
            private TextView userName;
            private TextView content;
            private TextView time, userState;

            public MineHolder(View itemView) {
                super(itemView);
                userPic = (ImageView) itemView.findViewById(R.id.item_user_pic);
                userName = (TextView) itemView.findViewById(R.id.item_user_name);
                content = (TextView) itemView.findViewById(R.id.item_comment_content);
                time = (TextView) itemView.findViewById(R.id.item_time__);
                userState = (TextView) itemView.findViewById(R.id.item_user_state);
            }
        }
    }

    /***
     * 数字更新网络请求
     **/
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

    @Override
    protected void onRestart() {
        super.onRestart();
        /*****获取用户对象******/
        try {
            user = UserDao.getInstance().getLocalUser();
            userId = user.getUserId();
            nickName = user.getNickName();
            countNumber = user.getAccountNumber();
        } catch (ContentException e) {
            e.printStackTrace();
            userId = "";
            nickName = "";
            countNumber = "";
        }
    }
}

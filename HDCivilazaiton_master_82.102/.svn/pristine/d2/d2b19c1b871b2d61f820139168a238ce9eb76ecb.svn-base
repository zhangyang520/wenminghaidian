package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tencent.connect.share.QQShare;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.dao.ClickLikesStateDao;
import com.zhjy.hdcivilization.dao.CommentDao;
import com.zhjy.hdcivilization.dao.MainNumberDao;
import com.zhjy.hdcivilization.dao.SystemSettingDao;
import com.zhjy.hdcivilization.dao.UserCommentListDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.ClickLikesState;
import com.zhjy.hdcivilization.entity.HDC_CommentDetail;
import com.zhjy.hdcivilization.entity.HDC_MainNumber;
import com.zhjy.hdcivilization.entity.HDC_UserCommentList;
import com.zhjy.hdcivilization.entity.SystemSetting;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.progressbar.KProgressHUD;
import com.zhjy.hdcivilization.protocol.ClickDetailCountProtocol;
import com.zhjy.hdcivilization.protocol.ClickLikesProtocol;
import com.zhjy.hdcivilization.protocol.ContentSendProtocol;
import com.zhjy.hdcivilization.protocol.MainNumberProtocol;
import com.zhjy.hdcivilization.protocol.OneCommentProtocol;
import com.zhjy.hdcivilization.utils.BitmapUtil;
import com.zhjy.hdcivilization.utils.DateUtil;
import com.zhjy.hdcivilization.utils.FileUtils;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.NetUtils;
import com.zhjy.hdcivilization.utils.SharedPreferencesManager;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.ToolUtils;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.LoadMoreRecyclerView;
import com.zhjy.hdcivilization.view.NoScrollView;
import com.zhjy.hdcivilization.view.OKPopup;
import com.zhjy.hdcivilization.view.SendCommentPopup;
import com.zhjy.hdcivilization.view.ShareCommentPopup;
import com.zhjy.hdcivilization.wxapi.WXEntryActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static android.view.GestureDetector.OnGestureListener;

/**
 * @author :huangxianfeng on 2016/7/29.
 *         文明评论详情界面管理类
 */
public class CommentDetailActivity extends BaseActivity {

    private TextView title;
    private TextView userType, userName;
    private TextView time, tv_comment_number;
    private TextView zan;
    private LinearLayout imgeList;
    private ImageView imge_2, imge_3, btnBack;
    private ImageView imge_1;
    private TextView content;
    private NoScrollView scrollView;
    private String keyTitle = "评论详情";
    private HDC_CommentDetail commentDetail;
    private TextView bottom_shared, bottom_logo_zan, bottom_comment;
    private RelativeLayout rl_bottom_shared, rl_bottom_logo_zan,rl_bottom_comment;
    private RelativeLayout commentList;
    private Button enter_comment;
    private RelativeLayout rl_layout, rl_back;
    private String itemUserId;
    private String userId;
    private User user;
    private String nickName;
    private String countNumber;
    private final int contentSendSuccess = 206;//发送评论的内容成功请求码
    private final int contentSendFailure = 207;//发送评论的内容失败请求码
    private String channelId;
    private RelativeLayout rl_img_list;
    private TextView tv_img_list_number;
    private DetailAdapter detailAdapter;
    private boolean isLoadMoring = false;

    private List<HDC_UserCommentList> list = new ArrayList<HDC_UserCommentList>();
    private OneCommentProtocol oneCommentProtocol = new OneCommentProtocol();


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (detailAdapter == null) {
                synchronized (CommentDetailActivity.this) {
                    if (detailAdapter == null) {
                        detailAdapter = new DetailAdapter();
                        recyclerView.setAdapter(detailAdapter);
                    }
                }
            }

            switch (msg.what) {
                case HDCivilizationConstants.MESSAGE_DIAN_ZAN:
                    //进行点赞
                    commentDetail.setCount(commentDetail.getCount() + 1);
                    List<HDC_CommentDetail> datas = CommentDao.getInstance().//
                            getHDC_CommentDetailList(commentDetail.getItemId());
//                    System.out.println("MESSAGE_DIAN_ZAN datas size:" + datas.size());
                    if (datas != null && datas.size() > 0) {
                        for (HDC_CommentDetail data : datas) {
                            data.setCount(commentDetail.getCount());
                        }
                    }

                    CommentDao.getInstance().saveAll(datas);
                    zan.setText(commentDetail.getCount() + "");
                    UiUtils.getInstance().showToast(msg.obj + "");

                    clickLikesState =//
                            ClickLikesStateDao.getInstance().getClickLikesState(//
                                    userId, commentDetail.getItemIdAndType());
                    if (clickLikesState != null) {
                        clickLikesState.setIsClickState(true);
                    } else {
                        clickLikesState = new ClickLikesState();
                        clickLikesState.setIsClickState(true);
                        clickLikesState.setItemIdAndType(commentDetail.getItemIdAndType());
                        clickLikesState.setUserId(userId);
                    }
                    ClickLikesStateDao.getInstance().saveClickLikesState(clickLikesState);
                    bottom_logo_zan.setBackgroundResource(R.drawable.bottm_logo_zan_press);
                    break;
                case HDCivilizationConstants.MESSAGE_DIAN_ZAN_NOT:
                    //不能重复点赞
                    UiUtils.getInstance().showToast(msg.obj + "");
                    clickLikesState =//
                            ClickLikesStateDao.getInstance().getClickLikesState(//
                                    userId, commentDetail.getItemIdAndType());
                    if (clickLikesState != null) {
                        clickLikesState.setIsClickState(true);
                    } else {
                        clickLikesState = new ClickLikesState();
                        clickLikesState.setIsClickState(true);
                        clickLikesState.setItemIdAndType(commentDetail.getItemIdAndType());
                        clickLikesState.setUserId(userId);
                    }
                    bottom_logo_zan.setBackgroundResource(R.drawable.bottm_logo_zan_press);
                    ClickLikesStateDao.getInstance().saveClickLikesState(clickLikesState);
                    break;

                case contentSendSuccess:
                    //将评论的数据放入到数据库中 暂时不放数据库
                    UserCommentListDao.getInstance().saveObj((HDC_UserCommentList) msg.obj);
                    SendCommentPopup.instance.dismissDialog();
                    UiUtils.getInstance().showToast("评论成功!");
                    if (hud != null) {
                        hud.dismiss();
                    }
                    List<HDC_UserCommentList> lists = UserCommentListDao.getInstance().getList(itemId, itemIdAndType);
                    detailAdapter.setList(lists);
                    recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();

                    if (userId.equals(itemUserId)) {
                        getNumberForServer();
//                        try {
                            //消息提醒数量 去除
//                            HDC_MainNumber mainNumber = MainNumberDao.getInstance().getNumberBy(userId);
//                            mainNumber.setCommentCount(mainNumber.getCommentCount() + 1);
//                            MainNumberDao.getInstance().saveNumber(mainNumber);
                            List<HDC_CommentDetail> HDC_CommentDetails = CommentDao.getInstance().getHDC_CommentDetailList(itemId);
                            for (HDC_CommentDetail data1 : HDC_CommentDetails) {
                                  //去除
//                                data1.setTypeCount(data1.getTypeCount() + 1);
                                data1.setCommentCount(data1.getCommentCount() + 1);
                            }
                            CommentDao.getInstance().saveAll(HDC_CommentDetails);
                                  //去除
//                            commentDetail.setTypeCount(commentDetail.getTypeCount() + 1);
                            initCommentDetailCount(commentDetail);
//                        } catch (ContentException e) {
//                            e.printStackTrace();
//                        }
                    } else {
                        //话题评论数量
                        List<HDC_CommentDetail> HDC_CommentDetails = CommentDao.getInstance().getHDC_CommentDetailList(itemId);
                        for (HDC_CommentDetail data1 : HDC_CommentDetails) {
                            data1.setCommentCount(data1.getCommentCount() + 1);
                        }
                        CommentDao.getInstance().saveAll(HDC_CommentDetails);
                    }


                    break;

                case contentSendFailure:
                    UiUtils.getInstance().showToast((String) msg.obj);
                    if (hud != null) {
                        hud.dismiss();
                    }
                    break;

                case HDCivilizationConstants.REQUEST_FIRST_PAGE:
                    list = (List<HDC_UserCommentList>) msg.obj;
                    if (list != null && list.size() <= 0) {
                        //清空数据 不能清空所有的数据
//                        UserCommentListDao.getInstance().clearData();
//                        detailAdapter.setList(list);
//                        recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        UiUtils.getInstance().showToast(HDCivilizationConstants.NO_SUB_COMMENT);
                        list.clear();
                    } else {
                        UserCommentListDao.getInstance().clearData();
                        UserCommentListDao.getInstance().saveAll(list);
                        List<HDC_UserCommentList> commentList = UserCommentListDao.getInstance().getList(itemId, itemIdAndType);
                        detailAdapter.setList(commentList);
                        recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                    }

                    //将对应的文明评论的消息提醒;进行去除
                    if(!userId.trim().equals("")){
                        //如果不为空字符串
                        List<HDC_CommentDetail> HDC_CommentDetails = CommentDao.getInstance().getHDC_CommentDetailList(itemId);
                        if(HDC_CommentDetails.size()>0){
                            for(HDC_CommentDetail data:HDC_CommentDetails){
                                data.setTypeCount(0);
                            }
                            CommentDao.getInstance().saveAll(HDC_CommentDetails);
                        }

                        try {
                            HDC_MainNumber mainNumber = MainNumberDao.getInstance().getNumberBy(userId);
                            mainNumber.setCommentCount(0);
                            MainNumberDao.getInstance().saveNumber(mainNumber);
                        } catch (ContentException e) {
                            e.printStackTrace();
                        }
                    }

                    break;

                case HDCivilizationConstants.LOAD_MORE:
                    if (((List<HDC_UserCommentList>) msg.obj).size() <= 0) {
                        UiUtils.getInstance().showToast("没有更多评论");
//                        recyclerView.notifyMoreFinish(true);
                        recyclerView.setLoadingMore(false);
                        recyclerView.setLoadMoreState(LoadMoreRecyclerView.LOAD_MORE_FINISHED);
                        scrollView.scrollBy(0, -recyclerView.getFooterViewHeight());
                    } else {
                        UserCommentListDao.getInstance().saveAll(((List<HDC_UserCommentList>) msg.obj));
                        int firstSize = list.size();
                        list.addAll((List<HDC_UserCommentList>) msg.obj);
                        detailAdapter.setList(list);
                        linearLayoutManager.scrollToPosition(firstSize - 1);
                        recyclerView.notifyMoreFinish(true);
                        recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        currentPage = currentPage + 1;
                    }
                    isLoadMoring = false;
                    break;
                case HDCivilizationConstants.ERROR_CODE:
                    //错误码
                    if (msg.getData().getInt(HDCivilizationConstants.ACTION_CODE) == HDCivilizationConstants.LOAD_MORE) {
                        isLoadMoring = false;
                        recyclerView.setLoadingMore(false);
                        recyclerView.setLoadMoreState(LoadMoreRecyclerView.LOAD_MORE_FINISHED);
                        scrollView.scrollBy(0, -recyclerView.getFooterViewHeight());
                        UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));

                    } else if (msg.getData().getInt(HDCivilizationConstants.ACTION_CODE) == HDCivilizationConstants.REQUEST_FIRST_PAGE) {
                        UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                    } else {
                        UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                    }

                    break;
            }
        }
    };
    public static String USER_ID_KEY = "USER_ID_KEY";//用户的userid的键
    public static String TOPIC_TYPE = "TOPIC_TYPE";//主题类型的键
    public static String ITEM_ID_AND_TYPE = "ITEM_ID_AND_TYPE";//条目的id和类型
    public static String ITEM_ID = "item_id";
    public static String ITEMUSER_ID = "item_user_id";//条目的用户id
    private LoadMoreRecyclerView recyclerView;
    private String itemId;
    private String itemIdAndType;
    int topicType;
    private LinearLayoutManager linearLayoutManager;
    private ClickLikesProtocol clickLikesProtocol = new ClickLikesProtocol();//点赞的协议类
    ClickLikesState clickLikesState;
    boolean isClickable;

    private void proessDataToView(HDC_CommentDetail cd) {
        title.setText(cd.getTitle());
        time.setText(cd.getPublishTime());
        zan.setText(cd.getCount() + "");
        //初始化数字
        initCommentDetailCount(cd);
        if (cd.getImgUrlList() != null) {
            int index = cd.getImgUrlList().size();
            if (index == 0) {
                rl_img_list.setVisibility(View.GONE);
                imgeList.setVisibility(View.GONE);
            } else {
                if (index == 1) {
                    rl_img_list.setVisibility(View.GONE);
                    imgeList.setVisibility(View.VISIBLE);
                    imge_1.setVisibility(View.VISIBLE);
                    BitmapUtil.getInstance().displayImg(imge_1, UrlParamsEntity.WUCHEN_XU_IP_FILE + cd.getImgUrlList().get(0).getImgUrl());
                } else {
                    imgeList.setVisibility(View.VISIBLE);
                    imge_1.setVisibility(View.VISIBLE);
                    rl_img_list.setVisibility(View.VISIBLE);
                    BitmapUtil.getInstance().displayImg(imge_1, UrlParamsEntity.WUCHEN_XU_IP_FILE + cd.getImgUrlList().get(0).getImgUrl());
                    tv_img_list_number.setText(index + "");
                }
            }
        } else {
            //如果为空:都为空
            imgeList.setVisibility(View.GONE);
        }


        /****手动改变话题详情的内容字体大小*/
        try {
            SystemSetting systemSetting = SystemSettingDao.getInstance().getSystemSetting(userId);
            if (systemSetting.getFontSize().equals(HDCivilizationConstants.LARGE)) {
                content.setTextSize(20);
            } else if (systemSetting.getFontSize().equals(HDCivilizationConstants.IN_LARGE)) {
                content.setTextSize(15);
            } else if (systemSetting.getFontSize().equals(HDCivilizationConstants.SMALL)) {
                content.setTextSize(10);
            } else {
                content.setTextSize(14);
            }
        } catch (ContentException e) {
            e.printStackTrace();
        }
        content.setText(cd.getContent());


        if (cd.getLaunchUser().getIdentityState().equals(UserPermisson.VOLUNTEER.getType())) {
            userType.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
        } else {
            userType.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
        }

        String nickName = "";
        if (cd.getLaunchUser().getNickName().matches("^[1][3,4,5,8,7][0-9]{9}$")) {
            nickName = cd.getLaunchUser().getNickName().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        } else {
            try {
                User user = UserDao.getInstance().getLocalUser();
                //进行判断对象是否和该条目的launcherUser一致
                if (user.getUserId().equals(cd.getLaunchUser().getUserId())) {
                    //如果id一致 昵称是否为空 :y 手机号
                    nickName = cd.getLaunchUser().getNickName().equals("") ?//
                            user.getAccountNumber() : cd.getLaunchUser().getNickName();
                    if (nickName.matches("^[1][3,4,5,8,7][0-9]{9}$")) {
                        nickName = nickName.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                    }
                } else {
                    nickName = cd.getLaunchUser().getNickName();
                }
            } catch (ContentException e) {
                e.printStackTrace();
                nickName = cd.getLaunchUser().getNickName();
            }
        }
        userName.setText(nickName);
        //进行初始化点赞状态
    }

    private void initCommentDetailCount(HDC_CommentDetail cd) {
        if (cd.getTypeCount() == 0) {
            tv_comment_number.setVisibility(View.INVISIBLE);
        } else {
            tv_comment_number.setVisibility(View.VISIBLE);
            tv_comment_number.setText(cd.getTypeCount() + "");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contentView = UiUtils.getInstance().inflate(R.layout.activity_comment_detail);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        channelId = (String) SharedPreferencesManager.get(UiUtils.getInstance().getContext(), HDCivilizationConstants.CHANNELID, "");
        try {
            user = UserDao.getInstance().getLocalUser();
            userId = user.getUserId();
            nickName = user.getNickName();
            countNumber = user.getAccountNumber();
//            System.out.println("initViews userId ="+userId+",nickName = "+nickName+",countNumber = "+countNumber);
        } catch (ContentException e) {
            e.printStackTrace();
            userId = "";
            nickName = "";
            countNumber = "";
        }

        scrollView = (NoScrollView) findViewById(R.id.comment_scrollview);
        title = (TextView) findViewById(R.id.tv_comment_title);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        bottom_shared = (TextView) findViewById(R.id.bottom_shared);
        bottom_comment = (TextView) findViewById(R.id.bottom_comment);
        bottom_logo_zan = (TextView) findViewById(R.id.bottom_logo_zan);
        userType = (TextView) findViewById(R.id.hot_user_id_type);
        userName = (TextView) findViewById(R.id.hot_user_name);
        tv_comment_number = (TextView) findViewById(R.id.tv_comment_number);
        time = (TextView) findViewById(R.id.comment_detail_time);
        zan = (TextView) findViewById(R.id.comment_detail_zan);
        imgeList = (LinearLayout) findViewById(R.id.ll_imge_list);
        imge_1 = (ImageView) findViewById(R.id.img_comment_detail_1);
        content = (TextView) findViewById(R.id.comment_detail_content);

        commentList = (RelativeLayout) findViewById(R.id.rl_bottom_comment);
        rl_bottom_logo_zan = (RelativeLayout) findViewById(R.id.rl_bottom_logo_zan);
        rl_bottom_logo_zan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bottom_logo_zan.setBackgroundResource(R.drawable.bottm_logo_zan_press);
                        break;

                    case MotionEvent.ACTION_UP:
                        bottom_logo_zan.setBackgroundResource(R.drawable.bottm_logo_zan);
                        break;
                }
                return false;
            }
        });
        rl_bottom_shared = (RelativeLayout) findViewById(R.id.rl_bottom_shared);
        rl_bottom_shared.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bottom_shared.setBackgroundResource(R.drawable.bottom_shared_press);
                        break;

                    case MotionEvent.ACTION_UP:
                        bottom_shared.setBackgroundResource(R.drawable.bottom_shared);
                        break;
                }
                return false;
            }
        });
        rl_bottom_comment = (RelativeLayout) findViewById(R.id.rl_bottom_comment);
        rl_bottom_comment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bottom_comment.setBackgroundResource(R.drawable.bottom_comment_press);
                        break;

                    case MotionEvent.ACTION_UP:
                        bottom_comment.setBackgroundResource(R.drawable.bottom_comment);
                        break;
                }
                return false;
            }
        });

        recyclerView = (LoadMoreRecyclerView) findViewById(R.id.comment_detail_recyclerview);
        recyclerView.setIsLanJie(true);
        linearLayoutManager = new LinearLayoutManager(UiUtils.getInstance().getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setPageSize(PAGE_SIZE);
        recyclerView.switchLayoutManager(new LinearLayoutManager(UiUtils.getInstance().getContext()));

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                initRecycleViewHeight = recyclerView.getMeasuredHeight();
            }
        });
        rl_img_list = (RelativeLayout) findViewById(R.id.rl_img_list);
        tv_img_list_number = (TextView) findViewById(R.id.tv_img_list_number);
        isLoadMoring = false;
        scrollView.setOnOverScolledListener(new NoScrollView.OnOverScolledListener() {
            @Override
            public void onOverl() {
                if (isLoadMoring) {
                    return;
                }

                recyclerView.setLoadingMore(true);
                recyclerView.loadMoreState = LoadMoreRecyclerView.LOAD_MORE_ING;
                recyclerView.mListener.onLoadMore();

                //TODO
            }
        });

        enter_comment = (Button) findViewById(R.id.enter_comment);
        //进行获取relativelayout
        rl_layout = (RelativeLayout) findViewById(R.id.rl_layout);

        //首先进行获取对应的参数
        itemId = getIntent().getStringExtra(ITEM_ID);//
        itemIdAndType = getIntent().getStringExtra(ITEM_ID_AND_TYPE);//
//        System.out.println("itemIdAndType=" + itemIdAndType);
        topicType = getIntent().getIntExtra(TOPIC_TYPE, -1);
        itemUserId = getIntent().getStringExtra(ITEMUSER_ID);//条目的用户Id
//        System.out.println("itemIdAndType:" + itemId + "...topicType:" + topicType);
        if (itemId != null && topicType != -1) {
            switch (topicType) {
                case HDCivilizationConstants.HOT_TOPIC_TYPE:
                    commentDetail = CommentDao.getInstance().getHotTopicsBy(itemId);
                    if (commentDetail == null) {
                        finish();
                    } else {
                        proessDataToView(commentDetail);
                    }
                    break;
                case HDCivilizationConstants.JOIN_TOPIC_TYPE:
                    commentDetail = CommentDao.getInstance().getJoinTopicsBy(itemId);
                    if (commentDetail == null) {
                        finish();
                    } else {
                        proessDataToView(commentDetail);
                    }
                    break;
                case HDCivilizationConstants.SUB_TOPIC_TYPE:
                    commentDetail = CommentDao.getInstance().getSubTopicsBy(itemId);
                    if (commentDetail == null) {
                        finish();
                    } else {
                        proessDataToView(commentDetail);
                    }
                    break;
            }
        }

        imge_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CommentDeatilActivity bei dian ji le ");
                Intent intent = new Intent(CommentDetailActivity.this, CommentImgListActivity.class);
                intent.putExtra(CommentImgListActivity.ITEM_ID, itemId);
                intent.putExtra(CommentImgListActivity.ITEM_ID_AND_TYPE, itemIdAndType);
                startActivity(intent);
            }
        });

        //进行初始化pressed
        initPressed();
    }

    private void initPressed() {
        //进行初始化点赞状态
        clickLikesState =//
                ClickLikesStateDao.getInstance().getClickLikesState(//
                        userId, commentDetail.getItemIdAndType());

        if (clickLikesState != null && clickLikesState.isClickState()) {
            isClickable = true;
            bottom_logo_zan.setPressed(true);
        } else {
            isClickable = false;
            bottom_logo_zan.setPressed(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void initInitevnts() {

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getCommentList();

        /*****评论的内容发送*****/
        enter_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进行设置点击事件
                SendCommentPopup.instance.showPopup(CommentDetailActivity.this, new SendCommentPopup.BtnSendCommentListener() {
                    @Override
                    public void sendEditComment(EditText editText) {
                        //进行获取发送内容的编辑框:
                        final String sendContent = editText.getText().toString();
                        try {
                            User user = UserDao.getInstance().getLocalUser();
                            if (Integer.parseInt(user.getIdentityState()) <
                                    Integer.parseInt(UserPermisson.ORDINARYSTATE.getType())) {
                                UiUtils.getInstance().showToast(HDCivilizationConstants.FORBIDDEN_USER);
                            } else {
                                if (sendContent.trim().equals("")) {
                                    UiUtils.getInstance().showToast(HDCivilizationConstants.PLEASE_WRITE_COMMENT);
                                } else {
                                    int size = "中".getBytes().length;
                                    if (sendContent.trim().getBytes().length <= HDCivilizationConstants.MIN_SEND_COMMENT_LENGTH * size) {
                                        //进行获取内容进行发送 訪問網絡！
                                        hud = KProgressHUD.create(CommentDetailActivity.this)
                                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                                .setLabel("评论发表中...")
                                                .setCancellable(false);
                                        hud.setCancellable(false);
                                        hud.show();
                                        RequestParams params = new RequestParams();
//                                        params.addQueryStringParameter("tranCode", "AROUND0029");
                                        params.addQueryStringParameter("tranCode", "AROUND0052");
                                        params.addQueryStringParameter("userId", userId);
                                        /******************************/
                                        String content = ToolUtils.getInstance().filterEmoji(sendContent);
                                        /******************************/
                                        params.addQueryStringParameter("content", content);
                                        params.addQueryStringParameter("desItemId", itemId);
                                        params.addQueryStringParameter("itemType", HDCivilizationConstants.THEME_TYPE);
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
                                                        System.out.println("HttpUrl..." + getRequestUrl());
                                                        System.out.println("CommentDetailActivity  SendCommentPopup url:" + getRequestUrl() + "....nickName:" + nickName + "...countNumber:" + countNumber);
                                                    }

                                                    @Override
                                                    public void onLoading(long total, long current, boolean isUploading) {
                                                        super.onLoading(total, current, isUploading);
                                                    }

                                                    @Override
                                                    public void onSuccess(ResponseInfo<String> responseInfo) {
                                                        try {
                                                            contentSendProtocol.setFatherItemId(itemId);
                                                            contentSendProtocol.setItemIdAndType(itemIdAndType);
                                                            Message messsage = Message.obtain();
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
                                        UiUtils.getInstance().showToast("评论内容不能超过" + HDCivilizationConstants.MIN_SEND_COMMENT_LENGTH + "个字");
                                    }
                                }
                            }
                        } catch (ContentException e) {
                            e.printStackTrace();
                            ToolUtils.getInstance().closeKeyBoard(editText);
                            OKPopup.getInstance().showPopup(CommentDetailActivity.this, new OKPopup.BtnClickListener() {
                                @Override
                                public void btnOk() {
                                    Intent intent = new Intent(CommentDetailActivity.this, LoginActivity.class);
                                    intent.putExtra(LoginActivity.ISFROM_OTHRES, true);
                                    startActivity(intent);
                                    OKPopup.getInstance().dismissDialog();
                                }
                            }, false, "未登录，跳转至登录界面?");
                        }
                    }
                });
            }
        });


        rl_bottom_shared.setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent();

            @Override
            public void onClick(View v) {
                ShareCommentPopup.instance.showPopup(rl_layout);
                ShareCommentPopup.instance.setShareOnClickListener(new ShareCommentPopup.ShareOnClickListener() {
                    @Override
                    public void shareOnClick(int viewId) {
                        //进行回调的点击事件
                        switch (viewId) {
                            case R.id.share_qq:
//                                QQShareUtils.shareWebUrl(CommentDetailActivity.this, getQQShareWebBundle());
                                break;

                            case R.id.share_weixin://微信好友
                                intent.setClass(CommentDetailActivity.this, WXEntryActivity.class);
                                intent.putExtra(HDCivilizationConstants.SHARE_TITLE, getLeftTitile(commentDetail.getTitle(), HDCivilizationConstants.SHARE_WECHAT_MAX_TITLE_LENGTH));
                                intent.putExtra(HDCivilizationConstants.SHARE_DESRIPTION, getLeftTitile(commentDetail.getContent(), HDCivilizationConstants.SHARE_WECHAT_MAX_CONTENT_LENGTH));
                                if (commentDetail.getImgUrlList() != null && commentDetail.getImgUrlList().size() > 0) {
                                    File file = BitmapUtil.getInstance().getBitmapFileCache(UrlParamsEntity.WUCHEN_XU_IP_FILE + commentDetail.getImgUrlList().get(0).getImgUrl());
                                    if (file != null) {
                                        intent.putExtra(HDCivilizationConstants.SHARE_IMG_PATH, file.getAbsolutePath());
                                    } else {
//                                        intent.putExtra(HDCivilizationConstants.SHARE_IMG_PATH, UrlParamsEntity.WUCHEN_XU_IP_FILE + commentDetail.getImgUrlList().get(0).getImgUrl());
                                        if (!FileUtils.getInstance().isExistsFile(MyApplication.shareLogoPath)) {
                                            try {
                                                FileUtils.getInstance().writeToDir(getAssets().open("ic_launcher_logo.png"), MyApplication.shareLogoPath);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        intent.putExtra(HDCivilizationConstants.SHARE_IMG_PATH, MyApplication.shareLogoPath);
                                    }
                                } else {
                                    intent.putExtra(HDCivilizationConstants.SHARE_IMG_PATH, "");
                                }
                                intent.putExtra(HDCivilizationConstants.SHARE_TARGET_URL, UrlParamsEntity.SHARE_THEME_WEB_URL + "id=" + commentDetail.getItemId());
                                intent.putExtra(HDCivilizationConstants.SHARE_SCENEFLAG, false);
                                intent.putExtra(HDCivilizationConstants.SHARE_TYPE, HDCivilizationConstants.SHARE_TYPE_WEBURL);
                                startActivity(intent);
                                break;
                            case R.id.share_Circle_friend://微信朋友圈
                                intent.setClass(CommentDetailActivity.this, WXEntryActivity.class);
                                intent.setClass(CommentDetailActivity.this, WXEntryActivity.class);
                                intent.putExtra(HDCivilizationConstants.SHARE_TITLE, getLeftTitile(commentDetail.getTitle(), HDCivilizationConstants.SHARE_WECHAT_MAX_TITLE_LENGTH));
                                intent.putExtra(HDCivilizationConstants.SHARE_DESRIPTION, getLeftTitile(commentDetail.getContent(), HDCivilizationConstants.SHARE_WECHAT_MAX_CONTENT_LENGTH));
                                if (commentDetail.getImgUrlList() != null && commentDetail.getImgUrlList().size() > 0) {
                                    File file = BitmapUtil.getInstance().getBitmapFileCache(UrlParamsEntity.WUCHEN_XU_IP_FILE + commentDetail.getImgUrlList().get(0).getImgThumbUrl());
                                    if (file != null) {
                                        intent.putExtra(HDCivilizationConstants.SHARE_IMG_PATH, file.getAbsolutePath());
                                    } else {
//                                        intent.putExtra(HDCivilizationConstants.SHARE_IMG_PATH, UrlParamsEntity.WUCHEN_XU_IP_FILE + commentDetail.getImgUrlList().get(0).getImgUrl());
                                        if (!FileUtils.getInstance().isExistsFile(MyApplication.shareLogoPath)) {
                                            try {
                                                FileUtils.getInstance().writeToDir(getAssets().open("ic_launcher_logo.png"), MyApplication.shareLogoPath);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        intent.putExtra(HDCivilizationConstants.SHARE_IMG_PATH, MyApplication.shareLogoPath);
                                    }
                                } else {
                                    intent.putExtra(HDCivilizationConstants.SHARE_IMG_PATH, "");
                                }
                                intent.putExtra(HDCivilizationConstants.SHARE_TARGET_URL, UrlParamsEntity.SHARE_THEME_WEB_URL + "id=" + commentDetail.getItemId());
                                intent.putExtra(HDCivilizationConstants.SHARE_SCENEFLAG, true);
                                intent.putExtra(HDCivilizationConstants.SHARE_TYPE, HDCivilizationConstants.SHARE_TYPE_WEBURL);
                                startActivity(intent);
                                break;
//                            case R.id.share_sign://微博
//                                intent.setClass(CommentDetailActivity.this, WBShareActivity.class);
//                                intent.putExtra(HDCivilizationConstants.SHARE_TITLE, getLeftTitile(commentDetail.getTitle(), HDCivilizationConstants.SHARE_WIEBO_MAX_TITLE_LENGTH));
//                                intent.putExtra(HDCivilizationConstants.SHARE_DESRIPTION, getLeftTitile(commentDetail.getContent(), HDCivilizationConstants.SHARE_WEIBO_MAX_CONTENT_LENGTH));
//                                if (commentDetail.getImgUrlList() != null && commentDetail.getImgUrlList().size() > 0) {
//                                    File file = BitmapUtil.getInstance().getBitmapFileCache(UrlParamsEntity.WUCHEN_XU_IP_FILE + commentDetail.getImgUrlList().get(0).getImgUrl());
//                                    if (file != null) {
//                                        intent.putExtra(HDCivilizationConstants.SHARE_IMG_PATH, file.getAbsolutePath());
//                                    } else {
//                                        if(!FileUtils.getInstance().isExistsFile(MyApplication.shareLogoPath)){
//                                            try {
//                                                FileUtils.getInstance().writeToDir(getAssets().open("ic_launcher_logo.png"), MyApplication.shareLogoPath);
//                                            } catch (IOException e){
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                        intent.putExtra(HDCivilizationConstants.SHARE_IMG_PATH, MyApplication.shareLogoPath);
//                                    }
//                                } else {
//                                    intent.putExtra(HDCivilizationConstants.SHARE_IMG_PATH, "");
//                                }
//                                intent.putExtra(HDCivilizationConstants.SHARE_TARGET_URL, UrlParamsEntity.SHARE_THEME_WEB_URL + "id=" + commentDetail.getItemId());
//                                intent.putExtra(HDCivilizationConstants.SHARE_TYPE, HDCivilizationConstants.SHARE_TYPE_WEBURL);
//                                startActivity(intent);
//                                break;
                        }
                    }
                });
            }
        });

        rl_bottom_logo_zan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进行点赞
                if ((clickLikesState == null || !clickLikesState.isClickState())) {
                    if (NetUtils.getInstance().checkNetwork(CommentDetailActivity.this)) {
                        isClickable = true;
                        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                Bundle bundle = new Bundle();
                                try {
                                    String userId = "";
                                    try {
                                        User user = UserDao.getInstance().getLocalUser();
                                        userId = user.getUserId();
                                    } catch (ContentException e) {
                                        e.printStackTrace();
                                        userId = "";
                                    }
                                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                                    LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                                    paramsMap.put("tranCode", "AROUND0024");
                                    paramsMap.put("itemId", commentDetail.getItemId());
                                    paramsMap.put("channelId", channelId);//百度云推送唯一标示 TODO
                                    paramsMap.put("userId", userId);
                                    paramsMap.put("topicType", HDCivilizationConstants.THEME_TYPE);//这个是主题类型
                                    urlParamsEntity.setParamsHashMap(paramsMap);
                                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                                    clickLikesProtocol.setActionKeyName("点赞失败");
                                    message.obj = clickLikesProtocol.loadData(urlParamsEntity);
                                    isClickable = true;
                                    message.what = HDCivilizationConstants.MESSAGE_DIAN_ZAN;//点赞的消息码
                                    handler.sendMessage(message);
                                } catch (JsonParseException e) {
                                    e.printStackTrace();
                                    isClickable = false;
                                    message.what = HDCivilizationConstants.ERROR_CODE;
                                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                                    message.setData(bundle);
                                    handler.sendMessage(message);
                                } catch (ContentException e) {
                                    e.printStackTrace();
                                    if (e.getErrorCode() == HDCivilizationConstants.MESSAGE_DIAN_ZAN_NOT) {
                                        message.what = HDCivilizationConstants.MESSAGE_DIAN_ZAN_NOT;
                                        message.obj = e.getErrorContent();
                                        isClickable = true;
                                    } else {
                                        isClickable = false;
                                        message.what = HDCivilizationConstants.ERROR_CODE;
                                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                                    }
                                    message.setData(bundle);
                                    handler.sendMessage(message);
                                }
                            }
                        });
                    } else {
                        isClickable = false;
                        UiUtils.getInstance().showToast("点赞失败,请检查网络!");
                    }
                } else {
                    isClickable = true;
                    UiUtils.getInstance().showToast(HDCivilizationConstants.ALREADY_DIAN_ZAN);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bottom_logo_zan.setBackgroundResource(R.drawable.bottm_logo_zan_press);
                        }
                    }, 200);
                }
            }
        });

        //评论内容
        rl_bottom_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommentDetailActivity.this, CiviCommentListActivity.class);
                intent.putExtra(CiviCommentListActivity.ITEMID, itemId);//itemId条目id
                intent.putExtra(CiviCommentListActivity.ITEMUSER_ID, itemUserId);//条目的用户id
                intent.putExtra(CiviCommentListActivity.ITEM_ID_AND_TYPE, itemIdAndType);//条目的id+type
                intent.putExtra(CiviCommentListActivity.THEMETYPE, HDCivilizationConstants.THEME_TYPE);//条目的id+type
                intent.putExtra(CiviCommentListActivity.SEND_CONTENT_TYPE, HDCivilizationConstants.THEME_TYPE);
                startActivity(intent);
            }
        });
        recyclerView.setAutoLoadMoreEnable(true);
        recyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (!isLoadMoring) {
                    isLoadMoring = true;
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                                @Override
                                public void run() {
                                    Message message = new Message();
                                    Bundle bundle = new Bundle();

                                    String userId = "";
                                    try {
                                        try {
                                            User user = UserDao.getInstance().getLocalUser();
                                            userId = user.getUserId();

                                        } catch (ContentException e) {
                                            e.printStackTrace();
                                            userId = "";
                                        }
                                        UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                                        LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                                        paramsMap.put("tranCode", "AROUND0050");
                                        paramsMap.put("itemId", itemId);
                                        paramsMap.put("userId", userId);
                                        paramsMap.put("pagerNum", PAGE_SIZE + "");
                                        paramsMap.put("currentPager", (currentPage + 1) + "");
                                        paramsMap.put("targetUserId", itemUserId);
                                        if (itemUserId.equals(userId)) {
                                            paramsMap.put("infoReadStatus", "1");
                                        } else {
                                            paramsMap.put("infoReadStatus", "0");
                                        }
                                        paramsMap.put("themeType", HDCivilizationConstants.THEME_TYPE);
                                        urlParamsEntity.setParamsHashMap(paramsMap);
                                        urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                                        oneCommentProtocol.setUserId(userId);
                                        oneCommentProtocol.setItemIdAndType(itemIdAndType);
                                        oneCommentProtocol.setFatherItemId(itemId);
                                        oneCommentProtocol.setActionKeyName("加载评论数据失败");
                                        message.obj = oneCommentProtocol.loadData(urlParamsEntity);
                                        message.what = HDCivilizationConstants.LOAD_MORE;
                                        handler.sendMessage(message);
                                    } catch (JsonParseException e) {
                                        e.printStackTrace();
                                        message.what = HDCivilizationConstants.ERROR_CODE;
                                        bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.LOAD_MORE);
                                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                                        message.setData(bundle);
                                        handler.sendMessage(message);
                                    } catch (ContentException e) {
                                        e.printStackTrace();
                                        message.what = HDCivilizationConstants.ERROR_CODE;
                                        bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.LOAD_MORE);
                                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                                        message.setData(bundle);
                                        handler.sendMessage(message);
                                    }
                                }
                            });
                        }
                    }, 500);
                }
            }
        });
    }


    /**
     * @param title
     * @param maxLength 不能超过对应的字节数
     * @return
     */
    private String getLeftTitile(String title, int maxLength) {
        if (title.length() > maxLength) {
            title = title.substring(0, maxLength);
        }
        return title;
    }

    /**
     * 进行获取qq分享网页类型的bundle
     *
     * @return
     */
    private Bundle getQQShareWebBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, getLeftTitile(commentDetail.getTitle(), HDCivilizationConstants.SHARE_QQ_MAX_TITLE_LENGTH));
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, UrlParamsEntity.SHARE_THEME_WEB_URL + "id=" + commentDetail.getItemId());
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, getLeftTitile(commentDetail.getContent(), HDCivilizationConstants.SHARE_QQ_MAX_CONTENT_LENGTH));
        if (commentDetail.getImgUrlList() != null && commentDetail.getImgUrlList().size() > 0) {
            File file = BitmapUtil.getInstance().getBitmapFileCache(UrlParamsEntity.WUCHEN_XU_IP_FILE + commentDetail.getImgUrlList().get(0).getImgThumbUrl());
            if (file == null) {
                bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, UrlParamsEntity.WUCHEN_XU_IP_FILE + commentDetail.getImgUrlList().get(0).getImgThumbUrl());
            } else {
                bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, file.getAbsolutePath());
            }
        } else {
            //如果图片为空
            //那么使用默认图片
            if (!FileUtils.getInstance().isExistsFile(MyApplication.shareLogoPath)) {
                try {
                    FileUtils.getInstance().writeToDir(getAssets().open("ic_launcher_logo.png"), MyApplication.shareLogoPath);
                    bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, MyApplication.shareLogoPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, MyApplication.shareLogoPath);
            }
        }
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, UiUtils.getInstance().getContext().getString(R.string.app_name));
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0x00);
        return bundle;
    }

    KProgressHUD hud;

    @Override
    protected void onRestart() {
        super.onRestart();
        channelId = (String) SharedPreferencesManager.get(UiUtils.getInstance().getContext(), HDCivilizationConstants.CHANNELID, "");
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
        //进行初始化按钮
        initPressed();
        //进行重新设置消息提醒个数:
        if (itemId != null && topicType != -1) {
            switch (topicType) {
                case HDCivilizationConstants.HOT_TOPIC_TYPE:
                    commentDetail = CommentDao.getInstance().getHotTopicsBy(itemId);
                    if (commentDetail == null) {
                        finish();
                    } else {
                        initCommentDetailCount(commentDetail);
                    }
                    break;
                case HDCivilizationConstants.JOIN_TOPIC_TYPE:
                    commentDetail = CommentDao.getInstance().getJoinTopicsBy(itemId);
                    if (commentDetail == null) {
                        finish();
                    } else {
                        initCommentDetailCount(commentDetail);
                    }
                    break;
                case HDCivilizationConstants.SUB_TOPIC_TYPE:
                    commentDetail = CommentDao.getInstance().getSubTopicsBy(itemId);
                    if (commentDetail == null) {
                        finish();
                    } else {
                        initCommentDetailCount(commentDetail);
                    }
                    break;
            }
        }


        /**在退回到本界面的时候，重新显示最新数据（二级评论数据的显示）**/
        List<HDC_UserCommentList> data = UserCommentListDao.getInstance().getList(itemId, itemIdAndType);
        if (data != null && data.size() > 0 && detailAdapter!=null) {
            detailAdapter.setList(data);
            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
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
    protected void onResume() {
        super.onResume();
        //进行执行网络交互
        if (NetUtils.getInstance().checkNetwork(this)) {
            //如果有网
            ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        User user = null;
                        try {
                            user = UserDao.getInstance().getLocalUser();
                            userId = user.getUserId();
                        } catch (ContentException e) {
                            e.printStackTrace();
                            userId = "";
                        }
                        UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                        LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                        paramsMap.put("tranCode", "AROUND0032");
                        paramsMap.put("userId", userId);//userId
                        paramsMap.put("type", HDCivilizationConstants.THEME_TYPE);//userId
                        paramsMap.put("itemId", commentDetail.getItemId());//userId
                        urlParamsEntity.setParamsHashMap(paramsMap);
                        ClickDetailCountProtocol clickDetailCountProtocol = new ClickDetailCountProtocol();
                        urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                        clickDetailCountProtocol.setUserId(userId);
                        clickDetailCountProtocol.setUser(user);
                        clickDetailCountProtocol.setActionKeyName("同步阅读量失败!");
                        clickDetailCountProtocol.loadData(urlParamsEntity);
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    } catch (ContentException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private final int PAGE_SIZE = 12;//固定个数
    private int firstPage = 1;
    private int currentPage = firstPage;

    private int initRecycleViewHeight;


    public int getInitRecycleViewHeight() {
        return initRecycleViewHeight;
    }

    public void setInitRecycleViewHeight(int initRecycleViewHeight) {
        this.initRecycleViewHeight = initRecycleViewHeight;
    }


    private void getCommentList() {
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                String userId = "";
                try {
                    try {
                        User user = UserDao.getInstance().getLocalUser();
                        userId = user.getUserId();
                    } catch (ContentException e) {
                        e.printStackTrace();
                        userId = "";
                    }
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode", "AROUND0050");
                    paramsMap.put("itemId", itemId);
                    paramsMap.put("userId", userId);
                    paramsMap.put("pagerNum", PAGE_SIZE + "");
                    paramsMap.put("currentPager", (firstPage) + "");
                    paramsMap.put("targetUserId", itemUserId);
                    if (itemUserId.equals(userId)) {
                        paramsMap.put("infoReadStatus", "1");
                    } else {
                        paramsMap.put("infoReadStatus", "0");
                    }
                    paramsMap.put("themeType", HDCivilizationConstants.THEME_TYPE);
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                    oneCommentProtocol.setUserId(userId);
                    oneCommentProtocol.setItemIdAndType(itemIdAndType);
                    oneCommentProtocol.setFatherItemId(itemId);
                    oneCommentProtocol.setActionKeyName("加载评论数据失败");
                    message.obj = oneCommentProtocol.loadData(urlParamsEntity);
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
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REQUEST_FIRST_PAGE);
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        });
    }



    class DetailAdapter extends LoadMoreRecyclerView.Adapter<DetailAdapter.DetailHolder> {
        private int ITEM_TYPE_ERROR_CODE = 101;//失败条目类型
        private int ITEM_TYPE_EMPTY_CODE = 102;//为空的条目类型

        private List<HDC_UserCommentList> list;

        public List<HDC_UserCommentList> getList() {
            return list;
        }

        public void setList(List<HDC_UserCommentList> list) {
            this.list = list;
        }

        @Override
        public DetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == ITEM_TYPE_ERROR_CODE) {
                //如果是错误类型
                return new DetailHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.loadpager_error, null), ITEM_TYPE_ERROR_CODE);
            } else if (viewType == ITEM_TYPE_EMPTY_CODE) {
                //如果是数据为空类型
                return new DetailHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.loadpage_empty, null), ITEM_TYPE_EMPTY_CODE);
            }
            return new DetailHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.listview_item_civi_comment_list_boomto, null));
        }

        @Override
        public void onBindViewHolder(DetailHolder holder, int position) {
            if (getItemViewType(position) == ITEM_TYPE_ERROR_CODE) {
                visiableError(holder);

            } else if (getItemViewType(position) == ITEM_TYPE_EMPTY_CODE) {
                //数据为空时!
            } else {
                HDC_UserCommentList hdc_userCommentList = list.get(position);
                if(hdc_userCommentList.getUser().getPortraitUrl()!=null &&  hdc_userCommentList.getUser().getPortraitUrl().startsWith("http://")){
                    BitmapUtil.getInstance().displayUserPic(holder.user_pic, hdc_userCommentList.getUser().getPortraitUrl());
                }else{
                    BitmapUtil.getInstance().displayUserPic(holder.user_pic, UrlParamsEntity.WUCHEN_XU_IP_FILE + hdc_userCommentList.getUser().getPortraitUrl());
                }

                if (Integer.parseInt(hdc_userCommentList.getUser().getIdentityState()) == 4) {
                    holder.userPermision.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
                } else if (Integer.parseInt(hdc_userCommentList.getUser().getIdentityState()) < 4) {
                    holder.userPermision.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
                }

                String nickName = "";
                if (hdc_userCommentList.getUser().getNickName().matches("^[1][3,4,5,8,7][0-9]{9}$")) {
                    nickName = hdc_userCommentList.getUser().getNickName().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                } else {
                    nickName = hdc_userCommentList.getUser().getNickName();
                }
                holder.userName.setText(nickName);
                holder.detailDate.setText(DateUtil.getInstance().getDayOrMonthOrYear1(hdc_userCommentList.getPublishTime()));
                /******************/
                String string0 =hdc_userCommentList.getContent();
                ToolUtils.getInstance().setString(holder.commentContent, string0);
                /******************/
//                holder.commentContent.setText(hdc_userCommentList.getContent());

                List<HDC_UserCommentList> data = UserCommentListDao.getInstance().getLists(hdc_userCommentList.getItemId());
                if (data != null && data.size() > 0) {
                    holder.totleLay.setVisibility(View.VISIBLE);
                    if (data.size() >= 3) {
                        if (data.size() == 3) {
                            holder.comment_list_one.setVisibility(View.VISIBLE);
                            holder.comment_list_two.setVisibility(View.VISIBLE);
                            holder.comment_list_three.setVisibility(View.VISIBLE);
                            holder.totalCount.setVisibility(View.GONE);
                            if (hdc_userCommentList.getCount()>3){
                                holder.totalCount.setVisibility(View.VISIBLE);
                                holder.totalCount.setText("查看全部" + hdc_userCommentList.getCount() + "条回复");
                            }
                            System.out.println("CommentDetailActivity data.size()1 = "+ data.size());
                            setTextToData(holder, data);
                        } else if (data.size() > 3) {
                            holder.comment_list_one.setVisibility(View.VISIBLE);
                            holder.comment_list_two.setVisibility(View.VISIBLE);
                            holder.comment_list_three.setVisibility(View.VISIBLE);
                            holder.totalCount.setVisibility(View.VISIBLE);
                            holder.totalCount.setText("查看全部" + hdc_userCommentList.getCount() + "条回复");

                            setTextToData(holder, data);
                        }
                    } else {
                        if (data.size() == 1) {
                            holder.comment_list_one.setVisibility(View.VISIBLE);
                            holder.comment_list_two.setVisibility(View.GONE);
                            holder.comment_list_three.setVisibility(View.GONE);
                            holder.totalCount.setVisibility(View.GONE);
                            HDC_UserCommentList commentList = data.get(0);
                            /*****根据返回来的UerPermission来判断志愿者身份还是普通用户的身份*****/
                            if (commentList.getUser().getIdentityState().equals(HDCivilizationConstants.FOUR)) {
                                holder.userPermiessionOne.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
                            } else if (commentList.getUser().getIdentityState().equals(HDCivilizationConstants.THREE) ||
                                    commentList.getUser().getIdentityState().equals(HDCivilizationConstants.TWO) ||
                                    commentList.getUser().getIdentityState().equals(HDCivilizationConstants.ONE) ||
                                    commentList.getUser().getIdentityState().equals(HDCivilizationConstants.ZARRO)) {
                                holder.userPermiessionOne.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
                            }

                            String nickNameOne = "";
                            if (commentList.getUser().getNickName().matches("^[1][3,4,5,8,7][0-9]{9}$")) {
                                nickNameOne = commentList.getUser().getNickName().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                            } else {
                                nickNameOne = commentList.getUser().getNickName();
                            }

                            holder.userPermiessionOneName.setText(nickNameOne);
                            /******************/
                            String string1 =commentList.getContent();
                            ToolUtils.getInstance().setString(holder.userPermiessionOneContent, string1);
                            /******************/
//                            holder.userPermiessionOneContent.setText(commentList.getContent());
                        } else if (data.size() == 2) {

                            holder.comment_list_one.setVisibility(View.VISIBLE);
                            holder.comment_list_two.setVisibility(View.VISIBLE);
                            holder.comment_list_three.setVisibility(View.GONE);
                            holder.totalCount.setVisibility(View.GONE);
                            /******************************/
                            if (data.get(0).getUser().getIdentityState().equals(HDCivilizationConstants.FOUR)) {
                                holder.userPermiessionOne.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
                            } else if (data.get(0).getUser().getIdentityState().equals(HDCivilizationConstants.THREE) ||
                                    data.get(0).getUser().getIdentityState().equals(HDCivilizationConstants.TWO) ||
                                    data.get(0).getUser().getIdentityState().equals(HDCivilizationConstants.ONE) ||
                                    data.get(0).getUser().getIdentityState().equals(HDCivilizationConstants.ZARRO)) {
                                holder.userPermiessionOne.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
                            }
                            /****设置手机号码中间有星号的展示**/
                            String nickNameOne = "";
                            if (data.get(0).getUser().getNickName().matches("^[1][3,4,5,8,7][0-9]{9}$")) {
                                nickNameOne = data.get(0).getUser().getNickName().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                            } else {
                                nickNameOne = data.get(0).getUser().getNickName();
                            }
                            holder.userPermiessionOneName.setText(nickNameOne);
                            /******************/
                            String string1 =data.get(0).getContent();
                            ToolUtils.getInstance().setString(holder.userPermiessionOneContent, string1);
                            /******************/
//                            holder.userPermiessionOneContent.setText(data.get(0).getContent());

                            /******************************/
                            if (data.get(1).getUser().getIdentityState().equals(HDCivilizationConstants.FOUR)) {
                                holder.userPermiessionTwo.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
                            } else if (data.get(1).getUser().getIdentityState().equals(HDCivilizationConstants.THREE) ||
                                    data.get(1).getUser().getIdentityState().equals(HDCivilizationConstants.TWO) ||
                                    data.get(1).getUser().getIdentityState().equals(HDCivilizationConstants.ONE) ||
                                    data.get(1).getUser().getIdentityState().equals(HDCivilizationConstants.ZARRO)) {
                                holder.userPermiessionTwo.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
                            }
                            /****设置手机号码中间有星号的展示**/
                            String nickNameTwo = "";
                            if (data.get(1).getUser().getNickName().matches("^[1][3,4,5,8,7][0-9]{9}$")) {
                                nickNameTwo = data.get(1).getUser().getNickName().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                            } else {
                                nickNameTwo = data.get(1).getUser().getNickName();
                            }
                            holder.userPermiessionTwoName.setText(nickNameTwo);
                            /******************/
                            String string2 =data.get(1).getContent();
                            ToolUtils.getInstance().setString(holder.userPermiessionTwoContent, string2);
                            /******************/
//                            holder.userPermiessionTwoContent.setText(data.get(1).getContent());
                        }
                    }
                } else {
                    holder.totleLay.setVisibility(View.GONE);
                }
            }
        }

        private void setTextToData(final DetailHolder holder, List<HDC_UserCommentList> data) {
            final HDC_UserCommentList commentList = data.get(0);
            /*********查看全部评论*************/
//            holder.totleLay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    holder.totleLay.getParent().requestDisallowInterceptTouchEvent(true);
//                    Intent intent = new Intent(CommentDetailActivity.this, CiviCommentListDetailActivity.class);
//                    intent.putExtra(CiviCommentListDetailActivity.ITEMIDTHEME, itemId);
//                    intent.putExtra(CiviCommentListDetailActivity.ITEMID, commentList.getFatherItemId());//itemId条目id
//                    intent.putExtra(CiviCommentListDetailActivity.ITEMUSER_ID, commentList.getUser().getUserId());//条目的用户id
//                    startActivity(intent);
//                }
//            });

            final GestureDetectorCompat gestureDetector = new GestureDetectorCompat(CommentDetailActivity.this, new OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {

                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {
                    System.out.println("GestureDetectorCompat onShowPress....");
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    System.out.println("GestureDetectorCompat onDown....");
                    holder.totleLay.getParent().requestDisallowInterceptTouchEvent(true);
                    Intent intent = new Intent(CommentDetailActivity.this, CiviCommentListDetailActivity.class);
                    intent.putExtra(CiviCommentListDetailActivity.ITEMIDTHEME, itemId);
                    intent.putExtra(CiviCommentListDetailActivity.ITEMID, commentList.getFatherItemId());//itemId条目id
                    intent.putExtra(CiviCommentListDetailActivity.ITEMUSER_ID, commentList.getUser().getUserId());//条目的用户id
                    startActivity(intent);
                    System.out.println("GestureDetectorCompat onSingleTapUp....");
                    return false;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    System.out.println("GestureDetectorCompat onLongPress....");
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                    System.out.println("GestureDetectorCompat onFling....velocityX:"+velocityX+"...velocityY:"+velocityY+"....velocityTracker x:"+velocityTracker.getXVelocity()+"...y:"+velocityTracker.getYVelocity());
//                    ValueAnimator valueAnimator=ValueAnimator.ofFloat(velocityY,0);
//                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                        float startValue=0;
//                        @Override
//                        public void onAnimationUpdate(ValueAnimator animation) {
//                            //对headView的重新设定
////                            int dy=Math.round((float) animation.getAnimatedValue() - startValue);
////                            scrollView.scrollBy(0,-dy);
////                            startValue=(float)animation.getAnimatedValue();
//                            scrollView.scrollBy(0,-Math.round((float) animation.getAnimatedValue()/10));
//                        }
//                    });
//                    valueAnimator.setDuration(1000);
//                    valueAnimator.start();
                    return false;
                }
            });

            gestureDetector.setIsLongpressEnabled(false);
            holder.totleLay.setOnTouchListener(new View.OnTouchListener() {
                float lastX,lastY;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    holder.totleLay.getParent().requestDisallowInterceptTouchEvent(true);
                    boolean flag=gestureDetector.onTouchEvent(event);

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastX=event.getRawX();
                            lastY=event.getRawY();
                            break;

                        case MotionEvent.ACTION_MOVE:
                            scrollView.scrollBy(0,-Math.round(event.getRawY()-lastY));
                            lastX=event.getRawX();
                            lastY=event.getRawY();
                            break;

                        case MotionEvent.ACTION_UP:
                            break;
                    }
                    return true;
                }
            });

            /*********************/
            if (commentList.getUser().getIdentityState().equals(HDCivilizationConstants.FOUR)) {
                holder.userPermiessionOne.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
            } else if (commentList.getUser().getIdentityState().equals(HDCivilizationConstants.THREE) ||
                    commentList.getUser().getIdentityState().equals(HDCivilizationConstants.TWO) ||
                    commentList.getUser().getIdentityState().equals(HDCivilizationConstants.ONE) ||
                    commentList.getUser().getIdentityState().equals(HDCivilizationConstants.ZARRO)
                    ) {
                holder.userPermiessionOne.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
            }
            /****设置手机号码中间有星号的展示**/
            String nickNameOne = "";
            if (commentList.getUser().getNickName().matches("^[1][3,4,5,8,7][0-9]{9}$")) {
                nickNameOne = commentList.getUser().getNickName().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            } else {
                nickNameOne = commentList.getUser().getNickName();
            }

            holder.userPermiessionOneName.setText(nickNameOne);
            /******************/
            String string =commentList.getContent();
            ToolUtils.getInstance().setString(holder.userPermiessionOneContent, string);
            /******************/
//            holder.userPermiessionOneContent.setText(commentList.getContent());

            /*********************/
            HDC_UserCommentList commentList1 = data.get(1);
            if (commentList1.getUser().getIdentityState().equals(HDCivilizationConstants.FOUR)) {
                holder.userPermiessionTwo.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
            } else if (commentList1.getUser().getIdentityState().equals(HDCivilizationConstants.THREE) ||
                    commentList1.getUser().getIdentityState().equals(HDCivilizationConstants.TWO) ||
                    commentList1.getUser().getIdentityState().equals(HDCivilizationConstants.ONE) ||
                    commentList1.getUser().getIdentityState().equals(HDCivilizationConstants.ZARRO)) {
                holder.userPermiessionTwo.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
            }
            /****设置手机号码中间有星号的展示**/
            String nickNameTwo = "";
            if (commentList1.getUser().getNickName().matches("^[1][3,4,5,8,7][0-9]{9}$")) {
                nickNameTwo = commentList1.getUser().getNickName().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            } else {
                nickNameTwo = commentList1.getUser().getNickName();
            }
            holder.userPermiessionTwoName.setText(nickNameTwo);
            /******************/
            String string1 =commentList1.getContent();
            ToolUtils.getInstance().setString(holder.userPermiessionTwoContent, string1);
            /******************/
//            holder.userPermiessionTwoContent.setText(commentList1.getContent());

            /*********************/
            HDC_UserCommentList commentList2 = data.get(2);
            if (commentList2.getUser().getIdentityState().equals(HDCivilizationConstants.FOUR)) {
                holder.userPermiessionThree.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
            } else if (commentList2.getUser().getIdentityState().equals(HDCivilizationConstants.THREE) ||
                    commentList2.getUser().getIdentityState().equals(HDCivilizationConstants.TWO) ||
                    commentList2.getUser().getIdentityState().equals(HDCivilizationConstants.ONE) ||
                    commentList2.getUser().getIdentityState().equals(HDCivilizationConstants.ZARRO)) {
                holder.userPermiessionThree.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
            }
            /****设置手机号码中间有星号的展示**/
            String nickNameThree = "";
            if (commentList2.getUser().getNickName().matches("^[1][3,4,5,8,7][0-9]{9}$")) {
                nickNameThree = commentList2.getUser().getNickName().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            } else {
                nickNameThree = commentList2.getUser().getNickName();
            }
            holder.userPermiessionThreeName.setText(nickNameThree);
            /******************/
            String string2 =commentList2.getContent();
            ToolUtils.getInstance().setString(holder.userPermiessionThreeContent, string2);
            /******************/
//            holder.userPermiessionThreeContent.setText(commentList2.getContent());
        }


        /**
         * 进行显示错误页面
         * //错误时候的view
         * Button button;//按钮
         * ProgressBar pb_load;
         * ImageView page_iv;
         * loading_txt
         *
         * @param mineHodler
         */
        private void visiableError(DetailHolder mineHodler) {
            mineHodler.button.setVisibility(View.VISIBLE);
            mineHodler.page_iv.setVisibility(View.VISIBLE);
            mineHodler.pb_load.setVisibility(View.GONE);
            mineHodler.loading_txt.setVisibility(View.GONE);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 && list == null) {
                return ITEM_TYPE_ERROR_CODE;
            } else if (position == 0 && list.size() == 0) {
                return ITEM_TYPE_EMPTY_CODE;
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            if (list == null || list.size() == 0) {
                return 1;
            }
            return list.size();
        }


        class DetailHolder extends LoadMoreRecyclerView.ViewHolder {

            private TextView userPermision, userName, detailDate, commentContent, userPermiession, totalCount;
            private int viewType;//条目的类型

            private TextView userPermiessionOne, userPermiessionOneName, userPermiessionOneContent;
            private TextView userPermiessionTwo, userPermiessionTwoName, userPermiessionTwoContent;
            private TextView userPermiessionThree, userPermiessionThreeName, userPermiessionThreeContent;
            private ImageView user_pic;
            private LinearLayout comment_list_one;
            private LinearLayout comment_list_two;
            private LinearLayout comment_list_three, totleLay;


            private int ITEM_TYPE_ERROR_CODE = 101;//失败条目类型
            private int ITEM_TYPE_EMPTY_CODE = 102;//为空的条目类型

            //为空时候的view
            TextView tv_empty, loading_txt;//按钮
            RelativeLayout loadpage_empty;
            LinearLayout loadpage_error;
            //错误时候的view
            Button button;//按钮
            ProgressBar pb_load;
            ImageView page_iv;


            public DetailHolder(View itemView, int viewType) {
                super(itemView);
                this.viewType = viewType;
                initView(itemView);
            }

            public DetailHolder(View itemView) {
                super(itemView);
                initView(itemView);
            }

            private void initView(View itemView) {
                if (viewType == ITEM_TYPE_ERROR_CODE) {
                    //错误的处理
                    loadpage_error = (LinearLayout) itemView.findViewById(R.id.loadpage_error);
                    loading_txt = (TextView) itemView.findViewById(R.id.loading_txt);
                    page_iv = (ImageView) itemView.findViewById(R.id.page_iv);
                    pb_load = (ProgressBar) itemView.findViewById(R.id.pb_load);
                    loadpage_error.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, initRecycleViewHeight));
                    loadpage_error.requestLayout();
                    button = (Button) itemView.findViewById(R.id.page_bt);
                    //访问网络失败时
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //进行请求网络
                            visiableLoading();
                        }

                        private void visiableLoading() {
                            loading_txt.setVisibility(View.VISIBLE);
                            pb_load.setVisibility(View.VISIBLE);
                            page_iv.setVisibility(View.GONE);
                            button.setVisibility(View.GONE);
                        }
                    });
                } else if (viewType == ITEM_TYPE_EMPTY_CODE) {
                    //为空的处理
                    loadpage_empty = (RelativeLayout) itemView.findViewById(R.id.loadpage_empty);
                    System.out.println("loadpage_empty is null:" + (loadpage_empty == null));
                    loadpage_empty.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, initRecycleViewHeight));
//                loadpage_empty.getLayoutParams().height=initRecycleViewHeight;
                    loadpage_empty.requestLayout();
                    tv_empty = (TextView) itemView.findViewById(R.id.tv_empty);
                    tv_empty.setText("评论" + HDCivilizationConstants.EMPTY_STRING);
                } else {
                    user_pic = (ImageView) itemView.findViewById(R.id.user_pic);
                    totleLay = (LinearLayout) itemView.findViewById(R.id.totle_lay);
                    userPermision = (TextView) itemView.findViewById(R.id.user_userPermision);
                    userName = (TextView) itemView.findViewById(R.id.user_name);
                    detailDate = (TextView) itemView.findViewById(R.id.comment_detail_date);
                    commentContent = (TextView) itemView.findViewById(R.id.comment_content);


                    comment_list_one = (LinearLayout) itemView.findViewById(R.id.comment_list_one);
                    userPermiessionOne = (TextView) itemView.findViewById(R.id.userPermiession_one);
                    userPermiessionOneName = (TextView) itemView.findViewById(R.id.userPermiession_one_name);
                    userPermiessionOneContent = (TextView) itemView.findViewById(R.id.userPermiession_one_content);

                    comment_list_two = (LinearLayout) itemView.findViewById(R.id.comment_list_two);
                    userPermiessionTwo = (TextView) itemView.findViewById(R.id.userPermiession_two);
                    userPermiessionTwoName = (TextView) itemView.findViewById(R.id.userPermiession_two_name);
                    userPermiessionTwoContent = (TextView) itemView.findViewById(R.id.userPermiession_two_conetent);

                    comment_list_three = (LinearLayout) itemView.findViewById(R.id.comment_list_thress);
                    userPermiessionThree = (TextView) itemView.findViewById(R.id.userPermiession_three);
                    userPermiessionThreeName = (TextView) itemView.findViewById(R.id.userPermiession_three_name);
                    userPermiessionThreeContent = (TextView) itemView.findViewById(R.id.userPermiession_three_content);
                    totalCount = (TextView) itemView.findViewById(R.id.totalCount);
                }
            }
        }
    }

}

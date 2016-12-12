package com.zhjy.hdcivilization.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.activity.CommentDetailActivity;
import com.zhjy.hdcivilization.activity.CommentUserInfoActivity;
import com.zhjy.hdcivilization.dao.CommentDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_CommentDetail;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.protocol.CommentUserJoinProtocol;
import com.zhjy.hdcivilization.utils.BitmapUtil;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.LoadMoreRecyclerView;
import com.zhjy.hdcivilization.view.MaskView;
import com.zhjy.hdcivilization.view.SimpleSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/7/30.
 *         文明评论用户的个人信息-参与的话题
 */
public class CommentJoinFragment extends Fragment {

    private CommentUserInfoActivity activity;
    private TextView joinText;
    private String gambitCount;
    private View contentView;
    private LoadMoreRecyclerView recyclerView;
    private String keyName ="参与话题";
    private String refershName = "刷新参与话题信息失败";
    private String loadmoreName = "加载更多参与话题信息失败";
    private String loadFirstPage = "加载参与话题信息失败";
    private UserInfoAdapter adapter;
    private SimpleSwipeRefreshLayout mSwipeRefreshLayout;
    private CommentUserJoinProtocol joinProtocol = new CommentUserJoinProtocol();
    private List<HDC_CommentDetail> commentDetailList = new ArrayList<HDC_CommentDetail>();
    private LinearLayoutManager linearLayoutManager;
    private final int PAGE_SIZE=12;//固定个数
    private  int firstPage=1;//首页index
    private  int currentPage=firstPage;//当前页Index
    private String  targetUserId;//目标用户的id

    private int initRecycleViewHeight;
    private int leftWidth;//三张图片剩余的宽度

    public int getInitRecycleViewHeight() {
        return initRecycleViewHeight;
    }

    public void setInitRecycleViewHeight(int initRecycleViewHeight) {
        this.initRecycleViewHeight = initRecycleViewHeight;
    }

    public void setJoinText(TextView joinText) {
        this.joinText = joinText;
    }

    public void setActivity(CommentUserInfoActivity activity) {
        this.activity = activity;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(adapter==null) {
                synchronized (CommentJoinFragment.this) {
                    adapter = new UserInfoAdapter();
                    recyclerView.setAdapter(adapter);
                }
            }
            switch (msg.what) {
                case HDCivilizationConstants.REQUEST_FIRST_PAGE:
                    CommentDao.getInstance().clearJoinTopic(targetUserId);
                    if (((List<HDC_CommentDetail>) msg.obj).size()<=0) {
                        UiUtils.getInstance().showToast("尚未参与话题!");
                        commentDetailList.clear();
                    } else {
                        //保存数据
                        CommentDao.getInstance().saveAll((List<HDC_CommentDetail>) msg.obj);
                        commentDetailList = (List<HDC_CommentDetail>) msg.obj;
                        //进行更新其他类型的数据
                        for(HDC_CommentDetail data:(List<HDC_CommentDetail>) msg.obj){
                            CommentDao.getInstance().update(data);
                        }
//                        UiUtils.getInstance().showToast(keyName+"请求首页数据成功!");
                    }
                    adapter.setItemInfos(commentDetailList);
                    recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                    //进行设置参与话题的个数
                    User user=UserDao.getInstance().getUserId(targetUserId);
                    if(user!=null){
                        activity.setCount(user);
                    }
                    currentPage=firstPage;
                    mSwipeRefreshLayout.setRefreshing(false);
                    mSwipeRefreshLayout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                    break;

                case HDCivilizationConstants.REFRESH_PAGE:
                    CommentDao.getInstance().clearJoinTopic(targetUserId);
                    if (((List<HDC_CommentDetail>) msg.obj).size()<=0) {
                        UiUtils.getInstance().showToast("尚未参与话题!");
                        commentDetailList.clear();
                    } else {
                        //保存数据
                        CommentDao.getInstance().saveAll((List<HDC_CommentDetail>) msg.obj);
                        commentDetailList = (List<HDC_CommentDetail>) msg.obj;
                        //进行更新其他类型的数据
                        for(HDC_CommentDetail data:(List<HDC_CommentDetail>) msg.obj){
                            CommentDao.getInstance().update(data);
                        }
//                        UiUtils.getInstance().showToast(keyName + "请求首页数据成功!");
                    }
                    adapter.setItemInfos(commentDetailList);
                    recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                    //进行设置参与话题的个数
                    user=UserDao.getInstance().getUserId(targetUserId);
                    if(user!=null){
                        activity.setCount(user);
                    }
                    currentPage=firstPage;
                    mSwipeRefreshLayout.setRefreshing(false);
                    mSwipeRefreshLayout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                    break;

                case HDCivilizationConstants.LOAD_MORE:
                    if (((List<HDC_CommentDetail>) msg.obj).size()<=0) {
                        UiUtils.getInstance().showToast("没有更多参与话题信息!");
                    } else {
                        CommentDao.getInstance().saveAll((List<HDC_CommentDetail>) msg.obj);
                        //进行更新其他类型的数据
                        for(HDC_CommentDetail data:(List<HDC_CommentDetail>) msg.obj){
                            CommentDao.getInstance().update(data);
                        }
                        int firstSize=commentDetailList.size();
                        commentDetailList.addAll((List<HDC_CommentDetail>) msg.obj);
                        adapter.setItemInfos(commentDetailList);
                        recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(firstSize - 1);
                        currentPage=currentPage+1;
//                        UiUtils.getInstance().showToast(keyName+"加载更多数据成功!");
                    }
                    recyclerView.notifyMoreFinish(true);
                    //进行设置参与话题的个数
                    user=UserDao.getInstance().getUserId(targetUserId);
                    if(user!=null){
                        activity.setCount(user);
                    }
                    break;

                case HDCivilizationConstants.ERROR_CODE:
                    UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                    if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)==HDCivilizationConstants.LOAD_MORE){
                        recyclerView.notifyMoreFinish(true);
//                        linearLayoutManager.scrollToPosition(commentDetailList.size() - 1);
                    }else if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)==HDCivilizationConstants.REFRESH_PAGE){
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                        if(adapter.commentDetailArrayList==null || adapter.commentDetailArrayList.size()==0){
                            adapter.commentDetailArrayList= null;
                            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        }

                    }else if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)==HDCivilizationConstants.REQUEST_FIRST_PAGE){
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                        if(adapter.commentDetailArrayList==null || adapter.commentDetailArrayList.size()==0){
                            adapter.commentDetailArrayList= null;
                            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(contentView==null){
            contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_join, null);
            init(contentView);
        }
        //初始化适配器数据
        refreshList();
        getRequestPagerData();
    }

    /**
     * 进行初始化数据
     */
    public void refreshList() {
        if(contentView==null || recyclerView==null){
            contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_join, null);
            init(contentView);
        }
        List<HDC_CommentDetail> datas= CommentDao.getInstance().getJoinTopics(targetUserId);
        if(datas!=null && datas.size()>0){
            commentDetailList=datas;
            if(adapter==null){
                adapter=new UserInfoAdapter();
                adapter.setItemInfos(datas);
                recyclerView.setAdapter(adapter);
            }else {
                adapter.setItemInfos(datas);
                recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
            }
        }else{
            commentDetailList.clear();
        }

        User user= UserDao.getInstance().getUserId(targetUserId);
        if(user!=null){
            activity.setCount(user);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        joinProtocol.setIsJoinThemeType(true);
        return contentView;
    }

    private void init(View view) {
        initView(view);
        initView();
    }

    /**
     * 初始化对象
     * @param view
     */
    private void initView(View view) {
        mSwipeRefreshLayout = (SimpleSwipeRefreshLayout) view.findViewById(R.id.join_swipe);
        recyclerView = (LoadMoreRecyclerView) view.findViewById(R.id.join_recyclerview);
        linearLayoutManager = new LinearLayoutManager(UiUtils.getInstance().getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.switchLayoutManager(new LinearLayoutManager(UiUtils.getInstance().getContext()));
        mSwipeRefreshLayout.setChild(recyclerView);
        recyclerView.setPageSize(PAGE_SIZE);
        //进行初始化时间
        joinProtocol.setNetWorktime(System.currentTimeMillis());

        leftWidth=UiUtils.getInstance().getDefaultWidth()-UiUtils.getDimen(R.dimen.edge_padding_left)*2-2*UiUtils.getDimen(R.dimen.gridview_column_space);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                initRecycleViewHeight = recyclerView.getMeasuredHeight();
            }
        });
    }

    private void initView() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3, R.color.color4);
        //下拉刷新数据
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                mSwipeRefreshLayout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESHINT);
                ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        String userId="";
                        try {
                            try {
                                User user= UserDao.getInstance().getLocalUser();
                                userId=user.getUserId();
                            } catch (ContentException e) {
                                e.printStackTrace();
                                userId="";
                            }
                            UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                            LinkedHashMap<String,String> paramsMap = new LinkedHashMap<String, String>();
                            paramsMap.put("tranCode","AROUND0009");
                            paramsMap.put("pagerNum",PAGE_SIZE+"");
                            paramsMap.put("currentPager",firstPage+"");
                            paramsMap.put("userId", userId);
                            paramsMap.put("targetUserId", targetUserId);
                            urlParamsEntity.setParamsHashMap(paramsMap);
                            urlParamsEntity.HDCURL=UrlParamsEntity.CURRENT_ID;
                            joinProtocol.setUserId(userId);
                            joinProtocol.setTargetUserId(targetUserId);
                            joinProtocol.setIsJoinThemeType(true);
                            joinProtocol.setActionKeyName(refershName);
                            message.obj = joinProtocol.loadData(urlParamsEntity);
                            message.what = HDCivilizationConstants.REFRESH_PAGE;
                            handler.sendMessage(message);
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                            message.what = HDCivilizationConstants.ERROR_CODE;
                            bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REFRESH_PAGE);
                            bundle.putString(HDCivilizationConstants.ERROR_CONTENT,e.getMessage());
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } catch (ContentException e) {
                            e.printStackTrace();
                            message.what = HDCivilizationConstants.ERROR_CODE;
                            bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REFRESH_PAGE);
                            bundle.putString(HDCivilizationConstants.ERROR_CONTENT,e.getErrorContent());
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                });
            }
        });


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
                                    paramsMap.put("tranCode", "AROUND0009");
                                    paramsMap.put("pagerNum", PAGE_SIZE + "");
                                    paramsMap.put("currentPager", (currentPage + 1) + "");
                                    paramsMap.put("userId", userId);
                                    paramsMap.put("targetUserId", targetUserId);
                                    urlParamsEntity.setParamsHashMap(paramsMap);
                                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                                    joinProtocol.setUserId(userId);
                                    joinProtocol.setTargetUserId(targetUserId);
                                    joinProtocol.setIsJoinThemeType(true);
                                    joinProtocol.setActionKeyName(loadmoreName);
                                    message.obj = joinProtocol.loadData(urlParamsEntity);
                                    joinProtocol.setUserId(userId);
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
                }, 1000);
            }
        });
    }


    /**
     *
     */
    public void getRequestPagerData(){
        //第一屏加载数据
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                mSwipeRefreshLayout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESHINT);
                getFirstData();
            }
        });
    }

    /**
     * 请求第1页数据
     */
    private void getFirstData() {
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
                    paramsMap.put("tranCode", "AROUND0009");
                    paramsMap.put("pagerNum", PAGE_SIZE + "");
                    paramsMap.put("currentPager", (firstPage) + "");
                    paramsMap.put("userId", userId);
                    paramsMap.put("targetUserId", targetUserId);
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                    joinProtocol.setUserId(userId);
                    joinProtocol.setTargetUserId(targetUserId);
                    joinProtocol.setActionKeyName(loadFirstPage);
                    message.obj = joinProtocol.loadData(urlParamsEntity);
                    message.what = HDCivilizationConstants.REQUEST_FIRST_PAGE;
                    handler.sendMessage(message);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REQUEST_FIRST_PAGE);
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT,  e.getMessage());
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


    /********
     * RecyclerView的适配器
     **********/
    class UserInfoAdapter extends LoadMoreRecyclerView.Adapter<UserInfoAdapter.MineHolder> {

        private List<HDC_CommentDetail> commentDetailArrayList;
        private int ITEM_TYPE_ERROR_CODE=101;//失败条目类型
        private int ITEM_TYPE_EMPTY_CODE=102;//为空的条目类型


        public void setItemInfos(List<HDC_CommentDetail> commentDetailArrayList) {
            this.commentDetailArrayList = commentDetailArrayList;
        }

        public UserInfoAdapter() {

        }

        @Override
        public UserInfoAdapter.MineHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if(viewType==ITEM_TYPE_ERROR_CODE){
                //如果是错误类型
                return new MineHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.loadpager_error,null),ITEM_TYPE_ERROR_CODE);
            }else if(viewType==ITEM_TYPE_EMPTY_CODE){
                //如果是数据为空类型
                return new MineHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.loadpage_empty,null),ITEM_TYPE_EMPTY_CODE);
            }
            return new MineHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.listview_item_comment_user,null));
        }

        /**
         * 进行显示错误页面
         * @param mineHodler
         */
        private void visiableError(MineHolder mineHodler) {
            mineHodler.button.setVisibility(View.VISIBLE);
            mineHodler.page_iv.setVisibility(View.VISIBLE);
            mineHodler.pb_load.setVisibility(View.GONE);
            mineHodler.loading_txt.setVisibility(View.GONE);
        }

        @Override
        public void onBindViewHolder(UserInfoAdapter.MineHolder holder, int position) {
            if(getItemViewType(position)==ITEM_TYPE_ERROR_CODE){
                mSwipeRefreshLayout.setEnabled(false);
                visiableError(holder);
            }else if(getItemViewType(position)==ITEM_TYPE_EMPTY_CODE){
                //数据为空时!
                mSwipeRefreshLayout.setEnabled(true);
            }else{
                mSwipeRefreshLayout.setEnabled(true);
                final HDC_CommentDetail data=commentDetailArrayList.get(position);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //进行跳转到评论的详情页
                        Intent intent=new Intent(UiUtils.getInstance().getContext(),CommentDetailActivity.class);
                        intent.putExtra(CommentDetailActivity.ITEM_ID,data.getItemId());
                        intent.putExtra(CommentDetailActivity.TOPIC_TYPE,HDCivilizationConstants.JOIN_TOPIC_TYPE);
                        intent.putExtra(CommentDetailActivity.ITEMUSER_ID,data.getLaunchUser().getUserId());
                        intent.putExtra(CommentDetailActivity.ITEM_ID_AND_TYPE,data.getItemIdAndType());
                        startActivity(intent);
                    }
                });
                holder.userTitle.setText(data.getTitle());
                holder.userContent.setText(data.getContent());

                if (data.getImgUrlList() != null) {
                    int imgUrlSize = data.getImgUrlList().size();
                    switch (imgUrlSize) {
                        case 0:
                            holder.rl_img_list.setVisibility(View.GONE);
                            holder.userImg.setVisibility(View.GONE);
                            break;
                        case 1:
                            holder.userImg.setVisibility(View.VISIBLE);
                            holder.userImg1.setVisibility(View.VISIBLE);
                            holder.userImg2.setVisibility(View.INVISIBLE);
                            holder.userImg3.setVisibility(View.INVISIBLE);
                            holder.rl_img_list.setVisibility(View.GONE);
                            BitmapUtil.getInstance().displayImg(holder.userImg1, UrlParamsEntity.WUCHEN_XU_IP_FILE+data.getImgUrlList().get(0).getImgThumbUrl());
                            break;
                        case 2:
                            holder.userImg.setVisibility(View.VISIBLE);
                            holder.userImg1.setVisibility(View.VISIBLE);
                            holder.userImg2.setVisibility(View.VISIBLE);
                            holder.userImg3.setVisibility(View.INVISIBLE);
                            holder.rl_img_list.setVisibility(View.GONE);
                            BitmapUtil.getInstance().displayImg(holder.userImg1,UrlParamsEntity.WUCHEN_XU_IP_FILE+ data.getImgUrlList().get(0).getImgThumbUrl());
                            BitmapUtil.getInstance().displayImg(holder.userImg2, UrlParamsEntity.WUCHEN_XU_IP_FILE+data.getImgUrlList().get(1).getImgThumbUrl());
                            break;
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                            holder.userImg.setVisibility(View.VISIBLE);
                            holder.userImg1.setVisibility(View.VISIBLE);
                            holder.userImg2.setVisibility(View.VISIBLE);
                            holder.userImg3.setVisibility(View.VISIBLE);
                            if (data.getImgUrlList().size()>3){
                                holder.rl_img_list.setVisibility(View.VISIBLE);
                                holder.tv_img_list_number.setText(data.getImgUrlList().size()+"");
                            }else{
                                holder.rl_img_list.setVisibility(View.GONE);
                            }
                            BitmapUtil.getInstance().displayImg(holder.userImg1, UrlParamsEntity.WUCHEN_XU_IP_FILE+data.getImgUrlList().get(0).getImgThumbUrl());
                            BitmapUtil.getInstance().displayImg(holder.userImg2, UrlParamsEntity.WUCHEN_XU_IP_FILE+data.getImgUrlList().get(1).getImgThumbUrl());
                            BitmapUtil.getInstance().displayImg(holder.userImg3, UrlParamsEntity.WUCHEN_XU_IP_FILE+data.getImgUrlList().get(2).getImgThumbUrl());
                            break;
                    }
                }else{
                    holder.userImg.setVisibility(View.GONE);
                }
                if(data.getLaunchUser().getIdentityState().equals(UserPermisson.VOLUNTEER.getType())){
                    holder.commentUserType.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
                }else{
                    holder.commentUserType.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
                }
                String nickName="";
                if(data.getLaunchUser().getNickName().matches("^[1][3,4,5,8,7][0-9]{9}$")){
                    nickName=data.getLaunchUser().getNickName().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
                }else{
                    nickName=data.getLaunchUser().getNickName();
                }
                holder.commentUserName.setText(nickName);
//            holder.commentUserName.setText(data.getLaunchUser().getNickName());
                holder.commentUserZan.setText(data.getCount()+"");
                holder.commentNumber.setText(data.getCommentCount()+"");

                //判断消息提醒个数是否大于0
                if(data.getTypeCount()>0){
                    holder.view_red_number.setVisibility(View.VISIBLE);
                }else{
                    holder.view_red_number.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            System.out.println("getItemViewType position:" + position + "...datas==null:" + (commentDetailArrayList == null));
            if(position==0 && commentDetailArrayList==null){
                return ITEM_TYPE_ERROR_CODE;
            }else if(position==0 && commentDetailArrayList.size()==0){
                return ITEM_TYPE_EMPTY_CODE;
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            if(commentDetailArrayList==null || commentDetailArrayList.size()==0){
                return 1;
            }
            return commentDetailArrayList.size();
        }

        class MineHolder extends LoadMoreRecyclerView.ViewHolder {

            private TextView userTitle, userContent, commentUserType, commentUserName, commentUserZan;
            private ImageView userImg1, userImg2, userImg3;
            private LinearLayout userImg;
            private RelativeLayout rl_img_list;
            private  TextView tv_img_list_number;

            private TextView commentNumber;


            private int ITEM_TYPE_ERROR_CODE=101;//失败条目类型
            private int ITEM_TYPE_EMPTY_CODE=102;//为空的条目类型

            //为空时候的view
            TextView tv_empty,loading_txt;//按钮
            RelativeLayout loadpage_empty;
            LinearLayout loadpage_error;
            //错误时候的view
            Button button;//按钮
            ProgressBar pb_load;
            ImageView page_iv;
            private View view_red_number;
            private int viewType;//条目的类型

            public MineHolder(View itemView, int viewType) {
                super(itemView);
                this.viewType = viewType;
                initView(itemView);
            }

            public MineHolder(View itemView) {
                super(itemView);
                initView(itemView);
            }

            private void initView(View itemView) {
                if(viewType==ITEM_TYPE_ERROR_CODE){
                    //错误的处理
                    loadpage_error=(LinearLayout)itemView.findViewById(R.id.loadpage_error);
                    loading_txt=(TextView)itemView.findViewById(R.id.loading_txt);
                    page_iv=(ImageView)itemView.findViewById(R.id.page_iv);
                    pb_load=(ProgressBar)itemView.findViewById(R.id.pb_load);
                    System.out.println("ITEM_TYPE_ERROR_CODE initRecycleViewHeight:"+initRecycleViewHeight);
                    loadpage_error.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,initRecycleViewHeight));
                    loadpage_error.requestLayout();
                    button=(Button)itemView.findViewById(R.id.page_bt);
                    button = (Button) itemView.findViewById(R.id.page_bt);
                    //访问网络失败时
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v){
                            //进行请求网络
                            visiableLoading();
                            getFirstData();
                        }

                        private void visiableLoading() {
                            loading_txt.setVisibility(View.VISIBLE);
                            pb_load.setVisibility(View.VISIBLE);
                            page_iv.setVisibility(View.GONE);
                            button.setVisibility(View.GONE);
                        }
                    });
                }else if(viewType==ITEM_TYPE_EMPTY_CODE){
                    //为空的处理
                    loadpage_empty=(RelativeLayout)itemView.findViewById(R.id.loadpage_empty);
                    System.out.println("loadpage_empty is null:" + (loadpage_empty==null));
                    loadpage_empty.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,initRecycleViewHeight));
//                loadpage_empty.getLayoutParams().height=initRecycleViewHeight;
                    loadpage_empty.requestLayout();
                    tv_empty=(TextView)itemView.findViewById(R.id.tv_empty);
                    tv_empty.setText(keyName+HDCivilizationConstants.EMPTY_STRING);
                }else{
                    userTitle = (TextView) itemView.findViewById(R.id.tv_user_title);
                    userContent = (TextView) itemView.findViewById(R.id.tv_user_content);
                    int width=(int)Math.round((double)leftWidth/3.0);
                    userImg = (LinearLayout) itemView.findViewById(R.id.ll_img_list);
                    userImg1 = (ImageView) itemView.findViewById(R.id.user_img_1);
                    userImg1.getLayoutParams().height=Math.round(width* MaskView.heghtDividWidthRate);
                    userImg1.requestLayout();
                    userImg2 = (ImageView) itemView.findViewById(R.id.user_img_2);
                    userImg2.getLayoutParams().height=Math.round(width* MaskView.heghtDividWidthRate);
                    userImg2.requestLayout();
                    userImg3 = (ImageView) itemView.findViewById(R.id.user_img_3);
                    userImg3.getLayoutParams().height=Math.round(width* MaskView.heghtDividWidthRate);
                    userImg3.requestLayout();
                    commentUserType = (TextView) itemView.findViewById(R.id.comment_user_id_type);
                    commentUserName = (TextView) itemView.findViewById(R.id.comment_user_name_item);
                    commentUserZan = (TextView) itemView.findViewById(R.id.comment_user_zan);
                    commentNumber = (TextView)itemView.findViewById(R.id.comment_user_comment_number);

                    rl_img_list = (RelativeLayout)itemView.findViewById(R.id.rl_img_list);
                    tv_img_list_number = (TextView)itemView.findViewById(R.id.tv_img_list_number);

                    //红色背景
                    view_red_number =itemView.findViewById(R.id.view_red_number);
                }
            }
        }
    }

    /***
     * 判断是否超时
     * @return boolean
     */
    public boolean getNetWorkTime(){
        boolean trueOrFalse = (System.currentTimeMillis() - joinProtocol.getNetWorktime()) > HDCivilizationConstants.NET_GAP_TIME;
        return trueOrFalse;
    }

    public boolean isRequestintNetwork(){
        if(mSwipeRefreshLayout.getRefreshState()==SimpleSwipeRefreshLayout.SWIPE_REFRESHINT ||
                recyclerView.getLoadMoreState()==LoadMoreRecyclerView.LOAD_MORE_ING){
            //正在访问网络
            return true;
        }
        //没有正在访问网络
        return false;
    }
}

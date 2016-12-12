package com.zhjy.hdcivilization.fragment;

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
import com.zhjy.hdcivilization.entity.SystemSetting;
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
 *         文明评论用户的个人信息-发表的话题
 */
public class CommentSubFragment extends Fragment {

    private CommentUserInfoActivity activity;
    private String gambitCount;
    private TextView subText;
    private View contentView;
    private LoadMoreRecyclerView recyclerView;
    private String refershName = "刷新发表话题失败";
    private String loadmoreName = "加载更多发表话题失败";
    private String loadFirstPage = "加载发表话题失败";
    private UserInfoAdapter adapter;
    private SimpleSwipeRefreshLayout mSwipeRefreshLayout;
    private CommentUserJoinProtocol joinProtocol ;
    private List<HDC_CommentDetail> commentUserInfoList = new ArrayList<HDC_CommentDetail>();
    private LinearLayoutManager linearLayoutManager;

    private String keyName="发表话题";
    private final int PAGE_SIZE=12;//固定个数
    private  int firstPage=1;//首页index
    private  int currentPage=firstPage;//当前页Index
    private String targetUserId;
    private User targetUser;
    private int initRecycleViewHeight;
    private int leftWidth;//三张图片剩余的宽度


    public int getInitRecycleViewHeight() {
        return initRecycleViewHeight;
    }

    public void setInitRecycleViewHeight(int initRecycleViewHeight) {
        this.initRecycleViewHeight = initRecycleViewHeight;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public void setSubText(TextView subText) {
        this.subText = subText;
    }

    public void setActivity(CommentUserInfoActivity activity) {
        this.activity = activity;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(adapter==null) {
                synchronized (CommentSubFragment.this) {
                    adapter = new UserInfoAdapter();
                    adapter.setItemInfos(commentUserInfoList);
                    recyclerView.setAdapter(adapter);
                }
            }
            switch (msg.what) {
                case HDCivilizationConstants.REQUEST_FIRST_PAGE:
                    CommentDao.getInstance().clearSubTopic(targetUserId);
                    if (((List<HDC_CommentDetail>) msg.obj).size()<=0) {
                        UiUtils.getInstance().showToast(HDCivilizationConstants.NOT_FABU_THEME);
                        commentUserInfoList.clear();
                    } else {
                        CommentDao.getInstance().saveAll((List<HDC_CommentDetail>) msg.obj);
                        commentUserInfoList = (List<HDC_CommentDetail>) msg.obj;
                        //进行更新其他类型的数据
                        for(HDC_CommentDetail data:(List<HDC_CommentDetail>) msg.obj){
                            CommentDao.getInstance().update(data);
                        }
//                        UiUtils.getInstance().showToast(keyName + "请求首页数据成功!");
                    }
                    adapter.setItemInfos(commentUserInfoList);
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
                    CommentDao.getInstance().clearSubTopic(targetUserId);
                    if (((List<HDC_CommentDetail>) msg.obj).size()<=0) {
                        UiUtils.getInstance().showToast(HDCivilizationConstants.NOT_FABU_THEME);
                        commentUserInfoList.clear();
                    } else {
                        CommentDao.getInstance().saveAll((List<HDC_CommentDetail>) msg.obj);
                        commentUserInfoList = (List<HDC_CommentDetail>) msg.obj;
                        //进行更新其他类型的数据
                        for(HDC_CommentDetail data:(List<HDC_CommentDetail>) msg.obj){
                            CommentDao.getInstance().update(data);
                        }
//                        UiUtils.getInstance().showToast(keyName + "请求首页数据成功!");
                    }

                    adapter.setItemInfos(commentUserInfoList);
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
                        UiUtils.getInstance().showToast("没有更多发表话题了!");
                    } else {
                        CommentDao.getInstance().saveAll((List<HDC_CommentDetail>) msg.obj);
                        //进行更新其他类型的数据
                        for(HDC_CommentDetail data:(List<HDC_CommentDetail>) msg.obj){
                            CommentDao.getInstance().update(data);
                        }
                        int firstSize=commentUserInfoList.size();
                        commentUserInfoList.addAll((List<HDC_CommentDetail>) msg.obj);
                        adapter.setItemInfos(commentUserInfoList);
                        recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
//                        linearLayoutManager.scrollToPosition(commentUserInfoList.size() - 1);
                        linearLayoutManager.scrollToPosition(firstSize - 1);
                        recyclerView.setAutoLoadMoreEnable(true);
                        currentPage=currentPage+1;
//                        UiUtils.getInstance().showToast(keyName + "加载更多数据成功!");
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
//                        linearLayoutManager.scrollToPosition(commentUserInfoList.size() - 1);
                    }else if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)==HDCivilizationConstants.REFRESH_PAGE){
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                        if(adapter.itemInfos==null || adapter.itemInfos.size()==0){
                            adapter.itemInfos= null;
                            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        }
                    }else if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)==HDCivilizationConstants.REQUEST_FIRST_PAGE){
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                        if(adapter.itemInfos==null || adapter.itemInfos.size()==0){
                            adapter.itemInfos= null;
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
            contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_sub, null);
            init(contentView);
        }
        joinProtocol = new CommentUserJoinProtocol();
        joinProtocol.setNetWorktime(System.currentTimeMillis()-HDCivilizationConstants.NET_GAP_TIME*2);
        joinProtocol.setIsJoinThemeType(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //进行初始化数据
        refreshList();
        return contentView;
    }

    /**
     * 进行初始化数据
     */
    public void refreshList() {
        targetUser= UserDao.getInstance().getUserId(targetUserId);
        List<HDC_CommentDetail> datas= CommentDao.getInstance().getSubTopic(targetUserId);
        if(datas!=null && datas.size()>0){
            commentUserInfoList=datas;
            if(adapter==null){
                adapter=new UserInfoAdapter();
                adapter.setItemInfos(commentUserInfoList);
                if(recyclerView==null){
                    recyclerView = (LoadMoreRecyclerView) contentView.findViewById(R.id.sub_recyclerview);
                }
                recyclerView.setAdapter(adapter);
            }else{
                adapter.setItemInfos(commentUserInfoList);
                if(recyclerView==null){
                    recyclerView = (LoadMoreRecyclerView) contentView.findViewById(R.id.sub_recyclerview);
                }
                recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
            }
//            System.out.println("CommentSubFragment onCreateView init datas size:" + datas.size() + "..nickName:" + targetUser.getNickName());
        }else{
//            commentUserInfoList.clear();
//            adapter.setItemInfos(commentUserInfoList);
//            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
        }
        User user=UserDao.getInstance().getUserId(targetUserId);
        if(user!=null){
//            System.out.println("CommentSubFragment onCreateView init getSubThemeCount size:" + user.getSubThemeCount() + "..nickName:" + targetUser.getNickName());
            activity.setCount(user);
        }
    }

    private void init(View view) {
        mSwipeRefreshLayout = (SimpleSwipeRefreshLayout) view.findViewById(R.id.sub_swipe);
        recyclerView = (LoadMoreRecyclerView) view.findViewById(R.id.sub_recyclerview);
        mSwipeRefreshLayout.setChild(recyclerView);
        linearLayoutManager = new LinearLayoutManager(UiUtils.getInstance().getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.switchLayoutManager(new LinearLayoutManager(UiUtils.getInstance().getContext()));
        recyclerView.setPageSize(PAGE_SIZE);

        leftWidth=UiUtils.getInstance().getDefaultWidth()-UiUtils.getDimen(R.dimen.edge_padding_left)*2-2*UiUtils.getDimen(R.dimen.gridview_column_space);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                initRecycleViewHeight = recyclerView.getMeasuredHeight();
            }
        });
        initView();
    }

    private void initView() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3, R.color.color4);
        //下拉刷新加载更多的数据
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
                            paramsMap.put("tranCode","AROUND0022");
                            paramsMap.put("pagerNum",PAGE_SIZE+"");
                            paramsMap.put("currentPager",firstPage+"");
                            paramsMap.put("userId", userId);
                            paramsMap.put("targetUserId", targetUserId);
                            urlParamsEntity.setParamsHashMap(paramsMap);
                            urlParamsEntity.HDCURL=UrlParamsEntity.CURRENT_ID;
                            joinProtocol.setUserId(userId);
                            joinProtocol.setTargetUserId(targetUserId);
                            joinProtocol.setIsJoinThemeType(false);
                            joinProtocol.setActionKeyName(refershName);
                            message.obj = joinProtocol.loadData(urlParamsEntity);
                            message.what = HDCivilizationConstants.REFRESH_PAGE;
                            handler.sendMessage(message);
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                            message.what = HDCivilizationConstants.ERROR_CODE;
                            bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REFRESH_PAGE);
                            bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } catch (ContentException e) {
                            e.printStackTrace();
                            message.what = HDCivilizationConstants.ERROR_CODE;
                            bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REFRESH_PAGE);
                            bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
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
                                    paramsMap.put("tranCode", "AROUND0022");
                                    paramsMap.put("pagerNum", PAGE_SIZE + "");
                                    paramsMap.put("currentPager", (currentPage + 1) + "");
                                    paramsMap.put("userId", userId);
                                    paramsMap.put("targetUserId", targetUserId);
                                    urlParamsEntity.setParamsHashMap(paramsMap);
                                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                                    joinProtocol.setUserId(userId);
                                    joinProtocol.setTargetUserId(targetUserId);
                                    joinProtocol.setIsJoinThemeType(false);
                                    joinProtocol.setActionKeyName(loadmoreName);
                                    message.obj = joinProtocol.loadData(urlParamsEntity);
                                    joinProtocol.setUserId(userId);
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
                                    message.what = HDCivilizationConstants.ERROR_CODE;
                                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.LOAD_MORE);
                                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT,  e.getErrorContent());
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

    public void getRequestPagerData(){
        //第一屏数据加载
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
     * 进行获取第一页数据
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
                    paramsMap.put("tranCode", "AROUND0022");
                    paramsMap.put("pagerNum", PAGE_SIZE + "");
                    paramsMap.put("currentPager", (firstPage) + "");
                    paramsMap.put("userId", userId);
                    paramsMap.put("targetUserId", targetUserId);
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                    joinProtocol.setUserId(userId);
                    joinProtocol.setTargetUserId(targetUserId);
                    joinProtocol.setIsJoinThemeType(false);
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
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT,  e.getErrorContent());
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

        private List<HDC_CommentDetail> itemInfos;
        private int ITEM_TYPE_ERROR_CODE=101;//失败条目类型
        private int ITEM_TYPE_EMPTY_CODE=102;//为空的条目类型



        public void setItemInfos(List<HDC_CommentDetail> itemInfos) {
            this.itemInfos = itemInfos;
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
                final HDC_CommentDetail data=itemInfos.get(position);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //进行跳转到评论的详情页
                        Intent intent = new Intent(UiUtils.getInstance().getContext(), CommentDetailActivity.class);
                        intent.putExtra(CommentDetailActivity.ITEM_ID, data.getItemId());
                        intent.putExtra(CommentDetailActivity.TOPIC_TYPE, HDCivilizationConstants.SUB_TOPIC_TYPE);
                        intent.putExtra(CommentDetailActivity.ITEMUSER_ID, data.getLaunchUser().getUserId());
                        intent.putExtra(CommentDetailActivity.ITEM_ID_AND_TYPE, data.getItemIdAndType());
                        startActivity(intent);
                    }
                });
                holder.userTitle.setText(data.getTitle());
                if (data.getContent()==null || data.getContent().toString().equals("")){
                    holder.userContent.setVisibility(View.GONE);
                }else{
                    holder.userContent.setVisibility(View.VISIBLE);
                    holder.userContent.setText(data.getContent());
                }
                /***图片的显示问题***/
                if(data.getImgUrlList()!=null){
                    int imgUrlSize = data.getImgUrlList().size();
                    switch (imgUrlSize) {
                        case 0:
                            holder.userImg.setVisibility(View.GONE);
                            holder.rl_img_list.setVisibility(View.GONE);
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
                            BitmapUtil.getInstance().displayImg(holder.userImg1, UrlParamsEntity.WUCHEN_XU_IP_FILE+data.getImgUrlList().get(0).getImgThumbUrl());
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


                System.out.println("targetUser=" + targetUser.getIdentityState());
                if(targetUser.getIdentityState().equals(UserPermisson.VOLUNTEER.getType())){
                    holder.commentUserType.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
                }else{
                    holder.commentUserType.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
                }


                String nickName="";
                if(targetUser.getNickName().matches("^[1][3,4,5,8,7][0-9]{9}$")){
                    nickName=targetUser.getNickName().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
                }else{
                    nickName=targetUser.getNickName();
                }
                holder.commentUserName.setText(nickName);
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
            System.out.println("getItemViewType position:"+position+"...datas==null:"+(itemInfos==null));
            if(position==0 && itemInfos==null){
                return ITEM_TYPE_ERROR_CODE;
            }else if(position==0 && itemInfos.size()==0){
                return ITEM_TYPE_EMPTY_CODE;
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            if(itemInfos==null || itemInfos.size()==0){
                return 1;
            }
            return itemInfos.size();
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


            public MineHolder(View itemView) {
                super(itemView);
                initView(itemView);
            }

            public MineHolder(View itemView, int viewType) {
                super(itemView);
                this.viewType = viewType;
                initView(itemView);
            }

            /**
             * 进行初始化View
             * @param itemView
             */
            private void initView(View itemView) {
                if (viewType == ITEM_TYPE_ERROR_CODE) {
                    //错误的处理
                    loadpage_error = (LinearLayout) itemView.findViewById(R.id.loadpage_error);
                    loading_txt=(TextView)itemView.findViewById(R.id.loading_txt);
                    page_iv=(ImageView)itemView.findViewById(R.id.page_iv);
                    pb_load=(ProgressBar)itemView.findViewById(R.id.pb_load);
                    System.out.println("ITEM_TYPE_ERROR_CODE initRecycleViewHeight:" + initRecycleViewHeight);
                    loadpage_error.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, initRecycleViewHeight));
                    loadpage_error.requestLayout();
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
                } else if (viewType == ITEM_TYPE_EMPTY_CODE) {
                    //为空的处理
                    loadpage_empty = (RelativeLayout) itemView.findViewById(R.id.loadpage_empty);
                    System.out.println("loadpage_empty is null:" + (loadpage_empty == null));
                    loadpage_empty.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, initRecycleViewHeight));
//                loadpage_empty.getLayoutParams().height=initRecycleViewHeight;
                    loadpage_empty.requestLayout();
                    tv_empty = (TextView) itemView.findViewById(R.id.tv_empty);
                    tv_empty.setText(keyName + HDCivilizationConstants.EMPTY_STRING);
                } else {
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

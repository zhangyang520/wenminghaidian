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
import android.text.TextPaint;
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
import com.zhjy.hdcivilization.activity.CiviStateDetailActivity;
import com.zhjy.hdcivilization.activity.CommentDetailActivity;
import com.zhjy.hdcivilization.activity.CommentUserInfoActivity;
import com.zhjy.hdcivilization.dao.CommentDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_CiviState;
import com.zhjy.hdcivilization.entity.HDC_CommentDetail;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.protocol.CommentHotProtocol;
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
 * @author :huangxianfeng on 2016/7/27.
 *         文明评论的热门话题Fragment
 */
public class CiviCommentHotFragment extends Fragment {

    private Activity activity;
    public void setActivity(Activity activity) {
        this.activity = activity;
    }
    private LinearLayoutManager linearLayoutManager;
    private View contentView;
    private SimpleSwipeRefreshLayout mSwipeRefreshLayout;
    private LoadMoreRecyclerView recyclerView;
    private CommentHotProtocol commentHotProtocol;
    private String keyName = "热门话题";
    private String refershName = "刷新热门话题信息失败";
    private String loadmoreName = "加载更多热门话题信息失败";
    private String loadFirstPage = "加载热门话题信息失败";
    private final int PAGE_SIZE=12;//固定个数
    private  int firstPage=1;
    private  int currentPage=firstPage;
    private List<HDC_CommentDetail> list = new ArrayList<HDC_CommentDetail>();

    private HotAdapter hotAdapter;
    private int initRecycleViewHeight;

    private int leftWidth;//三张图片剩余的宽度

    public int getInitRecycleViewHeight() {
        return initRecycleViewHeight;
    }

    public void setInitRecycleViewHeight(int initRecycleViewHeight) {
        this.initRecycleViewHeight = initRecycleViewHeight;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(hotAdapter==null) {
                synchronized (CiviCommentHotFragment.this) {
                    if(hotAdapter==null) {
                        hotAdapter = new HotAdapter();
                        recyclerView.setAdapter(hotAdapter);
                    }
                }
            }
            switch (msg.what) {
                case HDCivilizationConstants.REQUEST_FIRST_PAGE:
                    CommentDao.getInstance().clearHotTopic();
                    if (((List<HDC_CommentDetail>) msg.obj).size()<=0) {
                        UiUtils.getInstance().showToast("尚未发布新话题!");
                        list.clear();
                    } else {
                        CommentDao.getInstance().saveAll((List<HDC_CommentDetail>) msg.obj);
                        //进行更新其他类型的数据
                        for(HDC_CommentDetail data:(List<HDC_CommentDetail>) msg.obj){
                            CommentDao.getInstance().update(data);
                        }
                        list = (List<HDC_CommentDetail>) msg.obj;
//                        UiUtils.getInstance().showToast(keyName + "加载首页数据成功!");
                    }
                    hotAdapter.setList(list);
                    recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                    currentPage=firstPage;
                    mSwipeRefreshLayout.setRefreshing(false);
                    mSwipeRefreshLayout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                    break;

                case HDCivilizationConstants.REFRESH_PAGE:
                    CommentDao.getInstance().clearHotTopic();
                    if (((List<HDC_CommentDetail>) msg.obj).size()<=0) {
                        UiUtils.getInstance().showToast("尚未发布新话题!");
                        list.clear();
                    } else {
//                        System.out.println("REFRESH_PAGE (List<HDC_CommentDetail>) msg.obj:"+((List<HDC_CommentDetail>) msg.obj).toString());
                        list = (List<HDC_CommentDetail>) msg.obj;
                        CommentDao.getInstance().saveAll((List<HDC_CommentDetail>) msg.obj);
                        //进行更新其他类型的数据
                        for(HDC_CommentDetail data:(List<HDC_CommentDetail>) msg.obj){
                            CommentDao.getInstance().update(data);
                        }
//                        UiUtils.getInstance().showToast(keyName + "刷新首页数据成功!");
                    }
                    hotAdapter.setList(list);
                    recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                    currentPage=firstPage;
                    mSwipeRefreshLayout.setRefreshing(false);
                    mSwipeRefreshLayout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                    break;

                case HDCivilizationConstants.LOAD_MORE:
                    if (((List<HDC_CommentDetail>) msg.obj).size()<=0) {
                        UiUtils.getInstance().showToast("没有更多热门话题信息了!");
                        recyclerView.notifyMoreFinish(true);
                    } else {
//                        UiUtils.getInstance().showToast(keyName + "加载更多数据成功！");
                        CommentDao.getInstance().saveAll((List<HDC_CommentDetail>) msg.obj);
                        //进行更新其他类型的数据
                        for(HDC_CommentDetail data:(List<HDC_CommentDetail>) msg.obj){
                            CommentDao.getInstance().update(data);
                        }
                        int firstSize=list.size();
                        list.addAll((List<HDC_CommentDetail>) msg.obj);
                        hotAdapter.setList(list);
//                        linearLayoutManager.scrollToPosition(list.size() - 1);
                        linearLayoutManager.scrollToPosition(firstSize - 1);
                        recyclerView.notifyMoreFinish(true);
                        recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        currentPage=currentPage+1;
                    }
                    break;

                case HDCivilizationConstants.ERROR_CODE:
                    UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                    if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)==HDCivilizationConstants.LOAD_MORE){
                        recyclerView.notifyMoreFinish(true);
//                        linearLayoutManager.scrollToPosition(list.size() - 1);
                    }else if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)==HDCivilizationConstants.REFRESH_PAGE){
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                        if(hotAdapter.list==null || hotAdapter.list.size()==0){
                            hotAdapter.list= null;
                            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        }
                    }else if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)==HDCivilizationConstants.REQUEST_FIRST_PAGE){
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                        if(hotAdapter.list==null || hotAdapter.list.size()==0){
                            hotAdapter.list= null;
                            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        }
                    }
                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(contentView==null){
            contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_hot_gambit, null);
            init(contentView);
        }
        refreshList();
        getRequestPagerData();
    }

    public void refreshList() {
        if(contentView==null || recyclerView==null){
            contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_hot_gambit, null);
            init(contentView);
        }
        //首先进行初始化数据
        List<HDC_CommentDetail> datas= CommentDao.getInstance().getHotTopics();
        if(datas!=null && datas.size()>0){
            list=datas;
            if(hotAdapter==null){
                hotAdapter=new HotAdapter();
                hotAdapter.setList(datas);
                recyclerView.setAdapter(hotAdapter);
            }else {
                hotAdapter.setList(datas);
                recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
            }
        }else{
            list.clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return contentView;
    }


    private void init(View view) {
        initView(view);

        leftWidth=UiUtils.getInstance().getDefaultWidth()-UiUtils.getDimen(R.dimen.edge_padding_left)*2-2*UiUtils.getDimen(R.dimen.gridview_column_space);
        //进行初始化时间
        commentHotProtocol = new CommentHotProtocol();
        commentHotProtocol.setNetWorktime(System.currentTimeMillis());
        initView();
    }

    /**
     * 初始化view对象
     * @param view
     */
    private void initView(View view) {
        mSwipeRefreshLayout = (SimpleSwipeRefreshLayout) view.findViewById(R.id.hot_swipe);
        recyclerView = (LoadMoreRecyclerView) view.findViewById(R.id.hot_recyclerview);
        mSwipeRefreshLayout.setChild(recyclerView);
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
    }

    private void initView() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3, R.color.color4);
        /**********下拉刷新最新的数据*******/
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESHINT);
                mSwipeRefreshLayout.setRefreshing(true);
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
                            paramsMap.put("tranCode","AROUND0008");
                            paramsMap.put("pagerNum",PAGE_SIZE+"");
                            paramsMap.put("currentPager",firstPage+"");
                            paramsMap.put("userId", userId);
                            urlParamsEntity.setParamsHashMap(paramsMap);
                            urlParamsEntity.HDCURL=UrlParamsEntity.CURRENT_ID;
                            commentHotProtocol.setUserId(userId);
                            commentHotProtocol.setActionKeyName(refershName);
                            message.obj = commentHotProtocol.loadData(urlParamsEntity);
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
        //上拉加载更多的数据
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
                                    paramsMap.put("tranCode","AROUND0008");
                                    paramsMap.put("pagerNum",PAGE_SIZE+"");
                                    paramsMap.put("currentPager",(currentPage+1)+"");
                                    paramsMap.put("userId", userId);
                                    urlParamsEntity.setParamsHashMap(paramsMap);
                                    urlParamsEntity.HDCURL=UrlParamsEntity.CURRENT_ID;
                                    commentHotProtocol.setUserId(userId);
                                    commentHotProtocol.setActionKeyName(loadmoreName);
                                    message.obj = commentHotProtocol.loadData(urlParamsEntity);
                                    commentHotProtocol.setUserId(userId);
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
                },1000);
            }
        });
    }

    /**
     * 请求第一屏数据
     */
    public void getRequestPagerData() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESHINT);
                mSwipeRefreshLayout.setRefreshing(true);
                //获取首页数据
                getFirstData();
            }
        });
    }

    private void getFirstData() {
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
                    paramsMap.put("tranCode","AROUND0008");
                    paramsMap.put("pagerNum",PAGE_SIZE+"");
                    paramsMap.put("currentPager",(firstPage)+"");
                    paramsMap.put("userId", userId);
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    urlParamsEntity.HDCURL=UrlParamsEntity.CURRENT_ID;
                    commentHotProtocol.setUserId(userId);
                    commentHotProtocol.setActionKeyName(loadFirstPage);
                    message.obj = commentHotProtocol.loadData(urlParamsEntity);
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
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT,e.getErrorContent());
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        });
    }


    /********
     * RecyclerView的适配器
     **********/
    class HotAdapter extends LoadMoreRecyclerView.Adapter<HotAdapter.MineHolder> {

        private List<HDC_CommentDetail> list;
        private int ITEM_TYPE_ERROR_CODE=101;//失败条目类型
        private int ITEM_TYPE_EMPTY_CODE=102;//为空的条目类型


        public void setList(List<HDC_CommentDetail> list) {
            this.list = list;
        }

        public HotAdapter() {
        }

        @Override
        public MineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==ITEM_TYPE_ERROR_CODE){
                //如果是错误类型
                return new MineHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.loadpager_error,null),ITEM_TYPE_ERROR_CODE);
            }else if(viewType==ITEM_TYPE_EMPTY_CODE){
                //如果是数据为空类型
                return new MineHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.loadpage_empty,null),ITEM_TYPE_EMPTY_CODE);
            }
            return new MineHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.listview_item_hot_gambit,null));
        }

        @Override
        public void onBindViewHolder(MineHolder holder, int position) {
            if(getItemViewType(position)==ITEM_TYPE_ERROR_CODE){
                //请求失败时
                mSwipeRefreshLayout.setEnabled(false);
                visiableError(holder);
            }else if(getItemViewType(position)==ITEM_TYPE_EMPTY_CODE){
                //数据为空时!
                mSwipeRefreshLayout.setEnabled(true);
            }else{
                mSwipeRefreshLayout.setEnabled(true);
                System.out.println("onBindViewHolder leftWidth:" + leftWidth);
                final HDC_CommentDetail data=list.get(position);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //进行跳转到评论的详情页
                        Intent intent=new Intent(UiUtils.getInstance().getContext(),CommentDetailActivity.class);
                        intent.putExtra(CommentDetailActivity.ITEM_ID,data.getItemId());
                        intent.putExtra(CommentDetailActivity.TOPIC_TYPE,HDCivilizationConstants.HOT_TOPIC_TYPE);
                        intent.putExtra(CommentDetailActivity.ITEMUSER_ID,data.getLaunchUser().getUserId());
                        intent.putExtra(CommentDetailActivity.ITEM_ID_AND_TYPE,data.getItemIdAndType());
                        startActivity(intent);
                    }
                });

//                TextPaint tp = holder.hotTitle.getPaint();
//                tp.setFakeBoldText(true);
                holder.hotTitle.setText(data.getTitle());
                holder.hotContent.setText(data.getContent());

                if(data.getImgUrlList()!=null){
                    int imgUrlSize = data.getImgUrlList().size();
                    System.out.println("onBindViewHolder data.getImgUrlList().size();"+imgUrlSize+"..title:"+data.getTitle());
                    switch (imgUrlSize) {
                        case 0:
                            holder.rl_img_list.setVisibility(View.GONE);
                            holder.hotImgRl.setVisibility(View.GONE);
                            break;
                        case 1:
                            holder.hotImgRl.setVisibility(View.VISIBLE);
                            holder.hotImg1.setVisibility(View.VISIBLE);
                            holder.hotImg2.setVisibility(View.INVISIBLE);
                            holder.hotImg3.setVisibility(View.INVISIBLE);
                            holder.rl_img_list.setVisibility(View.GONE);
                            BitmapUtil.getInstance().displayImg(holder.hotImg1, UrlParamsEntity.WUCHEN_XU_IP_FILE + data.getImgUrlList().get(0).getImgThumbUrl());

                            break;
                        case 2:
                            holder.hotImgRl.setVisibility(View.VISIBLE);
                            holder.hotImg1.setVisibility(View.VISIBLE);
                            holder.hotImg2.setVisibility(View.VISIBLE);
                            holder.hotImg3.setVisibility(View.INVISIBLE);
                            holder.rl_img_list.setVisibility(View.GONE);
                            BitmapUtil.getInstance().displayImg(holder.hotImg1, UrlParamsEntity.WUCHEN_XU_IP_FILE + data.getImgUrlList().get(0).getImgThumbUrl());
                            BitmapUtil.getInstance().displayImg(holder.hotImg2, UrlParamsEntity.WUCHEN_XU_IP_FILE + data.getImgUrlList().get(1).getImgThumbUrl());
                            break;
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                            holder.hotImgRl.setVisibility(View.VISIBLE);
                            holder.hotImg1.setVisibility(View.VISIBLE);
                            holder.hotImg2.setVisibility(View.VISIBLE);
                            holder.hotImg3.setVisibility(View.VISIBLE);
                            if (data.getImgUrlList().size()>3){
                                holder.rl_img_list.setVisibility(View.VISIBLE);
                                holder.tv_img_list_number.setText(data.getImgUrlList().size()+"");
                            }else{
                                holder.rl_img_list.setVisibility(View.GONE);
                            }
                            BitmapUtil.getInstance().displayImg(holder.hotImg1, UrlParamsEntity.WUCHEN_XU_IP_FILE + data.getImgUrlList().get(0).getImgThumbUrl());
                            BitmapUtil.getInstance().displayImg(holder.hotImg2, UrlParamsEntity.WUCHEN_XU_IP_FILE + data.getImgUrlList().get(1).getImgThumbUrl());
                            BitmapUtil.getInstance().displayImg(holder.hotImg3, UrlParamsEntity.WUCHEN_XU_IP_FILE + data.getImgUrlList().get(2).getImgThumbUrl());


                            break;
                    }
                }else{
                    holder.hotImgRl.setVisibility(View.GONE);
                    holder.rl_img_list.setVisibility(View.GONE);
                }

                /*******此处需判断是否是当天的消息，如是当天，则显示当天的发布时间（未做判断）********/
                holder.hotTime.setText(data.getPublishTime());
                holder.hotUserType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UiUtils.getInstance().getContext(), CommentUserInfoActivity.class);
                        intent.putExtra(CommentUserInfoActivity.USER_ID_KEY, data.getLaunchUser().getUserId());
                        startActivity(intent);
                    }
                });
                holder.hotUserName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(UiUtils.getInstance().getContext(), CommentUserInfoActivity.class);
                        intent.putExtra(CommentUserInfoActivity.USER_ID_KEY,data.getLaunchUser().getUserId());
                        startActivity(intent);
                    }
                });

                holder.commentCount.setText(data.getCommentCount()+"");
                //进行显示置顶的标识:
                if(data.isTopType()){
                    holder.hotTop.setVisibility(View.VISIBLE);
                }else{
                    holder.hotTop.setVisibility(View.GONE);
                }

                if(data.getLaunchUser().getIdentityState().equals(UserPermisson.VOLUNTEER.getType())){
                    holder.hotUserType.setText(HDCivilizationConstants.IDENTITY_VOLUNTEER);
                }else{
                    holder.hotUserType.setText(HDCivilizationConstants.IDENTITY_ORDINARY);
                }

                String nickName="";
                if(data.getLaunchUser().getNickName().matches("^[1][3,4,5,8,7][0-9]{9}$")){
                    nickName=data.getLaunchUser().getNickName().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
                }else{
                    nickName=data.getLaunchUser().getNickName();
                }
                holder.hotUserName.setText(nickName);
                holder.hotZambia.setText(data.getCount()+"");

                //判断消息提醒个数是否大于0
                if(data.getTypeCount()>0){
                    holder.view_red_number.setVisibility(View.VISIBLE);
                }else{
                    holder.view_red_number.setVisibility(View.GONE);
                }
            }
        }

        /**
         * 进行显示错误页面
         *  //错误时候的view
         Button button;//按钮
         ProgressBar pb_load;
         ImageView page_iv;
         loading_txt
         * @param mineHodler
         */
        private void visiableError(MineHolder mineHodler) {
            mineHodler.button.setVisibility(View.VISIBLE);
            mineHodler.page_iv.setVisibility(View.VISIBLE);
            mineHodler.pb_load.setVisibility(View.GONE);
            mineHodler.loading_txt.setVisibility(View.GONE);
        }


        @Override
        public int getItemViewType(int position) {
            System.out.println("getItemViewType position:"+position+"...datas==null:"+(list==null));
            if(position==0 && list==null){
                return ITEM_TYPE_ERROR_CODE;
            }else if(position==0 && list.size()==0){
                return ITEM_TYPE_EMPTY_CODE;
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            if(list ==null || list.size()==0){
                return 1;
            }
            return list.size();
        }

        class MineHolder extends LoadMoreRecyclerView.ViewHolder {

            private TextView hotTitle, hotContent, hotUserType, hotUserName, hotZambia, hotTime;
            private ImageView hotTop, hotImg1, hotImg2, hotImg3;
            private LinearLayout hotImgRl;
            private RelativeLayout rl_img_list;
            private  TextView tv_img_list_number,commentCount;
            private View view_red_number;
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
             * 进行初始化界面
             * @param itemView
             */
            private void initView(View itemView) {
                if(viewType==ITEM_TYPE_ERROR_CODE){
                    //错误的处理
                    loadpage_error=(LinearLayout)itemView.findViewById(R.id.loadpage_error);
                    loading_txt=(TextView)itemView.findViewById(R.id.loading_txt);
                    page_iv=(ImageView)itemView.findViewById(R.id.page_iv);
                    pb_load=(ProgressBar)itemView.findViewById(R.id.pb_load);
                    System.out.println("ITEM_TYPE_ERROR_CODE initRecycleViewHeight:"+initRecycleViewHeight);
                    loadpage_error.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, initRecycleViewHeight));
                    loadpage_error.requestLayout();
                    button=(Button)itemView.findViewById(R.id.page_bt);
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
                    hotTitle = (TextView) itemView.findViewById(R.id.tv_hot_title);
                    hotContent = (TextView) itemView.findViewById(R.id.tv_hot_content);
                    hotTop = (ImageView) itemView.findViewById(R.id.hot_top_img);
                    hotImgRl = (LinearLayout) itemView.findViewById(R.id.ll_img_list);

                    int width=(int)Math.round((double)leftWidth/3.0);
                    hotImg1 = (ImageView) itemView.findViewById(R.id.hot_img_1);
                    hotImg1.getLayoutParams().height=Math.round(width* MaskView.heghtDividWidthRate);
                    hotImg1.requestLayout();
                    hotImg2 = (ImageView) itemView.findViewById(R.id.hot_img_2);
                    hotImg2.getLayoutParams().height=Math.round(width* MaskView.heghtDividWidthRate);
                    hotImg2.requestLayout();
                    hotImg3 = (ImageView) itemView.findViewById(R.id.hot_img_3);
                    hotImg3.getLayoutParams().height=Math.round(width* MaskView.heghtDividWidthRate);
                    hotImg3.requestLayout();
                    hotUserType = (TextView) itemView.findViewById(R.id.hot_user_id_type);
                    hotUserName = (TextView) itemView.findViewById(R.id.hot_user_name);
                    hotZambia = (TextView) itemView.findViewById(R.id.hot_gambit_zambia);
                    hotTime = (TextView) itemView.findViewById(R.id.hot_time);
                    commentCount = (TextView)itemView.findViewById(R.id.hot_gambit_comment_nummber);

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
     *
     * @return boolean
     */
    public boolean getNetWorkTime(){
//        System.out.println("(System.currentTimeMillis() - commentHotProtocol.getNetWorktime()):"+(System.currentTimeMillis() - commentHotProtocol.getNetWorktime()) );
        boolean trueOrFalse = (System.currentTimeMillis() - commentHotProtocol.getNetWorktime()) > HDCivilizationConstants.NET_GAP_TIME;
        return trueOrFalse;
    }

    public boolean isRequestintNetwork(){
//        System.out.println("isRequestintNetwork mSwipeRefreshLayout.getRefreshState()==SimpleSwipeRefreshLayout.SWIPE_REFRESHINT:"+//
//                                                                        (mSwipeRefreshLayout.getRefreshState()==SimpleSwipeRefreshLayout.SWIPE_REFRESHINT));
//        System.out.println("recyclerView.getLoadMoreState()==LoadMoreRecyclerView.LOAD_MORE_ING:"+//
//                                                                                (recyclerView.getLoadMoreState()==LoadMoreRecyclerView.LOAD_MORE_ING));
        if(mSwipeRefreshLayout.getRefreshState()==SimpleSwipeRefreshLayout.SWIPE_REFRESHINT ||
                                recyclerView.getLoadMoreState()==LoadMoreRecyclerView.LOAD_MORE_ING){
            //正在访问网络
            return true;
        }
        //没有正在访问网络
        return false;
    }

}

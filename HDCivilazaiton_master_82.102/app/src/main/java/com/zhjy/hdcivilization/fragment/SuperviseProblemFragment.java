package com.zhjy.hdcivilization.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
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
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.SuperviseMySubListDao;
import com.zhjy.hdcivilization.dao.SuperviseProblemListDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_SuperviseMySubList;
import com.zhjy.hdcivilization.entity.HDC_SuperviseProblemList;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.protocol.GetVolunteerInfoProtocol;
import com.zhjy.hdcivilization.protocol.SuperviseProblemListProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.LoadMoreRecyclerView;
import com.zhjy.hdcivilization.view.SimpleSwipeRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 问题统计的Fragment
 */
public class SuperviseProblemFragment extends Fragment {

    private View contentView;
    private SimpleSwipeRefreshLayout simple_swipe_refresh_layout;
    private LoadMoreRecyclerView recyclerView;
    private String keyName="问题统计";
    private String refershName="刷新问题统计信息失败";
    private String loadmoreName="加载更多问题统计信息失败";
    private String loadFirstPage="加载问题统计信息失败";
    private SuperviseProblemListProtocol superviseProblemListProtocol=new SuperviseProblemListProtocol();
    private List<HDC_SuperviseProblemList> datas=new ArrayList<HDC_SuperviseProblemList>();
    private SuperviseMineProblemAdapter superviseProblemListAdapter;
    private LinearLayoutManager linearLayoutManager;

    private final int PAGE_SIZE=12;//固定个数
    private int firstPage=1;//第一页
    private int currentPage=firstPage;//当前的页码

    final int getVolunteerInfoSuccess=208;//进行获取志愿者信息成功码
    final int getVolunteerInfoFailure=209;//进行获取志愿者信息失败码

    final int requestType=-1;//请求类型:0请求首页,1:刷新首页
    final int volunteerRequestFirstPage=0;//请求首页
    final int volunteerRefresh=1;//刷新首页
    final String REQUEST_TYPE_KEY="REQUEST_TYPE_KEY";//键的名称

    private int initRecycleViewHeight;

    public SuperviseProblemFragment() {
    }

    /**
     * 进行初始化对象
     */
    private Handler handler=new Handler(){

        //进行处理消息
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(superviseProblemListAdapter ==null){
                synchronized (SuperviseMineListFragment.class) {
                    if (superviseProblemListAdapter ==null) {
                        superviseProblemListAdapter =new SuperviseMineProblemAdapter(datas);
                        recyclerView.setAdapter(superviseProblemListAdapter);
                    }
                }
            }
            switch (msg.what) {
                case HDCivilizationConstants.REQUEST_FIRST_PAGE:
                    //进行处理
                    datas=(List<HDC_SuperviseProblemList>)msg.obj;
                    if(datas.size()<=0){
                        //提示用户为空
                        UiUtils.getInstance().showToast("您尚未提报!");
                        //以后还可能要进行数据库操作
                        superviseProblemListAdapter.setDatas(datas);
                        //增加判断
                        if(recyclerView==null){
                            contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_hot_gambit, null);
                            initView(contentView);
                            recyclerView.setAdapter(superviseProblemListAdapter);
                        }else{
                            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        }
                    }else{
//                        UiUtils.getInstance().showToast(keyName + "请求首页数据成功!");
                        superviseProblemListAdapter.setDatas(datas);
                        //增加判断
                        if(recyclerView==null){
                            contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_hot_gambit, null);
                            initView(contentView);
                            recyclerView.setAdapter(superviseProblemListAdapter);
                        }else{
                            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        }
                    }
                    currentPage=firstPage;
                    simple_swipe_refresh_layout.setRefreshing(false);
                    simple_swipe_refresh_layout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                    break;
                case  HDCivilizationConstants.REFRESH_PAGE:
                    //进行处理
                    datas=(List<HDC_SuperviseProblemList>)msg.obj;
                    if(datas.size()<=0){
                        //提示用户为空
                        superviseProblemListAdapter.setDatas(datas);
                        //增加判断
                        if(recyclerView==null){
                            contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_hot_gambit, null);
                            initView(contentView);
                            recyclerView.setAdapter(superviseProblemListAdapter);
                        }else{
                            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        }
                        UiUtils.getInstance().showToast("您尚未提报!");
                    }else{
                        superviseProblemListAdapter.setDatas(datas);
                        //增加判断
                        if(recyclerView==null){
                            contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_hot_gambit, null);
                            initView(contentView);
                            recyclerView.setAdapter(superviseProblemListAdapter);
                        }else{
                            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        }
//                        UiUtils.getInstance().showToast(keyName + "刷新首页数据成功!");
                    }
                    currentPage=firstPage;
                    simple_swipe_refresh_layout.setRefreshing(false);
                    simple_swipe_refresh_layout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                    break;

                case HDCivilizationConstants.LOAD_MORE:
                    if (((List<HDC_SuperviseMySubList>)msg.obj).size()<=0){
                        UiUtils.getInstance().showToast("沒有更多问题统计信息了!");
                    }else{
//                        UiUtils.getInstance().showToast(keyName + "加载更多数据成功");
                        datas.addAll((List<HDC_SuperviseProblemList>) msg.obj);
                        superviseProblemListAdapter.setDatas(datas);
                        //增加判断
                        if(recyclerView==null){
                            contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_hot_gambit, null);
                            initView(contentView);
                            recyclerView.setAdapter(superviseProblemListAdapter);
                        }else{
                            if(recyclerView.getmAutoLoadAdapter()==null){
                                superviseProblemListAdapter =new SuperviseMineProblemAdapter(datas);
                                recyclerView.setAdapter(superviseProblemListAdapter);
                            }else{
                                recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                            }
                        }
                    }
                    currentPage+=1;
                    linearLayoutManager.scrollToPosition(datas.size()-1);
                    recyclerView.notifyMoreFinish(true);
                    break;

                case HDCivilizationConstants.ERROR_CODE:
                    UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                    if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)==HDCivilizationConstants.LOAD_MORE){
                        //增加判断
                        if(recyclerView==null){
                            contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_hot_gambit, null);
                            initView(contentView);
                            recyclerView.setAdapter(superviseProblemListAdapter);
                            recyclerView.notifyMoreFinish(true);
                        }else{
                            recyclerView.notifyMoreFinish(true);
                        }
                        linearLayoutManager.scrollToPosition(datas.size()-1);
                        //recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                    }else if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)==HDCivilizationConstants.REFRESH_PAGE){
                        simple_swipe_refresh_layout.setRefreshing(false);
                        simple_swipe_refresh_layout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                        if(superviseProblemListAdapter.datas==null || superviseProblemListAdapter.datas.size()==0){
                            superviseProblemListAdapter.datas= null;
                            //增加判断
                            if(recyclerView==null){
                                contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_hot_gambit, null);
                                initView(contentView);
                                recyclerView.setAdapter(superviseProblemListAdapter);
                            }else{
                                recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                            }
                        }
                    }else if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)==HDCivilizationConstants.REQUEST_FIRST_PAGE){
                        simple_swipe_refresh_layout.setRefreshing(false);
                        simple_swipe_refresh_layout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                        if(superviseProblemListAdapter.datas==null || superviseProblemListAdapter.datas.size()==0){
                            superviseProblemListAdapter.datas= null;
                            //增加判断
                            if(recyclerView==null){
                                contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_hot_gambit, null);
                                initView(contentView);
                                recyclerView.setAdapter(superviseProblemListAdapter);
                            }else{
                                recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                            }
                        }
                    }
                    break;

                case getVolunteerInfoSuccess://请求志愿者信息成功!
                    int requestType=msg.getData().getInt(REQUEST_TYPE_KEY);
                    if(requestType==volunteerRequestFirstPage){
                        //请求第一页数据
                        requestFirstPageData();
                    }else if(requestType==volunteerRefresh){
                        //刷新首页数据
                        refreshPageData();
                    }
                    break;

                case getVolunteerInfoFailure://请求志愿者信息失败!
                    requestType=msg.getData().getInt(REQUEST_TYPE_KEY);
                    if(requestType==volunteerRequestFirstPage){
                        //请求第一页数据
                        UiUtils.getInstance().showToast(keyName+"首页数据加载失败!");
                    }else if(requestType==volunteerRefresh){
                        //刷新首页数据
                        UiUtils.getInstance().showToast(keyName + "刷新首页失败!");
                    }
                    break;
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //进行初始化
        refreshView();
        //进行初始化事件
        initEvent();
    }

    /**
     * 进行初始化
     */
    private void refreshView() {
        if(contentView==null){
            contentView= View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_supervise_mine_list, null);
            contentView.setBackgroundDrawable(getResources().getDrawable(R.drawable.supervise_mine_list_selector));
        }
        initView(contentView);

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                initRecycleViewHeight = recyclerView.getMeasuredHeight();
            }
        });
        //进行从数据库中获取数据
        try {
            User user= UserDao.getInstance().getLocalUser();
            //进行获取用户的id
            String userId=user.getUserId();
            datas= SuperviseProblemListDao.getInstance().getListBy(userId);
            if(datas!=null && datas.size()>0){
                if(superviseProblemListAdapter==null){
                    superviseProblemListAdapter=new SuperviseMineProblemAdapter(datas);
                    superviseProblemListAdapter.setDatas(datas);
                    recyclerView.setAdapter(superviseProblemListAdapter);
                }else{
                    recyclerView.setAdapter(superviseProblemListAdapter);
                    superviseProblemListAdapter.setDatas(datas);
                    recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                }
            }
        } catch (ContentException e){
            e.printStackTrace();
        }
    }

    private void initView(View contentView) {
        simple_swipe_refresh_layout=(SimpleSwipeRefreshLayout) this.contentView.findViewById(R.id.simple_swipe_refresh_layout);
        recyclerView=(LoadMoreRecyclerView) this.contentView.findViewById(R.id.recyclerView);
        simple_swipe_refresh_layout.setChild(recyclerView);
        linearLayoutManager = new LinearLayoutManager(UiUtils.getInstance().getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.switchLayoutManager(new LinearLayoutManager(UiUtils.getInstance().getContext()));
        recyclerView.setPageSize(PAGE_SIZE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //进行初始化时间
        superviseProblemListProtocol.setNetWorktime(System.currentTimeMillis() - 2 * HDCivilizationConstants.NET_GAP_TIME);
//        datas= SuperviseProblemListDao.getInstance().getAll();
//        superviseProblemListAdapter =new SuperviseMineProblemAdapter(datas);
//        recyclerView.setAdapter(superviseProblemListAdapter);

        return contentView;
    }

    public void getDataFromInternet(){
        simple_swipe_refresh_layout.post(new Runnable() {
            @Override
            public void run() {
                simple_swipe_refresh_layout.setRefreshing(true);
                simple_swipe_refresh_layout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESHINT);
                //首先需要进行判断:
                try {
                    User user=UserDao.getInstance().getLocalUser();
                    if(user.getIdentityState().equals(UserPermisson.VOLUNTEER.getType()) &&
                            user.getVolunteerId().equals("")){
                        //没有志愿者id 却是志愿者身份
                        getVolunteerInfo(user,volunteerRequestFirstPage);
                    }else{
                        //其他情况
                        requestFirstPageData();
                    }
                } catch (ContentException e) {
                    e.printStackTrace();
                }
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
                                try {
                                    Message message = Message.obtain();
                                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                                    LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                                    paramsMap.put("tranCode", "AROUND0006");
                                    paramsMap.put("volunteerId", UserDao.getInstance().getLocalUser().getVolunteerId());
                                    paramsMap.put("userId", UserDao.getInstance().getLocalUser().getUserId());
                                    paramsMap.put("currentPager", (currentPage + 1) + "");
                                    paramsMap.put("pagerNum", PAGE_SIZE + "");
                                    urlParamsEntity.setParamsHashMap(paramsMap);
                                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                                    superviseProblemListProtocol.setUserId(UserDao.getInstance().getLocalUser().getUserId());
                                    message.what = HDCivilizationConstants.LOAD_MORE;
                                    superviseProblemListProtocol.setActionKeyName(loadmoreName);
                                    message.obj = superviseProblemListProtocol.loadData(urlParamsEntity);
                                    handler.sendMessage(message);
                                } catch (JsonParseException e) {
                                    e.printStackTrace();
                                    Message message = Message.obtain();
                                    message.what = HDCivilizationConstants.ERROR_CODE;
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.LOAD_MORE);
                                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                                    message.setData(bundle);
                                    handler.sendMessage(message);
                                } catch (ContentException e) {
                                    e.printStackTrace();
                                    Message message = Message.obtain();
                                    message.what = HDCivilizationConstants.ERROR_CODE;
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.LOAD_MORE);
                                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
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

    private void requestFirstPageData() {
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Message message = Message.obtain();
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode", "AROUND0006");
                    paramsMap.put("volunteerId", UserDao.getInstance().getLocalUser().getVolunteerId());
//                            paramsMap.put("volunteerId","10007");
                    paramsMap.put("userId", UserDao.getInstance().getLocalUser().getUserId());
//                            paramsMap.put("userId","5");
                    paramsMap.put("currentPager", firstPage + "");
                    paramsMap.put("pagerNum", PAGE_SIZE + "");
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
//                            superviseMineListProtocol.setUserId("5");
                    superviseProblemListProtocol.setUserId(UserDao.getInstance().getLocalUser().getUserId());
                    message.what = HDCivilizationConstants.REQUEST_FIRST_PAGE;
                    superviseProblemListProtocol.setActionKeyName(loadFirstPage);
                    message.obj = superviseProblemListProtocol.loadData(urlParamsEntity);
                    handler.sendMessage(message);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    Bundle bundle = new Bundle();
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REQUEST_FIRST_PAGE);
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT,  e.getMessage());
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (ContentException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    Bundle bundle = new Bundle();
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REQUEST_FIRST_PAGE);
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT,  e.getMessage());
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        });
    }

    /**
     * 进行初始化事件
     */
    private void initEvent(){
        simple_swipe_refresh_layout.setColorSchemeColors(R.color.color1, R.color.color2, R.color.color3, R.color.color4);
        simple_swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                simple_swipe_refresh_layout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESHINT);
                simple_swipe_refresh_layout.setRefreshing(true);
                getFirstData();

            }
        });
    }

    /**
     * 有条件地获取第一页数据
     */
    private void getFirstData() {
        //首先需要进行判断:
        try {
            User user= UserDao.getInstance().getLocalUser();
            if(user.getIdentityState().equals(UserPermisson.VOLUNTEER.getType()) &&
                    user.getVolunteerId().equals("")){
                //没有志愿者id 却是志愿者身份
                getVolunteerInfo(user,volunteerRefresh);
            }else{
                //其他情况
                refreshPageData();
            }
        } catch (ContentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新首页数据
     */
    private void refreshPageData() {
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Message message = Message.obtain();
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode", "AROUND0006");
                    paramsMap.put("volunteerId", UserDao.getInstance().getLocalUser().getVolunteerId());
//                            paramsMap.put("volunteerId","10007");
                    paramsMap.put("userId", UserDao.getInstance().getLocalUser().getUserId());
//                            paramsMap.put("userId","5");
                    paramsMap.put("currentPager", firstPage + "");
                    paramsMap.put("pagerNum", PAGE_SIZE + "");
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
//                            superviseMineListProtocol.setUserId("5");
                    superviseProblemListProtocol.setUserId(UserDao.getInstance().getLocalUser().getUserId());
                    message.what = HDCivilizationConstants.REFRESH_PAGE;
                    superviseProblemListProtocol.setActionKeyName(refershName);
                    message.obj = superviseProblemListProtocol.loadData(urlParamsEntity);
                    handler.sendMessage(message);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    Bundle bundle = new Bundle();
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT,  e.getMessage());
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REFRESH_PAGE);
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (ContentException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    Bundle bundle = new Bundle();
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT,e.getMessage());
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REFRESH_PAGE);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        });
    }

    /**
     * 适配器
     */
    class SuperviseMineProblemAdapter extends LoadMoreRecyclerView.Adapter<SuperviseMineProblemHolder>{

        private List<HDC_SuperviseProblemList> datas;
        private SimpleDateFormat dateFormat=new SimpleDateFormat("MM-dd");
        private int ITEM_TYPE_ERROR_CODE=101;//失败条目类型
        private int ITEM_TYPE_EMPTY_CODE=102;//为空的条目类型

        public SuperviseMineProblemAdapter(List<HDC_SuperviseProblemList> datas) {
            this.datas=datas;
        }

        public List<HDC_SuperviseProblemList> getDatas() {
            return datas;
        }

        public void setDatas(List<HDC_SuperviseProblemList> datas) {
            this.datas = datas;
        }

        public SimpleDateFormat getDateFormat() {
            return dateFormat;
        }

        public void setDateFormat(SimpleDateFormat dateFormat) {
            this.dateFormat = dateFormat;
        }

        @Override
        public SuperviseMineProblemHolder onCreateViewHolder(ViewGroup parent, int viewType){
            if(viewType==ITEM_TYPE_ERROR_CODE){
                //如果是错误类型
                return new SuperviseMineProblemHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.loadpager_error,null),ITEM_TYPE_ERROR_CODE);
            }else if(viewType==ITEM_TYPE_EMPTY_CODE){
                //如果是数据为空类型
                return new SuperviseMineProblemHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.loadpage_empty,null),ITEM_TYPE_EMPTY_CODE);
            }
            return new SuperviseMineProblemHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.listitem_supervise_problem,null));
        }

        /**
         * 进行显示错误页面
         * @param mineHodler
         */
        private void visiableError(SuperviseMineProblemHolder mineHodler) {
            mineHodler.button.setVisibility(View.VISIBLE);
            mineHodler.page_iv.setVisibility(View.VISIBLE);
            mineHodler.pb_load.setVisibility(View.GONE);
            mineHodler.loading_txt.setVisibility(View.GONE);
        }

        @Override
        public void onBindViewHolder(SuperviseMineProblemHolder holder, int position) {
            if(getItemViewType(position)==ITEM_TYPE_ERROR_CODE){
                visiableError(holder);
                simple_swipe_refresh_layout.setEnabled(false);
            }else if(getItemViewType(position)==ITEM_TYPE_EMPTY_CODE){
                //数据为空时!
                simple_swipe_refresh_layout.setEnabled(true);
            }else{
                simple_swipe_refresh_layout.setEnabled(true);
                HDC_SuperviseProblemList data=this.datas.get(position);
                holder.supervise_tv_des.setText("当日上报问题数"+data.getProblemCount()+"个,审批通过"+data.getVerifiedCountPerDay()+"条,获得文明贡献值"+data.getProblemCoin());
                holder.supervise_tv_total.setText("总文明贡献值:"+data.getTotalCoin());
                holder.supervise_tv_time.setText(data.getDate());
                holder.supervise_v_line.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemViewType(int position) {
            System.out.println("getItemViewType position:"+position+"...datas==null:"+(datas==null));
            if(position==0 && datas==null){
                return ITEM_TYPE_ERROR_CODE;
            }else if(position==0 && datas.size()==0){
                return ITEM_TYPE_EMPTY_CODE;
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            if(datas==null || datas.size()==0){
                return 1;
            }
            return datas.size();
        }
    }


    /***
     * 对应的holder类
     */
    class SuperviseMineProblemHolder extends LoadMoreRecyclerView.ViewHolder{
        TextView supervise_tv_des,supervise_tv_time,supervise_tv_total;
        TextView supervise_v_line;

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
        public SuperviseMineProblemHolder(View itemView) {
            super(itemView);
            initView(itemView);
        }

        public SuperviseMineProblemHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            initView(itemView);
        }

        private void initView(final View itemView) {
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
                itemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case  MotionEvent.ACTION_DOWN:
                                itemView.setBackgroundColor(UiUtils.getInstance().getContext().getResources().getColor(R.color.supervise_item_press));
                                break;

                            case MotionEvent.ACTION_UP:
                                itemView.setBackgroundColor(UiUtils.getInstance().getContext().getResources().getColor(R.color.activity_background_color));
                                break;
                        }
                        return false;
                    }
                });
                supervise_tv_des=(TextView)itemView.findViewById(R.id.supervise_tv_des);//描述内容
                supervise_tv_time=(TextView)itemView.findViewById(R.id.supervise_tv_time);//时间
                supervise_tv_total=(TextView)itemView.findViewById(R.id.supervise_tv_total);//总分数
                supervise_v_line=(TextView)itemView.findViewById(R.id.supervise_v_line);//总分数
            }
        }
    }

    /***
     * 判断是否超时
     * @return boolean
     */
    public boolean getNetWorkTime(){
        boolean trueOrFalse = (System.currentTimeMillis() - superviseProblemListProtocol.getNetWorktime()) > HDCivilizationConstants.NET_GAP_TIME;
        return trueOrFalse;
    }

    public boolean isRequestintNetwork(){
        if(simple_swipe_refresh_layout.getRefreshState()==SimpleSwipeRefreshLayout.SWIPE_REFRESHINT ||
                recyclerView.getLoadMoreState()==LoadMoreRecyclerView.LOAD_MORE_ING){
            //正在访问网络
            return true;
        }
        //没有正在访问网络
        return false;
    }


    /**
     * 进行获取
     */
    private void getVolunteerInfo(final User user,final int requestType){
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
                            Message message = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putInt(REQUEST_TYPE_KEY, requestType);
                            message.setData(bundle);
                            GetVolunteerInfoProtocol getVolunteerInfoProtocol = new GetVolunteerInfoProtocol();
                            getVolunteerInfoProtocol.setUserId(user.getUserId());
                            getVolunteerInfoProtocol.setOutUser(user);
                            getVolunteerInfoProtocol.setActionKeyName(loadFirstPage);
                            message.obj = getVolunteerInfoProtocol.parseJson(responseInfo.result);
                            message.what = getVolunteerInfoSuccess;
                            handler.sendMessage(message);
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                            Message message = Message.obtain();
                            message.what = getVolunteerInfoFailure;
                            message.obj = e.getMessage();
                            Bundle bundle = new Bundle();
                            bundle.putInt(REQUEST_TYPE_KEY, requestType);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } catch (ContentException e) {
                            e.printStackTrace();
                            Message message = Message.obtain();
                            message.what = getVolunteerInfoFailure;
                            message.obj = e.getErrorContent();
                            Bundle bundle = new Bundle();
                            bundle.putInt(REQUEST_TYPE_KEY, requestType);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Message message = Message.obtain();
                        message.what = getVolunteerInfoFailure;
                        message.obj = "加载问题统计信息失败!";
                        handler.sendMessage(message);
                        //java.net.SocketTimeoutException
                        System.out.println("uploadImgProtocol  onFailure......" + "error:" + //
                                error.getExceptionCode() + "..getMessage:" + error.getMessage() + "...msg:" + msg);
                    }
                });
    }
}

package com.zhjy.hdcivilization.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.activity.MySuperviseListDetailActivity;
import com.zhjy.hdcivilization.dao.MainNumberDao;
import com.zhjy.hdcivilization.dao.SuperviseMySubListDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_MainNumber;
import com.zhjy.hdcivilization.entity.HDC_SuperviseMySubList;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.protocol.GetVolunteerInfoProtocol;
import com.zhjy.hdcivilization.protocol.SuperviseMineListProtocol;
import com.zhjy.hdcivilization.utils.BitmapUtil;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.LoadMoreRecyclerView;
import com.zhjy.hdcivilization.view.SimpleSwipeRefreshLayout;
import com.zhjy.hdcivilization.view.SuperviseProgressView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 我的提报fragment
 */
public class SuperviseMineListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private String itemIdPush;

    private View contentView;
    private SimpleSwipeRefreshLayout  simple_swipe_refresh_layout;
    private LoadMoreRecyclerView recyclerView;
    private SuperviseMineListAdapter superviseMineListAdapter;
    private String keyName="我的上报";
    private String refershName="刷新提报信息失败";
    private String loadmoreName="加载更多提报信息失败";
    private String loadFirstPage="加载提报信息失败";
    private SuperviseMineListProtocol superviseMineListProtocol=new SuperviseMineListProtocol();
    private List<HDC_SuperviseMySubList> datas=new ArrayList<HDC_SuperviseMySubList>();
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

    private String userId;//用户的id

    private int initRecycleViewHeight;

    public String getItemIdPush() {
        return itemIdPush;
    }

    public void setItemIdPush(String itemIdPush) {
        this.itemIdPush = itemIdPush;
    }

    public SuperviseMineListFragment() {

    }

    public static SuperviseMineListFragment newInstance(String param1, String param2) {
        SuperviseMineListFragment fragment = new SuperviseMineListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * 进行初始化对象
     */
    private Handler handler=new Handler(){
        //进行处理消息
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(superviseMineListAdapter==null) {
                synchronized (SuperviseMineListFragment.this) {
                    if(superviseMineListAdapter==null) {
                        superviseMineListAdapter=new SuperviseMineListAdapter(datas);
                        recyclerView.setAdapter(superviseMineListAdapter);
                    }
                }
            }
            switch (msg.what) {
                case HDCivilizationConstants.REQUEST_FIRST_PAGE:
                    //进行处理 清除数据库中的数据
                    SuperviseMySubListDao.getInstance().clearAll();
                    if(((List<HDC_SuperviseMySubList>) msg.obj).size()<=0){
                        //提示用户为空
                        UiUtils.getInstance().showToast("您尚未提报!");
                        datas.clear();
                        //以后还可能要进行数据库操作
                    }else{
                        SuperviseMySubListDao.getInstance().saveAll((List<HDC_SuperviseMySubList>) msg.obj);
                        datas=(List<HDC_SuperviseMySubList>)msg.obj;
//                        UiUtils.getInstance().showToast(keyName + "请求首页数据成功!");
                        System.out.println("REQUEST_FIRST_PAGE datas size:"+datas.size());
                    }
                    //进行重新初始化文明监督消息提醒个数
                    initSuperviseCount();
                    superviseMineListAdapter.setDatas(datas);
                    //增加判断
                    if(recyclerView==null){
                            contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_hot_gambit, null);
                            initView(contentView);
                            recyclerView.setAdapter(superviseMineListAdapter);
                    }else{
                        if(recyclerView.getmAutoLoadAdapter()==null){
                            superviseMineListAdapter=new SuperviseMineListAdapter(datas);
                            recyclerView.setAdapter(superviseMineListAdapter);
                        }else{
                            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        }
                    }
                    currentPage=firstPage;
                    simple_swipe_refresh_layout.setRefreshing(false);
                    simple_swipe_refresh_layout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                    break;
                case  HDCivilizationConstants.REFRESH_PAGE:
                    //进行处理 清除数据库中的数据
                    SuperviseMySubListDao.getInstance().clearAll();
                    if(((List<HDC_SuperviseMySubList>) msg.obj).size()<=0){
                        //提示用户为空
                        UiUtils.getInstance().showToast("您尚未提报!");
                        datas.clear();
                    }else{
                        SuperviseMySubListDao.getInstance().saveAll((List<HDC_SuperviseMySubList>) msg.obj);
                        datas=(List<HDC_SuperviseMySubList>)msg.obj;
//                        UiUtils.getInstance().showToast(keyName + "刷新首页数据成功!");
                        System.out.println("REFRESH_PAGE datas size:" + datas.size());
                    }
                    superviseMineListAdapter.setDatas(datas);
                    //增加判断
                    if(recyclerView==null){
                        contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_hot_gambit, null);
                        initView(contentView);
                        recyclerView.setAdapter(superviseMineListAdapter);
                    }else{
                        recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                    }
                    currentPage=firstPage;
                    simple_swipe_refresh_layout.setRefreshing(false);
                    simple_swipe_refresh_layout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                    //进行重新初始化文明监督消息提醒个数
                    initSuperviseCount();
                    break;

                case HDCivilizationConstants.LOAD_MORE:
                    if (((List<HDC_SuperviseMySubList>)msg.obj).size()<=0){
                        UiUtils.getInstance().showToast("沒有更多提报信息了!");
                    }else{
                        SuperviseMySubListDao.getInstance().saveAll((List<HDC_SuperviseMySubList>) msg.obj);
//                        UiUtils.getInstance().showToast(keyName + "加载更多数据成功");
                        int firstSize=datas.size();
                        datas.addAll((List<HDC_SuperviseMySubList>) msg.obj);
                        superviseMineListAdapter.setDatas(datas);
                        //增加判断
                        if(recyclerView==null){
                            contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_hot_gambit, null);
                            initView(contentView);
                            recyclerView.setAdapter(superviseMineListAdapter);
                        }else{
                            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        }
//                        linearLayoutManager.scrollToPosition(datas.size() - 1);
                        linearLayoutManager.scrollToPosition(firstSize - 1);
                        currentPage=currentPage+1;
                    }
                    //进行重新初始化文明监督消息提醒个数
                    initSuperviseCount();
                    recyclerView.notifyMoreFinish(true);
                    break;

                case HDCivilizationConstants.ERROR_CODE:
                    UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                    if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)==HDCivilizationConstants.LOAD_MORE){
//                        linearLayoutManager.scrollToPosition(datas.size() - 1);
                        //增加判断
                        if(recyclerView==null){
                            contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_hot_gambit, null);
                            initView(contentView);
                            recyclerView.setAdapter(superviseMineListAdapter);
                            recyclerView.notifyMoreFinish(true);
                        }else{
                            recyclerView.notifyMoreFinish(true);
                        }
                    }else if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)==HDCivilizationConstants.REFRESH_PAGE){
                        simple_swipe_refresh_layout.setRefreshing(false);
                        simple_swipe_refresh_layout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);
                        if(superviseMineListAdapter.datas==null || superviseMineListAdapter.datas.size()==0){
                            superviseMineListAdapter.datas= null;
                            //增加判断
                            if(recyclerView==null){
                                contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_hot_gambit, null);
                                initView(contentView);
                                recyclerView.setAdapter(superviseMineListAdapter);
                            }else{
                                recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                            }
                        }

                    }else if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)==HDCivilizationConstants.REQUEST_FIRST_PAGE){
                        simple_swipe_refresh_layout.setRefreshing(false);
                        simple_swipe_refresh_layout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESH_FINISHED);

                        if(superviseMineListAdapter.datas==null || superviseMineListAdapter.datas.size()==0){
                            superviseMineListAdapter.datas= null;
                            //增加判断
                            if(recyclerView==null){
                                contentView = View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_hot_gambit, null);
                                initView(contentView);
                                recyclerView.setAdapter(superviseMineListAdapter);
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
                        UiUtils.getInstance().showToast(loadFirstPage+"!");
                    }else if(requestType==volunteerRefresh){
                        //刷新首页数据
                        UiUtils.getInstance().showToast(refershName+"!");
                    }
                    break;
            }
        }
    };

    private void initSuperviseCount() {
        try {
            HDC_MainNumber hdc_mainNumber= MainNumberDao.getInstance().getNumberBy(userId);
            hdc_mainNumber.setSuperviseCount(0);//将文明监督进行消失掉
            MainNumberDao.getInstance().saveNumber(hdc_mainNumber);
        } catch (ContentException e) {
            e.printStackTrace();
            HDC_MainNumber hdc_mainNumber=new HDC_MainNumber();//进行新建对象
            hdc_mainNumber.setSuperviseCount(0);
            MainNumberDao.getInstance().saveNumber(hdc_mainNumber);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    private void refreshView() {
        contentView= View.inflate(UiUtils.getInstance().getContext(), R.layout.fragment_supervise_mine_list, null);
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
            userId=user.getUserId();
            datas= SuperviseMySubListDao.getInstance().getListBy(userId);
            if(datas!=null && datas.size()>0){
                if(superviseMineListAdapter==null){
                    superviseMineListAdapter=new SuperviseMineListAdapter(datas);
                    superviseMineListAdapter.setDatas(datas);
                    recyclerView.setAdapter(superviseMineListAdapter);
                }else{
                    superviseMineListAdapter.setDatas(datas);
                    recyclerView.setAdapter(superviseMineListAdapter);
                    recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                }
            }
        } catch (ContentException e){
            e.printStackTrace();
        }
    }

    private void initView(View contentView) {
        simple_swipe_refresh_layout=(SimpleSwipeRefreshLayout)contentView.findViewById(R.id.simple_swipe_refresh_layout);
        recyclerView=(LoadMoreRecyclerView)contentView.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(UiUtils.getInstance().getContext());
        simple_swipe_refresh_layout.setChild(recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.switchLayoutManager(new LinearLayoutManager(UiUtils.getInstance().getContext()));
        recyclerView.setPageSize(PAGE_SIZE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        refreshView();
        //初始化事件
        initEvent();
        //进行第一次访问网络
        getDataFromInternet();

        //进行初始化时间
        superviseMineListProtocol.setNetWorktime(System.currentTimeMillis());
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }



    public void getDataFromInternet(){
        simple_swipe_refresh_layout.post(new Runnable() {
            @Override
            public void run() {
                simple_swipe_refresh_layout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESHINT);
                simple_swipe_refresh_layout.setRefreshing(true);
                getFirstData();
            }
        });

        recyclerView.setAutoLoadMoreEnable(true);
        recyclerView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * 刷新首页数据
                         */
                        loadMoreData();
                    }
                },1000);
            }
        });
    }

    /**
     * 有条件的获取第一页数据
     */
    private void getFirstData() {
        //首先需要进行判断:
        try {
            User user= UserDao.getInstance().getLocalUser();
            //进行获取用户的id
            userId=user.getUserId();
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

    /**
     * 刷新首页数据
     */
    private void loadMoreData() {
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    User user=UserDao.getInstance().getLocalUser();
                    Message message = Message.obtain();
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String,String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode","AROUND0005");
                    paramsMap.put("volunteerId", user.getVolunteerId());
                    paramsMap.put("userId",user.getUserId());
                    paramsMap.put("currentPager",(currentPage+1)+"");
                    paramsMap.put("pagerNum", PAGE_SIZE + "");
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    urlParamsEntity.HDCURL=UrlParamsEntity.CURRENT_ID;
                    superviseMineListProtocol.setUserId(user.getUserId());
                    superviseMineListProtocol.setActionKeyName(loadmoreName);
                    message.what = HDCivilizationConstants.LOAD_MORE;
                    message.obj = superviseMineListProtocol.loadData(urlParamsEntity);
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

    /**
     * 请求第一页数据
     */
    private void requestFirstPageData() {
        //未登录:
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Message message = Message.obtain();
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String,String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode","AROUND0005");
                    paramsMap.put("volunteerId", UserDao.getInstance().getLocalUser().getVolunteerId());
//                                  paramsMap.put("volunteerId","10007");
                    paramsMap.put("userId",UserDao.getInstance().getLocalUser().getUserId());
//                                  paramsMap.put("userId","5");
                    paramsMap.put("currentPager",firstPage+"");
                    paramsMap.put("pagerNum", PAGE_SIZE + "");
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    urlParamsEntity.HDCURL=UrlParamsEntity.CURRENT_ID;
//                                  superviseMineListProtocol.setUserId("5");
                    superviseMineListProtocol.setUserId(UserDao.getInstance().getLocalUser().getUserId());
                    superviseMineListProtocol.setActionKeyName(loadFirstPage);
                    message.what = HDCivilizationConstants.REQUEST_FIRST_PAGE;
                    message.obj = superviseMineListProtocol.loadData(urlParamsEntity);
                    handler.sendMessage(message);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    Bundle bundle = new Bundle();
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REQUEST_FIRST_PAGE);
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (ContentException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    Bundle bundle = new Bundle();
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REQUEST_FIRST_PAGE);
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
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
                simple_swipe_refresh_layout.setRefreshing(true);
                simple_swipe_refresh_layout.setRefreshState(SimpleSwipeRefreshLayout.SWIPE_REFRESHINT);
                //首先需要进行判断:
                try {
                    User user=UserDao.getInstance().getLocalUser();
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
        });
    }

    private void refreshPageData() {
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Message message = Message.obtain();
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String,String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode","AROUND0005");
                    paramsMap.put("volunteerId", UserDao.getInstance().getLocalUser().getVolunteerId());
//                            paramsMap.put("volunteerId","10007");
                    paramsMap.put("userId",UserDao.getInstance().getLocalUser().getUserId());
//                            paramsMap.put("userId","5");
                    paramsMap.put("currentPager",firstPage+"");
                    paramsMap.put("pagerNum", PAGE_SIZE + "");
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    urlParamsEntity.HDCURL=UrlParamsEntity.CURRENT_ID;
//                            superviseMineListProtocol.setUserId("5");
                    superviseMineListProtocol.setUserId(UserDao.getInstance().getLocalUser().getUserId());
                    message.what = HDCivilizationConstants.REFRESH_PAGE;
                    superviseMineListProtocol.setActionKeyName(refershName);
                    message.obj = superviseMineListProtocol.loadData(urlParamsEntity);
                    handler.sendMessage(message);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    Bundle bundle = new Bundle();
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REFRESH_PAGE);
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (ContentException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    Bundle bundle = new Bundle();
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REFRESH_PAGE);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    class SuperviseMineListAdapter extends RecyclerView.Adapter<SuperviseMineListHolder>{

        private List<HDC_SuperviseMySubList> datas;

        private int ITEM_TYPE_ERROR_CODE=101;//失败条目类型
        private int ITEM_TYPE_EMPTY_CODE=102;//为空的条目类型


        public SuperviseMineListAdapter(List<HDC_SuperviseMySubList> datas) {
            this.datas=datas;
        }

        public List<HDC_SuperviseMySubList> getDatas() {
            return datas;
        }

        public void setDatas(List<HDC_SuperviseMySubList> datas) {
            this.datas = datas;
        }

//        @Override
//        public int getItemViewType(int position) {
//            return Integer.parseInt((datas.get(position).getProcessState()));
//        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onViewRecycled(SuperviseMineListHolder holder) {
            super.onViewRecycled(holder);
            System.out.print("onViewRecycled ..........");
        }

        @Override
        public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            super.registerAdapterDataObserver(observer);
            System.out.print("registerAdapterDataObserver ..........");
        }

        @Override
        public SuperviseMineListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==ITEM_TYPE_ERROR_CODE){
                //如果是错误类型
                return new SuperviseMineListHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.loadpager_error,null),ITEM_TYPE_ERROR_CODE);
            }else if(viewType==ITEM_TYPE_EMPTY_CODE){
                //如果是数据为空类型
                return new SuperviseMineListHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.loadpage_empty,null),ITEM_TYPE_EMPTY_CODE);
            }
            return new SuperviseMineListHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.listitem_supervise_mysubdata,null));
        }

        /**
         * 进行显示错误页面
         * @param mineHodler
         */
        private void visiableError(SuperviseMineListHolder mineHodler) {
            mineHodler.button.setVisibility(View.VISIBLE);
            mineHodler.page_iv.setVisibility(View.VISIBLE);
            mineHodler.pb_load.setVisibility(View.GONE);
            mineHodler.loading_txt.setVisibility(View.GONE);
        }

        @Override
        public void onBindViewHolder(SuperviseMineListHolder holder,int position) {
            if(getItemViewType(position)==ITEM_TYPE_ERROR_CODE){
                simple_swipe_refresh_layout.setEnabled(false);
                visiableError(holder);
            }else if(getItemViewType(position)==ITEM_TYPE_EMPTY_CODE){
                //数据为空时!
                simple_swipe_refresh_layout.setEnabled(true);
            }else{
                holder.itemView.setOnClickListener(new MyOnClickListener(position) {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(UiUtils.getInstance().getContext(), MySuperviseListDetailActivity.class);
                        intent.putExtra(MySuperviseListDetailActivity.ITEM_ID_KEY,datas.get(this.position).getItemId());
                        getActivity().startActivity(intent);
                    }
                });

                System.out.println("hdc_superviseMySubList.getItemIdAndType():" +datas.get(position).getItemIdAndType());
                simple_swipe_refresh_layout.setEnabled(true);
                //进行数据的绑定
                if(datas.get(position).getImgEntity()!=null && datas.get(position).getImgEntity().size()>0){
                    BitmapUtil.getInstance().displayImg(holder.iv_supervise_mysub, datas.get(position).getImgEntity().get(0).getImgThumbUrl());
                }else{
                    BitmapUtil.getInstance().displayImg(holder.iv_supervise_mysub,"");
                }

                holder.supervise_tv_time.setText(datas.get(position).getPublishTime());
                holder.supervise_tv_title.setText(datas.get(position).getAddress());
                holder.supervise_tv_content.setText(datas.get(position).getDescription());

                holder.supervise_v_line.setVisibility(View.VISIBLE);
                //进行根据类型进行处理
                /*int viewType=getItemViewType(position);*/
                int viewType= 0;
                viewType = getItemViewType(position);
                if(viewType>=HDCivilizationConstants.SUBMIT_TASK_STATUS_0 && //
                                                viewType<=HDCivilizationConstants.SUBMIT_TASK_STATUS_5){
                    holder.supervise_rl_2.setVisibility(View.VISIBLE);
                    holder.supervise_rl_2.setType(viewType);
                }else{
                    holder.supervise_rl_2.setVisibility(View.VISIBLE);
                    viewType=HDCivilizationConstants.SUBMIT_TASK_STATUS_DEFAULT;
                    holder.supervise_rl_2.setType(viewType);
                }

                //进行绑定TextView
                holder.supervise_rl_2.setTextViewStatus1(holder.supervise_tv_content_1);
                holder.supervise_rl_2.setTextViewStatus2(holder.supervise_tv_content_2);
                holder.supervise_rl_2.setTextViewStatus3(holder.supervise_tv_content_3);
                int widthSpeec=View.MeasureSpec.makeMeasureSpec(4000, View.MeasureSpec.AT_MOST);
                int heightSpeec=View.MeasureSpec.makeMeasureSpec(4000, View.MeasureSpec.AT_MOST);
                holder.supervise_rl_2.measure(widthSpeec,heightSpeec);
                holder.supervise_rl_2.requestLayout();

                //进行背景的赋值:
                holder.supervise_iv_progress_1.setImageDrawable(UiUtils.getInstance().getDrawable(R.drawable.supervise_icon_progress_press));
                holder.supervise_iv_progress_2.setImageDrawable(UiUtils.getInstance().getDrawable(R.drawable.supervise_icon_progress_press));
                holder.supervise_iv_progress_3.setImageDrawable(UiUtils.getInstance().getDrawable(R.drawable.supervise_icon_progress_press));

                //进行颜色的赋值
                holder.supervise_tv_content_1.setTextColor(UiUtils.getInstance().//
                        getContext().getResources().getColor(R.color.supervise_progress_color_press));
                holder.supervise_tv_content_2.setTextColor(UiUtils.getInstance().//
                        getContext().getResources().getColor(R.color.supervise_progress_color_press));
                holder.supervise_tv_content_3.setTextColor(UiUtils.getInstance().//
                        getContext().getResources().getColor(R.color.supervise_progress_color_press));

                //进度横线的颜色的赋值
                holder.supervise_tv_progress_1.setBackgroundColor(UiUtils.getInstance().//
                        getContext().getResources().getColor(R.color.supervise_progress_color_press));
                holder.supervise_tv_progress_2.setBackgroundColor(UiUtils.getInstance().//
                        getContext().getResources().getColor(R.color.supervise_progress_color_press));

                switch (viewType) {
                    case HDCivilizationConstants.SUBMIT_TASK_STATUS_0://未接受 已上报
                    case HDCivilizationConstants.SUBMIT_TASK_STATUS_3:

                        holder.rl_progress3.setVisibility(View.INVISIBLE);
                        holder.rl_progress1.setVisibility(View.VISIBLE);
                        holder.rl_progress2.setVisibility(View.VISIBLE);

                        holder.supervise_tv_progress_1.setVisibility(View.VISIBLE);
                        holder.supervise_tv_progress_2.setVisibility(View.INVISIBLE);
                        break;

                    case HDCivilizationConstants.SUBMIT_TASK_STATUS_1://通过  受理
                    case HDCivilizationConstants.SUBMIT_TASK_STATUS_2://未通过 未受理
                    case HDCivilizationConstants.SUBMIT_TASK_STATUS_4://复核通过 完成
                    case HDCivilizationConstants.SUBMIT_TASK_STATUS_5:

                        holder.rl_progress3.setVisibility(View.VISIBLE);
                        holder.rl_progress1.setVisibility(View.VISIBLE);
                        holder.rl_progress2.setVisibility(View.VISIBLE);


                        holder.supervise_tv_progress_1.setVisibility(View.VISIBLE);
                        holder.supervise_tv_progress_2.setVisibility(View.VISIBLE);
                        break;

                    default :
                        //进行缺省值的判断:
                        holder.rl_progress3.setVisibility(View.INVISIBLE);
                        holder.rl_progress1.setVisibility(View.VISIBLE);
                        holder.rl_progress2.setVisibility(View.INVISIBLE);

                        holder.supervise_tv_progress_1.setVisibility(View.INVISIBLE);
                        holder.supervise_tv_progress_2.setVisibility(View.INVISIBLE);
                        break;
                }
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
            if(position>=datas.size()){
                return -1;
            }else{
                int type=1;
                try {
                    type=Integer.parseInt((datas.get(position).getProcessState()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    type=HDCivilizationConstants.SUBMIT_TASK_STATUS_DEFAULT;
                }
                return type;
            }
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
    class SuperviseMineListHolder extends RecyclerView.ViewHolder{

        ImageView iv_supervise_mysub,//图片
                 supervise_iv_progress_1,supervise_iv_progress_2,//颜色进度
                supervise_iv_progress_3;//图片
        TextView supervise_tv_title, supervise_tv_content,//内容和标题
                supervise_tv_time,//时间
                supervise_tv_progress_1,//进度条
                supervise_tv_progress_2;
        View supervise_v_line;
        TextView supervise_tv_content_1,supervise_tv_content_2,supervise_tv_content_3;//进度描述信息
        SuperviseProgressView supervise_rl_2;
        RelativeLayout rl_progress1,rl_progress2,rl_progress3;

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

        public SuperviseMineListHolder(View itemView) {
            super(itemView);
            initView(itemView);
        }

        public SuperviseMineListHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
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
                loading_txt=(TextView)itemView.findViewById(R.id.loading_txt);
                page_iv=(ImageView)itemView.findViewById(R.id.page_iv);
                pb_load=(ProgressBar)itemView.findViewById(R.id.pb_load);
                loadpage_empty.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,initRecycleViewHeight));
//                loadpage_empty.getLayoutParams().height=initRecycleViewHeight;
                loadpage_empty.requestLayout();
                tv_empty=(TextView)itemView.findViewById(R.id.tv_empty);
                tv_empty.setText(keyName+HDCivilizationConstants.EMPTY_STRING);
            }else{
                iv_supervise_mysub=(ImageView)itemView.findViewById(R.id.iv_supervise_mysub);
                supervise_iv_progress_1=(ImageView)itemView.findViewById(R.id.supervise_iv_progress_1);
                supervise_iv_progress_2=(ImageView)itemView.findViewById(R.id.supervise_iv_progress_2);
                supervise_iv_progress_3=(ImageView)itemView.findViewById(R.id.supervise_iv_progress_3);

                supervise_tv_title=(TextView)itemView.findViewById(R.id.supervise_tv_title);
                supervise_tv_content=(TextView)itemView.findViewById(R.id.supervise_tv_content);
                supervise_tv_time=(TextView)itemView.findViewById(R.id.supervise_tv_time);
                supervise_tv_progress_1=(TextView)itemView.findViewById(R.id.supervise_tv_progress_1);
                supervise_tv_progress_2=(TextView)itemView.findViewById(R.id.supervise_tv_progress_2);

                supervise_tv_content_1=(TextView)itemView.findViewById(R.id.supervise_tv_content_1);
                supervise_tv_content_2=(TextView)itemView.findViewById(R.id.supervise_tv_content_2);
                supervise_tv_content_3=(TextView)itemView.findViewById(R.id.supervise_tv_content_3);

                supervise_v_line=(View)itemView.findViewById(R.id.supervise_v_line);
                supervise_rl_2=(SuperviseProgressView)itemView.findViewById(R.id.supervise_rl_2);
                rl_progress1=(RelativeLayout)itemView.findViewById(R.id.rl_progress1);
                rl_progress2=(RelativeLayout)itemView.findViewById(R.id.rl_progress2);
                rl_progress3=(RelativeLayout)itemView.findViewById(R.id.rl_progress3);
            }
        }
    }

    /***
     * 判断是否超时
     * @return boolean
     */
    public boolean getNetWorkTime(){
        boolean trueOrFalse = (System.currentTimeMillis() - superviseMineListProtocol.getNetWorktime()) > HDCivilizationConstants.NET_GAP_TIME;
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
                            Message message=Message.obtain();
                            Bundle bundle=new Bundle();
                            bundle.putInt(REQUEST_TYPE_KEY, requestType);
                            message.setData(bundle);
                            GetVolunteerInfoProtocol getVolunteerInfoProtocol=new GetVolunteerInfoProtocol();
                            getVolunteerInfoProtocol.setUserId(user.getUserId());
                            getVolunteerInfoProtocol.setOutUser(user);
                            getVolunteerInfoProtocol.setActionKeyName(loadFirstPage);
                            message.obj=getVolunteerInfoProtocol.parseJson(responseInfo.result);
                            message.what=getVolunteerInfoSuccess;
                            handler.sendMessage(message);
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                            Message message=Message.obtain();
                            message.what=getVolunteerInfoFailure;
                            message.obj=e.getMessage();
                            Bundle bundle=new Bundle();
                            bundle.putInt(REQUEST_TYPE_KEY, requestType);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } catch (ContentException e){
                            e.printStackTrace();
                            Message message=Message.obtain();
                            message.what=getVolunteerInfoFailure;
                            message.obj=e.getErrorContent();
                            Bundle bundle=new Bundle();
                            bundle.putInt(REQUEST_TYPE_KEY, requestType);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Message message=Message.obtain();
                        message.what=getVolunteerInfoFailure;
                        message.obj=loadFirstPage+"!";
                        handler.sendMessage(message);
                        //java.net.SocketTimeoutException
                        System.out.println("uploadImgProtocol  onFailure......"+"error:" + //
                                error.getExceptionCode()+"..getMessage:"+error.getMessage()+"...msg:"+msg);
                    }
                });
    }

    abstract  class MyOnClickListener implements View.OnClickListener{
        int position;

        public MyOnClickListener(int position) {
            this.position = position;
        }
    }
}

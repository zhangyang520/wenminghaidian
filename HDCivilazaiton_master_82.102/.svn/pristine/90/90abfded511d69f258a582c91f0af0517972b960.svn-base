package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.CivistateDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.ActivityType;
import com.zhjy.hdcivilization.entity.HDC_CiviState;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.holder.MainPagerHolder;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.protocol.CiviStateListProtocol;
import com.zhjy.hdcivilization.protocol.MainNumberProtocol;
import com.zhjy.hdcivilization.utils.BitmapUtil;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.ScreenUtil;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.LoadMoreRecyclerView;
import com.zhjy.hdcivilization.view.SticklayoutSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 文明动态的页面
 */
public class CiviStateActivity extends BaseActivity {
    private SticklayoutSwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout frameLayout;
    private RelativeLayout rl_back;
    private String keyName = "文明动态";
    private LoadMoreRecyclerView recyclerView;
    private MainPagerHolder mainPagerHolder = new MainPagerHolder();
    private CiviStateListProtocol civiStateListProtocol=new CiviStateListProtocol();
    private CiviStateAdapter civiStateAdapter;
    private LinearLayout ll_civil_state;
    private LinearLayoutManager linearLayoutManager;
    private ImageView btnBack;
    public  static  final int PAGE_SIZE=10;//固定个数
    private  final int SECOND_PAGE_SIZE=6;//固定个数

    private List<HDC_CiviState> recyclerViewDatas =new ArrayList<HDC_CiviState>();//recyclerView适配器对应的数据集合
    private List<HDC_CiviState> tempList = new ArrayList<HDC_CiviState>();
    private List<HDC_CiviState> pagerAdapterDatas = new ArrayList<HDC_CiviState>();//viewPager对应的适配器集合
    private int firstPage=1;
    private int currentPager = firstPage;
    private final int recyceler_type=2;
    private final int pagerAdapter_type=3;
    private String userId;

    private int initRecycleViewHeight;

    private String refershName = "刷新文明动态信息失败";
    private String loadmoreName = "加载更多文明动态信息失败";
    private String loadFirstPage = "加载文明动态信息失败";

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(civiStateAdapter==null) {
                synchronized (CiviStateActivity.this) {
                    if(civiStateAdapter==null){
                        civiStateAdapter = new CiviStateAdapter();
                        recyclerView.setAdapter(civiStateAdapter);
                    }
                }
            }
            switch (msg.what){
                case HDCivilizationConstants.ERROR_CODE:
                    if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)== HDCivilizationConstants.LOAD_MORE){
                        UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                        recyclerView.notifyMoreFinish(true);
                    }else if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)== HDCivilizationConstants.REFRESH_PAGE){
                        //刷新首页 失败
                        UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setRefreshing(false);
                        if((civiStateAdapter.datas==null || civiStateAdapter.datas.size()==0) &&//
                                (mainPagerHolder.getDatas()==null || mainPagerHolder.getDatas().size()==0)){
                            civiStateAdapter.datas= null;
                            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        }
                    }else if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)== HDCivilizationConstants.HEAD_DATA){
                        UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                        mSwipeRefreshLayout.setRefreshing(false);
                    }else if(msg.getData().getInt(HDCivilizationConstants.ACTION_CODE)== HDCivilizationConstants.CILIVIZATION_CIVISTATE_LIST_FIRST_PAGE){
                        //请求首页 失败
                        mSwipeRefreshLayout.setRefreshing(false);
                        if((civiStateAdapter.datas==null || civiStateAdapter.datas.size()==0)){
                            civiStateAdapter.datas= null;
                            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                        }
                        UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                    }else{
                        System.out.println(".....");
                    }
                    break;

                case HDCivilizationConstants.CILIVIZATION_CIVISTATE_LIST_FIRST_PAGE://第一屏数据加载

                    if(((List<HDC_CiviState>)msg.obj).size()<=0){
                        UiUtils.getInstance().showToast("尚未发布文明动态信息");
                        //进行清除数据
                        CivistateDao.getInstance().clearAll();
                        pagerAdapterDatas.clear();
                        recyclerViewDatas.clear();
                        //frameLayout.setBackgroundResource(R.drawable.real_horizontal_default);
                    }else{
                        //进行清除数据
                        CivistateDao.getInstance().clearAll();
                        CivistateDao.getInstance().saveAllCivistateList((List<HDC_CiviState>) msg.obj);
                        //pagerAdapter轮播图的适配器
                        pagerAdapterDatas=getFirstFourList(((List<HDC_CiviState>)msg.obj),pagerAdapter_type);
                        //recycler的适配器
                        recyclerViewDatas=getFirstFourList(((List<HDC_CiviState>)msg.obj),recyceler_type);
//                        UiUtils.getInstance().showToast(keyName + "加载首页数据成功!");
                        getNumberForServer();
                    }
                    //轮播图相关的处理
                    mainPagerHolder.stop();
                    mainPagerHolder.setDatas(pagerAdapterDatas);
                    if(pagerAdapterDatas.size()>0){
                        mainPagerHolder.start();
                    }
                    View contentView = mainPagerHolder.getContentView();
                    if (contentView.getParent()!= null){
                        ((ViewGroup)contentView.getParent()).removeView(contentView);
                    }
                    frameLayout.addView(contentView);

                    currentPager=firstPage;
                    //recyclerView的相关处理
                    civiStateAdapter.datas= recyclerViewDatas;
                    recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                    //重新执行数字更新

                    break;

                case HDCivilizationConstants.REFRESH_PAGE:
                    if(((List<HDC_CiviState>)msg.obj).size()<=0){
                        CivistateDao.getInstance().clearAll();
                        UiUtils.getInstance().showToast("尚未发布文明动态信息");
                        pagerAdapterDatas.clear();
                        recyclerViewDatas.clear();
                    }else{
                        System.out.println("(List<HDC_CiviState>) msg.obj:" + (List<HDC_CiviState>) msg.obj);
                        CivistateDao.getInstance().clearAll();
                        CivistateDao.getInstance().saveAllCivistateList((List<HDC_CiviState>) msg.obj);
                        //pagerAdapter轮播图的适配器
                        pagerAdapterDatas=getFirstFourList(((List<HDC_CiviState>)msg.obj),pagerAdapter_type);
                        //recycler的适配器
                        recyclerViewDatas=getFirstFourList(((List<HDC_CiviState>)msg.obj),recyceler_type);
//                        UiUtils.getInstance().showToast(keyName + "刷新首页数据成功!");
                        getNumberForServer();
                    }
                    currentPager=firstPage;
                    //轮播图相关的处理
                    mainPagerHolder.stop();
                    mainPagerHolder.setDatas(pagerAdapterDatas);
                    if(pagerAdapterDatas.size()>0){
                        mainPagerHolder.start();
                    }
                    contentView = mainPagerHolder.getContentView();
                    if (contentView.getParent()!= null){
                        ((ViewGroup)contentView.getParent()).removeView(contentView);
                    }
                    frameLayout.addView(contentView);

                    //recyclerView的相关处理
                    civiStateAdapter.datas= recyclerViewDatas;
                    recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                    //重新执行数字更新
                    break;
                //加载更多
                case HDCivilizationConstants.LOAD_MORE:
                    if(((List<HDC_CiviState>)msg.obj).size()==0){
                        UiUtils.getInstance().showToast("没有更多文明动态信息了!");
                        recyclerView.notifyMoreFinish(true);
                    }else{
                        //进行保存到数据库中
                        CivistateDao.getInstance().saveAllCivistateList((List<HDC_CiviState>) msg.obj);
                        int firstSize=recyclerViewDatas.size();
                        recyclerViewDatas.addAll((List<HDC_CiviState>) msg.obj);
                        civiStateAdapter.datas= recyclerViewDatas;
                        recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
//                        linearLayoutManager.scrollToPosition(recyclerViewDatas.size() - 1);
                        linearLayoutManager.scrollToPosition(firstSize - 1);
//                        UiUtils.getInstance().showToast(keyName + "加载更多数据成功!");
                        currentPager+=1;
                        recyclerView.notifyMoreFinish(true);
                    }
                    //重新执行数字更新
                    getNumberForServer();
                    break;
            }
            super.handleMessage(msg);
        };
    };

    /**
     * 进行获取前四个 datas集合的元素
     * @param datas
     * @return
     */
    private <T> List<T> getFirstFourList(List<T> datas,int type) {
        List<T> tempDatas=new ArrayList<T>();
        if(datas.size()>0) {
            switch (type) {
                case pagerAdapter_type://轮播集合的类型
                    if (datas.size() <= HDCivilizationConstants.CIVI_STATE_COUNT) {
                        return datas;
                    } else {
                        for (int i = 0; i < HDCivilizationConstants.MAIN_NOTICE_COUNT; ++i) {
                            tempDatas.add(datas.get(i));
                        }
                    }
                    break;

                case recyceler_type://列表集合的类型
                    if(datas.size() <= HDCivilizationConstants.CIVI_STATE_COUNT){
                        return tempDatas;
                    }else{
                        //进行遍历集合
                        for(int i= HDCivilizationConstants.CIVI_STATE_COUNT;i<datas.size();++i){
                            tempDatas.add(datas.get(i));
                        }
                        return tempDatas;
                    }
            }
        }
        return tempDatas;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        customLayout = R.layout.activity_civi_state;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        btnBack = (ImageView)findViewById(R.id.btn_back);
        //加载布局控件
        mSwipeRefreshLayout = (SticklayoutSwipeRefreshLayout)findViewById(R.id.simple_swipe_refresh_layout);
        mSwipeRefreshLayout.setIsStickLayoutEnable(true);
        frameLayout = (LinearLayout)findViewById(R.id.frame_layout);
        rl_back = (RelativeLayout)findViewById(R.id.rl_back);
        recyclerView = (LoadMoreRecyclerView)findViewById(R.id.recyclerView);
        ll_civil_state = (LinearLayout)findViewById(R.id.ll_civil_state);
        linearLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);

        recyclerView.setLayoutManager(linearLayoutManager);
        mSwipeRefreshLayout.setChild(recyclerView);

        recyclerView.setPageSize(SECOND_PAGE_SIZE);
        ll_civil_state.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ll_civil_state.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                frameLayout.getLayoutParams().height = ScreenUtil.getInstance().getHeight(UiUtils.getInstance().getContext(),1120,736);
                frameLayout.requestLayout();
                mSwipeRefreshLayout.setHeadView(frameLayout);
                mSwipeRefreshLayout.setViewPagerTop((LinearLayout) mainPagerHolder.getContentView());
            }
        });
        mSwipeRefreshLayout.setLinearLayoutManager(linearLayoutManager);
        mainPagerHolder.setActivity(CiviStateActivity.this);
        mainPagerHolder.setActivityType(ActivityType.ACTIVITYSTATE.getType());

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                initRecycleViewHeight = recyclerView.getMeasuredHeight();
            }
        });
        try {
            User user= UserDao.getInstance().getLocalUser();
            userId=user.getUserId();
        } catch (ContentException e) {
            e.printStackTrace();
            userId="";
        }
    }

    @Override
    protected void initInitevnts() {

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.color1, R.color.color2, R.color.color3, R.color.color4);
//        /**第一屏数据加载**/
        List<HDC_CiviState> listDataBase = CivistateDao.getInstance().getAllCivistateList();
        //判断数据库中是有数据
        if(listDataBase!=null && listDataBase.size()>0){
            //pagerAdapter轮播图的适配器
            pagerAdapterDatas=getFirstFourList(listDataBase,pagerAdapter_type);
            //recycler的适配器
            recyclerViewDatas=getFirstFourList(listDataBase,recyceler_type);
        }else{
            pagerAdapterDatas.clear();
            recyclerViewDatas.clear();
        }
        //轮播图相关的处理
        mainPagerHolder.stop();
        mainPagerHolder.setDatas(pagerAdapterDatas);
        if(pagerAdapterDatas.size()>0){
            mainPagerHolder.start();
        }
        contentView = mainPagerHolder.getContentView();
        if (contentView.getParent()!= null){
            ((ViewGroup)contentView.getParent()).removeView(contentView);
        }
        frameLayout.addView(contentView);

        //recyclerView的相关处理
        if(recyclerViewDatas.size()>0){
            civiStateAdapter=new CiviStateAdapter();
            civiStateAdapter.datas= recyclerViewDatas;
            recyclerView.setAdapter(civiStateAdapter);
            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
        }

        //进行首页的访问数据
        getCiviStateListForServer("加载首页数据");
        //设置刷新监听
        refreshCiviStateListForServer("刷新首页数据");
        //设置加载更多的监听
        downLoadDatasForServer();
    }


    /**
     * 第一屏加载数据
     */
    private void getCiviStateListForServer(final String failDes){
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                getFirstData(failDes);
            }
        });
    }

    /**
     * 有条件地获取第一页数据
     * @param failDes
     */
    private void getFirstData(final String failDes) {
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                try {
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode", "AROUND0007");
                    paramsMap.put("pagerNum", PAGE_SIZE + "");
                    paramsMap.put("currentPager", firstPage + "");
                    paramsMap.put("userId", userId);
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                    civiStateListProtocol.setUserId(userId);
                    civiStateListProtocol.setActionKeyName(loadFirstPage);
                    message.obj = civiStateListProtocol.loadData(urlParamsEntity);
                    message.what = HDCivilizationConstants.CILIVIZATION_CIVISTATE_LIST_FIRST_PAGE;
                    handler.sendMessage(message);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.CILIVIZATION_CIVISTATE_LIST_FIRST_PAGE);
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (ContentException e) {
                    e.printStackTrace();
                    //权限过低的情况 此权限过低的情况只是:前后台的userId不匹配 //// TODO: 2016/9/19
                    if (e.getErrorCode() == HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE) {
                        message.what = HDCivilizationConstants.ERROR_CODE;
                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT, loadFirstPage + "!");
                        bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.CILIVIZATION_CIVISTATE_LIST_FIRST_PAGE);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    } else {
                        message.what = HDCivilizationConstants.ERROR_CODE;
                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                        bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.CILIVIZATION_CIVISTATE_LIST_FIRST_PAGE);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }

            }
        });
    }

    /**
     * 刷新数据
     */
    private void refreshCiviStateListForServer(final String failDes){

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //列表数据
                ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        try {
                            UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                            LinkedHashMap<String,String> paramsMap = new LinkedHashMap<String, String>();
                            paramsMap.put("tranCode","AROUND0007");
                            paramsMap.put("pagerNum",PAGE_SIZE+"");
                            paramsMap.put("currentPager",firstPage+"");
                            paramsMap.put("userId", userId);
                            urlParamsEntity.setParamsHashMap(paramsMap);
                            urlParamsEntity.HDCURL= UrlParamsEntity.CURRENT_ID;
                            civiStateListProtocol.setUserId(userId);
                            civiStateListProtocol.setActionKeyName(refershName);
                            message.obj = civiStateListProtocol.loadData(urlParamsEntity);
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
                                //权限过低的情况 此权限过低的情况只是:前后台的userId不匹配 //// TODO: 2016/9/19
                                message.what = HDCivilizationConstants.ERROR_CODE;
                                bundle.putString(HDCivilizationConstants.ERROR_CONTENT,refershName+"!");
                                bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REFRESH_PAGE);
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }else{
                                message.what = HDCivilizationConstants.ERROR_CODE;
                                bundle.putString(HDCivilizationConstants.ERROR_CONTENT,e.getErrorContent());
                                bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.REFRESH_PAGE);
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                        }
                    }
                });
            }
        });
    }

    /***加载更多数据****/
    private void downLoadDatasForServer() {
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
                                    LinkedHashMap<String,String> paramsMap = new LinkedHashMap<String, String>();
                                    paramsMap.put("tranCode","AROUND0007");
                                    paramsMap.put("pagerNum",PAGE_SIZE+"");
                                    paramsMap.put("currentPager",(currentPager+1)+"");
                                    paramsMap.put("userId",userId);
                                    urlParamsEntity.setParamsHashMap(paramsMap);
                                    urlParamsEntity.HDCURL= UrlParamsEntity.CURRENT_ID;
                                    civiStateListProtocol.setUserId(userId);
                                    civiStateListProtocol.setActionKeyName(loadmoreName);
                                    message.obj = civiStateListProtocol.loadData(urlParamsEntity);
                                    message.what = HDCivilizationConstants.LOAD_MORE;
                                    handler.sendMessage(message);
                                } catch (JsonParseException e) {
                                    e.printStackTrace();
                                    message.what = HDCivilizationConstants.ERROR_CODE;
                                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT,e.getMessage());
                                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.LOAD_MORE);
                                    message.setData(bundle);
                                    handler.sendMessage(message);
                                } catch (ContentException e) {
                                    e.printStackTrace();
                                    //权限过低的情况 此权限过低的情况只是:前后台的userId不匹配 //// TODO: 2016/9/19
                                    if(e.getErrorCode()== HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE){
                                        message.what = HDCivilizationConstants.ERROR_CODE;
                                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT,loadmoreName+"!");
                                        bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.LOAD_MORE);
                                        message.setData(bundle);
                                        handler.sendMessage(message);
                                    }else{
                                        message.what = HDCivilizationConstants.ERROR_CODE;
                                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT,e.getErrorContent());
                                        bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.LOAD_MORE);
                                        message.setData(bundle);
                                        handler.sendMessage(message);
                                    }
                                }
                            }
                        });
                    }
                },1000);
            }
        });

    }

    boolean emptyShowState;
    class  CiviStateAdapter extends RecyclerView.Adapter<CiviStateViewHolder>{

        private List<HDC_CiviState> datas;

        public CiviStateAdapter() {
        }

        public CiviStateAdapter(List<HDC_CiviState> datas) {
            this.datas=datas;
        }

        private int ITEM_TYPE_ERROR_CODE=101;//失败条目类型
        private int ITEM_TYPE_EMPTY_CODE=102;//为空的条目类型
        @Override
        public CiviStateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            System.out.println("CiviStateViewHolder onCreateViewHolder viewType:"+viewType);
            if(viewType==ITEM_TYPE_ERROR_CODE){
                //如果是错误类型
                return new CiviStateViewHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.loadpager_error,null),ITEM_TYPE_ERROR_CODE);
            }else if(viewType==ITEM_TYPE_EMPTY_CODE){
                //如果是数据为空类型
                return new CiviStateViewHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.loadpage_empty,null),ITEM_TYPE_EMPTY_CODE);
            }
            return new CiviStateViewHolder(View.inflate(UiUtils.getInstance().getContext(), R.layout.listview_item_civistate,null));
        }

        /**
         * 进行显示错误页面
         * @param mineHodler
         */
        private void visiableError(CiviStateViewHolder mineHodler) {
            mineHodler.button.setVisibility(View.VISIBLE);
            mineHodler.page_iv.setVisibility(View.VISIBLE);
            mineHodler.pb_load.setVisibility(View.GONE);
            mineHodler.loading_txt.setVisibility(View.GONE);
        }

        @Override
        public void onBindViewHolder(CiviStateViewHolder holder, int position) {
            System.out.println("CiviStateViewHolder onBindViewHolder viewType:"+getItemViewType(position));
            if(getItemViewType(position)==ITEM_TYPE_ERROR_CODE){
                mSwipeRefreshLayout.setEnabled(false);
                visiableError(holder);
            }else if(getItemViewType(position)==ITEM_TYPE_EMPTY_CODE){
                //数据为空时!
                mSwipeRefreshLayout.setEnabled(true);
                if(emptyShowState){
                    //如果展现
                    holder.loadpage_empty.setVisibility(View.VISIBLE);
                }else{
                    //如果消失
                    holder.loadpage_empty.setVisibility(View.INVISIBLE);
                }
            }else{
                mSwipeRefreshLayout.setEnabled(true);
                if(datas.size()>0){
                    final HDC_CiviState data=datas.get(position);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CiviStateActivity.this, CiviStateDetailActivity.class);
                            intent.putExtra(CiviStateDetailActivity.ITEM_ID_AND_TYPE, data.getItemId());
                            startActivity(intent);
                        }
                    });
                    if (data.getImgEntity()!=null){
                        holder.civistate_iv.setVisibility(View.VISIBLE);
                        BitmapUtil.getInstance().displayImg(holder.civistate_iv, UrlParamsEntity.WUCHEN_XU_IP_FILE+data.getImgEntity().getImgThumbUrl());
                    }else{
                        holder.civistate_iv.setVisibility(View.GONE);
                    }
                    holder.civistate_tv_des.setText(data.getDes());
                    holder.civistate_tv_time.setText(data.getPublishTime());
                    holder.civistate_tv_title.setText(data.getTitle());
                    holder.civistate_tv_zan.setText("赞" + data.getDianZanCount());
                    //判断线
                    if(position==(datas.size()-1)){
                        holder.supervise_v_line.setVisibility(View.INVISIBLE);
                    }else{
                        holder.supervise_v_line.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        /**
         * 获取对应的item类型
         * @param position
         * @return
         */
        @Override
        public int getItemViewType(int position) {
            if(position==0 && datas==null){
                return ITEM_TYPE_ERROR_CODE;
            }else if((position==0 && datas.size()==0)){
                if((mainPagerHolder.getDatas().size()==0)){
                    emptyShowState =true;
                }else{
                    emptyShowState =false;
                }
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

    /**
     * viewHolder
     */
    class CiviStateViewHolder extends  RecyclerView.ViewHolder{

        private int viewType;//条目的类型

        ImageView civistate_iv;
        TextView civistate_tv_title,civistate_tv_des,civistate_tv_zan,civistate_tv_time,supervise_v_line;

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

        public CiviStateViewHolder(View itemView) {
            super(itemView);
            initView(itemView);
        }

        public CiviStateViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            initView(itemView);
        }

        /**
         * 进行初始化view
         */
        private void initView(View itemView){
            if(viewType==ITEM_TYPE_ERROR_CODE){
                mSwipeRefreshLayout.setIsStickLayoutEnable(false);
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
                        getFirstData("加载首页");
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
                mSwipeRefreshLayout.setIsStickLayoutEnable(false);
                loadpage_empty=(RelativeLayout)itemView.findViewById(R.id.loadpage_empty);
                loadpage_empty.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,initRecycleViewHeight));
//                loadpage_empty.getLayoutParams().height=initRecycleViewHeight;
                loadpage_empty.requestLayout();
                tv_empty=(TextView)itemView.findViewById(R.id.tv_empty);
                tv_empty.setText(keyName+ HDCivilizationConstants.EMPTY_STRING);
            }else{
                mSwipeRefreshLayout.setIsStickLayoutEnable(true);
                civistate_iv=(ImageView)itemView.findViewById(R.id.civistate_iv);
                civistate_tv_title=(TextView)itemView.findViewById(R.id.civistate_tv_title);
                civistate_tv_des=(TextView)itemView.findViewById(R.id.civistate_tv_des);
                civistate_tv_zan=(TextView)itemView.findViewById(R.id.civistate_tv_zan);
                civistate_tv_time=(TextView)itemView.findViewById(R.id.civistate_tv_time);
                supervise_v_line=(TextView)itemView.findViewById(R.id.supervise_v_line);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        /**第一屏数据加载**/
        List<HDC_CiviState> listDataBase = CivistateDao.getInstance().getAllCivistateList();
        //判断数据库中是有数据
        if(listDataBase!=null && listDataBase.size()>0){
            //pagerAdapter轮播图的适配器
            pagerAdapterDatas=getFirstFourList(listDataBase,pagerAdapter_type);
            //recycler的适配器
            recyclerViewDatas=getFirstFourList(listDataBase,recyceler_type);
        }else{
            pagerAdapterDatas.clear();
            recyclerViewDatas.clear();
        }

//        //轮播图相关的处理
        mainPagerHolder.stop();
        mainPagerHolder.setDatas(pagerAdapterDatas);
        if(pagerAdapterDatas.size()>0){
            mainPagerHolder.start();
        }
        contentView = mainPagerHolder.getContentView();
        if (contentView.getParent()!= null){
            ((ViewGroup)contentView.getParent()).removeView(contentView);
        }
        frameLayout.addView(contentView);

        //recyclerView的相关处理
        if(recyclerViewDatas.size()>0){
            civiStateAdapter.datas= recyclerViewDatas;
            recyclerView.getmAutoLoadAdapter().notifyDataSetChanged();
        }
    }


    /***数字更新网络请求**/
    private void getNumberForServer() {
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                User user= null;
                try {
                    //进行获取本地用户
                    user = UserDao.getInstance().getLocalUser();
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String,String> paramsMap = new LinkedHashMap<String, String>();
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
}

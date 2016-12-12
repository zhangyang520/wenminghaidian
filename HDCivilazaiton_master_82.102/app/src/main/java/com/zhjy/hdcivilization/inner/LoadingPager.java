package com.zhjy.hdcivilization.inner;

import java.util.List;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.UiUtils;

/**
 * @项目名：AroundYou
 * @类名称：LoadingPager
 * @类描述： 正在加载中的页面
 * @创建人：HXF
 * @修改人：
 * @创建时间：2015-12-22 下午5:59:48
 */
public abstract class LoadingPager extends FrameLayout {

    //加载的flag的状态值
    private boolean loadFlag = false;

    //定义java变量
    private final int STATE_UNKOWN = -1;//未知状态
    private final int STATE_LOADING = 0;//正在加载中状态
    private final int STATE_EMPTY = 1;//空状态
    private final int STATE_ERROR = 2;//加载失败状态
    private final int STATE_SUCCESS = 3;//加载成功状态

    //初始化设置状态值
    private int state = STATE_UNKOWN;

    private View loadingView;// 加载中的界面S
    private View errorView;// 错误界面
    private View emptyView;// 空界面
    private View successView;//加载成功的界面

    private String errorString; //提示错误的字符串
    private String loadingString;//正在加载的字符串
    private LoadStatus loadStatus=LoadStatus.hasComplete;
    private long networkTime=0;

    public long getNetworkTime() {
        return networkTime;
    }

    public void setNetworkTime(long networkTime) {
        this.networkTime = networkTime;
    }

    public LoadStatus getLoadStatus() {
        return loadStatus;
    }

    public void setLoadStatus(LoadStatus loadStatus) {
        this.loadStatus = loadStatus;
    }

    public String getLoadingString() {
        return loadingString;
    }

    public void setLoadingString(String loadingString) {
        this.loadingString = loadingString;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    public LoadingPager(Context context){
        super(context);
        //初始化函数
        init();
    }

    public LoadingPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //初始化函数
        init();
    }

    public LoadingPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化函数
        init();
    }

    /**
     * 初始化函数
     */
    private void init() {
        loadingView = createLoadingView(); // 创建了加载中的界面
        if (loadingView != null) {
            this.addView(loadingView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
        errorView = createErrorView(); // 加载错误界面
        if (errorView != null) {
            this.addView(errorView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
        emptyView = createEmptyView(); //加载空的界面
        if (emptyView != null) {
            this.addView(emptyView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
        showPage();//根据不同的状态显示不同的界面
    }


    /**
     * 进行显示页面
     */
    private void showPage() {
        // TODO Auto-generated method stub
        if (loadingView != null) {
            if (loadingString != null && !loadingString.trim().equals("")) {
                TextView loading_txt = (TextView) loadingView.findViewById(R.id.loading_txt);
                loading_txt.setText(loadingString);
            }
            loadingView.setVisibility(state == STATE_UNKOWN
                    || state == STATE_LOADING ? View.VISIBLE : View.INVISIBLE);
        }
        if (errorView != null) {
            if (errorString != null && !errorString.trim().equals("")) {
                Button page_bt = (Button) errorView.findViewById(R.id.page_bt);
                page_bt.setText(errorString);
            }
            errorView.setVisibility(state == STATE_ERROR ? View.VISIBLE
                    : View.INVISIBLE);
        }
        if (emptyView != null) {
            if(state == STATE_EMPTY){
                networkTime=System.currentTimeMillis();
                emptyView.setVisibility( View.VISIBLE);
            }else{
                emptyView.setVisibility(View.INVISIBLE);
            }
        }
        if (state == STATE_SUCCESS) {
            if (successView == null) {
                successView = createSuccessView();
                this.addView(successView, new LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            }
            networkTime=System.currentTimeMillis();
            successView.setVisibility(View.VISIBLE);
        } else {
            if (successView != null) {
                successView.setVisibility(View.INVISIBLE);
            }
        }
    }

    //创建成功的页面
    protected abstract View createSuccessView();

    /**
     * 创建空页面
     *
     * @return
     */
    private View createEmptyView() {
        View view = View.inflate(UiUtils.getInstance().getContext(), R.layout.loadpage_empty,
                null);
        return view;
    }

    /**
     * 创建错误的页面
     *
     * @return
     */
    private View createErrorView() {
        View view = View.inflate(UiUtils.getInstance().getContext(), R.layout.loadpager_error,
                null);
        Button page_bt = (Button) view.findViewById(R.id.page_bt);
        page_bt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                show();
            }
        });
        return view;
    }


    /**
     * 创建加载中的页面
     *
     * @return
     */
    private View createLoadingView() {
        View view = View.inflate(UiUtils.getInstance().getContext(),
                R.layout.loadpager_loading, null);
        return view;
    }

    public enum LoadResult {
        error(2), empty(1), success(3);

        int value;

        LoadResult(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

    //加载访问网络的状态
    public enum LoadStatus{
        hasComplete(0), noComplete(1);
        int value;

        LoadStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public void show() {
        // TODO Auto-generated method stub
        loadStatus=LoadStatus.noComplete;
        if (state == STATE_ERROR || state == STATE_EMPTY || state == STATE_SUCCESS) {
            state = STATE_LOADING;
        }

        // 请求服务器 获取服务器上数据 进行判断
        // 请求服务器 返回一个结果
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {

            @Override
            public void run() {
                SystemClock.sleep(300);
                final LoadResult result = load();
                UiUtils.getInstance().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (result != null) {
                            state = result.getValue();
                            System.out.println("state = " + state);
                            showPage(); // 状态改变了,重新判断当前应该显示哪个界面
                            loadStatus=LoadStatus.hasComplete;
                        }
                    }
                });
            }
        });

        showPage();
    }

    protected abstract LoadResult load();

    public boolean isLoadFlag() {
        return loadFlag;
    }

    public void setLoadFlag(boolean loadFlag) {
        this.loadFlag = loadFlag;
    }

    /**
     * 校验数据
     */
    public LoadResult checkData(List datas) {
        if (datas == null) {
            return LoadResult.error;//  请求服务器失败
        } else {
            if (datas.size() == 0) {
                return LoadResult.empty;
            } else {
                return LoadResult.success;
            }
        }
    }

    public void setEmptyStatus(){
        state=STATE_EMPTY;
        showPage();
    }
}

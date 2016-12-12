package com.zhjy.hdcivilization.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.utils.UiUtils;

/**
 * @author :huangxianfeng on 2016/8/9.
 * 自定义RecyclerView，实现上拉加载更多
 */
public class LoadMoreRecyclerView extends RecyclerView {

    /**
     * item 类型
     */
    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_HEADER = 1;//头部--支持头部增加一个headerView
    public final static int TYPE_INSUFFICIENT=11;//不足的状态
    public final static int TYPE_FOOTER = 2;//底部--往往是loading_more
    public final static int TYPE_LIST = 3;//代表item展示的模式是list模式
    public final static int TYPE_STAGGER = 4;//代码item展示模式是网格模式

    private boolean mIsFooterEnable = false;//是否允许加载更多
    public int loadMoreState=LOAD_MORE_FINISHED;//默认是结束
    private int pageSize;//每页显示的个数
    public static final int LOAD_MORE_ING=5;//正在加载更多中
    public static final int LOAD_MORE_FINISHED=6;//加载更多结束

    private boolean isScrolling=false;
    /**
     * 自定义实现了头部和底部加载更多的adapter
     */
    private AutoLoadAdapter mAutoLoadAdapter;
    /**
     * 标记是否正在加载更多，防止再次调用加载更多接口
     */
    private boolean mIsLoadingMore;
    /**
     * 标记加载更多的position
     */
    private int mLoadMorePosition;
    /**
     * 加载更多的监听-业务需要实现加载数据
     */
    public LoadMoreListener mListener;

    public LoadMoreRecyclerView(Context context) {
        super(context);
        init();
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public int getLoadMoreState() {
        return loadMoreState;
    }

    public void setLoadMoreState(int loadMoreState) {
        this.loadMoreState = loadMoreState;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 初始化-添加滚动监听
     * <p/>
     * 回调加载更多方法，前提是
     * <pre>
     *    1、有监听并且支持加载更多：null != mListener && mIsFooterEnable
     *    2、目前没有在加载，正在上拉（dy>0），当前最后一条可见的view是否是当前数据列表的最后一条--及加载更多
     * </pre>
     */
    private void init() {
        if(!isLanJie){
            super.addOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            isScrolling=true;
                            break;

                        case RecyclerView.SCROLL_STATE_IDLE:
                        case RecyclerView.SCROLL_STATE_SETTLING:
                            isScrolling=false;
                            break;
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
//                    if (null != mListener && mIsFooterEnable && !mIsLoadingMore && dy > 0) {
//                        int lastVisiblePosition = getLastVisiblePosition();
//                        System.out.println("onScrolled  recyclerView lastVisiblePosition:" + lastVisiblePosition+"..mAutoLoadAdapter.getItemCount():"+mAutoLoadAdapter.getItemCount()+"..pageSize:"+pageSize);
//                        //mAutoLoadAdapter.getItemCount()减去footView这个节点
//                        if (lastVisiblePosition + 1 == (mAutoLoadAdapter.getItemCount()-1)) {
//                            //如果小于固定个数
//                            if(lastVisiblePosition<pageSize){
////                                UiUtils.getInstance().showToast("首页不足"+pageSize+"不能加载更多！");
//                            }else{
//                                System.out.println("onScrolled  recyclerView:" + dx + "...clampedY:" + dy+"2222222222");
//                                setLoadingMore(true);
//                                mLoadMorePosition = lastVisiblePosition;
//                                loadMoreState=LOAD_MORE_ING;
//                                mListener.onLoadMore();
//                            }
//                        }
//                    }
                }
            });
        }
    }

    /**
     * 设置加载更多的监听
     *
     * @param listener
     */
    public void setLoadMoreListener(LoadMoreListener listener) {
        mListener = listener;
    }

    /**
     * 设置正在加载更多
     *
     * @param loadingMore
     */
    public void setLoadingMore(boolean loadingMore) {
        this.mIsLoadingMore = loadingMore;
    }

    /**
     * 加载更多监听
     */
    public interface LoadMoreListener {
        /**
         * 加载更多
         */
        void onLoadMore();
    }

    AutoLoadAdapter.FooterViewHolder footerViewHolder ;
    int footerViewHeight;
    /**
     *
     */
    public class AutoLoadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        /**
         * 数据adapter
         */
        private RecyclerView.Adapter mInternalAdapter;

        private boolean mIsHeaderEnable;
        private int mHeaderResId;
        private int ITEM_TYPE_ERROR_CODE=101;//失败条目类型
        private int ITEM_TYPE_EMPTY_CODE=102;//为空的条目类型

        public AutoLoadAdapter(RecyclerView.Adapter adapter) {
            mInternalAdapter = adapter;
            mIsHeaderEnable = false;
        }

        @Override
        public int getItemViewType(int position){

            if(mInternalAdapter.getItemViewType(position)==ITEM_TYPE_ERROR_CODE){
                return  ITEM_TYPE_ERROR_CODE;
            }else if(mInternalAdapter.getItemViewType(position)==ITEM_TYPE_EMPTY_CODE){
                return  ITEM_TYPE_EMPTY_CODE;
            }else{
                int footerPosition = getItemCount()-1;
                System.out.println("getItemViewType footerPosition:"+footerPosition+"...position:"+position);
                if (footerPosition == position && mIsFooterEnable){
                    if(footerPosition>=(pageSize)){
                        System.out.println("getItemViewType TYPE_FOOTER:");
                        return TYPE_FOOTER;
                    }else{
                        //不足
                        System.out.println("getItemViewType TYPE_INSUFFICIENT:");
                        return TYPE_INSUFFICIENT;
                    }
                }
                /**
                 * 这么做保证layoutManager切换之后能及时的刷新上对的布局
                 */
                if (getLayoutManager() instanceof LinearLayoutManager) {
                    return TYPE_LIST;
                } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
                    return TYPE_STAGGER;
                } else {
                    return TYPE_NORMAL;
                }
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        mHeaderResId, parent, false));
            }else if (viewType == TYPE_FOOTER) {
                 footerViewHolder=new FooterViewHolder(  LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.foot_loading, parent, false));
                int widthMeasure=View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                int heightMeasure=View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                footerViewHolder.itemView.measure(widthMeasure, heightMeasure);
                footerViewHeight= footerViewHolder.itemView.getMeasuredHeight();
                return footerViewHolder;
            } else if(viewType==TYPE_INSUFFICIENT){
                return new InsufficientViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_insufficient, parent, false));
            }else{ // type normal
                return mInternalAdapter.onCreateViewHolder(parent, viewType);
            }
        }

        public class FooterViewHolder extends RecyclerView.ViewHolder {

            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class HeaderViewHolder extends RecyclerView.ViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class InsufficientViewHolder extends RecyclerView.ViewHolder {
            public InsufficientViewHolder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if ((type != TYPE_FOOTER && type != TYPE_HEADER && type!=TYPE_INSUFFICIENT) || //
                            type==ITEM_TYPE_ERROR_CODE || type==ITEM_TYPE_EMPTY_CODE){
                //normal的情况下
                mInternalAdapter.onBindViewHolder(holder, position);
            }else if (type == TYPE_FOOTER) {
                if (null != mListener && mIsFooterEnable && !mIsLoadingMore) {
                    setLoadingMore(true);
                    loadMoreState=LOAD_MORE_ING;
                    mListener.onLoadMore();
                }
                footerViewHolder.itemView.setVisibility(View.VISIBLE);
            }else if(type==TYPE_INSUFFICIENT){
//                UiUtils.getInstance().showToast("首页不足"+pageSize+"不能加载更多！");
            }
        }


        /**
         * 需要计算上加载更多和添加的头部俩个
         *
         * @return
         */
        @Override
        public int getItemCount() {
            //进行加1:footView
            int count = mInternalAdapter.getItemCount()+1;
            return count;
        }

        public void setHeaderEnable(boolean enable) {
            mIsHeaderEnable = enable;
        }

        public void addHeaderView(int resId) {
            mHeaderResId = resId;
        }
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter != null) {
            mAutoLoadAdapter = new AutoLoadAdapter(adapter);
        }
        super.setAdapter(mAutoLoadAdapter);
    }

    public AutoLoadAdapter getmAutoLoadAdapter() {
        return mAutoLoadAdapter;
    }

    public void setmAutoLoadAdapter(AutoLoadAdapter mAutoLoadAdapter) {
        this.mAutoLoadAdapter = mAutoLoadAdapter;
    }

    /**
     * 切换layoutManager
     *
     * 为了保证切换之后页面上还是停留在当前展示的位置，记录下切换之前的第一条展示位置，切换完成之后滚动到该位置
     * 另外切换之后必须要重新刷新下当前已经缓存的itemView，否则会出现布局错乱（俩种模式下的item布局不同），
     * RecyclerView提供了swapAdapter来进行切换adapter并清理老的itemView cache
     *
     * @param layoutManager
     */
    public void switchLayoutManager(LayoutManager layoutManager) {
        int firstVisiblePosition = getFirstVisiblePosition();
//        getLayoutManager().removeAllViews();
        setLayoutManager(layoutManager);
        //super.swapAdapter(mAutoLoadAdapter, true);
        getLayoutManager().scrollToPosition(firstVisiblePosition);
    }

    /**
     * 获取第一条展示的位置
     *
     * @return
     */
    private int getFirstVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMinPositions(lastPositions);
        } else {
            position = 0;
        }
        return position;
    }

    /**
     * 获得当前展示最小的position
     *
     * @param positions
     * @return
     */
    private int getMinPositions(int[] positions) {
        int size = positions.length;
        int minPosition = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            minPosition = Math.min(minPosition, positions[i]);
        }
        return minPosition;
    }

    /**
     * 获取最后一条展示的位置
     *
     * @return
     */
    private int getLastVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    /**
     * 添加头部view
     *
     * @param resId
     */
    public void addHeaderView(int resId) {
        mAutoLoadAdapter.addHeaderView(resId);
    }

    /**
     * 设置头部view是否展示
     * @param enable
     */
    public void setHeaderEnable(boolean enable) {
        mAutoLoadAdapter.setHeaderEnable(enable);
    }

    /**
     * 设置是否支持自动加载更多
     *
     * @param autoLoadMore
     */
    public void setAutoLoadMoreEnable(boolean autoLoadMore) {
        mIsFooterEnable = autoLoadMore;
    }

    /**
     * 通知更多的数据已经加载
     *
     * 每次加载完成之后添加了Data数据，用notifyItemRemoved来刷新列表展示，
     * 而不是用notifyDataSetChanged来刷新列表
     *
     * @param hasMore
     */
    public void notifyMoreFinish(boolean hasMore) {
        System.out.println("notifyMoreFinish mLoadMorePosition:" + mLoadMorePosition);
        mAutoLoadAdapter.notifyItemRemoved(mLoadMorePosition);
        scrollBy(0,-footerViewHeight);
        System.out.println("onBindViewHolder footerViewHolder GONE");
        setLoadingMore(false);
        loadMoreState=LOAD_MORE_FINISHED;
    }


    public int getFooterViewHeight() {
        return footerViewHeight;
    }

    public void setFooterViewHeight(int footerViewHeight) {
        this.footerViewHeight = footerViewHeight;
    }

    boolean isLanJie;

    public boolean isLanJie() {
        return isLanJie;
    }

    public void setIsLanJie(boolean isLanJie) {
        this.isLanJie = isLanJie;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        System.out.println("LoadMoreRecyclerView onTouchEvent ..........");
        if(isLanJie) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    super.onTouchEvent(e);
                    break;

                case MotionEvent.ACTION_UP:
                    super.onTouchEvent(e);
                    break;
            }
            return false;
        }else{
            return super.onTouchEvent(e);
        }
    }
}

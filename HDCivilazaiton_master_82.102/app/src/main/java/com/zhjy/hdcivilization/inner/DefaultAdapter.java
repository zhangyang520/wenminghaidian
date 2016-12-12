package com.zhjy.hdcivilization.inner;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.List;

public abstract class DefaultAdapter<Data> extends BaseAdapter {
    protected List<Data> datas;
    private static final int DEFAULT_ITEM = 0;
    private static final int MORE_ITEM = 1;
    private ListView lv;

    public List<Data> getDatas() {
        return datas;
    }

    public void setDatas(List<Data> datas) {
        this.datas = datas;
        System.out.println("setDatas ....datas size:"+(datas.size()));
        //进行对数据进行处理
        processDatasList();
    }

    public DefaultAdapter(List<Data> datas, ListView lv) {
        this.datas = datas;
        //进行对数据进行处理
        processDatasList();
//		lv.setOnItemClickListener(this);
        this.lv = lv;
    }

    // ListView item的点击事件
//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position,
//			long id) {
//		//Toast.makeText(UiUtils.getContext(), "position:"+position, 0).show();
//		position=position-lv.getHeaderViewsCount();//头结点的个数
//		onInnerItemClick(position);
//	}

    //上层需要实现的点击内容的条目事件
    public void onInnerItemClick(int position) {

    }

    @Override
    public int getCount() {
        return datas.size(); // 最后的一个条目 就是加载更多的条目
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseHolder holder = null;

        if (convertView == null) {
            holder = getHolder();
        } else {
            holder = (BaseHolder) convertView.getTag();
        }
        if (position < datas.size()) {
            holder.setData(datas.get(position), position);
        }

//        View view=getViewMethod(holder,position);
//        System.out.println("getView .......111111111");
//        if(view==null){
//            return holder.getContentView();  //进行获取显示view的对象
//        }else{
//            return view;
//        }
         //进行对convertView对应的子类的处理

        processConvertView(holder.getContentView());
        return holder.getContentView();
    }


    protected abstract BaseHolder<Data> getHolder();

    /**
     * 交给上层完成的抽像方法
     */
    protected abstract void processDatasList();

    protected View getViewMethod(BaseHolder baseHolder,int position){
        return null;
    }
    protected  void processConvertView(View view){

    }

    public ListView getLv() {
        return lv;
    }

    public void setLv(ListView lv) {
        this.lv = lv;
    }
}

package com.zhjy.hdcivilization.inner;

import android.view.View;

import java.util.List;

public abstract class BaseHolder<Data> {
    private int type;
    private View contentView;
    private Data data;
    private int position;
    private int size;
    private List<Data> datas;

    public BaseHolder() {
        contentView = initView();
        contentView.setTag(this);
    }

    public BaseHolder(int type) {
        this.type = type;
        contentView = initView();
        System.out.println("BaseHolder contentView is null:"+(contentView==null));
        contentView.setTag(this);
    }

    /**
     * 初始化view
     */
    public abstract View initView();

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public void setData(Data data) {
        this.data = data;
        refreshView(data);
    }

    public void setData(Data data, int position) {
        this.data = data;
        this.position = position;
        refreshView(data);
    }

    /**
     * 刷新数据
     */
    public abstract void refreshView(Data data);

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * @return the data
     */
    public Data getData() {
        return data;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Data> getDatas() {
        return datas;
    }

    public void setDatas(List<Data> datas) {
        this.datas = datas;
    }
}

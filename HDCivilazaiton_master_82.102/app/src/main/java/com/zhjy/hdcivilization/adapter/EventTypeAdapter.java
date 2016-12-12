package com.zhjy.hdcivilization.adapter;

import android.widget.ListView;

import com.zhjy.hdcivilization.entity.HDC_EventType;
import com.zhjy.hdcivilization.holder.EventTypeHolder;
import com.zhjy.hdcivilization.inner.BaseHolder;
import com.zhjy.hdcivilization.inner.DefaultAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/7/26.
 */
public class EventTypeAdapter extends DefaultAdapter<HDC_EventType>{
    public EventTypeAdapter(List<HDC_EventType> strings, ListView lv) {
        super(strings, lv);
    }

    @Override
    protected BaseHolder<HDC_EventType> getHolder() {
        return new EventTypeHolder();
    }

    @Override
    protected void processDatasList() {

    }
}

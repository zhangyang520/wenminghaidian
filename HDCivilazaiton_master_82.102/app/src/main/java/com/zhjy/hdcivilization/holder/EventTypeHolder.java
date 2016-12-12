package com.zhjy.hdcivilization.holder;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.entity.HDC_EventType;
import com.zhjy.hdcivilization.inner.BaseHolder;
import com.zhjy.hdcivilization.utils.UiUtils;

/**
 * Created by Administrator on 2016/7/26.
 */
public class EventTypeHolder extends BaseHolder<HDC_EventType>{

    TextView tv_event_type;
    @Override
    public View initView() {
        View contentView= UiUtils.getInstance().inflate(R.layout.event_type_string);
        tv_event_type=(TextView)contentView.findViewById(R.id.tv_event_type);
        return contentView;
    }

    @Override
    public void refreshView(HDC_EventType s) {
        tv_event_type.setText(s.getEventName());
    }
}

package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.adapter.EventTypeAdapter;
import com.zhjy.hdcivilization.entity.HDC_EventType;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.utils.UiUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 事件类型的跳转页
 */
public class SuperviseEventTypeActivity extends BaseActivity implements View.OnClickListener {

    ListView lv_event_type;
    List<String> eventTypeList;
    ImageView btn_back;
    private RelativeLayout rl_back;
    public static  String eventTypeKey="eventTypeKey";
    public static  String eventTypeIndex="eventTypeIndex";
    private List<HDC_EventType> hdc_eventTypes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        customLayout=R.layout.activity_supervise_event_type;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        lv_event_type=(ListView)findViewById(R.id.lv_event_type);

        btn_back=(ImageView)findViewById(R.id.btn_back);
        rl_back=(RelativeLayout)findViewById(R.id.rl_back);
        //进行读取本地数据
        String[] eventTypeDatas=UiUtils.getInstance().getContext().//
                                        getResources().getStringArray(//
                                                R.array.supervise_event_type_datas);

        eventTypeList=Arrays.asList(eventTypeDatas);
        hdc_eventTypes=new ArrayList<HDC_EventType>();
        int i=1;
        for (String data:eventTypeList){
            //进行遍历字符串

                HDC_EventType eventType=new HDC_EventType();
                String str[]=data.split("----");
                eventType.setEventName(str[0]);
                eventType.setIndex(str[1]);
                hdc_eventTypes.add(eventType);
            i++;
        }
        EventTypeAdapter eventTypeAdapter = new EventTypeAdapter(hdc_eventTypes, lv_event_type);
        lv_event_type.setAdapter(eventTypeAdapter);
    }

    @Override
    protected void initInitevnts() {
        lv_event_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HDC_EventType value = hdc_eventTypes.get(position);
                Bundle bundle = new Bundle();
                System.out.println("value.getEventName():"+value.getEventName()+"....eventTypeIndex:"+value.getIndex());
                bundle.putString(eventTypeKey, value.getEventName());
                bundle.putString(eventTypeIndex,value.getIndex());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(MySubSuperviseActivity.EVENT_TYPE_RESULT_CODE, intent);
                finish();
            }
        });
        rl_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                //结束处理
                finish();
                break;
        }
    }
}

package com.zhjy.hdcivilization.view;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.utils.UiUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/7/27.
 */
public class SupervisePositionPopup {
    public static SupervisePositionPopup instance=new SupervisePositionPopup();
    private PopupWindow popupWindow;
    List<String> positionDatas=null;
    private SupervisePositionPopup(){

    }

    /**
     * 进行展现出popup
     * @param parent
     */
    public void showPopup(View parent,final SupervisePositionCallbackListener listener,final List<String> positionList){
        if(popupWindow==null){
            popupWindow=new PopupWindow(UiUtils.getInstance().getContext());
        }
        //进行设置内容
        ListView listView=new ListView(UiUtils.getInstance().getContext());
        listView.setCacheColorHint(UiUtils.getInstance().getContext().getResources().getColor(R.color.white));
        ColorDrawable colorDrawable=new ColorDrawable(UiUtils.getInstance().getContext().getResources().getColor(R.color.supervise_mysubmit_line_color));
        listView.setDivider(colorDrawable);
        listView.setDividerHeight(UiUtils.getDimen(R.dimen.supervise_mysub_line_height));
        listView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
        listView.setBackgroundDrawable(UiUtils.getInstance().getDrawable(R.drawable.supervise_position_background));
//        listView.setBackgroundColor(UiUtils.getInstance().getContext().getResources().getColor(R.color.activity_background_color));
//        String positionArray[]=UiUtils.getInstance().getContext().getResources().getStringArray(R.array.supervise_position_array);
//        positionDatas=Arrays.asList(positionArray);
        listView.setAdapter(new ArrayAdapter<String>(UiUtils.getInstance().getContext(), R.layout.supervise_popup_position, R.id.tv_supervise_position, positionList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.getPosition(positionList.get(position));
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setContentView(listView);
        //进行设置popupWindow的属性
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setBackgroundDrawable(UiUtils.getInstance().getDrawable(R.drawable.supervise_position_rect));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(UiUtils.getInstance().getDefaultWidth()-2*UiUtils.getDimen(R.dimen.popup_position_margin));
        popupWindow.showAsDropDown(parent, 30, UiUtils.getDimen(R.dimen.supervise_mysub_item_height)/5);
    }

    public interface SupervisePositionCallbackListener{
        void getPosition(String position);
    }
}

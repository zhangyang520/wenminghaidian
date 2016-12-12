package com.zhjy.hdcivilization.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zhjy.hdcivilization.R;

/**
 * @author :huangxianfeng on 2016/8/14.
 */
public class LoginSuccessPopupWindow extends PopupWindow {

    View loginSuccess;
    TextView perfectInfo;
    Button i_known,applyVolunteer;


    @SuppressLint("InflateParams")
    public LoginSuccessPopupWindow(Context context, View.OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        loginSuccess = inflater.inflate(R.layout.login_success_ui, null);
        perfectInfo = (TextView) loginSuccess.findViewById(R.id.perfect);
        i_known = (Button) loginSuccess.findViewById(R.id.btn_i_known);
        applyVolunteer = (Button) loginSuccess.findViewById(R.id.btn_apply_volunteer);
        // 设置按钮监听
        perfectInfo.setOnClickListener(itemsOnClick);
        i_known.setOnClickListener(itemsOnClick);
        applyVolunteer.setOnClickListener(itemsOnClick);

        // 设置SelectPicPopupWindow的View
        this.setContentView(loginSuccess);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.PopupAnimation);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x80000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        loginSuccess.setOnTouchListener(new View.OnTouchListener() {

            @Override
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {

                int height = loginSuccess.findViewById(R.id.login_rl_success).getTop();

                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

    }



}

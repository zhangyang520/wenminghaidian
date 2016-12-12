package com.zhjy.hdcivilization.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.utils.UiUtils;

/**
 * @author :huangxianfeng on 2016/8/31.
 * 权限过低时出现的PopupWindow处理逻辑
 */
public class OKPopup {

    private  static OKPopup instance=new OKPopup();
    private PopupWindow popupWindow;
    private View contentView;
    private TextView txt_warnging;
    private Button btn_ok;
    private Button btn_cancel;
    private RelativeLayout rl_ok,rl_cancel;
    Dialog dialog;
    private OKPopup(){

    }

    public static OKPopup getInstance(){
        if(instance==null){
            instance=new OKPopup();
        }
        return instance;
    }
    /**
     * 进行展现出popup
     * @param parent
     */
    public void showPopup(View parent,final BtnClickListener btnClickListener,boolean showOk,String content){

        popupWindow=new PopupWindow(UiUtils.getInstance().getContext());
        contentView=UiUtils.getInstance().inflate(R.layout.popup_ok_popup);
        txt_warnging=(TextView)contentView.findViewById(R.id.warning_txt);
        txt_warnging.setText(content);
        btn_ok =(Button)contentView.findViewById(R.id.btn_ok);

        rl_ok =(RelativeLayout)contentView.findViewById(R.id.rl_ok);

        //进行设置内容
        popupWindow.setContentView(contentView);
        //进行设置popupWindow的属性
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);

        btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (btnClickListener != null) {
                            btnClickListener.btnOk();
                        }
            }
        });
    }


    /*
      进行显示Dialog
     */
    public void showPopup(Context context,final BtnClickListener btnClickListener,boolean showOk,String content){
        dialog=new Dialog(context,R.style.theme_dialog);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        final View view=UiUtils.getInstance().inflate(R.layout.popup_ok_popup);

        txt_warnging=(TextView)view.findViewById(R.id.warning_txt);
        txt_warnging.setText(content);
        btn_ok =(Button)view.findViewById(R.id.btn_ok);

        rl_ok =(RelativeLayout)view.findViewById(R.id.rl_ok);

        dialog.show();
        dialog.getWindow().setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setContentView(view);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;//显示dialog的时候,就显示软键盘
//        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致不能获取焦点,默认的是FLAG_NOT_FOCUSABLE,故名思义不能获取输入焦点,
        params.dimAmount = 0.5f;//设置对话框的透明程度背景(非布局的透明度)
        params.windowAnimations = R.style.PopupAnimation;
        dialog.getWindow().setAttributes(params);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnClickListener != null) {
                    btnClickListener.btnOk();
                }
            }
        });
    }

    /**
     * 点击发送评论的接口
     */
    public interface BtnClickListener{
        //点击发送评论方法
        void btnOk();
    }

    /**
     * 進行消失....
     */
    public void dismiss(){
        if(popupWindow!=null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }

    public void dismissDialog(){
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }
}

package com.zhjy.hdcivilization.view;

import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.utils.UiUtils;


/**
 * 提示框
 * Created by zhangyang on 2016/7/27.
 */
public class WarningPopup {
    private  static WarningPopup instance=new WarningPopup();
    private PopupWindow popupWindow;
    private View contentView;
    private TextView txt_warnging;
    private Button btn_ok;
    private Button btn_cancel;
    private RelativeLayout rl_ok,rl_cancel;
    private WarningPopup(){

    }

    public static WarningPopup getInstance(){
        if(instance==null){
            instance=new WarningPopup();
        }
        return instance;
    }
    /**
     * 进行展现出popup
     * @param parent
     */
    public void showPopup(View parent,final BtnClickListener btnClickListener,boolean showOk,boolean showCancel,String content){
//          if(popupWindow==null){
               popupWindow=new PopupWindow(UiUtils.getInstance().getContext());
               contentView=UiUtils.getInstance().inflate(R.layout.popup_warning);
//           }
           txt_warnging=(TextView)contentView.findViewById(R.id.warning_txt);
            txt_warnging.setText(content);
           btn_ok =(Button)contentView.findViewById(R.id.btn_ok);
           btn_cancel =(Button)contentView.findViewById(R.id.btn_cancel);
           rl_ok =(RelativeLayout)contentView.findViewById(R.id.rl_ok);
           rl_cancel =(RelativeLayout)contentView.findViewById(R.id.rl_cancel);
           rl_ok.setVisibility(showOk?View.VISIBLE:View.GONE);
           rl_cancel.setVisibility(showCancel ? View.VISIBLE : View.GONE);

           if(contentView.getParent()!=null){
               ((ViewGroup)(contentView.getParent())).removeView(contentView);
           }
            //进行设置内容
            popupWindow.setContentView(contentView);
            //进行设置popupWindow的属性
            popupWindow.setAnimationStyle(R.style.PopupAnimation);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setWidth(UiUtils.getDimen(R.dimen.popup_width));
            popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnClickListener != null) {
                        btnClickListener.btnOk();
                    }
                }
            });

           btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnClickListener!=null){
                    btnClickListener.btnCancel();
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
        void btnCancel();
    }

    /**
     * 進行消失....
     */
    public void dismiss(){
        if(popupWindow!=null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }
}

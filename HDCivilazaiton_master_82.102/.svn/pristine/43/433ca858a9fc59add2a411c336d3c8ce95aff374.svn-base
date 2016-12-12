package com.zhjy.hdcivilization.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.utils.ToolUtils;
import com.zhjy.hdcivilization.utils.UiUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/7/27.
 */
public class SendCommentPopup {
    public static SendCommentPopup instance = new SendCommentPopup();
    private PopupWindow popupWindow;
    private View contentView;
    private EditText ed_send_comment;
    private Button btn_send_comment;
    Dialog dialog;
    private SendCommentPopup() {

    }

    /**
     * 进行展现出popup
     *
     * @param parent
     */
    public void showPopup(View parent, final BtnSendCommentListener sendCommentListener) {
        if(popupWindow==null){
            popupWindow=new PopupWindow(UiUtils.getInstance().getContext());
            contentView=UiUtils.getInstance().inflate(R.layout.popup_send_comment);
         }
            ed_send_comment=(EditText)contentView.findViewById(R.id.ed_send_comment);
            btn_send_comment=(Button)contentView.findViewById(R.id.btn_send_comment);
            btn_send_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sendCommentListener != null) {
                        sendCommentListener.sendEditComment(ed_send_comment);
                    }
                }
            });

        ed_send_comment.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                }
                return false;
            }
        });
        ed_send_comment.setText("");
        //进行设置内容
        popupWindow.setContentView(contentView);
        //进行设置popupWindow的属性
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ToolUtils.getInstance().closeKeyBoard(ed_send_comment);
            }
        });
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ((InputMethodManager) UiUtils.getInstance().getContext().getSystemService(UiUtils.getInstance().getContext().INPUT_METHOD_SERVICE))
                        .toggleSoftInput(0,
                                InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 500);
    }

    public void showPopup(AppCompatActivity context, final BtnSendCommentListener sendCommentListener) {

//        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog=new Dialog(context,R.style.theme_dialog);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialog.setCanceledOnTouchOutside(true);
        final View view=UiUtils.getInstance().inflate(R.layout.popup_send_comment);
//        dialog.setView(view,0,0,0,0);
//        dialog.setContentView(view);
        dialog.show();

        dialog.getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setContentView(view);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;//显示dialog的时候,就显示软键盘
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;//就是这个属性导致不能获取焦点,默认的是FLAG_NOT_FOCUSABLE,故名思义不能获取输入焦点,
        params.dimAmount = 0.5f;//设置对话框的透明程度背景(非布局的透明度)
        params.windowAnimations = R.style.PopupAnimation;
        dialog.getWindow().setAttributes(params);

        ed_send_comment=(EditText)view.findViewById(R.id.ed_send_comment);
        btn_send_comment=(Button)view.findViewById(R.id.btn_send_comment);
        btn_send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendCommentListener != null) {
                    sendCommentListener.sendEditComment(ed_send_comment);
                }
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    ToolUtils.getInstance().closeKeyBoard(ed_send_comment);
                }
                return false;
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ToolUtils.getInstance().closeKeyBoard(ed_send_comment);
            }
        });
    }

    /**
     * 点击发送评论的接口
     */
    public interface BtnSendCommentListener {
        //点击发送评论方法
        void sendEditComment(EditText editText);
    }

    /**
     * 進行消失....
     */
    public void dismiss() {
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

package com.zhjy.hdcivilization.view;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.utils.UiUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/7/27.
 */
public class ShareCommentPopup implements View.OnClickListener {
    public static ShareCommentPopup instance=new ShareCommentPopup();
    private PopupWindow popupWindow;
    private View contentView;
    private EditText ed_send_comment;
    private Button share_qq,share_weixin,share_Circle_friend,share_sign;
    private ShareOnClickListener shareOnClickListener;

    private ShareCommentPopup(){

    }

    public ShareOnClickListener getShareOnClickListener() {
        return shareOnClickListener;
    }

    public void setShareOnClickListener(ShareOnClickListener shareOnClickListener) {
        this.shareOnClickListener = shareOnClickListener;
    }

    /**
     * 进行展现出popup
     * @param parent
     */
    public void showPopup(View parent){
        if(popupWindow==null){
            popupWindow=new PopupWindow(UiUtils.getInstance().getContext());
            contentView=UiUtils.getInstance().inflate(R.layout.popup_share);
            ed_send_comment=(EditText)contentView.findViewById(R.id.ed_send_comment);
            share_qq=(Button)contentView.findViewById(R.id.share_qq);
            share_qq.setOnClickListener(this);
            share_sign=(Button)contentView.findViewById(R.id.share_sign);
            share_sign.setOnClickListener(this);
            share_weixin=(Button)contentView.findViewById(R.id.share_weixin);
            share_weixin.setOnClickListener(this);
            share_Circle_friend=(Button)contentView.findViewById(R.id.share_Circle_friend);
            share_Circle_friend.setOnClickListener(this);
        }
        //进行设置内容
        popupWindow.setContentView(contentView);
        //进行设置popupWindow的属性
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(UiUtils.getInstance().getDrawable(R.drawable.share_popup_rect));
//        popupWindow.setHeight(UiUtils.getDimen(R.dimen.supervise_popup_position_height));
        popupWindow.setHeight(UiUtils.getDimen(R.dimen.share_rect_height));
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }

    public interface  ShareOnClickListener{
        void shareOnClick(int viewId);
    }
    @Override
    public void onClick(View v) {
        if(shareOnClickListener!=null){
            shareOnClickListener.shareOnClick(v.getId());
        }
    }
}

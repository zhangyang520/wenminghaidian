package com.zhjy.hdcivilization.qqShare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.activity.CommentDetailActivity;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.utils.UiUtils;

/**
 * Created by Administrator on 2016/9/11.
 */
public class QQShareUtils {

    public static void share(AppCompatActivity appCompatActivity){
        final Bundle params = new Bundle();
//        if (shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
            params.putString(QQShare.SHARE_TO_QQ_TITLE, UiUtils.getInstance().getContext().getString(R.string.qqshare_title_content));
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, UiUtils.getInstance().getContext().getString(R.string.qqshare_targetUrl_content));
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, UiUtils.getInstance().getContext().getString(R.string.qqshare_summary_content));
//        }
//        if (shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
//            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl.getText().toString());
//        } else {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,  UiUtils.getInstance().getContext().getString(R.string.qqshare_imageUrl_content));
//        }
//        params.putString(shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE ? QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL
//                : QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl.getText().toString());
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, UiUtils.getInstance().getContext().getString(R.string.qqshare_appName_content));
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0x00);
//        if (shareType == QQShare.SHARE_TO_QQ_TYPE_AUDIO) {
//            params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, mEditTextAudioUrl.getText().toString());
//        }
//        if ((mExtarFlag & QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN) != 0) {
//            showToast("在好友选择列表会自动打开分享到qzone的弹窗~~~");
//        } else if ((mExtarFlag & QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE) != 0) {
//            showToast("在好友选择列表隐藏了qzone分享选项~~~");
//        }
        doShareToQQ(params,appCompatActivity);
    }

    private static void doShareToQQ(Bundle params,AppCompatActivity appCompatActivity) {
        MyApplication.mTencent.shareToQQ(appCompatActivity, params, new IUiListener() {
            @Override
            public void onCancel() {
                UiUtils.getInstance().showToast("QQ onCancel");
            }
            @Override
            public void onComplete(Object response) {
                // TODO Auto-generated method stub
                UiUtils.getInstance().showToast("QQ onComplete");
            }
            @Override
            public void onError(UiError e) {
                // TODO Auto-generated method stub
                UiUtils.getInstance().showToast("QQ onError");
            }
        });
    }


    /**
     * 进行分享网页的连接
     * @param commentDetailActivity
     * @param bundle
     */
    public static void shareWebUrl(AppCompatActivity commentDetailActivity, Bundle bundle) {
        doShareToQQ(bundle,commentDetailActivity);
    }
}

package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tencent.connect.share.QQShare;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.utils.FileUtils;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.wxapi.WXEntryActivity;

import java.io.IOException;

/**
 * @author :huangxianfeng on 2016/8/4.
 * 我的界面---分享
 */
public class ShareActivity extends BaseActivity {


    private ImageView btnBack;
    private RelativeLayout rl_back;
    private Button share_weixin,share_weibo,share_Circle_friend,share_qq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        customLayout= R.layout.activity_share;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        btnBack = (ImageView)findViewById(R.id.btn_back);
        rl_back = (RelativeLayout)findViewById(R.id.rl_back);


        share_weixin = (Button)findViewById(R.id.share_weixin);
        share_weibo = (Button)findViewById(R.id.share_weibo);
        share_Circle_friend = (Button)findViewById(R.id.share_Circle_friend);
        share_qq = (Button)findViewById(R.id.share_qq);

    }

    @Override
    protected void initInitevnts() {

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //分享微信好友
        share_weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(ShareActivity.this, WXEntryActivity.class);
                intent.putExtra(HDCivilizationConstants.SHARE_SCENEFLAG,false);
                //进行设置图片的路径
                if(!FileUtils.getInstance().isExistsFile(MyApplication.shareZinCodePath)){
                    try {
                        FileUtils.getInstance().writeToDir(getAssets().open("mine_share_code.png"), MyApplication.shareZinCodePath);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                //HDCivilizationConstants.SHARE_IMG_PATH
                intent.putExtra(HDCivilizationConstants.SHARE_IMG_PATH, MyApplication.shareZinCodePath);
                intent.putExtra(HDCivilizationConstants.SHARE_TYPE, HDCivilizationConstants.SHARE_TYPE_IMAGE);
                startActivity(intent);
            }
        });

        /**
         * 分享微信朋友圈
         */
        share_Circle_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(ShareActivity.this, WXEntryActivity.class);
                intent.putExtra(HDCivilizationConstants.SHARE_SCENEFLAG,true);
                //进行设置图片的路径
                if(!FileUtils.getInstance().isExistsFile(MyApplication.shareZinCodePath)){
                    try {
                        FileUtils.getInstance().writeToDir(getAssets().open("mine_share_code.png"), MyApplication.shareZinCodePath);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                //HDCivilizationConstants.SHARE_IMG_PATH
                intent.putExtra(HDCivilizationConstants.SHARE_IMG_PATH, MyApplication.shareZinCodePath);
                intent.putExtra(HDCivilizationConstants.SHARE_TYPE, HDCivilizationConstants.SHARE_TYPE_IMAGE);
                startActivity(intent);
            }
        });

        /**
         * 分享至微博
         */
        share_weibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent =new Intent();
//                intent.setClass(ShareActivity.this, WBShareActivity.class);
//                intent.putExtra(HDCivilizationConstants.SHARE_TITLE, getLeftTitile("扫描二维码下载文明海淀app", HDCivilizationConstants.SHARE_WIEBO_MAX_TITLE_LENGTH));
//                intent.putExtra(HDCivilizationConstants.SHARE_DESRIPTION, getLeftTitile("扫描二维码下载文明海淀app", HDCivilizationConstants.SHARE_WEIBO_MAX_CONTENT_LENGTH));
//                //进行设置图片的路径
//                if(!FileUtils.getInstance().isExistsFile(MyApplication.shareZinCodePath)){
//                    try {
//                        FileUtils.getInstance().writeToDir(getAssets().open("mine_share_code.png"),MyApplication.shareZinCodePath);
//                    } catch (IOException e){
//                        e.printStackTrace();
//                    }
//                }
//                //HDCivilizationConstants.SHARE_IMG_PATH
//                intent.putExtra(HDCivilizationConstants.SHARE_IMG_PATH,MyApplication.shareZinCodePath);
//                intent.putExtra(HDCivilizationConstants.SHARE_TYPE, HDCivilizationConstants.SHARE_TYPE_IMAGE);
//                startActivity(intent);
            }
        });

        /**
         * 分享至qq
         */
        share_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                QQShareUtils.shareWebUrl(ShareActivity.this, getQQShareImageBundle());
            }
        });
    }


    /**
     * 进行获取qq分享网页类型的bundle
     *
     * @return
     */
    private Bundle getQQShareImageBundle() {
        Bundle bundle = new Bundle();
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, getLeftTitile("扫描二维码下载文明海淀app", HDCivilizationConstants.SHARE_QQ_MAX_TITLE_LENGTH));
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, getLeftTitile("扫描二维码下载文明海淀app", HDCivilizationConstants.SHARE_QQ_MAX_CONTENT_LENGTH));
        //进行设置图片的路径
        if(!FileUtils.getInstance().isExistsFile(MyApplication.shareZinCodePath)){
            try {
                FileUtils.getInstance().writeToDir(getAssets().open("mine_share_code.png"), MyApplication.shareZinCodePath);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, MyApplication.shareZinCodePath);
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, UiUtils.getInstance().getContext().getString(R.string.app_name));
        bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0x00);
        return bundle;
    }

    /**
     * @param title
     * @param maxLength 不能超过对应的字节数
     * @return
     */
    private String getLeftTitile(String title, int maxLength) {
        if (title.length() > maxLength) {
            title = title.substring(0, maxLength);
        }
        return title;
    }
}

/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 mob.com. All rights reserved.
 */

package com.zhjy.hdcivilization.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;


import com.lidroid.xutils.util.LogUtils;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.utils.FileUtils;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.UiUtils;

import java.io.IOException;
import java.net.URL;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	
	private Button gotoBtn, regBtn, launchBtn, checkBtn;
	
	// IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
	private boolean flag;
	private String title;
	private String description;
	private String shareFilePath;
	private String webpageUrl;
	private boolean sceneFlag;  //判断朋友圈的状态 true时是朋友圈 false微信好友
    private String shareType="";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //通过WXAPIFactory工厂，获取IWXAPI的实例
    	api = WXAPIFactory.createWXAPI(this, HDCivilizationConstants.WXAPP_ID, false);
    	//进行注册
    	api.registerApp(HDCivilizationConstants.WXAPP_ID);
        api.handleIntent(getIntent(), this);
        
		//进行参数的判断
		title=getIntent().getStringExtra(HDCivilizationConstants.SHARE_TITLE);
		description=getIntent().getStringExtra(HDCivilizationConstants.SHARE_DESRIPTION);
		shareFilePath=getIntent().getStringExtra(HDCivilizationConstants.SHARE_IMG_PATH);
		webpageUrl=getIntent().getStringExtra(HDCivilizationConstants.SHARE_TARGET_URL);
		sceneFlag=getIntent().getBooleanExtra(HDCivilizationConstants.SHARE_SCENEFLAG, false);
		shareType=getIntent().getStringExtra(HDCivilizationConstants.SHARE_TYPE);
		share2Wx();
    }

	
	@Override
	protected void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
//		Toast.makeText(UiUtils.getContext(), "发送了请求", Toast.LENGTH_SHORT).show();
	}

	//第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		int result = 0;
		switch (resp.errCode){
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
			
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		finish();
	}
	
	
	//新添加的方法
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	/**
	 *
	 * @param
	 */
	private void share2Wx(){
		 // 通过WXAPIFactory工厂，获取IWXAPI的实例
		 //首先进行检测微信的版本
		 int wxSdkVersion = api.getWXAppSupportAPI();
	     if (api.isWXAppSupportAPI() && api.isWXAppInstalled()){
				 //进行支持:看是否分享到朋友圈还是微信的好友
			    if(shareType!=null && shareType.equals(HDCivilizationConstants.SHARE_TYPE_WEBURL)){
					//网络类型
					WXWebpageObject webpage = new WXWebpageObject();
					webpage.webpageUrl =this.webpageUrl;
					final WXMediaMessage msg = new WXMediaMessage(webpage);
					msg.mediaObject=webpage;

					//待定
					msg.title = title;
					msg.description =description ;
					if(shareFilePath!=null && !shareFilePath.equals("")){
//						if(shareFilePath.startsWith("http://")){
//							new Thread(){
//								@Override
//								public void run() {
//										try {
//											Bitmap bmp=BitmapFactory.decodeStream(new URL(shareFilePath).openStream());
//											Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp,99,99, true);
//											bmp.recycle();
//											msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
//										} catch (IOException e) {
//											e.printStackTrace();
//										}
//								}
//							}.start();
//						}else{
//							//本地路径
//							System.out.println("WX FILE PATH:"+shareFilePath);
//							Bitmap bmp = BitmapFactory.decodeFile(shareFilePath);
//							Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp,99,69, true);
//							bmp.recycle();
//							msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
//						}
						//本地路径
						System.out.println("WX FILE PATH:"+shareFilePath);
						Bitmap bmp = BitmapFactory.decodeFile(shareFilePath);
						Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp,99,69, true);
						bmp.recycle();
						msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
					}else{
						Bitmap bmp =BitmapFactory.decodeResource(//
								UiUtils.getInstance().getContext().getResources(), R.mipmap.ic_launcher_logo);
						Bitmap thumbBmp=Bitmap.createScaledBitmap(bmp, 99, 69, true);
						bmp.recycle();
						msg.thumbData=Util.bmpToByteArray(thumbBmp, true);
					}
					SendMessageToWX.Req req = new SendMessageToWX.Req();
					req.transaction = buildTransaction("webpage");
					req.message = msg;
					req.scene = sceneFlag ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
					api.sendReq(req);
					finish();
				}else if(shareType!=null && shareType.equals(HDCivilizationConstants.SHARE_TYPE_IMAGE)){

					//网络类型
					final WXImageObject imgObject = new WXImageObject();
					WXMediaMessage msg = new WXMediaMessage(imgObject);

					//
					if(shareFilePath!=null && !shareFilePath.equals("")){
						System.out.println("WX FILE PATH:"+shareFilePath);
//						if(shareFilePath.startsWith("http://")){
//							new Thread(){
//								@Override
//								public void run() {
//									try {
//										Bitmap bmp=BitmapFactory.decodeStream(new URL(shareFilePath).openStream());
//										Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp,99,99, true);
//										bmp.recycle();
//										imgObject.imagePath=shareFilePath;
//										imgObject.imageData = Util.bmpToByteArray(thumbBmp, true);
//										imgObject.imageUrl=shareFilePath;
//									} catch (IOException e) {
//										e.printStackTrace();
//									}
//								}
//							}.start();

//						}else{
//							//本地路径
//							Bitmap bmp = BitmapFactory.decodeFile(shareFilePath);
//							Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp,99,99, true);
//							bmp.recycle();
//							imgObject.imagePath=shareFilePath;
//							imgObject.imageData = Util.bmpToByteArray(thumbBmp, true);
//							imgObject.imageUrl=shareFilePath;
//						}

						Bitmap bmp = BitmapFactory.decodeFile(shareFilePath);
						Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp,99,99, true);
						bmp.recycle();
						imgObject.imagePath=shareFilePath;
						imgObject.imageData = Util.bmpToByteArray(thumbBmp, true);
						imgObject.imageUrl=shareFilePath;
					}else{
						Bitmap bmp =BitmapFactory.decodeResource(//
								UiUtils.getInstance().getContext().getResources(), R.mipmap.ic_launcher_logo);
						Bitmap thumbBmp=Bitmap.createScaledBitmap(bmp, 99, 99, true);
						bmp.recycle();
						imgObject.imagePath=shareFilePath;
						imgObject.imageData = Util.bmpToByteArray(thumbBmp, true);
						imgObject.imageUrl=shareFilePath;
					}

//					if(!FileUtils.getInstance().isExistsFile(MyApplication.shareZinCodePath)){
//						try {
//							FileUtils.getInstance().writeToDir(getAssets().open("mine_share_code.png"),MyApplication.shareZinCodePath);
//						} catch (IOException e){
//							e.printStackTrace();
//						}
//					}
//					Bitmap bmp =BitmapFactory.decodeFile(MyApplication.shareZinCodePath);
//					Bitmap thumbBmp=Bitmap.createScaledBitmap(bmp, 99, 99, true);
//					imgObject.imagePath=MyApplication.shareZinCodePath;
//					imgObject.imageData=Util.bmpToByteArray(thumbBmp, true);
//					imgObject.imageUrl=MyApplication.shareZinCodePath;
//					bmp.recycle();

					msg.thumbData=imgObject.imageData;
					//待定
					msg.title = "文明海淀app";
					msg.description ="文明海淀app 文明监督" ;
					//待定
					SendMessageToWX.Req req = new SendMessageToWX.Req();
					req.transaction = buildTransaction("webpage");
					req.message = msg;
					req.scene = sceneFlag ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
					api.sendReq(req);
					finish();
				}

	     }else{
	    	 	Toast.makeText(UiUtils.getInstance().getContext(),
							"您的微信的版本过低版本,或者未装微信客户端", Toast.LENGTH_LONG).show();
	     }
	}
}

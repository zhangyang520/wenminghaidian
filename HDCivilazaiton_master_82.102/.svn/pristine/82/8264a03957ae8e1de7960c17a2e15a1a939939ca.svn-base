package com.zhjy.hdcivilization.wbshare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.api.BaseMediaObject;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.mm.sdk.platformtools.Util;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.UiUtils;

import java.io.IOException;
import java.net.URL;

/**
 * 微博分享的主类
 * @author zhangyang
 *
 */
public class WBShareActivity extends Activity implements IWeiboHandler.Response{
	
	 /** 微博微博分享接口实例 */
    private IWeiboShareAPI  mWeiboShareAPI = null;
    private final int SUMB_X=99;
    private final int SUMB_Y=99;
    
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    
    private AuthInfo mAuthInfo;
    
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    
    /**
     * 传递过来的数据
     */
    private boolean flag;
    private String title;
    private String description;
    private String shareFilePath;
    private String webpageUrl;

    private String shareType;//分享的类型:网页分享和图片分享

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 创建微博分享接口实例
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
		
		// 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
		// 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
		// NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
		mWeiboShareAPI.registerApp();
		
		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
		// 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
		// 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
		// 失败返回 false，不调用上述回调
		mWeiboShareAPI.handleWeiboResponse(getIntent(),this);

        title=getIntent().getStringExtra(HDCivilizationConstants.SHARE_TITLE);
        description=getIntent().getStringExtra(HDCivilizationConstants.SHARE_DESRIPTION);
        shareFilePath=getIntent().getStringExtra(HDCivilizationConstants.SHARE_IMG_PATH);
        webpageUrl=getIntent().getStringExtra(HDCivilizationConstants.SHARE_TARGET_URL);
        shareType=getIntent().getStringExtra(HDCivilizationConstants.SHARE_TYPE);

		mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(WBShareActivity.this, mAuthInfo);
        mSsoHandler.authorize(new AuthListener());
		
	}
	
	private void shareContent(){
//		Toast.makeText(UiUtils.getContext(), "flag:"+flag, 0).show();
        if(shareType!=null && shareType.equals(HDCivilizationConstants.SHARE_TYPE_WEBURL)) {
            sendMultiMessage(title, description, shareFilePath, webpageUrl);
        }else if(shareType!=null && shareType.equals(HDCivilizationConstants.SHARE_TYPE_IMAGE)){
            //图片类型的分享
            sendImgMessage(title, description, shareFilePath, webpageUrl);
        }
	}


    /**
     * 进行发送图片类型的信息
     * @param title
     * @param description
     * @param shareFilePath
     * @param webpageUrl
     */
    private void sendImgMessage(String title, String description, String shareFilePath, String webpageUrl) {
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
//        weiboMessage.mediaObject = getWebpageObj(title,description,shareFilePath,webpageUrl);
        weiboMessage.textObject=getTextObject(title);
        if(shareFilePath!=null){
            weiboMessage.imageObject=getImageObject(shareFilePath);
        }
        //2.初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        //如果安装了客户端:则进行用客户端
        if(mWeiboShareAPI.isWeiboAppInstalled()){
            int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
            if (supportApi >=10351){
                mWeiboShareAPI.sendRequest(WBShareActivity.this, request);
            }else{
                Toast.makeText(UiUtils.getInstance().getContext(), "微博客户端版本低过低!", Toast.LENGTH_SHORT).show();
            }
        }else{
            //先不考虑新闻客户端：
            AuthInfo authInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
            String token = "";
            if (accessToken != null) {
                token = accessToken.getToken();
            }
            mWeiboShareAPI.sendRequest(this, request, authInfo, token, new WeiboAuthListener() {

                @Override
                public void onWeiboException(WeiboException arg0 ) {
                    Toast.makeText(getApplicationContext(), "分享异常", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onComplete(Bundle bundle){
                    Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                    AccessTokenKeeper.writeAccessToken(getApplicationContext(), newToken);
                    //Toast.makeText(getApplicationContext(), "onAuthorizeComplete token = " + newToken.getToken(), 0).show();
//                  Toast.makeText(getApplicationContext(), "成功分享", 0).show();

                }

                @Override
                public void onCancel(){
                    Toast.makeText(getApplicationContext(), "取消分享!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    /**
	 * 实现onResponse方法
	 */
	@Override
	public void onResponse(BaseResponse baseResp){
		switch (baseResp.errCode){
	        case WBConstants.ErrorCode.ERR_OK:
	            Toast.makeText(WBShareActivity.this, R.string.weibosdk_demo_toast_share_success, Toast.LENGTH_LONG).show();
	            //进行访问网络
	            break;
	        case WBConstants.ErrorCode.ERR_CANCEL:
	            Toast.makeText(WBShareActivity.this, R.string.weibosdk_demo_toast_share_canceled, Toast.LENGTH_LONG).show();
	            break;
	        case WBConstants.ErrorCode.ERR_FAIL:
	            Toast.makeText(WBShareActivity.this, 
	                    "code:"+WBConstants.ErrorCode.ERR_FAIL+"...."+getString(R.string.weibosdk_demo_toast_share_failed) + "Error Message: " + baseResp.errMsg, 
	                    Toast.LENGTH_LONG).show();
	            break;
        }
        //分享成功之后：
        finish();
	};
	
	/**
     * @see {@link Activity#onNewIntent}
     */	
    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        Log.e("onNewIntent", "onNewIntent");
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }
    
	/**
	 * 直接进行分享
     * @param
     * @param shareFilePath 
     * @param description 
     * @param title 
	 */
	public  void sendMultiMessage(String title, String description, String shareFilePath, String webpageUrl){
		  // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.mediaObject = getWebpageObj(title, description, shareFilePath, webpageUrl);
        weiboMessage.textObject=getTextObject(title);
        if(shareFilePath!=null){
        	weiboMessage.imageObject=getImageObject(shareFilePath);
        }
        //2.初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        
        //如果安装了客户端:则进行用客户端
        if(mWeiboShareAPI.isWeiboAppInstalled()){
        	int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
            if (supportApi >=10351){
            	mWeiboShareAPI.sendRequest(WBShareActivity.this, request);
            }else{
            	Toast.makeText(UiUtils.getInstance().getContext(), "微博客户端版本低过低!", Toast.LENGTH_SHORT).show();
            }
       }else{
        	//先不考虑新闻客户端：
            AuthInfo authInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
            String token = "";
            if (accessToken != null) {
                token = accessToken.getToken();
            }
            mWeiboShareAPI.sendRequest(this, request, authInfo, token, new WeiboAuthListener() {
                
                @Override
                public void onWeiboException(WeiboException arg0 ) {
                	 Toast.makeText(getApplicationContext(), "分享异常", Toast.LENGTH_SHORT).show();
                     finish();
                }
                
                @Override
                public void onComplete(Bundle bundle){
                    Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                    AccessTokenKeeper.writeAccessToken(getApplicationContext(), newToken);
                    //Toast.makeText(getApplicationContext(), "onAuthorizeComplete token = " + newToken.getToken(), 0).show();
//                  Toast.makeText(getApplicationContext(), "成功分享", 0).show();
                    
                }
                
                @Override
                public void onCancel(){
                	 Toast.makeText(getApplicationContext(), "取消分享!", Toast.LENGTH_SHORT).show();
                	 finish();
                }
            });
        }
	}
	
	/**
	 * 进行获取网络的obj
	 * @param
	 * @param shareFilePath 
	 * @param description 
	 * @param title 
	 * @return
	 */
	private  BaseMediaObject getWebpageObj(String title, String description, final String shareFilePath, String webpageUrl) {

		final WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        //mediaObject.title = mShareWebPageView.getTitle();
        mediaObject.title = title;
        //mediaObject.description = mShareWebPageView.getShareDesc();
        mediaObject.description =description;
        
      
      //设置 Bitmap 类型的图片到视频对象里
      if(shareFilePath!=null && !shareFilePath.equals("")){
          if(shareFilePath.startsWith("http://")){
              new Thread(){
                  @Override
                  public void run() {
                      try {
                          Bitmap bmp=BitmapFactory.decodeStream(new URL(shareFilePath).openStream());
                          Bitmap thumbBmp=Bitmap.createScaledBitmap(bmp,99,99, true);
                          bmp.recycle();
                          mediaObject.thumbData= Util.bmpToByteArray(thumbBmp, true);
                      } catch (IOException e) {
                          e.printStackTrace();
                      }
                  }
              }.start();

          }else {
              Bitmap bmp = BitmapFactory.decodeFile(shareFilePath);
              Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, SUMB_X, SUMB_Y, true);
              bmp.recycle();
              mediaObject.thumbData = Util.bmpToByteArray(thumbBmp, true);
          }
		}else{
			Bitmap bmp =BitmapFactory.decodeResource(//
				 		UiUtils.getInstance().getContext().getResources(), R.mipmap.ic_launcher_logo);
		   Bitmap thumbBmp=Bitmap.createScaledBitmap(bmp, 99, 99, true);
		   bmp.recycle();
		   mediaObject.thumbData=Util.bmpToByteArray(thumbBmp, true);
		}
        mediaObject.actionUrl =webpageUrl;
        mediaObject.defaultText = description;
        return mediaObject;
	}
	/**
	 * 进行获取图片的对象
	 * @param
	 * @return
	 */
	private ImageObject getImageObject(final String shareFilePath){
		final ImageObject imageObject=new ImageObject();
		 if(shareFilePath!=null && !shareFilePath.equals("")){
             if(shareFilePath.startsWith("http://")){
                 new Thread(){
                     @Override
                     public void run() {
                         try {
                             Bitmap bmp=BitmapFactory.decodeStream(new URL(shareFilePath).openStream());
                             Bitmap thumbBmp=Bitmap.createScaledBitmap(bmp,99,99, true);
                             bmp.recycle();
                             imageObject.thumbData= Util.bmpToByteArray(thumbBmp, true);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }
                 }.start();

             }else {
                 Bitmap bmp = BitmapFactory.decodeFile(shareFilePath);
                 Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, SUMB_X, SUMB_Y, true);
                 bmp.recycle();
                 imageObject.thumbData = Util.bmpToByteArray(thumbBmp, true);
             }
		 }else{
			 //如果图片的路径为空:进行使用默认的图片
			 Bitmap bmp =BitmapFactory.decodeResource(//
					 		UiUtils.getInstance().getContext().getResources(),R.mipmap.ic_launcher_logo);
			 Bitmap thumbBmp=Bitmap.createScaledBitmap(bmp, 99, 99, true);
			 bmp.recycle();
			 imageObject.setImageObject(thumbBmp);
		 }
		return imageObject;
	}
	/**
	 * 进行获取文本的信息
	 * @return
	 */
	private TextObject getTextObject(String content){
		 	TextObject textObject = new TextObject();
	        textObject.text = content;
	        return textObject;
	}
	
	/**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     * 
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        
    }

	/**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            //从这里获取用户输入的 电话号码信息 
            String  phoneNum =  mAccessToken.getPhoneNum();
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
//                updateTokenView(false);
                
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(WBShareActivity.this, mAccessToken);
                Toast.makeText(WBShareActivity.this, 
                        "授权成功", Toast.LENGTH_SHORT).show();
                //FIXME
                shareContent();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
//                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
//                if (!TextUtils.isEmpty(code)) {
//                    message = message + "\nObtained the code: " + code;
//                }
                Toast.makeText(WBShareActivity.this, "分享失败："+code, Toast.LENGTH_LONG).show();
                finish();
            }
        }

        @Override
        public void onCancel(){
            Toast.makeText(WBShareActivity.this, 
                   "您取消了分享", Toast.LENGTH_LONG).show();
            finish();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(WBShareActivity.this, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

}

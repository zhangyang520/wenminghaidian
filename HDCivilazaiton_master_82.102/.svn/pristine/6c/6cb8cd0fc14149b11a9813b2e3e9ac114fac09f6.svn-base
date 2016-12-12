package com.zhjy.hdcivilization.utils;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import com.zhjy.hdcivilization.exception.ContentException;
/**
 * 获取版本的工具类
 * @author niyl
 *
 */
public class VersionUtil {
	
	public static final int osNum=android.os.Build.VERSION.SDK_INT;
	
	public static final String PACKAGE_NAME = "com.zhjy.hdcivilization";
	
	public static final int INSTALL_REQUEST_CODE=49;
	/**
	 * 获得版本号
	 * 
	 * @author niyl
	 * @param context
	 * @return int 版本号
	 */
	public static int getVersionCode(Context context) {
		int verCode = -1;
		try {
			verCode = UiUtils.getInstance().getContext().getPackageManager().getPackageInfo(PACKAGE_NAME,
					0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verCode;
	}

	/**
	 * 获得版本名称
	 * 
	 * @author niyl
	 * @param context
	 * @return string 版本名称
	 */
	public static String getVersionName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(PACKAGE_NAME,
					0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verName;
	}
	
	/**
	 * 安装APK文件
	 */
	public static boolean installApk(Activity context,String fileAbPath)
	{
		File apkfile = new File(fileAbPath);
		if (!apkfile.exists())
		{
			return false;
		}
		// 通过Intent安装APK文件
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + fileAbPath), "application/vnd.android.package-archive");
		context.startActivityForResult(intent, VersionUtil.INSTALL_REQUEST_CODE);
		android.os.Process.killProcess(android.os.Process.myPid());

		return true;
	}
	
	/**
	 * 是否比服务器端传递过来的apk的版本号大
	 * @param jsonApkValue
	 * @return
	 * @throws
	 */
	public static boolean isLowerJsonApk(String jsonApkValue) throws ContentException{
		try {
			//进行获取本地的版本号
			String localApkVersion=getVersionName(UiUtils.getInstance().getContext());
			System.out.println("isLowerJsonApk localApkVersion:"+localApkVersion);
			//先进行判断版本的格式
			String regex="^[0-9]+(.[0-9]+)*$";
			if(!jsonApkValue.matches(regex)){
				throw new ContentException("软件版本号获取失败！");
			}else if(!localApkVersion.matches(regex)){
				throw new ContentException("软件版本号获取失败！");
			}
			
			//进行解析正确格式的版本
			String jsons[]=jsonApkValue.split("\\.");
			String locals[]=localApkVersion.split("\\.");
			int indexLength=jsons.length>locals.length?jsons.length:locals.length;
//			for(int ){
//
//			}
			System.out.println("jsons[] length:"+jsons.length+"....locals[] length:"+locals.length);
			for(int i=0;i<jsons.length;++i){
				//如果其中的某段服务器格式版本>本地的
				if(i<=(locals.length-1)){
					//进行比对范围之内的
					if(Integer.valueOf(jsons[i])>Integer.valueOf(locals[i])){
						return true;
					}else if(Integer.valueOf(jsons[i])<Integer.valueOf(locals[i])){
						//如果服务器版本号的长度>本地的，而且之前的比较没有胜负！返回服务器的版本号大
						return false;
					}
				}else{
					System.out.println("Integer.valueOf(jsons["+i+"]):"+jsons[i]);
                    if(Integer.valueOf(jsons[i])>0){
						return true;
					}else if(i==(jsons.length-1)){
						return false;
					}
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//最后返回本地的版本号大 或者相等
		return false;
	}
}

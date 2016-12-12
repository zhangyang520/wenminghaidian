package com.zhjy.hdcivilization.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * url链接的封装类
 * 
 * @author zhangyang
 * 
 */
public class UrlParamsEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String url;//
	private LinkedHashMap<String, String> paramsHashMap;
	private String methodName;// 方法名称

	//webService相关的url
	private static String webServiceUrl = "http://192.168.82.112:8080/default/serviceService?wsdl";
	private static String namingSpace="http://www.primeton.com/serviceService";
	public static String ipPort="http://192.168.82.112:8080/";
	public static String textUrl=ipPort+"default/mobileInterface/show/infoPage.jsp";

	//海淀文明办的相关的url
	//个人的ipd
	public static String SAN_XING_IP_FILE="http://192.168.82.116:8081/";
	public static String WU_CHEN_XU_IP_FILE="http://192.168.82.105:8080/";
	public static String ZHANG_RUI_IP_FILE="http://192.168.88.89:8080/";
	public static String LI_RUI_FANG_IP_url="http://192.168.83.90:9001/";
	public static String LI_RUI_FANG_IP_url1="http://192.168.83.90:9001/";
//	public static String HD_CIVILIZATION_IP="http://218.249.38.206:9999/";
	public static String KAIGE="http://192.168.83.102:8081/";
	public static String ZHJY="http://218.249.38.206:9999/";
	public static String A_LI_YUN="http://101.200.104.230:80/";//阿里云服务器Ip

	public static String HD_CIVILIZATION_IP=A_LI_YUN;
   //当前ip地址
	public static String CURRENT_ID = HD_CIVILIZATION_IP+"cs/supervision/Service/ServiceGate.jsp?";
	public static String CURRENT_ID_1 = HD_CIVILIZATION_IP+"cs/supervision/Service/ServiceGate.jsp";
	//上传图片
	public static String UPLOAD_IMG = HD_CIVILIZATION_IP+"cs/FileUpServlet.action";
	//通知公告webView
	public static String NOTICE_DETAIL=HD_CIVILIZATION_IP+"cs/supervision/Advise/Advise_DialogByPhone.jsp";
	//文明动态
	public static String WEBVIEW_URL=HD_CIVILIZATION_IP+"cs/supervision/Dynamic/Dynamic_DialogByPhone.jsp";
	//上传图片
	public static String UPLOAD_IMG_URL=HD_CIVILIZATION_IP+"cs/FileUpServlet.action?";
	//图片的拼接
	public static String WUCHEN_XU_IP_FILE=HD_CIVILIZATION_IP+"file/";

	//分享的地址:
	//主题分享
	public static String SHARE_THEME_WEB_URL=HD_CIVILIZATION_IP+"cs/supervision/ThemeApplication/Theme_ByPhone.jsp?";
	//通知公告的url：
	public static String SHARE_NOTICE_WEB_URL=HD_CIVILIZATION_IP+"cs/supervision/Advise/AdviseByShare.jsp?";
	//文明动态的url:
	public static String SHARE_STATE_WEB_URL=HD_CIVILIZATION_IP+"cs/supervision/Dynamic/DialogByShare.jsp?";

	//
	/*********************/
//	public String HDCURL="http://192.168.82.101:8080/cs/supervision/Service/ServiceGate.jsp?";
	public String HDCURL=UrlParamsEntity.CURRENT_ID;

	//当前头像的图片的拼接的url
	public static String CURRENT_PHOTO_IP=WUCHEN_XU_IP_FILE;
	public static String HDCURL_UPLOAD_DATA=HD_CIVILIZATION_IP+"cs/supervision/Service/ServiceGate.jsp?";
	//评论列表的图片的拼接的url
	public static String IMGURL = WUCHEN_XU_IP_FILE;//拼接图片的Url

	// 构造函数
	public UrlParamsEntity() {
		super();
	}

	public UrlParamsEntity(String ulr, LinkedHashMap<String, String> paramsHashMap) {
		super();
		this.url = ulr;
		this.paramsHashMap = paramsHashMap;
	}

	public HashMap<String, String> getParamsHashMap() {
		return paramsHashMap;
	}

	public void setParamsHashMap(LinkedHashMap<String, String> paramsHashMap) {
		this.paramsHashMap = paramsHashMap;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public static String getWebServiceUrl() {
		return webServiceUrl;
	}

	public static void setWebServiceUrl(String webServiceUrl) {
		UrlParamsEntity.webServiceUrl = webServiceUrl;
	}

	public static String getNamingSpace() {
		return namingSpace;
	}

	public static void setNamingSpace(String namingSpace) {
		UrlParamsEntity.namingSpace = namingSpace;
	}

}

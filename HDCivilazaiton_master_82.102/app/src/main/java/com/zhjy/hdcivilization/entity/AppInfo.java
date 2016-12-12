package com.zhjy.hdcivilization.entity;

/**
 * Created by zhangyang on 2016/8/28.
 */
public class AppInfo{
    String appVersionCode;
    String appContent;//app的描述内容
    String appUrl;//app对应的下载的url

    public String getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppContent() {
        return appContent;
    }

    public void setAppContent(String appContent) {
        this.appContent = appContent;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "appVersionCode='" + appVersionCode + '\'' +
                ", appContent='" + appContent + '\'' +
                ", appUrl='" + appUrl + '\'' +
                '}';
    }
}

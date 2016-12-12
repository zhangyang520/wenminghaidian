package com.zhjy.hdcivilization.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Foreign;

/**
 * @author :huangxianfeng on 2016/8/12.
 * (建表)系统设置表
 */
public class SystemSetting extends EntityBase {

    private boolean isPush;//是否推送
    private String loadMode="";//图片加载模式
    private String fontSize="";//字体大小：
    private String userId="";

    public SystemSetting() {
    }


    public SystemSetting(boolean isPush, String loadMode, String fontSize, String userId) {
        this.isPush = isPush;
        this.loadMode = loadMode;
        this.fontSize = fontSize;
        this.userId = userId;
    }

    public boolean isPush() {
        return isPush;
    }

    public void setIsPush(boolean isPush) {
        this.isPush = isPush;
    }

    public String getLoadMode() {
        return loadMode;
    }

    public void setLoadMode(String loadMode) {
        this.loadMode = loadMode;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "SystemSetting{" +
                "isPush=" + isPush +
                ", loadMode='" + loadMode + '\'' +
                ", fontSize='" + fontSize + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}

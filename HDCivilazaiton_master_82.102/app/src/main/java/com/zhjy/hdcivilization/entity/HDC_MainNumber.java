package com.zhjy.hdcivilization.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Foreign;

/**
 * @author :huangxianfeng on 2016/7/26.
 * 首页提示数字实体类
 */
public class HDC_MainNumber extends EntityBase {
    private int superviseCount=0;//文明监督的个数--
    private int commentCount=0;//文明评论的个数
    private int stateCount=0;//文明动态的个数
    private int notifyCount=0;//通知公告的个数

    private String userId;

    public HDC_MainNumber() {
    }

    public HDC_MainNumber( int superviseCount, int commentCount, int stateCount, int notifyCount, String user) {
        this.superviseCount = superviseCount;
        this.commentCount = commentCount;
        this.stateCount = stateCount;
        this.notifyCount = notifyCount;
        this.userId = user;
    }

    public int getSuperviseCount() {
        return superviseCount;
    }

    public void setSuperviseCount(int superviseCount) {
        this.superviseCount = superviseCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getStateCount() {
        return stateCount;
    }

    public void setStateCount(int stateCount) {
        this.stateCount = stateCount;
    }

    public int getNotifyCount() {
        return notifyCount;
    }

    public void setNotifyCount(int notifyCount) {
        this.notifyCount = notifyCount;
    }

    public String getUser() {
        return userId;
    }

    public void setUser(String user) {
        this.userId = user;
    }

    @Override
    public String toString() {
        return "HDC_MainNumber{" + '\'' +
                ", superviseCount=" + superviseCount +
                ", commentCount=" + commentCount +
                ", stateCount=" + stateCount +
                ", notifyCount=" + notifyCount +
                ", user=" + userId +
                '}';
    }
}

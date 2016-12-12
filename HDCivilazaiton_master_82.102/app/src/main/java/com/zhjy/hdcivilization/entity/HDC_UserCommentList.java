package com.zhjy.hdcivilization.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Foreign;

import java.util.List;

/**
 * @author :huangxianfeng on 2016/8/17.
 * 评论的列表
 */
public class HDC_UserCommentList extends EntityBase{

    private String title;
    private int tipCount;
    private long publishTime;
    private int count;
    private String content;
    private int topOrder;//>0时为置顶
    @Foreign(column = "userId",foreign = "userId")
    @Column(column = "userId")
    private User user;
    private int topicType;//0.热门话题1.发起话题2.参与话题

    private String itemId;//一级评论,二级评论的itemId
    private String fatherItemId;//如果是二级评论,此为其父节点一级评论的itemId;
    //存储的是该节点父节点的itemIdAndType
    private String topicItemIdAndType;//包含:主题,通知公告,文明动态的itemIdAndType:

    public HDC_UserCommentList() {

    }


    public HDC_UserCommentList(String title, int tipCount, long publishTime, int count, String content, int topOrder, User user, int topicType, String itemId, String fatherItemId, String topicItemIdAndType) {
        this.title = title;
        this.tipCount = tipCount;
        this.publishTime = publishTime;
        this.count = count;
        this.content = content;
        this.topOrder = topOrder;
        this.user = user;
        this.topicType = topicType;
        this.itemId = itemId;
        this.fatherItemId = fatherItemId;
        this.topicItemIdAndType = topicItemIdAndType;
    }


    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTipCount() {
        return tipCount;
    }

    public void setTipCount(int tipCount) {
        this.tipCount = tipCount;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTopOrder() {
        return topOrder;
    }

    public void setTopOrder(int topOrder) {
        this.topOrder = topOrder;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getTopicType() {
        return topicType;
    }

    public void setTopicType(int topicType) {
        this.topicType = topicType;
    }

    public String getFatherItemId() {
        return fatherItemId;
    }

    public void setFatherItemId(String fatherItemId) {
        this.fatherItemId = fatherItemId;
    }

    public String getTopicItemIdAndType() {
        return topicItemIdAndType;
    }

    public void setTopicItemIdAndType(String topicItemIdAndType) {
        this.topicItemIdAndType = topicItemIdAndType;
    }

    @Override
    public String toString() {
        return "HDC_UserCommentList{" +
                "title='" + title + '\'' +
                ", tipCount=" + tipCount +
                ", publishTime=" + publishTime +
                ", count=" + count +
                ", content='" + content + '\'' +
                ", topOrder=" + topOrder +
                ", user=" + user +
                ", topicType=" + topicType +
                ", itemId='" + itemId + '\'' +
                ", fatherItemId='" + fatherItemId + '\'' +
                ", topicItemIdAndType='" + topicItemIdAndType + '\'' +
                '}';
    }
}

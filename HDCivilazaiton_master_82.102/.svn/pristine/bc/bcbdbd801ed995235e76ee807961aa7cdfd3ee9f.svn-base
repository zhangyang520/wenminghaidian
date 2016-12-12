package com.zhjy.hdcivilization.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Foreign;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;

import java.util.List;

/**
 * @author :huangxianfeng on 2016/7/29.
 * （建表）文明评论详情页和文明评论热门话题和我的话题、参与的话题列表、实体类
 */
public class HDC_CommentDetail extends EntityBase{

    private String itemId;
    private int typeCount;//提醒数
    private boolean isTopType;//是否置顶
    private int topValue=0;
    private String title;//标题
    private String publishTime;//发布时间
    private int count;//点赞个数
    private String content;//文字描述
    @Foreign(column = "itemIdAndType",foreign = "itemIdAndItemType")
    private List<ImgEntity> imgUrlList;//图片的Url集合
    @Foreign(column = "launcherUserId",foreign = "userId")
    @Column(column = "launcherUserId")
    private User launchUser;//发起话题用户，User对象
    @Foreign(column = "participateUserId",foreign = "userId")
    @Column(column = "participateUserId")
    private User participate;//用户参与者
    private int topicType=-1;//0.热门话题1.发起话题2.参与话题
    private String itemIdAndType;//条目的Id和条目类型的组合
    private String itemType ="comment";//条目类型
    private long orderTime;
    private int  commentCount;//评论数
    public HDC_CommentDetail() {
    }

    public HDC_CommentDetail(String itemId, int typeCount, boolean isTopType, String title, String publishTime, int count, String content, List<ImgEntity> imgUrlList, User launchUser, User participate, int topicType, String itemIdAndType, String itemType,int commentCount) {
        this.itemId = itemId;
        this.typeCount = typeCount;
        this.isTopType = isTopType;
        this.title = title;
        this.publishTime = publishTime;
        this.count = count;
        this.content = content;
        this.imgUrlList = imgUrlList;
        this.launchUser = launchUser;
        this.participate = participate;
        this.topicType = topicType;
        this.itemIdAndType = itemIdAndType;
        this.itemType = itemType;
        this.commentCount=commentCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getTopValue() {
        return topValue;
    }

    public void setTopValue(int topValue) {
        this.topValue = topValue;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
        this.itemIdAndType=itemId+this.itemType;
    }

    public int getTypeCount() {
        return typeCount;
    }

    public void setTypeCount(int typeCount) {
        this.typeCount = typeCount;
    }

    public boolean isTopType() {
        return isTopType;
    }

    public void setIsTopType(boolean isTopType) {
        this.isTopType = isTopType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
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

    public List<ImgEntity> getImgUrlList() {
        return imgUrlList;
    }

    public void setImgUrlList(List<ImgEntity> imgUrlList) {
        this.imgUrlList = imgUrlList;
    }

    public User getLaunchUser() {
        return launchUser;
    }

    public void setLaunchUser(User launchUser) {
        this.launchUser = launchUser;
    }

    public User getParticipate() {
        return participate;
    }

    public void setParticipate(User participate) {
        this.participate = participate;
    }

    public int getTopicType() {
        return topicType;
    }

    public void setTopicType(int topicType) {
        this.topicType = topicType;
    }

    public String getItemIdAndType() {
        return itemIdAndType;
    }

    public void setItemIdAndType(String itemIdAndType) {
        this.itemIdAndType = itemIdAndType;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    @Override
    public String toString() {
        return "HDC_CommentDetail{" +
                "itemId='" + itemId + '\'' +
                ", typeCount=" + typeCount +
                ", isTopType=" + isTopType +
                ", topValue=" + topValue +
                ", title='" + title + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", count=" + count +
                ", content='" + content + '\'' +
                ", imgUrlList=" + imgUrlList +
                ", launchUser=" + launchUser +
                ", participate=" + participate +
                ", topicType=" + topicType +
                ", itemIdAndType='" + itemIdAndType + '\'' +
                ", itemType='" + itemType + '\'' +
                ", orderTime=" + orderTime +
                ", commentCount=" + commentCount +
                '}';
    }
}

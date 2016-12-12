package com.zhjy.hdcivilization.entity;

/**
 * 点赞状态的记录
 * Created by zhangyang on 2016/8/29.
 */
public class ClickLikesState extends EntityBase{
    boolean isClickState;//true 已经点过赞 false:没有点赞
    String UserId;//用户的id
    String itemIdAndType;//不同种类条目的唯一标识

    public ClickLikesState() {
    }

    public ClickLikesState(boolean isClickState, String userId, String itemIdAndType) {
        this.isClickState = isClickState;
        UserId = userId;
        this.itemIdAndType = itemIdAndType;
    }

    public boolean isClickState() {
        return isClickState;
    }

    public void setIsClickState(boolean isClickState) {
        this.isClickState = isClickState;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getItemIdAndType() {
        return itemIdAndType;
    }

    public void setItemIdAndType(String itemIdAndType) {
        this.itemIdAndType = itemIdAndType;
    }

    @Override
    public String toString() {
        return "ClickLikesState{" +
                "isClickState=" + isClickState +
                ", UserId='" + UserId + '\'' +
                ", itemIdAndType='" + itemIdAndType + '\'' +
                '}';
    }
}

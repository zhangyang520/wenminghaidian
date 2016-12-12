package com.zhjy.hdcivilization.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Foreign;

/**
 * Created by zhangyang on 2016/8/1.
 * (文明动态)文明动态列表
 */
public class HDC_CiviState extends EntityBase{

    private String itemId;//条目的id
    private String title;//主题
    private int tipCount;//消息提醒数量
    private String publishTime;//发布时间
    private int dianZanCount;//点赞个数
    private String des;//描述内容
    @Foreign(column = "imgId",foreign = "id")
    @Column(column = "imgId")
    private ImgEntity imgEntity;//图片的实体对象
    private String itemType ="civistate";//条目类型
    private String itemIdAndType;//。。。。
    private String userId;//用户的id
    private long publishTimeLong;//发布时间

    public long getPublishTimeLong() {
        return publishTimeLong;
    }

    public void setPublishTimeLong(long publishTimeLong) {
        this.publishTimeLong = publishTimeLong;
    }

    public HDC_CiviState() {
    }

    public HDC_CiviState(String itemId, String title, int tipCount, String publishTime, int dianZanCount, String des, ImgEntity imgEntity, String itemType) {
        this.itemId = itemId;
        this.title = title;
        this.tipCount = tipCount;
        this.publishTime = publishTime;
        this.dianZanCount = dianZanCount;
        this.des = des;
        this.imgEntity = imgEntity;
        this.itemType = itemType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public int getDianZanCount() {
        return dianZanCount;
    }

    public void setDianZanCount(int dianZanCount) {
        this.dianZanCount = dianZanCount;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public ImgEntity getImgEntity() {
        return imgEntity;
    }

    public void setImgEntity(ImgEntity imgEntity) {
        this.imgEntity = imgEntity;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemIdAndType() {
        return itemIdAndType;
    }

    public void setItemIdAndType(String itemIdAndType) {
        this.itemIdAndType = itemIdAndType;
    }

    @Override
    public String toString() {
        return "HDC_CiviState{" +
                "itemId='" + itemId + '\'' +
                ", title='" + title + '\'' +
                ", tipCount=" + tipCount +
                ", publishTime='" + publishTime + '\'' +
                ", dianZanCount=" + dianZanCount +
                ", des='" + des + '\'' +
                ", imgEntity=" + imgEntity +
                ", itemType='" + itemType + '\'' +
                ", itemIdAndType='" + itemIdAndType + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}

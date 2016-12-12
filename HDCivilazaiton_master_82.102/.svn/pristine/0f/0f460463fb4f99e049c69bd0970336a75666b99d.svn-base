package com.zhjy.hdcivilization.entity;

import com.lidroid.xutils.db.annotation.Foreign;

/**
 * @author :huangxianfeng on 2016/7/26.
 * （建表）首页通知公告的实体类
 */
public class HDC_MainNotice extends EntityBase{

    private String itemId;
    private String title;
    private int TipCount;
    private String publishTime;
    private int dianZanCount;//点赞个数
    private String des;//描述信息
    @Foreign(column = "itemIdAndType",foreign = "itemIdAndItemType")
    private ImgEntity imgEntity;//图片的实体对象
    private String itemIdAndType;//条目id和类型
    private String itemType ="notice";//条目类型

    public HDC_MainNotice() {

    }


    public HDC_MainNotice(String itemId, String title, int tipCount, String publishTime, int dianZanCount, String des, ImgEntity imgEntity, String itemIdAndType, String itemType) {
        this.itemId = itemId;
        this.title = title;
        TipCount = tipCount;
        this.publishTime = publishTime;
        this.dianZanCount = dianZanCount;
        this.des = des;
        this.imgEntity = imgEntity;
        this.itemIdAndType = itemIdAndType;
        this.itemType = itemType;
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
        return TipCount;
    }

    public void setTipCount(int tipCount) {
        TipCount = tipCount;
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
        return "HDC_MainNotice{" +
                "itemId='" + itemId + '\'' +
                ", title='" + title + '\'' +
                ", TipCount=" + TipCount +
                ", publishTime='" + publishTime + '\'' +
                ", dianZanCount=" + dianZanCount +
                ", des='" + des + '\'' +
                ", imgEntity=" + imgEntity +
                ", itemIdAndType='" + itemIdAndType + '\'' +
                ", itemType='" + itemType + '\'' +
                '}';
    }
}

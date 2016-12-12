package com.zhjy.hdcivilization.entity;

import com.lidroid.xutils.db.annotation.Foreign;
import com.lidroid.xutils.db.annotation.Transient;

import java.util.List;

/**
 * Created by zhangyang on 2016/7/29.
 * 我的上报列表的条目实体
 */
public class HDC_SuperviseMySubList extends EntityBase{

    private String itemId="";
    private String publishTime="";//发布时间
    private String address="";//地址
    @Foreign(column = "itemIdAndType",foreign = "itemIdAndItemType")
    private List<ImgEntity> imgEntity;//图片的实体对象
    private String description="";//描述信息
    private String processState;//当前状态
    private String itemType="supervise";//与ImgEntity中的item类型相匹配
    private String streetBelong="";//所属街道
    private String firstEventType="";//一级事件类型
    private String secondEventType="";//二级事件类型
    /*
     0:未受理
     1:已通过
     2:不通过
     3:待复核
     4:复核通过
     5:复核不通过
     */
    private String itemIdAndType="";

    private String unPassReason="";//不通过原因
    private String userId;//用户的id
    public HDC_SuperviseMySubList() {
    }


    public HDC_SuperviseMySubList(String itemId, String publishTime, String address, List<ImgEntity> imgEntity, String description, String processState, String itemType, String itemIdAndType) {
        this.itemId = itemId;
        this.publishTime = publishTime;
        this.address = address;
        this.imgEntity = imgEntity;
        this.description = description;
        this.processState = processState;
        this.itemType = itemType;
        this.itemIdAndType = itemIdAndType;
    }

    public String getUnPassReason() {
        return unPassReason;
    }

    public void setUnPassReason(String unPassReason) {
        this.unPassReason = unPassReason;
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

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<ImgEntity> getImgEntity() {
        return imgEntity;
    }

    public void setImgEntity( List<ImgEntity> imgEntity) {
        this.imgEntity = imgEntity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProcessState() {
        return processState;
    }

    public void setProcessState(String processState) {
        this.processState = processState;
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

    public String getStreetBelong() {
        return streetBelong;
    }

    public void setStreetBelong(String streetBelong) {
        this.streetBelong = streetBelong;
    }

    public String getFirstEventType() {
        return firstEventType;
    }

    public void setFirstEventType(String firstEventType) {
        this.firstEventType = firstEventType;
    }

    public String getSecondEventType() {
        return secondEventType;
    }

    public void setSecondEventType(String secondEventType) {
        this.secondEventType = secondEventType;
    }

    @Override
    public String toString() {
        return "HDC_SuperviseMySubList{" +
                "itemId='" + itemId + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", address='" + address + '\'' +
                ", imgEntity=" + imgEntity +
                ", description='" + description + '\'' +
                ", processState='" + processState + '\'' +
                ", itemType='" + itemType + '\'' +
                ", itemIdAndType='" + itemIdAndType + '\'' +
                '}';
    }


}

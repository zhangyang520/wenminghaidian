package com.zhjy.hdcivilization.entity;

/**
 * @author :huangxianfeng on 2016/8/12.
 * 图片的Url实体类
 */
public class ImgEntity extends EntityBase{

    private String itemId;//图片的Id
    private String imgUrl;//原图url
    private String imgThumbUrl;//缩略图url
    private int itemType;//0.评论1.动态2.文明监督3.通知公告（记录这个图片的所属的条目类型）
    private String itemIdAndItemType;//由类型和条目的id进行组合的字符串！//作为外键

    public ImgEntity() {
    }

    public ImgEntity(String itemId, String imgUrl, String imgThumbUrl) {
        this.itemId = itemId;
        this.imgUrl = imgUrl;
        this.imgThumbUrl = imgThumbUrl;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgThumbUrl() {
        return imgThumbUrl;
    }

    public void setImgThumbUrl(String imgThumbUrl) {
        this.imgThumbUrl = imgThumbUrl;
    }

    public String getItemIdAndItemType() {
        return itemIdAndItemType;
    }

    public void setItemIdAndItemType(String itemIdAndItemType) {
        this.itemIdAndItemType = itemIdAndItemType;
    }

    @Override
    public String toString() {
        return "ImgEntity{" +
                "itemId='" + itemId + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", imgThumbUrl='" + imgThumbUrl + '\'' +
                ", itemType=" + itemType +
                ", itemIdAndItemType='"+itemIdAndItemType +"..superTostring"+super.toString()+
                '}';
    }
}

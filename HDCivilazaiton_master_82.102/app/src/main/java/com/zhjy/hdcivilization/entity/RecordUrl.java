package com.zhjy.hdcivilization.entity;

/**
 *
 * Created by zhangyang on 2016/8/29.
 */
public class RecordUrl extends EntityBase{
    String currentTime;//当前时间的属性
    String recordUrl;//记录的url

    public RecordUrl() {
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getRecordUrl() {
        return recordUrl;
    }

    public void setRecordUrl(String recordUrl) {
        this.recordUrl = recordUrl;
    }

    @Override
    public String toString() {
        return "RecordUrl{" +
                "currentTime='" + currentTime + '\'' +
                ", recordUrl='" + recordUrl + '\'' +
                '}';
    }
}

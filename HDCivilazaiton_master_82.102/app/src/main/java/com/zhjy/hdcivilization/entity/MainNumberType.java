package com.zhjy.hdcivilization.entity;

/**
 * @author :huangxianfeng on 2016/7/26.
 */
public enum MainNumberType {

    SUPERVISE("1"),COMMENT("2"),AUTOSTATE("3"),MINE("4");

    private String type;

    MainNumberType(String type){
        this.type=type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}

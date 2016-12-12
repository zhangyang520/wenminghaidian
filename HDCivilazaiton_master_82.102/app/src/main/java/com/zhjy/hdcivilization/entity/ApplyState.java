package com.zhjy.hdcivilization.entity;

/**
 * @author :huangxianfeng on 2016/7/26.
 * 志愿者申请四个状态值
 * 4: 申请成功
 * 3:申请失败
 * 2:申请中
 * 1:申请提交
 */
public enum ApplyState {

    APPLYSUCCESS("4"),APPLYFAIL("3"),APPLYING("2"),APPLYSUB("1");

    private String state;

    ApplyState(String type){
        this.state=type;
    }

    public String getType() {
        return state;
    }

    public void setType(String type) {
        this.state = type;
    }


}

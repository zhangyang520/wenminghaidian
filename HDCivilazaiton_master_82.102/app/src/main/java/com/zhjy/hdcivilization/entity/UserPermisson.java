package com.zhjy.hdcivilization.entity;

/**
 * @author :huangxianfeng on 2016/7/26.
 *         志愿者申请四个状态值
 *         0:缺省状态
 *         1:普通用户停用
 *         2:普通用户
 *         3:普通用户申请志愿者中
 *         4:志愿者用户
 */
public enum UserPermisson {

    DEFAULTSTATE("0"), ORDINARYSTOPSTATE("1"), ORDINARYSTATE("2"), ORDINARYAPPLYING("3"),VOLUNTEER("4"),UNKNOW_VALUE("-1");

    private String state;

    UserPermisson(String type) {
        this.state = type;
    }

    public String getType() {
        return state;
    }

    public void setType(String type) {
        this.state = type;
    }


}

package com.zhjy.hdcivilization.entity;

/**
 * @author :huangxianfeng on 2016/8/12.
 * (建表)user用户表
 */
public class User extends EntityBase {

    private String userId="";//userId（普通用户Id）
    private String identityState="0";//身份状态0缺省状态1.普通用户停用2.普通用户3.普通用户申请中4.志愿者
    private String volunteerId="";//志愿者Id
    private String nickName="";//昵称
    private String portraitUrl="";//头像的Url
    private String AccountNumber="";//账号
    private String goldCoin="";//金币值
    private int applyState;//0.是未兑换1.是已兑换
    private boolean isLocalUser;//是否是登录用户
    private String exchangeState="";//金币提取的交易状态

    private int joinThemeCount;//参与话题的个数
    private int subThemeCount;//发表话题的个数

    //versionCode 6版本新添加的属性
    String sendCode;//验证码属性
    long lastLoginTime=0;//最后登录时间

    public User() {
    }


    public User(String userId, String identityState, String volunteerId, String nickName, String portraitUrl, String accountNumber, String goldCoin, int applyState, boolean isLocalUser, String exchangeState, int joinThemeCount, int subThemeCount) {
        this.userId = userId;
        this.identityState = identityState;
        this.volunteerId = volunteerId;
        this.nickName = nickName;
        this.portraitUrl = portraitUrl;
        AccountNumber = accountNumber;
        this.goldCoin = goldCoin;
        this.applyState = applyState;
        this.isLocalUser = isLocalUser;
        this.exchangeState = exchangeState;
        this.joinThemeCount = joinThemeCount;
        this.subThemeCount = subThemeCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIdentityState() {
        return identityState;
    }

    public void setIdentityState(String identityState) {
        this.identityState = identityState;
    }

    public String getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(String volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public void setPortraitUrl(String portraitUrl) {
        this.portraitUrl = portraitUrl;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        AccountNumber = accountNumber;
    }

    public String getGoldCoin() {
        return goldCoin;
    }

    public void setGoldCoin(String goldCoin) {
        this.goldCoin = goldCoin;
    }

    public int getApplyState() {
        return applyState;
    }

    public void setApplyState(int applyState) {
        this.applyState = applyState;
    }

    public boolean isLocalUser() {
        return isLocalUser;
    }

    public void setIsLocalUser(boolean isLocalUser) {
        this.isLocalUser = isLocalUser;
    }

    public String getExchangeState() {
        return exchangeState;
    }

    public void setExchangeState(String exchangeState) {
        this.exchangeState = exchangeState;
    }

    public int getJoinThemeCount() {
        return joinThemeCount;
    }

    public void setJoinThemeCount(int joinThemeCount) {
        this.joinThemeCount = joinThemeCount;
    }

    public int getSubThemeCount() {
        return subThemeCount;
    }

    public void setSubThemeCount(int subThemeCount) {
        this.subThemeCount = subThemeCount;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", identityState='" + identityState + '\'' +
                ", volunteerId='" + volunteerId + '\'' +
                ", nickName='" + nickName + '\'' +
                ", portraitUrl='" + portraitUrl + '\'' +
                ", AccountNumber='" + AccountNumber + '\'' +
                ", goldCoin='" + goldCoin + '\'' +
                ", applyState=" + applyState +
                ", isLocalUser=" + isLocalUser +
                ", exchangeState='" + exchangeState + '\'' +
                ", joinThemeCount=" + joinThemeCount +
                ", subThemeCount=" + subThemeCount +
                '}';
    }
}

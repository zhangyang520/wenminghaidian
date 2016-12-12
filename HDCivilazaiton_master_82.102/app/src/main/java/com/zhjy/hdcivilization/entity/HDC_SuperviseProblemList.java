package com.zhjy.hdcivilization.entity;

/**
 * 问题统计列表
 * Created by zhangyang on 2016/7/30.
 *
 */
public class HDC_SuperviseProblemList extends EntityBase{

    int problemCount;//当天的上报问题数
    int problemCoin;//当天获取的积分
    String date;//日期
    int totalCoin;//总积分
    int verifiedCountPerDay;//当天的审核通过个数
    private String userId;//用户的id
    public HDC_SuperviseProblemList() {
    }

    public int getProblemCount() {
        return problemCount;
    }

    public void setProblemCount(int problemCount) {
        this.problemCount = problemCount;
    }

    public int getProblemCoin() {
        return problemCoin;
    }

    public void setProblemCoin(int problemCoin) {
        this.problemCoin = problemCoin;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTotalCoin() {
        return totalCoin;
    }

    public void setTotalCoin(int totalCoin) {
        this.totalCoin = totalCoin;
    }

    public int getVerifiedCountPerDay() {
        return verifiedCountPerDay;
    }

    public void setVerifiedCountPerDay(int verifiedCountPerDay) {
        this.verifiedCountPerDay = verifiedCountPerDay;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "HDC_SuperviseProblemList{" +
                "problemCount=" + problemCount +
                ", problemCoin=" + problemCoin +
                ", date='" + date + '\'' +
                ", totalCoin=" + totalCoin +
                ", verifiedCountPerDay=" + verifiedCountPerDay +
                '}';
    }
}

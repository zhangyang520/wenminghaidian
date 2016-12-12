package com.zhjy.hdcivilization.dao;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.entity.HDC_MainNumber;
import com.zhjy.hdcivilization.exception.ContentException;

import java.util.List;

/**
 * Created by zhangyang on 2016/8/13.
 * 通知公告
 */
public class MainNumberDao {

    private static MainNumberDao instance;
    private MainNumberDao() {}

    public static MainNumberDao getInstance(){
        if (instance == null){
            synchronized (MainNumberDao.class){
                if (instance == null){
                    instance = new MainNumberDao();
                }
            }
        }
        return instance;
    }

    /**
     * 保存不同类型的提醒数
     * @param
     */
    public void  saveNumber(HDC_MainNumber obj){
        try {
            MyApplication.dbUtils.saveOrUpdate(obj);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存文明动态数据失败！");
        }
    }

    /**
     * 通过用户的id进行获取HDC_MainNumber实体对象
     * @param userID
     * @return
     */
    public HDC_MainNumber getNumberBy(String userID) throws ContentException {
        try {
            List<HDC_MainNumber> datas=MyApplication.dbUtils.findAll(//
                                                     Selector.from(HDC_MainNumber.class).//
                                                                                where("userId","=",userID));
            if(datas!=null && datas.size()>0){
                return datas.get(0);
            }else{
                throw new ContentException("no HDC_MainNumber");
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库查找错误!");
        }
    }
    /**
     * 获取所有类型的提醒数
     * @return
     */
    public List<HDC_MainNumber> getNumberList(){
        try {
            return MyApplication.dbUtils.findAll(HDC_MainNumber.class);
        } catch (DbException e) {
            e.printStackTrace();
            throw  new RuntimeException("查询文明动态数据失败！");
        }
    }

    /**
     * 清除所有数据
     */
    public void clearNumberAll(){
        try {
            MyApplication.dbUtils.deleteAll(HDC_MainNumber.class);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("清除数据失败！");
        }

    }
}

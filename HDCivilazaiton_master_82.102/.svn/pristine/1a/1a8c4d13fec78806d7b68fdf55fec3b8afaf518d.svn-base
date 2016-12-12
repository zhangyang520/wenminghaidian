package com.zhjy.hdcivilization.dao;

import com.lidroid.xutils.exception.DbException;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.entity.HDC_MainNotice;

import java.util.List;

/**
 * Created by zhangyang on 2016/8/13.
 * 通知公告
 */
public class MainNoticeDao {

    private static MainNoticeDao instance;
    private MainNoticeDao() {}

    public static MainNoticeDao getInstance(){
        if (instance == null){
            synchronized (MainNoticeDao.class){
                if (instance == null){
                    instance = new MainNoticeDao();
                }
            }
        }
        return instance;
    }

    /**
     * 进行保存通知公告集合
     * @param datas
     */
    public void  saveMainNoticeList(List<HDC_MainNotice> datas){
        try {
            MyApplication.dbUtils.saveAll(datas);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存通知公告数据失败！");
        }
    }

    /**
     * 进行获取所有的本地的通知公告集合
     * @return
     */
    public List<HDC_MainNotice> getNoticeList(){
        try {
            return MyApplication.dbUtils.findAll(HDC_MainNotice.class);
        } catch (DbException e) {
            e.printStackTrace();
            throw  new RuntimeException("查询通知公告数据失败！");
        }
    }

    /**
     * 进行清除所有的数据
     */
    public void clearAll(){
        try {
            //进行清除所有的数据
            MyApplication.dbUtils.deleteAll(HDC_MainNotice.class);
        } catch (DbException e) {
            e.printStackTrace();
            throw  new RuntimeException("清除通知公告数据失败!");
        }
    }
}

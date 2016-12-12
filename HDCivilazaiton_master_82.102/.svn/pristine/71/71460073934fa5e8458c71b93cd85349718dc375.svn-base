package com.zhjy.hdcivilization.dao;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.entity.HDC_ApplyCheck;
import com.zhjy.hdcivilization.entity.User;

import java.util.List;

/**
 * @author :huangxianfeng on 2016/8/14.
 * 申请查询Dao
 */
public class ApplyCheckDao {

    private static ApplyCheckDao instance;
    private ApplyCheckDao() {}

    public static ApplyCheckDao getInstance(){
        if (instance == null){
            synchronized (CivistateDao.class){
                if (instance == null){
                    instance = new ApplyCheckDao();
                }
            }
        }
        return instance;
    }

    /**
     * 进行保存图片集合
     */
    public void saveAll(List<HDC_ApplyCheck> datas){
        try {
            MyApplication.dbUtils.saveAll(datas);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存数据失败");
        }
    }

    /**
     * 保存对象
     * @param data
     */
    public void saveObj(HDC_ApplyCheck data){
        try {
            MyApplication.dbUtils.saveOrUpdate(data);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存数据失败");
        }
    }
    /**
     * @return
     */
    public List<HDC_ApplyCheck> findAll(){
        try {
            return MyApplication.dbUtils.findAll(HDC_ApplyCheck.class);
        } catch (DbException e) {
            e.printStackTrace();
            throw  new RuntimeException("查找数据失败!");
        }
    }

    public void clearData(){
        try {
            MyApplication.dbUtils.deleteAll(HDC_ApplyCheck.class);
        } catch (DbException e) {
            e.printStackTrace();
            throw  new RuntimeException("清除数据失败!");
        }
    }


    /**
     * 通过用户的id进行查找对应的状态值
     * @param userId
     * @return
     */
    public HDC_ApplyCheck getApprovalState(String userId){
        try {
            List<HDC_ApplyCheck> datas =  MyApplication.dbUtils.findAll(Selector.from(HDC_ApplyCheck.class).where("userId","=",userId));
            if (datas.size()>0){
                return datas.get(0);
            }else{
                return null;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 通过userid进行删除该记录
     * @param userId
     */
    public  void clearApprovalState(String userId){
        try {
            MyApplication.dbUtils.delete(
                    HDC_ApplyCheck.class, WhereBuilder.b("userId","=",userId));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}

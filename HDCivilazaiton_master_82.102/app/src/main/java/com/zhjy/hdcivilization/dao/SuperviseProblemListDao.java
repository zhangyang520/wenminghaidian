package com.zhjy.hdcivilization.dao;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.entity.HDC_SuperviseMySubList;
import com.zhjy.hdcivilization.entity.HDC_SuperviseProblemList;

import java.util.List;

/**
 * @author :huangxianfeng on 2016/8/14.
 */
public class SuperviseProblemListDao {

    private static SuperviseProblemListDao instance;

    private SuperviseProblemListDao(){}

    public static SuperviseProblemListDao getInstance(){
        if (instance==null){
            synchronized (UserDao.class){
                if (instance==null){
                    instance=new SuperviseProblemListDao();
                }
            }
        }
        return instance;
    }

    /***
     * 保存数据
     * @param datas
     */
    public void saveAll(List<HDC_SuperviseProblemList> datas){
        try {
            MyApplication.dbUtils.saveAll(datas);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存数据失败!");
        }
    }


    /**
     * 查询所有数据
     * @return
     */
    public List<HDC_SuperviseProblemList> getAll(){
        try{
            return MyApplication.dbUtils.findAll(HDC_SuperviseProblemList.class);
        }catch(DbException e){
             e.printStackTrace();
            throw new RuntimeException("查找数据失败!");
        }
    }

    /**
     * 通过用户的id进行查询集合
     * @param userId
     * @return
     */
    public List<HDC_SuperviseProblemList> getListBy(String userId) {
        try{
            return MyApplication.dbUtils.findAll(Selector.from(HDC_SuperviseProblemList.class).where("userId","=",userId));
        }catch(DbException e){
            e.printStackTrace();
            throw new RuntimeException("查找数据失败!");
        }
    }

}

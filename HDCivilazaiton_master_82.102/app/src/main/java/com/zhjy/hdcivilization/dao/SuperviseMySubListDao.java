package com.zhjy.hdcivilization.dao;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.entity.HDC_SuperviseMySubList;
import com.zhjy.hdcivilization.entity.HDC_SuperviseProblemList;

import java.net.ConnectException;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/8/14.
 * 我的上报列表的条目实体
 */
public class SuperviseMySubListDao {

    private static SuperviseMySubListDao instance;

    private SuperviseMySubListDao(){}

    public static SuperviseMySubListDao getInstance(){
        if (instance==null){
            synchronized (UserDao.class){
                if (instance==null){
                    instance=new SuperviseMySubListDao();
                }
            }
        }
        return instance;
    }


    /***
     * 保存数据
     * @param datas
     */
    public void saveAll(List<HDC_SuperviseMySubList> datas){
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
    public List<HDC_SuperviseMySubList> getAll(){
        try{
            return MyApplication.dbUtils.findAll(HDC_SuperviseMySubList.class);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("查找数据失败!");
        }
    }

    /**
     * 进行清除所有的数据
     */
    public void clearAll(){
        try {
            MyApplication.dbUtils.deleteAll(HDC_SuperviseMySubList.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过itemId查找数据
     * @param itemId
     * @return
     * @throws ConnectException
     */
    public HDC_SuperviseMySubList getItemBy(String itemId) throws ConnectException {
        try {
            List<HDC_SuperviseMySubList> datas=MyApplication.dbUtils.findAll(//
                                                        Selector.from(HDC_SuperviseMySubList.class).//
                                                                                        where("itemId","=",itemId));
            if(datas!=null && datas.size()>0){
                return  datas.get(0);
            }else{
                throw  new ConnectException("查找不到数据!");
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw  new RuntimeException("查找数据失败!");
        }
    }


    public List<HDC_SuperviseMySubList> getListBy(String userId) {
        try{
            return MyApplication.dbUtils.findAll(Selector.from(HDC_SuperviseMySubList.class).where("userId","=",userId));
        }catch(DbException e){
            e.printStackTrace();
            throw new RuntimeException("查找数据失败!");
        }
    }
}

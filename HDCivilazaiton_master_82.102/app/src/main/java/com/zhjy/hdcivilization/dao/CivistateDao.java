package com.zhjy.hdcivilization.dao;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.entity.HDC_CiviState;

import java.util.List;

/**
 * Created by zhangyang on 2016/8/13.
 * 文明动态的dao
 */
public class CivistateDao {
    private static CivistateDao instance;
    private CivistateDao() {}

    public static CivistateDao getInstance(){
        if (instance == null){
            synchronized (CivistateDao.class){
                if (instance == null){
                    instance = new CivistateDao();
                }
            }
        }
        return instance;
    }

    /**
     * 进行保存动态集合
     * @param datas
     */
    public void  saveAllCivistateList(List<HDC_CiviState> datas){
        try {
            MyApplication.dbUtils.saveOrUpdateAll(datas);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存文明动态数据失败！");
        }
    }

    /**
     * 保存某个对象
     * @param
     */
    public void  saveAllCivistateoObj(HDC_CiviState data){
        try {
            MyApplication.dbUtils.saveOrUpdate(data);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存文明动态数据失败！");
        }
    }

    /**
     * 进行获取所有的本地的文明动态集合数据
     * @return
     */
    public List<HDC_CiviState> getAllCivistateList(){
        try {
            return MyApplication.dbUtils.findAll(HDC_CiviState.class);
        } catch (DbException e) {
            e.printStackTrace();
            throw  new RuntimeException("查询文明动态数据失败！");
        }
    }

    /**
     * 进行获取所有的本地的文明动态集合数据
     * @return
     */
    public List<HDC_CiviState> getAllCivistateListBy(String userId){
        try {
            return MyApplication.dbUtils.findAll(Selector.from(HDC_CiviState.class).where("userId","=",userId));
        } catch (DbException e) {
            e.printStackTrace();
            throw  new RuntimeException("查询文明动态数据失败！");
        }
    }

    /**
     * 对数据的删除
     */
    public void clearAll(){
        try {
            MyApplication.dbUtils.deleteAll(HDC_CiviState.class);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("清空数据失败！");
        }
    }

    /**
     * 对数据的删除
     */
    public void clearAllBy(String userId){
        try {
            MyApplication.dbUtils.delete(HDC_CiviState.class, WhereBuilder.b("userId", "=", userId).or("userId", "=", ""));
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("清空数据失败！");
        }
    }
    /**
     * 进行获取文明动态的实体对象
     * @param itemId
     */
    public List<HDC_CiviState> getCiviState(String itemId){
        try {
            List<HDC_CiviState> datas=MyApplication.dbUtils.findAll(//
                                            Selector.from(HDC_CiviState.class).where(//
                                                                    WhereBuilder.b("itemId","=",itemId)));
            if(datas!=null && datas.size()>0){
                return datas;
            }else{
                return null;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("清空数据失败！");
        }
    }

    public HDC_CiviState getCiviStateObj(String itemId){
        try {
            List<HDC_CiviState> datas=MyApplication.dbUtils.findAll(//
                    Selector.from(HDC_CiviState.class).where(//
                            WhereBuilder.b("itemId","=",itemId)));
            if(datas!=null && datas.size()>0){
                return datas.get(0);
            }else{
                return null;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("清空数据失败！");
        }
    }
    public HDC_CiviState getCiviState(String itemId,String userId){
        try {
            List<HDC_CiviState> datas=MyApplication.dbUtils.findAll(//
                    Selector.from(HDC_CiviState.class).where(//
                            WhereBuilder.b("itemId","=",itemId)));
            if(datas!=null && datas.size()>0){
                return datas.get(0);
            }else{
                return null;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("清空数据失败！");
        }
    }
}

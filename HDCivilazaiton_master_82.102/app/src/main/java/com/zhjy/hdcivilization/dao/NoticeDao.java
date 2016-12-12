package com.zhjy.hdcivilization.dao;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.entity.HDC_CiviState;
import com.zhjy.hdcivilization.entity.HDC_Notice;

import java.util.List;

/**
 * Created by zhangyang on 2016/8/13.
 * 文明动态的dao
 */
public class NoticeDao {
    private static NoticeDao instance;
    private NoticeDao() {}

    public static NoticeDao getInstance(){
        if (instance == null){
            synchronized (NoticeDao.class){
                if (instance == null){
                    instance = new NoticeDao();
                }
            }
        }
        return instance;
    }

    /**
     * 进行保存动态集合
     * @param datas
     */
    public void saveAllNoticeList(List<HDC_Notice> datas){
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
    public void  saveNoticeObj(HDC_Notice data){
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
    public List<HDC_Notice> getAllNoticeList(){
        try {
            return MyApplication.dbUtils.findAll(HDC_Notice.class);
        } catch (DbException e) {
            e.printStackTrace();
            throw  new RuntimeException("查询文明动态数据失败！");
        }
    }

    /**
     * 进行获取所有的本地的文明动态集合数据
     * @return
     */
    public List<HDC_Notice> getAllNoticeListBy(String userId){
        try {
            return MyApplication.dbUtils.findAll(Selector.from(HDC_Notice.class).where("userId","=",userId));
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
            MyApplication.dbUtils.deleteAll(HDC_Notice.class);
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
            MyApplication.dbUtils.delete(HDC_Notice.class, WhereBuilder.b("userId", "=", userId));
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("清空数据失败！");
        }
    }
    /**
     * 进行获取文明动态的实体对象
     * @param itemId
     */
    public HDC_Notice getNotice(String itemId){
        try {
            List<HDC_Notice> datas=MyApplication.dbUtils.findAll(//
                    Selector.from(HDC_Notice.class).where(//
                            WhereBuilder.b("itemId", "=", itemId)));
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

    public List<HDC_Notice> getNoticeList(String itemId){
        try {
            List<HDC_Notice> datas=MyApplication.dbUtils.findAll(//
                    Selector.from(HDC_Notice.class).where(//
                            WhereBuilder.b("itemId", "=", itemId)));
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



    /**
     * 进行获取文明动态的实体对象
     * @param itemId
     */
    public HDC_Notice getNotice(String itemId,String userId){
        try {
            List<HDC_Notice> datas=MyApplication.dbUtils.findAll(//
                    Selector.from(HDC_Notice.class).where(//
                            WhereBuilder.b("itemId", "=", itemId)));
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

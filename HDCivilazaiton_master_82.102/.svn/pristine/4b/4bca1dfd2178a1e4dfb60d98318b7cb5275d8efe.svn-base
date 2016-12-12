package com.zhjy.hdcivilization.dao;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.entity.HDC_UserCommentList;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.exception.ContentException;

import java.util.List;

/**
 * @author :huangxianfeng on 2016/8/17.
 * 用户评论的列表
 */
public class UserCommentListDao {

    private static UserCommentListDao instance;

    private UserCommentListDao(){}

    public static UserCommentListDao getInstance(){
        if (instance==null){
            synchronized (UserCommentListDao.class){
                if (instance==null){
                    instance=new UserCommentListDao();
                }
            }
        }
        return instance;
    }


    /**
     * 保存所有数据
     * @param datas
     */
    public void saveAll(List<HDC_UserCommentList> datas){
        try {
            MyApplication.dbUtils.saveAll(datas);
        } catch (DbException e) {
            e.printStackTrace();
            System.out.println("e.getMessage() = "+e.getMessage());
            throw new RuntimeException("数据保存失败！");
        }
    }

    /**
     * 对单条数据的保存
     * @param hdc_userCommentList
     */
    public void saveObj(HDC_UserCommentList hdc_userCommentList){
        try {
            MyApplication.dbUtils.saveOrUpdate(hdc_userCommentList);
        } catch (DbException e) {
            e.printStackTrace();
            System.out.println("e.getMessage() = " + e.getMessage());
            throw new RuntimeException("数据保存失败！");
        }
    }


    /**
     * 保存更新数据
     * @param
     */
    public void saveUpDate(HDC_UserCommentList userCommentList){
        try {
            MyApplication.dbUtils.saveOrUpdate(userCommentList);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存数据失败!");
        }
    }


    /**
     * 查询数据
     * @return
     * @throws ContentException
     */
    public List<HDC_UserCommentList> getList(){
        try {
            List<HDC_UserCommentList> datas = MyApplication.dbUtils.findAll(HDC_UserCommentList.class);
            if (datas==null || datas.size()==0){
                return null;
            }else{
                return datas;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 查询数据
     * @return
     * @throws ContentException
     */
    public List<HDC_UserCommentList> getList(String fatherItemId,String fatherItemType){
        try {
            List<HDC_UserCommentList> datas = MyApplication.dbUtils.findAll(//
                                                 Selector.from(HDC_UserCommentList.class).where("fatherItemId","=",fatherItemId).//
                                                                                                and("topicItemIdAndType","=",fatherItemType).orderBy("publishTime",true));
            if (datas==null || datas.size()==0){
                return null;
            }else{
                return datas;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    /**
     * 查询数据
     * @return
     * @throws ContentException
     */
    public HDC_UserCommentList getDatas(String itemId){
        try {
            List<HDC_UserCommentList> datas = MyApplication.dbUtils.findAll(Selector.from(HDC_UserCommentList.class).where("itemId", "=", itemId));
            if (datas==null || datas.size()==0){
                return null;
            }else{
                return datas.get(0);
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    /**
     * 查询二级列表里面的数据集合
     * @return
     * @throws ContentException
     */
    public List<HDC_UserCommentList> getList(String itemId){
        try {
            List<HDC_UserCommentList> datas = MyApplication.dbUtils.findAll(Selector.from(HDC_UserCommentList.class).where("itemId", "=", itemId));
            if (datas==null || datas.size()==0){
                return null;
            }else{
                return datas;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    /**
     * 利用fatherId查询二级列表里面的数据集合
     * @return
     * @throws ContentException
     */
    public List<HDC_UserCommentList> getLists(String fatherItemId){
        try {
            List<HDC_UserCommentList> datas = MyApplication.dbUtils.findAll(Selector.from(HDC_UserCommentList.class).where("fatherItemId", "=", fatherItemId));
            if (datas==null || datas.size()==0){
                return null;
            }else{
                return datas;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }




    /**
     * 清除数据失败
     */
    public void clearData(){
        try {
            MyApplication.dbUtils.deleteAll(HDC_UserCommentList.class);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("清除数据失败！");
        }
    }


    /**
     * 清除文明评论的一级数据库的数据
     */
    public void clearItemAndType(String fatherItemId,String itemIdAndType){
        try {
            MyApplication.dbUtils.delete(HDC_UserCommentList.class, WhereBuilder.b("fatherItemId", "=", fatherItemId).and("topicItemIdAndType", "=", itemIdAndType));
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("清除数据失败！");
        }
    }


    /**
     * 清除文明评论的二级数据库的数据
     */
    public void clearItemId(String fatherItemId){
        try {
            MyApplication.dbUtils.delete(HDC_UserCommentList.class, WhereBuilder.b("fatherItemId", "=", fatherItemId));
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("清除数据失败！");
        }
    }



    /**
     * 查询数据信息
     */
    public HDC_UserCommentList findData(String itemId){
        try {
            List<HDC_UserCommentList> datas = MyApplication.dbUtils.findAll(Selector.from(HDC_UserCommentList.class).where("itemId","=",itemId));
            if (datas!=null && datas.size()>0){
                return datas.get(0);
            }else{
                return null;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("查询数据失败！");
        }
    }
}

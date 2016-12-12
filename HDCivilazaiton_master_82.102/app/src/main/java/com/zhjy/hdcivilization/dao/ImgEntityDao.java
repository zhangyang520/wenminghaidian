package com.zhjy.hdcivilization.dao;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.entity.ImgEntity;

import java.util.List;

/**
 * Created by zhangyang on 2016/8/13.
 */
public class ImgEntityDao {
    private static ImgEntityDao instance;
    private ImgEntityDao() {}

    public static ImgEntityDao getInstance(){
        if (instance == null){
            synchronized (ImgEntityDao.class){
                if (instance == null){
                    instance = new ImgEntityDao();
                }
            }
        }
        return instance;
    }

    /**
     *
     * @return
     */
    public List<ImgEntity> findAll(){
        try {
            return MyApplication.dbUtils.findAll(ImgEntity.class);
        } catch (DbException e) {
            e.printStackTrace();
            throw  new RuntimeException("查找图片数据失败!");
        }
    }

    /**
     * 进行保存图片集合
     */
    public void saveAll(List<ImgEntity> datas){
        try {
            MyApplication.dbUtils.saveOrUpdateAll(datas);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存图片数据失败");
        }
    }
    /**
     * 进行保存图片集合
     */
    public void saveImgObj(ImgEntity imgObj){
        try {
            MyApplication.dbUtils.saveOrUpdate(imgObj);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存图片数据失败");
        }
    }
    /**
     * 通过条目的Id进行获取图片的实体
     * @param itemId
     * @return
     */
    public ImgEntity getImgEntity(String itemId){
        try {
            List<ImgEntity> datas=MyApplication.dbUtils.findAll(//
                                                Selector.from(ImgEntity.class).where(//
                                                                        WhereBuilder.b("itemId","=",itemId)));
            if(datas!=null && datas.size()>0){
                return datas.get(0);
            }else{
                return null;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw  new RuntimeException("获取图片实体失败!");
        }
    }

    public List<ImgEntity> getImgEntityList(String itemIdAndType) {
        try {
            List<ImgEntity> datas=MyApplication.dbUtils.findAll(//
                    Selector.from(ImgEntity.class).where(//
                            WhereBuilder.b("itemIdAndItemType","=",itemIdAndType)));
            return datas;
        } catch (DbException e) {
            e.printStackTrace();
            throw  new RuntimeException("获取图片实体失败!");
        }
    }
}

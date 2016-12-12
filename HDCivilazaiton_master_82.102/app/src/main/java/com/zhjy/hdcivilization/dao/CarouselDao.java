package com.zhjy.hdcivilization.dao;

import com.lidroid.xutils.exception.DbException;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.entity.HDC_Carousel;

import java.util.List;

/**
 * @author :huangxianfeng on 2016/8/14.
 * 轮播图实体Dao
 */
public class CarouselDao {

    private static CarouselDao instance;
    private CarouselDao() {}

    public static CarouselDao getInstance(){
        if (instance == null){
            synchronized (CivistateDao.class){
                if (instance == null){
                    instance = new CarouselDao();
                }
            }
        }
        return instance;
    }

    /**
     * 进行保存图片集合
     */
    public void saveAll(List<HDC_Carousel> datas){
        try {
            MyApplication.dbUtils.saveAll(datas);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存图片数据失败");
        }
    }

    /**
     * @return
     */
    public List<HDC_Carousel> findAll(){
        try {
            return MyApplication.dbUtils.findAll(HDC_Carousel.class);
        } catch (DbException e) {
            e.printStackTrace();
            throw  new RuntimeException("查找图片数据失败!");
        }
    }


}

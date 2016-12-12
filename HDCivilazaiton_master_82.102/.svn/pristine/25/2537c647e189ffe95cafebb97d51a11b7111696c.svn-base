package com.zhjy.hdcivilization.dao;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.entity.ClickLikesState;

import java.util.List;

/**
 * 点赞状态的记录
 * Created by zhangyang on 2016/8/29.
 */
public class ClickLikesStateDao {
    private static ClickLikesStateDao instance;
    private ClickLikesStateDao() {}

    public static ClickLikesStateDao getInstance(){
        if (instance == null){
            synchronized (CivistateDao.class){
                if (instance == null){
                    instance = new ClickLikesStateDao();
                }
            }
        }
        return instance;
    }

    /**
     * 通过userid和itemIdAndType获取点赞状态
     * @param userId
     * @param itemIdAndType
     * @return
     */
    public ClickLikesState getClickLikesState(String userId,String itemIdAndType){
        try {
            List<ClickLikesState> datas=MyApplication.dbUtils.findAll(Selector.from(ClickLikesState.class).where(WhereBuilder.b("userId","=",userId).and("itemIdAndType","=",itemIdAndType)));
            if(datas!=null && datas.size()>0){
                return datas.get(0);
            }else{
                return null;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("点赞状态查询失败!");
        }
    }

    /**
     * 通过userid和itemIdAndType获取点赞状态
     * @param
     * @param itemIdAndType
     * @return
     */
    public ClickLikesState getClickLikesState(String itemIdAndType){
        try {
            List<ClickLikesState> datas=MyApplication.dbUtils.findAll(Selector.from(ClickLikesState.class).where(WhereBuilder.b("itemIdAndType","=",itemIdAndType)));
            if(datas!=null && datas.size()>0){
                return datas.get(0);
            }else{
                return null;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("点赞状态查询失败!");
        }
    }
    /**
     * 进行保存点赞状态
     * @param
     * @param
     */
    public void saveClickLikesState(ClickLikesState clickLikesState){
        try {
            MyApplication.dbUtils.saveOrUpdate(clickLikesState);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}

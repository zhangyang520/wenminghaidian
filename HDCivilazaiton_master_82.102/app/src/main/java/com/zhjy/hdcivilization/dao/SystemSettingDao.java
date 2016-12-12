package com.zhjy.hdcivilization.dao;


import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.entity.SystemSetting;
import com.zhjy.hdcivilization.exception.ContentException;

import java.util.List;

/**
 * @author :huangxianfeng on 2016/8/14.
 * 系统设置相关Dao
 */
public class SystemSettingDao {
    private static SystemSettingDao instance;

    private SystemSettingDao(){}

    public static SystemSettingDao getInstance(){
        if (instance==null){
            synchronized (SystemSettingDao.class){
                if (instance==null){
                    instance=new SystemSettingDao();
                }
            }
        }
        return instance;
    }


    /**
     * 保存所有数据
     * @param systemSetting
     */
    public void saveObj(SystemSetting systemSetting){
        try {
            MyApplication.dbUtils.saveOrUpdate(systemSetting);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存数据失败!");
        }
    }

    /**
     * 进行更新用户对应设置信息
     * @param
     */
    public void updateSize(SystemSetting systemSetting,String userId,String fontSize ){
        try {
            systemSetting.setUserId(userId);
            systemSetting.setFontSize(fontSize);
            MyApplication.dbUtils.update(systemSetting, "userId", "fontSize");
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 根据Id进行获取对象
     * @return
     */
    public SystemSetting getSystemSetting(String userId)throws ContentException {
        try {
            List<SystemSetting> datas =  MyApplication.dbUtils.findAll(com.lidroid.xutils.db.sqlite.Selector.from(SystemSetting.class).where("userId", "=", userId));
            if (datas!=null && datas.size()>0){
                return datas.get(0);
            }else{
                throw new ContentException("数据为空！");
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    public void clearData(String userId){
        try {
            MyApplication.dbUtils.delete(Selector.from(SystemSetting.class).where("userId","=",userId));
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}

package com.zhjy.hdcivilization.dao;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.entity.RecordUrl;

import java.net.ConnectException;
import java.util.List;

/**
 * Created by Administrator on 2016/8/29.
 */
public class RecordUrlDao {
    private  static RecordUrlDao instance;

    private  RecordUrlDao(){}

    public static RecordUrlDao getInstance(){
        if(instance==null) {
            synchronized (RecordUrlDao.class) {
                if(instance==null){
                    instance=new RecordUrlDao();
                }
            }
        }
        return  instance;
    }

    /**
     * 进行通过systemCurrent进行删除
     * @param systemCurrent
     */
    public void deleteBy(String systemCurrent){
        try {
            MyApplication.dbUtils.delete(//
                    RecordUrl.class, WhereBuilder.b(//
                                        "currentTime","=",systemCurrent));
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("删除数据失败!");
        }
    }

    /**
     * 是否包含对应的url记录
     * @param url
     */
    public RecordUrl contains(String url) throws ConnectException {
        try {
            List<RecordUrl> datas=//
                    MyApplication.dbUtils.findAll(//
                            Selector.from(RecordUrl.class).where(//
                                                WhereBuilder.b("recordUrl","=",url)));
            if(datas!=null && datas.size()>0){
                return datas.get(0);
            }
            throw new ConnectException("");
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("查找数据失败!");
        }
    }

    /**
     * 进行插入到数据库中
     * @param data
     */
    public void addRecordUrl(RecordUrl data){
        try {
              MyApplication.dbUtils.saveOrUpdate(data);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("查找数据失败!");
        }
    }
}

package com.zhjy.hdcivilization.dao;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.entity.HDC_CiviState;
import com.zhjy.hdcivilization.entity.HDC_CommentDetail;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;

import java.util.List;

/**
 * Created by zhangyang on 2016/8/13.
 * 评论相关的dao
 */
public class CommentDao {
    private CommentDao(){};
    private static CommentDao instance;

    public static  CommentDao getInstance(){
        if(instance==null){
            synchronized (CommentDao.class) {
                if (instance==null) {
                    instance=new CommentDao();
                }
            }
        }
        return instance;
    }

    /**
     * 保存所有的数据
     * @param datas
     */
    public void saveAll(List<HDC_CommentDetail> datas){
        try {
            MyApplication.dbUtils.saveOrUpdateAll(datas);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存数据失败!");
        }
    }

    /**
     * 进行更新对应的信息
     * @param entity
     */
    public void update(HDC_CommentDetail entity){
        try {
            MyApplication.dbUtils.update(entity, WhereBuilder.b("itemId", "=", entity.getItemId()),"typeCount","count","commentCount");
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("更新数据失败!");
        }
    }
    /**
     * 保存所有的数据
     * @param
     */
    public void saveObj(HDC_CommentDetail commentDetail){
        try {
            MyApplication.dbUtils.saveOrUpdate(commentDetail);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存数据失败!");
        }
    }

    public void saveObj_1(HDC_CommentDetail commentDetail){
        try {
            MyApplication.dbUtils.save(commentDetail);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存数据失败!");
        }
    }
    /**
     * 通过Itemid进行获取集合数据
     * @param itemId
     * @return
     */
    public List<HDC_CommentDetail> getHDC_CommentDetailList(String itemId){
        try {
            return MyApplication.dbUtils.findAll(Selector.from(HDC_CommentDetail.class).where(WhereBuilder.b("itemId","=",itemId)));
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("获取数据失败!");
        }
    }
    /**
     * 通过Itemid进行获取集合数据
     * @param itemId
     * @return
     */
    public HDC_CommentDetail getHDC_CommentDetailLists(String itemId){
        try {
            List<HDC_CommentDetail> datas = MyApplication.dbUtils.findAll(Selector.from(HDC_CommentDetail.class).where(WhereBuilder.b("itemId","=",itemId)));
            if (datas!= null && datas.size()>0){
                return datas.get(0);
            }else{
                return null;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("获取数据失败!");
        }
    }
    /**
     * 进行返回所有的集合
     * @return
     */
    public List<HDC_CommentDetail> getAll(){
        try{
            return MyApplication.dbUtils.findAll(HDC_CommentDetail.class);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("查找数据失败!");
        }
    }

    /**
     * 对数据的删除
     */
    public void clearAll(){
        try {
            MyApplication.dbUtils.deleteAll(HDC_CommentDetail.class);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("清空数据失败！");
        }
    }


    /**
     * 进行清除热门话题类型的条目
     */
    public void clearHotTopic(){
        try {
            MyApplication.dbUtils.delete(HDC_CommentDetail.class, WhereBuilder.b("topicType", "=", HDCivilizationConstants.HOT_TOPIC_TYPE));
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("清除热门话题数据失败!");
        }
    }

    /**
     * 进行获取热门话题类型的集合数据
     * @return
     */
    public List<HDC_CommentDetail> getHotTopics(){
        try {
            //id desc,id2 asc
            return MyApplication.dbUtils.findAll(//
                                Selector.from(HDC_CommentDetail.class).where(//
                                                    WhereBuilder.b("topicType","=", HDCivilizationConstants.HOT_TOPIC_TYPE)).orderBy("topValue desc,orderTime ",true));
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("查找热门话题数据失败!");
        }
    }

    /**
     * 进行获取条目的id的热门话题条目
     * @param itemIdAndType
     * @return
     */
    public HDC_CommentDetail getHotTopicsBy(String itemIdAndType){
        try {
            System.out.println("getHotTopics:"+getHotTopics().toString());
            List<HDC_CommentDetail> datas=MyApplication.dbUtils.findAll(//
                    Selector.from(HDC_CommentDetail.class).where(//
                            WhereBuilder.b("topicType","=", HDCivilizationConstants.HOT_TOPIC_TYPE).and("itemId","=",itemIdAndType)));
            if(datas!=null && datas.size()>0){
                return datas.get(0);
            }else{
                return null;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("查找热门话题数据失败!");
        }
    }
    /**
     * 清除对应人发表的话题
     * @param userId
     */
    public void clearSubTopic(String userId){
        try {
            MyApplication.dbUtils.delete(HDC_CommentDetail.class, //
                    WhereBuilder.b("topicType", "=", //
                            HDCivilizationConstants.SUB_TOPIC_TYPE).//
                            and("launcherUserId", "=", userId));
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("清除发表话题数据失败!");
        }
    }

    /**
     * 进行获取发表话题的列表数据
     * @param userId
     * @return
     */
    public List<HDC_CommentDetail> getSubTopic(String userId){
        try {
            return MyApplication.dbUtils.findAll(//
                            Selector.from(HDC_CommentDetail.class).where("topicType", "=", //
                                                                HDCivilizationConstants.SUB_TOPIC_TYPE).//
                                                                                  and("launcherUserId", "=", userId));
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("查找发表话题数据失败!");
        }
    }

    public List<HDC_CommentDetail> getSubTopicOrderTime(String userId){
        try {
            return MyApplication.dbUtils.findAll(//
                    Selector.from(HDC_CommentDetail.class).where("topicType", "=", //
                            HDCivilizationConstants.SUB_TOPIC_TYPE).//
                            and("launcherUserId", "=", userId).orderBy("orderTime",true));
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("查找发表话题数据失败!");
        }
    }
    public List<HDC_CommentDetail> getSubTopic(){
        try {
            return MyApplication.dbUtils.findAll(//
                    Selector.from(HDC_CommentDetail.class).where(//
                            WhereBuilder.b("topicType", "=", HDCivilizationConstants.SUB_TOPIC_TYPE)));
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("查找热门话题数据失败!");
        }
    }
    /**
     * 进行获取条目的id的热门话题条目
     * @param
     * @return
     */
    public HDC_CommentDetail getSubTopicsBy(String itemId){
        try {
            System.out.println("getSubTopic:"+getSubTopic().toString());
            List<HDC_CommentDetail> datas=MyApplication.dbUtils.findAll(//
                    Selector.from(HDC_CommentDetail.class).where(//
                            WhereBuilder.b("topicType","=", HDCivilizationConstants.SUB_TOPIC_TYPE).and("itemId","=",itemId)));

            if(datas!=null && datas.size()>0){
                return datas.get(0);
            }else{
                return null;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("查找发表话题数据失败!");
        }
    }

    /**
     * 进行删除对应用户参与话题类型的数据
     * @param userId
     */
    public void clearJoinTopic(String userId){
        try {
            MyApplication.dbUtils.delete(HDC_CommentDetail.class, //
                    WhereBuilder.b("topicType", "=", //
                            HDCivilizationConstants.JOIN_TOPIC_TYPE).//
                            and("participateUserId", "=", userId));
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("清除参与话题数据失败!");
        }
    }

    /**
     * 进行获取对应用户参与话题的集合数据
     * @param userId
     */
    public List<HDC_CommentDetail> getJoinTopics(String userId){
        try {
            return MyApplication.dbUtils.findAll(//
                                     Selector.from(HDC_CommentDetail.class).where("topicType", "=", //
                                                                     HDCivilizationConstants.JOIN_TOPIC_TYPE).//
                                                                                                and("participateUserId", "=", userId));
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("查找参与话题数据失败!");
        }
    }

    /**
     * 进行获取条目的id的热门话题条目
     * @param itemIdAndType
     * @return
     */
    public HDC_CommentDetail getJoinTopicsBy(String itemIdAndType){
        try {
            System.out.println("getHotTopics:"+getHotTopics().toString());
            List<HDC_CommentDetail> datas=MyApplication.dbUtils.findAll(//
                    Selector.from(HDC_CommentDetail.class).where(//
                            WhereBuilder.b("topicType","=", HDCivilizationConstants.JOIN_TOPIC_TYPE).and("itemId","=",itemIdAndType)));

            if(datas!=null && datas.size()>0){
                return datas.get(0);
            }else{
                return null;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("查找发表话题数据失败!");
        }
    }

}

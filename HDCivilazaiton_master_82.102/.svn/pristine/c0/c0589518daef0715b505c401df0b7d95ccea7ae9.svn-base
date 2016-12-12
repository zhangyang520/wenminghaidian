package com.zhjy.hdcivilization.dao;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.zhjy.hdcivilization.application.MyApplication;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.exception.ContentException;

import java.net.ConnectException;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/8/14.
 * User相关dao
 */
public class UserDao {

    private static UserDao instance;

    private UserDao(){}

    public static UserDao getInstance(){
        if (instance==null){
            synchronized (UserDao.class){
                if (instance==null){
                    instance=new UserDao();
                }
            }
        }
        return instance;
    }

    /**
     * 保存所有数据
     * @param user
     */
    public void saveAll(User user){
        try {
            MyApplication.dbUtils.save(user);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存数据失败!");
        }
    }


    /**
     * 保存更新数据
     * @param user
     */
    public void saveUpDate(User user){
        try {
            MyApplication.dbUtils.saveOrUpdate(user);
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("保存数据失败!");
        }
    }



    /**
     * 查找数据
     * @param
     * @return
     */
    public List<User> getAll()throws ContentException {
        try {
            List<User> datas =  MyApplication.dbUtils.findAll(User.class);
            if(datas==null || datas.size()==0){
                throw new ContentException("没有用户列表!");
            }
            return datas;
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 得到User对象的UserId
     * @param userId
     * @return
     */
    public User getUserId(String userId) {
        try {
            List<User> datas =  MyApplication.dbUtils.findAll(Selector.from(User.class).where("userId","=",userId));
            System.out.println("UserDao.size="+datas.size());
            if (datas.size()>0){
                return datas.get(0);
            }else{
               return null;
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 进行获取本地用户
     * @return
     */
    public User getLocalUser()throws ContentException {
        try {
            List<User> datas =  MyApplication.dbUtils.findAll(Selector.from(User.class).where("isLocalUser", "=", true));
            if (datas!=null && datas.size()>0){
                return datas.get(0);
            }else{
               throw new ContentException("您尚未登录!");
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 保存当前本地用户的权限值
     * @param userPermission
     */
    public void saveLocalUserPermission(String userPermission){
        try {
            User user=getLocalUser();
            user.setIdentityState(userPermission);
            MyApplication.dbUtils.saveOrUpdate(user);
        } catch (ContentException e) {
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 进行更新所有用户的本地身份状态
     * @param flag
     */
    public void updateAllUserLocalState(boolean flag){
        try {
            List<User> userList=getAll();
            for (User user:userList){
                user.setIsLocalUser(flag);
            }
            MyApplication.dbUtils.updateAll(userList, "isLocalUser");
        } catch (ContentException e) {
            //没有用户
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 进行更新除了该用户所有用户的本地身份状态
     * @param flag
     */
    public void updateExceptUserLocalState(String userId,boolean flag){
        try {
            List<User> userList=getAll();
            for(User user:userList){
                if(!user.getUserId().equals(userId)){
                    //设置其他用户为false
                    user.setIsLocalUser(false);
                }else{
                    //对应的userId用户为flag
                    user.setIsLocalUser(flag);
                }
            }
            MyApplication.dbUtils.updateAll(userList);
        } catch (ContentException e) {
            //没有用户
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 进行更新用户对应的 userId,volunteerId,身份权限值
     * @param targetUser
     * @param
     */
    public void updateUserIds(User targetUser,String userId,String volunteerId,String userPermission){
        try {
            targetUser.setUserId(userId);
            targetUser.setVolunteerId(volunteerId);
            targetUser.setIdentityState(userPermission);
            MyApplication.dbUtils.update(targetUser, "userId", "volunteerId", "identityState");
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }



    /**
     * 进行更新用户对应的 userId,金币值
     * @param targetUser
     * @param
     */
    public void updateGold(User targetUser,String userId,String gold ){
        try {
            targetUser.setUserId(userId);
            targetUser.setGoldCoin(gold);
            MyApplication.dbUtils.update(targetUser,"userId","goldCoin");
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 进行更新用户对应的 userId,金币兑换状态
     * @param targetUser
     * @param
     */
    public void updateExchangeState(User targetUser,String userId,String exchangeState ){
        try {
            targetUser.setUserId(userId);
            targetUser.setExchangeState(exchangeState);
            MyApplication.dbUtils.update(targetUser,"userId","exchangeState");
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    /**
     * 进行获取最新登录的用户实体类
     * @return
     */
    public User getLastLoginUser() throws ConnectException {
        try {
            List<User> userList= MyApplication.dbUtils.findAll(//
                                                Selector.from(User.class).where("lastLoginTime",">",0).//
                                                                                        orderBy("lastLoginTime",true));
            if (userList!=null && userList.size()>0) {
                return userList.get(0);
            }else{
                throw new ConnectException("暂无最新登录用户");
            }
        } catch (DbException e) {
            e.printStackTrace();
            throw new RuntimeException("查找数据失败");
        }
    }
}

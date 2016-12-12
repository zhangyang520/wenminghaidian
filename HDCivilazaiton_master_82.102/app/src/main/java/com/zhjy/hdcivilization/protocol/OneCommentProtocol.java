package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_UserCommentList;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/9/13.
 */
public class OneCommentProtocol extends BaseProtocol<List<HDC_UserCommentList>> {

    private String fatherItemId;

    public String getFatherItemId() {
        return fatherItemId;
    }

    public void setFatherItemId(String fatherItemId) {
        this.fatherItemId = fatherItemId;
    }

    private String itemIdAndType;

    public String getItemIdAndType() {
        return itemIdAndType;
    }

    public void setItemIdAndType(String itemIdAndType) {
        this.itemIdAndType = itemIdAndType;
    }

    HDC_UserCommentList userCommentList;

    @Override
    protected List<HDC_UserCommentList> parseJson(String jsonStr) throws JsonParseException, ContentException {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            System.out.println("OneCommentProtocol = "+ jsonObject.toString());
            JSONObject valuesObject = jsonObject.getJSONObject("values");
            String status=valuesObject.getString("status");
            if (status.equals(HDCivilizationConstants.STATUS_0)) {
                throw new ContentException(getActionKeyName()+"!");
            } else if (status.equals(HDCivilizationConstants.STATUS_2)) {
                String userPermission = valuesObject.getString("userPermission");
                if (userPermission.equals(UserPermisson.UNKNOW_VALUE.getType())) {
                    //进行前后台userid不一致
                    throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,getActionKeyName()+"!");
                }else{
                    //一般不会被执行
                    try {
                        //如果此时进行更新权限值 获取本地用户
                        User user = UserDao.getInstance().getLocalUser();
                        user.setIdentityState(userPermission);
                        UserDao.getInstance().saveUpDate(user);
                    } catch (ContentException e) {
                        e.printStackTrace();
                    }
                    throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,"权限过低！");
                }
            } else {
                String userPermission = valuesObject.getString("userPermission");
                try {
                    //如果此时进行更新权限值 获取本地用户
                    User user = UserDao.getInstance().getLocalUser();
                    user.setIdentityState(userPermission);
                    UserDao.getInstance().saveUpDate(user);
                } catch (ContentException e) {
                    e.printStackTrace();

                }
                //首先进行根据链接的个数进行判断iten种类
                JSONObject infoObject = jsonObject.getJSONObject("info");
                String listString = infoObject.getString("firstList");
                JSONArray listArray=new JSONArray(listString);
                if (listArray.length()==0){
                   return  new ArrayList<HDC_UserCommentList>();
                }else{
                    List<HDC_UserCommentList> datas = new ArrayList<HDC_UserCommentList>();
                    for (int i = 0; i < listArray.length(); i++) {

                        userCommentList = new HDC_UserCommentList();
                        JSONObject firstObj = new JSONObject(listArray.get(i).toString());
                        String userId = firstObj.getString("userId");
                        String userState = firstObj.getString("userState");
                        String itemId = firstObj.getString("itemId");
                        String imgUrl = firstObj.getString("imgUrl");
                        long time = firstObj.getLong("time");
                        String content = firstObj.getString("content");
                        String nickName = firstObj.getString("nickName");

                        long secondListCount = firstObj.getLong("secondListCount");
                        User user = UserDao.getInstance().getUserId(userId);
                        if (user == null){
                            user = new User();
                            user.setUserId(userId);
                            user.setPortraitUrl(imgUrl);
                            user.setIdentityState(userState);
                            user.setNickName(nickName);
                        }else{
                            user.setUserId(userId);
                            user.setPortraitUrl(imgUrl);
                            user.setNickName(nickName);
                            user.setIdentityState(userState);
                        }
                        UserDao.getInstance().saveUpDate(user);
//                        System.out.println("firstComment index :" + i + "..contnent:" + content + "...11111======" + UserDao.getInstance().getLocalUser().toString() + "1");
                        userCommentList.setUser(user);
                        userCommentList.setItemId(itemId);
                        userCommentList.setPublishTime(time);
                        userCommentList.setCount((int) secondListCount);
                        userCommentList.setContent(content);
                        userCommentList.setFatherItemId(fatherItemId);
                        userCommentList.setTopicItemIdAndType(itemIdAndType);

                        datas.add(userCommentList);

                        JSONArray secondList = firstObj.getJSONArray("secondList");
                        if (secondList.length()==0){
                            //进行下一个循环。。。。。
                        }else{
                            for (int j = 0; j < secondList.length(); j++) {
                                JSONObject json = new JSONObject(secondList.get(j).toString());
                                HDC_UserCommentList comment = new HDC_UserCommentList();
                                String userIdS = json.getString("userId");
                                String userState1 = json.getString("userState");
                                String userName = json.getString("nickName");
//                                System.out.println("initViews nickName1 ="+nickName);
                                String itemIds = json.getString("itemId");
                                String imgUrls = json.getString("imgUrl");
                                String contents = json.getString("content");
                                User user1 = UserDao.getInstance().getUserId(userIdS);
                                if (user1==null){
                                    user1 = new User();
                                    user1.setUserId(userIdS);
                                    user1.setPortraitUrl(imgUrls);
                                    user1.setNickName(userName);
                                    user1.setIdentityState(userState1);
                                }else{
                                    user1.setUserId(userIdS);
                                    user1.setPortraitUrl(imgUrls);
                                    user1.setNickName(userName);
                                    user1.setIdentityState(userState1);
                                }
                                UserDao.getInstance().saveUpDate(user1);
                                comment.setUser(user1);
//                                System.out.println("second Comment index :" + j+ "..contnent:" + contents + "...11111======" + UserDao.getInstance().getLocalUser().toString() + "1");
                                comment.setContent(contents);
                                comment.setItemId(itemIds);
                                comment.setFatherItemId(itemId);
                                comment.setTopicItemIdAndType("");
                                datas.add(comment);
                            }
                        }
                    }
//                    System.out.println("initViews ======"+UserDao.getInstance().getLocalUser().toString()+"00000000000000");
                    return datas;
                }
            }
        } catch (JSONException e) {
            throw new JsonParseException(getActionKeyName()+"!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
